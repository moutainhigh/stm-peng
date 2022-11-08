
// ResConverterDlg.cpp : ʵ���ļ�
//

#include "stdafx.h"
#include "ResConverter.h"
#include "ResConverterDlg.h"
#include "NetworkXmlToXml.h"
#include "HostXmlToXml.h"
#include "HostSnmpXmlToXml.h"
#include "DBXmlToXml.h"
#include "ApplicationXmlToXml.h"
#include "MiddlewareXmlToXml.h"
#include "ExcelToXml.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// ����Ӧ�ó��򡰹��ڡ��˵���� CAboutDlg �Ի���

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// �Ի�������
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV ֧��

// ʵ��
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
END_MESSAGE_MAP()


// CResConverterDlg �Ի���




CResConverterDlg::CResConverterDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CResConverterDlg::IDD, pParent)
{
	//m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	m_hIcon = AfxGetApp()->LoadIcon(IDI_ICON1);
}

void CResConverterDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CResConverterDlg, CDialog)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP
	ON_BN_CLICKED(IDC_OPEN_FILE, &CResConverterDlg::OnBnClickedOpenFile)
	ON_BN_CLICKED(IDC_OPEN_FOLDER, &CResConverterDlg::OnBnClickedOpenFolder)
	ON_BN_CLICKED(IDC_NETWORK_XML_TO_XML, &CResConverterDlg::OnBnClickedNetworkXmlToXml)
	ON_BN_CLICKED(IDC_HOST_XML_TO_XML, &CResConverterDlg::OnBnClickedHostXmlToXml)
	ON_BN_CLICKED(IDC_DB_XML_TO_XML, &CResConverterDlg::OnBnClickedDbXmlToXml)
	ON_BN_CLICKED(IDC_APPLICATION_XML_TO_XML, &CResConverterDlg::OnBnClickedApplicationXmlToXml)
	ON_BN_CLICKED(IDC_MIDDLEWARE_XML_TO_XML, &CResConverterDlg::OnBnClickedMiddlewareXmlToXml)
	ON_BN_CLICKED(IDC_RADIO1, &CResConverterDlg::OnBnClickedRadio1)
	ON_BN_CLICKED(IDC_RADIO2, &CResConverterDlg::OnBnClickedRadio2)
	ON_BN_CLICKED(IDC_RADIO3, &CResConverterDlg::OnBnClickedRadio3)
END_MESSAGE_MAP()


// CResConverterDlg ��Ϣ�������

BOOL CResConverterDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// ��������...���˵�����ӵ�ϵͳ�˵��С�

	// IDM_ABOUTBOX ������ϵͳ���Χ�ڡ�
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// ���ô˶Ի����ͼ�ꡣ��Ӧ�ó��������ڲ��ǶԻ���ʱ����ܽ��Զ�
	//  ִ�д˲���
	SetIcon(m_hIcon, TRUE);			// ���ô�ͼ��
	SetIcon(m_hIcon, FALSE);		// ����Сͼ��

	// TODO: �ڴ���Ӷ���ĳ�ʼ������

	((CButton *)GetDlgItem(IDC_RADIO1))->SetCheck(TRUE);
	m_ConverterType_ = XML_TO_XML;

	//��ʼ��OneCenter4.0��Ŀ¼�ṹ
	//ResPathInit();

	return TRUE;  // ���ǽ��������õ��ؼ������򷵻� TRUE
}

void CResConverterDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// �����Ի��������С����ť������Ҫ����Ĵ���
//  �����Ƹ�ͼ�ꡣ����ʹ���ĵ�/��ͼģ�͵� MFC Ӧ�ó���
//  �⽫�ɿ���Զ���ɡ�

void CResConverterDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // ���ڻ��Ƶ��豸������

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// ʹͼ���ڹ����������о���
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// ����ͼ��
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

//���û��϶���С������ʱϵͳ���ô˺���ȡ�ù��
//��ʾ��
HCURSOR CResConverterDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

BOOL CResConverterDlg::CreateFolder(const string Path)
{
	if(!CheckFolderExist(Path))
	{
		if(!CreateDirectory(Path.c_str(),NULL))
		{
			loglog.str("");
			loglog << "�����ļ���[" << Path << "]ʧ��.";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return FALSE;
		}
	}	

	//loglog.str("");
	//loglog << "�����ļ���[" << Path << "]�ɹ�.";
	//MYLOG(loglog.str().c_str());
	
	return TRUE;
}




