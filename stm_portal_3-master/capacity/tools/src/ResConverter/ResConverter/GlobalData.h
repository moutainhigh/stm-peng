/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		GlobalData.h
Description:	ת�����ߵ�һЩ�����ĺ�����ȫ�ֱ���
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/

#ifndef _GLOBAL_DATA_H_
#define _GLOBAL_DATA_H_

typedef set<int>								SET_INT_DATAS;
typedef set<std::string>						SET_STRING_DATAS;
typedef vector<std::string>						VEC_STRING_DATAS;
typedef map<std::string, std::string>			MAP_STRING_DATAS;
typedef multimap<int, std::string>				MAP_INT_STRING_DATAS;
typedef multimap<std::string, VEC_STRING_DATAS>	MAP_STRING_VEC_DATAS;
typedef vector<VEC_STRING_DATAS>				VEC_VEC_DATAS;
typedef map<std::string, VEC_STRING_DATAS>		MAP_VEC_DATAS;
typedef map<std::string, VEC_VEC_DATAS>			MAP_VEC_VEC_DATAS;

#define XML_TO_XML_EXCEL 0
#define XML_TO_XML		 1
#define XML_TO_EXCEL	 2

#define KPI_INFO_COUNT   18

#define COVERTER_IS_ENCRYPT	"FALSE"
#define COVERTER_VERSION	"4.1.0"

class GlobalData  
{
public:
	GlobalData();
	virtual ~GlobalData();
private:
	void Init();
	void InitDatas();

public:
	MAP_VEC_DATAS Excleinfo_;			//��׼excel�ļ���sheetҳ�����ƺͱ�ͷ��������
	VEC_STRING_DATAS Sheetnameinfo_;	//��׼excel�ļ���sheetҳ����

	
	VEC_VEC_DATAS PCALDatas_;			//oc4 ��collect.xml �� PluginClassAliasList ��ǩ��ʼ��
	VEC_VEC_DATAS RGMSDatas_;			//oc4 ��resource.xml �� GlobalMetricSetting ��ǩ��ʼ��
	
	//ʧ�ܵļ�¼����
	VEC_VEC_DATAS g_faildatas;

	int		g_threadcount;
};

extern GlobalData g_GlobalData;


/*
** ���ܣ�	��GBתΪUTF8
** ���룺	��Ҫת����char 
** �����	��
** ����ֵ�� ת�����string
*/
inline std::string GBToUTF8(const char* str)
{
	std::string result;
	WCHAR *strSrc;
	TCHAR *szRes;

	//�����ʱ�����Ĵ�С, gb2312 to unicode
	int i = MultiByteToWideChar(CP_ACP, 0, str, -1, NULL, 0);
	strSrc = new WCHAR[i+1];
	memset(strSrc, 0, i+1);
	MultiByteToWideChar(CP_ACP, 0, str, -1, strSrc, i);

	//�����ʱ�����Ĵ�С, unicode to utf8
	i = WideCharToMultiByte(CP_UTF8, 0, strSrc, -1, NULL, 0, NULL, NULL);
	szRes = new TCHAR[i+1];
	memset(szRes, 0, i+1);
	int j=WideCharToMultiByte(CP_UTF8, 0, strSrc, -1, szRes, i, NULL, NULL);

	result = szRes;
	delete []strSrc;
	delete []szRes;

	return result;
}

/*
** ���ܣ�	��UTF8תΪGB
** ���룺	��Ҫת����char 
** �����	��
** ����ֵ�� ת�����string
*/
inline std::string UTF8ToGB(const char* str)
{
	std::string result;
	WCHAR *strSrc;

	//�����ʱ�����Ĵ�С
	int i = MultiByteToWideChar(CP_UTF8, 0, str, -1, NULL, 0);
	strSrc = new WCHAR[i+1];
	memset(strSrc, 0, i+1);
	MultiByteToWideChar(CP_UTF8, 0, str, -1, strSrc, i);

	// WCHAR *תstring
	DWORD dwNum = WideCharToMultiByte(CP_OEMCP,NULL,strSrc,-1,NULL,0,NULL,FALSE);		
	char *psText;	
	psText = new char[dwNum+1];	
	memset(psText, 0, dwNum+1);
	WideCharToMultiByte (CP_OEMCP,NULL,strSrc,-1,psText,dwNum,NULL,FALSE);	
	result = psText;
	delete []psText;
	delete []strSrc;

	return result;
}

