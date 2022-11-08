/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		ExcelToXml.h
Description:	��4.0�б�׼��excel�ļ� ���� 4.0��collect.xml �� resource.xml
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
	** ���ܣ�	��ʼ������
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void InitDatas();

	/*
	** ���ܣ�	�ļ�����������
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void AnalysisMain();

	/*
	** ���ܣ�	��������excel��sheetҳ�Ƿ�Ϸ���ȷ���Ƿ���ϱ�׼��excel�ļ���sheetҳ
	** ���룺	��
	** �����	��
	** ����ֵ�� TRUE����ȷ��  FALSE������ȷ
	*/
	BOOL CheckBook();

	/*
	** ���ܣ�	��excel�н�����sheetҳ�����ݣ�һ��ȫ������������Ч�ʸ�
	** ���룺	��
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL ReadExcel();

	/*
	** ���ܣ�	��excel���ݣ��������� ���� collect.xml �� resource.xml �����ļ�
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void ReadExcelAndWriteXml();

	/*
	** ���ܣ�	����Ƿ���Ҫ�ϲ�ͬ���͵Ĳ�Ʒ�������Ҫ����·���޸��Լ��ϲ�
	** ���룺	��Ҫ���Ĳ�Ʒ�ļ�·�� newpath
				�ϲ��Ĳ�Ʒ findstr
				�ϲ������� findtype
	** �����	�޸ĺ�Ĳ�Ʒ�ļ�·�� newpath
	** ����ֵ�� ��
	*/
	void ChangeOutFolder(string &newpath, const string findstr, const string findtype);

	/*
	** ���ܣ�	��������4.0��xml�ļ���·��
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	BOOL CreateOutFolder();

public:

	//��Ҫ�����excel�ļ�ȫ·��
	string excelpathfilename_;

	//excel������sheetҳ���Ƽ���
	SET_STRING_DATAS	setsheetnames_;

	//excel������sheetҳ�������ݼ���
	VEC_STRING_DATAS	vecsheetname_;

	//excel �� sheetҳ��������
	MAP_VEC_DATAS		mapSheetColumnName_;

	//�Ƿ�����Resource.xml�ļ�
	BOOL isWriteResourceXml_;

};
