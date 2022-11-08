#include "StdAfx.h"
#include "HostXmlToXml.h"

HostXmlToXml::HostXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag)
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

HostXmlToXml::~HostXmlToXml(void)
{
	//MYLOG("entry");
}

void HostXmlToXml::AnalysisMain(int flag)
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
	ParseHost();

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

void HostXmlToXml::InitDatas()
{
	VEC_STRING_DATAS tmpdata;

	ismodifyhostname_ = TRUE;
	issolaris_ = FALSE;

	if(resourceid_ == "Solaris")
	{
		issolaris_ = TRUE;
	}

	//CGlobalMetricSettingDatas_��ʼ��
	//ĿǰΪ��

	//PluginClassAliasListDatas_ ��ʼ��
	PluginClassAliasListDatas_ = g_GlobalData.PCALDatas_;
	tmpdata.clear();
	tmpdata.push_back("processConverter");
	tmpdata.push_back("com.mainsteam.stm.plugin.common.ProcessResultSetConverter");
	PluginClassAliasListDatas_.push_back(tmpdata);

	if(issolaris_)
	{
		tmpdata.clear();
		tmpdata.push_back("SolarisNetStatProcessor");
		tmpdata.push_back("com.mainsteam.stm.plugin.common.SolarisNetStatProcessor");
		PluginClassAliasListDatas_.push_back(tmpdata);
	}

	//RGlobalMetricSettingDatas_ ��ʼ��
	RGlobalMetricSettingDatas_ = g_GlobalData.RGMSDatas_;

	
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
	tmpdata.push_back("macAddress");
	tmpdata.push_back("macAddress");
	tmpdata.push_back("macAddress");
	PropertiesDatas_.push_back(tmpdata);
	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back("sysUpTime");
	tmpdata.push_back("sysUpTime");
	tmpdata.push_back("sysUpTime");
	PropertiesDatas_.push_back(tmpdata);

	//PropertiesDatas_ ��ʼ�� ����ԴhostProcess
	string subresourceid = resourceid_ + FirstLetterToupper("HostProcess");
	tmpdata.clear();
	tmpdata.push_back(subresourceid);
	tmpdata.push_back("processId");
	tmpdata.push_back("processId");
	tmpdata.push_back("processId");
	PropertiesDatas_.push_back(tmpdata);
	tmpdata.clear();
	tmpdata.push_back(subresourceid);
	tmpdata.push_back("processRemark");
	tmpdata.push_back("processRemark");
	tmpdata.push_back("");
	PropertiesDatas_.push_back(tmpdata);
	tmpdata.clear();
	tmpdata.push_back(subresourceid);
	tmpdata.push_back("instanceIdKeyValues");
	tmpdata.push_back("instanceIdKeyValues");
	tmpdata.push_back("");
	PropertiesDatas_.push_back(tmpdata);

	VEC_VEC_DATAS::iterator it = PropertiesDatas_.begin();
	for( ; it != PropertiesDatas_.end(); ++it)
	{
		PropertiesData_.insert((it->at(0)) + (it->at(1)));
	}


	//InstantiationDatas_ ��ʼ�� ����Դ
	string mainvalue = ",macAddress,";
	mapinstantiationId_[resourceid_] = mainvalue;
	mapinstantiationName_[resourceid_] = "Name";
	//InstantiationDatas_.push_back(tmpdata);

	//InstantiationDatas_ ��ʼ�� ����ԴhostProcess
	//tmpdata.clear();
	//tmpdata.push_back("hostProcess");
	//tmpdata.push_back("processCommand");
	//tmpdata.push_back("processCommand");
	//InstantiationDatas_.push_back(tmpdata);
	//string hostProcess = ",processCommand,";
	//mapinstantiationId_[subresourceid] = hostProcess;
	//mapinstantiationName_[subresourceid] = "processCommand";	

	//if(isLinux_)
	//{
		string cpusubresourceid = resourceid_ + FirstLetterToupper("cpu");
		mapinstantiationName_[cpusubresourceid] = "cpuId";
	//}
}



void HostXmlToXml::ParseHost()
{
	string filename = strXmlPath_ + filename_;
	ParseFile(filename);
}

