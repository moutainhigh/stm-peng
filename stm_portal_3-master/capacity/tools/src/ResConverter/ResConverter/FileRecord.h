/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		filerecord.h
Description:	��־����ļ�
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/

#pragma once

#include <stdarg.h>
#include <atltime.h>

//��־һ��д����󳤶�
#define MAX_WRITE_LEN 1024*6

//����һ����־����ĺ�
#define MYLOG(mylog)  memset(LOGBUF, 0, sizeof(LOGBUF));\
	sprintf (LOGBUF, "%s  %d  %s \r\n", \
	__FILE__, __LINE__, mylog); \
	g_FRcd.WriteRecord(LOGBUF);

class CFileRecord
{

public:
	CFileRecord(void);
	~CFileRecord(void);
	
	/*
	** ���ܣ�	�������������־�ı���
	** ���룺	��ҪЩ������ char* cRecord
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL WriteRecord(char* cRecord);

	/*
	** ���ܣ�	�������������־�ı���
	** ���룺	��ҪЩ������ CString const& CSRec
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL WriteRecord(CString const& CSRec);

	/*
	** ���ܣ�	�������������־�ı���
	** ���룺	��ҪЩ������ string const& CSRec
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL WriteRecord(string const& CSRec);
	

	static CRITICAL_SECTION cs;			//����д��
	static BOOL cs_ok;					//��ʼ����־
	static void InitCriticalSection();	//��ʼ��
	static char m_FilePath[MAX_PATH];	//��־�ļ�Ŀ¼

private:

	static char m_cRecordFileName[255];	//��־�ļ���ȫ·��

	static HANDLE m_hFNew;				//�ļ����

	static int count;					//д�ļ��Ĵ���
};

