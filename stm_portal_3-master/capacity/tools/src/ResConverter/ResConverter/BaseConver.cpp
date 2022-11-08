#include "StdAfx.h"
#include "BaseConver.h"

BaseConver::BaseConver(void)
{
	IniData();
}


BaseConver::~BaseConver(void)
{
}


void BaseConver::IniData()
{
	filename_.clear();
	kpifilename_.clear();
	strXmlPath_.clear();
	outfilepath_.clear();

	collectfilename_ = "\\collect.xml";

	CGlobalMetricSettingDatas_.clear();
	PluginClassAliasListDatas_.clear();
	MetricPluginDatas_.clear();
	RGlobalMetricSettingDatas_.clear();
	ResourceDatas_.clear();
	PropertiesDatas_.clear();
	InstantiationDatas_.clear();

	mapPropertiesDatas_.clear();
	mapInstantiationDatas_.clear();

	mapinstantiationId_.clear();
	mapinstantiationName_.clear();

	mapkpidatas_.clear();
	mapKPIchildDatas_.clear();
	mapchipolicydatas_.clear();

	MetricidCollectDatas_.clear();
	MetricidResourceDatas_.clear();
	PropertiesData_.clear();
	setChildIndexDatas_.clear();

	mapResTypeDatas_.clear();
	ismodifyhostname_ = FALSE;

	setInstanceID_.clear();

	//��ʼ��resource��typeֵ
	mapResTypeDatas_["hostInterface"] = "NetInterface";
	mapResTypeDatas_["netInterface"] = "NetInterface";
	mapResTypeDatas_["netIPaddress"] = "IPAddress";
	mapResTypeDatas_["cpu"] = "CPU";
	mapResTypeDatas_["hostFileSystem"] = "Partition";
	mapResTypeDatas_["dbFileSystem"] = "FileSystem";
	mapResTypeDatas_["hardDiskWmi"] = "HardDisk";
	mapResTypeDatas_["hostFile"] = "File";

	mapResTypeDatas_["hostDir"] = "Directory";
	mapResTypeDatas_["hostLVGroup"] = "LogicalVolumeGroup";
	mapResTypeDatas_["hostProcess"] = "Process";
	mapResTypeDatas_["databaseProcess"] = "Process";
	mapResTypeDatas_["sqlserverjob"] = "Job";

	mapResTypeDatas_["jdbcPoolOra"] = "JDBCPool";
	mapResTypeDatas_["jdbcPoolWPS"] = "JDBCPool";
	mapResTypeDatas_["jdbcPoolSUN"] = "JDBCPool";
	mapResTypeDatas_["jdbcPoolResin"] = "JDBCPool";
	mapResTypeDatas_["jdbcPoolWAS"] = "JDBCPool";
	mapResTypeDatas_["weblogicJP"] = "JDBCPool";
	mapResTypeDatas_["JBossJDBC"] = "JDBCPool";

	mapResTypeDatas_["webappresin"] = "WebApp";
	mapResTypeDatas_["webappSUN"] = "WebApp";
	mapResTypeDatas_["webappWPS"] = "WebApp";
	mapResTypeDatas_["webappWAS"] = "WebApp";
	mapResTypeDatas_["portalApp"] = "WebApp";
	mapResTypeDatas_["weblogicWA"] = "WebApp";
	mapResTypeDatas_["JBossWebApp"] = "WebApp";
	mapResTypeDatas_["iisApp"] = "WebApp";

	mapResTypeDatas_["threadPoolOra"] = "ThreadPool";
	mapResTypeDatas_["threadPoolSun"] = "ThreadPool";
	mapResTypeDatas_["threadPoolWAS"] = "ThreadPool";

	mapResTypeDatas_["jmsWAS"] = "JMS";
	mapResTypeDatas_["weblogicJMS"] = "JMS";

	mapResTypeDatas_["nsf"] = "Database";
	mapResTypeDatas_["txserver"] = "Server";
	mapResTypeDatas_["txservice"] = "Service";
	mapResTypeDatas_["txpq"] = "Queue";
	mapResTypeDatas_["queuename"] = "Queue";

	mapResTypeDatas_["channelname"] = "Channel";
	mapResTypeDatas_["tlqrec"] = "ReceiveQueue";
	mapResTypeDatas_["tlqsnd"] = "SendQueue";
	mapResTypeDatas_["weblogicJVM"] = "JVM";
	mapResTypeDatas_["weblogicJMSConn"] = "JMSConnection";

	mapResTypeDatas_["systablespace"] = "TableSpace";
	mapResTypeDatas_["weblogicServlet"] = "Servlet";

	//��ʼ�� �����Ϣָ��
	/***************************************************************************
		fullProcess,netstat,arpTable,ipRouteTable,basePortTable,
		ipAddrTable,sqlTop10CPUTime,sqlTop10DiskReads,sqlTop10BufferGets,sqlTop10ElapseTime
		sqlTop10Execute,sqlTop10SortNum,sqlTop10FullScan

		fdbTable
	***************************************************************************/
	setTablemetric_.clear();
	setTablemetric_.insert("fullProcess");
	setTablemetric_.insert("netstat");
	setTablemetric_.insert("arpTable");
	setTablemetric_.insert("ipRouteTable");
	setTablemetric_.insert("basePortTable");

	setTablemetric_.insert("ipAddrTable");
	setTablemetric_.insert("sqlTop10CPUTime");
	setTablemetric_.insert("sqlTop10DiskReads");
	setTablemetric_.insert("sqlTop10BufferGets");
	setTablemetric_.insert("sqlTop10ElapseTime");

	setTablemetric_.insert("sqlTop10Execute");
	setTablemetric_.insert("sqlTop10SortNum");
	setTablemetric_.insert("sqlTop10FullScan");

	setTablemetric_.insert("fdbTable");
	
	
}


BOOL BaseConver::CheckParam(string xmlpath, string filename, string kpifilename, string outfilepath)
{
	if( (xmlpath.empty()) || (filename.empty()) || (kpifilename.empty()) || (outfilepath.empty()) )
	{
		string templog = "error : xmlpath=[" + xmlpath + "],filename=[" + filename + "],kpifilename=[" + kpifilename + "],outfilepath=[" + outfilepath + "].";
		MYLOG(templog.c_str());
		return FALSE;
	}
	inputdata_.clear();
	inputdata_.push_back(xmlpath);
	inputdata_.push_back(filename);
	inputdata_.push_back(kpifilename);
	inputdata_.push_back(outfilepath);

	strXmlPath_ = xmlpath;
	outfilepath_ = outfilepath;
	kpifilename_ = kpifilename;
	filename_ = filename;


	//�ж��Ƿ���windows_wmi.xml�ļ�
	string tempfilename = filename_;
	int npos = tempfilename.find_last_of("\\");
	tempfilename = tempfilename.substr(npos + 1, tempfilename.length() - npos - 1);
	tempfilename = str_trim(tempfilename);

	if((tempfilename == "windows_wmi.xml") || (tempfilename == "windows_snmp.xml"))
	{
		isWindowsWmi_ = TRUE;
	}
	else
	{
		isWindowsWmi_ = FALSE;
	}


	return TRUE;
}



BOOL BaseConver::GetResourceInfo()
{
	BOOL ret = FALSE;
	VEC_STRING_DATAS devicetypeinfodata;
	devicetypeinfodata.clear();

	resourceid_.clear();
	resourcecategory_.clear();
	resourceicon_.clear();
	resourcename_.clear();
	resourcedesc_.clear();

	string switchfilename = strXmlPath_ + filename_;
	//MYLOG(switchfilename.c_str());
	if(!CheckFileExist(switchfilename))
	{
		loglog.str("");
		loglog << "�ļ��� " << switchfilename << " �����ڣ�";
		AfxMessageBox(loglog.str().c_str());
		MYLOG(loglog.str().c_str());
		return FALSE;
	}

	file<> fdoc(switchfilename.c_str());
	xml_document<>   doc;
	doc.parse<0>(fdoc.data());

	//! ��ȡ���ڵ�
	xml_node<>* root = doc.first_node();

	//! ��ȡ���ڵ��һ���ڵ�	
	for(rapidxml::xml_node<char> * devicetypeinfo = root->first_node("devicetypeinfo");
		devicetypeinfo != NULL;
		devicetypeinfo = devicetypeinfo->next_sibling())
	{

		/********************************************************************
		����devicetypeinfo�ڵ������
		**********************************************************************/
		for(rapidxml::xml_attribute<char> * attr = devicetypeinfo->first_attribute();
			attr != NULL;
			attr = attr->next_attribute())
		{
			devicetypeinfodata.push_back(UTF8ToGB(attr->value()));
		}
	}
	if(!devicetypeinfodata.empty())
	{
		ret = TRUE;
		string tempid = devicetypeinfodata[0];
		resourceid_ = replace_all_distinct(tempid, "_", "");
		resourcecategory_ = devicetypeinfodata[3];
		resourceicon_ = resourceid_ + ".png";
		resourcename_ = devicetypeinfodata[5];
		resourcedesc_ = devicetypeinfodata[5];
	}

	return ret;
}


void BaseConver::Chipolicy(MAP_VEC_DATAS &mapchipolicydata)
{
	string chipolicydatafilename = strXmlPath_ + "\\chipolicy.xml";

	//MYLOG(chipolicydatafilename.c_str());
	if(!CheckFileExist(chipolicydatafilename))
	{
		loglog.str("");
		loglog << "�ļ��� " << chipolicydatafilename << " �����ڣ�";
		AfxMessageBox(loglog.str().c_str());
		MYLOG(loglog.str().c_str());
		return ;
	}

	file<> fdoc(chipolicydatafilename.c_str());
	xml_document<>   doc;
	doc.parse<0>(fdoc.data());

	//! ��ȡ���ڵ�
	xml_node<>* root = doc.first_node();

	//! ��ȡ���ڵ��һ���ڵ�

	VEC_STRING_DATAS tmpdata;

	/**********************************************************************************************
	mapdatas��value vecֵΪ�������ݣ�
	<chipolicy childrestype="netInterface" childresname="�˿�" filtercolumn="N1ifType" 
	filtertype="hold" order="10" group="interface" groupname="�˿�" defaultfield="N1ifName" 
	imgpath="image/icon/interface.png" isbasicinfo="" shownum="9"> 
	***********************************************************************************************/
	for(rapidxml::xml_node<char> * chipolicy = root->first_node("chipolicy");
		chipolicy != NULL;
		chipolicy = chipolicy->next_sibling())
	{
		//loglog.str("");
		tmpdata.clear();
		for(rapidxml::xml_attribute<char> * attr = chipolicy->first_attribute();
			attr != NULL;
			attr = attr->next_attribute())
		{
			loglog << "[" << UTF8ToGB(attr->name()) << "=" << UTF8ToGB(attr->value()) << "]" << flush;
			tmpdata.push_back(UTF8ToGB(attr->value()));
		}
		//MYLOG(loglog.str().c_str());
		mapchipolicydatas_[tmpdata[0]] = tmpdata;
	}

	return;
}


