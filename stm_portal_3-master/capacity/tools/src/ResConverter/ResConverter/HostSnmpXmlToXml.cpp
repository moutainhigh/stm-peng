#include "StdAfx.h"
#include "HostSnmpXmlToXml.h"

HostSnmpXmlToXml::HostSnmpXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag)
{

	//MYLOG("entry");

	//检查输入的参数是否可用
	if(!CheckParam(xmlpath, filename, kpifilename, outfilepath))
	{
		MYLOG("error : 输入的参数出错");
		return;
	}
	
	AnalysisMain(flag);
}

HostSnmpXmlToXml::~HostSnmpXmlToXml(void)
{
	//MYLOG("entry");
}


void HostSnmpXmlToXml::AnalysisMain(int flag)
{
	//获取资源信息
	if(!GetResourceInfo())
	{
		loglog.str("");
		loglog << "获取文件" << strXmlPath_	 << filename_ << "信息失败." << flush;
		MYLOG(loglog.str().c_str());
		//AfxMessageBox(loglog.str().c_str());
		return;
	}

	//初始化一些指标信息
	InitDatas();

	GetKpiDatas(mapkpidatas_);

	Chipolicy(mapchipolicydatas_);

	//解析自己的文件
	ParseHostSnmp();

	//将数据排序，为写文件准备
	PreperDatasForWrite();

	switch(flag)
	{
	case XML_TO_XML_EXCEL:
		WriteCollectXml();		//生成collect xml文件		
		WriteResourceXml();  //生成Resource xml文件
		WriteExcel();  //将数据写入到excel中
		break;
	case XML_TO_XML:		
		WriteCollectXml();		//生成collect xml文件		
		WriteResourceXml();  //生成Resource xml文件
		break;
	case XML_TO_EXCEL:		
		WriteExcel();  //将数据写入到excel中
		break;
	default :
		loglog.str("");
		loglog << "处理的方式不正确. flag = [" << flag << "]." << flush;
		MYLOG(loglog.str().c_str());
		return;
	}

}

void HostSnmpXmlToXml::InitDatas()
{
	VEC_STRING_DATAS tmpdata;

	ismodifyhostname_ = TRUE;

	setChildIndexDatas_.clear();

	//CGlobalMetricSettingDatas_初始化
	//目前为空

	//PluginClassAliasListDatas_ 初始化
	PluginClassAliasListDatas_ = g_GlobalData.PCALDatas_;

	//RGlobalMetricSettingDatas_ 初始化
	RGlobalMetricSettingDatas_ = g_GlobalData.RGMSDatas_;	

	//MetricPluginDatas_ 初始化
	//增加 macAddress 指标
	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");
	tmpdata.push_back("");
	tmpdata.push_back("");
	tmpdata.push_back("method");
	tmpdata.push_back("walk");
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//插件type
	tmpdata.push_back("");			//插件classKey
	tmpdata.push_back("");			//type值
	tmpdata.push_back("");			//key值
	tmpdata.push_back("1.3.6.1.2.1.2.2.1.8");		//value值
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//插件type
	tmpdata.push_back("");			//插件classKey
	tmpdata.push_back("");			//type值
	tmpdata.push_back("");			//key值
	tmpdata.push_back("1.3.6.1.2.1.2.2.1.7");		//value值
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginDataHandler");
	tmpdata.push_back("");
	tmpdata.push_back("selectProcessor");
	tmpdata.push_back("");
	tmpdata.push_back("SELECT");
	tmpdata.push_back("SELECT hostInterfaceIndex,((ifOperStatus=='1' && ifAdminStatus=='1')?'1':'0') as ifStatus");
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginDataConverter");
	tmpdata.push_back("");	//插件type
	tmpdata.push_back("subInstConverter");			//插件classKey
	tmpdata.push_back("");			//type值
	tmpdata.push_back("IndexColumnTitle");			//key值
	tmpdata.push_back("hostInterfaceIndex");		//value值
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginDataConverter");
	tmpdata.push_back("");	//插件type
	tmpdata.push_back("subInstConverter");			//插件classKey
	tmpdata.push_back("");			//type值
	tmpdata.push_back("ValueColumnTitle");			//key值
	tmpdata.push_back("ifStatus");		//value值
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginDataConverter");
	tmpdata.push_back("");	//插件type
	tmpdata.push_back("subInstConverter");			//插件classKey
	tmpdata.push_back("ResourceProperty");			//type值
	tmpdata.push_back("InstPropertyKey");			//key值
	tmpdata.push_back("hostInterfaceIndex");		//value值
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);


	string subresourceid = resourceid_ + FirstLetterToupper("hostInterface");
	tmpdata.clear();
	tmpdata.push_back(subresourceid);
	tmpdata.push_back("");
	tmpdata.push_back("");
	tmpdata.push_back("接口");
	tmpdata.push_back("接口");
	tmpdata.push_back("hostInterface");
	tmpdata.push_back(resourceid_);
	
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("AvailabilityMetric");
	tmpdata.push_back("网卡可用性");
	tmpdata.push_back("网卡可用性");
	tmpdata.push_back("");

	tmpdata.push_back("true");
	tmpdata.push_back("23000");
	tmpdata.push_back("true");
	tmpdata.push_back("false");
	tmpdata.push_back("min1");
	tmpdata.push_back("min1,min5,hour1");
	tmpdata.push_back("true");
	tmpdata.push_back("1");

	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	ResourceDatas_.push_back(tmpdata);


	//PropertiesDatas_ 初始化 主资源
	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back("osVersion");
	tmpdata.push_back("osVersion");
	tmpdata.push_back("osVersion");
	PropertiesDatas_.push_back(tmpdata);
	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back("ip");
	tmpdata.push_back("ip");
	tmpdata.push_back("ip");
	PropertiesDatas_.push_back(tmpdata);
	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back("Name");
	tmpdata.push_back("Name");
	tmpdata.push_back("Name");
	PropertiesDatas_.push_back(tmpdata);
	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back("sysUpTime");
	tmpdata.push_back("sysUpTime");
	tmpdata.push_back("sysUpTime");
	PropertiesDatas_.push_back(tmpdata);


	VEC_VEC_DATAS::iterator it = PropertiesDatas_.begin();
	for( ; it != PropertiesDatas_.end(); ++it)
	{
		PropertiesData_.insert((it->at(0)) + (it->at(1)));
	}


	//InstantiationDatas_ 初始化 主资源
	//tmpdata.clear();
	//tmpdata.push_back(resourceid_);
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("hostName");
	//InstantiationDatas_.push_back(tmpdata);
	//tmpdata.clear();
	string mainvalue = ",Name,";
	mapinstantiationId_[resourceid_] = mainvalue;
	mapinstantiationName_[resourceid_] = "Name";
}