string CResConverterDlg::OpenFolder()
{
	string pathname("");
	BROWSEINFO bi;
	ZeroMemory(&bi,sizeof(BROWSEINFO));
	//�����ʾ���
	bi.lpszTitle= _T("��ѡ���ļ���");
	//���"�½��ļ�����"��"�༭��"
	bi.ulFlags = BIF_NEWDIALOGSTYLE | BIF_EDITBOX;
	bi.hwndOwner = *this;

	// ��ʾһ���Ի��������û�ѡ���ļ���
	LPITEMIDLIST pidl = SHBrowseForFolder(&bi);
	char szFolder[_MAX_PATH];
	memset(szFolder, 0, sizeof(szFolder));
	CString strFolder = _T("");
	if (pidl != NULL)
	{
		SHGetPathFromIDList(pidl, szFolder);
		pathname = szFolder;
	}

	return pathname;
}



void CResConverterDlg::SearchFileInDirectroy(int filetype, const string &dir, VEC_STRING_DATAS &outList )
{
	WIN32_FIND_DATA findData;
	HANDLE hHandle;
	string filePathName;
	string fullPathName;

	filePathName = dir;

	switch(filetype)
	{
	case 1:
		filePathName += "\\*.xml";
		break;
	case 2:
		filePathName += "\\*.xlsx";
		break;
	case 3:
		filePathName += "\\*.xls";
		break;
	default:
		filePathName += "\\*.*";
		break;
	}
	

	hHandle = FindFirstFile( filePathName.c_str(), &findData );
	if( INVALID_HANDLE_VALUE == hHandle )
	{
		loglog.str("");
		loglog << "Open path is error:" << filePathName << flush;
		MYLOG(loglog.str().c_str());
		return;
	}

	do
	{
		if( strcmp(".", findData.cFileName) == 0 || strcmp("..", findData.cFileName) == 0 )
		{
			continue;
		}

		fullPathName = dir;
		fullPathName += "\\";
		fullPathName += findData.cFileName;

		if( findData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY )
		{
			SearchFileInDirectroy(filetype, fullPathName, outList);
		}
		else
		{
			outList.push_back(fullPathName);
		}

	} while( FindNextFile( hHandle, &findData ) );

	FindClose( hHandle );
}


void CResConverterDlg::OnBnClickedOpenFile()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������
	CString pathfilename("");
	stringstream pathfile("");

	CFileDialog dlg(TRUE, "*", "", OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT, "Excel Files(*.xlsx)|*.xlsx|All Files (*.*)|*.*||", this );

	dlg.m_pOFN->lpstrTitle = "�����ȷ��Excel�ļ�"; 

	int ret = dlg.DoModal();
	if(ret == IDOK)
	{
		
		pathfilename = dlg.GetPathName();

		loglog.str("");
		loglog << "dispose file : " << pathfilename << " begin.";
		MYLOG(loglog.str().c_str());
		pathfile << pathfilename << flush;

		ExcelToXml m_excletoxml(pathfile.str());

		string outpathfile = pathfile.str();
		int npos = outpathfile.find_last_of('\\');
		outpathfile = outpathfile.substr(0, npos + 1) + OUT_XML_PATH;
		loglog.str("");
		loglog << "ExcelתΪXML�������" << endl << "�����ļ�·�� " << outpathfile << flush;
		AfxMessageBox(loglog.str().c_str());
	}
	else
	{
		//û��ѡ���κ��ļ�
		//�����κδ���
		MYLOG("Cancel or Close!");
	}

}