void BaseConver::GetKpiDatas(MAP_VEC_DATAS &mapdatas)
{
	string KPIfilename = strXmlPath_ + kpifilename_;

	MYLOG(KPIfilename.c_str());
	if(!CheckFileExist(KPIfilename))
	{
		loglog.str("");
		loglog << "�ļ��� " << KPIfilename << " �����ڣ�";
		AfxMessageBox(loglog.str().c_str());
		MYLOG(loglog.str().c_str());
		return ;
	}

	file<> fdoc(KPIfilename.c_str());
	xml_document<>   doc;
	doc.parse<0>(fdoc.data());

	//! ��ȡ���ڵ�
	xml_node<>* root = doc.first_node();

	//! ��ȡ���ڵ��һ���ڵ�
	//stringstream tmplog("");
	VEC_STRING_DATAS tmpdata;

	/**********************************************************************************************
	mapdatas��value vecֵΪ�������ݣ�
	kpiid="Availability" kpiname_en="Availability" kpiname_cn="������" isdefault="y"
	kpitype="avail" isprivate="n" reslevel="main" childrestype="" ischildresid=""
	ischildresname="" childcolumnorder="" childroworcer="" kpiorder="1010"
	showpretreatment="" kpigroup="" automonitor="y" columnorder=""
	showtype=""   alerttype="101005" istooltip="" 
	***********************************************************************************************/
	for(rapidxml::xml_node<char> * kpiinfo = root->first_node("kpiinfo");
		kpiinfo != NULL;
		kpiinfo = kpiinfo->next_sibling())
	{
		//tmplog.str("");
		tmpdata.clear();
		for(rapidxml::xml_attribute<char> * attr = kpiinfo->first_attribute();
			attr != NULL;
			attr = attr->next_attribute())
		{
			//tmplog << "[" << attr->name() << "=" << UTF8ToGB(attr->value()) << "]" <<flush;
			tmpdata.push_back(UTF8ToGB(attr->value()));
		}
		//MYLOG(tmplog.str().c_str());
		mapdatas[tmpdata[0]] = tmpdata;

		if( ((tmpdata[8] == "Y") || (tmpdata[8] == "y"))
			&& (!tmpdata[7].empty()) )
		{
			mapKPIchildDatas_[tmpdata[7]] = tmpdata[0];
		}
	}

	return;
}


string BaseConver::ChangeKpiid(string kpiid)
{
	string retkpiid = kpiid;
	char ch1 = kpiid[0];
	char ch2 = kpiid[1];

	string kpitype("");

	//�����һ���ַ���Ӣ����ĸ���ڶ����ַ������֣�ֱ��ȥ��ǰ��λ
	if( (((ch1 >= 'a') && (ch1 <='z')) || ((ch1 >= 'A') && (ch1 <='Z')))
		&& ((ch2 >= '0') && (ch2 <= '9')))
	{
		kpitype = kpiid.substr(0, 2);
		retkpiid = kpiid.substr(2, kpiid.length() -2);
	}
	else if( ((ch1 == 'A') && (ch2 == 'B'))			//middleware/resin.xml
			|| ((ch1 == 'M') && (ch2 == 'A')) 		//middleware/iis.xml
			|| ((ch1 == 'A') && (ch2 == 'A')) )		//middleware/resin.xml
	{
		kpitype = kpiid.substr(0, 2);
		retkpiid = kpiid.substr(2, kpiid.length() -2);
	}

	//cpuUsage��ΪcpuRate
	//	memUsage��ΪmemRate
	//	Availability��Ϊavailability
	// PingResponseTime ��Ϊ icmpDelayTime
	string tempkpiid = retkpiid;
	tempkpiid = LetterTolower(tempkpiid);

	if(retkpiid == "cpuUsage")
	{
		if(kpitype == "D1")
		{
			retkpiid = "appCpuRate";
		}
		else
		{
			retkpiid = "cpuRate";
		}		
	}
	else if(retkpiid == "memUsage")
	{
		
		if(kpitype == "D1")
		{
			retkpiid = "appMemRate";
		}
		else
		{
			retkpiid = "memRate";
		}
	}
	else if((retkpiid == "Availability") || (tempkpiid == "instanceavail") || (tempkpiid == "serviceavail"))
	{
		retkpiid = "availability";
	}
	else if (retkpiid == "PingResponseTime")
	{
		retkpiid = "icmpDelayTime";
	}
	else if((retkpiid == "Name") || (retkpiid == "HostName"))
	{
		retkpiid = "hostName";
	}
	else if(tempkpiid == "hostname")
	{
		if(ismodifyhostname_)
		{
			retkpiid = "Name";
		}		
	}
	else if((tempkpiid == "cpurate") || (tempkpiid == "iiscpurate"))
	{
		retkpiid = "appCpuRate";
	}
	else if((tempkpiid == "memrate") || (tempkpiid == "iismemrate"))
	{
		retkpiid = "appMemRate";
	}
	else if((retkpiid == "ipAddress") || (retkpiid == "allIpAddress"))
	{
		//�����������ipAddress��allIpAddress������ָ�꣬��ָ�����Ƹ�Ϊip
		if(ch1 == 'H')
		{
			retkpiid = "ip";
		}
	}

	return retkpiid;
}



string BaseConver::ChangePluginid(string pluginid)
{
	string retpluginid("");

	retpluginid = FirstLetterToupper(LetterTolower(pluginid)) + "Plugin";
	return retpluginid;

}

string& BaseConver::ChangeTranslations(string &translationsdata, string sourcevalue, string destvalue)
{
	string tempsourcelower = sourcevalue;
	tempsourcelower = str_trim(LetterTolower(tempsourcelower));
	//if(tempsourcelower == "$source")
	//{
	//	//����������ת��
	//}
	//else if(tempsourcelower == "default")
	if(tempsourcelower == "default")
	{
		translationsdata = translationsdata + destvalue + ";";
	}
	else
	{
		translationsdata = translationsdata + sourcevalue + "," + destvalue + ";";
	}

	return translationsdata;
}


string BaseConver::FirstLetterToupper(string strvalue)
{
	string retstr("");
	retstr = strvalue[0];
	retstr = LetterToupper(retstr);
	retstr = retstr + strvalue.substr(1, strvalue.length() -1);

	return retstr;
}


string BaseConver::GetMethodType(string cmdvalues)
{
	//cmdvalues  1.3.6.1.2.1.2.2.1.11:1.3.6.1.2.1.31.1.1.1.7:0
	//cmdvalues  1.3.6.1.2.1.2.2.1.11:1.3.6.1.2.1.31.1.1.1.7
	//cmdvalues  1.3.6.1.2.1.2.2.1.0
	string strmethod("walk");
	string cmdlast1("");
	string cmdlast2("");
	string cmdlast3("");
	string cmdlast4("");
	int length = 0;

	length = cmdvalues.length();

	if(length < 2)
	{
		//û�����壬�����ܳ���
		strmethod = "walk";
	}
	else if((length >= 2) && (length < 4))
	{
		cmdlast1 = cmdvalues[length - 1];
		cmdlast2 = cmdvalues[length - 2];

		//���������ܣ�Ҳû������
		if( (cmdlast1 == "0") && (cmdlast2 == ".") )
		{
			strmethod = "get";
		}
	}
	else
	{
		cmdlast1 = cmdvalues[length - 1];
		cmdlast2 = cmdvalues[length - 2];
		cmdlast3 = cmdvalues[length - 3];
		cmdlast4 = cmdvalues[length - 4];
		if( (cmdlast1 == "0") && (cmdlast2 == ".") 
			|| (cmdlast1 == "0") && (cmdlast2 == ":") && (cmdlast3 == "0") && (cmdlast4 == "."))
		{
			strmethod = "get";
		}
		else
		{
			strmethod = "walk";
		}
	}

	return strmethod;
}

void BaseConver::CheckIsIndex(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string &childindex, BOOL ishostsnmp)
{
	//�ж�ָ��id�Ƿ���ȷ
	VEC_STRING_DATAS kpiinfo;
	kpiinfo.clear();

	string tmpkpiid = resourceattr[0];
	kpiinfo = mapkpidatas_[tmpkpiid];
	if(kpiinfo.size() < KPI_INFO_COUNT)
	{
		loglog.str("");
		loglog << "kpiid[" << tmpkpiid << "] is not in kpi_info.xml." << flush;
		MYLOG(loglog.str().c_str());
		return ;
	}

	string reslevel = kpiinfo[6];
	string childrestype = kpiinfo[7];
	string ischildresid = LetterTolower(kpiinfo[8]);
	string ischildresname = LetterTolower(kpiinfo[9]);
	string cmdvalues("");

	if(reslevel != "child")
	{
		return ;
	}

	//��childindex��ֵ
	if(childrestype == "netInterface")
	{
		childindex = "ifIndex";
	}
	else
	{
		childindex = childrestype + "Index";
	}

	//��ischildresid��ischildresname����y����������Դ��indexָ��
	if( (ischildresid == "y") && (ischildresname == "y") )
	{
		SET_STRING_DATAS::iterator it = setChildIndexDatas_.find(childrestype);
		if( it != setChildIndexDatas_.end() )
		{
			//�ҵ��ˣ������κδ���
		}
		else
		{
			string tempid = ChangeKpiid(resourceattr[0]);
			if(tempid == childindex)
			{
				//�������ָ���Ѿ����ڣ��Ͳ���������
				MYLOG(childindex.c_str());
				return ;
			}

			if(!collectattr.empty())
			{
				VEC_VEC_DATAS::iterator itcmdid = collectattr.begin();
				VEC_STRING_DATAS veccmd;
				veccmd.clear();

				string tmpcmdid = itcmdid->at(1);

				//����cmd�м���ð�ŵ�
				VEC_STRING_DATAS veccmdid;
				veccmdid.clear();
				if(IsMultiCmdid(tmpcmdid, veccmdid))
				{
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

					if(veccmd.size() >= 3)
					{
						cmdvalues = veccmd[2];
					}
					else
					{
						//cmdid ����ȷ��ֱ�ӷ�������
						MYLOG(tmpcmdid.c_str());
					}				
				}
			}
			else
			{
				//
				string loglog = "resource[0] = [" + resourceattr[0] + "], collectattr is empty.";
				MYLOG(loglog.c_str());
				return ;
			}

			string temppluginid = ChangePluginid(resourceattr[1]);
			VEC_STRING_DATAS metricplugindata;
			metricplugindata.clear();			
			//{"metricid", "pluginid", "collectType", "�ɼ�����", "���type", "���classKey", "typeֵ", "keyֵ", "valueֵ", "columnֵ"};
			metricplugindata.push_back(childindex);
			metricplugindata.push_back(temppluginid);
			metricplugindata.push_back("");
			metricplugindata.push_back("PluginParameter");
			metricplugindata.push_back("ArrayType");			
			metricplugindata.push_back("");		//���classKey			
			metricplugindata.push_back("");		//typeֵ			
			metricplugindata.push_back("method");	//keyֵ			
			metricplugindata.push_back(GetMethodType(cmdvalues));	//valueֵ			
			metricplugindata.push_back(childindex);	//columnֵ
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);

			metricplugindata.clear();			
			metricplugindata.push_back(childindex);
			metricplugindata.push_back(temppluginid);
			metricplugindata.push_back("");
			metricplugindata.push_back("PluginParameter");
			metricplugindata.push_back("ArrayType");			
			metricplugindata.push_back("");		//���classKey			
			metricplugindata.push_back("");		//typeֵ			
			metricplugindata.push_back("");	//keyֵ			
			metricplugindata.push_back(cmdvalues);	//valueֵ			
			metricplugindata.push_back(childindex);	//columnֵ
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(metricplugindata);

			if(ishostsnmp)
			{
				string selectvalue = "SELECT " + childindex;
				metricplugindata.clear();
				metricplugindata.push_back(childindex);
				metricplugindata.push_back(temppluginid);
				metricplugindata.push_back("");
				metricplugindata.push_back("PluginDataHandler");		
				metricplugindata.push_back("");	//���type		
				metricplugindata.push_back("selectProcessor");	//���classKey		
				metricplugindata.push_back("");	//typeֵ		
				metricplugindata.push_back("SELECT");	//keyֵ
				metricplugindata.push_back(selectvalue);	//valueֵ
				metricplugindata.push_back(childindex);	//columnֵ		
				//дһ�����������ݵ�Excel�ڴ�������
				MetricPluginDatas_.push_back(metricplugindata);
			}

			metricplugindata.clear();
			metricplugindata.push_back(childindex);
			metricplugindata.push_back(temppluginid);
			metricplugindata.push_back("");
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back("");	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back("");	//typeֵ		
			metricplugindata.push_back("IndexColumnTitle");	//keyֵ		
			metricplugindata.push_back(childindex);	//valueֵ		
			metricplugindata.push_back(childindex);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);

			metricplugindata.clear();
			metricplugindata.push_back(childindex);
			metricplugindata.push_back(temppluginid);
			metricplugindata.push_back("");
			metricplugindata.push_back("PluginDataConverter");		
			metricplugindata.push_back("");	//���type		
			metricplugindata.push_back("subInstConverter");	//���classKey		
			metricplugindata.push_back("");	//typeֵ		
			metricplugindata.push_back("ValueColumnTitle");	//keyֵ	
			metricplugindata.push_back(childindex);	//valueֵ
			metricplugindata.push_back(childindex);	//columnֵ		
			MetricPluginDatas_.push_back(metricplugindata);

			
			//metricplugindata.clear();
			//metricplugindata.push_back(childindex);
			//metricplugindata.push_back(temppluginid);
			//metricplugindata.push_back("");
			//metricplugindata.push_back("PluginDataConverter");		
			//metricplugindata.push_back("");	//���type		
			//metricplugindata.push_back("subInstConverter");	//���classKey		
			//metricplugindata.push_back("ResourceProperty");	//typeֵ		
			//metricplugindata.push_back("InstPropertyKey");	//keyֵ		
			//metricplugindata.push_back(childindex);	//valueֵ		
			//metricplugindata.push_back(childindex);	//columnֵ		
			//MetricPluginDatas_.push_back(metricplugindata);
				

			//����resourceֵ
			VEC_STRING_DATAS resourceinfo;
			resourceinfo.clear();

			string resourcetype("");
			MAP_STRING_DATAS::iterator itrestype = mapResTypeDatas_.find(childrestype);
			if(itrestype != mapResTypeDatas_.end())
			{
				resourcetype = mapResTypeDatas_[childrestype];
			}
			else
			{
				resourcetype = FirstLetterToupper(childrestype);
			}

			VEC_STRING_DATAS vecchipolicy = mapchipolicydatas_[childrestype];
			string subresourceid("");
			string indexname("");
			if(!vecchipolicy.empty())
			{
				subresourceid = resourceid_ + FirstLetterToupper(childrestype);
				resourceinfo.push_back(subresourceid);
				resourceinfo.push_back("");
				resourceinfo.push_back("");
				resourceinfo.push_back(vecchipolicy[1]);
				resourceinfo.push_back(vecchipolicy[1]);
				resourceinfo.push_back(resourcetype);
				resourceinfo.push_back(resourceid_);
				indexname = vecchipolicy[1] + "����";
			}
			else
			{
				subresourceid = resourceid_ + FirstLetterToupper(childrestype);
				resourceinfo.push_back(subresourceid);
				resourceinfo.push_back("");
				resourceinfo.push_back("");
				resourceinfo.push_back(childrestype);
				resourceinfo.push_back(childrestype);
				resourceinfo.push_back(resourcetype);
				resourceinfo.push_back(resourceid_);
				indexname = childrestype + "����";
			}

			string kpitype = kpiinfo[4];
			int nkpitype = 0;
			resourceinfo.push_back(childindex);
			resourceinfo.push_back("InformationMetric");
			resourceinfo.push_back(indexname);
			resourceinfo.push_back(indexname);
			resourceinfo.push_back("");
			resourceinfo.push_back("false");
			resourceinfo.push_back(kpiinfo[12]);
			resourceinfo.push_back("false");
			resourceinfo.push_back("false");
			resourceinfo.push_back("day1");
			resourceinfo.push_back("hour1,day1");			
			resourceinfo.push_back("false");		//IsAlert			
			resourceinfo.push_back("1");		//DefaultFlapping

			ResourceDatas_.push_back(resourceinfo);

			//������Դ���ӵ�set��
			setChildIndexDatas_.insert(childrestype);


			//����PropertiesData_
			SET_STRING_DATAS::iterator it = PropertiesData_.find(subresourceid + childindex);
			if(it == PropertiesData_.end())
			{
				//û���ҵ������ԣ��� ����������
				VEC_STRING_DATAS vecpro;
				vecpro.clear();

				vecpro.push_back(subresourceid);
				vecpro.push_back(childindex);
				vecpro.push_back(childindex);
				vecpro.push_back(childindex);
				PropertiesDatas_.push_back(vecpro);

				PropertiesData_.insert(subresourceid + childindex);
			}

			MAP_STRING_DATAS::iterator it2 = mapinstantiationId_.find(subresourceid);
			if(it2 == mapinstantiationId_.end())
			{
				string newvalue = "," + childindex + ",";
				mapinstantiationId_[subresourceid] = newvalue;
			}
		}

	}

	return ;

}