void HostSnmpXmlToXml::ParseHostSnmp()
{
	string pathfilename = strXmlPath_ + filename_;
	ParseFile(pathfilename);
}

void HostSnmpXmlToXml::ParseFile(string pathfilename)
{
	VEC_STRING_DATAS tmpdata;

	MYLOG(pathfilename.c_str());
	if(!CheckFileExist(pathfilename))
	{
		loglog.str("");
		loglog << "文件： " << pathfilename << " 不存在！";
		AfxMessageBox(loglog.str().c_str());
		MYLOG(loglog.str().c_str());
		return ;
	}

	file<> fdoc(pathfilename.c_str());
	xml_document<>   doc;
	doc.parse<0>(fdoc.data());

	//! 获取根节点
	xml_node<>* root = doc.first_node();

	//! 获取根节点第一个节点	
	for(rapidxml::xml_node<char> * devicetypeinfo = root->first_node("devicetypeinfo");
		devicetypeinfo != NULL;
		devicetypeinfo = devicetypeinfo->next_sibling())
	{
		//tmplog.str("");
		VEC_STRING_DATAS deviceattr;
		deviceattr.clear();
		/********************************************************************
		解析devicetypeinfo节点的属性
		**********************************************************************/
		for(rapidxml::xml_attribute<char> * attr = devicetypeinfo->first_attribute();
			attr != NULL;
			attr = attr->next_attribute())
		{
			//tmplog << "[" << attr->name() << "=" << UTF8ToGB(attr->value()) << "]" <<flush;
			deviceattr.push_back(UTF8ToGB(attr->value()));
		}
		//MYLOG(tmplog.str().c_str());

		/********************************************************************
		解析devicetypeinfo节点的子节点commands
		**********************************************************************/
		//map存储： key是comid， value是一个vec，第一个元素为coltype，其他为command（有多少个就存储多少个）
		MAP_VEC_DATAS kpimapcmd;
		kpimapcmd.clear();
		for(rapidxml::xml_node<char> * commands = devicetypeinfo->first_node("commands");
			commands != NULL;
			commands = commands->next_sibling())
		{
			VEC_STRING_DATAS valuedata;
			//解析commands节点的子节点command
			for(rapidxml::xml_node<char> * command = commands->first_node("command");
				command != NULL;
				command = command->next_sibling())
			{
				//tmplog.str("");
				valuedata.clear();
				//解析command节点的属性
				for(rapidxml::xml_attribute<char> * attr2 = command->first_attribute();
					attr2 != NULL;
					attr2 = attr2->next_attribute())
				{
					//读取次序为coltype、comid、command
					valuedata.push_back(UTF8ToGB(attr2->value()));
					//tmplog << "[" << attr2->name() << "=" << UTF8ToGB(attr2->value()) << "]" <<flush;
				}
				//MYLOG(tmplog.str().c_str());
				//解析command节点的子节点columns
				for(rapidxml::xml_node<char> * columns = command->first_node("columns");
					columns != NULL;
					columns = columns->next_sibling())
				{
					//tmplog.str("");
					//解析command节点的子节点column
					for(rapidxml::xml_node<char> * column = columns->first_node("column");
						column != NULL;
						column = column->next_sibling())
					{
						//tmplog.str("");
						//解析column节点的属性
						for(rapidxml::xml_attribute<char> * attr3 = column->first_attribute();
							attr3 != NULL;
							attr3 = attr3->next_attribute())
						{
							//元素顺序为key、value
							//cisco_switch.xml 遇到了  key、value、select、defaultvalue
							string tempname = UTF8ToGB(attr3->name());
							if((tempname == "key") || (tempname == "value"))
							{
								valuedata.push_back(UTF8ToGB(attr3->value()));
							}
						}
						//MYLOG(tmplog.str().c_str());
					}
				}

				if (!valuedata.empty())
				{
					kpimapcmd[valuedata[1]] = valuedata;
				}
				else
				{
					MYLOG("warn : valuedata is empty.");
				}
			}


		}

		/********************************************************************
		解析devicetypeinfo节点的子节点resources
		**********************************************************************/
		//
		for(rapidxml::xml_node<char> * resources = devicetypeinfo->first_node("resources");
			resources != NULL;
			resources = resources->next_sibling())
		{

			//解析resources节点的子节点resource
			for(rapidxml::xml_node<char> * resource = resources->first_node("resource");
				resource != NULL;
				resource = resource->next_sibling())
			{
				VEC_STRING_DATAS resourceattr;
				VEC_VEC_DATAS collectattr;
				string  translationsdata("");
				VEC_STRING_DATAS kpipolicyattr;
				//解析resource节点的属性
				for(rapidxml::xml_attribute<char> * attr4 = resource->first_attribute();
					attr4 != NULL;
					attr4 = attr4->next_attribute())
				{
					//读取次序为kpiid、coltype、value、valuetype、unit
					resourceattr.push_back(UTF8ToGB(attr4->value()));
				}

				//解析resource节点的子节点collects
				for(rapidxml::xml_node<char> * collects = resource->first_node("collects");
					collects != NULL;
					collects = collects->next_sibling())
				{
					//解析collects节点的子节点collect
					for(rapidxml::xml_node<char> * collect = collects->first_node("collect");
						collect != NULL;
						collect = collect->next_sibling())
					{
						//解析collect节点的属性
						VEC_STRING_DATAS tmpcollectattr;
						for(rapidxml::xml_attribute<char> * attr5 = collect->first_attribute();
							attr5 != NULL;
							attr5 = attr5->next_attribute())
						{
							//元素顺序为exeorder、comid、select、merge、deal
							tmpcollectattr.push_back(UTF8ToGB(attr5->value()));
							//break;
						}
						collectattr.push_back(tmpcollectattr);
					}
				}

				//解析resource节点的子节点translations
				for(rapidxml::xml_node<char> * translations = resource->first_node("translations");
					translations != NULL;
					translations = translations->next_sibling())
				{
					//解析translations节点的子节点translation
					for(rapidxml::xml_node<char> * translation = translations->first_node("translation");
						translation != NULL;
						translation = translation->next_sibling())
					{
						string tempsource("");
						string tempdest("");
						//解析translation节点的属性
						for(rapidxml::xml_attribute<char> * attr6 = translation->first_attribute();
							attr6 != NULL;
							attr6 = attr6->next_attribute())
						{
							//元素顺序为source、dest
							if(UTF8ToGB(attr6->name()) == "source")
							{
								tempsource = UTF8ToGB(attr6->value());
							}
							else if(UTF8ToGB(attr6->name()) == "dest")
							{
								tempdest = UTF8ToGB(attr6->value());
							}

						}

						translationsdata = ChangeTranslations(translationsdata, tempsource, tempdest);
					}
				}

				//将最后的一个分号去掉
				translationsdata = translationsdata.substr(0, translationsdata.length() - 1);

				//解析resource节点的子节点kpipolicy
				for(rapidxml::xml_node<char> * kpipolicy = resource->first_node("kpipolicy");
					kpipolicy != NULL;
					kpipolicy = kpipolicy->next_sibling())
				{
					//解析collect节点的属性
					for(rapidxml::xml_attribute<char> * attr7 = kpipolicy->first_attribute();
						attr7 != NULL;
						attr7 = attr7->next_attribute())
					{
						//元素顺序为table、field、fieldtype、willalert、alertexp1、alertdesc1、
						//          alertexp2、alertdesc2、alertexp3、alertdesc3、alertexp4、alertdesc4、
						//          alertexp5、alertdesc5、alertcontin、circle、storefactor
						//在这里只有一个kpipolicy，所以for只循环一次，如果多个的话，后面有效只取一个
						kpipolicyattr.push_back(UTF8ToGB(attr7->value()));
					}
				}

				ParseMetricData(deviceattr, kpimapcmd, resourceattr, collectattr, kpipolicyattr, translationsdata);				

			}
		}
	}

	return;
}