void HostXmlToXml::ParseFile(string pathfilename)
{
	VEC_STRING_DATAS tmpdata;

	//

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
		int counttest = 0;
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
				//counttest++;
				//stringstream mytest("");
				//mytest << counttest;
				//MYLOG(mytest.str().c_str());
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

BOOL HostXmlToXml::CheckResourceattr(VEC_STRING_DATAS &resourceattr)
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

void HostXmlToXml::ParseMetricData(VEC_STRING_DATAS &deviceattr, MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, VEC_STRING_DATAS &kpipolicyattr, string translationsdata)
{

	//���resourceattr����ȷ��
	if(!CheckResourceattr(resourceattr))
	{
		return;
	}

	//MYLOG(resourceattr[0].c_str());

	if(resourceattr[1] == "CLI")
	{
		//MetricPlugin sheetҳ������׼��

		//����תΪssh
		resourceattr[1] = "SSH";
		PrepareMetricPluginAllData(kpimapcmd, resourceattr, collectattr, translationsdata);
		//����תΪtelnet
		resourceattr[1] = "TELNET";
		PrepareMetricPluginAllData(kpimapcmd, resourceattr, collectattr, translationsdata);


		//Resource sheetҳ������׼��
		PrepareResourceAllData(deviceattr, resourceattr, kpipolicyattr);
	}
	else
	{
		//MetricPlugin sheetҳ������׼��
		PrepareMetricPluginAllData(kpimapcmd, resourceattr, collectattr, translationsdata);

		//Resource sheetҳ������׼��
		PrepareResourceAllData(deviceattr, resourceattr, kpipolicyattr);
	}
}


BOOL HostXmlToXml::GetColumnData(string kpiid, string collecttype, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr, string childrestype, BOOL &isSpecial)
{
	BOOL isSELECT = FALSE;

	if(!resourcevalue.empty())
	{
		if((childrestype == "hostProcess") && (collecttype != "WMI"))
		{
			if(kpiid == "H1processAvail")
			{
				isSELECT = TRUE;
			}
			columnvalue = resourcevalue;
			columnvalue = replace_all_distinct(columnvalue, "(", "");
			columnvalue = replace_all_distinct(columnvalue, ")", "");
		}
		else
		{
			isSELECT = TRUE;

			SET_INT_DATAS setparam;
			setparam.clear();
			GetParam(resourcevalue, setparam);

			//���������ʵ�ʵ�collectҪ��
			if(setparam.size() > collectattr.size())
			{
				isSpecial = TRUE;
				SET_INT_DATAS::iterator it = setparam.begin();
				for( ; it != setparam.end(); ++it )
				{
					stringstream oldvalue("");
					stringstream newvalue("");
					oldvalue << "$d" << *it << flush;
					newvalue << "column" << *it << flush;

					resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue.str());
					columnvalue = columnvalue + newvalue.str() + ",";
				}

			}
			else
			{
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

	//ȥ������һ�����ţ�����hostProcess��Ҫȥ��
	if((!columnvalue.empty()) &&
			((childrestype != "hostProcess") || ((childrestype == "hostProcess") && (collecttype == "WMI"))))
	{
		columnvalue = columnvalue.substr(0, columnvalue.length() - 1);		
	}

	return isSELECT;
}


BOOL HostXmlToXml::SpecialHandling(VEC_STRING_DATAS &resourceattr)
{
	BOOL ret = FALSE;
	string kpiid = resourceattr[0];
	string coltype = resourceattr[1];
	string metricid = ChangeKpiid(kpiid);
	string pluginid = ChangePluginid(coltype);

	VEC_STRING_DATAS tmpvec;
	if(kpiid == "Availability")
	{
		ret = TRUE;
		tmpvec.clear();
		tmpvec.push_back(metricid);
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
		tmpvec.push_back(metricid);
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
		tmpvec.push_back(metricid);
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
		tmpvec.push_back(metricid);
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

	//if(kpiid == "H1usedPSSize")
	//{
	//	ret = TRUE;
	//	tmpvec.clear();
	//	tmpvec.push_back(metricid);
	//	tmpvec.push_back(pluginid);
	//	tmpvec.push_back(coltype);
	//	tmpvec.push_back("PluginParameter");		
	//	tmpvec.push_back("ArrayType");	//���type		
	//	tmpvec.push_back("");	//���classKey		
	//	tmpvec.push_back("");	//typeֵ		
	//	tmpvec.push_back("COMMAND");	//keyֵ
	//	tmpvec.push_back("lsps -s");	//valueֵ
	//	tmpvec.push_back("pagingSpaceSize,pagingTotal");	//columnֵ		
	//	//дһ�����������ݵ�Excel�ڴ�������
	//	MetricPluginDatas_.push_back(tmpvec);

	//	tmpvec.clear();
	//	tmpvec.push_back(metricid);
	//	tmpvec.push_back(pluginid);
	//	tmpvec.push_back(coltype);
	//	tmpvec.push_back("PluginDataHandler");		
	//	tmpvec.push_back("");	//���type		
	//	tmpvec.push_back("RegularFilter");	//���classKey		
	//	tmpvec.push_back("");	//typeֵ		
	//	tmpvec.push_back("REGULAR");	//keyֵ
	//	tmpvec.push_back("(\\d+)MB\\s+(\\d+)%");	//valueֵ
	//	tmpvec.push_back("pagingSpaceSize,pagingTotal");	//columnֵ		
	//	//дһ�����������ݵ�Excel�ڴ�������
	//	MetricPluginDatas_.push_back(tmpvec);

	//	tmpvec.clear();
	//	tmpvec.push_back(metricid);
	//	tmpvec.push_back(pluginid);
	//	tmpvec.push_back(coltype);
	//	tmpvec.push_back("PluginDataHandler");		
	//	tmpvec.push_back("");	//���type		
	//	tmpvec.push_back("selectProcessor");	//���classKey		
	//	tmpvec.push_back("");	//typeֵ		
	//	tmpvec.push_back("SELECT");	//keyֵ
	//	tmpvec.push_back("SELECT (pagingSpaceSize*pagingTotal/100) as usedPSSize");	//valueֵ
	//	tmpvec.push_back("pagingSpaceSize,pagingTotal");	//columnֵ		
	//	//дһ�����������ݵ�Excel�ڴ�������
	//	MetricPluginDatas_.push_back(tmpvec);
	//}

	return ret;
}

BOOL HostXmlToXml::ChangeCommand(const string tempCollectType,const string childrestype, string &command, string &childidvalue, string &columnvalue)
{
	BOOL ret = FALSE;
	int npos = -1;
	string oldstr = "$childid";
	string newstr = "${childid}";
	npos = command.find(oldstr);
	if (npos >= 0)
	{
		ret = TRUE;
		command = replace_all_distinct(command, oldstr, newstr);

		childidvalue = mapKPIchildDatas_[childrestype];
		if(childidvalue.empty())
		{
			loglog.str("");
			loglog << "error: childrestype��[" << childrestype << "]������." << flush;
			MYLOG(loglog.str().c_str());
		}
		childidvalue = ChangeKpiid(childidvalue);

		//���childid���ڣ���ôvalue�Ͳ�����columnvalue�д���
		string tempcolumn = "," + columnvalue + ",";
		string tempfind = "," + childidvalue + ",";
		int npostemp = tempcolumn.find(tempfind);
		if(npostemp >= 0)
		{
			tempcolumn = replace_all_distinct(tempcolumn, tempfind.substr(1, tempfind.length() - 1), "");	//�� ��childidvalue,�� ȥ��
			columnvalue = tempcolumn.substr(1, tempcolumn.length() - 2);		//ȥ��ǰ��Ķ���
		}

	}

	//����Դ�� �����������������${Index}��ֱ��ȥ��
	string oldstr2 = "${Index}";
	npos = command.find(oldstr2);
	if (npos >= 0)
	{
		command = replace_all_distinct(command, oldstr2, "");
	}


	if((tempCollectType == "SSH") || (tempCollectType == "TELNET"))
	{
		string oldcmd1 = "/tmp/os.sh";
		string oldcmd2 = "/tmp/appinfo.sh";
		string newcmd1 = "/tmp/hostcommon/os.sh";
		string newcmd2 = "/tmp/hostcommon/appinfo.sh";
		npos = command.find(oldcmd1);
		if (npos >= 0)
		{
			command = replace_all_distinct(command, oldcmd1, newcmd1);
		}
		npos = command.find(oldcmd2);
		if (npos >= 0)
		{
			command = replace_all_distinct(command, oldcmd2, newcmd2);
		}
	}

	return ret;
}


BOOL HostXmlToXml::ReplaceSpecial(string &strvalue, string childrestype)
{
	BOOL ret = TRUE;
	//int  keycount = 0;
	int npos = -1;
	string findstr1 = "($key)";
	string findstr2 = "$key";
	string newstr1("");
	string newstr2 = "(${childid})";
	if(childrestype == "cpu")
	{
		newstr1 = "(\\d+)";
	}
	else
	{
		newstr1 = "(\\S+)";
	}
	
	npos = strvalue.find(findstr1);
	if(npos >= 0)
	{
		//keycount++;
		int temppos = strvalue.find("(");
		if(temppos == npos)
		{
			temppos = strvalue.find("(", temppos + 1);
		}

		if((temppos < npos) && (temppos >= 0))
		{
			ret = FALSE;
		}

		strvalue = replace_all_distinct(strvalue, findstr1, newstr1);
	}


	npos = strvalue.find(findstr2);
	if(npos >= 0)
	{
		//keycount++;
		int temppos = strvalue.find("(");
		if((temppos < npos) && (temppos >= 0))
		{
			ret = FALSE;
		}

		strvalue = replace_all_distinct(strvalue, findstr2, newstr2);
	}

	return ret;
}


string HostXmlToXml::WMIChangeSize(string columnvalue, int flag)
{
	string retstr("");
	if(columnvalue.empty())
	{
		return retstr;
	}

	switch(flag)
	{
	case 1:
		retstr = columnvalue.substr(0, columnvalue.length() - 1);
		break;
	case 2:
		retstr = columnvalue;
		break;
	default:
		break;
	}
	
	retstr = "," + str_trim(retstr) + ",";
	string findstr = ",size,";
	string newstr = ",size1,";
	int npos = retstr.find(findstr);
	if(npos >= 0)
	{
		retstr = replace_all_distinct(retstr, findstr, newstr);
	}

	//ȥ��ǰ�󶺺�
	retstr = retstr.substr(1, retstr.length() - 2);

	return retstr;
}

void HostXmlToXml::WMIMetricPluginData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata)
{
	VEC_STRING_DATAS metricplugindata;
	VEC_STRING_DATAS veccommand;
	VEC_STRING_DATAS kpiinfo;
	VEC_STRING_DATAS vecarraykey;
	VEC_STRING_DATAS vecarrayvalue;
	VEC_STRING_DATAS vecdealdata;
	BOOL isConverterIndex = FALSE;
	BOOL isConverterValue = FALSE;
	string tempnullstr("");
	string columnvalue("");
	string strselect("");
	string childindex("");
	string childrestype("");
	string convertervalue("");
	string converterindex("");
	string strmerge("");
	string tmpkpiid = resourceattr[0];
	string resourcevalue = resourceattr[2];
	//{"metricid", "pluginid", "collectType", "�ɼ�����", "���type", "���classKey", "typeֵ", "keyֵ", "valueֵ", "columnֵ"};

	metricplugindata.clear();
	veccommand.clear();
	kpiinfo.clear();
	vecarraykey.clear();
	vecarrayvalue.clear();
	vecdealdata.clear();

	//�ж�ָ��id�Ƿ���ȷ
	kpiinfo = mapkpidatas_[tmpkpiid];		
	if(kpiinfo.size() < KPI_INFO_COUNT)
	{
		loglog.str("");
		loglog << "warn : kpiid[" << tmpkpiid << "] is not in kpi_info.xml." << flush;
		MYLOG(loglog.str().c_str());
		return ;
	}
	
	childrestype = kpiinfo[7];
	if(!childrestype.empty())
	{
		childindex = ChangeKpiid(mapKPIchildDatas_[childrestype]);
	}

	if(collectattr.empty())
	{
		return ;
	}

	VEC_VEC_DATAS::iterator itcmdid = collectattr.begin();	
	strmerge = itcmdid->at(3);
	for( ; itcmdid != collectattr.end(); ++itcmdid )
	{
		VEC_STRING_DATAS veccmd;
		veccmd.clear();
		metricplugindata.clear();
		int vecsize = 0;
		string cmdvalues("");
		string tmpcmdid = itcmdid->at(1);

		VEC_STRING_DATAS veccmdid;
		veccmdid.clear();
		veccmd = kpimapcmd[tmpcmdid];
		vecsize = (int)veccmd.size();
		if(vecsize >= 3)
		{
			//û��������
			cmdvalues = veccmd[2];

			string findstr1 = "select ";
			string findstr2 = " from ";
			string findstr3 = "${Index}";

			int nposbegin = cmdvalues.find(findstr1);
			int nposend = cmdvalues.find(findstr2);
			if((nposbegin >= 0) && (nposend >= 0) && (nposend >= nposbegin))
			{
				columnvalue = columnvalue + cmdvalues.substr(nposbegin + findstr1.length(), nposend - nposbegin - findstr1.length()) + ",";
			}


			//Ŀǰֻ����һ��${Index}
			int npos = columnvalue.find(findstr3);
			if(npos >= 0)
			{
				string temparrayvalue("");
				int npostemp = columnvalue.find(",", npos);
				int findstr3leng = findstr3.length();
				if(npostemp >= 0)
				{
					temparrayvalue = columnvalue.substr(npos + findstr3leng, npostemp - npos - findstr3leng);
				}
				else
				{
					temparrayvalue = cmdvalues.substr(nposbegin, cmdvalues.length() - nposbegin);
				}

				temparrayvalue = str_trim(temparrayvalue);
				vecarrayvalue.push_back(temparrayvalue);

				cmdvalues = replace_all_distinct(cmdvalues, findstr3, "");
				columnvalue = replace_all_distinct(columnvalue, findstr3, "");
			}

			veccommand.push_back(cmdvalues);
		}
		else
		{
			//cmdid ����ȷ��ֱ�ӷ�������
			loglog.str("");
			loglog << "warn : cmdid[" << tmpcmdid << "] is error." << flush;
			MYLOG(loglog.str().c_str());
			break;
		}

		//�ж�3.5��resource->collects->collect->select���Ƿ���ֵ
		//WMI ����Դ�� select ֵ�����һ��������Ĳ�����
		string tmpselect = itcmdid->at(2);
		if(!tmpselect.empty())
		{
			strselect = strselect + tmpselect + ",";
		}

		//�ж�3.5��resource->collects->collect->deal���Ƿ���ֵ������У���Ҫ����ע��
		string tmpdeal = itcmdid->at(4);
		if(!tmpdeal.empty())
		{
			vecdealdata.push_back(tmpdeal);
		}

	}

	columnvalue = WMIChangeSize(columnvalue, 1);
	////ȥ������һ������
	//if(!columnvalue.empty())
	//{
	//	columnvalue = columnvalue.substr(0, columnvalue.length() - 1);

	//}

	VEC_STRING_DATAS vecselect;
	vecselect.clear();
	string oldstr("");
	string newstr("");
	
	if(!strselect.empty())
	{
		strselect = strselect.substr(0, strselect.length() - 1);
		//strselect = WMIChangeSize(strselect);
		vecselect = split_str_bychar(strselect, ',');
		if(!vecselect.empty())
		{
			VEC_STRING_DATAS::iterator it = vecselect.begin();
			for(int i = 0; it != vecselect.end(); ++it, i++)
			{
				string tempvalue = *it;
				int npos1 = tempvalue.find("(");
				int npos2 = tempvalue.find(")");
				if((npos2 > npos1) && (npos1 >= 0) && (npos2 >= 0))
				{
					isConverterValue = TRUE;
					convertervalue = tempvalue.substr(npos1 + 1, npos2 - npos1 - 1);
					int npostemp = tempvalue.find(" as ");
					if(npostemp >= 0)
					{
						string tempvecvalue = tempvalue.substr(0, npostemp);	//Ϊ�����newstr��׼��
						vecselect[i] = WMIChangeSize(tempvecvalue, 2);
					}
					else
					{
						vecselect[i] = WMIChangeSize(convertervalue, 2);	//Ϊ�����newstr��׼��
					}
					
					oldstr = tempvalue;
				}

				npos1 = tempvalue.find("[");
				npos2 = tempvalue.find("]");
				if((npos2 > npos1) && (npos1 >= 0) && (npos2 >= 0))
				{
					isConverterIndex = TRUE;
					converterindex = tempvalue.substr(npos1 + 1, npos2 - npos1 - 1);
					vecselect[i] = WMIChangeSize(converterindex, 2);  	//Ϊ�����newstr��׼��
				}				
			}
		}		
	}

	if(!resourcevalue.empty())
	{
		SET_INT_DATAS setparam;
		setparam.clear();
		GetParam(resourcevalue, setparam);

		if(!setparam.empty())
		{
			int selectsize = vecselect.size();
			newstr = resourcevalue;
			SET_INT_DATAS::iterator it2 = setparam.begin();
			for( ; it2 != setparam.end(); ++it2 )
			{
				int number = *it2;
				stringstream oldvalue("");
				string newvalue("");
				oldvalue << "$d" << number << flush;
				if(number <= selectsize)
				{
					newvalue = vecselect[number-1];
					newstr = replace_all_distinct(newstr, oldvalue.str(), newvalue);
				}
				else
				{
					loglog.str("");
					loglog << "warn : mertricid[" << resourceattr[0] << "] is count error." << flush;
					MYLOG(loglog.str().c_str());
				}
			}
			newstr = "(" + newstr + ") as " + convertervalue;
		}

	}

	if((!oldstr.empty()) && (!newstr.empty()))
	{
		strselect = replace_all_distinct(strselect, oldstr, newstr);
	}
	else
	{
		strselect = replace_all_distinct(strselect, "(", "");
		strselect = replace_all_distinct(strselect, ")", "");
	}

	strselect = replace_all_distinct(strselect, "[", "");
	strselect = replace_all_distinct(strselect, "]", "");

	if(!strmerge.empty())
	{
		int npos = strselect.find(",");
		if(npos >= 0)
		{
			string tempmerge = strselect.substr(0, npos);
			tempmerge = strmerge + "(" + tempmerge + ")";
			strselect = tempmerge + strselect.substr(npos, strselect.length() - npos);
		}
		else
		{
			strselect = strmerge + "(" + strselect + ")";
		}
	}

	strselect = "SELECT " + strselect;

	string tempMetricid = ChangeKpiid(resourceattr[0]);
	string tempPluginid = ChangePluginid(resourceattr[1]);
	string tempCollectType = resourceattr[1];
	if(tempCollectType == "SNMP")
	{
		tempCollectType = "";
	}

	if(!vecarrayvalue.empty())
	{
		VEC_STRING_DATAS::iterator it = vecarrayvalue.begin();
		for( ; it != vecarrayvalue.end(); ++it)
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ			
			metricplugindata.push_back("INDEX");			//keyֵ				
			metricplugindata.push_back(*it);			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
		}
	}

	if(!veccommand.empty())
	{
		VEC_STRING_DATAS::iterator it = veccommand.begin();
		for( ; it != veccommand.end(); ++it)
		{
			string tempcmdvalue = *it;
			tempcmdvalue = str_trim(tempcmdvalue);
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ			
			metricplugindata.push_back("COMMAND");			//keyֵ				
			metricplugindata.push_back(tempcmdvalue);			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
		}
	}

	metricplugindata.clear();
	metricplugindata.push_back(tempMetricid);
	metricplugindata.push_back(tempPluginid);
	metricplugindata.push_back(tempCollectType);
	metricplugindata.push_back("PluginDataHandler");		
	metricplugindata.push_back(tempnullstr);	//���type		
	metricplugindata.push_back("selectProcessor");	//���classKey		
	metricplugindata.push_back(tempnullstr);	//typeֵ		
	metricplugindata.push_back("SELECT");	//keyֵ
	metricplugindata.push_back(strselect);			//valueֵ			
	metricplugindata.push_back(columnvalue);		//columnֵ			
	//дһ�����������ݵ�Excel�ڴ�������
	MetricPluginDatas_.push_back(metricplugindata);


	if(!vecdealdata.empty())
	{
		VEC_STRING_DATAS::iterator it = vecdealdata.begin();
		string tempdeal = *it;
		VEC_STRING_DATAS tmpvec;
		tmpvec.clear();
		//����MetricPluginData

		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempCollectType);
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
		tmpvec.push_back(tempCollectType);
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

	if(!translationsdata.empty())
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginDataHandler");			
		metricplugindata.push_back(tempnullstr);		//���type			
		metricplugindata.push_back("translateProcessor");	//���classKey			
		metricplugindata.push_back(tempnullstr);		//typeֵ
		metricplugindata.push_back(convertervalue);	//keyֵ
		metricplugindata.push_back(translationsdata);			//valueֵ
		metricplugindata.push_back(columnvalue);		//columnֵ			
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);		
	}

	if((childrestype == "hostProcess") && isConverterValue)
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginDataConverter");		
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("processConverter");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
		metricplugindata.push_back(convertervalue);	//valueֵ
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);

		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginDataConverter");		
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("processConverter");	//���classKey		
		metricplugindata.push_back("ResourceProperty");	//typeֵ		
		metricplugindata.push_back("InstPropertyKey");	//keyֵ		
		metricplugindata.push_back(childindex);	//valueֵ	
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
	}

	if(childrestype != "hostProcess")
	{
		if(isConverterIndex && isConverterValue)
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("IndexColumnTitle");	//keyֵ		
			metricplugindata.push_back(converterindex);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);

			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
			metricplugindata.push_back(convertervalue);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);

			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back("ResourceProperty");	//typeֵ		
			metricplugindata.push_back("InstPropertyKey");	//keyֵ		
			metricplugindata.push_back(childindex);	//valueֵ	
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);
		}
		else if(isConverterIndex)
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("IndexColumnTitle");	//keyֵ		
			metricplugindata.push_back(converterindex);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);

			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
			metricplugindata.push_back(converterindex);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);
		}
		else if(isConverterValue)
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("IndexColumnTitle");	//keyֵ		
			metricplugindata.push_back(convertervalue);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);

			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
			metricplugindata.push_back(convertervalue);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);
		}
		
	}
}

