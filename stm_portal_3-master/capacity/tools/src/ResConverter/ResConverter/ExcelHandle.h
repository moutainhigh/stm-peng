/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		ExcelHandle.h
Description:	Excel文件以ADO方式进行读写的功能，支持office多个版本 2003、2007、2010
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/

#pragma once

#include <iostream>
#include <sstream>
#include <fstream>
#include <time.h>
#import "C:\Program Files\Common Files\System\ADO\msado15.dll" no_namespace rename("EOF", "adoEOF") rename("BOF", "adoBOF")

//const int writeRows = 50000;

inline void TESTHR(HRESULT x) {if FAILED(x) _com_issue_error(x);};

class ExcelHandle
{
public:
	ExcelHandle(void);
	~ExcelHandle(void);

	/*
	** 功能：	COM口的初始化 
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void Init();

	/*
	** 功能：	Excel文件的读写方式已经版本 
	** 输入：	文件名 filename ； 第一行读写标志 header
	** 输出：	无
	** 返回值： 读写方式 string
	*/
	string makeConnStr(std::string filename, bool header = true);
	
	/*
	** 功能：	读Excel文件的某个sheet页的全部内容 
	** 输入：	文件名 excelFile ，文件sheet页名 sheetName ，sheet页的最好列数 countcolumn
	** 输出：	sheet页所有数据的vec集datas
	** 返回值： 读取到sheet页的有效行数
	*/
	int ReadSheet(std::string excelFile, string sheetName, VEC_VEC_DATAS &datas, int countcolumn);

	/*
	** 功能：	删除文件 
	** 输入：	文件名 filename
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL DeleteExcelFile(string filename);

	/*
	** 功能：	写excel文件 sheet也的内容
	** 输入：	文件名 filename， sheet页名 sheetname， 需要写的sheet也的表头集合（列名） columnname， 写的数据 datas
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL WriteSheet(string filename, string sheetname, VEC_STRING_DATAS columnname, VEC_VEC_DATAS datas);


	/*
	** 功能：	获取excel文件sheet页的所有页名
	** 输入：	文件名 pathfilename
	** 输出：	sheet页的所有页名集合sheetnames
	** 返回值： sheet页的所有页名组成的一个字符串string，页名之间以逗号隔开
	*/
	string OpenBook(const string pathfilename, SET_STRING_DATAS &sheetnames);

	/*
	** 功能： 两个vec比较
	** 输入： vec1， vec2
	** 输出： 无
	**返回值：	TRUE:  vec1中的值vec2中全有,同时，vec2中的值vec1中全有
				FALSE：vec1和vec2的值不一样
	*/
	BOOL vecCompare(VEC_STRING_DATAS vec1, VEC_STRING_DATAS vec2);

private:
	BOOL	isRun_;			//com是否正常运行 ，暂时没有使用
	int		initcount;		//重复初始化次数
};

extern ExcelHandle gbookhandle;

