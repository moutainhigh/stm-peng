#include "StdAfx.h"
#include "DBXmlToXml.h"

DBXmlToXml::DBXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag)
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

DBXmlToXml::~DBXmlToXml(void)
{
	//MYLOG("entry");
}


void DBXmlToXml::AnalysisMain(int flag)
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
	ParseDatabase();

	//д�� ***InstanceID ָ��
	InsertMainInstanceID();

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


void DBXmlToXml::InitDatas()
{
	VEC_STRING_DATAS tmpdata;
	//CGlobalMetricSettingDatas_��ʼ��
	tmpdata.clear();
	tmpdata.push_back("PluginInitParameter");
	tmpdata.push_back("JdbcPlugin");		//pluginid
	tmpdata.push_back("dbType");		//parameterId

	string databaseport("");
	int npos = resourceid_.find("Oracle");
	if(npos >= 0)
	{
		tmpdata.push_back("Oracle");		//parameterValue
		databaseport = "1521";
	}
	else
	{
		npos = resourceid_.find("SQLServer");
		if(npos >= 0)
		{
			tmpdata.push_back("SQLServer");		//parameterValue
			databaseport = "1433";
		}
		else
		{
			tmpdata.push_back(resourceid_);		//parameterValue
		}
	}

	npos = resourceid_.find("DB2");
	if(npos >= 0)
	{
		databaseport = "50000";
	}
	
	npos = resourceid_.find("Informix");
	if(npos >= 0)
	{
		databaseport = "9089";
	}

	npos = resourceid_.find("MySQL");
	if(npos >= 0)
	{
		databaseport = "3306";
	}

	npos = resourceid_.find("PostgreSQL");
	if(npos >= 0)
	{
		databaseport = "5432";
	}

	npos = resourceid_.find("Sybase");
	if(npos >= 0)
	{
		databaseport = "5000";
	}

	tmpdata.push_back("");		//parameterProperty
	tmpdata.push_back("");		//propertyValue
	CGlobalMetricSettingDatas_.push_back(tmpdata);
	
	tmpdata.clear();
	tmpdata.push_back("ChangePluginInitParameter");
	tmpdata.push_back("JdbcPlugin");		//pluginid
	tmpdata.push_back("inputInstallPath");		//parameterId
	tmpdata.push_back("");		//parameterValue
	tmpdata.push_back("isDisplay");		//parameterProperty
	tmpdata.push_back("TRUE");		//propertyValue
	CGlobalMetricSettingDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("ChangePluginInitParameter");
	tmpdata.push_back("JdbcPlugin");		//pluginid
	tmpdata.push_back("jdbcPort");		//parameterId
	tmpdata.push_back("");		//parameterValue
	tmpdata.push_back("DefaultValue");		//parameterProperty
	tmpdata.push_back(databaseport);		//propertyValue
	CGlobalMetricSettingDatas_.push_back(tmpdata);

	//PluginClassAliasListDatas_ ��ʼ��
	PluginClassAliasListDatas_ = g_GlobalData.PCALDatas_;
	tmpdata.clear();
	tmpdata.push_back("processConverter");
	tmpdata.push_back("com.mainsteam.stm.plugin.common.ProcessResultSetConverter");
	PluginClassAliasListDatas_.push_back(tmpdata);

	//RGlobalMetricSettingDatas_ ��ʼ��
	RGlobalMetricSettingDatas_ = g_GlobalData.RGMSDatas_;

	/********************
	** ���� macAddress ָ��
	********************/
	//tmpdata.clear();
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("WmiPlugin");
	//tmpdata.push_back("WMI");
	//tmpdata.push_back("PluginParameter");
	//tmpdata.push_back("ArrayType");	//���type
	//tmpdata.push_back("");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("COMMAND");			//keyֵ
	//tmpdata.push_back("root\\cimv2::SELECT MacAddress FROM Win32_NetworkAdapterConfiguration WHERE IPEnabled = True");		//valueֵ
	//tmpdata.push_back("macAddress");
	//MetricPluginDatas_.push_back(tmpdata);

	//tmpdata.clear();
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("WmiPlugin");
	//tmpdata.push_back("WMI");
	//tmpdata.push_back("PluginDataHandler");
	//tmpdata.push_back("");	//���type
	//tmpdata.push_back("selectProcessor");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("SELECT");			//keyֵ
	//tmpdata.push_back("SELECT join(macAddress) AS macAddress");
	//tmpdata.push_back("macAddress");
	//MetricPluginDatas_.push_back(tmpdata);

	//tmpdata.clear();
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("TelnetPlugin");
	//tmpdata.push_back("TELNET");
	//tmpdata.push_back("PluginParameter");
	//tmpdata.push_back("ArrayType");	//���type
	//tmpdata.push_back("");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("COMMAND");			//keyֵ
	//tmpdata.push_back("/tmp/hostcommon/os.sh");		//valueֵ
	//tmpdata.push_back("");
	//MetricPluginDatas_.push_back(tmpdata);

	//tmpdata.clear();
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("TelnetPlugin");
	//tmpdata.push_back("TELNET");
	//tmpdata.push_back("PluginDataHandler");
	//tmpdata.push_back("");	//���type
	//tmpdata.push_back("RegularFilter");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("REGULAR");			//keyֵ
	//tmpdata.push_back("<macaddress>(.+)</macaddress>");
	//tmpdata.push_back("");
	//MetricPluginDatas_.push_back(tmpdata);

	//tmpdata.clear();
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("SshPlugin");
	//tmpdata.push_back("SSH");
	//tmpdata.push_back("PluginParameter");
	//tmpdata.push_back("ArrayType");	//���type
	//tmpdata.push_back("");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("COMMAND");			//keyֵ
	//tmpdata.push_back("/tmp/hostcommon/os.sh");		//valueֵ
	//tmpdata.push_back("");
	//MetricPluginDatas_.push_back(tmpdata);

	//tmpdata.clear();
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("SshPlugin");
	//tmpdata.push_back("SSH");
	//tmpdata.push_back("PluginDataHandler");
	//tmpdata.push_back("");	//���type
	//tmpdata.push_back("RegularFilter");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("REGULAR");			//keyֵ
	//tmpdata.push_back("<macaddress>(.+)</macaddress>");
	//tmpdata.push_back("");
	//MetricPluginDatas_.push_back(tmpdata);


	//tmpdata.clear();
	//tmpdata.push_back(resourceid_);
	//tmpdata.push_back(resourcecategory_);
	//tmpdata.push_back(resourceicon_);
	//tmpdata.push_back(resourcename_);
	//tmpdata.push_back(resourcedesc_);
	//tmpdata.push_back("");
	//tmpdata.push_back("");
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("InformationMetric");
	//tmpdata.push_back("MAC��ַ");
	//tmpdata.push_back("MAC��ַ");
	//tmpdata.push_back("");
	//tmpdata.push_back("true");
	//tmpdata.push_back("1500");
	//tmpdata.push_back("true");
	//tmpdata.push_back("false");
	//tmpdata.push_back("hour1");
	//tmpdata.push_back("hour1,day1");
	//tmpdata.push_back("true");
	//tmpdata.push_back("1");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	////tmpdata.push_back("");
	//ResourceDatas_.push_back(tmpdata);
	///* macAddress ���*/

	/********************
	** ���� cpuRate ָ��
	********************/
	tmpdata.clear();
	tmpdata.push_back("cpuRate");
	tmpdata.push_back("WmiPlugin");
	tmpdata.push_back("WMI");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("COMMAND");			//keyֵ
	tmpdata.push_back("root\\cimv2::select PercentProcessorTime from Win32_PerfFormattedData_PerfProc_Process where name = '_Total'");		//valueֵ
	tmpdata.push_back("CPU");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("cpuRate");
	tmpdata.push_back("TelnetPlugin");
	tmpdata.push_back("TELNET");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("COMMAND");			//keyֵ
	tmpdata.push_back("/tmp/hostcommon/appinfo.sh");		//valueֵ
	tmpdata.push_back("CPU");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("cpuRate");
	tmpdata.push_back("TelnetPlugin");
	tmpdata.push_back("TELNET");
	tmpdata.push_back("PluginDataHandler");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("RegularFilter");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("REGULAR");			//keyֵ
	tmpdata.push_back("<get_cpurate>(.+)</get_cpurate>");
	tmpdata.push_back("CPU");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("cpuRate");
	tmpdata.push_back("SshPlugin");
	tmpdata.push_back("SSH");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("COMMAND");			//keyֵ
	tmpdata.push_back("/tmp/hostcommon/appinfo.sh");		//valueֵ
	tmpdata.push_back("CPU");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("cpuRate");
	tmpdata.push_back("SshPlugin");
	tmpdata.push_back("SSH");
	tmpdata.push_back("PluginDataHandler");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("RegularFilter");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("REGULAR");			//keyֵ
	tmpdata.push_back("<get_cpurate>(.+)</get_cpurate>");
	tmpdata.push_back("CPU");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back(resourcecategory_);
	tmpdata.push_back(resourceicon_);
	tmpdata.push_back(resourcename_);
	tmpdata.push_back(resourcedesc_);
	tmpdata.push_back("");
	tmpdata.push_back("");

	tmpdata.push_back("cpuRate");
	tmpdata.push_back("PerformanceMetric");
	tmpdata.push_back("CPU������");
	tmpdata.push_back("CPU������");
	tmpdata.push_back("%");

	tmpdata.push_back("true");
	tmpdata.push_back("1400");
	tmpdata.push_back("true");
	tmpdata.push_back("false");
	tmpdata.push_back("min5");
	tmpdata.push_back("min5,hour1,day1");
	tmpdata.push_back("true");
	tmpdata.push_back("1");

	tmpdata.push_back("<");
	tmpdata.push_back("80");
	tmpdata.push_back("Normal");
	tmpdata.push_back(">=");
	tmpdata.push_back("80");
	tmpdata.push_back("Minor");
	tmpdata.push_back(">=");
	tmpdata.push_back("90");
	tmpdata.push_back("Major");
	ResourceDatas_.push_back(tmpdata);
	/* cpuRate ���*/

	/********************
	** ���� memRate ָ��
	********************/
	//tmpdata.clear();
	//tmpdata.push_back("memRate");
	//tmpdata.push_back("WmiPlugin");
	//tmpdata.push_back("WMI");
	//tmpdata.push_back("PluginParameter");
	//tmpdata.push_back("ArrayType");	//���type
	//tmpdata.push_back("");			//���classKey
	//tmpdata.push_back("");			//typeֵ
	//tmpdata.push_back("COMMAND");			//keyֵ
	//tmpdata.push_back("root\\cimv2::select WorkingSet from Win32_PerfFormattedData_PerfProc_Process where name = '_Total'");		//valueֵ
	//tmpdata.push_back("Memory,TotalMemSize");
	//MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("memRate");
	tmpdata.push_back("WmiPlugin");
	tmpdata.push_back("WMI");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("COMMAND");			//keyֵ
	tmpdata.push_back("root\\cimv2::select FreePhysicalMemory,TotalVisibleMEMorySize from Win32_OperatingSystem");		//valueֵ
	tmpdata.push_back("Memory,TotalMemSize");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("memRate");
	tmpdata.push_back("WmiPlugin");
	tmpdata.push_back("WMI");
	tmpdata.push_back("PluginDataHandler");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("selectProcessor");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("SELECT");			//keyֵ
	tmpdata.push_back("SELECT (TotalMemSize - Memory)/TotalMemSize*100 AS memRate");
	tmpdata.push_back("Memory,TotalMemSize");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("memRate");
	tmpdata.push_back("TelnetPlugin");
	tmpdata.push_back("TELNET");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("COMMAND");			//keyֵ
	tmpdata.push_back("/tmp/hostcommon/appinfo.sh");		//valueֵ
	tmpdata.push_back("Memory");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("memRate");
	tmpdata.push_back("TelnetPlugin");
	tmpdata.push_back("TELNET");
	tmpdata.push_back("PluginDataHandler");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("RegularFilter");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("REGULAR");			//keyֵ
	tmpdata.push_back("<get_pmemrate>(.*)</get_pmemrate>");
	tmpdata.push_back("Memory");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("memRate");
	tmpdata.push_back("SshPlugin");
	tmpdata.push_back("SSH");
	tmpdata.push_back("PluginParameter");
	tmpdata.push_back("ArrayType");	//���type
	tmpdata.push_back("");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("COMMAND");			//keyֵ
	tmpdata.push_back("/tmp/hostcommon/appinfo.sh");		//valueֵ
	tmpdata.push_back("Memory");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back("memRate");
	tmpdata.push_back("SshPlugin");
	tmpdata.push_back("SSH");
	tmpdata.push_back("PluginDataHandler");
	tmpdata.push_back("");	//���type
	tmpdata.push_back("RegularFilter");			//���classKey
	tmpdata.push_back("");			//typeֵ
	tmpdata.push_back("REGULAR");			//keyֵ
	tmpdata.push_back("<get_pmemrate>(.*)</get_pmemrate>");
	tmpdata.push_back("Memory");
	MetricPluginDatas_.push_back(tmpdata);

	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back(resourcecategory_);
	tmpdata.push_back(resourceicon_);
	tmpdata.push_back(resourcename_);
	tmpdata.push_back(resourcedesc_);
	tmpdata.push_back("");
	tmpdata.push_back("");

	tmpdata.push_back("memRate");
	tmpdata.push_back("PerformanceMetric");
	tmpdata.push_back("�ڴ�������");
	tmpdata.push_back("�ڴ�������");
	tmpdata.push_back("%");

	tmpdata.push_back("true");
	tmpdata.push_back("2500");
	tmpdata.push_back("true");
	tmpdata.push_back("false");
	tmpdata.push_back("min5");
	tmpdata.push_back("min5,hour1,day1");
	tmpdata.push_back("true");
	tmpdata.push_back("1");

	tmpdata.push_back("<");
	tmpdata.push_back("80");
	tmpdata.push_back("Normal");
	tmpdata.push_back(">=");
	tmpdata.push_back("80");
	tmpdata.push_back("Minor");
	tmpdata.push_back(">=");
	tmpdata.push_back("90");
	tmpdata.push_back("Major");
	ResourceDatas_.push_back(tmpdata);
	/* memRate ���*/

	//PropertiesDatas_ ��ʼ�� ����Դ
	tmpdata.clear();
	tmpdata.push_back(resourceid_);
	tmpdata.push_back("hostName");
	tmpdata.push_back("hostName");
	tmpdata.push_back("hostName");
	PropertiesDatas_.push_back(tmpdata);
	//tmpdata.clear();
	//tmpdata.push_back(resourceid_);
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("macAddress");
	//tmpdata.push_back("macAddress");
	//PropertiesDatas_.push_back(tmpdata);

	VEC_VEC_DATAS::iterator it = PropertiesDatas_.begin();
	for( ; it != PropertiesDatas_.end(); ++it)
	{
		PropertiesData_.insert((it->at(0)) + (it->at(1)));
	}


	//InstantiationDatas_ ��ʼ�� ����Դ
	string tempInstanceID = resourcecategory_ + "InstanceID";
	string mainvalue = "," + tempInstanceID + ",";
	mapinstantiationId_[resourceid_] = mainvalue;
	mapinstantiationName_[resourceid_] = tempInstanceID;
}