void CResConverterDlg::OnBnClickedOpenFolder()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������

	string pathname("");
	pathname = OpenFolder();

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}

	MYLOG(pathname.c_str());

	VEC_STRING_DATAS outpath;
	outpath.clear();

	//2007��2010��excel �ļ�
	SearchFileInDirectroy(2, pathname, outpath);

	//2003��excel �ļ�
	SearchFileInDirectroy(3, pathname, outpath);

	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����excel�ļ�!");
		return;
	}

	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		//string filename = *it;
		//int npos = filename.find_last_of('.');
		//string excelfile = filename.substr(npos, filename.length() - npos);
		//if(excelfile == ".xlsx")
		//{
		//	ExcelToXml m_creatxml(*it);
		//}

		ExcelToXml m_excletoxml(*it);		
	}


	loglog.str("");
	loglog << "Excel����ת��ΪXML�������" << endl << "�����ļ�·�� " << pathname << "\\" << OUT_XML_PATH << flush;
	AfxMessageBox(loglog.str().c_str());

}


BOOL CResConverterDlg::CheckNetworkFilename(string filename)
{
	if(    (filename == "network_kpiinfo.xml") 
		|| (filename == "public_network.xml")  )
	{
		return FALSE;
	}

	return TRUE;
}

BOOL CResConverterDlg::NetworkCreateFolder(const string pathname, const string outfilename)
{
	string temppathname = pathname;
	string tempoutfilename = outfilename;

	string find3com = "3com";
	string findcisco = "cisco";
	string findf52 = "f52";
	string findfortinet = "fortinet";
	string findh3c = "h3c";
	string findhuawei = "huawei";
	string findmaipu = "maipu";
	string findsecworld = "secworld";
	string findtopsec = "topsec";
	string findvenus = "venus";
	string findzte = "zte";
	string findsangfor = "sangfor";


	string findswitch = "switch";
	string findrouter = "router";
	string findfirewall = "firewall";

	int npos2 = outfilename.find(findswitch);
	int npos3 = outfilename.find(findrouter);
	int npos4 = outfilename.find(findfirewall);

	int npos1 = outfilename.find(find3com);
	if((npos1 >= 0) && (npos2 >= 0))
	{
		tempoutfilename = "\\network\\3comswitch";
	}

	npos1 = outfilename.find(findcisco);
	if((npos1 >= 0) && (npos2 >= 0))
	{
		tempoutfilename = "\\network\\ciscoswitch";
	}

	if((npos1 >= 0) && (npos3 >= 0))
	{
		tempoutfilename = "\\network\\ciscorouter";
	}

	npos1 = outfilename.find(findf52);
	if(npos1 >= 0)
	{
		tempoutfilename = "\\network\\f5";
	}

	npos1 = outfilename.find(findfortinet);
	if((npos1 >= 0) && (npos4 >= 0))
	{
		tempoutfilename = "\\network\\fortinetfirewall";
	}

	npos1 = outfilename.find(findh3c);
	if((npos1 >= 0) && (npos2 >= 0))
	{
		tempoutfilename = "\\network\\h3cswitch";
	}

	if((npos1 >= 0) && (npos3 >= 0))
	{
		tempoutfilename = "\\network\\h3crouter";
	}

	if((npos1 >= 0) && (npos4 >= 0))
	{
		tempoutfilename = "\\network\\h3cfirewall";
	}

	npos1 = outfilename.find(findhuawei);
	if((npos1 >= 0) && (npos2 >= 0))
	{
		tempoutfilename = "\\network\\huaweiswitch";
	}

	if((npos1 >= 0) && (npos3 >= 0))
	{
		tempoutfilename = "\\network\\huaweirouter";
	}

	npos1 = outfilename.find(findmaipu);
	if((npos1 >= 0) && (npos2 >= 0))
	{
		tempoutfilename = "\\network\\maipuswitch";
	}

	npos1 = outfilename.find(findsangfor);
	if(npos1 >= 0)
	{
		int tempnpos = outfilename.find("vpn");
		if((npos4 >= 0) || (tempnpos >= 0))
		{
			tempoutfilename = "\\network\\sangforvpn";
		}		
	}

	npos1 = outfilename.find(findsecworld);
	if((npos1 >= 0) && (npos4 >= 0))
	{
		tempoutfilename = "\\network\\secworldfirewall";
	}

	npos1 = outfilename.find(findtopsec);
	if((npos1 >= 0) && (npos4 >= 0))
	{
		tempoutfilename = "\\network\\topsecfirewall";
	}

	npos1 = outfilename.find(findvenus);
	if((npos1 >= 0) && (npos4 >= 0))
	{
		tempoutfilename = "\\network\\venusfirewall";
	}

	npos1 = outfilename.find(findzte);
	if((npos1 >= 0) && (npos2 >= 0))
	{
		tempoutfilename = "\\network\\zteswitch";
	}
	
	string newfolder = temppathname + tempoutfilename;
	if(!CreateFolder(newfolder))
	{
		return FALSE;
	}

	return TRUE;
}

