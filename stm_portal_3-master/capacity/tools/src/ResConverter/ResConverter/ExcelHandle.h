/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		ExcelHandle.h
Description:	Excel�ļ���ADO��ʽ���ж�д�Ĺ��ܣ�֧��office����汾 2003��2007��2010
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
	** ���ܣ�	COM�ڵĳ�ʼ�� 
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void Init();

	/*
	** ���ܣ�	Excel�ļ��Ķ�д��ʽ�Ѿ��汾 
	** ���룺	�ļ��� filename �� ��һ�ж�д��־ header
	** �����	��
	** ����ֵ�� ��д��ʽ string
	*/
	string makeConnStr(std::string filename, bool header = true);
	
	/*
	** ���ܣ�	��Excel�ļ���ĳ��sheetҳ��ȫ������ 
	** ���룺	�ļ��� excelFile ���ļ�sheetҳ�� sheetName ��sheetҳ��������� countcolumn
	** �����	sheetҳ�������ݵ�vec��datas
	** ����ֵ�� ��ȡ��sheetҳ����Ч����
	*/
	int ReadSheet(std::string excelFile, string sheetName, VEC_VEC_DATAS &datas, int countcolumn);

	/*
	** ���ܣ�	ɾ���ļ� 
	** ���룺	�ļ��� filename
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL DeleteExcelFile(string filename);

	/*
	** ���ܣ�	дexcel�ļ� sheetҲ������
	** ���룺	�ļ��� filename�� sheetҳ�� sheetname�� ��Ҫд��sheetҲ�ı�ͷ���ϣ������� columnname�� д������ datas
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL WriteSheet(string filename, string sheetname, VEC_STRING_DATAS columnname, VEC_VEC_DATAS datas);


	/*
	** ���ܣ�	��ȡexcel�ļ�sheetҳ������ҳ��
	** ���룺	�ļ��� pathfilename
	** �����	sheetҳ������ҳ������sheetnames
	** ����ֵ�� sheetҳ������ҳ����ɵ�һ���ַ���string��ҳ��֮���Զ��Ÿ���
	*/
	string OpenBook(const string pathfilename, SET_STRING_DATAS &sheetnames);

	/*
	** ���ܣ� ����vec�Ƚ�
	** ���룺 vec1�� vec2
	** ����� ��
	**����ֵ��	TRUE:  vec1�е�ֵvec2��ȫ��,ͬʱ��vec2�е�ֵvec1��ȫ��
				FALSE��vec1��vec2��ֵ��һ��
	*/
	BOOL vecCompare(VEC_STRING_DATAS vec1, VEC_STRING_DATAS vec2);

private:
	BOOL	isRun_;			//com�Ƿ��������� ����ʱû��ʹ��
	int		initcount;		//�ظ���ʼ������
};

extern ExcelHandle gbookhandle;