//string HostXmlToXml::ParseValue(const string strvalue, const string newvalue1, string &columnvalue)
string HostXmlToXml::ParseValue(const string strvalue, const string newvalue1, const string newvalue2)
{
	string retstr = strvalue;
	string oldvalue1 = "$d1";
	string oldvalue2 = "$d2";

	//SET_INT_DATAS setparam;
	//setparam.clear();
	//GetParam(strvalue, setparam);

	////���������ʵ�ʵ�collectҪ��
	//if(setparam.size() > 1)
	//{
	//	VEC_STRING_DATAS tempvec;
	//	tempvec.clear();
	//	tempvec = split_str_bychar(columnvalue, ',');
	//	
	//	columnvalue.clear();
	//	SET_INT_DATAS::iterator it = setparam.begin();
	//	for( ; it != setparam.end(); ++it )
	//	{
	//		stringstream oldvalue("");
	//		stringstream newvalue("");
	//		oldvalue << "$d" << *it << flush;
	//		newvalue << "column" << *it << flush;

	//		retstr = replace_all_distinct(retstr, oldvalue.str(), newvalue.str());
	//		columnvalue = columnvalue + newvalue.str() + ",";
	//	}

	//	if(tempvec[0] == newvalue1)
	//	{
	//		columnvalue = newvalue1 + "," + columnvalue.substr(0, columnvalue.length() - 1);
	//	}
	//	else
	//	{
	//		columnvalue = columnvalue + newvalue1;
	//	}
	//	

	//}
	//else
	//{
		retstr = replace_all_distinct(retstr, oldvalue1, newvalue1);
		retstr = replace_all_distinct(retstr, oldvalue2, newvalue2);
	//}

	return retstr;

}