BOOL HostSnmpXmlToXml::CheckResourceattr(VEC_STRING_DATAS &resourceattr)
{
	//有5个属性
	if(resourceattr.size() < 5)
	{
		MYLOG("warn : resourceattr.size() < 5");
		return FALSE;
	}

	if((resourceattr[0] == "Availability") || (resourceattr[0] == "H1PingResponseTime"))
	{
		return TRUE;
	}

	if(resourceattr[1] == "INNER_PROC")
	{
		string loglog = "warn : resourceattr[0]=[" + resourceattr[0] +  "] discard.  resourceattr[1]=INNER_PROC";
		MYLOG(loglog.c_str());
		return FALSE;
	}

	return TRUE;

}

void HostSnmpXmlToXml::ParseMetricData(VEC_STRING_DATAS &deviceattr, MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, VEC_STRING_DATAS &kpipolicyattr, string translationsdata)
{

	//检查resourceattr的正确性
	if(!CheckResourceattr(resourceattr))
	{
		return;
	}


	//MetricPlugin sheet页的数据准备
	PrepareMetricPluginAllData(kpimapcmd, resourceattr, collectattr, translationsdata);

	//Resource sheet页的数据准备
	PrepareResourceAllData(deviceattr, resourceattr, kpipolicyattr);


}