void CResConverterDlg::OnBnClickedNetworkXmlToXml()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������

	string pathname("");
	pathname = OpenFolder();

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}
	MYLOG(pathname.c_str());

	VEC_STRING_DATAS outpath;
	outpath.clear();

	SearchFileInDirectroy(1, pathname, outpath);
	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����network��xml�ļ�!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\network";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("�����ļ���networkʧ��");
		return;
	}

	g_GlobalData.g_faildatas.clear();

	string kpifilename = openfolder + "\\network_kpiinfo.xml";
	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		string filepathname = *it;
		int beginpos = filepathname.find_last_of('\\');
		int endpos = filepathname.find_last_of('.');
		string allfilename = filepathname.substr(beginpos + 1, filepathname.length() - beginpos - 1);

		if(!CheckNetworkFilename(allfilename))
		{
			continue;
		}
		allfilename = openfolder + "\\" + allfilename;

		string filename = filepathname.substr(beginpos + 1, endpos - beginpos - 1);
		string outfilename = filename;
		outfilename = LetterTolower(replace_all_distinct(outfilename, "_", ""));
		string newfolder = pathname + "\\network\\" + outfilename;
		outfilename = "\\network\\" + outfilename;

		if(m_ConverterType_ != XML_TO_EXCEL)
		{
			if(!NetworkCreateFolder(pathname, outfilename))
			{
				continue;
			}
		}

		NetworkXmlToXml m_network(pathname, allfilename, kpifilename, outfilename, m_ConverterType_);
	}

	if(m_ConverterType_ != XML_TO_XML)
	{
		stringstream loglog("");
		VEC_VEC_DATAS parsefaildata;
		parsefaildata.clear();
		parsefaildata = g_GlobalData.g_faildatas;
		int failcount = parsefaildata.size();

		for( int i = 1; failcount > 0; i++)
		{
			loglog.str("");
			loglog << "��[" << i << "]������,ʧ�ܸ���[" << failcount << "]." << flush;
			MYLOG(loglog.str().c_str());
			g_GlobalData.g_faildatas.clear();
			VEC_VEC_DATAS::iterator it = parsefaildata.begin();
			for( ; it != parsefaildata.end(); it++ )
			{
				NetworkXmlToXml m_network(it->at(0), it->at(1), it->at(2), it->at(3), XML_TO_EXCEL);
			}
			parsefaildata.clear();
			parsefaildata = g_GlobalData.g_faildatas;
			failcount = parsefaildata.size();
		}

	}

	newoutpath = "Network ������� \r\n�����ļ���·��" + newoutpath;
	AfxMessageBox(newoutpath.c_str());

}


