
// ResConverterDlg.cpp : 实现文件
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


// 用于应用程序“关于”菜单项的 CAboutDlg 对话框

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// 对话框数据
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

// 实现
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


// CResConverterDlg 对话框




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


// CResConverterDlg 消息处理程序

BOOL CResConverterDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// 将“关于...”菜单项添加到系统菜单中。

	// IDM_ABOUTBOX 必须在系统命令范围内。
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

	// 设置此对话框的图标。当应用程序主窗口不是对话框时，框架将自动
	//  执行此操作
	SetIcon(m_hIcon, TRUE);			// 设置大图标
	SetIcon(m_hIcon, FALSE);		// 设置小图标

	// TODO: 在此添加额外的初始化代码

	((CButton *)GetDlgItem(IDC_RADIO1))->SetCheck(TRUE);
	m_ConverterType_ = XML_TO_XML;

	//初始化OneCenter4.0的目录结构
	//ResPathInit();

	return TRUE;  // 除非将焦点设置到控件，否则返回 TRUE
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

// 如果向对话框添加最小化按钮，则需要下面的代码
//  来绘制该图标。对于使用文档/视图模型的 MFC 应用程序，
//  这将由框架自动完成。

void CResConverterDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 用于绘制的设备上下文

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 使图标在工作区矩形中居中
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 绘制图标
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

//当用户拖动最小化窗口时系统调用此函数取得光标
//显示。
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
			loglog << "创建文件夹[" << Path << "]失败.";
			MYLOG(loglog.str().c_str());
			//AfxMessageBox(loglog.str().c_str());
			return FALSE;
		}
	}	

	//loglog.str("");
	//loglog << "创建文件夹[" << Path << "]成功.";
	//MYLOG(loglog.str().c_str());
	
	return TRUE;
}




string CResConverterDlg::OpenFolder()
{
	string pathname("");
	BROWSEINFO bi;
	ZeroMemory(&bi,sizeof(BROWSEINFO));
	//添加提示语句
	bi.lpszTitle= _T("请选择文件夹");
	//添加"新建文件夹项"和"编辑框"
	bi.ulFlags = BIF_NEWDIALOGSTYLE | BIF_EDITBOX;
	bi.hwndOwner = *this;

	// 显示一个对话框让让用户选择文件夹
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
	// TODO: 在此添加控件通知处理程序代码
	CString pathfilename("");
	stringstream pathfile("");

	CFileDialog dlg(TRUE, "*", "", OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT, "Excel Files(*.xlsx)|*.xlsx|All Files (*.*)|*.*||", this );

	dlg.m_pOFN->lpstrTitle = "请打开正确的Excel文件"; 

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
		loglog << "Excel转为XML处理结束" << endl << "生成文件路径 " << outpathfile << flush;
		AfxMessageBox(loglog.str().c_str());
	}
	else
	{
		//没有选中任何文件
		//不做任何处理
		MYLOG("Cancel or Close!");
	}

}