void HostXmlToXml::ParseHostMerge(string &resourcevalue, VEC_VEC_DATAS &collectattr)
{
	if(resourcevalue.empty())
	{
		//���resourcevalueΪ�գ�ֻ������һ�е� merge ������ ֵ ���� value 
		if(!collectattr.empty())
		{
			VEC_VEC_DATAS::iterator it = collectattr.begin();
			string tempmerge = it->at(3);
			if (!tempmerge.empty())
			{
				resourcevalue = tempmerge + "($d1)";
			}
		}		
	}
	else
	{
		//�� $di ȫ�� �滻  merge($di)
		VEC_VEC_DATAS::iterator it = collectattr.begin();
		for(int i = 1 ; it != collectattr.end(); ++it)
		{
			//i �Ǹ��� tempselect�е����Ž��м����
			string tempmerge = it->at(3);
			string tempselect = it->at(2);
			int npos = tempselect.find("(");
			if((tempselect.empty()) && (!tempmerge.empty()))
			{
				stringstream oldvalue("");
				oldvalue << "$d" << i << flush;
				string newvalue = tempmerge + "(" + oldvalue.str() + ")";
				resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue);
				i++;
			}
			else
			{
				while(npos >= 0)
				{
					int nposbegin = npos;
					if(!tempmerge.empty())
					{				
						stringstream oldvalue("");
						oldvalue << "$d" << i << flush;
						string newvalue = tempmerge + "(" + oldvalue.str() + ")";
						resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue);
					}
					i++;
					npos = tempselect.find("(", nposbegin + 1);
				}
			}
		}
	}
}