void CResConverterDlg::OnBnClickedHostXmlToXml()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������
	string pathname("");
	pathname = OpenFolder();

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}

	MYLOG(pathname.c_str());

	VEC_STRING_DATAS outpath;
	outpath.clear();

	SearchFileInDirectroy(1, pathname, outpath);

	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����host��xml�ļ�!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\host4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("�����ļ���host4ʧ��");
		return;
	}

	g_GlobalData.g_faildatas.clear();
	string kpifilename = openfolder + "\\host_kpi_info.xml";
	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		string filepathname = *it;
		int beginpos = filepathname.find_last_of('\\');
		int endpos = filepathname.find_last_of('.');
		string allfilename = filepathname.substr(beginpos + 1, filepathname.length() - beginpos - 1);

		if(allfilename == "host_kpi_info.xml") 
		{
			continue;
		}
		allfilename = openfolder + "\\" + allfilename;

		string filename = filepathname.substr(beginpos + 1, endpos - beginpos - 1);
		string outfilename = filename;
		outfilename = LetterTolower(replace_all_distinct(outfilename, "_", ""));
		string newfolder = pathname + "\\host4\\" + outfilename;
		if(m_ConverterType_ != XML_TO_EXCEL)
		{
			if(!CreateFolder(newfolder))
			{
				continue;
			}
		}

		outfilename = "\\host4\\" + outfilename;

		int npos = allfilename.find("snmp");
		if(npos >= 0)
		{
			HostSnmpXmlToXml m_hostsnmp(pathname, allfilename, kpifilename, outfilename, m_ConverterType_);
		}
		else
		{
			HostXmlToXml m_host(pathname, allfilename, kpifilename, outfilename, m_ConverterType_);
		}
	}

	if(m_ConverterType_ != XML_TO_XML)
	{
		stringstream loglog("");
		VEC_VEC_DATAS parsefaildata;
		parsefaildata.clear();
		parsefaildata = g_GlobalData.g_faildatas;
		int failcount = parsefaildata.size();

		for( int i = 1; failcount > 0; i++)
		{
			loglog.str("");
			loglog << "��[" << i << "]������,ʧ�ܸ���[" << failcount << "]." << flush;
			MYLOG(loglog.str().c_str());
			g_GlobalData.g_faildatas.clear();
			VEC_VEC_DATAS::iterator it = parsefaildata.begin();
			for( ; it != parsefaildata.end(); it++ )
			{
				string allfilename = it->at(1);
				int npos = allfilename.find("snmp");
				if(npos >= 0)
				{
					HostSnmpXmlToXml m_hostsnmp(it->at(0), it->at(1), it->at(2), it->at(3), XML_TO_EXCEL);
				}
				else
				{
					HostXmlToXml m_host(it->at(0), it->at(1), it->at(2), it->at(3), XML_TO_EXCEL);
				}
			}
			parsefaildata.clear();
			parsefaildata = g_GlobalData.g_faildatas;
			failcount = parsefaildata.size();
		}

	}

	newoutpath = "Host ������� \r\n�����ļ���·��" + newoutpath;
	AfxMessageBox(newoutpath.c_str());
}

void CResConverterDlg::OnBnClickedDbXmlToXml()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������
	string pathname("");
	pathname = OpenFolder();

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}

	MYLOG(pathname.c_str());

	VEC_STRING_DATAS outpath;
	outpath.clear();

	SearchFileInDirectroy(1, pathname, outpath);
	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����database��xml�ļ�!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\db4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("�����ļ���database4ʧ��");
		return;
	}

	g_GlobalData.g_faildatas.clear();
	string kpifilename = openfolder + "\\db_kpi_info.xml";
	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		string filepathname = *it;
		int beginpos = filepathname.find_last_of('\\');
		int endpos = filepathname.find_last_of('.');
		string allfilename = filepathname.substr(beginpos + 1, filepathname.length() - beginpos - 1);

		if(allfilename == "db_kpi_info.xml")
		{
			continue;
		}
		allfilename = openfolder + "\\" + allfilename;

		string filename = filepathname.substr(beginpos + 1, endpos - beginpos - 1);
		string outfilename = filename;
		outfilename = LetterTolower(replace_all_distinct(outfilename, "_", ""));
		string newfolder = pathname + "\\db4\\" + outfilename;
		if(m_ConverterType_ != XML_TO_EXCEL)
		{
			if(!CreateFolder(newfolder))
			{
				continue;
			}
		}

		outfilename = "\\db4\\" + outfilename;
		DBXmlToXml m_datebase(pathname, allfilename, kpifilename, outfilename, m_ConverterType_);
	}

	if(m_ConverterType_ != XML_TO_XML)
	{
		stringstream loglog("");
		VEC_VEC_DATAS parsefaildata;
		parsefaildata.clear();
		parsefaildata = g_GlobalData.g_faildatas;
		int failcount = parsefaildata.size();

		for( int i = 1; failcount > 0; i++)
		{
			loglog.str("");
			loglog << "��[" << i << "]������,ʧ�ܸ���[" << failcount << "]." << flush;
			MYLOG(loglog.str().c_str());
			g_GlobalData.g_faildatas.clear();
			VEC_VEC_DATAS::iterator it = parsefaildata.begin();
			for( ; it != parsefaildata.end(); it++ )
			{
				DBXmlToXml m_datebase(it->at(0), it->at(1), it->at(2), it->at(3), XML_TO_EXCEL);
			}
			parsefaildata.clear();
			parsefaildata = g_GlobalData.g_faildatas;
			failcount = parsefaildata.size();
		}

	}

	newoutpath = "DataBase ������� \r\n�����ļ���·��" + newoutpath;
	AfxMessageBox(newoutpath.c_str());

}


