#include "stdafx.h"
#include "filerecord.h"

CRITICAL_SECTION CFileRecord::cs;		
BOOL CFileRecord::cs_ok = FALSE;
char CFileRecord::m_cRecordFileName[255] = {0};
HANDLE CFileRecord::m_hFNew = NULL;
int CFileRecord::count = 0;
char CFileRecord::m_FilePath[MAX_PATH] = {0};


//定义日志对象
CFileRecord g_FRcd;
char LOGBUF[MAX_WRITE_LEN+100];

void CFileRecord::InitCriticalSection()
{
	if(!cs_ok)
	{
		InitializeCriticalSection(&cs);
		cs_ok = TRUE;
	}
	
	//初始化文件路径
	GetCurrentDirectory(MAX_PATH, m_FilePath);
}

CFileRecord::CFileRecord(void)
{
	count++;
}

CFileRecord::~CFileRecord(void)
{
	count--;
	if(m_hFNew && count<=0)
	{
		CloseHandle(m_hFNew);
		m_hFNew = NULL;
	}
}

BOOL CFileRecord::WriteRecord(CString const& CSRec)
{
	char Record[MAX_WRITE_LEN];
	strncpy(Record, CSRec, MAX_WRITE_LEN);
	return WriteRecord(Record);
}

BOOL CFileRecord::WriteRecord(string const& CSRec)
{
	return WriteRecord((char *)CSRec.c_str());
}

BOOL CFileRecord::WriteRecord(char* cRecord)
{
	if(!cs_ok)
	{
		printf("记录日志对象临界区未初始化\r\n");
		return FALSE;
	}

	unsigned long WriteBytes = 0;
	DWORD wSize = 0; 
	char ct[100];
	
	memset(ct, 0, sizeof(ct));

	EnterCriticalSection(&cs);

	CTime cTime = CTime::GetCurrentTime();
	static CTime cTimeOld;	

	if( (cTime.GetDay() != cTimeOld.GetDay()) || ( strlen(m_cRecordFileName) == 0 ) )
	{
		strcpy(m_cRecordFileName, m_FilePath);
		strcat(m_cRecordFileName, "\\ResCnverter_log\\");
		CreateDirectory(m_cRecordFileName, NULL);
		sprintf(m_cRecordFileName, "%s%04d%02d%02d.log", 
						m_cRecordFileName, cTime.GetYear(), cTime.GetMonth(), cTime.GetDay());


		cTimeOld = cTime;

		if (m_hFNew)
		{
			CloseHandle(m_hFNew);
			m_hFNew = NULL;
		}

		m_hFNew = CreateFile(m_cRecordFileName, 
			GENERIC_WRITE, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL,
			OPEN_ALWAYS,
			FILE_ATTRIBUTE_NORMAL|FILE_FLAG_SEQUENTIAL_SCAN,
			0);//打开准备写入的文件句柄

		if(m_hFNew == INVALID_HANDLE_VALUE)
		{
			m_hFNew = NULL;
			LeaveCriticalSection(&cs);
			return FALSE;
		}	
		SetFilePointer(m_hFNew, GetFileSize(m_hFNew, NULL), NULL, FILE_BEGIN);
	}

	if( m_hFNew == NULL )
	{
		m_hFNew = CreateFile(m_cRecordFileName, 
			GENERIC_WRITE, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL,
			OPEN_ALWAYS,
			FILE_ATTRIBUTE_NORMAL|FILE_FLAG_SEQUENTIAL_SCAN,
			0);//打开准备写入的文件句柄

		if(m_hFNew == INVALID_HANDLE_VALUE)
		{
			m_hFNew = NULL;
			LeaveCriticalSection(&cs);
			return FALSE;
		}	
		SetFilePointer(m_hFNew, GetFileSize(m_hFNew, NULL), NULL, FILE_BEGIN);
	}
	
	memset(ct, 0, sizeof(ct));
	sprintf(ct, "%04d-%02d-%02d %02d:%02d:%02d   ",cTime.GetYear(), cTime.GetMonth(), cTime.GetDay(),
		                        cTime.GetHour(), cTime.GetMinute(), cTime.GetSecond());   
	wSize = (DWORD)strlen(ct);
	WriteFile(m_hFNew, ct, wSize, &WriteBytes, NULL);
	
	wSize = (DWORD)strlen(cRecord);
	WriteFile(m_hFNew, cRecord, wSize, &WriteBytes, NULL);

	LeaveCriticalSection(&cs);
	
	return TRUE;
}


