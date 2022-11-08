#include "StdAfx.h"
#include "ExcelToXml.h"

//ExcelToXml::ExcelToXml(string strpath, string filename)
ExcelToXml::ExcelToXml(string pathfilename)
{
	//excelpathname_ = strpath;
	//excelfilename_ = filename;
	//excelpathfilename_ = excelpathname_ + excelfilename_;
	excelpathfilename_ = pathfilename;

	/**********调整生成的文件目录*****************************/
	//int npos = pathfile.find_last_of('\\');
	//int endpos = pathfile.find_last_of('.');	
	//strfilename_ = pathfile.substr(npos + 1, endpos -  npos - 1);

	//string desPath = strPath_ + "\\" + strfilename_;

	//if(!CheckFolderExist(desPath))
	//{
	//	if(!CreateDirectory(desPath.c_str(),NULL))
	//	{
	//		loglog.str("");
	//		loglog << "创建文件夹[" << desPath << "]失败.";
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

	//初始化所有sheet也名称
	vecsheetname_ = g_GlobalData.Sheetnameinfo_;
	VEC_STRING_DATAS::iterator it = vecsheetname_.begin();
	for( ; it != vecsheetname_.end(); ++it)
	{
		setsheetnames_.insert(*it);
	}

	//变量清空
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
		loglog << "需要解析的文件不存在.[" << excelpathfilename_ << "].";
		MYLOG(loglog.str().c_str());
	}
	
	ReadExcelAndWriteXml();

}

void ExcelToXml::ReadExcelAndWriteXml()
{
	int rc = 0;

	//将excel数据读出
	if(!ReadExcel())
	{
		loglog.str("");
		loglog << "Read Excel is fail. Read Excel is :[" << excelpathfilename_ << "].";
		MYLOG(loglog.str().c_str());
		return ;
	}

	//准备生成的文件路径
	if(!CreateOutFolder())
	{
		loglog.str("");
		loglog << "CreateOutFolder is fail.";
		MYLOG(loglog.str().c_str());
		return ;
	}
	
	//生成collect xml文件
	WriteCollectXml();

	//生成Resource xml文件
	if(isWriteResourceXml_)
	{
		WriteResourceXml();
	}	

	return ;
}

BOOL ExcelToXml::CheckBook()
{
	//检查excel的sheet页面名称是否正确	
	SET_STRING_DATAS setsheetname;

	string sheetname = gbookhandle.OpenBook(excelpathfilename_, setsheetname);
	if(setsheetname == setsheetnames_)
	{
		//MYLOG("sheetnames is correct!");
	}
	else
	{
		//AfxMessageBox("输入的Excel文件的sheet页面不正确，请确认文件的正确性！");
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

	//检查excel的sheet名称
	if(!CheckBook())
	{
		MYLOG("CheckBook is fail. ");
		return FALSE;
	}

	//将数据从Excel中读出来,按照sheet页来读取
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

	//有注释列时使用的
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
	/**********调整生成的文件目录*****************************/
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
			loglog << "创建文件夹[" << tempfilepath << "]失败.";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return FALSE;
		}
	}

	/**********调整生成的文件目录*****************************/
	newpath = LetterTolower(newpath);

	string findswitch = "switch";
	string findrouter = "router";
	string findfirewall = "firewall";

	string find3com = "3com";
	ChangeOutFolder(newpath, find3com, findswitch);

	string findcisco = "cisco";
	ChangeOutFolder(newpath, findcisco, findswitch);
	ChangeOutFolder(newpath, findcisco, findrouter);

	//将f5生成到一个文件夹中
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



	//创建生成目录
	tempfilepath = tempfilepath + "\\" + newpath;
	if(!CheckFolderExist(tempfilepath))
	{
		if(!CreateDirectory(tempfilepath.c_str(),NULL))
		{
			loglog.str("");
			loglog << "创建文件夹[" << tempfilepath << "]失败.";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return FALSE;
		}
	}

	strXmlPath_.clear();
	outfilepath_ = tempfilepath;

	return TRUE;
}