BOOL BaseConver::IsMultiCmdid(const string tmpcmdid, VEC_STRING_DATAS &veccmdid)
{
	veccmdid = split_str_bychar(tmpcmdid, ':');
	if(veccmdid.size() < 2)
	{
		//δ�ҵ�ð��
		return FALSE;
	}

	return TRUE;
}

void BaseConver::ChangeColon(string &valuedata)
{
	//��ð���滻Ϊ�»��ߣ���������:0�����滻
	valuedata = replace_all_distinct(valuedata, ":", "_");
	int length = valuedata.length();
	if((valuedata[length - 1] == '0') && (valuedata[length - 2] == '_'))
	{
		//valuedata[length - 2] = ':';
		valuedata = valuedata.substr(0, valuedata.length() - 2);
	}
}


void BaseConver::GetParam(const string resourcevalue, SET_INT_DATAS &setdatas)
{
	if(resourcevalue.empty())
	{
		return ;
	}
	string resource = resourcevalue;
	string findstr("$d");
	int nposbegin = resource.find(findstr);
	int strleng = resource.length();

	while(nposbegin >= 0)
	{
		nposbegin = nposbegin + 2;
		int nposend = nposbegin;
		if(nposend >= strleng)
		{
			break;
		}
		while(nposend < strleng)
		{
			if(!IsNumber(resource[nposend]))
			{
				break;
			}
			nposend++;
		}
		string tempvalue("");
		tempvalue = resource.substr(nposbegin, nposend - nposbegin);
		int insertvalue = atoi(tempvalue.c_str());
		setdatas.insert(insertvalue);
		nposbegin = resource.find(findstr, nposend);		
	}
}



BOOL BaseConver::IsRepeatMetricid(const string metricid, const string collecttype, int flag)
{
	//����Ѿ�����ָ��id�������κδ���
	//���û���ҵ�����idд�뵽vec��

	string tempmetricid = ChangeKpiid(metricid);
	switch(flag)
	{
	case 1:
		//����collect.xml�е�ָ��
		{
			string metrickey = tempmetricid + collecttype;
			SET_STRING_DATAS::iterator it = MetricidCollectDatas_.find(metrickey);
			if( it != MetricidCollectDatas_.end())
			{
				return TRUE;  
			}
			MetricidCollectDatas_.insert(metrickey);
		}
		break;
	case 2:
		//����resource.xml�е�ָ��
		{
			SET_STRING_DATAS::iterator it = MetricidResourceDatas_.find(tempmetricid);
			if( it != MetricidResourceDatas_.end())
			{
				return TRUE;
			}

			MetricidResourceDatas_.insert(tempmetricid);
		}
		break;
	default:
		break;
	}

	//������ָ���У�ָ�� H1cpuName����������Ϊ��H1cpuId�ظ��ˣ�ֱ�Ӷ���
	//if( metricid == "H1cpuName")
	//{
	//	return TRUE;
	//}

	return FALSE;
}


void BaseConver::PrepareResourceSubData(string kpiid, string subresourceid)
{
	VEC_STRING_DATAS kpiinfo;
	kpiinfo.clear();
	kpiinfo = mapkpidatas_[kpiid];

	string ischildresid = LetterTolower(kpiinfo[8]);
	string ischildresname = LetterTolower(kpiinfo[9]);
	string newkpiid = ChangeKpiid(kpiid);

	if(newkpiid.empty())
	{
		return;
	}

	if(ischildresid == "y")
	{
		//SetPropertiesData();

		SET_STRING_DATAS::iterator it = PropertiesData_.find(subresourceid + newkpiid);
		if(it == PropertiesData_.end())
		{
			//û���ҵ������ԣ��� ����������
			VEC_STRING_DATAS vecpro;
			vecpro.clear();

			vecpro.push_back(subresourceid);
			vecpro.push_back(newkpiid);
			vecpro.push_back(newkpiid);
			vecpro.push_back(newkpiid);
			PropertiesDatas_.push_back(vecpro);

			PropertiesData_.insert(subresourceid + newkpiid);
		}

		MAP_STRING_DATAS::iterator it2 = mapinstantiationId_.find(subresourceid);
		if(it2 == mapinstantiationId_.end())
		{
			string newvalue = "," + newkpiid + ",";
			mapinstantiationId_[subresourceid] = newvalue;
		}

	}

	if(ischildresname == "y")
	{
		MAP_STRING_DATAS::iterator it = mapinstantiationName_.find(subresourceid);
		if(it == mapinstantiationName_.end())
		{
			string newvalue = newkpiid;
			mapinstantiationName_[subresourceid] = newvalue;
		}

	}

}

BOOL BaseConver::ParsePluginDataHandlerValue(string &resourcevalue, SET_STRING_DATAS &replacedatas)
{
	BOOL ret = FALSE;
	int length = resourcevalue.length();
	int npos = resourcevalue.find("$");
	int endpos = 0;

	replacedatas.clear();
	while(npos >= 0)
	{
		string replacevalue("");
		string newdata("");
		for(endpos = npos + 1; endpos < length; endpos++)
		{
			if(!IsNumberAndLetter(resourcevalue[endpos]))
			{
				break;
			}
		}
		if(endpos > (npos + 1))
		{
			ret = TRUE;
			//��$����ȡ��
			replacevalue = resourcevalue.substr(npos + 1, endpos - npos - 1);

			//��$����ת��Ϊָ�����
			newdata = "{" + ChangeKpiid(replacevalue) + "}";

			//�� ָ����� �滻 $����
			resourcevalue = resourcevalue.substr(0, npos + 1) + newdata + resourcevalue.substr(endpos, length - endpos);
			
			//�� $���� д�뵽replacedatas��
			replacedatas.insert(replacevalue);
		}
		if(endpos >= length)
		{
			ret = TRUE;
			replacevalue = resourcevalue.substr(npos + 1, length - npos - 1);
			newdata = "{" + ChangeKpiid(replacevalue) + "}";
			resourcevalue = resourcevalue.substr(0, npos + 1) + newdata;
			replacedatas.insert(replacevalue);
			break;
		}
		npos = resourcevalue.find("$", npos + 1 + newdata.length());
		length = resourcevalue.length();
	}

	return ret;
}

