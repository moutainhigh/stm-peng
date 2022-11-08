#include "StdAfx.h"
#include "ExcelToXml.h"

//ExcelToXml::ExcelToXml(string strpath, string filename)
ExcelToXml::ExcelToXml(string pathfilename)
{
	//excelpathname_ = strpath;
	//excelfilename_ = filename;
	//excelpathfilename_ = excelpathname_ + excelfilename_;
	excelpathfilename_ = pathfilename;

	/**********�������ɵ��ļ�Ŀ¼*****************************/
	//int npos = pathfile.find_last_of('\\');
	//int endpos = pathfile.find_last_of('.');	
	//strfilename_ = pathfile.substr(npos + 1, endpos -  npos - 1);

	//string desPath = strPath_ + "\\" + strfilename_;

	//if(!CheckFolderExist(desPath))
	//{
	//	if(!CreateDirectory(desPath.c_str(),NULL))
	//	{
	//		loglog.str("");
	//		loglog << "�����ļ���[" << desPath << "]ʧ��.";
	//		MYLOG(loglog.str().c_str());
	//		AfxMessageBox(loglog.str().c_str());
	//		return ;
	//	}
	//}
	/***************************************/

	AnalysisMain();
}

ExcelToXml::~ExcelToXml(void)
{
}


void ExcelToXml::InitDatas()
{
	//pathfilename_.clear();
	isWriteResourceXml_ = TRUE;

	//��ʼ������sheetҲ����
	vecsheetname_ = g_GlobalData.Sheetnameinfo_;
	VEC_STRING_DATAS::iterator it = vecsheetname_.begin();
	for( ; it != vecsheetname_.end(); ++it)
	{
		setsheetnames_.insert(*it);
	}

	//�������
	mapSheetColumnName_.clear();
	mapSheetColumnName_ = g_GlobalData.Excleinfo_;
}

void ExcelToXml::AnalysisMain()
{
	InitDatas();


	BOOL ret = CheckFileExist(excelpathfilename_);
	if(!ret)
	{
		loglog.str("");
		loglog << "��Ҫ�������ļ�������.[" << excelpathfilename_ << "].";
		MYLOG(loglog.str().c_str());
	}
	
	ReadExcelAndWriteXml();

}

void ExcelToXml::ReadExcelAndWriteXml()
{
	int rc = 0;

	//��excel���ݶ���
	if(!ReadExcel())
	{
		loglog.str("");
		loglog << "Read Excel is fail. Read Excel is :[" << excelpathfilename_ << "].";
		MYLOG(loglog.str().c_str());
		return ;
	}

	//׼�����ɵ��ļ�·��
	if(!CreateOutFolder())
	{
		loglog.str("");
		loglog << "CreateOutFolder is fail.";
		MYLOG(loglog.str().c_str());
		return ;
	}
	
	//����collect xml�ļ�
	WriteCollectXml();

	//����Resource xml�ļ�
	if(isWriteResourceXml_)
	{
		WriteResourceXml();
	}	

	return ;
}

BOOL ExcelToXml::CheckBook()
{
	//���excel��sheetҳ�������Ƿ���ȷ	
	SET_STRING_DATAS setsheetname;

	string sheetname = gbookhandle.OpenBook(excelpathfilename_, setsheetname);
	if(setsheetname == setsheetnames_)
	{
		//MYLOG("sheetnames is correct!");
	}
	else
	{
		//AfxMessageBox("�����Excel�ļ���sheetҳ�治��ȷ����ȷ���ļ�����ȷ�ԣ�");
		loglog.str("");
		loglog << "sheetnames is not correct! Input sheetname is :[" << sheetname << "].";
		MYLOG(loglog.str().c_str());
		return FALSE;
	}

	return TRUE;
}