void CResConverterDlg::ApplicationFilenameIni(MAP_STRING_DATAS &applicationfilename)
{
	applicationfilename["DirectoryServer_IBM.xml"] = "kpiinfo_ibmDirectoryServer.xml";
	applicationfilename["DirectoryServerSUN.xml"] = "kpiinfo_sunDirectoryServer.xml";
	applicationfilename["Domino.xml"] = "kpiinfo_domino.xml";
	applicationfilename["Exchange2003.xml"] = "kpiinfo_exchange2003.xml";
	applicationfilename["Exchange2007.xml"] = "kpiinfo_exchange2007.xml";
}

void CResConverterDlg::OnBnClickedApplicationXmlToXml()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������

	//GetNetworkSysoid();
	string pathname("");
	pathname = OpenFolder();

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}

	MYLOG(pathname.c_str());

	VEC_STRING_DATAS outpath;
	outpath.clear();

	SearchFileInDirectroy(1, pathname, outpath);
	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����application��xml�ļ�!");
		return;
	}


	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\application4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("�����ļ���application4ʧ��");
		return;
	}

	MAP_STRING_DATAS applicationfilename;
	applicationfilename.clear();

	//��ʼ��application���ļ���
	ApplicationFilenameIni(applicationfilename);

	g_GlobalData.g_faildatas.clear();

	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		string filepathname = *it;
		int beginpos = filepathname.find_last_of('\\');
		int endpos = filepathname.find_last_of('.');
		string allfilename = filepathname.substr(beginpos + 1, filepathname.length() - beginpos - 1);
		string kpifilename("");

		//�ж��Ƿ���kpi��xml��������ǣ��Ž���
		MAP_STRING_DATAS::iterator itkpifilename = applicationfilename.find(allfilename);
		if (itkpifilename == applicationfilename.end())
		{
			continue;
		}

		kpifilename = openfolder + "\\" + applicationfilename[allfilename];
		allfilename = openfolder + "\\" + allfilename;
		

		string filename = filepathname.substr(beginpos + 1, endpos - beginpos - 1);
		string outfilename = filename;
		outfilename = LetterTolower(replace_all_distinct(outfilename, "_", ""));
		string newfolder = pathname + "\\application4\\" + outfilename;
		if(m_ConverterType_ != XML_TO_EXCEL)
		{
			if(!CreateFolder(newfolder))
			{
				continue;
			}
		}

		outfilename = "\\application4\\" + outfilename;
		ApplicationXmlToXml m_application(pathname, allfilename, kpifilename, outfilename, m_ConverterType_);
	}

	if(m_ConverterType_ != XML_TO_XML)
	{
		stringstream loglog("");
		VEC_VEC_DATAS parsefaildata;
		parsefaildata.clear();
		parsefaildata = g_GlobalData.g_faildatas;
		int failcount = parsefaildata.size();

		for( int i = 1; failcount > 0; i++)
		{
			loglog.str("");
			loglog << "��[" << i << "]������,ʧ�ܸ���[" << failcount << "]." << flush;
			MYLOG(loglog.str().c_str());
			g_GlobalData.g_faildatas.clear();
			VEC_VEC_DATAS::iterator it = parsefaildata.begin();
			for( ; it != parsefaildata.end(); it++ )
			{
				ApplicationXmlToXml m_application(it->at(0), it->at(1), it->at(2), it->at(3), XML_TO_EXCEL);
			}
			parsefaildata.clear();
			parsefaildata = g_GlobalData.g_faildatas;
			failcount = parsefaildata.size();
		}

	}
	
	newoutpath = "Application ������� \r\n�����ļ���·��" + newoutpath;
	AfxMessageBox(newoutpath.c_str());

}