void BaseConver::InsertMainInstanceID()
{
	string basepluginid = ",TelnetPlugin,SshPlugin,WmiPlugin,";
	string otherpluginid("");
	VEC_STRING_DATAS tmpdata;

	if(setInstanceID_.empty())
	{
		return;
	}

	SET_STRING_DATAS::iterator it = setInstanceID_.begin();
	for( ; it != setInstanceID_.end(); ++it)
	{
		string temppluginid = "," + (*it) + ",";
		int npos = basepluginid.find(temppluginid);
		if(npos >= 0)
		{
			continue;
		}

		//��ȥ������3��pluginid�⣬һ���ļ��о����ֻ��һ��������pluginid
		otherpluginid = *it;
		break;
	}

	/***************************************
	******	��ʼ�� ***InstanceID ָ�� ******
	****************************************/
	//������3�л���������pluginid�ģ���ֻ�������������Ĳ�����
	//��������3������ģ��������еĻ�����pluginid
	string tempMetricid = resourcecategory_ + "InstanceID";
	if(!otherpluginid.empty())
	{
		tmpdata.clear();
		tmpdata.push_back(tempMetricid);
		tmpdata.push_back(otherpluginid);
		tmpdata.push_back("");
		tmpdata.push_back("PluginParameter");
		tmpdata.push_back("ArrayType");	//���type		
		tmpdata.push_back("");	//���classKey		
		tmpdata.push_back("DiscoveryInfo");	//typeֵ		
		tmpdata.push_back("Port");	//keyֵ
		tmpdata.push_back("Port");	//valueֵ
		tmpdata.push_back("");	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpdata);

		tmpdata.clear();
		tmpdata.push_back(tempMetricid);
		tmpdata.push_back(otherpluginid);
		tmpdata.push_back("");
		tmpdata.push_back("PluginParameter");
		tmpdata.push_back("ArrayType");	//���type		
		tmpdata.push_back("");	//���classKey		
		tmpdata.push_back("DiscoveryInfo");	//typeֵ		
		tmpdata.push_back("IP");	//keyֵ
		tmpdata.push_back("IP");	//valueֵ
		tmpdata.push_back("");	//columnֵ		
		//дһ�����������ݵ�Excel�ڴ�������
		MetricPluginDatas_.push_back(tmpdata);

		//ֱ�ӳ�ʼ��
		tmpdata.clear();
		tmpdata.push_back(resourceid_);
		tmpdata.push_back(resourcecategory_);
		tmpdata.push_back(resourceicon_);
		tmpdata.push_back(resourcename_);
		tmpdata.push_back(resourcedesc_);
		tmpdata.push_back("");
		tmpdata.push_back("");

		tmpdata.push_back(tempMetricid);
		tmpdata.push_back("InformationMetric");
		tmpdata.push_back("��Դʵ��ID");
		tmpdata.push_back("��Դʵ��ID");
		tmpdata.push_back("");

		tmpdata.push_back("false");
		tmpdata.push_back("1500");
		tmpdata.push_back("false");
		tmpdata.push_back("false");
		tmpdata.push_back("hour1");
		tmpdata.push_back("hour1,day1");
		tmpdata.push_back("false");
		tmpdata.push_back("1");

		ResourceDatas_.push_back(tmpdata);
	}
	else
	{
		SET_STRING_DATAS::iterator itpluginid = setInstanceID_.begin();
		for( ; itpluginid != setInstanceID_.end(); ++ itpluginid)
		{
			string tempCollectType = *itpluginid;
			tempCollectType = tempCollectType.substr(0, tempCollectType.length() - 6);	//�� SshPlugin �е� SshPlugin ȥ��
			tempCollectType = LetterToupper(tempCollectType);
			tmpdata.clear();
			tmpdata.push_back(tempMetricid);
			tmpdata.push_back(*itpluginid);
			tmpdata.push_back(tempCollectType);
			tmpdata.push_back("PluginParameter");
			tmpdata.push_back("ArrayType");	//���type		
			tmpdata.push_back("");	//���classKey		
			tmpdata.push_back("DiscoveryInfo");	//typeֵ		
			tmpdata.push_back("Port");	//keyֵ
			tmpdata.push_back("Port");	//valueֵ
			tmpdata.push_back("");	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(tmpdata);

			tmpdata.clear();
			tmpdata.push_back(tempMetricid);
			tmpdata.push_back(*itpluginid);
			tmpdata.push_back(tempCollectType);
			tmpdata.push_back("PluginParameter");
			tmpdata.push_back("ArrayType");	//���type		
			tmpdata.push_back("");	//���classKey		
			tmpdata.push_back("DiscoveryInfo");	//typeֵ		
			tmpdata.push_back("IP");	//keyֵ
			tmpdata.push_back("IP");	//valueֵ
			tmpdata.push_back("");	//columnֵ		
			//дһ�����������ݵ�Excel�ڴ�������
			MetricPluginDatas_.push_back(tmpdata);
		}
		

		//ֱ�ӳ�ʼ��
		tmpdata.clear();
		tmpdata.push_back(resourceid_);
		tmpdata.push_back(resourcecategory_);
		tmpdata.push_back(resourceicon_);
		tmpdata.push_back(resourcename_);
		tmpdata.push_back(resourcedesc_);
		tmpdata.push_back("");
		tmpdata.push_back("");

		tmpdata.push_back(tempMetricid);
		tmpdata.push_back("InformationMetric");
		tmpdata.push_back("��Դʵ��ID");
		tmpdata.push_back("��Դʵ��ID");
		tmpdata.push_back("");

		tmpdata.push_back("false");
		tmpdata.push_back("1500");
		tmpdata.push_back("false");
		tmpdata.push_back("false");
		tmpdata.push_back("hour1");
		tmpdata.push_back("hour1,day1");
		tmpdata.push_back("false");
		tmpdata.push_back("1");

		ResourceDatas_.push_back(tmpdata);
	}

}


void BaseConver::CheckPropertiesData(string &propertiesdata, BOOL isChild, string childrestype)
{
	string tempkey("");

	propertiesdata = ChangeKpiid(propertiesdata);

	if(propertiesdata.empty())
	{
		return;
	}

	if(isChild)
	{
		if(childrestype == "netInterface")
		{
			tempkey = resourceid_ + "NIC";
		}
		else
		{
			tempkey = resourceid_ + FirstLetterToupper(childrestype);
		}
	}
	else
	{
		tempkey = resourceid_;
	}

	SET_STRING_DATAS::iterator it = PropertiesData_.find(tempkey + propertiesdata);
	if(it == PropertiesData_.end())
	{
		//û���ҵ������ԣ��� ����������
		VEC_STRING_DATAS vecpro;
		vecpro.clear();

		vecpro.push_back(tempkey);
		vecpro.push_back(propertiesdata);
		vecpro.push_back(propertiesdata);
		vecpro.push_back(propertiesdata);
		PropertiesDatas_.push_back(vecpro);

		PropertiesData_.insert(tempkey + propertiesdata);
	}

}


BOOL BaseConver::CheckIsTableMetric(const string metricid)
{
	SET_STRING_DATAS::iterator it = setTablemetric_.find(metricid);
	if(it != setTablemetric_.end())
	{
		return TRUE;
	}

	return FALSE;

}

void BaseConver::ParseMerge(string &resourcevalue, VEC_VEC_DATAS &collectattr)
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
		for(int i = 1 ; it != collectattr.end(); ++it, i++ )
		{
			string tempmerge = it->at(3);
			if(!tempmerge.empty())
			{
				stringstream oldvalue("");
				oldvalue << "$d" << i << flush;
				string newvalue = tempmerge + "(" + oldvalue.str() + ")";
				resourcevalue = replace_all_distinct(resourcevalue, oldvalue.str(), newvalue);
			}

		}
	}
}


string& BaseConver::ModifyStr(string &datas)
{     
	//xml �� ���ں� �Ľ���
	datas = replace_all_distinct(datas, "&gt;", ">");

	//xml �� С�ں� �Ľ���
	datas = replace_all_distinct(datas, "&lt;", "<");
	return datas;
}


string BaseConver::GetKpiType(string kpitype, int &nkpitype)
{
	//������ָ��  AvailabilityMetric  kpitype=1
	//����ָ�� PerformanceMetric  kpitype=2
	//��Ϣָ�� InformationMetric  kpitype=3
	//����ָ�� ConfigurationMetric  kpitype=4
	string kpitypeinfo("");

	if(kpitype == "avail")
	{
		kpitypeinfo = "AvailabilityMetric";
		nkpitype = 1;
	}
	else if(kpitype == "perf")
	{
		kpitypeinfo = "PerformanceMetric";
		nkpitype = 2;
	}
	else if(kpitype == "info")
	{
		kpitypeinfo = "InformationMetric";
		nkpitype = 3;
	}
	else if(kpitype == "conf")
	{
		kpitypeinfo = "InformationMetric";
		nkpitype = 4;
	}
	else
	{
		//�����ָ������
		string loglog("");
		loglog = "kpitype is error. kpitype=" + kpitype;
		MYLOG(loglog.c_str());
		kpitypeinfo = kpitype;
		nkpitype = 0;
	}
	return kpitypeinfo;
}



void BaseConver::SetResourceInfo(string kpiid, string reslevel, string type, VEC_STRING_DATAS &resourceinfo)
{
	string subresourceid("");

	resourceinfo.clear();

	//����Դ
	if(reslevel == "main")
	{
		resourceinfo.push_back(resourceid_);
		resourceinfo.push_back(resourcecategory_);
		resourceinfo.push_back(resourceicon_);
		resourceinfo.push_back(resourcename_);
		resourceinfo.push_back(resourcedesc_);
		resourceinfo.push_back("");
		resourceinfo.push_back("");
	}
	else	//����Դ
	{
		string resourcetype("");

		MAP_STRING_DATAS::iterator it = mapResTypeDatas_.find(type);
		if(it != mapResTypeDatas_.end())
		{
			resourcetype = mapResTypeDatas_[type];
		}
		else
		{
			resourcetype = FirstLetterToupper(type);
		}

		VEC_STRING_DATAS vecchipolicy = mapchipolicydatas_[type];
		if(!vecchipolicy.empty())
		{
			if(type == "netInterface")
			{
				subresourceid = resourceid_ + "NIC";
				resourceinfo.push_back(subresourceid);
				resourceinfo.push_back("");
				resourceinfo.push_back("");
				resourceinfo.push_back(vecchipolicy[1]);
				resourceinfo.push_back("Interface of Network Device");
				resourceinfo.push_back(resourcetype);
				resourceinfo.push_back(resourceid_);

			}
			else
			{
				string subname = str_trim(vecchipolicy[1]);
				subresourceid = resourceid_ + FirstLetterToupper(type);
				resourceinfo.push_back(subresourceid);
				resourceinfo.push_back("");
				resourceinfo.push_back("");
				resourceinfo.push_back(subname);
				resourceinfo.push_back(subname);
				resourceinfo.push_back(resourcetype);
				resourceinfo.push_back(resourceid_);
			}
		}
		else
		{
			subresourceid = resourceid_ + FirstLetterToupper(type);
			resourceinfo.push_back(subresourceid);
			resourceinfo.push_back("");
			resourceinfo.push_back("");
			resourceinfo.push_back(type);
			resourceinfo.push_back(type);
			resourceinfo.push_back(resourcetype);
			resourceinfo.push_back(resourceid_);
		}

		PrepareResourceSubData(kpiid, subresourceid);
	}

	return ;
}



