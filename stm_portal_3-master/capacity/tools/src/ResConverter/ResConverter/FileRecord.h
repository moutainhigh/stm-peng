/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		filerecord.h
Description:	日志输出文件
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/

#pragma once

#include <stdarg.h>
#include <atltime.h>

//日志一次写的最大长度
#define MAX_WRITE_LEN 1024*6

//定义一个日志输出的宏
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
	** 功能：	将内容输出到日志文本中
	** 输入：	需要些的内容 char* cRecord
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL WriteRecord(char* cRecord);

	/*
	** 功能：	将内容输出到日志文本中
	** 输入：	需要些的内容 CString const& CSRec
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL WriteRecord(CString const& CSRec);

	/*
	** 功能：	将内容输出到日志文本中
	** 输入：	需要些的内容 string const& CSRec
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL WriteRecord(string const& CSRec);
	

	static CRITICAL_SECTION cs;			//互斥写入
	static BOOL cs_ok;					//初始化标志
	static void InitCriticalSection();	//初始化
	static char m_FilePath[MAX_PATH];	//日志文件目录

private:

	static char m_cRecordFileName[255];	//日志文件名全路径

	static HANDLE m_hFNew;				//文件句柄

	static int count;					//写文件的次数
};