BOOL HostSnmpXmlToXml::GetColumnData(string kpiid, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr)
{
	BOOL isSELECT = FALSE;

	if(!resourcevalue.empty())
	{
		isSELECT = TRUE;
		VEC_VEC_DATAS::iterator it = collectattr.begin();
		for(int i = 1 ; it != collectattr.end(); ++it, i++ )
		{
			stringstream oldvalue("");

			oldvalue << "$d" << i << flush;
			string newvalue = it->at(1);

			//将里面的冒号，全部转为下划线
			//如果是以冒号0结尾的，直接去掉默认值
			ChangeColon(newvalue);

			resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue);
			columnvalue = columnvalue + newvalue + ",";
		}
	}
	else
	{
		//判断是类型
		VEC_STRING_DATAS kpiinfo;
		kpiinfo.clear();
		kpiinfo = mapkpidatas_[kpiid];
		string reslevel = kpiinfo[6];

		VEC_VEC_DATAS::iterator it = collectattr.begin();
		for(int i = 1 ; it != collectattr.end(); ++it, i++ )
		{
			VEC_STRING_DATAS tmpcmd;
			tmpcmd.clear();
			string tmpcmdid = it->at(1);

			VEC_STRING_DATAS veccmdid;
			veccmdid.clear();
			if(IsMultiCmdid(tmpcmdid, veccmdid))
			{
				if(reslevel == "child")
				{
					ChangeColon(tmpcmdid);
					columnvalue = columnvalue + tmpcmdid + ",";
				}
				continue;
			}

			tmpcmd = kpimapcmd[tmpcmdid];
			if (tmpcmd.size() > 3)
			{
				VEC_STRING_DATAS::iterator itcmd = tmpcmd.begin() + 4;
				//for( int i = 0 ; itcmd != veccmd.end(); ++itcmd, ++i)
				//veccmd存储了cmd和说明
				for( int i = 0 ; itcmd != tmpcmd.end(); ++itcmd, ++i)
				{
					string tempstr = *itcmd;
					ChangeColon(tempstr);
					columnvalue = columnvalue + tempstr + ",";
					++itcmd;
					if(itcmd == tmpcmd.end())
					{
						break;
					}
				}
			}
			else
			{
				if(reslevel == "child")
				{
					ChangeColon(tmpcmdid);
					columnvalue = columnvalue + tmpcmdid + ",";
				}
			}
		}

	}

	if(!columnvalue.empty())
	{
		columnvalue = columnvalue.substr(0, columnvalue.length() - 1);
		//将里面的冒号，全部转为下划线
		//columnvalue = replace_all_distinct(columnvalue, ":", "_");
	}

	return isSELECT;
}