void CResConverterDlg::MiddlewareFilenameIni(MAP_STRING_DATAS &middlewarefilename)
{
	middlewarefilename["apache2.2.22.xml"] = "kpiinfo_apache.xml";
	middlewarefilename["apache2.2.25.xml"] = "kpiinfo_apache.xml";

	middlewarefilename["ibmmq.xml"] = "kpiinfo_ibmmq.xml";

	middlewarefilename["iis.xml"] = "kpiinfo_iis.xml";

	middlewarefilename["JBoss6.0.0.M2.After.xml"] = "kpiinfo_jboss.xml";
	middlewarefilename["JBoss6.0.0.M2.Before.xml"] = "kpiinfo_jboss.xml";

	middlewarefilename["MW_CICS.xml"] = "kpiinfo_MW_CICS.xml";

	middlewarefilename["MW_TUXEDO_CLI.xml"] = "kpiinfo_MW_TUXEDO.xml";

	middlewarefilename["OracleAS.xml"] = "kpiinfo_as.xml";

	middlewarefilename["resin.xml"] = "kpiinfo_resin.xml";

	middlewarefilename["sunjes.xml"] = "kpiinfo_sunjes.xml";

	middlewarefilename["tlq.xml"] = "kpiinfo_tlq.xml";

	middlewarefilename["tomcat5.xml"] = "kpiinfo_tomcat.xml";
	middlewarefilename["tomcat6.xml"] = "kpiinfo_tomcat.xml";
	middlewarefilename["tomcat7.xml"] = "kpiinfo_tomcat.xml";
	middlewarefilename["tomcat8.xml"] = "kpiinfo_tomcat.xml";

	middlewarefilename["was.xml"] = "kpiinfo_was.xml";

	middlewarefilename["weblogic.xml"] = "kpiinfo_weblogic.xml";
	middlewarefilename["weblogic8.xml"] = "kpiinfo_weblogic.xml";

	middlewarefilename["wps.xml"] = "kpiinfo_wps.xml";
}


void CResConverterDlg::OnBnClickedMiddlewareXmlToXml()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������

	string pathname("");
	pathname = OpenFolder();

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}

	MYLOG(pathname.c_str());

	VEC_STRING_DATAS outpath;
	outpath.clear();

	SearchFileInDirectroy(1, pathname, outpath);
	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����middleware��xml�ļ�!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\middleware4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("�����ļ���middleware4ʧ��");
		return;
	}

	MAP_STRING_DATAS middlewarefilename;
	middlewarefilename.clear();

	//��ʼ��middleware���ļ����Լ���Ӧ��kpi�ļ���
	MiddlewareFilenameIni(middlewarefilename);

	g_GlobalData.g_faildatas.clear();
	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		string filepathname = *it;
		int beginpos = filepathname.find_last_of('\\');
		int endpos = filepathname.find_last_of('.');
		string allfilename = filepathname.substr(beginpos + 1, filepathname.length() - beginpos - 1);
		string kpifilename("");

		//�ж��Ƿ�����Ҫ�������ļ�
		MAP_STRING_DATAS::iterator itfilename = middlewarefilename.find(allfilename);
		if(itfilename == middlewarefilename.end())
		{
			continue;
		}

		kpifilename = openfolder + "\\" + middlewarefilename[allfilename];
		allfilename = openfolder + "\\" + allfilename;

		string filename = filepathname.substr(beginpos + 1, endpos - beginpos - 1);
		string outfilename = filename;
		outfilename = LetterTolower(replace_all_distinct(outfilename, "_", ""));
		string newfolder = pathname + "\\middleware4\\" + outfilename;
		if(m_ConverterType_ != XML_TO_EXCEL)
		{
			if(!CreateFolder(newfolder))
			{
				continue;
			}
		}		

		outfilename = "\\middleware4\\" + outfilename;
		MiddlewareXmlToXml m_middleware(pathname, allfilename, kpifilename, outfilename, m_ConverterType_);
	}

	if(m_ConverterType_ != XML_TO_XML)
	{
		stringstream loglog("");
		VEC_VEC_DATAS parsefaildata;
		parsefaildata.clear();
		parsefaildata = g_GlobalData.g_faildatas;
		int failcount = parsefaildata.size();

		for( int i = 1; failcount > 0; i++)
		{
			loglog.str("");
			loglog << "��[" << i << "]������,ʧ�ܸ���[" << failcount << "]." << flush;
			MYLOG(loglog.str().c_str());
			g_GlobalData.g_faildatas.clear();
			VEC_VEC_DATAS::iterator it = parsefaildata.begin();
			for( ; it != parsefaildata.end(); it++ )
			{
				MiddlewareXmlToXml m_middleware(it->at(0), it->at(1), it->at(2), it->at(3), XML_TO_EXCEL);
			}
			parsefaildata.clear();
			parsefaildata = g_GlobalData.g_faildatas;
			failcount = parsefaildata.size();
		}

	}

	newoutpath = "Middleware ������� \r\n�����ļ���·��" + newoutpath;
	AfxMessageBox(newoutpath.c_str());

}



