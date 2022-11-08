#include "StdAfx.h"
#include "ExcelHandle.h"
#include <algorithm>

ExcelHandle gbookhandle;

ExcelHandle::ExcelHandle(void)
{
	MYLOG("ExcelHandle entry.");
	isRun_ = FALSE;
	initcount = 3;

	Init();
}

ExcelHandle::~ExcelHandle(void)
{
	//是否com对象
	::CoUninitialize();
	MYLOG("~ExcelHandle.");
}

void ExcelHandle::Init()
{
	for(int i = 0; i < initcount; i++)
	{
		if(FAILED(::CoInitialize(NULL)))
		{
			MYLOG("创建com对象失败！");
			continue;
		}
		else
		{
			isRun_ = TRUE;
			break;
		}
	}	

}

std::string ExcelHandle::makeConnStr(std::string filename, bool header)
{
	std::stringstream stream;

	std::string hdr = header ? "YES" : "NO";

	if(!filename.empty())
		if(*--filename.end() == 'x')
			stream << "Provider=Microsoft.ACE.OLEDB.12.0;Data Source=" << filename << ";Extended Properties=\"Excel 12.0 Xml;HDR=" << hdr << "\"";
		else        
			stream << "Provider='Microsoft.JET.OLEDB.4.0';Data Source=" << filename << ";Extended Properties=\"Excel 8.0;HDR=" << hdr << "\"";

	return stream.str();
}


string ExcelHandle::OpenBook(const string pathfilename, SET_STRING_DATAS &sheetnames)
{
	int nsheetnum = 0;
	_ConnectionPtr pCon = NULL;
	_RecordsetPtr pSchema = NULL;
	stringstream tmpsheetname("");
	
	try
	{
		_bstr_t connStr(makeConnStr(pathfilename, true).c_str());

		TESTHR(pCon.CreateInstance(__uuidof(Connection)));
		TESTHR(pCon->Open(connStr, "", "", NULL));

		pSchema = pCon->OpenSchema(adSchemaTables);

		while(!pSchema->adoEOF)
		{
			std::string sheetName = (char*)(_bstr_t)pSchema->Fields->GetItem("TABLE_NAME")->Value.bstrVal;
			//MYLOG(sheetName.c_str());
			if( sheetName.find_last_of('$') == (sheetName.length()-1))
			{
				sheetName = sheetName.substr(0, sheetName.length() -1);
			}

			sheetnames.insert(sheetName);
			nsheetnum++;
			tmpsheetname << sheetName << ",";
			pSchema->MoveNext();
		}

	}
	catch(_com_error &e)
	{
		stringstream errorlog("");
		_bstr_t bstrDescription(e.Description());
		CharToOem(bstrDescription, bstrDescription);
		errorlog << "catch: [" << bstrDescription << "][" << e.Error() << "][" << e.ErrorMessage()<< endl;
		MYLOG(errorlog.str().c_str());
	}
	

	if(pSchema->State)
	{
		pSchema->Close();
	}
	pSchema = NULL;

	if(pCon != 0 && pCon->State == adStateOpen)
	{
		pCon->Close();
	}
	pCon = NULL;
	
	//MYLOG(tmpsheetname.str().c_str());
	return tmpsheetname.str();
}

int ExcelHandle::ReadSheet(std::string excelFile, string sheetName, VEC_VEC_DATAS &datas, int countcolumn)
{
	_RecordsetPtr		pRec = NULL;
	stringstream		strstr("");
	VEC_STRING_DATAS	tmpdata;
	int					rowcount = 0;
	stringstream		sqlcmd("");
	stringstream		loglog("");
	BOOL				isFirstRow = TRUE;

	//一次性将一个sheet页中的内容全部读出来，在内存中处理效率要高，速率要快
	sqlcmd << "SELECT * FROM [" << sheetName << "$]";
	Sleep(10);

	try
	{
		_bstr_t connStr(makeConnStr(excelFile, true).c_str());

		TESTHR(pRec.CreateInstance(__uuidof(Recordset)));       
		TESTHR(pRec->Open(sqlcmd.str().c_str(), connStr, adOpenStatic, adLockOptimistic, adCmdText));

		//MYLOG(sqlcmd.str().c_str());
		
		int column_count = pRec->Fields->GetCount();

		if(column_count < countcolumn)
		{
			loglog.str("");
			loglog << sheetName << "页，至少应该有" << countcolumn << "列，但是现在只有" << column_count << "列！生成xml文件失败！";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return -1;
		}

		while(!pRec->adoEOF)
		{
			tmpdata.clear();

			BOOL isvalidrow = FALSE;
			for(long i = 0; i < pRec->Fields->GetCount(); ++i)
			{
				if(pRec->Fields->GetItem(i)->GetActualSize() > 0)
				{					
					_variant_t v = pRec->Fields->GetItem(i)->Value;
					_bstr_t bstr_t(v.bstrVal);
					string tempvalue(bstr_t);

					//只有第一行内容需要去掉前后空格
					if(isFirstRow)
					{
						tempvalue = str_trim(tempvalue);
					}
					tmpdata.push_back(tempvalue);
					
					isvalidrow = TRUE;					
				}
				else
				{
					tmpdata.push_back("");
					//MYLOG("NULL");
				}
			}
			if(isvalidrow)
			{
				datas.push_back(tmpdata);
				rowcount++;
			}

			isFirstRow = FALSE;
			pRec->MoveNext();
		}
	}
	catch(_com_error &e)
	{
		stringstream errorlog("");
		_bstr_t bstrDescription(e.Description());
		CharToOem(bstrDescription, bstrDescription);
		errorlog << "catch: [" << bstrDescription << "][" << e.Error() << "][" << e.ErrorMessage()<< endl;
		MYLOG(errorlog.str().c_str());
		//AfxMessageBox("读Excel文件出现异常，生成xml文件失败，请重试！");
		return -1;
	}

	loglog.str("");
	loglog << sqlcmd.str() << " , valid datas : " << rowcount << " rows";
	MYLOG(loglog.str().c_str());

	if(pRec->State)
	{
		pRec->Close();
	}
	pRec = NULL;

	return rowcount;
}