/*
** ���ܣ�	����ļ����Ƿ����
** ���룺	��Ҫ���� �ļ���·��
** �����	��
** ����ֵ�� TRUE�����ڣ�  FALSE��������
*/
BOOL CheckFolderExist(const string &strPath);

/*
** ���ܣ�	����ļ��Ƿ����
** ���룺	��Ҫ���� �ļ�·��
** �����	��
** ����ֵ�� TRUE�����ڣ�  FALSE��������
*/
BOOL CheckFileExist(const string &strfilename);

/*
** ���ܣ�	��stringǰ�Ŀո�ȥ��
** ���룺	��Ҫ�����string
** �����	��
** ����ֵ�� ������string
*/
std::string str_trim_left(const std::string& str);

/*
** ���ܣ�	��string��Ŀո�ȥ��
** ���룺	��Ҫ�����string
** �����	��
** ����ֵ�� ������string
*/
std::string str_trim_right(const std::string& str);

/*
** ���ܣ�	��stringǰ��Ŀո�ȥ��
** ���룺	��Ҫ�����string
** �����	��
** ����ֵ�� ������string
*/
std::string str_trim(const std::string& str);

/*
** ���ܣ�	��string�Թ̶����ַ��ֽ�Ϊvec
** ���룺	��Ҫ�����string�� �̶����ַ� char c�� �Ƿ�ȥ��ǰ��ո� non_space
** �����	��
** ����ֵ�� ������vector
*/
std::vector<std::string> split_str_bychar(const std::string & str, char c, int non_space = 0);

/*
** ���ܣ�	��string�Թ̶����ַ��ֽ�Ϊvec
** ���룺	��Ҫ�����string�� �̶����ַ��� findstr
** �����	������vector  vecdata
** ����ֵ�� ��
*/
void spilt_str_bystr(string strvalue, string findstr, VEC_STRING_DATAS &vecdata);

/*
** ���ܣ�	��stringȫ��תΪСд
** ���룺	��Ҫ�����string
** �����	��
** ����ֵ�� ������string
*/
string LetterTolower(string input);

/*
** ���ܣ�	��stringȫ��תΪ��д
** ���룺	��Ҫ�����string
** �����	��
** ����ֵ�� ������string
*/
string LetterToupper(string input);

/*
** ���ܣ�	�ж��Ƿ�������
** ���룺	��Ҫ�жϵ� char ch
** �����	��
** ����ֵ�� TRUE���ǣ�  FALSE������
*/
BOOL IsNumber(char ch);

/*
** ���ܣ�	�ж��Ƿ������ֻ�����ĸ
** ���룺	��Ҫ�жϵ� char ch
** �����	��
** ����ֵ�� TRUE���ǣ�  FALSE������
*/
BOOL IsNumberAndLetter(char ch);

/*
** ���ܣ�	���ַ����е�һ�������ݽ���ȫ���滻�������µ��ַ����滻�ϵ��ַ���
** ���룺	��Ҫ������ַ���string &str�� ��Ҫ�滻���ַ��� string &old_value ,�滻Ϊ���ַ��� string &new_value
** �����	�滻����ַ���string &str
** ����ֵ�� �滻����ַ���string &
*/
string& replace_all_distinct(string &str, const string &old_value, const string &new_value);

/*
** ���ܣ�	��VEC_VEC_DATAS �������򣬸���VEC_STRING_DATAS�ĵ�һ��ֵ��������
** ���룺	��Ҫ�����VEC_VEC_DATAS &datas
** �����	������VEC_VEC_DATAS &datas
** ����ֵ�� ��
*/
void SortVecVec(VEC_VEC_DATAS &datas);



#endif