void HostXmlToXml::PrepareMetricPluginAllData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata)
{
	VEC_STRING_DATAS metricplugindata;	
	string tempnullstr("");
	BOOL isSELECT = FALSE;
	BOOL isREGULAR = FALSE;
	BOOL isChild = FALSE;
	BOOL isChildConverter = FALSE;
	BOOL isMainConverter = FALSE;
	BOOL isCommandChildid = FALSE;
	BOOL childindexisfirst = TRUE;
	BOOL isSpecial = FALSE;
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
	string childindex("");
	string childrestype("");
	string reslevel("");
	string ischildresid("");
	string ischildresname("");
	string childidvalue("");
	VEC_STRING_DATAS kpiinfo;
	kpiinfo.clear();

	//�ж��Ƿ��Ѿ����ڣ�������ڣ�ֱ�ӷ���
	if(IsRepeatMetricid(resourceattr[0], resourceattr[1], 1))
	{
		loglog.str("");
		loglog << "warn : resourceattr[0]=[" << resourceattr[0] << "],resourceattr[1]=" << resourceattr[1] << "] is already exist(MetricPluginAllData).";
		MYLOG(loglog.str().c_str());
		return;
	}

	if(SpecialHandling(resourceattr))
	{
		return ;
	}

	{
		//�ж�ָ��id�Ƿ���ȷ
		kpiinfo = mapkpidatas_[tmpkpiid];		
		if(kpiinfo.size() < KPI_INFO_COUNT)
		{
			loglog.str("");
			loglog << "warn : kpiid[" << tmpkpiid << "] is not in kpi_info.xml." << flush;
			MYLOG(loglog.str().c_str());
			return ;
		}

		kpitype = kpiinfo[4];
		reslevel = kpiinfo[6];
		childrestype = kpiinfo[7];
		ischildresid = LetterTolower(kpiinfo[8]);
		ischildresname = LetterTolower(kpiinfo[9]);

		if(!childrestype.empty())
		{
			childindex = ChangeKpiid(mapKPIchildDatas_[childrestype]);
		}
		
	}
	string tempMetricid = ChangeKpiid(resourceattr[0]);
	string tempPluginid = ChangePluginid(resourceattr[1]);
	string tempCollectType = resourceattr[1];
	if(tempCollectType == "SNMP")
	{
		tempCollectType = "";
	}

	//�����WMI������Դ����Ϊ���⴦��
	if((tempCollectType == "WMI") && (reslevel == "child"))
	{
		WMIMetricPluginData(kpimapcmd, resourceattr, collectattr, translationsdata);
		return ;
	}
	

	//���� merge �ֶ�
	ParseHostMerge(resourcevalue, collectattr);
	//ParseMerge(resourcevalue, collectattr);

	isSELECT = GetColumnData(tmpkpiid, resourceattr[1], resourcevalue, columnvalue, kpimapcmd, collectattr, childrestype, isSpecial);

	BOOL isContinue = FALSE;
	//string iswalk("");
	if(!collectattr.empty())
	{
		isContinue = TRUE;
		VEC_VEC_DATAS::iterator itcmdid = collectattr.begin();
		for( ; itcmdid != collectattr.end(); ++itcmdid )
		{
			VEC_STRING_DATAS veccmd;
			veccmd.clear();
			metricplugindata.clear();
			int vecsize = 0;
			string cmdvalues("");
			string tmpcmdid = itcmdid->at(1);

			//�ж�3.5��resource->collects->collect->select���Ƿ���ֵ������У���Ҫ������
			string tmpselect = itcmdid->at(2);
			if(!tmpselect.empty())
			{
				isREGULAR = TRUE;
				childindexisfirst = ReplaceSpecial(tmpselect, childrestype);
				vecregulardata.push_back(tmpselect);
			}

			//�ж�3.5��resource->collects->collect->deal���Ƿ���ֵ������У���Ҫ����ע��
			string tmpdeal = itcmdid->at(4);
			if(!tmpdeal.empty())
			{
				vecdealdata.push_back(tmpdeal);
			}

			VEC_STRING_DATAS veccmdid;
			veccmdid.clear();
			veccmd = kpimapcmd[tmpcmdid];
			vecsize = (int)veccmd.size();
			if( vecsize > 3)
			{
				//˵����cmd�Ǿ���column��
				isMainConverter = TRUE;
				cmdvalues = veccmd[2];
			}
			else if(vecsize == 3)
			{
				//û��������
				cmdvalues = veccmd[2];
			}
			else
			{
				//cmdid ����ȷ��ֱ�ӷ�������
				loglog.str("");
				loglog << "warn : cmdid[" << tmpcmdid << "] is error." << flush;
				MYLOG(loglog.str().c_str());
				break;
			}				

			if(reslevel == "main")
			{
				//����Դ  ʲô������
			}
			else if(reslevel == "child")
			{
				//if( ((ischildresid == "N") || (ischildresid == "n"))
				//	&& ((ischildresname == "N") || (ischildresname == "n")) )
				//{
					isChildConverter = TRUE;
				//}

				isChild = TRUE;
				if(!columnvalue.empty())
				{
					tempcolumnvalue = columnvalue;
					if( (ischildresid == "y") && (childrestype != "hostProcess") )
					{
						columnvalue = tempMetricid;
					}
					else if( (isChildConverter) 
						&& ( (childrestype != "hostProcess") || ((childrestype == "hostProcess") && (tempCollectType == "WMI"))))
					{
						if(childindexisfirst)
						{
							//if( ( (isSELECT) || (isLinux_ && isChild && (childrestype == "cpu") && (tempMetricid != "cpuId")) )
							if( ( (isSELECT) || (isChild && (childrestype == "cpu") && (tempMetricid != "cpuId")) )
								&& (!isSpecial) )
							{
								columnvalue = childindex + "," + tempMetricid;
								//resourcevalue = ParseValue(resourceattr[2], childindex, tempMetricid);
								//����$key���㣬����$d1��tempMetricid
								resourcevalue = ParseValue(resourceattr[2], tempMetricid, childindex);
							}
							else
							{
								columnvalue = childindex + "," + columnvalue;
							}								
						}
						else
						{
							//if( ( (isSELECT) || (isLinux_ && isChild && (childrestype == "cpu") && (tempMetricid != "cpuId")) )
							if( ( (isSELECT) || (isChild && (childrestype == "cpu") && (tempMetricid != "cpuId")) )
								&& (!isSpecial) )
							{
								columnvalue = tempMetricid + "," + childindex;
								resourcevalue = ParseValue(resourceattr[2], tempMetricid, childindex);
							}
							else
							{
								columnvalue = columnvalue + "," + childindex;
							}							
						}											
					}					
				}
			}
			else
			{
				//ָ�����ͳ���
				loglog.str("");
				loglog << "warn : cmdid[" << tmpcmdid << "] , reslevel[" << reslevel << "]is error." << flush;
				MYLOG(loglog.str().c_str());
				break;
			}

			if((tempMetricid == "cpuId") || (tempMetricid == "cpuName"))
			{
				columnvalue = "cpuId";
			}

			if((issolaris_) && (tempMetricid == "cpuMulCpuRate")) 
			{
				columnvalue = "cpuMulCpuRate";
			}
			

			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ			
			metricplugindata.push_back("COMMAND");			//keyֵ	

			BOOL childid = ChangeCommand(tempCollectType, childrestype, cmdvalues, childidvalue, columnvalue);
			if(childid)
			{
				isCommandChildid = TRUE;
			}

			cmdvalues = str_trim(cmdvalues);
			metricplugindata.push_back(cmdvalues);			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

		}
	}

	if((isCommandChildid) && (!childidvalue.empty()))
	{
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginParameter");			
		metricplugindata.push_back("ArrayType");		//���type			
		metricplugindata.push_back(tempnullstr);	//���classKey			
		metricplugindata.push_back("ResourceProperty");		//typeֵ			
		metricplugindata.push_back("childid");			//keyֵ			
		metricplugindata.push_back(childidvalue);			//valueֵ			
		metricplugindata.push_back(columnvalue);		//columnֵ			
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
		metricplugindata.clear();

		//���PropertiesData���Ƿ��д�����
		CheckPropertiesData(childidvalue, isChild, childrestype);

		//�����childid�Ͳ���Ҫתconverter��
		isChildConverter = FALSE;
	
	}

	if(isREGULAR)
	{
		BOOL tempischildid = TRUE;
		string oldstr = "(${childid})";
		string newstr("");
		if(childrestype == "cpu")
		{
			newstr = "(\\d+)";
		}
		else
		{
			newstr = "(\\S+)";
		}
		VEC_STRING_DATAS::iterator itregular = vecregulardata.begin();
		for( ; itregular != vecregulardata.end(); ++itregular )
		{
			string tempvalue = *itregular;
			tempvalue = str_trim(tempvalue);
			int npos = tempvalue.find(oldstr);

			if(npos >= 0)
			{
				if(	(ischildresid == "y") 
					|| (ischildresname == "y")
					|| (childrestype == "hostProcess")
					|| (childrestype == "hostInterface") )
				{
					//�� (${childid}) �滻Ϊ (\S+)����(\d+)
					tempvalue = replace_all_distinct(tempvalue, oldstr, newstr);
				}
				else
				{
					if(tempischildid)
					{
						metricplugindata.clear();
						metricplugindata.push_back(tempMetricid);
						metricplugindata.push_back(tempPluginid);
						metricplugindata.push_back(tempCollectType);
						metricplugindata.push_back("PluginDataHandler");			
						metricplugindata.push_back(tempnullstr);		//���type			
						metricplugindata.push_back("RegularFilter");	//���classKey			
						metricplugindata.push_back("ResourceProperty");		//typeֵ			
						metricplugindata.push_back("childid");			//keyֵ			
						metricplugindata.push_back(childindex);			//valueֵ			
						metricplugindata.push_back(columnvalue);		//columnֵ			
						//дһ�����������ݵ�Excel�ڴ�������
						MetricPluginDatas_.push_back(metricplugindata);

						//ֻ����һ�� childid
						tempischildid = FALSE;
					}
				}
			}
			
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataHandler");			
			metricplugindata.push_back(tempnullstr);		//���type			
			metricplugindata.push_back("RegularFilter");	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ			
			metricplugindata.push_back("REGULAR");			//keyֵ			
			metricplugindata.push_back(tempvalue);			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();
		}
	}

	//BOOL cpuSelect = FALSE;
	if(isSELECT)
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginDataHandler");		
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("selectProcessor");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back("SELECT");	//keyֵ

		//valueֵ
		string tmptype("");

		//if(isChild && (childrestype == "cpu") && (tempMetricid != "cpuId") )
		if(isChild && (childrestype == "cpu") && (tempMetricid == "cpuName"))
		{
			//ֻ��cpuName�Ż�����Ҫ���⴦��
			//if(tempMetricid == "cpuName")
			//{
				tmptype = "SELECT ('CPU' + cpuId) as cpuName, cpuId";
				metricplugindata.push_back(tmptype);		//valueֵ		
				metricplugindata.push_back(columnvalue);	//columnֵ		
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);
				metricplugindata.clear();
			//}
		}
		else
		{
			tmptype = "SELECT (" + resourcevalue + ") as " + tempMetricid;
			if(isChild && isChildConverter && (!columnvalue.empty()))
			{
				if((issolaris_) && (tempMetricid == "cpuMulCpuRate"))
				{
					//��������ʹ��ԭʼ��
				}
				else
				{
					tmptype = tmptype + "," + childindex;
				}				
			}
			if(tempMetricid == "processAvail")
			{
				tmptype = "SELECT pid,(avail != null?'1':'0') as avail,command";
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
						CheckPropertiesData(tempvalue, isChild, childrestype);

						//����MetricPluginData
						tmpvec.clear();
						tmpvec.push_back(tempMetricid);
						tmpvec.push_back(tempPluginid);
						tmpvec.push_back(tempCollectType);
						tmpvec.push_back("PluginDataHandler");		
						tmpvec.push_back(tempnullstr);	//���type		
						tmpvec.push_back("selectProcessor");	//���classKey		
						tmpvec.push_back("ResourceProperty");	//typeֵ		
						//tmpvec.push_back(tempkey);	//keyֵ
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

		metricplugindata.clear();	
		
	}
	else
	{
		if(isChild && (childrestype == "cpu") )
		{
			//����MetricPluginData
			VEC_STRING_DATAS tmpvec;
			tmpvec.clear();
			tmpvec.push_back(tempMetricid);
			tmpvec.push_back(tempPluginid);
			tmpvec.push_back(tempCollectType);
			tmpvec.push_back("PluginDataHandler");		
			tmpvec.push_back(tempnullstr);	//���type		
			tmpvec.push_back("selectProcessor");	//���classKey		
			tmpvec.push_back("");	//typeֵ		
			tmpvec.push_back("SELECT");	//keyֵ
			//ֻ��cpuName�Ż���selectProcessor
			if(tempMetricid == "cpuName")
			{
				tmpvec.push_back("SELECT ('CPU' + cpuId) as cpuName, cpuId");	//valueֵ
				tmpvec.push_back(columnvalue);	//columnֵ		
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(tmpvec);				
			}
			metricplugindata.clear();
			
		}
	}	

	if(tempMetricid == "ifStatus")
	{
		VEC_STRING_DATAS tmpvec;
		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempCollectType);
		tmpvec.push_back("PluginDataHandler");		
		tmpvec.push_back(tempnullstr);	//���type		
		tmpvec.push_back("selectProcessor");	//���classKey		
		tmpvec.push_back("");	//typeֵ		
		tmpvec.push_back("SELECT");	//keyֵ
		tmpvec.push_back("SELECT ifName,(ifInfo=='UP'?'1':'0') as ifInfo");	//valueֵ
		tmpvec.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);	
	}


	if(!vecdealdata.empty())
	{
		VEC_STRING_DATAS::iterator it = vecdealdata.begin();
		string tempdeal = *it;
		VEC_STRING_DATAS tmpvec;
		
		//����MetricPluginData
		if(reslevel != "main")
		{
			tmpvec.clear();
			tmpvec.push_back(tempMetricid);
			tmpvec.push_back(tempPluginid);
			tmpvec.push_back(tempCollectType);
			tmpvec.push_back("PluginDataHandler");		
			tmpvec.push_back("");	//���type		
			tmpvec.push_back("ChangeRateProcessor");	//���classKey		
			tmpvec.push_back("");	//typeֵ
			tmpvec.push_back("COLUMNINDEX");	//keyֵ
			tmpvec.push_back(childindex);	//valueֵ
			tmpvec.push_back(columnvalue);	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(tmpvec);
		}

		tmpvec.clear();
		tmpvec.push_back(tempMetricid);
		tmpvec.push_back(tempPluginid);
		tmpvec.push_back(tempCollectType);
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


	string tempporcess("");
	if((childrestype == "hostProcess") && (tempCollectType != "WMI") && isContinue)
	{
		int nposbegin = resourcevalue.find("(");
		int nposend = resourcevalue.find(")");
		if((nposbegin >= 0) && (nposend >= 0) && (nposend > nposbegin))
		{
			tempporcess = resourcevalue.substr(nposbegin + 1, nposend - nposbegin - 1);
		}
	}


	if((!translationsdata.empty()) && (kpitype != "avail"))
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
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
			if((childrestype == "hostProcess") && (tempCollectType != "WMI"))
			{
				metricplugindata.push_back(tempporcess);	//keyֵ
			}
			else
			{
				if(tempMetricid == "netstat")
				{
					metricplugindata.push_back("state");	//keyֵ
				}
				else
				{
					metricplugindata.push_back(tempcolumnvalue);	//keyֵ
				}
			}			
		}

		//translationsdata = str_trim(translationsdata);
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

	if((issolaris_) && (tempMetricid == "netstat"))
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginDataHandler");		
		metricplugindata.push_back(tempnullstr);	//���type		
		metricplugindata.push_back("SolarisNetStatProcessor");	//���classKey		
		metricplugindata.push_back(tempnullstr);	//typeֵ		
		metricplugindata.push_back(tempnullstr);	//keyֵ		
		metricplugindata.push_back(tempnullstr);	//valueֵ		
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
		metricplugindata.clear();
	}

	if(isMainConverter && (!isChild))
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
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

	if((childrestype == "hostProcess") && (tempCollectType != "WMI") && isContinue)
	{

		if(!tempporcess.empty())
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("processConverter");	//���classKey		
			metricplugindata.push_back(tempnullstr);	//typeֵ		
			metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
			metricplugindata.push_back(tempporcess);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back(tempnullstr);	//���type		
			metricplugindata.push_back("processConverter");	//���classKey		
			metricplugindata.push_back("ResourceProperty");	//typeֵ		
			metricplugindata.push_back("InstPropertyKey");	//keyֵ		
			metricplugindata.push_back("instanceIdKeyValues");	//valueֵ	
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();
		}
		
	}
	else
	{
		if(isChildConverter)
		{
			if((issolaris_) && (tempMetricid == "cpuMulCpuRate"))
			{
				//������ת����
			}
			else
			{
				metricplugindata.clear();
				metricplugindata.push_back(tempMetricid);
				metricplugindata.push_back(tempPluginid);
				metricplugindata.push_back(tempCollectType);
				metricplugindata.push_back("PluginDataConverter");		
				metricplugindata.push_back(tempnullstr);	//���type		
				metricplugindata.push_back("subInstConverter");	//���classKey		
				metricplugindata.push_back(tempnullstr);	//typeֵ		
				metricplugindata.push_back("IndexColumnTitle");	//keyֵ		
				metricplugindata.push_back(childindex);	//valueֵ
				metricplugindata.push_back(columnvalue);	//columnֵ		
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);

				metricplugindata.clear();
				metricplugindata.push_back(tempMetricid);
				metricplugindata.push_back(tempPluginid);
				metricplugindata.push_back(tempCollectType);
				metricplugindata.push_back("PluginDataConverter");		
				metricplugindata.push_back(tempnullstr);	//���type		
				metricplugindata.push_back("subInstConverter");	//���classKey		
				metricplugindata.push_back(tempnullstr);	//typeֵ		
				metricplugindata.push_back("ValueColumnTitle");	//keyֵ	

				if( (isSELECT) || (ischildresid == "y")
					|| (isChild && (childrestype == "cpu") && (tempMetricid != "cpuId")) )
				{
					metricplugindata.push_back(tempMetricid);	//valueֵ
				}
				else
				{
					metricplugindata.push_back(tempcolumnvalue);
				}

				metricplugindata.push_back(columnvalue);	//columnֵ		
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);

				if(childindex != tempMetricid)
				{
					metricplugindata.clear();
					metricplugindata.push_back(tempMetricid);
					metricplugindata.push_back(tempPluginid);
					metricplugindata.push_back(tempCollectType);
					metricplugindata.push_back("PluginDataConverter");		
					metricplugindata.push_back(tempnullstr);	//���type		
					metricplugindata.push_back("subInstConverter");	//���classKey		
					metricplugindata.push_back("ResourceProperty");	//typeֵ		
					metricplugindata.push_back("InstPropertyKey");	//keyֵ		
					metricplugindata.push_back(childindex);	//valueֵ	
					metricplugindata.push_back(columnvalue);	//columnֵ		
					//дһ�����������ݵ�Excel�ڴ�������
					MetricPluginDatas_.push_back(metricplugindata);
					metricplugindata.clear();
				}
			}			
			
		}
	}
	
}