BOOL ExcelHandle::WriteSheet(string filename, string sheetname, VEC_STRING_DATAS columnname, VEC_VEC_DATAS datas)
{

	Sleep(10);
	_ConnectionPtr pCon = NULL;
	_CommandPtr pCmd = NULL;
	_RecordsetPtr pRec = NULL;
	string sqlcmd("");
	string cmdstr("");

	cmdstr = "CREATE TABLE " + sheetname + "(";
	for (int i = 0; i < (int)columnname.size(); i++ )
	{
		cmdstr = cmdstr + columnname[i] + " MEMO, ";
	}	
	cmdstr = cmdstr.replace(cmdstr.length() - 2, 1, ")");
	//MYLOG(cmdstr.c_str());

	int columncount = columnname.size();
	sqlcmd = "SELECT * FROM " + sheetname ;

	try
	{
		_bstr_t connStr(makeConnStr(filename).c_str());

		TESTHR(pCon.CreateInstance(__uuidof(Connection)));
		TESTHR(pCon->Open(connStr, "", "", NULL));

		TESTHR(pCmd.CreateInstance(__uuidof(Command)));
		pCmd->ActiveConnection = pCon;       
		pCmd->CommandText = cmdstr.c_str();
		pCmd->Execute(NULL, NULL, adCmdText);

		TESTHR(pRec.CreateInstance(__uuidof(Recordset)));		
		pRec->Open(sqlcmd.c_str(), _variant_t((IDispatch*)pCon), adOpenKeyset, adLockOptimistic, adCmdText);
		
		if(datas.empty())
		{
			if(pCon != 0 && pCon->State == adStateOpen)
			{
				pCon->Close();
			}
			MYLOG("datas is empty.");
			return TRUE;
		}
		else
		{
			int datasize = datas.size();
			stringstream loglog("");
			loglog << sheetname << ", size = [" << datasize << "]" << flush;
			MYLOG(loglog.str().c_str());
		}
		VEC_VEC_DATAS::iterator it = datas.begin();
		for( ; it != datas.end(); ++it )
		{
			TESTHR(pRec->AddNew());
			VEC_STRING_DATAS rowdatas;
			rowdatas = *it;
			VEC_STRING_DATAS::iterator itrow = rowdatas.begin();
			for(int i =0 ; (itrow != rowdatas.end()) && (i < columncount); ++itrow, ++i )
			{
				string tempvalue = *itrow;
				pRec->Fields->GetItem(columnname[i].c_str())->Value = _variant_t(tempvalue.c_str());	
			}
		}
		TESTHR(pRec->Update());
		TESTHR(pRec->Close());
	}
	catch(_com_error &e)
	{        
		stringstream errorlog("");
		_bstr_t bstrDescription(e.Description());
		CharToOem(bstrDescription, bstrDescription);
		errorlog << "errorlog : excel name:[" << filename << "],sheetname:[" << sheetname << 
			"] catch: [" << bstrDescription << "][" << e.Error() << "][" << e.ErrorMessage()<< endl;
		MYLOG(errorlog.str().c_str());
		//AfxMessageBox("写Excel文件出现异常，请重试！");
		return FALSE;
	}

	if(pCon != 0 && pCon->State == adStateOpen)
	{
		pCon->Close();
	}

	return TRUE;
}

BOOL ExcelHandle::DeleteExcelFile(string filename)
{
	BOOL ret = FALSE;
	ret = DeleteFileA(filename.c_str());
	return ret;
}

BOOL ExcelHandle::vecCompare(VEC_STRING_DATAS vec1, VEC_STRING_DATAS vec2)
{
	if(vec1 == vec2)
	{
		return TRUE;
	}

	//一个vec为空，另一个不为空，直接返回FALSE
	//如果两个vec数据不一致，直接返回失败
	if(    (vec1.empty() && !vec2.empty())
		|| (!vec1.empty() && vec2.empty())
		|| (vec1.size() != vec2.size()) )
	{
		return FALSE;
	}

	//当vec1和vec2 的size 相等时
	//这里不考虑重复元素造成一致的情况
	VEC_STRING_DATAS::iterator it = vec1.begin();
	for( ; it != vec1.end(); ++it)
	{
		string tmpstr = *it;
		VEC_STRING_DATAS::iterator it2 = find(vec2.begin(), vec2.end(), tmpstr);		
		if( it2 == vec2.end() ) 
		{
			return FALSE;
		}
	}
	
	return TRUE;
}