void BaseConver::ParseThreshold(int nkpitype, VEC_STRING_DATAS &resourcedata, VEC_STRING_DATAS &kpipolicyattr)
{
	string willalert("");

	willalert = LetterTolower(kpipolicyattr[3]);

	if(willalert == "y")
	{
		//IsAlert
		resourcedata.push_back("true");

		//DefaultFlapping
		resourcedata.push_back("1");

		//��Ҫ������ֵ������ Major / Minor  /Normal
		//������ָ��  AvailabilityMetric  kpitype=1
		//����ָ�� PerformanceMetric  kpitype=2
		//��Ϣָ�� InformationMetric  kpitype=3
		//����ָ�� ConfigurationMetric  kpitype=4

		//	"��ֵ����", "��ֵ", "��ֵ״̬"
		MAP_INT_STRING_DATAS thresholdValues;
		thresholdValues.clear();

		//��Ҫ����kpipolicyattr
		ParseThresholdData(thresholdValues, kpipolicyattr);

		switch(nkpitype)
		{
		case 1:
			//������ָ��
			break;
		case 2:
			if(thresholdValues.empty())
			{
				//���������ָ��Ϊ�գ�����λ����%�����޸�Ϊ��Ϣָ��
				//20141225���������ת��
				//if (resourcedata[11] != "%")
				//{
				//	//������ָ�� ��Ϊ ��Ϣָ��
				//	resourcedata[8] = "InformationMetric";
				//	resourcedata[16] = "day1";
				//	resourcedata[17] = "hour1,day1";
				//}
			}
		case 3:
		case 4:
			if(thresholdValues.empty())
			{
				//�����ָ��Ϊ�գ�����λ��%����Ĭ�Ϸ�ֵ
				if (resourcedata[11] == "%")
				{
					//��һ�鷧ֵ
					resourcedata.push_back(">=");
					resourcedata.push_back("0");
					resourcedata.push_back("Normal");
					//�ڶ��鷧ֵ
					resourcedata.push_back(">=");
					resourcedata.push_back("85");
					resourcedata.push_back("Minor");
					//�����鷧ֵ
					resourcedata.push_back(">=");
					resourcedata.push_back("90");
					resourcedata.push_back("Major");
				}
				break;
			}
			{
				MAP_INT_STRING_DATAS::iterator it = thresholdValues.begin();
				for( int i = 0; ((it != thresholdValues.end()) && (i < 3)); ++it, ++i)
				{
					stringstream valuedata;
					valuedata << it->first << flush;
					resourcedata.push_back(it->second);
					resourcedata.push_back(valuedata.str());

					switch(i)
					{
					case 0:
						resourcedata.push_back("Normal");
						break;
					case 1:
						resourcedata.push_back("Minor");
						break;
					case 2:
						resourcedata.push_back("Major");
						break;
					default:
						//�����ܽ�������Ϊi����С��3
						break;
					}

				}
			}


			break;
		default:
			//ָ�����Ͳ���ȷ�ģ���д�κ�����
			break;
		}
	}
	else
	{
		//IsAlert
		resourcedata.push_back("false");

		//DefaultFlapping
		resourcedata.push_back("1");

		//�����û��ָ��ģ�����λ��%����Ĭ�Ϸ�ֵ
		if (resourcedata[11] == "%")
		{
			//��һ�鷧ֵ
			resourcedata.push_back(">=");
			resourcedata.push_back("0");
			resourcedata.push_back("Normal");
			//�ڶ��鷧ֵ
			resourcedata.push_back(">=");
			resourcedata.push_back("85");
			resourcedata.push_back("Minor");
			//�����鷧ֵ
			resourcedata.push_back(">=");
			resourcedata.push_back("90");
			resourcedata.push_back("Major");
		}
		else
		{
			//���������ָ��Ϊ�գ�����λ����%�����޸�Ϊ��Ϣָ��
			//20141225���������ת��
			//if(nkpitype == 2)
			//{
			//	resourcedata[8] = "InformationMetric";
			//	resourcedata[16] = "day1";
			//	resourcedata[17] = "hour1,day1";
			//}
		}

	}


	if(resourcedata[7] == "icmpDelayTime")
	{
		resourcedata[8] = "PerformanceMetric";
		resourcedata[16] = "min5";
		resourcedata[17] = "min5,hour1,day1";

		//��һ�鷧ֵ
		resourcedata.push_back("<");
		resourcedata.push_back("500");
		resourcedata.push_back("Normal");
		//�ڶ��鷧ֵ
		resourcedata.push_back(">");
		resourcedata.push_back("1000");
		resourcedata.push_back("Minor");
		//�����鷧ֵ
		resourcedata.push_back(">");
		resourcedata.push_back("3000");
		resourcedata.push_back("Major");
	}
}


void BaseConver::ParseThresholdData(MAP_INT_STRING_DATAS &thresholdValues, VEC_STRING_DATAS &kpipolicyattr)
{
	int nkey = 0;
	string strkey("");
	string strvalue("");
	//���5��ָ��,��ѯ�������5��
	for (int i = 0; i < 5; i++)
	{
		int npos = 0;
		int npos1 = -1;
		int npos2 = -1;
		int npos3 = -1;
		int npos4 = -1;
		int npos5 = -1;
		int offset = 0;
		int endpos = 0;
		string alertexp = ModifyStr(kpipolicyattr[4 + i*2]);
		BOOL iscorrect = FALSE;

		strkey.clear();
		strvalue.clear();

		if(alertexp.empty())
		{
			continue;
		}

		//MYLOG(alertexp.c_str());
		npos1 = alertexp.find(">=");
		npos2 = alertexp.find("<=");
		npos3 = alertexp.find(">");
		npos4 = alertexp.find("<");
		npos5 = alertexp.find("=");

		if(npos1 >= 0)
		{
			npos = npos1;
			strvalue = ">=";
			offset = 2;
		}
		else if(npos2 >= 0)
		{
			npos = npos2;
			strvalue = "<=";
			offset = 2;
		}
		else if(npos3 >= 0)
		{
			npos = npos3;
			strvalue = ">";
			offset = 1;
		}
		else if(npos4 >= 0)
		{
			npos = npos4;
			strvalue = "<";
			offset = 1;
		}
		else if(npos5 >= 0)
		{
			npos = npos5;
			strvalue = "=";
			offset = 1;
		}
		else
		{
			//û���ҵ��κ�ֵ
			continue;
		}

		for(endpos = npos + offset; endpos < (int)alertexp.length(); endpos++)
		{
			if(IsNumber(alertexp[endpos]))
			{
				iscorrect = TRUE;
				continue;
			}
		}
		if(iscorrect)
		{
			strkey = alertexp.substr(npos + offset, endpos - npos - offset);
			thresholdValues.insert(make_pair(atoi(strkey.c_str()), strvalue));
		}

	}
}


void BaseConver::PrepareResourceAllData(VEC_STRING_DATAS &deviceattr, VEC_STRING_DATAS &resourceattr, VEC_STRING_DATAS &kpipolicyattr)
{
	/*************************************************************************************************************
	resource sheet ҳ���������
	"��Դid", "category", "icon", "��Դ����name", "��Դ����description", "��Դ����type", "����Դparentid", 
	"ָ��id", "ָ��style", "ָ��name", "ָ������description", "ָ�����unit", 
	"�Ƿ�չʾIsDisplay", "չʾ����displayOrder", "�Ƿ���IsMonitor", "�Ƿ�༭IsEdit", "Ĭ�ϼ��Ƶ��DefaultMonitorFreq", 
	"֧�ּ��Ƶ��SupportMonitorFreq", "�Ƿ�澯IsAlert", "DefaultFlapping", 
	�������3��ֵ����ѭ������	"��ֵ����", "��ֵ", "��ֵ״̬" �����3������
	****************************************************************************************************************/

	string tempnullstr("");
	string ischildresid("");
	string ischildresname("");
	string automonitor("");
	VEC_STRING_DATAS resourcedata;
	VEC_STRING_DATAS kpiinfo;

	resourcedata.clear();
	kpiinfo.clear();

	if(IsRepeatMetricid(resourceattr[0], resourceattr[1], 2))
	{
		loglog.str("");
		loglog << "warn : resourceattr[0] = [" << resourceattr[0] << "],resourceattr[1] = " << resourceattr[1] << "] is already exist(ResourceAllData).";
		MYLOG(loglog.str().c_str());
		return;
	}

	string resourceid = deviceattr[0];

	string kpiid = resourceattr[0];
	kpiinfo = mapkpidatas_[kpiid];
	if(kpiinfo.size() < KPI_INFO_COUNT)
	{
		loglog.str("");
		loglog << "kpiid[" << kpiid << "] is not in kpi_info.xml." << flush;
		MYLOG(loglog.str().c_str());
		return ;
	}
	
	/***********************************************************
	//��Դ��Ϣ����
	*************************************************************/
	string reslevel = kpiinfo[6];
	string childrestype = kpiinfo[7];

	SetResourceInfo(kpiid, reslevel, childrestype, resourcedata);

	/***********************************************************
	//ָ����Ϣ����
	*************************************************************/
	string kpitype = kpiinfo[4];
	int nkpitype = 0;
	string tempMetricid = str_trim(ChangeKpiid(kpiid));
	resourcedata.push_back(tempMetricid);
	resourcedata.push_back(GetKpiType(kpitype, nkpitype));

	string metricname = str_trim(kpiinfo[2]);
	if(isWindowsWmi_)
	{
		if(tempMetricid == "fileSysName")
		{
			resourcedata.push_back("��������");
			resourcedata.push_back("��������");
		}
		else
		{
			resourcedata.push_back(metricname);
			resourcedata.push_back(metricname);
		}
	}
	else
	{
		resourcedata.push_back(metricname);
		resourcedata.push_back(metricname);
	}

	resourcedata.push_back(resourceattr[4]);

	/***********************************************************
	//ָ������Ϣ
	*************************************************************/
	ischildresid = kpiinfo[8];
	ischildresid = LetterTolower(ischildresid);
	ischildresname = kpiinfo[9];
	ischildresname = LetterTolower(ischildresname);
	automonitor = kpiinfo[15];
	automonitor = LetterTolower(automonitor);
	
	//IsDisplay 
	//�� ischildresid = y && ischildresname == n��  IsDisplay=false
	// �������Ϊtrue
	if((ischildresid == "y") && (ischildresname == "n"))
	{
		resourcedata.push_back("false");
	}
	else
	{
		 if((tempMetricid == "fullProcess") || (tempMetricid == "netstat"))
		 {
			 resourcedata.push_back("false");
		 }
		 else
		 {
			 resourcedata.push_back("true");
		 }		
	}
	
	//displayOrder
	resourcedata.push_back(kpiinfo[12]);

	//IsMonitor
	//�� automonitor = y��� &&  ischildresid != y�� IsMonitor = true
	//�������Ϊ flase
	/*if( ( (automonitor == "y") || (automonitor.empty()) )
		&& (ischildresid != "y") )*/
	if(ischildresid != "y")
	{
		resourcedata.push_back("true");
	}
	else
	{
		resourcedata.push_back("false");
	}

	//IsEdit
	resourcedata.push_back("false");


	//��Ҫ������ֵ������ Major / Minor  /Normal
	//������ָ��  AvailabilityMetric  kpitype=1
	//����ָ�� PerformanceMetric  kpitype=2
	//��Ϣָ�� InformationMetric  kpitype=3
	//����ָ�� ConfigurationMetric  kpitype=4
	switch(nkpitype)
	{
	case 1:
		resourcedata.push_back("min1");
		resourcedata.push_back("min1,min5,hour1");
		break;
	case 2:
		resourcedata.push_back("min5");
		resourcedata.push_back("min5,hour1,day1");
		break;
	case 3:
	case 4:
		resourcedata.push_back("day1");
		resourcedata.push_back("hour1,day1");
		break;
	default:
		resourcedata.push_back(tempnullstr);
		resourcedata.push_back(tempnullstr);
		break;
	}


	/***********************************************************
	//��ֵ��Ϣ����
	*************************************************************/
	ParseThreshold(nkpitype, resourcedata, kpipolicyattr);

	//������д�뵽Excel���ڴ���
	ResourceDatas_.push_back(resourcedata);

}


void BaseConver::CheckProperties()
{
	if(!mapinstantiationName_.empty())
	{
		MAP_STRING_DATAS::iterator it = mapinstantiationName_.begin();
		for( ; it != mapinstantiationName_.end(); ++it)
		{
			string resourceid = it->first;
			string instantname = it->second;
			if(instantname.empty())
			{
				continue;
			}

			SET_STRING_DATAS::iterator it2 = PropertiesData_.find(resourceid + instantname);
			if(it2 == PropertiesData_.end())
			{
				//û���ҵ������ԣ��� ����������
				VEC_STRING_DATAS vecpro;
				vecpro.clear();
				vecpro.push_back(resourceid);
				vecpro.push_back(instantname);
				vecpro.push_back(instantname);
				vecpro.push_back(instantname);
				PropertiesDatas_.push_back(vecpro);
				PropertiesData_.insert(resourceid + instantname);

			}
		}
	}
}