BOOL HostSnmpXmlToXml::SpecialHandling(string kpiid)
{
	BOOL ret = FALSE;
	VEC_STRING_DATAS tmpvec;
	if(kpiid == "Availability")
	{
		ret = TRUE;
		tmpvec.clear();
		tmpvec.push_back("availability");
		tmpvec.push_back("IcmpPlugin");
		tmpvec.push_back("");
		tmpvec.push_back("PluginParameter");		
		tmpvec.push_back("ArrayType");	//插件type		
		tmpvec.push_back("");	//插件classKey		
		tmpvec.push_back("DiscoveryInfo");	//type值		
		tmpvec.push_back("IP");	//key值
		tmpvec.push_back("IP");	//value值
		tmpvec.push_back("ip,isAlive,ResponseTime");	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(tmpvec);

		tmpvec.clear();
		tmpvec.push_back("availability");
		tmpvec.push_back("IcmpPlugin");
		tmpvec.push_back("");
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//插件type		
		tmpvec.push_back("selectProcessor");	//插件classKey		
		tmpvec.push_back("");	//type值		
		tmpvec.push_back("SELECT");	//key值
		tmpvec.push_back("SELECT isAlive");	//value值

		tmpvec.push_back("ip,isAlive,ResponseTime");	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(tmpvec);
	}

	if(kpiid == "H1PingResponseTime")
	{
		ret = TRUE;
		tmpvec.clear();
		tmpvec.push_back("icmpDelayTime");
		tmpvec.push_back("IcmpPlugin");
		tmpvec.push_back("");
		tmpvec.push_back("PluginParameter");
		tmpvec.push_back("ArrayType");	//插件type		
		tmpvec.push_back("");	//插件classKey		
		tmpvec.push_back("DiscoveryInfo");	//type值		
		tmpvec.push_back("IP");	//key值
		tmpvec.push_back("IP");	//value值
		tmpvec.push_back("ip,isAlive,ResponseTime");	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(tmpvec);

		tmpvec.clear();
		tmpvec.push_back("icmpDelayTime");
		tmpvec.push_back("IcmpPlugin");
		tmpvec.push_back("");
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//插件type		
		tmpvec.push_back("selectProcessor");	//插件classKey		
		tmpvec.push_back("");	//type值		
		tmpvec.push_back("SELECT");	//key值
		tmpvec.push_back("SELECT ResponseTime");	//value值
		tmpvec.push_back("ip,isAlive,ResponseTime");	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(tmpvec);
	}

	return ret;
}

