
// ResConverterDlg.h : 头文件
//

#pragma once


// CResConverterDlg 对话框
class CResConverterDlg : public CDialog
{
// 构造
public:
	CResConverterDlg(CWnd* pParent = NULL);	// 标准构造函数

// 对话框数据
	enum { IDD = IDD_RESCONVERTER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV 支持


// 实现
protected:
	HICON m_hIcon;

	// 生成的消息映射函数
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnBnClickedOpenFile();
	afx_msg void OnBnClickedOpenFolder();
	afx_msg void OnBnClickedNetworkXmlToXml();
	afx_msg void OnBnClickedHostXmlToXml();
	afx_msg void OnBnClickedDbXmlToXml();
	afx_msg void OnBnClickedApplicationXmlToXml();
	afx_msg void OnBnClickedMiddlewareXmlToXml();
	afx_msg void OnBnClickedRadio1();
	afx_msg void OnBnClickedRadio2();
	afx_msg void OnBnClickedRadio3();

private:
	//void ResPathInit();
	//BOOL CreateResFolder(const string &Path);	
	//void TempResPathInit();

	/*
	** 功能：	打开一个文件夹
	** 输入：	无
	** 输出：	无
	** 返回值： 打开的文件夹路径
	*/
	string OpenFolder();

	/*
	** 功能：	创建文件夹，如果文件夹已经存在，则不创建
	** 输入：	需要创建的文件夹路径 Path
	** 输出：	无
	** 返回值： TRUE：文件夹存在或者创建成功；  FALSE：创建失败
	*/
	BOOL CreateFolder(const string Path);

	/*
	** 功能：	检查文件夹，将符合要求的所有文件筛选出来
	** 输入：	需要筛选的文件种类 filetype
				需要检查的文件夹路径 dir
	** 输出：	符合标准文件集合outList
	** 返回值： 无
	*/
	void SearchFileInDirectroy(int filetype, const string& dir, VEC_STRING_DATAS &outList);

	/*
	** 功能：	network的文件检查，将public和kpi文件的筛选
	** 输入：	需要检查的文件名 filename
	** 输出：	无
	** 返回值： TRUE：可以；  FALSE：不可以
	*/
	BOOL CheckNetworkFilename(string filename);

	/*
	** 功能：	应用application所有文件名的初始化
	** 输入：	无
	** 输出：	中间件所有文件名的集合 applicationfilename
	** 返回值： 无
	*/
	void ApplicationFilenameIni(MAP_STRING_DATAS &applicationfilename);

	/*
	** 功能：	中间件middleware所有文件名的初始化
	** 输入：	无
	** 输出：	中间件所有文件名的集合 middlewarefilename
	** 返回值： 无
	*/
	void MiddlewareFilenameIni(MAP_STRING_DATAS &middlewarefilename);

	
	/*
	** 功能：	调试使用：Network中将sysoid读取出来，与resourceid对应，然后生成一个本地文件
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void GetNetworkSysoid();

	/*
	** 功能：	根据路径和文件名创建文件夹
	** 输入：	路径名 pathname
				文件名 outfilename
	** 输出：	无
	** 返回值： TRUE：创建文件夹成功；  FALSE：创建文件夹失败
	*/
	BOOL NetworkCreateFolder(const string pathname, const string outfilename);

private:
	//VEC_STRING_DATAS ResPath_;		//resource文件的目录
	int m_ConverterType_;					//转换类型
	stringstream loglog;			//写日志专用	
};


//工作线程参数
class ThreadParam
{
public:
	ThreadParam(void)
	{
		pathname = "";
		filename = "";
		outfilename = "";
	}

	~ThreadParam(void)
	{
	}

public:
	std::string		pathname;
	std::string		filename;
	std::string		outfilename;
};