void BaseConver::PreperDatasForWrite()
{

	SortVecVec(ResourceDatas_);

	CheckProperties();
	SortVecVec(PropertiesDatas_);

	if(!mapinstantiationId_.empty())
	{
		MAP_STRING_DATAS::iterator it = mapinstantiationId_.begin();
		for( ; it != mapinstantiationId_.end(); ++it)
		{
			VEC_STRING_DATAS tempvec;
			tempvec.clear();

			string resourceid = it->first;
			string instantiationid = it->second;

			//ȥ��ǰ��Ķ���
			instantiationid = instantiationid.substr(1, instantiationid.length() - 2);

			string instantiationname = mapinstantiationName_[resourceid];
			
			if(instantiationname.empty())
			{
				instantiationname = instantiationid;
			}

			tempvec.push_back(resourceid);
			tempvec.push_back(instantiationid);
			tempvec.push_back(instantiationname);

			InstantiationDatas_.push_back(tempvec);

		}
	}

	//SortVecVec(InstantiationDatas_);
}



void BaseConver::WriteExcel()
{

	BOOL ret = FALSE;

	string excelpathfile = strXmlPath_ + outfilepath_ + ".xlsx";

	gbookhandle.DeleteExcelFile(excelpathfile);

	VEC_STRING_DATAS vecsheetname;
	MAP_VEC_DATAS ExcleSheetinfo;

	vecsheetname.clear();
	ExcleSheetinfo.clear();

	ExcleSheetinfo = g_GlobalData.Excleinfo_;
	vecsheetname = g_GlobalData.Sheetnameinfo_;

	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[0], ExcleSheetinfo[vecsheetname[0]], CGlobalMetricSettingDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}
	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[1], ExcleSheetinfo[vecsheetname[1]], PluginClassAliasListDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}

	/*********************************************************************
	**	д�ո�,Ϊ�˽���� excle ����󳤶�Ϊ255������
	**	��ΪĬ�϶�excel��ֻɨ��ǰ���У�����ȷ����������ռ䣬
	**	���Ҫ�޸ģ���Ҫ��ע������ｫ��һ�е�ֱֵ��д��512�����������
	***********************************************************************/
	string oldfirstcommandvalue("");
	string newfirstcommandvalue("");
	if(!MetricPluginDatas_.empty())
	{
		oldfirstcommandvalue = MetricPluginDatas_[0][8];
		newfirstcommandvalue = oldfirstcommandvalue;
		for(int i = oldfirstcommandvalue.length(); i < 512; i++)
		{
			newfirstcommandvalue += " ";
		}
		MetricPluginDatas_[0][8] = newfirstcommandvalue;
	}
	/*      �Ƚ� MetricPluginDatas_ �еĵ�һ�е� valueֵ �޸�Ϊ512��С       */
	

	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[2], ExcleSheetinfo[vecsheetname[2]], MetricPluginDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}

	/*      �� MetricPluginDatas_ �еĵ�һ�е� valueֵ ��ԭΪԭ���Ĵ�С       */
	oldfirstcommandvalue = str_trim(oldfirstcommandvalue);
	MetricPluginDatas_[0][8] = oldfirstcommandvalue;

	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[3], ExcleSheetinfo[vecsheetname[3]], RGlobalMetricSettingDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}

	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[4], ExcleSheetinfo[vecsheetname[4]], ResourceDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}

	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[5], ExcleSheetinfo[vecsheetname[5]], PropertiesDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}

	ret = gbookhandle.WriteSheet(excelpathfile, vecsheetname[6], ExcleSheetinfo[vecsheetname[6]], InstantiationDatas_);
	if(!ret)
	{
		MYLOG("write sheet is error.");
		g_GlobalData.g_faildatas.push_back(inputdata_);
		return ;
	}

	loglog.str("");
	loglog << "����Excel�ɹ���·���� " << excelpathfile << flush;
	MYLOG(loglog.str().c_str());
	//AfxMessageBox(loglog.str().c_str());

}