void DBXmlToXml::ParseDatabase()
{
	//�����Լ����ļ�
	string pathfilename = strXmlPath_ + filename_;

	ismysql_ = FALSE;
	int nposmysql = filename_.find("MySQL.xml");
	if(nposmysql >= 0)
	{
		ismysql_ = TRUE;
	}

	//�����oracle�ģ�Ҫ�Ƚ���Oracle.xml
	int npos1 = filename_.find("Oracle");
	int npos2 = filename_.find("Oracle.xml");
	//���ҵ�Oracle && ����Oracle.xml ����Ҫ�Ƚ���һ�� Oracle.xml�ļ����ڽ����Լ�
	if((npos1 >= 0) && (npos2 < 0))
	{
		string public_oracle = strXmlPath_ + "\\db\\Oracle.xml";
		ParseFile(public_oracle);
	}
	
	//�����oracle�ģ�Ҫ�Ƚ���Oracle.xml
	int npos3 = filename_.find("SQLServer");
	int npos4 = filename_.find("SQLServer.xml");
	//���ҵ�SQLServer && ����SQLServer.xml ����Ҫ�Ƚ���һ�� SQLServer.xml�ļ����ڽ����Լ�
	if((npos3 >= 0) && (npos4 < 0))
	{
		string public_oracle = strXmlPath_ + "\\db\\SQLServer.xml";
		ParseFile(public_oracle);
	}

	ParseFile(pathfilename);
}