BOOL ExcelToXml::ReadExcel()
{
	int rc = 0;

	//���excel��sheet����
	if(!CheckBook())
	{
		MYLOG("CheckBook is fail. ");
		return FALSE;
	}

	//�����ݴ�Excel�ж�����,����sheetҳ����ȡ
	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[0], CGlobalMetricSettingDatas_, (mapSheetColumnName_[vecsheetname_[0]]).size());
	if(rc < 0)
	{
		MYLOG("Read collectGlobalMetricSetting Datas is error.");
		return FALSE;
	}

	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[1], PluginClassAliasListDatas_, (mapSheetColumnName_[vecsheetname_[1]]).size());
	if(rc < 0)
	{
		MYLOG("Read PluginClassAliasList Datas is error.");
		return FALSE;
	}

	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[2], MetricPluginDatas_, (mapSheetColumnName_[vecsheetname_[2]]).size());

	//��ע����ʱʹ�õ�
	//rc = gbookhandle.ReadSheet(strPathFile_, vecsheetname_[2], MetricPluginDatas_, (mapSheetColumnName_[vecsheetname_[2]]).size() - 1);
	if(rc < 0)
	{
		MYLOG("Read MetricPlugin Datas is error.");
		return FALSE;
	}

	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[3], RGlobalMetricSettingDatas_, (mapSheetColumnName_[vecsheetname_[3]]).size());
	if(rc < 0)
	{
		MYLOG("Read resourceGlobalMetricSetting Datas is error.");
		return FALSE;
	}

	//rc = gbookhandle.ReadSheet(strPathFile_, vecsheetname_[4], ResourceDatas_, (mapSheetColumnName_[vecsheetname_[4]]).size() - 3*5 );
	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[4], ResourceDatas_, (mapSheetColumnName_[vecsheetname_[4]]).size() - 3*3 );
	if(rc < 0)
	{
		MYLOG("Read Resource Datas is error.");
		return FALSE;
	}

	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[5], PropertiesDatas_, (mapSheetColumnName_[vecsheetname_[5]]).size());
	if(rc < 0)
	{
		MYLOG("Read Properties Datas is error.");
		return FALSE;
	}

	rc = gbookhandle.ReadSheet(excelpathfilename_, vecsheetname_[6], InstantiationDatas_, (mapSheetColumnName_[vecsheetname_[6]]).size());
	if(rc < 0)
	{
		MYLOG("Read Instantiation Datas is error.");
		return FALSE;
	}

	return TRUE;
}


void ExcelToXml::ChangeOutFolder(string &newpath, const string findstr, const string findtype)
{
	int npos1 = newpath.find(findstr);
	int npos2 = newpath.find(findtype);
 	string temppath = findstr + findtype;

	if((npos1 >= 0) && (npos2 >= 0))
	{
		if(newpath != temppath)
		{
			newpath = replace_all_distinct(newpath, findtype, "");
			collectfilename_ = "\\" + newpath + "_collect.xml";
			if(temppath == "sangforfirewall")
			{
				temppath = "sangforvpn";
			}
			newpath = temppath;
			isWriteResourceXml_ = FALSE;
		}
	}
}

BOOL ExcelToXml::CreateOutFolder()
{
	/**********�������ɵ��ļ�Ŀ¼*****************************/
	string tempfilepath = excelpathfilename_;
	int npos = tempfilepath.find_last_of('\\');
	int endpos = tempfilepath.find_last_of('.');	
	string newpath = tempfilepath.substr(npos + 1, endpos -  npos - 1);
	tempfilepath = tempfilepath.substr(0, npos + 1) + OUT_XML_PATH;

	if(!CheckFolderExist(tempfilepath))
	{
		if(!CreateDirectory(tempfilepath.c_str(),NULL))
		{
			loglog.str("");
			loglog << "�����ļ���[" << tempfilepath << "]ʧ��.";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return FALSE;
		}
	}

	/**********�������ɵ��ļ�Ŀ¼*****************************/
	newpath = LetterTolower(newpath);

	string findswitch = "switch";
	string findrouter = "router";
	string findfirewall = "firewall";

	string find3com = "3com";
	ChangeOutFolder(newpath, find3com, findswitch);

	string findcisco = "cisco";
	ChangeOutFolder(newpath, findcisco, findswitch);
	ChangeOutFolder(newpath, findcisco, findrouter);

	//��f5���ɵ�һ���ļ�����
	if(newpath == "f52")
	{
		collectfilename_ = "\\" + newpath + "_collect.xml";	
		newpath = "f5";
		isWriteResourceXml_ = FALSE;
	}

	string findfortinet = "fortinet";
	ChangeOutFolder(newpath, findfortinet, findfirewall);

	string findh3c = "h3c";
	ChangeOutFolder(newpath, findh3c, findswitch);
	ChangeOutFolder(newpath, findh3c, findrouter);
	ChangeOutFolder(newpath, findh3c, findfirewall);

	string findhuawei = "huawei";
	ChangeOutFolder(newpath, findhuawei, findswitch);
	ChangeOutFolder(newpath, findhuawei, findrouter);

	string findmaipu = "maipu";
	ChangeOutFolder(newpath, findmaipu, findswitch);

	string findsecworld = "secworld";
	ChangeOutFolder(newpath, findsecworld, findfirewall);

	string findtopsec = "topsec";
	ChangeOutFolder(newpath, findtopsec, findfirewall);

	string findvenus = "venus";
	ChangeOutFolder(newpath, findvenus, findfirewall);

	string findzte = "zte";
	ChangeOutFolder(newpath, findzte, findswitch);

	string findsangfor = "sangfor";
	ChangeOutFolder(newpath, findsangfor, findfirewall);
	/********************************************************************/



	//��������Ŀ¼
	tempfilepath = tempfilepath + "\\" + newpath;
	if(!CheckFolderExist(tempfilepath))
	{
		if(!CreateDirectory(tempfilepath.c_str(),NULL))
		{
			loglog.str("");
			loglog << "�����ļ���[" << tempfilepath << "]ʧ��.";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return FALSE;
		}
	}

	strXmlPath_.clear();
	outfilepath_ = tempfilepath;

	return TRUE;
}


