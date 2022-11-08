
// ResConverterDlg.h : ͷ�ļ�
//

#pragma once


// CResConverterDlg �Ի���
class CResConverterDlg : public CDialog
{
// ����
public:
	CResConverterDlg(CWnd* pParent = NULL);	// ��׼���캯��

// �Ի�������
	enum { IDD = IDD_RESCONVERTER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV ֧��


// ʵ��
protected:
	HICON m_hIcon;

	// ���ɵ���Ϣӳ�亯��
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
	** ���ܣ�	��һ���ļ���
	** ���룺	��
	** �����	��
	** ����ֵ�� �򿪵��ļ���·��
	*/
	string OpenFolder();

	/*
	** ���ܣ�	�����ļ��У�����ļ����Ѿ����ڣ��򲻴���
	** ���룺	��Ҫ�������ļ���·�� Path
	** �����	��
	** ����ֵ�� TRUE���ļ��д��ڻ��ߴ����ɹ���  FALSE������ʧ��
	*/
	BOOL CreateFolder(const string Path);

	/*
	** ���ܣ�	����ļ��У�������Ҫ��������ļ�ɸѡ����
	** ���룺	��Ҫɸѡ���ļ����� filetype
				��Ҫ�����ļ���·�� dir
	** �����	���ϱ�׼�ļ�����outList
	** ����ֵ�� ��
	*/
	void SearchFileInDirectroy(int filetype, const string& dir, VEC_STRING_DATAS &outList);

	/*
	** ���ܣ�	network���ļ���飬��public��kpi�ļ���ɸѡ
	** ���룺	��Ҫ�����ļ��� filename
	** �����	��
	** ����ֵ�� TRUE�����ԣ�  FALSE��������
	*/
	BOOL CheckNetworkFilename(string filename);

	/*
	** ���ܣ�	Ӧ��application�����ļ����ĳ�ʼ��
	** ���룺	��
	** �����	�м�������ļ����ļ��� applicationfilename
	** ����ֵ�� ��
	*/
	void ApplicationFilenameIni(MAP_STRING_DATAS &applicationfilename);

	/*
	** ���ܣ�	�м��middleware�����ļ����ĳ�ʼ��
	** ���룺	��
	** �����	�м�������ļ����ļ��� middlewarefilename
	** ����ֵ�� ��
	*/
	void MiddlewareFilenameIni(MAP_STRING_DATAS &middlewarefilename);

	
	/*
	** ���ܣ�	����ʹ�ã�Network�н�sysoid��ȡ��������resourceid��Ӧ��Ȼ������һ�������ļ�
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void GetNetworkSysoid();

	/*
	** ���ܣ�	����·�����ļ��������ļ���
	** ���룺	·���� pathname
				�ļ��� outfilename
	** �����	��
	** ����ֵ�� TRUE�������ļ��гɹ���  FALSE�������ļ���ʧ��
	*/
	BOOL NetworkCreateFolder(const string pathname, const string outfilename);

private:
	//VEC_STRING_DATAS ResPath_;		//resource�ļ���Ŀ¼
	int m_ConverterType_;					//ת������
	stringstream loglog;			//д��־ר��	
};


//�����̲߳���
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