void DBXmlToXml::ParseFile(string filename)
{
	//stringstream tmplog("");
	VEC_STRING_DATAS tmpdata;

	//	
	MYLOG(filename.c_str());
	if(!CheckFileExist(filename))
	{
		loglog.str("");
		loglog << "�ļ��� " << filename << " �����ڣ�";
		AfxMessageBox(loglog.str().c_str());
		MYLOG(loglog.str().c_str());
		return ;
	}

	file<> fdoc(filename.c_str());
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

BOOL DBXmlToXml::CheckResourceattr(VEC_STRING_DATAS &resourceattr)
{
	//��5������
	if(resourceattr.size() < 5)
	{
		MYLOG("warn : resourceattr.size() < 5");
		return FALSE;
	}

	//if((resourceattr[0] == "Availability") || (resourceattr[0] == "D1PingResponseTime"))
	if(resourceattr[0] == "D1PingResponseTime")
	{
		return TRUE;
	}

	if(resourceattr[1] == "INNER_PROC")
	{
		string loglog = "warn : resourceattr[0]=[" + resourceattr[0] +  "] discard.  resourceattr[1]=INNER_PROC";
		MYLOG(loglog.c_str());
		return FALSE;
	}

	//ȥ��macadrressָ��
	string tempmetric = LetterTolower(ChangePluginid(resourceattr[0]));
	if(tempmetric == "macaddress")
	{
		MYLOG("warn : metric = macaddress .");
		return FALSE;
	}

	return TRUE;

}

void DBXmlToXml::ParseMetricData(VEC_STRING_DATAS &deviceattr, MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, VEC_STRING_DATAS &kpipolicyattr, string translationsdata)
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


BOOL DBXmlToXml::GetColumnData(string kpiid, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr, string childrestype, BOOL &isSpecial)
{
	BOOL isSELECT = FALSE;

	if(!resourcevalue.empty())
	{
		if(childrestype == "hostProcess")
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
	if((!columnvalue.empty()) && (childrestype != "hostProcess"))
	{
		columnvalue = columnvalue.substr(0, columnvalue.length() - 1);
	}

	return isSELECT;
}


BOOL DBXmlToXml::SpecialHandling(VEC_STRING_DATAS &resourceattr)
{
	BOOL ret = FALSE;
	string kpiid = resourceattr[0];
	string coltype = resourceattr[1];
	string metricid = ChangeKpiid(kpiid);
	string pluginid = ChangePluginid(coltype);

	VEC_STRING_DATAS tmpvec;
	//if(kpiid == "Availability")
	//{
	//	ret = TRUE;
	//	tmpvec.clear();
	//	tmpvec.push_back(metricid);
	//	tmpvec.push_back("IcmpPlugin");
	//	tmpvec.push_back("");
	//	tmpvec.push_back("PluginParameter");		
	//	tmpvec.push_back("ArrayType");	//���type		
	//	tmpvec.push_back("");	//���classKey		
	//	tmpvec.push_back("DiscoveryInfo");	//typeֵ		
	//	tmpvec.push_back("IP");	//keyֵ
	//	tmpvec.push_back("IP");	//valueֵ
	//	tmpvec.push_back("ip,isAlive,ResponseTime");	//columnֵ		
	//	//дһ�����������ݵ�Excel�ڴ�������
	//	MetricPluginDatas_.push_back(tmpvec);

	//	tmpvec.clear();
	//	tmpvec.push_back(metricid);
	//	tmpvec.push_back("IcmpPlugin");
	//	tmpvec.push_back("");
	//	tmpvec.push_back("PluginDataHandler");		
	//	tmpvec.push_back("");	//���type		
	//	tmpvec.push_back("selectProcessor");	//���classKey		
	//	tmpvec.push_back("");	//typeֵ		
	//	tmpvec.push_back("SELECT");	//keyֵ
	//	tmpvec.push_back("SELECT isAlive");	//valueֵ
	//	tmpvec.push_back("ip,isAlive,ResponseTime");	//columnֵ		
	//	//дһ�����������ݵ�Excel�ڴ�������
	//	MetricPluginDatas_.push_back(tmpvec);

	//}

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

	return ret;
}


void DBXmlToXml::ChangeCommand(string tempCollectType, string tmpselect, string &command, string &tempkey, BOOL &isCommandChildid, BOOL &isCommandDB)
{
	isCommandChildid = FALSE;
	isCommandDB = FALSE;
	int npos = -1;

	if(command == "show status")
	{		
		npos = tmpselect.find("\\t");
		if(npos > 0)
		{
			tmpselect = tmpselect.substr(0, npos);
		}
		else if (npos == 0)
		{
			tmpselect = "";
		}

		if(tempCollectType == "JDBC")
		{
			tempkey = "SQL";
		}
		else
		{
			tempkey = "COMMAND";
		}
		
		command = "SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME='" + tmpselect + "'";
		return ;
	}
	
	string oldstr = "$childid";
	string newstr = "${childid}";
	npos = command.find(oldstr);
	if (npos >= 0)
	{
		isCommandChildid = TRUE;
		command = replace_all_distinct(command, oldstr, newstr);
	}

	npos = command.find("${dbName}");
	if (npos >= 0)
	{
		isCommandDB = TRUE;
	}

	//db ���� ����cmd
	if(tempCollectType == "JDBC")
	{
		tempkey = "SQL";
		string oldcmd1 = "show variables like";
		string oldcmd2 = "show global status like";
		string newcmd1 = "SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_VARIABLES WHERE VARIABLE_NAME=";
		string newcmd2 = "SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME=";
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
	else
	{
		tempkey = "COMMAND";
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
	}

	//string tempfind = "root\\cimv2::";
	//npos = command.find(tempfind);
	//if(npos >= 0)
	//{
	//	command = replace_all_distinct(command, tempfind, "");
	//}
	return ;
}

BOOL DBXmlToXml::ReplaceSpecial(string &strvalue, string childrestype)
{
	BOOL ret = TRUE;
	int npos = -1;
	string findstr1 = "($key)";
	string findstr2 = "$key";
	string newstr("");
	if(childrestype == "cpu")
	{
		newstr = "(\\d+)";
	}
	else
	{
		newstr = "(\\S+)";
	}

	npos = strvalue.find(findstr1);
	if(npos >= 0)
	{		
		int temppos = strvalue.find("(");
		if(temppos == npos)
		{
			temppos = strvalue.find("(", temppos + 1);
		}

		if((temppos < npos) && (temppos >= 0))
		{
			ret = FALSE;
		}

		strvalue = replace_all_distinct(strvalue, findstr1, newstr);
	}
	npos = strvalue.find(findstr2);
	if(npos >= 0)
	{		
		int temppos = strvalue.find("(");
		if((temppos < npos) && (temppos >= 0))
		{
			ret = FALSE;
		}

		strvalue = replace_all_distinct(strvalue, findstr2, newstr);
	}

	return ret;
}

BOOL DBXmlToXml::ParseSelectValue(string &strvalue, string childrestype, string &selectvalue, string &resourcevalue, string &columnvalue, string &indexcolumntitle, string &valuecolumntitle)
{
	BOOL ret = FALSE;

	int npos = -1;
	string findstr1 = "($key)";
	string findstr2 = "$key";
	string newstr("");
	if(childrestype == "cpu")
	{
		newstr = "(\\d+)";
	}
	else
	{
		newstr = "(\\S+)";
	}

	string splitstr = "\\t";
	VEC_STRING_DATAS vettempvalue;
	VEC_STRING_DATAS vetcolumnvalue;
	stringstream tempindexcolumn("");
	vettempvalue.clear();
	vetcolumnvalue.clear();
	
	spilt_str_bystr(strvalue, splitstr, vettempvalue);

	int  nposkey = 0;
	if(!vettempvalue.empty())
	{
		VEC_STRING_DATAS::iterator it = vettempvalue.begin();
		for(int i = 1; it != vettempvalue.end(); ++it, ++i)
		{
			string tempvalue = *it;

			if(!ret)
			{
				npos = tempvalue.find(findstr1);
				if(npos >= 0)
				{
					ret = TRUE;
					nposkey = i;
					tempindexcolumn << "column" << i << flush;
					indexcolumntitle = tempindexcolumn.str();
					strvalue = replace_all_distinct(strvalue, findstr1, newstr);
					tempvalue = replace_all_distinct(tempvalue, findstr1, newstr);
				}
				npos = tempvalue.find(findstr2);
				if(npos >= 0)
				{					
					ret = TRUE;
					nposkey = i;
					tempindexcolumn << "column" << i << flush;
					indexcolumntitle = tempindexcolumn.str();
					strvalue = replace_all_distinct(strvalue, findstr2, newstr);
					tempvalue = replace_all_distinct(tempvalue, findstr2, newstr);
				}
			}

			npos = tempvalue.find("(");
			if((npos >= 0) && (i != nposkey))
			{
				stringstream tempvalue("");
				tempvalue << "column" << i << flush;
				vetcolumnvalue.push_back(tempvalue.str());
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
			SET_INT_DATAS::iterator it = setparam.begin();
			for( ; it != setparam.end(); ++it )
			{
				int temp = *it;
				stringstream oldvalue("");
				string newvalue("");
				oldvalue << "$d" << *it << flush;
				if(((int)vetcolumnvalue.size()) >= temp)
				{
					newvalue = vetcolumnvalue[temp - 1];
					resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue);
				}
								
			}
			selectvalue = "SELECT " + indexcolumntitle + ", (" + resourcevalue + ") as column";
			valuecolumntitle = "column";
		}
	}
	else
	{
		selectvalue = "";
		if(vetcolumnvalue.empty())
		{
			valuecolumntitle = indexcolumntitle;
		}
		else
		{
			valuecolumntitle = vetcolumnvalue[0];
		}		
	}

	for(int i = 1; i <= ((int)vettempvalue.size()); i++)
	{
		stringstream tempvalue("");
		tempvalue << "column" << i << flush;
		columnvalue = columnvalue + tempvalue.str() + ",";
	}

	if(!columnvalue.empty())
	{
		columnvalue = columnvalue.substr(0, columnvalue.length() - 1);
	}

	return ret;


}
void DBXmlToXml::PrepareChildMetricData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata)
{
	VEC_STRING_DATAS metricplugindata;	
	string tempnullstr("");
	BOOL isSELECT = FALSE;
	BOOL isREGULAR = FALSE;
	BOOL isChild = FALSE;
	BOOL isChildConverter = FALSE;
	BOOL isMainConverter = FALSE;
	BOOL isCommandChildid = FALSE;
	BOOL isCommandDB = FALSE;
	BOOL childindexisfirst = TRUE;
	BOOL isSpecial = FALSE;
	VEC_STRING_DATAS vecregulardata;
	VEC_STRING_DATAS vecdealdata;
	VEC_STRING_DATAS veccolumndata;

	vecregulardata.clear();
	vecdealdata.clear();
	veccolumndata.clear();

	//{"metricid", "pluginid", "collectType", "�ɼ�����", "���type", "���classKey", "typeֵ", "keyֵ", "valueֵ", "columnֵ"};

	string tmpkpiid = resourceattr[0];
	string resourcevalue = resourceattr[2];
	string columnvalue("");
	string tempcolumnvalue("");
	string kpitype("");
	string childindex("");
	string childrestype("");
	string ischildresid("");
	string reslevel("");
	string ischildresname("");
	string selectvalue("");
	string indexcolumntitle("");
	string valuecolumntitle("");


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
		childrestype = kpiinfo[7];
		if(!childrestype.empty())
		{
			childindex = ChangeKpiid(mapKPIchildDatas_[childrestype]);
		}

		kpitype = kpiinfo[4];
		reslevel = kpiinfo[6];
		ischildresid = kpiinfo[8];
		ischildresname = kpiinfo[9];

		if(reslevel == "child")
		{
			isChild = TRUE;
		}

	}

	string tempMetricid = ChangeKpiid(resourceattr[0]);
	string tempPluginid = ChangePluginid(resourceattr[1]);
	string tempCollectType = resourceattr[1];
	if((tempCollectType == "SNMP") || (tempCollectType == "JDBC"))
	{
		tempCollectType = "";
	}

	//������ָ�� **InstanceID �� pluginid �ļ���
	if(!tempPluginid.empty())
	{
		setInstanceID_.insert(tempPluginid);
	}

	//���� merge �ֶ�
	ParseMerge(resourcevalue, collectattr);

	BOOL isContinue = FALSE;
	VEC_VEC_DATAS vcommandvalue;
	vcommandvalue.clear();
	
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
			if((!tmpselect.empty()) && (tmpcmdid != "statusInfo"))
			{
				isREGULAR = TRUE;
				isChildConverter = ParseSelectValue(tmpselect, childrestype, selectvalue, resourcevalue, columnvalue, indexcolumntitle, valuecolumntitle);
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
				MYLOG(tmpcmdid.c_str());
				break;
			}				

			string kpiid = resourceattr[0];
			string tempkey("");
			ChangeCommand(resourceattr[1], tmpselect, cmdvalues, tempkey, isCommandChildid, isCommandDB);

			
			VEC_STRING_DATAS tempcommandvalue;
			tempcommandvalue.push_back(tempkey);
			tempcommandvalue.push_back(str_trim(cmdvalues));

			vcommandvalue.push_back(tempcommandvalue);


		}
	}

	if((!translationsdata.empty()) && (columnvalue.empty()))
	{
		columnvalue = "column1";
	}

	if(!vcommandvalue.empty())
	{
		VEC_VEC_DATAS::iterator it = vcommandvalue.begin();
		for( ; it != vcommandvalue.end(); ++it)
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ

			metricplugindata.push_back(it->at(0));			//keyֵ	
			metricplugindata.push_back(it->at(1));			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();
		}
		
	}
	

	if(isCommandChildid)
	{
		string tempvalue = mapKPIchildDatas_[childrestype];
		if(tempvalue.empty())
		{
			loglog.str("");
			loglog << "error: ָ�꣺[" << tempMetricid << "],�ڵ�childrestype������" << flush;
			MYLOG(loglog.str().c_str());
		}
		else
		{
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back("ResourceProperty");		//typeֵ			
			metricplugindata.push_back("childid");			//keyֵ			
			metricplugindata.push_back(ChangeKpiid(tempvalue));			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

			//���PropertiesData���Ƿ��д�����
			CheckPropertiesData(tempvalue, isChild, childrestype);

			////�����childid�Ͳ���Ҫתconverter��
			//isChildConverter = FALSE;
		}		
	}

	if(isCommandDB)
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginParameter");			
		metricplugindata.push_back("ArrayType");		//���type			
		metricplugindata.push_back(tempnullstr);	//���classKey			
		metricplugindata.push_back("DiscoveryInfo");		//typeֵ			
		metricplugindata.push_back("dbName");			//keyֵ			
		metricplugindata.push_back("dbName");			//valueֵ			
		metricplugindata.push_back(columnvalue);		//columnֵ			
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
	}


	if(!selectvalue.empty())
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

		//����resourcevalue����� $����
		SET_STRING_DATAS replacedatas;
		replacedatas.clear();
		if(ParsePluginDataHandlerValue(selectvalue, replacedatas))
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
					tmpvec.push_back(tempvalue);	//keyֵ
					tmpvec.push_back(tempvalue);	//valueֵ
					tmpvec.push_back(columnvalue);	//columnֵ		
					//tmpvec.push_back(tempnullstr);	//Iscommentֵ
					//дһ�����������ݵ�Excel�ڴ�������
					MetricPluginDatas_.push_back(tmpvec);
				}
			}
		}


		metricplugindata.push_back(selectvalue);		//valueֵ		
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
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
		//tmpvec.push_back(tempnullstr);	//Iscommentֵ
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);	
	}


	//
	if(!vecdealdata.empty())
	{
		VEC_STRING_DATAS::iterator it = vecdealdata.begin();
		string tempdeal = *it;
		VEC_STRING_DATAS tmpvec;

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
		tmpvec.push_back(indexcolumntitle);	//valueֵ
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

	string tempporcess("");
	if((childrestype == "hostProcess") && isContinue)
	{
		int nposbegin = resourcevalue.find("(");
		int nposend = resourcevalue.find(")");
		if((nposbegin >= 0) && (nposend >= 0) && (nposend > nposbegin))
		{
			tempporcess = resourcevalue.substr(nposbegin + 1, nposend - nposbegin - 1);
		}
	}


	//if((!translationsdata.empty()) && (kpitype != "avail"))
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

		if(columnvalue.empty())
		{
			metricplugindata.push_back("");	//keyֵ
		}
		else
		{
			string tempvalue("");
			VEC_STRING_DATAS tempvet;
			tempvet.clear();
			tempvet = split_str_bychar(columnvalue, ',');

			if(indexcolumntitle.empty())
			{
				tempvalue = tempvet[0];
			}
			else
			{
				VEC_STRING_DATAS::iterator it = tempvet.begin();
				for( ; it != tempvet.end(); ++it)
				{
					tempvalue.clear();
					tempvalue = *it;
					tempvalue = str_trim(tempvalue);
					if(tempvalue != indexcolumntitle)
					{
						break;
					}
				}
			}
			
			metricplugindata.push_back(tempvalue);	//keyֵ			
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
	}

	if((childrestype == "hostProcess") && isContinue)
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
			metricplugindata.push_back("processCommand");	//valueֵ	
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);

		}

	}
	else
	{
		if((isChildConverter) && (!isCommandChildid))
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
			metricplugindata.push_back(indexcolumntitle);	//valueֵ
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
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
			metricplugindata.push_back(valuecolumntitle);
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

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
				//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);

				//��������� ������û�еģ����ӽ�ȥ
				CheckPropertiesData(childindex, isChild, childrestype);
			}
		}
	}

}