void HostSnmpXmlToXml::PrepareMetricPluginAllData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata)
{
	VEC_STRING_DATAS metricplugindata;	
	string tempnullstr("");
	BOOL isSELECT = FALSE;
	BOOL isREGULAR = FALSE;
	BOOL isChildConverter = FALSE;
	BOOL isMainConverter = FALSE;
	VEC_STRING_DATAS vecregulardata;
	VEC_STRING_DATAS vecdealdata;

	vecregulardata.clear();
	vecdealdata.clear();

	//{"metricid", "pluginid", "collectType", "采集种类", "插件type", "插件classKey", "type值", "key值", "value值", "column值"};

	string tmpkpiid = resourceattr[0];
	string resourcevalue = resourceattr[2];
	string columnvalue("");
	string tempcolumnvalue("");
	string kpitype("");
	string reslevel("");
	string childindex("");
	string ischildresid("");
	string ischildresname("");

	//判断是否已经存在，如果存在，直接返回
	if(IsRepeatMetricid(resourceattr[0], resourceattr[1], 1))
	{
		loglog.str("");
		loglog << "warn : resourceattr[0]=[" << resourceattr[0] << "],resourceattr[1]=" << resourceattr[1] << "] is already exist(MetricPluginAllData).";
		MYLOG(loglog.str().c_str());
		return;
	}

	if(SpecialHandling(tmpkpiid))
	{
		return ;
	}

	{
		//判断指标id是否正确
		VEC_STRING_DATAS kpiinfo;
		kpiinfo.clear();
		kpiinfo = mapkpidatas_[tmpkpiid];
		if(kpiinfo.size() < KPI_INFO_COUNT)
		{
			loglog.str("");
			loglog << "warn : kpiid[" << tmpkpiid << "] is not in kpi_info.xml." << flush;
			MYLOG(loglog.str().c_str());
			return ;
		}

		ischildresid = LetterTolower(kpiinfo[8]);
		ischildresname = LetterTolower(kpiinfo[9]);
		kpitype = kpiinfo[4];
		reslevel = kpiinfo[6];	
	}

	//获取childindex
	CheckIsIndex(kpimapcmd, resourceattr, collectattr, childindex, TRUE);

	//处理 merge 字段
	ParseMerge(resourcevalue, collectattr);

	isSELECT = GetColumnData(tmpkpiid, resourcevalue, columnvalue, kpimapcmd, collectattr);

	string tempMetricid = ChangeKpiid(resourceattr[0]);
	string tempPluginid = ChangePluginid(resourceattr[1]);
	

	//处理判断3.5中resource->collects->collect->deal
	if(!collectattr.empty())
	{
		BOOL Method = TRUE;
		VEC_VEC_DATAS::iterator itcmdid = collectattr.begin();
		for( ; itcmdid != collectattr.end(); ++itcmdid )
		{
			//判断3.5中resource->collects->collect->deal的是否有值，如果有，则要增加注释
			string tmpdeal = itcmdid->at(4);
			if(!tmpdeal.empty())
			{
				vecdealdata.push_back(tmpdeal);
			}
		}
	}

	if(!collectattr.empty())
	{
		BOOL Method = TRUE;
		VEC_VEC_DATAS::iterator itcmdid = collectattr.begin();
		for( ; itcmdid != collectattr.end(); ++itcmdid )
		{
			VEC_STRING_DATAS veccmd;
			veccmd.clear();
			metricplugindata.clear();
			//string tmpiswalk("");
			//string temptype("");
			int vecsize = 0;
			string cmdvalues("");
			string tmpcmdid = itcmdid->at(1);

			//判断3.5中resource->collects->collect->select的是否有值，如果有，则要增加行
			string tmpselect = itcmdid->at(2);
			if(!tmpselect.empty())
			{
				isREGULAR = TRUE;
				//vecregulardata.push_back(tmpselect);
				//ParseSelectValue(tmpselect, vecregulardata);
				string findstr = "@$@";
				spilt_str_bystr(tmpselect, findstr, vecregulardata);
			}

			//如果遇到cmd中间有冒号的，直接认为为getNext模式
			VEC_STRING_DATAS veccmdid;
			veccmdid.clear();
			string methodtype("");
			if(IsMultiCmdid(tmpcmdid, veccmdid))
			{
				BOOL getmethodtype = TRUE;
				VEC_STRING_DATAS::iterator it = veccmdid.begin();
				for( ; it != veccmdid.end(); ++it )
				{
					veccmd.clear();
					if( *it == "0")
					{
						cmdvalues = cmdvalues + "0:";
						continue;
					}

					veccmd = kpimapcmd[*it];					
					if(veccmd.size() >= 3)
					{
						cmdvalues = cmdvalues + veccmd[2] + ":";
						vecsize = 3;
						if(getmethodtype)
						{
							methodtype = GetMethodType(veccmd[2]);
							getmethodtype = FALSE;
						}
					}
					else
					{
						//如果不足3，指标有问题，直接丢弃
						MYLOG(tmpcmdid.c_str());
						break;
					}
				}
				cmdvalues = cmdvalues.substr(0, cmdvalues.length() - 1);
			}
			else
			{
				veccmd = kpimapcmd[tmpcmdid];
				vecsize = (int)veccmd.size();
				if( vecsize > 3)
				{
					//说明是cmd是具有column的，属于walk
					isMainConverter = TRUE;
				}
				else if(vecsize == 3)
				{
					//没有子命令，属于getNext
					cmdvalues = veccmd[2];
				}
				else
				{
					//cmdid 不正确，直接放弃解析
					MYLOG(tmpcmdid.c_str());
					break;
				}

				methodtype = GetMethodType(cmdvalues);
			}
		
			if(reslevel == "main")
			{
				childindex = tempMetricid + "Index";
				if(!columnvalue.empty())
				{
					if(methodtype == "walk")
					{
						columnvalue = childindex + "," + columnvalue;
					}					
				}
				else
				{
					if(!vecdealdata.empty())
					{
						columnvalue = childindex + "," + tempMetricid;
					}
				}
			}
			else if(reslevel == "child")
			{
				isChildConverter = TRUE;
				if(!columnvalue.empty())
				{
					tempcolumnvalue = columnvalue;
					columnvalue = childindex + "," + columnvalue;

				}
			}
			else
			{
				//指标类型出错
				MYLOG(tmpkpiid.c_str());
				break;
			}

			//写一条方法的记录
			if(Method)
			{
				//将数据写入
				//PrepareMetricPluginSubData(metricplugindata, resourceattr);

				metricplugindata.clear();
				metricplugindata.push_back(tempMetricid);
				metricplugindata.push_back(tempPluginid);
				metricplugindata.push_back(tempnullstr);
				metricplugindata.push_back("PluginParameter");
				metricplugindata.push_back("ArrayType");
				metricplugindata.push_back(tempnullstr);	//插件classKey
				metricplugindata.push_back(tempnullstr);	//type值				
				metricplugindata.push_back("method");		//key值				
				metricplugindata.push_back(methodtype);	//value值				
				metricplugindata.push_back(columnvalue);	//column值
				//写一条完整的数据到Excel内存数据中
				MetricPluginDatas_.push_back(metricplugindata);
				metricplugindata.clear();

				Method = FALSE;
			}


			if (vecsize == 3)
			{
				//将数据写入
				//PrepareMetricPluginSubData(metricplugindata, resourceattr);

				metricplugindata.clear();
				metricplugindata.push_back(tempMetricid);
				metricplugindata.push_back(tempPluginid);
				metricplugindata.push_back(tempnullstr);
				metricplugindata.push_back("PluginParameter");
				metricplugindata.push_back("ArrayType");
				metricplugindata.push_back(tempnullstr);	//插件classKey
				metricplugindata.push_back(tempnullstr);	//type值					
				metricplugindata.push_back(tempnullstr);	//key值				
				metricplugindata.push_back(cmdvalues);		//value值				
				metricplugindata.push_back(columnvalue);	//column值
				//写一条完整的数据到Excel内存数据中
				MetricPluginDatas_.push_back(metricplugindata);
				
			}
			else
			{
				//vecsize > 3 的情况
				VEC_STRING_DATAS::iterator itcmd = veccmd.begin() + 3;
				//veccmd存储了cmd和说明
				for( int i = 0 ; itcmd != veccmd.end(); ++itcmd,++i)
				{
					//将数据写入
					//PrepareMetricPluginSubData(metricplugindata, resourceattr);

					metricplugindata.clear();
					metricplugindata.push_back(tempMetricid);
					metricplugindata.push_back(tempPluginid);
					metricplugindata.push_back(tempnullstr);
					metricplugindata.push_back("PluginParameter");
					metricplugindata.push_back("ArrayType");
					metricplugindata.push_back(tempnullstr);	//插件classKey
					metricplugindata.push_back(tempnullstr);	//type值					
					metricplugindata.push_back(tempnullstr);		//key值					
					metricplugindata.push_back(*itcmd);		//value值					
					metricplugindata.push_back(columnvalue);	//column值

					//写一条完整的数据到Excel内存数据中
					MetricPluginDatas_.push_back(metricplugindata);

					++itcmd;
					if(itcmd == veccmd.end())
					{
						break;
					}
				}
			}
		}
	}

	if((childindex == "hostFileSystemIndex") && (tempMetricid != "fileSysName"))
	{
		childindex = "fileSysName";
	}

	if(isREGULAR)
	{
		VEC_STRING_DATAS::iterator itregular = vecregulardata.begin();
		for( ; itregular != vecregulardata.end(); ++itregular )
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempnullstr);
			metricplugindata.push_back("PluginDataHandler");			
			metricplugindata.push_back(tempnullstr);		//插件type			
			metricplugindata.push_back("RegularFilter");	//插件classKey			
			metricplugindata.push_back(tempnullstr);		//type值			
			metricplugindata.push_back("REGULAR");			//key值			
			metricplugindata.push_back(*itregular);			//value值			
			metricplugindata.push_back(columnvalue);		//column值			
			//写一条完整的数据到Excel内存数据中
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();
		}
	}

	if(isSELECT)
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataHandler");		
		metricplugindata.push_back(tempnullstr);	//插件type		
		metricplugindata.push_back("selectProcessor");	//插件classKey		
		metricplugindata.push_back(tempnullstr);	//type值		
		metricplugindata.push_back("SELECT");	//key值

		//value值
		string tmptype("");
		if(isChildConverter)
		{
			//tmptype = "SELECT ifIndex,(" + resourcevalue + ") as " + tempMetricid;
			tmptype = "SELECT " + childindex + ",(" + resourcevalue + ") as " + tempMetricid;
		}
		else
		{
			tmptype = "SELECT (" + resourcevalue + ") as " + tempMetricid;
		}
		//处理resourcevalue里面的 $变量
		SET_STRING_DATAS replacedatas;
		replacedatas.clear();
		if(ParsePluginDataHandlerValue(tmptype, replacedatas))
		{
			if(!replacedatas.empty())
			{
				VEC_STRING_DATAS kpiinfo;
				kpiinfo.clear();
				string kpiid = resourceattr[0];
				kpiinfo = mapkpidatas_[kpiid];
				string childrestype = kpiinfo[7];

				VEC_STRING_DATAS tmpvec;
				SET_STRING_DATAS::iterator it = replacedatas.begin();
				for( ; it != replacedatas.end(); ++it)
				{
					string tempvalue = *it;
					//如果变量是 属性中没有的，增加进去
					CheckPropertiesData(tempvalue, isChildConverter, childrestype);

					//增加MetricPluginData
					tmpvec.clear();
					tmpvec.push_back(tempMetricid);
					tmpvec.push_back(tempPluginid);
					tmpvec.push_back(tempnullstr);
					tmpvec.push_back("PluginDataHandler");		
					tmpvec.push_back(tempnullstr);	//插件type		
					tmpvec.push_back("selectProcessor");	//插件classKey		
					tmpvec.push_back("ResourceProperty");	//type值		
					tmpvec.push_back(tempvalue);	//key值
					tmpvec.push_back(tempvalue);	//value值
					tmpvec.push_back(columnvalue);	//column值		
					//写一条完整的数据到Excel内存数据中
					MetricPluginDatas_.push_back(tmpvec);
				}
			}
		}

		metricplugindata.push_back(tmptype);		//value值		
		metricplugindata.push_back(columnvalue);	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(metricplugindata);
		metricplugindata.clear();
	}

	if(!vecdealdata.empty())
	{
		VEC_STRING_DATAS::iterator it = vecdealdata.begin();
		string tempdeal = *it;
		VEC_STRING_DATAS tmpvec;

		//增加MetricPluginData
		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempnullstr);
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//插件type		
		tmpvec.push_back("ChangeRateProcessor");	//插件classKey		
		tmpvec.push_back("");	//type值
		tmpvec.push_back("COLUMNINDEX");	//key值
		tmpvec.push_back(childindex);	//value值
		tmpvec.push_back(columnvalue);	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(tmpvec);
	
		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempnullstr);
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//插件type		
		tmpvec.push_back("ChangeRateProcessor");	//插件classKey		
		tmpvec.push_back("");	//type值
		tmpvec.push_back("FUNCTION");	//key值
		tmpvec.push_back(LetterToupper(tempdeal));	//value值
		tmpvec.push_back(columnvalue);	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(tmpvec);

	}

	if((!translationsdata.empty()) && (kpitype != "avail"))
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataHandler");			
		metricplugindata.push_back(tempnullstr);		//插件type			
		metricplugindata.push_back("translateProcessor");	//插件classKey			
		metricplugindata.push_back(tempnullstr);		//type值
		if(isSELECT)
		{
			metricplugindata.push_back(tempMetricid);	//key值
		}
		else
		{
			int npos = tempcolumnvalue.find_last_of(",");
			if(npos >= 0)
			{
				tempcolumnvalue = tempcolumnvalue.substr(npos + 1, tempcolumnvalue.length() - npos - 1);
			}
			metricplugindata.push_back(tempcolumnvalue);	//key值
		}

		if(tempMetricid == "ifType")
		{
			string temptranslation = translationsdata + ";other";
			metricplugindata.push_back(temptranslation);			//value值
		}
		else
		{
			metricplugindata.push_back(translationsdata);			//value值
		}

		metricplugindata.push_back(columnvalue);		//column值			
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(metricplugindata);
		metricplugindata.clear();
	}

	if(isMainConverter && (!isChildConverter))
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataHandler");		
		metricplugindata.push_back(tempnullstr);	//插件type		
		metricplugindata.push_back("tableConverter");	//插件classKey		
		metricplugindata.push_back(tempnullstr);	//type值		
		metricplugindata.push_back(tempnullstr);	//key值		
		metricplugindata.push_back(tempnullstr);	//value值		
		metricplugindata.push_back(columnvalue);	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(metricplugindata);
		metricplugindata.clear();
	}


	if(isChildConverter)
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataConverter");		
		metricplugindata.push_back(tempnullstr);	//插件type		
		metricplugindata.push_back("subInstConverter");	//插件classKey		
		metricplugindata.push_back(tempnullstr);	//type值		
		metricplugindata.push_back("IndexColumnTitle");	//key值		
		//metricplugindata.push_back("ifIndex");	//value值	
		metricplugindata.push_back(childindex);	//value值
		metricplugindata.push_back(columnvalue);	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(metricplugindata);

		
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataConverter");		
		metricplugindata.push_back(tempnullstr);	//插件type		
		metricplugindata.push_back("subInstConverter");	//插件classKey		
		metricplugindata.push_back(tempnullstr);	//type值		
		metricplugindata.push_back("ValueColumnTitle");	//key值	
		if(isSELECT)
		{
			metricplugindata.push_back(tempMetricid);	//value值
		}
		else
		{
			int npos = tempcolumnvalue.find_last_of(",");
			if(npos >= 0)
			{
				tempcolumnvalue = tempcolumnvalue.substr(npos + 1, tempcolumnvalue.length() - npos - 1);
			}
			metricplugindata.push_back(tempcolumnvalue);
		}
		metricplugindata.push_back(columnvalue);	//column值		
		//写一条完整的数据到Excel内存数据中
		MetricPluginDatas_.push_back(metricplugindata);

		if((childindex != tempMetricid) && (tempMetricid != "fileSysName"))
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempnullstr);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//插件type		
			metricplugindata.push_back("subInstConverter");	//插件classKey		
			metricplugindata.push_back("ResourceProperty");	//type值		
			metricplugindata.push_back("InstPropertyKey");	//key值		
			//metricplugindata.push_back("ifIndex");	//value值	
			metricplugindata.push_back(childindex);	//value值	
			metricplugindata.push_back(columnvalue);	//column值		
			//写一条完整的数据到Excel内存数据中
			MetricPluginDatas_.push_back(metricplugindata);
		}
		
	}

	if(tempMetricid == "macAddress")
	{
		string tempproperties = "macAddress";
		CheckPropertiesData(tempproperties, FALSE, tempnullstr);
	}

}