void CResConverterDlg::OnBnClickedOpenFolder()
{
	// TODO: 在此添加控件通知处理程序代码

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

	//2007和2010的excel 文件
	SearchFileInDirectroy(2, pathname, outpath);

	//2003的excel 文件
	SearchFileInDirectroy(3, pathname, outpath);

	if(outpath.empty())
	{
		AfxMessageBox("没有找到可以转换的excel文件!");
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
	loglog << "Excel批量转换为XML处理结束" << endl << "生成文件路径 " << pathname << "\\" << OUT_XML_PATH << flush;
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
	// TODO: 在此添加控件通知处理程序代码

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
		AfxMessageBox("没有找到可以转换的network的xml文件!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\network";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("创建文件夹network失败");
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
			loglog << "第[" << i << "]次重试,失败个数[" << failcount << "]." << flush;
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

	newoutpath = "Network 处理结束 \r\n生成文件的路径" + newoutpath;
	AfxMessageBox(newoutpath.c_str());

}


void CResConverterDlg::OnBnClickedHostXmlToXml()
{
	// TODO: 在此添加控件通知处理程序代码
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
		AfxMessageBox("没有找到可以转换的host的xml文件!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\host4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("创建文件夹host4失败");
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
			loglog << "第[" << i << "]次重试,失败个数[" << failcount << "]." << flush;
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

	newoutpath = "Host 处理结束 \r\n生成文件的路径" + newoutpath;
	AfxMessageBox(newoutpath.c_str());
}

void CResConverterDlg::OnBnClickedDbXmlToXml()
{
	// TODO: 在此添加控件通知处理程序代码
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
		AfxMessageBox("没有找到可以转换的database的xml文件!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\db4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("创建文件夹database4失败");
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
			loglog << "第[" << i << "]次重试,失败个数[" << failcount << "]." << flush;
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

	newoutpath = "DataBase 处理结束 \r\n生成文件的路径" + newoutpath;
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
	// TODO: 在此添加控件通知处理程序代码

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
		AfxMessageBox("没有找到可以转换的application的xml文件!");
		return;
	}


	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\application4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("创建文件夹application4失败");
		return;
	}

	MAP_STRING_DATAS applicationfilename;
	applicationfilename.clear();

	//初始化application的文件名
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

		//判断是否是kpi的xml，如果不是，才解析
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
			loglog << "第[" << i << "]次重试,失败个数[" << failcount << "]." << flush;
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
	
	newoutpath = "Application 处理结束 \r\n生成文件的路径" + newoutpath;
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
	// TODO: 在此添加控件通知处理程序代码

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
		AfxMessageBox("没有找到可以转换的middleware的xml文件!");
		return;
	}

	int npos = pathname.find_last_of('\\');
	string openfolder = pathname.substr(npos, pathname.length() - npos);
	pathname = pathname.substr(0, npos);

	string newoutpath = pathname + "\\middleware4";
	if(!CreateFolder(newoutpath))
	{
		AfxMessageBox("创建文件夹middleware4失败");
		return;
	}

	MAP_STRING_DATAS middlewarefilename;
	middlewarefilename.clear();

	//初始化middleware的文件名以及对应的kpi文件名
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

		//判断是否是需要解析的文件
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
			loglog << "第[" << i << "]次重试,失败个数[" << failcount << "]." << flush;
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

	newoutpath = "Middleware 处理结束 \r\n生成文件的路径" + newoutpath;
	AfxMessageBox(newoutpath.c_str());

}



void CResConverterDlg::GetNetworkSysoid()
{
	string pathname("");
	pathname = OpenFolder();

	//写文件
	string writefilename = "d:\\NetworkSysoid.txt";
	locale::global(locale(""));//将全局区域设为操作系统默认区域 
	ofstream sysoidwrite(writefilename.c_str());
	locale::global(locale("C"));//还原全局区域设定 

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
		AfxMessageBox("没有找到可以转换的network的xml文件!");
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

		//! 获取根节点
		xml_node<>* root = doc.first_node();

		//! 获取根节点第一个节点	
		for(rapidxml::xml_node<char> * devicetypeinfo = root->first_node("devicetypeinfo");
			devicetypeinfo != NULL;
			devicetypeinfo = devicetypeinfo->next_sibling())
		{

			/********************************************************************
			解析devicetypeinfo节点的属性
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
	// TODO: 在此添加控件通知处理程序代码
	m_ConverterType_ = XML_TO_XML;
}

void CResConverterDlg::OnBnClickedRadio2()
{
	// TODO: 在此添加控件通知处理程序代码
	m_ConverterType_ = XML_TO_EXCEL;
}

void CResConverterDlg::OnBnClickedRadio3()
{
	// TODO: 在此添加控件通知处理程序代码
	m_ConverterType_ = XML_TO_XML_EXCEL;
}