void DBXmlToXml::PrepareMetricPluginAllData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata)
{
	VEC_STRING_DATAS metricplugindata;	
	string tempnullstr("");
	BOOL isSELECT = FALSE;
	BOOL isREGULAR = FALSE;
	BOOL isChild = FALSE;
	BOOL isChildConverter = FALSE;
	BOOL isMainConverter = FALSE;
	BOOL isCommandChildid = FALSE;
	BOOL isCommandDB = FALSE;
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
	string ischildresid("");
	string reslevel("");
	string ischildresname("");

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
		childrestype = kpiinfo[7];
		if(!childrestype.empty())
		{
			childindex = ChangeKpiid(mapKPIchildDatas_[childrestype]);
		}

		kpitype = kpiinfo[4];
		reslevel = kpiinfo[6];
		ischildresid = LetterTolower(kpiinfo[8]);
		ischildresname = LetterTolower(kpiinfo[9]);

	}

	string tempMetricid = ChangeKpiid(resourceattr[0]);
	string tempPluginid = ChangePluginid(resourceattr[1]);
	string tempCollectType = resourceattr[1];
	if((tempCollectType == "SNMP") || (tempCollectType == "JDBC"))
	{
		tempCollectType = "";
	}

	//��ȥmysql���������Դ����Ϊ���⴦��
	if((!ismysql_) && (reslevel == "child"))
	{
		PrepareChildMetricData(kpimapcmd, resourceattr, collectattr, translationsdata);
		return ;
	}

	//������ָ�� **InstanceID �� pluginid �ļ���
	if(!tempPluginid.empty())
	{
		setInstanceID_.insert(tempPluginid);
	}

	//���� merge �ֶ�
	ParseMerge(resourcevalue, collectattr);

	isSELECT = GetColumnData(tmpkpiid, resourcevalue, columnvalue, kpimapcmd, collectattr, childrestype, isSpecial);

	if((tempMetricid == "appMemRate") && (tempCollectType == "WMI"))
	{
		string oldvalue1 = "chk_memrate_wmi";
		string oldvalue2 = "chk_TotalMemSize_wmi";
		string newvalue1 = "Memory";
		string newvalue2 = "TotalMemSize";
		resourcevalue = replace_all_distinct(resourcevalue, oldvalue1, newvalue1);
		resourcevalue = replace_all_distinct(resourcevalue, oldvalue2, newvalue2);

		columnvalue = replace_all_distinct(columnvalue, oldvalue1, newvalue1);
		columnvalue = replace_all_distinct(columnvalue, oldvalue2, newvalue2);
	}

	BOOL isContinue = FALSE;
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
			if((!tmpselect.empty()) && (tmpcmdid != "statusInfo"))
			{
				if((tempCollectType == "WMI") && (tempMetricid == "appMemRate"))
				{
					tmpselect = "";
				}
				else
				{
					isREGULAR = TRUE;
					childindexisfirst = ReplaceSpecial(tmpselect, childrestype);
					vecregulardata.push_back(tmpselect);
				}
				
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
				MYLOG(tmpcmdid.c_str());
				break;
			}				

			string kpiid = resourceattr[0];
			string tempkey("");
			ChangeCommand(resourceattr[1], tmpselect, cmdvalues, tempkey, isCommandChildid, isCommandDB);

			if(reslevel == "main")
			{
				//����Դ ʲô������
			}
			else if(reslevel == "child")
			{
				//if( ((ischildresid == "N") || (ischildresid == "n"))
				//	|| ((ischildresname == "N") || (ischildresname == "n")) )
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
					else if( (isChildConverter) && (childrestype != "hostProcess") && (!isCommandChildid))
					{
						if(childindexisfirst)
						{
							columnvalue = childindex + "," + columnvalue;							
						}
						else
						{
							columnvalue = columnvalue + "," + childindex;
						}											
					}					
				}
			}
			else
			{
				//ָ�����ͳ���
				MYLOG(kpiid.c_str());
				break;
			}

			if((tempMetricid == "appMemRate") && (tempCollectType == "WMI"))
			{
				columnvalue = "Memory,TotalMemSize";
			}

			if(		((tempMetricid == "appCpuRate") ||(tempMetricid == "appMemRate"))
				&& ((tempCollectType == "TELNET") || (tempCollectType == "SSH")))
			{
				VEC_STRING_DATAS tmpvec;
				//����MetricPluginData
				tmpvec.clear();
				tmpvec.push_back(tempMetricid);
				tmpvec.push_back(tempPluginid);
				tmpvec.push_back(tempCollectType);
				tmpvec.push_back("PluginParameter");		
				tmpvec.push_back("ArrayType");	//���type		
				tmpvec.push_back(tempnullstr);	//���classKey		
				tmpvec.push_back("DiscoveryInfo");	//typeֵ		
				tmpvec.push_back("installPath");	//keyֵ
				tmpvec.push_back("inputInstallPath");	//valueֵ
				tmpvec.push_back(columnvalue);	//columnֵ		
				//tmpvec.push_back(tempnullstr);	//Iscommentֵ
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(tmpvec);
			}

			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ
			
			metricplugindata.push_back(tempkey);			//keyֵ	
			metricplugindata.push_back(str_trim(cmdvalues));			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

		}
	}


	if(isCommandChildid)
	{
		string tempvalue = mapKPIchildDatas_[childrestype];
		if(tempvalue.empty())
		{
			loglog.str("");
			loglog << "error: ָ�꣺[" << tempMetricid << "],�ڵ�childrestype������" << flush;
			MYLOG(loglog.str().c_str());
		}
		else
		{
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginParameter");			
			metricplugindata.push_back("ArrayType");		//���type			
			metricplugindata.push_back(tempnullstr);	//���classKey			
			metricplugindata.push_back("ResourceProperty");		//typeֵ			
			metricplugindata.push_back("childid");			//keyֵ			
			metricplugindata.push_back(ChangeKpiid(tempvalue));			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

			//���PropertiesData���Ƿ��д�����
			CheckPropertiesData(tempvalue, isChild, childrestype);

			////�����childid�Ͳ���Ҫתconverter��
			//isChildConverter = FALSE;
		}		
	}

	if(isCommandDB)
	{
		metricplugindata.clear();
		metricplugindata.push_back(tempMetricid);
		metricplugindata.push_back(tempPluginid);
		metricplugindata.push_back(tempCollectType);
		metricplugindata.push_back("PluginParameter");			
		metricplugindata.push_back("ArrayType");		//���type			
		metricplugindata.push_back(tempnullstr);	//���classKey			
		metricplugindata.push_back("DiscoveryInfo");		//typeֵ			
		metricplugindata.push_back("dbName");			//keyֵ			
		metricplugindata.push_back("dbName");			//valueֵ			
		metricplugindata.push_back(columnvalue);		//columnֵ			
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
	}

	if(isREGULAR)
	{
		VEC_STRING_DATAS::iterator itregular = vecregulardata.begin();
		for( ; itregular != vecregulardata.end(); ++itregular )
		{
			metricplugindata.clear();
			metricplugindata.push_back(tempMetricid);
			metricplugindata.push_back(tempPluginid);
			metricplugindata.push_back(tempCollectType);
			metricplugindata.push_back("PluginDataHandler");			
			metricplugindata.push_back(tempnullstr);		//���type			
			metricplugindata.push_back("RegularFilter");	//���classKey			
			metricplugindata.push_back(tempnullstr);		//typeֵ			
			metricplugindata.push_back("REGULAR");			//keyֵ			
			metricplugindata.push_back(*itregular);			//valueֵ			
			metricplugindata.push_back(columnvalue);		//columnֵ			
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
		}
	}

	
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
		tmptype = "SELECT (" + resourcevalue + ") as " + tempMetricid;
		if(isChild && isChildConverter && (!columnvalue.empty()) && (!isCommandChildid) )
		{
			tmptype = tmptype + "," + childindex;
		}

		if(tempMetricid == "processAvail")
		{
			tmptype = "SELECT pid,(avail != null?'1':'0') as avail,command";
		}

		//}
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
					tmpvec.push_back(tempvalue);	//keyֵ
					tmpvec.push_back(tempvalue);	//valueֵ
					tmpvec.push_back(columnvalue);	//columnֵ		
					//tmpvec.push_back(tempnullstr);	//Iscommentֵ
					//дһ�����������ݵ�Excel�ڴ�������
					MetricPluginDatas_.push_back(tmpvec);
				}
			}
		}

	
		metricplugindata.push_back(tmptype);		//valueֵ		
		metricplugindata.push_back(columnvalue);	//columnֵ		
		//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
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
		//tmpvec.push_back(tempnullstr);	//Iscommentֵ
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpvec);	
	}

	//host�����У�����Ҫ������
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
	if((childrestype == "hostProcess") && isContinue)
	{
		int nposbegin = resourcevalue.find("(");
		int nposend = resourcevalue.find(")");
		if((nposbegin >= 0) && (nposend >= 0) && (nposend > nposbegin))
		{
			tempporcess = resourcevalue.substr(nposbegin + 1, nposend - nposbegin - 1);
		}
	}


	//if((!translationsdata.empty()) && (kpitype != "avail"))
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
		if(isSELECT)
		{
			metricplugindata.push_back(tempMetricid);	//keyֵ
		}
		else
		{
			if(childrestype == "hostProcess")
			{
				metricplugindata.push_back(tempporcess);	//keyֵ
			}
			else
			{
				metricplugindata.push_back(tempcolumnvalue);	//keyֵ
			}			
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
		//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(metricplugindata);
	}

	if((childrestype == "hostProcess") && isContinue)
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
			metricplugindata.push_back("processCommand");	//valueֵ	
			metricplugindata.push_back(columnvalue);	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			
		}

	}
	else
	{
		if((isChildConverter) && (!isCommandChildid))
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
			//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
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

			if((isSELECT) || (ischildresid == "y"))
			{
				metricplugindata.push_back(tempMetricid);	//valueֵ
			}
			else
			{
				metricplugindata.push_back(tempcolumnvalue);
			}

			metricplugindata.push_back(columnvalue);	//columnֵ		
			//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);
			metricplugindata.clear();

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
				//metricplugindata.push_back(tempnullstr);	//Iscommentֵ
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);

				//��������� ������û�еģ����ӽ�ȥ
				CheckPropertiesData(childindex, isChild, childrestype);
			}
		}
	}

}

