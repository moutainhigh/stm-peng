
// ResConverter.h : PROJECT_NAME Ӧ�ó������ͷ�ļ�
//

#pragma once

#ifndef __AFXWIN_H__
	#error "�ڰ������ļ�֮ǰ������stdafx.h�������� PCH �ļ�"
#endif

#include "resource.h"		// ������

// CResConverterApp:
// �йش����ʵ�֣������ ResConverter.cpp
//

class CResConverterApp : public CWinAppEx
{
public:
	CResConverterApp();

// ��д
	public:
	virtual BOOL InitInstance();

// ʵ��

	DECLARE_MESSAGE_MAP()
};

extern CResConverterApp theApp;



