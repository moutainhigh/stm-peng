/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		GlobalData.h
Description:	转换工具的一些基础的函数和全局变量
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
	MAP_VEC_DATAS Excleinfo_;			//标准excel文件的sheet页的名称和表头（列名）
	VEC_STRING_DATAS Sheetnameinfo_;	//标准excel文件的sheet页名称

	
	VEC_VEC_DATAS PCALDatas_;			//oc4 的collect.xml 中 PluginClassAliasList 标签初始化
	VEC_VEC_DATAS RGMSDatas_;			//oc4 的resource.xml 中 GlobalMetricSetting 标签初始化
	
	//失败的记录集合
	VEC_VEC_DATAS g_faildatas;

	int		g_threadcount;
};

extern GlobalData g_GlobalData;


/*
** 功能：	将GB转为UTF8
** 输入：	需要转换的char 
** 输出：	无
** 返回值： 转换后的string
*/
inline std::string GBToUTF8(const char* str)
{
	std::string result;
	WCHAR *strSrc;
	TCHAR *szRes;

	//获得临时变量的大小, gb2312 to unicode
	int i = MultiByteToWideChar(CP_ACP, 0, str, -1, NULL, 0);
	strSrc = new WCHAR[i+1];
	memset(strSrc, 0, i+1);
	MultiByteToWideChar(CP_ACP, 0, str, -1, strSrc, i);

	//获得临时变量的大小, unicode to utf8
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
** 功能：	将UTF8转为GB
** 输入：	需要转换的char 
** 输出：	无
** 返回值： 转换后的string
*/
inline std::string UTF8ToGB(const char* str)
{
	std::string result;
	WCHAR *strSrc;

	//获得临时变量的大小
	int i = MultiByteToWideChar(CP_UTF8, 0, str, -1, NULL, 0);
	strSrc = new WCHAR[i+1];
	memset(strSrc, 0, i+1);
	MultiByteToWideChar(CP_UTF8, 0, str, -1, strSrc, i);

	// WCHAR *转string
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
** 功能：	检查文件夹是否存在
** 输入：	需要检查的 文件夹路径
** 输出：	无
** 返回值： TRUE：存在；  FALSE：不存在
*/
BOOL CheckFolderExist(const string &strPath);

/*
** 功能：	检查文件是否存在
** 输入：	需要检查的 文件路径
** 输出：	无
** 返回值： TRUE：存在；  FALSE：不存在
*/
BOOL CheckFileExist(const string &strfilename);

/*
** 功能：	将string前的空格去掉
** 输入：	需要处理的string
** 输出：	无
** 返回值： 处理后的string
*/
std::string str_trim_left(const std::string& str);

/*
** 功能：	将string后的空格去掉
** 输入：	需要处理的string
** 输出：	无
** 返回值： 处理后的string
*/
std::string str_trim_right(const std::string& str);

/*
** 功能：	将string前后的空格去掉
** 输入：	需要处理的string
** 输出：	无
** 返回值： 处理后的string
*/
std::string str_trim(const std::string& str);

/*
** 功能：	将string以固定的字符分解为vec
** 输入：	需要处理的string， 固定的字符 char c， 是否去掉前后空格 non_space
** 输出：	无
** 返回值： 处理后的vector
*/
std::vector<std::string> split_str_bychar(const std::string & str, char c, int non_space = 0);

/*
** 功能：	将string以固定的字符分解为vec
** 输入：	需要处理的string， 固定的字符串 findstr
** 输出：	处理后的vector  vecdata
** 返回值： 无
*/
void spilt_str_bystr(string strvalue, string findstr, VEC_STRING_DATAS &vecdata);

/*
** 功能：	将string全部转为小写
** 输入：	需要处理的string
** 输出：	无
** 返回值： 处理后的string
*/
string LetterTolower(string input);

/*
** 功能：	将string全部转为大写
** 输入：	需要处理的string
** 输出：	无
** 返回值： 处理后的string
*/
string LetterToupper(string input);

/*
** 功能：	判断是否是数字
** 输入：	需要判断的 char ch
** 输出：	无
** 返回值： TRUE：是；  FALSE：不是
*/
BOOL IsNumber(char ch);

/*
** 功能：	判断是否是数字或者字母
** 输入：	需要判断的 char ch
** 输出：	无
** 返回值： TRUE：是；  FALSE：不是
*/
BOOL IsNumberAndLetter(char ch);

/*
** 功能：	将字符串中的一部分内容进行全部替换，根据新的字符串替换老的字符串
** 输入：	需要处理的字符串string &str， 需要替换的字符串 string &old_value ,替换为的字符串 string &new_value
** 输出：	替换后的字符串string &str
** 返回值： 替换后的字符串string &
*/
string& replace_all_distinct(string &str, const string &old_value, const string &new_value);

/*
** 功能：	对VEC_VEC_DATAS 进行排序，根据VEC_STRING_DATAS的第一个值进行排序
** 输入：	需要排序的VEC_VEC_DATAS &datas
** 输出：	排序后的VEC_VEC_DATAS &datas
** 返回值： 无
*/
void SortVecVec(VEC_VEC_DATAS &datas);



#endif