void BaseConver::WriteCollectXml()
{
	//����ļ�
	xml_doc_.clear();

	//д�ļ�ͷ
	xml_node<>* rot = xml_doc_.allocate_node(rapidxml::node_pi,xml_doc_.allocate_string("xml version=\"1.0\" encoding=\"UTF-8\""));
	xml_doc_.append_node(rot);

	//����MetricPlugins��ǩ
	xml_node<>* MetricPlugins = xml_doc_.allocate_node(node_element,"MetricPlugins", NULL);
	MetricPlugins->append_attribute(xml_doc_.allocate_attribute("version","1.0"));
	MetricPlugins->append_attribute(xml_doc_.allocate_attribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance"));
	MetricPlugins->append_attribute(xml_doc_.allocate_attribute("xsi:noNamespaceSchemaLocation","../../../schema/Collect.xsd"));
	xml_doc_.append_node(MetricPlugins);

	//����GlobalMetricSetting��ǩ
	//������Դ collectGlobalMetricSetting sheetҳ��: CGlobalMetricSettingDatas_	
	WriteCollectGlobalMetricSetting(MetricPlugins);


	//����PluginClassAliasList��ǩ
	//������Դ PluginClassAliasList sheetҳ��: PluginClassAliasListDatas_	
	WriteCollectPluginClassAliasList(MetricPlugins);


	//����MetricPlugin��ǩ
	//������Դ MetricPlugin sheetҳ��: MetricPluginDatas_
	WriteCollectMetricPlugin(MetricPlugins);

	string collectPathname("");
	collectPathname = strXmlPath_ + outfilepath_ + collectfilename_;

	//loglog.str("");
	//loglog << "strXmlPath_ = " << strXmlPath_ << " , outfilepath_ = " << outfilepath_ << " , collectfilename_ = " << collectfilename_ ;
	//MYLOG(loglog.str().c_str());

	locale::global(locale(""));//��ȫ��������Ϊ����ϵͳĬ������ 
	std::ofstream collectxmlfile(collectPathname.c_str()); 
	locale::global(locale("C"));//��ԭȫ�������趨 

	//��UTF8�ı��뷽ʽ�洢
	stringstream doc_datas("");
	doc_datas << xml_doc_ << flush;

	collectxmlfile << GBToUTF8(doc_datas.str().c_str()) << flush;
	collectxmlfile.close();

	loglog.str("");
	loglog << "����[" << collectPathname << "]�ļ��ɹ�!";
	MYLOG(loglog.str().c_str());
	//AfxMessageBox(loglog.str().c_str());
	return ;
}


void BaseConver::WriteCollectGlobalMetricSetting(xml_node<>* MetricPlugins)
{
	xml_node<>* GlobalMetricSetting = xml_doc_.allocate_node(node_element,"GlobalMetricSetting", NULL);
	GlobalMetricSetting->append_attribute(xml_doc_.allocate_attribute("isEncrypt", COVERTER_IS_ENCRYPT));
	GlobalMetricSetting->append_attribute(xml_doc_.allocate_attribute("version", COVERTER_VERSION));

	if(CGlobalMetricSettingDatas_.empty())
	{
		//���Ϊ�գ������κδ���
		MYLOG("CGlobalMetricSettingDatas_ is empty.");
		MetricPlugins->append_node(GlobalMetricSetting);
		return ;
	}

	VEC_VEC_DATAS::iterator it = CGlobalMetricSettingDatas_.begin();
	for( ; it != CGlobalMetricSettingDatas_.end(); ++it )
	{
		string tempsettingtype = it->at(0);
		if(tempsettingtype == "Sysoid")
		{
			if(!(it->at(1)).empty())
			{
				xml_node<>* tag = xml_doc_.allocate_node(node_element, "Sysoid", (it->at(1)).c_str());
				GlobalMetricSetting->append_node(tag);
			}
		}
		else
		{
			xml_node<>* tag = xml_doc_.allocate_node(node_element, (it->at(0)).c_str(), NULL);
			if(!(it->at(1)).empty())
			{
				tag->append_attribute(xml_doc_.allocate_attribute("pluginid", (it->at(1)).c_str()));
			}
			if(!(it->at(2)).empty())
			{
				tag->append_attribute(xml_doc_.allocate_attribute("parameterId", (it->at(2)).c_str()));
			}
			if(!(it->at(3)).empty())
			{
				tag->append_attribute(xml_doc_.allocate_attribute("parameterValue", (it->at(3)).c_str()));
			}
			if(!(it->at(4)).empty())
			{
				tag->append_attribute(xml_doc_.allocate_attribute("parameterProperty", (it->at(4)).c_str()));
			}
			if(!(it->at(5)).empty())
			{
				tag->append_attribute(xml_doc_.allocate_attribute("propertyValue", (it->at(5)).c_str()));
			}
			GlobalMetricSetting->append_node(tag);
		}
		
		
	}

	MetricPlugins->append_node(GlobalMetricSetting);
	//MYLOG("WriteCollectPluginClassAliasList Successful.");
	return ;
}

void BaseConver::WriteCollectPluginClassAliasList(xml_node<>* MetricPlugins)
{
	xml_node<>* PluginClassAliasList = xml_doc_.allocate_node(node_element,"PluginClassAliasList", NULL);

	if(PluginClassAliasListDatas_.empty())
	{
		//���Ϊ�գ������κδ���
		MYLOG("PluginClassAliasListDatas_ is empty.");
		MetricPlugins->append_node(PluginClassAliasList);
		return ;
	}

	VEC_VEC_DATAS::iterator it = PluginClassAliasListDatas_.begin();
	for( ; it != PluginClassAliasListDatas_.end(); ++it )
	{
		xml_node<>* tag = xml_doc_.allocate_node(node_element, "PluginClassAlias", NULL);
		tag->append_attribute(xml_doc_.allocate_attribute("id", (it->at(0)).c_str()));
		tag->append_attribute(xml_doc_.allocate_attribute("class", (it->at(1)).c_str()));
		PluginClassAliasList->append_node(tag);
	}

	MetricPlugins->append_node(PluginClassAliasList);

	//MYLOG("WriteCollectPluginClassAliasList Successful.");
	return ;
}



void BaseConver::WriteCollectMetricPlugin(xml_node<>* MetricPlugins)
{
	string	metricid("");
	string	pluginid("");
	string	typeParameter("");
	string	typeDataHandler("");
	string	typeDataConverter("");
	BOOL	isParameter = FALSE;
	BOOL	isDataHandler = FALSE;
	BOOL	isDataConverter = FALSE;
	BOOL	isMetricPlugin = FALSE;

	xml_node<>* MetricPlugin = xml_doc_.allocate_node(node_element,"MetricPlugin", NULL);
	if(MetricPluginDatas_.empty())
	{
		//���Ϊ�գ������κδ���
		MYLOG("MetricPluginDatas_ is empty.");
		MetricPlugins->append_node(MetricPlugin);
		return ;
	}

	xml_node<>* PluginParameter = xml_doc_.allocate_node(node_element,"PluginParameter", NULL);
	xml_node<>* PluginDataHandlers = xml_doc_.allocate_node(node_element,"PluginDataHandlers", NULL);
	xml_node<>* PluginDataHandler = xml_doc_.allocate_node(node_element,"PluginDataHandler", NULL);
	xml_node<>* PluginDataConverter = xml_doc_.allocate_node(node_element,"PluginDataConverter", NULL);

	//metricid	pluginid	collectType	�ɼ�����	���type	���classKey	type	key	value	columns

	VEC_VEC_DATAS::iterator it = MetricPluginDatas_.begin();
	for( ; it != MetricPluginDatas_.end(); ++it )
	{
		string tmpmetricid = it->at(0);
		string tmppluginid = it->at(1);
		string plugintype = it->at(3);


		//�� metricid + pluginid �����仯ʱ����ʾһ����ǩ����
		if((metricid != tmpmetricid) || (pluginid != tmppluginid))
		{
			if(isParameter && (!typeParameter.empty()))
			{
				MetricPlugin->append_node(PluginParameter);
				isMetricPlugin = TRUE;
			}
			if(isDataHandler && (!typeDataHandler.empty()))
			{
				PluginDataHandlers->append_node(PluginDataHandler);
				MetricPlugin->append_node(PluginDataHandlers);
				isMetricPlugin = TRUE;
			}
			if(isDataConverter && !typeDataConverter.empty())
			{
				MetricPlugin->append_node(PluginDataConverter);
				isMetricPlugin = TRUE;
			}
			if(isMetricPlugin)
			{
				MetricPlugins->append_node(MetricPlugin);
				isMetricPlugin = FALSE;
			}

			//�����µĿռ䣬������һ�� PluginParameter����
			PluginParameter = xml_doc_.allocate_node(node_element,"PluginParameter", NULL);
			isParameter = FALSE;
			typeParameter = "";

			//�����µĿռ䣬������һ�� PluginDataHandler PluginDataHandlers ����
			PluginDataHandlers = xml_doc_.allocate_node(node_element,"PluginDataHandlers", NULL);
			PluginDataHandler = xml_doc_.allocate_node(node_element,"PluginDataHandler", NULL);
			isDataHandler = FALSE;
			typeDataHandler = "";

			//�����µĿռ䣬������һ�� PluginDataConverter ����
			PluginDataConverter = xml_doc_.allocate_node(node_element,"PluginDataConverter", NULL);
			isDataConverter = FALSE;
			typeDataConverter = "";

			//�����µĿռ䣬������һ��ָ�����
			MetricPlugin = xml_doc_.allocate_node(node_element,"MetricPlugin", NULL);

			MetricPlugin->append_attribute(xml_doc_.allocate_attribute("metricid", it->at(0).c_str()));
			MetricPlugin->append_attribute(xml_doc_.allocate_attribute("collectType", (it->at(2)).c_str()));
			MetricPlugin->append_attribute(xml_doc_.allocate_attribute("pluginid", it->at(1).c_str()));

			xml_node<>* PluginResultMetaInfo = xml_doc_.allocate_node(node_element, "PluginResultMetaInfo", NULL);
			PluginResultMetaInfo->append_attribute(xml_doc_.allocate_attribute("columns", (it->at(9)).c_str()));
			MetricPlugin->append_node(PluginResultMetaInfo);
		}

		//�ж�plugintype������
		if(plugintype == "PluginParameter")
		{
			string tmptypeParameter = it->at(4);
			if(typeParameter != tmptypeParameter)
			{
				//������Ա仯������ӵ�MetricPlugin������ʼ�µĶ�ȡ
				if(!typeParameter.empty())
				{
					MetricPlugin->append_node(PluginParameter);
				}

				PluginParameter = xml_doc_.allocate_node(node_element,"PluginParameter", NULL);
				PluginParameter->append_attribute(xml_doc_.allocate_attribute("type", it->at(4).c_str()));
			}

			if(	(it->at(6)).empty() &&  (it->at(7)).empty() &&  (it->at(8)).empty() )
			{
				//������ֵͬʱΪ�գ������
			}
			else
			{
				xml_node<>* Parameter = xml_doc_.allocate_node(node_element,"Parameter", NULL);

				Parameter->append_attribute(xml_doc_.allocate_attribute("type", (it->at(6)).c_str()));
				Parameter->append_attribute(xml_doc_.allocate_attribute("key", (it->at(7)).c_str()));
				Parameter->append_attribute(xml_doc_.allocate_attribute("value", (it->at(8)).c_str()));


				PluginParameter->append_node(Parameter);
			}

			typeParameter = tmptypeParameter;
			isParameter = TRUE;
		}
		else if(plugintype == "PluginDataHandler")
		{
			string tmptypeDataHandler = it->at(5);
			if(typeDataHandler != tmptypeDataHandler)
			{
				if(!typeDataHandler.empty())
				{
					//������Ա仯������ӵ�MetricPlugin������ʼ�µĶ�ȡ
					PluginDataHandlers->append_node(PluginDataHandler);
				}
				PluginDataHandler = xml_doc_.allocate_node(node_element,"PluginDataHandler", NULL);

				PluginDataHandler->append_attribute(xml_doc_.allocate_attribute("classKey", it->at(5).c_str()));
			}
			xml_node<>* Parameter = xml_doc_.allocate_node(node_element,"Parameter", NULL);
			Parameter->append_attribute(xml_doc_.allocate_attribute("type", (it->at(6)).c_str()));
			Parameter->append_attribute(xml_doc_.allocate_attribute("key", (it->at(7)).c_str()));
			Parameter->append_attribute(xml_doc_.allocate_attribute("value", (it->at(8)).c_str()));
			if(	(it->at(6)).empty() &&  (it->at(7)).empty() &&  (it->at(8)).empty() )
			{
				//������ֵͬʱΪ�գ������
			}
			else
			{
				PluginDataHandler->append_node(Parameter);
			}

			typeDataHandler = tmptypeDataHandler;
			isDataHandler = TRUE;
		}
		else if(plugintype == "PluginDataConverter")
		{
			string tmptypeDataConverter = it->at(5);
			if(typeDataConverter != tmptypeDataConverter)
			{
				//������Ա仯������ӵ�MetricPlugin������ʼ�µĶ�ȡ
				if(!typeDataConverter.empty())
				{
					MetricPlugin->append_node(PluginDataConverter);
				}

				PluginDataConverter = xml_doc_.allocate_node(node_element,"PluginDataConverter", NULL);
				isDataConverter = FALSE;

				PluginDataConverter->append_attribute(xml_doc_.allocate_attribute("classKey", it->at(5).c_str()));
			}
			xml_node<>* Parameter = xml_doc_.allocate_node(node_element,"Parameter", NULL);
			Parameter->append_attribute(xml_doc_.allocate_attribute("type", (it->at(6)).c_str()));
			Parameter->append_attribute(xml_doc_.allocate_attribute("key", (it->at(7)).c_str()));
			Parameter->append_attribute(xml_doc_.allocate_attribute("value", (it->at(8)).c_str()));
			if(	(it->at(6)).empty() &&  (it->at(7)).empty() &&  (it->at(8)).empty() )
			{
				//������ֵͬʱΪ�գ������
			}
			else
			{
				PluginDataConverter->append_node(Parameter);
			}			

			typeDataConverter = tmptypeDataConverter;
			isDataConverter = TRUE;
		}
		else
		{
			//����Ĳ������ֱ࣬�Ӷ���
			loglog.str("");
			loglog << "plugintype is error ! metricid:[" << tmpmetricid << "],pluginid[" << tmppluginid << "],plugintype :["  << plugintype << "].";
			MYLOG(loglog.str().c_str());
		}

		metricid = tmpmetricid;
		pluginid = tmppluginid;
	}

	//�����һ��ָ��������
	if(isParameter && (!typeParameter.empty()))
	{
		MetricPlugin->append_node(PluginParameter);
		isMetricPlugin = TRUE;
	}
	if(isDataHandler && (!typeDataHandler.empty()))
	{
		PluginDataHandlers->append_node(PluginDataHandler);
		MetricPlugin->append_node(PluginDataHandlers);
		isMetricPlugin = TRUE;
	}
	if(isDataConverter && !typeDataConverter.empty())
	{
		MetricPlugin->append_node(PluginDataConverter);
		isMetricPlugin = TRUE;
	}

	if(isMetricPlugin)
	{
		MetricPlugins->append_node(MetricPlugin);
	}
	//MYLOG("WriteCollectMetricPlugin Successful.");
	return;
}



void BaseConver::WriteResourceXml()
{
	//����ļ�
	xml_doc_.clear();

	//д�ļ�ͷ
	xml_node<>* rot = xml_doc_.allocate_node(rapidxml::node_pi,xml_doc_.allocate_string("xml version=\"1.0\" encoding=\"UTF-8\""));
	xml_doc_.append_node(rot);

	//����MetricPlugins��ǩ
	xml_node<>* Capacity = xml_doc_.allocate_node(node_element,"Capacity", NULL);
	Capacity->append_attribute(xml_doc_.allocate_attribute("version","1.0"));
	Capacity->append_attribute(xml_doc_.allocate_attribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance"));
	Capacity->append_attribute(xml_doc_.allocate_attribute("xsi:noNamespaceSchemaLocation","../../../schema/Resource.xsd"));
	xml_doc_.append_node(Capacity);

	//����GlobalMetricSetting��ǩ
	//������Դ resourceGlobalMetricSetting sheetҳ��: resourceGlobalMetricSettingDatas_	
	WriteResourceGlobalMetricSetting(Capacity);


	//����PluginClassAliasList��ǩ
	//������Դ PluginClassAliasList sheetҳ��: PluginClassAliasListDatas_	
	WriteResource(Capacity);

	string resourcePathname("");
	resourcePathname = strXmlPath_ + outfilepath_ + "\\resource.xml";

	locale::global(locale(""));//��ȫ��������Ϊ����ϵͳĬ������ 
	std::ofstream resourcexmlfile(resourcePathname.c_str()); 
	locale::global(locale("C"));//��ԭȫ�������趨 


	//��UTF8�ı��뷽ʽ�洢
	stringstream doc_datas("");
	doc_datas << xml_doc_ << flush;

	resourcexmlfile << GBToUTF8(doc_datas.str().c_str()) << flush;
	resourcexmlfile.close();

	loglog.str("");
	loglog << "����[" << resourcePathname << "]�ļ��ɹ�!";
	MYLOG(loglog.str().c_str());
	//AfxMessageBox(loglog.str().c_str());
	return ;
}

void BaseConver::WriteResourceGlobalMetricSetting(xml_node<>* Capacity)
{
	xml_node<>* GlobalMetricSetting = xml_doc_.allocate_node(node_element,"GlobalMetricSetting", NULL);
	GlobalMetricSetting->append_attribute(xml_doc_.allocate_attribute("isEncrypt", COVERTER_IS_ENCRYPT));
	GlobalMetricSetting->append_attribute(xml_doc_.allocate_attribute("version", COVERTER_VERSION));

	if(RGlobalMetricSettingDatas_.empty())
	{
		//���Ϊ�գ������κδ���
		MYLOG("RGlobalMetricSettingDatas_ is empty.");
		Capacity->append_node(GlobalMetricSetting);
		return ;
	}

	VEC_VEC_DATAS::iterator it = RGlobalMetricSettingDatas_.begin();
	for( ; it != RGlobalMetricSettingDatas_.end(); ++it )
	{
		GlobalMetricSetting->append_node(xml_doc_.allocate_node(node_element, "GlobalIsEdit", (it->at(0)).c_str()));
		GlobalMetricSetting->append_node(xml_doc_.allocate_node(node_element, "GlobalIsMonitor", (it->at(1)).c_str()));
		GlobalMetricSetting->append_node(xml_doc_.allocate_node(node_element, "GlobalDefaultMonitorFreq", (it->at(2)).c_str()));
		GlobalMetricSetting->append_node(xml_doc_.allocate_node(node_element, "GlobalsupportMonitorFreq", (it->at(3)).c_str()));
		GlobalMetricSetting->append_node(xml_doc_.allocate_node(node_element, "GlobalIsAlert", (it->at(4)).c_str()));
		GlobalMetricSetting->append_node(xml_doc_.allocate_node(node_element, "GlobalDefaultFlapping", (it->at(5)).c_str()));

		Capacity->append_node(GlobalMetricSetting);
	}

	//MYLOG("WriteResourceGlobalMetricSetting Successful.");

	return;
}


void BaseConver::WriteResource(xml_node<>* Capacity)
{
	xml_node<>* Resource;
	if(ResourceDatas_.empty())
	{
		//���Ϊ�գ������κδ���
		MYLOG("ResourceDatas_ is empty.");
		Resource = xml_doc_.allocate_node(node_element,"Resource", NULL);
		Capacity->append_node(Resource);
		return ;
	}

	//��Properties sheetҳ����ת��Ϊ�� resource_id Ϊkey �����ݼ���
	mapPropertiesDatas_.clear();
	ParsePropertiesDatas(mapPropertiesDatas_);

	//��Instantiation sheetҳ����ת��Ϊ�� resource_id Ϊkey �����ݼ���
	mapInstantiationDatas_.clear();
	ParseInstantiationDatas(mapInstantiationDatas_);

	string		resourceid("");
	string		metricid("");
	BOOL		isResource = FALSE;
	//
	// ��Դid	category	icon	��Դ����name	��Դ����description	��Դ����type	����Դparentid	
	// ָ��id	ָ��style	ָ��name	ָ������description	ָ�����unit	�Ƿ�չʾIsDisplay	չʾ����displayOrder	
	// �Ƿ���IsMonitor	�Ƿ�༭IsEdit	Ĭ�ϼ��Ƶ��DefaultMonitorFreq	֧�ּ��Ƶ��SupportMonitorFreq	�Ƿ�澯IsAlert	DefaultFlapping
	// ��ֵ����1 ��ֵ1 ��ֵ״̬1 ��ֵ����2 ��ֵ2 ��ֵ״̬2 ��ֵ����3 ��ֵ3 ��ֵ״̬3 ��ֵ����4	��ֵ4 ��ֵ״̬4 ��ֵ����5 ��ֵ5 ��ֵ״̬5
	//

	xml_node<>* Metrics;

	VEC_VEC_DATAS::iterator it = ResourceDatas_.begin();
	for( ; it != ResourceDatas_.end(); ++it )
	{
		int column_count = (*it).size();
		string tmpresourceid = it->at(0);
		string tmpmetricid = it->at(7);
		if( tmpresourceid != resourceid )
		{
			//���idΪ�գ��Ƿ���Ҫ?
			//��ʱȫ������Ϊ��Ч
			if(isResource)
			{
				//��Metrics�������ӵ��ڵ���
				Resource->append_node(Metrics);

				//��Properties�������ӵ��ڵ���
				WriteResourceProperties(Resource, resourceid);

				//��Instantiation�������ӵ��ڵ���
				WriteResourceInstantiation(Resource, resourceid);

				Capacity->append_node(Resource);
				isResource = FALSE;
			}


			Resource = xml_doc_.allocate_node(node_element,"Resource", NULL);

			string tempstr("");
			Resource->append_attribute(xml_doc_.allocate_attribute("id", (it->at(0)).c_str()));

			tempstr = it->at(5);
			if(!tempstr.empty())
			{
				Resource->append_attribute(xml_doc_.allocate_attribute("type", (it->at(5)).c_str()));
			}

			tempstr.clear();
			tempstr = it->at(1);
			if(!tempstr.empty())
			{
				Resource->append_attribute(xml_doc_.allocate_attribute("category", (it->at(1)).c_str()));
			}

			Resource->append_attribute(xml_doc_.allocate_attribute("icon", (it->at(2)).c_str()));			
			Resource->append_attribute(xml_doc_.allocate_attribute("name", (it->at(3)).c_str()));
			Resource->append_attribute(xml_doc_.allocate_attribute("description", (it->at(4)).c_str()));

			tempstr.clear();
			tempstr = it->at(6);
			if(!tempstr.empty())
			{
				Resource->append_attribute(xml_doc_.allocate_attribute("parentid", (it->at(6)).c_str()));
			}

			Metrics = xml_doc_.allocate_node(node_element,"Metrics", NULL);
		}

		xml_node<>* Metric = xml_doc_.allocate_node(node_element,"Metric", NULL);
		Metric->append_attribute(xml_doc_.allocate_attribute("id", (it->at(7)).c_str()));
		Metric->append_attribute(xml_doc_.allocate_attribute("style", (it->at(8)).c_str()));
		Metric->append_attribute(xml_doc_.allocate_attribute("name", (it->at(9)).c_str()));
		Metric->append_attribute(xml_doc_.allocate_attribute("description", (it->at(10)).c_str()));
		
		string tempunit = str_trim(it->at(11));
		if(tempunit == "����")
		{
			Metric->append_attribute(xml_doc_.allocate_attribute("unit", "ms"));
		}
		else
		{
			Metric->append_attribute(xml_doc_.allocate_attribute("unit", (it->at(11)).c_str()));
		}

		BOOL istable = CheckIsTableMetric(it->at(7));
		if(istable)
		{
			Metric->append_attribute(xml_doc_.allocate_attribute("isTable", "true"));
		}
		

		xml_node<>* IsDisplay = xml_doc_.allocate_node(node_element,"IsDisplay", (it->at(12)).c_str());
		IsDisplay->append_attribute(xml_doc_.allocate_attribute("displayOrder", (it->at(13)).c_str()));
		Metric->append_node(IsDisplay);
		if(istable)
		{
			Metric->append_node(xml_doc_.allocate_node(node_element, "IsMonitor", "false"));
		}
		else
		{
			Metric->append_node(xml_doc_.allocate_node(node_element, "IsMonitor", (it->at(14)).c_str()));
		}
		
		Metric->append_node(xml_doc_.allocate_node(node_element, "IsEdit", (it->at(15)).c_str()));  
		Metric->append_node(xml_doc_.allocate_node(node_element, "DefaultMonitorFreq", (it->at(16)).c_str()));  
		Metric->append_node(xml_doc_.allocate_node(node_element, "SupportMonitorFreq", (it->at(17)).c_str()));  
		Metric->append_node(xml_doc_.allocate_node(node_element, "IsAlert", (it->at(18)).c_str()));
		Metric->append_node(xml_doc_.allocate_node(node_element, "DefaultFlapping", (it->at(19)).c_str()));

		xml_node<>* Thresholds = xml_doc_.allocate_node(node_element,"Thresholds", NULL);
		BOOL isThresholds = FALSE;

		//����Ҫ��3��Ԫ�أ����ܽ���һ������
		for( int i = 20; i <= column_count - 3; )
		{
			string tmpoperator = it->at(i);
			string tmpdefaultvalue = it->at(i+1);
			string tmpstateid = it->at(i+2);
			xml_node<>* Threshold = xml_doc_.allocate_node(node_element,"Threshold", NULL);
			Threshold->append_attribute(xml_doc_.allocate_attribute("operator", (it->at(i)).c_str()));
			Threshold->append_attribute(xml_doc_.allocate_attribute("defaultvalue", (it->at(i+1)).c_str()));
			Threshold->append_attribute(xml_doc_.allocate_attribute("stateid", (it->at(i+2)).c_str()));

			//��3��ֵ��һ����Ϊ�գ�����Ϊ��Ч
			if((!tmpoperator.empty()) || (!tmpdefaultvalue.empty()) || (!tmpstateid.empty()))
			{
				Thresholds->append_node(Threshold);
				isThresholds = TRUE;
			}			

			//ÿ��Thresholdռ����3��:operator�� defaultvalue�� stateid
			i = i + 3;
		}
		if(isThresholds)
		{
			Metric->append_node(Thresholds);
		}
		Metrics->append_node(Metric);

		resourceid = tmpresourceid;
		isResource = TRUE;
	}

	//�����һ��Ҳ����
	if(isResource)
	{
		//��Metrics�������ӵ��ڵ���
		Resource->append_node(Metrics);

		//��Properties�������ӵ��ڵ���
		WriteResourceProperties(Resource, resourceid);

		//��Instantiation�������ӵ��ڵ���
		WriteResourceInstantiation(Resource, resourceid);

		Capacity->append_node(Resource);
	}

	//MYLOG("WriteResource Successful.");
	return ;

}

void BaseConver::ParsePropertiesDatas(MAP_VEC_VEC_DATAS &mapPropertiesDatas)
{
	if(PropertiesDatas_.empty())
	{
		MYLOG("PropertiesDatas_ is empty.");
		return;
	}

	string	resourceid("");
	BOOL	isInsert = FALSE;

	VEC_VEC_DATAS values;
	values.clear();

	VEC_VEC_DATAS::iterator it = PropertiesDatas_.begin();
	for( ; it != PropertiesDatas_.end(); ++it )
	{
		string tmpresourceid = it->at(0);		
		if (resourceid != tmpresourceid)
		{
			if(isInsert)
			{
				mapPropertiesDatas[resourceid] = values;
				values.clear();
				isInsert = FALSE;
			}
		}
		values.push_back(*it);
		isInsert = TRUE;
		resourceid = tmpresourceid;
	}

	if(isInsert)
	{
		mapPropertiesDatas[resourceid] = values;
	}

	//MYLOG("ParsePropertiesDatas Successful.");
	return;
}

void BaseConver::ParseInstantiationDatas(MAP_VEC_VEC_DATAS &mapInstantiationDatas)
{
	if(InstantiationDatas_.empty())
	{
		MYLOG("InstantiationDatas_ is empty.");
		return;
	}

	string	resourceid("");
	BOOL	isInsert = FALSE;

	VEC_VEC_DATAS values;
	values.clear();

	VEC_VEC_DATAS::iterator it = InstantiationDatas_.begin();
	for( ; it != InstantiationDatas_.end(); ++it )
	{
		string tmpresourceid = it->at(0);
		if (resourceid != tmpresourceid)
		{
			if(isInsert)
			{
				mapInstantiationDatas[resourceid] = values;
				values.clear();
				isInsert = FALSE;
			}
		}
		values.push_back(*it);
		isInsert = TRUE;
		resourceid = tmpresourceid;
	}

	if(isInsert)
	{
		mapInstantiationDatas[resourceid] = values;
	}

	//MYLOG("ParseInstantiationDatas Successful.");
	return;
}

void BaseConver::WriteResourceProperties(xml_node<>* Resource, const string resouceid)
{
	xml_node<>* Properties = xml_doc_.allocate_node(node_element,"Properties", NULL);
	if((mapPropertiesDatas_[resouceid]).empty())
	{
		//�������Ϊ�գ�ֱ�ӷ���
		Resource->append_node(Properties);
		MYLOG("PropertiesDatas is empty.");
		return;
	}

	VEC_VEC_DATAS::iterator it = (mapPropertiesDatas_[resouceid]).begin();
	for( ; it != (mapPropertiesDatas_[resouceid]).end(); ++it )
	{
		xml_node<>* Property = xml_doc_.allocate_node(node_element,"Property", NULL);
		Property->append_attribute(xml_doc_.allocate_attribute("id", (it->at(1)).c_str()));
		Property->append_attribute(xml_doc_.allocate_attribute("name", (it->at(2)).c_str()));
		Property->append_attribute(xml_doc_.allocate_attribute("metricid", (it->at(3)).c_str()));
		Properties->append_node(Property);
	}

	Resource->append_node(Properties);

	//MYLOG("WriteResourceProperties Successful.");
	return;
}

void BaseConver::WriteResourceInstantiation(xml_node<>* Resource, const string resouceid)
{
	xml_node<>* Instantiation;
	if((mapInstantiationDatas_[resouceid]).empty())
	{
		//�������Ϊ�գ�ֱ�ӷ���
		Instantiation = xml_doc_.allocate_node(node_element,"Instantiation", NULL);
		Resource->append_node(Instantiation);
		MYLOG("InstantiationDatas is empty.");
		return;
	}

	VEC_VEC_DATAS::iterator it = (mapInstantiationDatas_[resouceid]).begin();
	for( ; it != (mapInstantiationDatas_[resouceid]).end(); ++it )
	{
		Instantiation = xml_doc_.allocate_node(node_element,"Instantiation", NULL);
		Instantiation->append_attribute(xml_doc_.allocate_attribute("InstanceId", (it->at(1)).c_str()));
		Instantiation->append_attribute(xml_doc_.allocate_attribute("InstanceName", (it->at(2)).c_str()));
		Resource->append_node(Instantiation);
	}

	//MYLOG("WriteResourceInstantiation Successful.");
	return;
}


