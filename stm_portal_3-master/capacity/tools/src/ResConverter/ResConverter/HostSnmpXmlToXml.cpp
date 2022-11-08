#include "StdAfx.h"
#include "HostSnmpXmlToXml.h"

HostSnmpXmlToXml::HostSnmpXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag)
{

	//MYLOG("entry");

	//�������Ĳ����Ƿ����
	if(!CheckParam(xmlpath, filename, kpifilename, outfilepath))
	{
		MYLOG("error : ����Ĳ�������");
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
	//��ȡ��Դ��Ϣ
	if(!GetResourceInfo())
	{
		loglog.str("");
		loglog << "��ȡ�ļ�" << strXmlPath_	 << filename_ << "��Ϣʧ��." << flush;
		MYLOG(loglog.str().c_str());
		//AfxMessageBox(loglog.str().c_str());
		return;
	}

	//��ʼ��һЩָ����Ϣ
	InitDatas();

	GetKpiDatas(mapkpidatas_);

	Chipolicy(mapchipolicydatas_);

	//�����Լ����ļ�
	ParseHostSnmp();

	//����������Ϊд�ļ�׼��
	PreperDatasForWrite();

	switch(flag)
	{
	case XML_TO_XML_EXCEL:
		WriteCollectXml();		//����collect xml�ļ�		
		WriteResourceXml();  //����Resource xml�ļ�
		WriteExcel();  //������д�뵽excel��
		break;
	case XML_TO_XML:		
		WriteCollectXml();		//����collect xml�ļ�		
		WriteResourceXml();  //����Resource xml�ļ�
		break;
	case XML_TO_EXCEL:		
		WriteExcel();  //������д�뵽excel��
		break;
	default :
		loglog.str("");
		loglog << "����ķ�ʽ����ȷ. flag = [" << flag << "]." << flush;
		MYLOG(loglog.str().c_str());
		return;
	}

}

void HostSnmpXmlToXml::InitDatas()
{
	VEC_STRING_DATAS tmpdata;

	ismodifyhostname_ = TRUE;

	setChildIndexDatas_.clear();

	//CGlobalMetricSettingDatas_��ʼ��
	//ĿǰΪ��

	//PluginClassAliasListDatas_ ��ʼ��
	PluginClassAliasListDatas_ = g_GlobalData.PCALDatas_;

	//RGlobalMetricSettingDatas_ ��ʼ��
	RGlobalMetricSettingDatas_ = g_GlobalData.RGMSDatas_;	

	//MetricPluginDatas_ ��ʼ��
	//���� macAddress ָ��
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
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("");			//keyֵ
	tmpdata.push_back("1.3.6.1.2.1.2.2.1.8");		//valueֵ
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("");			//keyֵ
	tmpdata.push_back("1.3.6.1.2.1.2.2.1.7");		//valueֵ
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
	tmpdata.push_back("");	//���type
	tmpdata.push_back("subInstConverter");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("IndexColumnTitle");			//keyֵ
	tmpdata.push_back("hostInterfaceIndex");		//valueֵ
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginDataConverter");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("subInstConverter");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("ValueColumnTitle");			//keyֵ
	tmpdata.push_back("ifStatus");		//valueֵ
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("SnmpPlugin");
	tmpdata.push_back("");
	tmpdata.push_back("PluginDataConverter");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("subInstConverter");			//���classKey
	tmpdata.push_back("ResourceProperty");			//typeֵ
	tmpdata.push_back("InstPropertyKey");			//keyֵ
	tmpdata.push_back("hostInterfaceIndex");		//valueֵ
	tmpdata.push_back("hostInterfaceIndex,ifOperStatus,ifAdminStatus");
	MetricPluginDatas_.push_back(tmpdata);


	string subresourceid = resourceid_ + FirstLetterToupper("hostInterface");
	tmpdata.clear();
	tmpdata.push_back(subresourceid);
	tmpdata.push_back("");
	tmpdata.push_back("");
	tmpdata.push_back("�ӿ�");
	tmpdata.push_back("�ӿ�");
	tmpdata.push_back("hostInterface");
	tmpdata.push_back(resourceid_);
	
	tmpdata.push_back("ifStatus");
	tmpdata.push_back("AvailabilityMetric");
	tmpdata.push_back("����������");
	tmpdata.push_back("����������");
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


	//PropertiesDatas_ ��ʼ�� ����Դ
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


	//InstantiationDatas_ ��ʼ�� ����Դ
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
		loglog << "�ļ��� " << pathfilename << " �����ڣ�";
		AfxMessageBox(loglog.str().c_str());
		MYLOG(loglog.str().c_str());
		return ;
	}

	file<> fdoc(pathfilename.c_str());
	xml_document<>   doc;
	doc.parse<0>(fdoc.data());

	//! ��ȡ���ڵ�
	xml_node<>* root = doc.first_node();

	//! ��ȡ���ڵ��һ���ڵ�	
	for(rapidxml::xml_node<char> * devicetypeinfo = root->first_node("devicetypeinfo");
		devicetypeinfo != NULL;
		devicetypeinfo = devicetypeinfo->next_sibling())
	{
		//tmplog.str("");
		VEC_STRING_DATAS deviceattr;
		deviceattr.clear();
		/********************************************************************
		����devicetypeinfo�ڵ������
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
		����devicetypeinfo�ڵ���ӽڵ�commands
		**********************************************************************/
		//map�洢�� key��comid�� value��һ��vec����һ��Ԫ��Ϊcoltype������Ϊcommand���ж��ٸ��ʹ洢���ٸ���
		MAP_VEC_DATAS kpimapcmd;
		kpimapcmd.clear();
		for(rapidxml::xml_node<char> * commands = devicetypeinfo->first_node("commands");
			commands != NULL;
			commands = commands->next_sibling())
		{
			VEC_STRING_DATAS valuedata;
			//����commands�ڵ���ӽڵ�command
			for(rapidxml::xml_node<char> * command = commands->first_node("command");
				command != NULL;
				command = command->next_sibling())
			{
				//tmplog.str("");
				valuedata.clear();
				//����command�ڵ������
				for(rapidxml::xml_attribute<char> * attr2 = command->first_attribute();
					attr2 != NULL;
					attr2 = attr2->next_attribute())
				{
					//��ȡ����Ϊcoltype��comid��command
					valuedata.push_back(UTF8ToGB(attr2->value()));
					//tmplog << "[" << attr2->name() << "=" << UTF8ToGB(attr2->value()) << "]" <<flush;
				}
				//MYLOG(tmplog.str().c_str());
				//����command�ڵ���ӽڵ�columns
				for(rapidxml::xml_node<char> * columns = command->first_node("columns");
					columns != NULL;
					columns = columns->next_sibling())
				{
					//tmplog.str("");
					//����command�ڵ���ӽڵ�column
					for(rapidxml::xml_node<char> * column = columns->first_node("column");
						column != NULL;
						column = column->next_sibling())
					{
						//tmplog.str("");
						//����column�ڵ������
						for(rapidxml::xml_attribute<char> * attr3 = column->first_attribute();
							attr3 != NULL;
							attr3 = attr3->next_attribute())
						{
							//Ԫ��˳��Ϊkey��value
							//cisco_switch.xml ������  key��value��select��defaultvalue
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
		����devicetypeinfo�ڵ���ӽڵ�resources
		**********************************************************************/
		//
		for(rapidxml::xml_node<char> * resources = devicetypeinfo->first_node("resources");
			resources != NULL;
			resources = resources->next_sibling())
		{

			//����resources�ڵ���ӽڵ�resource
			for(rapidxml::xml_node<char> * resource = resources->first_node("resource");
				resource != NULL;
				resource = resource->next_sibling())
			{
				VEC_STRING_DATAS resourceattr;
				VEC_VEC_DATAS collectattr;
				string  translationsdata("");
				VEC_STRING_DATAS kpipolicyattr;
				//����resource�ڵ������
				for(rapidxml::xml_attribute<char> * attr4 = resource->first_attribute();
					attr4 != NULL;
					attr4 = attr4->next_attribute())
				{
					//��ȡ����Ϊkpiid��coltype��value��valuetype��unit
					resourceattr.push_back(UTF8ToGB(attr4->value()));
				}

				//����resource�ڵ���ӽڵ�collects
				for(rapidxml::xml_node<char> * collects = resource->first_node("collects");
					collects != NULL;
					collects = collects->next_sibling())
				{
					//����collects�ڵ���ӽڵ�collect
					for(rapidxml::xml_node<char> * collect = collects->first_node("collect");
						collect != NULL;
						collect = collect->next_sibling())
					{
						//����collect�ڵ������
						VEC_STRING_DATAS tmpcollectattr;
						for(rapidxml::xml_attribute<char> * attr5 = collect->first_attribute();
							attr5 != NULL;
							attr5 = attr5->next_attribute())
						{
							//Ԫ��˳��Ϊexeorder��comid��select��merge��deal
							tmpcollectattr.push_back(UTF8ToGB(attr5->value()));
							//break;
						}
						collectattr.push_back(tmpcollectattr);
					}
				}

				//����resource�ڵ���ӽڵ�translations
				for(rapidxml::xml_node<char> * translations = resource->first_node("translations");
					translations != NULL;
					translations = translations->next_sibling())
				{
					//����translations�ڵ���ӽڵ�translation
					for(rapidxml::xml_node<char> * translation = translations->first_node("translation");
						translation != NULL;
						translation = translation->next_sibling())
					{
						string tempsource("");
						string tempdest("");
						//����translation�ڵ������
						for(rapidxml::xml_attribute<char> * attr6 = translation->first_attribute();
							attr6 != NULL;
							attr6 = attr6->next_attribute())
						{
							//Ԫ��˳��Ϊsource��dest
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

				//������һ���ֺ�ȥ��
				translationsdata = translationsdata.substr(0, translationsdata.length() - 1);

				//����resource�ڵ���ӽڵ�kpipolicy
				for(rapidxml::xml_node<char> * kpipolicy = resource->first_node("kpipolicy");
					kpipolicy != NULL;
					kpipolicy = kpipolicy->next_sibling())
				{
					//����collect�ڵ������
					for(rapidxml::xml_attribute<char> * attr7 = kpipolicy->first_attribute();
						attr7 != NULL;
						attr7 = attr7->next_attribute())
					{
						//Ԫ��˳��Ϊtable��field��fieldtype��willalert��alertexp1��alertdesc1��
						//          alertexp2��alertdesc2��alertexp3��alertdesc3��alertexp4��alertdesc4��
						//          alertexp5��alertdesc5��alertcontin��circle��storefactor
						//������ֻ��һ��kpipolicy������forֻѭ��һ�Σ��������Ļ���������Чֻȡһ��
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
	//��5������
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

	//���resourceattr����ȷ��
	if(!CheckResourceattr(resourceattr))
	{
		return;
	}


	//MetricPlugin sheetҳ������׼��
	PrepareMetricPluginAllData(kpimapcmd, resourceattr, collectattr, translationsdata);

	//Resource sheetҳ������׼��
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

			//�������ð�ţ�ȫ��תΪ�»���
			//�������ð��0��β�ģ�ֱ��ȥ��Ĭ��ֵ
			ChangeColon(newvalue);

			resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue);
			columnvalue = columnvalue + newvalue + ",";
		}
	}
	else
	{
		//�ж�������
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
				//veccmd�洢��cmd��˵��
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
		//�������ð�ţ�ȫ��תΪ�»���
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
		tmpvec.push_back("ArrayType");	//���type		
		tmpvec.push_back("");	//���classKey		
		tmpvec.push_back("DiscoveryInfo");	//typeֵ		
		tmpvec.push_back("IP");	//keyֵ
		tmpvec.push_back("IP");	//valueֵ
		tmpvec.push_back("ip,isAlive,ResponseTime");	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);

		tmpvec.clear();
		tmpvec.push_back("availability");
		tmpvec.push_back("IcmpPlugin");
		tmpvec.push_back("");
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//���type		
		tmpvec.push_back("selectProcessor");	//���classKey		
		tmpvec.push_back("");	//typeֵ		
		tmpvec.push_back("SELECT");	//keyֵ
		tmpvec.push_back("SELECT isAlive");	//valueֵ

		tmpvec.push_back("ip,isAlive,ResponseTime");	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
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
		tmpvec.push_back("ArrayType");	//���type		
		tmpvec.push_back("");	//���classKey		
		tmpvec.push_back("DiscoveryInfo");	//typeֵ		
		tmpvec.push_back("IP");	//keyֵ
		tmpvec.push_back("IP");	//valueֵ
		tmpvec.push_back("ip,isAlive,ResponseTime");	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);

		tmpvec.clear();
		tmpvec.push_back("icmpDelayTime");
		tmpvec.push_back("IcmpPlugin");
		tmpvec.push_back("");
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//���type		
		tmpvec.push_back("selectProcessor");	//���classKey		
		tmpvec.push_back("");	//typeֵ		
		tmpvec.push_back("SELECT");	//keyֵ
		tmpvec.push_back("SELECT ResponseTime");	//valueֵ
		tmpvec.push_back("ip,isAlive,ResponseTime");	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
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

	//{"metricid", "pluginid", "collectType", "�ɼ�����", "���type", "���classKey", "typeֵ", "keyֵ", "valueֵ", "columnֵ"};

	string tmpkpiid = resourceattr[0];
	string resourcevalue = resourceattr[2];
	string columnvalue("");
	string tempcolumnvalue("");
	string kpitype("");
	string reslevel("");
	string childindex("");
	string ischildresid("");
	string ischildresname("");

	//�ж��Ƿ��Ѿ����ڣ�������ڣ�ֱ�ӷ���
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
		//�ж�ָ��id�Ƿ���ȷ
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

	//��ȡchildindex
	CheckIsIndex(kpimapcmd, resourceattr, collectattr, childindex, TRUE);

	//���� merge �ֶ�
	ParseMerge(resourcevalue, collectattr);

	isSELECT = GetColumnData(tmpkpiid, resourcevalue, columnvalue, kpimapcmd, collectattr);

	string tempMetricid = ChangeKpiid(resourceattr[0]);
	string tempPluginid = ChangePluginid(resourceattr[1]);
	

	//�����ж�3.5��resource->collects->collect->deal
	if(!collectattr.empty())
	{
		BOOL Method = TRUE;
		VEC_VEC_DATAS::iterator itcmdid = collectattr.begin();
		for( ; itcmdid != collectattr.end(); ++itcmdid )
		{
			//�ж�3.5��resource->collects->collect->deal���Ƿ���ֵ������У���Ҫ����ע��
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

			//�ж�3.5��resource->collects->collect->select���Ƿ���ֵ������У���Ҫ������
			string tmpselect = itcmdid->at(2);
			if(!tmpselect.empty())
			{
				isREGULAR = TRUE;
				//vecregulardata.push_back(tmpselect);
				//ParseSelectValue(tmpselect, vecregulardata);
				string findstr = "@$@";
				spilt_str_bystr(tmpselect, findstr, vecregulardata);
			}

			//�������cmd�м���ð�ŵģ�ֱ����ΪΪgetNextģʽ
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
						//�������3��ָ�������⣬ֱ�Ӷ���
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
					//˵����cmd�Ǿ���column�ģ�����walk
					isMainConverter = TRUE;
				}
				else if(vecsize == 3)
				{
					//û�����������getNext
					cmdvalues = veccmd[2];
				}
				else
				{
					//cmdid ����ȷ��ֱ�ӷ�������
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
				//ָ�����ͳ���
				MYLOG(tmpkpiid.c_str());
				break;
			}

			//дһ�������ļ�¼
			if(Method)
			{
				//������д��
				//PrepareMetricPluginSubData(metricplugindata, resourceattr);

				metricplugindata.clear();
				metricplugindata.push_back(tempMetricid);
				metricplugindata.push_back(tempPluginid);
				metricplugindata.push_back(tempnullstr);
				metricplugindata.push_back("PluginParameter");
				metricplugindata.push_back("ArrayType");
				metricplugindata.push_back(tempnullstr);	//���classKey
				metricplugindata.push_back(tempnullstr);	//typeֵ				
				metricplugindata.push_back("method");		//keyֵ				
				metricplugindata.push_back(methodtype);	//valueֵ				
				metricplugindata.push_back(columnvalue);	//columnֵ
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);
				metricplugindata.clear();

				Method = FALSE;
			}


			if (vecsize == 3)
			{
				//������д��
				//PrepareMetricPluginSubData(metricplugindata, resourceattr);

				metricplugindata.clear();
				metricplugindata.push_back(tempMetricid);
				metricplugindata.push_back(tempPluginid);
				metricplugindata.push_back(tempnullstr);
				metricplugindata.push_back("PluginParameter");
				metricplugindata.push_back("ArrayType");
				metricplugindata.push_back(tempnullstr);	//���classKey
				metricplugindata.push_back(tempnullstr);	//typeֵ					
				metricplugindata.push_back(tempnullstr);	//keyֵ				
				metricplugindata.push_back(cmdvalues);		//valueֵ				
				metricplugindata.push_back(columnvalue);	//columnֵ
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);
				
			}
			else
			{
				//vecsize > 3 �����
				VEC_STRING_DATAS::iterator itcmd = veccmd.begin() + 3;
				//veccmd�洢��cmd��˵��
				for( int i = 0 ; itcmd != veccmd.end(); ++itcmd,++i)
				{
					//������д��
					//PrepareMetricPluginSubData(metricplugindata, resourceattr);

					metricplugindata.clear();
					metricplugindata.push_back(tempMetricid);
					metricplugindata.push_back(tempPluginid);
					metricplugindata.push_back(tempnullstr);
					metricplugindata.push_back("PluginParameter");
					metricplugindata.push_back("ArrayType");
					metricplugindata.push_back(tempnullstr);	//���classKey
					metricplugindata.push_back(tempnullstr);	//typeֵ					
					metricplugindata.push_back(tempnullstr);		//keyֵ					
					metricplugindata.push_back(*itcmd);		//valueֵ					
					metricplugindata.push_back(columnvalue);	//columnֵ

					//дһ�����������ݵ�Excel�ڴ�������
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
			metricplugindata.push_back(tempnullstr);		//���type			
			metricplugindata.push_back("RegularFilter");	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ			
			metricplugindata.push_back("REGULAR");			//keyֵ			
			metricplugindata.push_back(*itregular);			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
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
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("selectProcessor");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back("SELECT");	//keyֵ

		//valueֵ
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
		//����resourcevalue����� $����
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
					//��������� ������û�еģ����ӽ�ȥ
					CheckPropertiesData(tempvalue, isChildConverter, childrestype);

					//����MetricPluginData
					tmpvec.clear();
					tmpvec.push_back(tempMetricid);
					tmpvec.push_back(tempPluginid);
					tmpvec.push_back(tempnullstr);
					tmpvec.push_back("PluginDataHandler");		
					tmpvec.push_back(tempnullstr);	//���type		
					tmpvec.push_back("selectProcessor");	//���classKey		
					tmpvec.push_back("ResourceProperty");	//typeֵ		
					tmpvec.push_back(tempvalue);	//keyֵ
					tmpvec.push_back(tempvalue);	//valueֵ
					tmpvec.push_back(columnvalue);	//columnֵ		
					//дһ�����������ݵ�Excel�ڴ�������
					MetricPluginDatas_.push_back(tmpvec);
				}
			}
		}

		metricplugindata.push_back(tmptype);		//valueֵ		
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
		metricplugindata.clear();
	}

	if(!vecdealdata.empty())
	{
		VEC_STRING_DATAS::iterator it = vecdealdata.begin();
		string tempdeal = *it;
		VEC_STRING_DATAS tmpvec;

		//����MetricPluginData
		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempnullstr);
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//���type		
		tmpvec.push_back("ChangeRateProcessor");	//���classKey		
		tmpvec.push_back("");	//typeֵ
		tmpvec.push_back("COLUMNINDEX");	//keyֵ
		tmpvec.push_back(childindex);	//valueֵ
		tmpvec.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);
	
		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempnullstr);
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back("");	//���type		
		tmpvec.push_back("ChangeRateProcessor");	//���classKey		
		tmpvec.push_back("");	//typeֵ
		tmpvec.push_back("FUNCTION");	//keyֵ
		tmpvec.push_back(LetterToupper(tempdeal));	//valueֵ
		tmpvec.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);

	}

	if((!translationsdata.empty()) && (kpitype != "avail"))
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataHandler");			
		metricplugindata.push_back(tempnullstr);		//���type			
		metricplugindata.push_back("translateProcessor");	//���classKey			
		metricplugindata.push_back(tempnullstr);		//typeֵ
		if(isSELECT)
		{
			metricplugindata.push_back(tempMetricid);	//keyֵ
		}
		else
		{
			int npos = tempcolumnvalue.find_last_of(",");
			if(npos >= 0)
			{
				tempcolumnvalue = tempcolumnvalue.substr(npos + 1, tempcolumnvalue.length() - npos - 1);
			}
			metricplugindata.push_back(tempcolumnvalue);	//keyֵ
		}

		if(tempMetricid == "ifType")
		{
			string temptranslation = translationsdata + ";other";
			metricplugindata.push_back(temptranslation);			//valueֵ
		}
		else
		{
			metricplugindata.push_back(translationsdata);			//valueֵ
		}

		metricplugindata.push_back(columnvalue);		//columnֵ			
		//дһ�����������ݵ�Excel�ڴ�������
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
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("tableConverter");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back(tempnullstr);	//keyֵ		
		metricplugindata.push_back(tempnullstr);	//valueֵ		
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
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
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("subInstConverter");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back("IndexColumnTitle");	//keyֵ		
		//metricplugindata.push_back("ifIndex");	//valueֵ	
		metricplugindata.push_back(childindex);	//valueֵ
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);

		
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempnullstr);
		metricplugindata.push_back("PluginDataConverter");		
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("subInstConverter");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
		if(isSELECT)
		{
			metricplugindata.push_back(tempMetricid);	//valueֵ
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
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);

		if((childindex != tempMetricid) && (tempMetricid != "fileSysName"))
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempnullstr);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back("ResourceProperty");	//typeֵ		
			metricplugindata.push_back("InstPropertyKey");	//keyֵ		
			//metricplugindata.push_back("ifIndex");	//valueֵ	
			metricplugindata.push_back(childindex);	//valueֵ	
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
		}
		
	}

	if(tempMetricid == "macAddress")
	{
		string tempproperties = "macAddress";
		CheckPropertiesData(tempproperties, FALSE, tempnullstr);
	}

}


