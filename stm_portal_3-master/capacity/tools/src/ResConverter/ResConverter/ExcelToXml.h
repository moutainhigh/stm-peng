/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		ExcelToXml.h
Description:	将4.0中标准的excel文件 生成 4.0的collect.xml 和 resource.xml
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/

#pragma once

#include "BaseConver.h"

#define OUT_XML_PATH "ExcelToXml"

class ExcelToXml : public BaseConver
{
public:
	//ExcelToXml(string strpath, string filename);
	ExcelToXml(string pathfilename);
	~ExcelToXml(void);

public:

	/*
	** 功能：	初始化数据
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void InitDatas();

	/*
	** 功能：	文件处理主函数
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void AnalysisMain();

	/*
	** 功能：	检查输入的excel的sheet页是否合法正确，是否符合标准的excel文件的sheet页
	** 输入：	无
	** 输出：	无
	** 返回值： TRUE：正确；  FALSE：不正确
	*/
	BOOL CheckBook();

	/*
	** 功能：	从excel中将所有sheet页的内容，一次全部读出，这样效率高
	** 输入：	无
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL ReadExcel();

	/*
	** 功能：	读excel内容，并将内容 生成 collect.xml 和 resource.xml 两个文件
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void ReadExcelAndWriteXml();

	/*
	** 功能：	检查是否需要合并同类型的产品，如果需要，将路径修改以及合并
	** 输入：	需要检查的产品文件路径 newpath
				合并的产品 findstr
				合并的类型 findtype
	** 输出：	修改后的产品文件路径 newpath
	** 返回值： 无
	*/
	void ChangeOutFolder(string &newpath, const string findstr, const string findtype);

	/*
	** 功能：	创建生成4.0的xml文件的路径
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	BOOL CreateOutFolder();

public:

	//需要处理的excel文件全路径
	string excelpathfilename_;

	//excel的所有sheet页名称集合
	SET_STRING_DATAS	setsheetnames_;

	//excel中所有sheet页名称数据集合
	VEC_STRING_DATAS	vecsheetname_;

	//excel 中 sheet页的列名称
	MAP_VEC_DATAS		mapSheetColumnName_;

	//是否生成Resource.xml文件
	BOOL isWriteResourceXml_;

};