void CResConverterDlg::GetNetworkSysoid()
{
	string pathname("");
	pathname = OpenFolder();

	//д�ļ�
	string writefilename = "d:\\NetworkSysoid.txt";
	locale::global(locale(""));//��ȫ��������Ϊ����ϵͳĬ������ 
	ofstream sysoidwrite(writefilename.c_str());
	locale::global(locale("C"));//��ԭȫ�������趨 

	if(pathname.empty())
	{
		MYLOG("chance path is empty.");
		return ;
	}

	VEC_STRING_DATAS outpath;

	string networkpath = pathname + "\\networkmodel";
	SearchFileInDirectroy(1, networkpath, outpath);

	if(outpath.empty())
	{
		AfxMessageBox("û���ҵ�����ת����network��xml�ļ�!");
		return;
	}

	VEC_STRING_DATAS::iterator it = outpath.begin();
	for( ; it != outpath.end(); ++it)
	{
		string resourceid("");
		string filepathname = *it;
		int beginpos = filepathname.find_last_of('\\');
		int endpos = filepathname.find_last_of('.');
		string allfilename = filepathname.substr(beginpos + 1, filepathname.length() - beginpos - 1);

		if(!CheckNetworkFilename(allfilename))
		{
			continue;
		}


		VEC_STRING_DATAS devicetypeinfodata;
		devicetypeinfodata.clear();

		file<> fdoc(filepathname.c_str());
		xml_document<>   doc;
		doc.parse<0>(fdoc.data());

		//! ��ȡ���ڵ�
		xml_node<>* root = doc.first_node();

		//! ��ȡ���ڵ��һ���ڵ�	
		for(rapidxml::xml_node<char> * devicetypeinfo = root->first_node("devicetypeinfo");
			devicetypeinfo != NULL;
			devicetypeinfo = devicetypeinfo->next_sibling())
		{

			/********************************************************************
			����devicetypeinfo�ڵ������
			**********************************************************************/
			for(rapidxml::xml_attribute<char> * attr = devicetypeinfo->first_attribute();
				attr != NULL;
				attr = attr->next_attribute())
			{
				devicetypeinfodata.push_back(UTF8ToGB(attr->value()));
			}

			resourceid = replace_all_distinct(devicetypeinfodata[0], "_", "");

			for(rapidxml::xml_node<char> * devicetypes = devicetypeinfo->first_node("devicetypes");
				devicetypes != NULL;
				devicetypes = devicetypes->next_sibling())
			{
				for(rapidxml::xml_node<char> * devicetype = devicetypes->first_node("devicetype");
					devicetype != NULL;
					devicetype = devicetype->next_sibling())
				{
					for(rapidxml::xml_attribute<char> * attr2 = devicetype->first_attribute("sysoid");
						attr2 != NULL;
						attr2 = attr2->next_attribute())
					{
						//devicetypeinfodata.push_back(UTF8ToGB(attr->value()));
						string sysoid = UTF8ToGB(attr2->value());
						sysoidwrite << resourceid << "," << sysoid << endl << flush;
					}
				}
			}
		}

	}
	sysoidwrite.close();
	AfxMessageBox("GetNetworkSysoid successful.");
}


void CResConverterDlg::OnBnClickedRadio1()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������
	m_ConverterType_ = XML_TO_XML;
}

void CResConverterDlg::OnBnClickedRadio2()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������
	m_ConverterType_ = XML_TO_EXCEL;
}

void CResConverterDlg::OnBnClickedRadio3()
{
	// TODO: �ڴ���ӿؼ�֪ͨ����������
	m_ConverterType_ = XML_TO_XML_EXCEL;
}
