#include "StdAfx.h"

GlobalData g_GlobalData;

GlobalData::GlobalData()
{
	Init();
}

GlobalData::~GlobalData()
{
}

void GlobalData::Init()
{
	string sheetname[] = {"collectGlobalMetricSetting","PluginClassAliasList","MetricPlugin","resourceGlobalMetricSetting","Resource","Properties","Instantiation"};
	string CGMSColumName[] = {"����", "pluginid", "parameterId", "parameterValue", "parameterProperty", "propertyValue"};
	string PCALColumName[] = {"id", "class"};
	//string MPColumName[] = {"metricid", "pluginid", "collectType", "�ɼ�����", "���type", "���classKey", "typeֵ", "keyֵ", "valueֵ", "columnֵ", "Iscomment"};
	string MPColumName[] = {"metricid", "pluginid", "collectType", "�ɼ�����", "���type", "���classKey", "typeֵ", "keyֵ", "valueֵ", "columnֵ"};
	string RGMSColumName[] = {"�Ƿ�ɱ༭GlobalIsEdit", "�Ƿ���GlobalIsMonitor", "Ĭ�ϼ��Ƶ��GlobalDefaultMonitorFreq", "֧�ּ��Ƶ��GlobalsupportMonitorFreq", "�Ƿ�澯GlobalIsAlert", "GlobalDefaultFlapping"};
	//string RColumName[] = {"��Դid", "category", "icon", "��Դ����name", "��Դ����description", "��Դ����type", "����Դparentid", "ָ��id", "ָ��style", "ָ��name", "ָ������description", "ָ�����unit", "�Ƿ�չʾIsDisplay", "չʾ����displayOrder", "�Ƿ���IsMonitor", "�Ƿ�༭IsEdit", "Ĭ�ϼ��Ƶ��DefaultMonitorFreq", "֧�ּ��Ƶ��SupportMonitorFreq", "�Ƿ�澯IsAlert", "DefaultFlapping", "��ֵ����1", "��ֵ1", "��ֵ״̬1", "��ֵ����2", "��ֵ2", "��ֵ״̬2", "��ֵ����3", "��ֵ3", "��ֵ״̬3", "��ֵ����4", "��ֵ4", "��ֵ״̬4", "��ֵ����5", "��ֵ5", "��ֵ״̬5"};
	string RColumName[] = {"��Դid", "category", "icon", "��Դ����name", "��Դ����description", "��Դ����type", "����Դparentid", "ָ��id", "ָ��style", "ָ��name", "ָ������description", "ָ�����unit", "�Ƿ�չʾIsDisplay", "չʾ����displayOrder", "�Ƿ���IsMonitor", "�Ƿ�༭IsEdit", "Ĭ�ϼ��Ƶ��DefaultMonitorFreq", "֧�ּ��Ƶ��SupportMonitorFreq", "�Ƿ�澯IsAlert", "DefaultFlapping", "��ֵ����1", "��ֵ1", "��ֵ״̬1", "��ֵ����2", "��ֵ2", "��ֵ״̬2", "��ֵ����3", "��ֵ3", "��ֵ״̬3"};
	string PColumName[] = {"��Դid", "����id", "��������name", "ָ��metricid"};
	string IColumName[] = {"��Դid", "InstanceId", "InstanceName"};
	
	//��ʼ������Excleinfo sheetҳ����
	VEC_STRING_DATAS tmpsheetname(sheetname, sheetname + sizeof(sheetname)/sizeof(string));
	Sheetnameinfo_ = tmpsheetname;

	//��ʼ�� Excleinfo
	Excleinfo_.clear();
	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpCGMS(CGMSColumName, CGMSColumName + sizeof(CGMSColumName)/sizeof(string));
	Excleinfo_[(Sheetnameinfo_[0])] = tmpCGMS;

	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpPCAL(PCALColumName, PCALColumName + sizeof(PCALColumName)/sizeof(string));
	Excleinfo_[Sheetnameinfo_[1]] = tmpPCAL;

	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpMP(MPColumName, MPColumName + sizeof(MPColumName)/sizeof(string));
	Excleinfo_[Sheetnameinfo_[2]] = tmpMP;

	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpRGMS(RGMSColumName, RGMSColumName + sizeof(RGMSColumName)/sizeof(string));
	Excleinfo_[Sheetnameinfo_[3]] = tmpRGMS;

	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpR(RColumName, RColumName + sizeof(RColumName)/sizeof(string));
	//Excleinfo_.insert(make_pair(Sheetnameinfo_[4], tmpR));
	Excleinfo_[Sheetnameinfo_[4]] = tmpR;

	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpP(PColumName, PColumName + sizeof(PColumName)/sizeof(string));
	Excleinfo_[Sheetnameinfo_[5]] = tmpP;

	//��ʼ��PluginClassAliasList��������
	VEC_STRING_DATAS tmpI(IColumName, IColumName + sizeof(IColumName)/sizeof(string));
	Excleinfo_[Sheetnameinfo_[6]] = tmpI;

	InitDatas();
}

void GlobalData::InitDatas()
{
	//
	VEC_STRING_DATAS tmpvec;
	tmpvec.clear();
	tmpvec.push_back("defaultProcessor");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.DefaultResultSetProcessor");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("defaultConverter");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.DefaultResultSetConverter");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("selectProcessor");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.SelectResultSetProcessor");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("columnPasteProcessor");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.ColumnPasteProcessor");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("RegularFilter");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.RegularFilter");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("subInstConverter");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.SubInstResultSetConverter");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("availableConverter");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.AvailableConverter");
	PCALDatas_.push_back(tmpvec);
	tmpvec.clear();
	tmpvec.push_back("tableConverter");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.TableResultSetConverter");
	PCALDatas_.push_back(tmpvec);

	tmpvec.clear();
	tmpvec.push_back("translateProcessor");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.TranslateResultSetProcessor");
	PCALDatas_.push_back(tmpvec);

	tmpvec.clear();
	tmpvec.push_back("ChangeRateProcessor");
	tmpvec.push_back("com.mainsteam.stm.plugin.common.ChangeRateProcessor");
	PCALDatas_.push_back(tmpvec);

	//
	tmpvec.clear();
	tmpvec.push_back("false");
	tmpvec.push_back("true");
	tmpvec.push_back("day1");
	tmpvec.push_back("min1,min5,min10,hour1,day1");
	tmpvec.push_back("true");
	tmpvec.push_back("1");
	RGMSDatas_.push_back(tmpvec);


}

BOOL CheckFolderExist(const string &strPath)
{
	WIN32_FIND_DATA  wfd;
	BOOL rValue = FALSE;
	HANDLE hFind = FindFirstFile(strPath.c_str(), &wfd);

	if ((hFind != INVALID_HANDLE_VALUE) && (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
	{
		rValue = TRUE;
	}
	FindClose(hFind);
	return rValue;
}

BOOL CheckFileExist(const string &strfilename)
{

	WIN32_FIND_DATA FindFileData;
	BOOL rValue = FALSE;
	HANDLE hFind = FindFirstFile(strfilename.c_str(), &FindFileData);

	if (hFind == INVALID_HANDLE_VALUE)
	{
		rValue = FALSE;
	}
	else
	{
		rValue = TRUE;
	}

	FindClose(hFind);
	return rValue;

}

string LetterTolower(string input)
{
	for(int i=0;i < (int)input.length();i++)
	{
		if((input[i] >= 'A') && (input[i] <= 'Z'))
		{
			input[i]+=32;
		}
	}

	return input;
}

string LetterToupper(string input)
{
	for(int i=0;i < (int)input.length();i++)
	{
		if((input[i] >= 'a') && (input[i] <= 'z'))
		{
			input[i]-=32;
		}
	}

	return input;
}

BOOL IsNumber(char ch)
{
	if((ch >= '0') && (ch <= '9'))
	{
		return TRUE;
	}

	return FALSE;
}

BOOL IsNumberAndLetter(char ch)
{
	if(    ((ch >= '0') && (ch <= '9')) 
		|| ((ch >= 'a') && (ch <= 'z'))
		|| ((ch >= 'A') && (ch <= 'Z')) )
	{
		return TRUE;
	}

	return FALSE;
}

string& replace_all_distinct(string &str, const string &old_value, const string &new_value)
{     
	for(string::size_type pos(0); pos!=string::npos; pos+=new_value.length())
	{
		if( (pos=str.find(old_value,pos))!=string::npos )
		{
			str.replace(pos,old_value.length(),new_value);
		}
		else
		{
			break;
		}
	}
	return str;
} 

std::string str_trim_left(const std::string& str)
{
	std::string t = str;
	std::string::iterator i;
	for (i = t.begin(); i != t.end(); i++)
	{
		if (!isspace(*i))
		{
			t.erase(t.begin(), i);
			break;
		}
	}
	return t;
}

std::string str_trim_right(const std::string& str)
{
	if (str.begin() == str.end())
	{
		return str;
	}

	std::string t = str;
	std::string::iterator i;
	for (i = t.end() - 1; i != t.begin(); i--)
	{
		if (!isspace(*i))
		{
			t.erase(i + 1, t.end());
			break;
		}
	}
	return t;
}

std::string str_trim(const std::string& str)
{
	std::string t = str;

	std::string::iterator i;
	for (i = t.begin(); i != t.end(); i++)
	{
		if (!isspace(*i))
		{
			t.erase(t.begin(), i);
			break;
		}
	}

	if (i == t.end()) {
		return t;
	}

	for (i = t.end() - 1; i != t.begin(); i--)
	{
		if (!isspace(*i))
		{
			t.erase(i + 1, t.end());
			break;
		}
	}

	return t;
}

std::vector<std::string> split_str_bychar(const std::string & str, char c, int non_space)
{
	std::vector < std::string> ret;
	std::vector < std::string>::size_type idx_begin = 0;
	std::vector < std::string>::size_type idx_end = str.find(c);
	UINT32 str_len = str.length();
	while (idx_end != std::string::npos) {
		std::string item = str.substr(idx_begin, idx_end - idx_begin);
		if ((!item.empty()) || (ret.size() != 0))
		{
			if (non_space != 0)
				item = str_trim(item);
			if (!item.empty())
				ret.push_back(item);
		}
		idx_begin = idx_end + 1;
		idx_end = str.find(c, idx_begin);
	}
	if (idx_begin < str_len)
		ret.push_back(str.substr(idx_begin, str_len - idx_begin));
	return ret;
}

void spilt_str_bystr(string selectvalue, string findstr, VEC_STRING_DATAS &vecdata)
{
	//string findstr = "@$@";
	string tempselectvalue = selectvalue;
	int findstrlength = findstr.length();
	int npos = tempselectvalue.find(findstr);
	while(npos >= 0)
	{
		string tempvalue = tempselectvalue.substr(0, npos);
		tempvalue = str_trim(tempvalue);
		if(!tempvalue.empty())
		{
			vecdata.push_back(tempvalue);
		}
		
		tempselectvalue = tempselectvalue.substr(npos + findstrlength, tempselectvalue.length() - npos - findstrlength);
		tempselectvalue = str_trim(tempselectvalue);
		npos = tempselectvalue.find(findstr);
	}

	if(!tempselectvalue.empty())
	{
		vecdata.push_back(tempselectvalue);
	}

	return;
}

void SortVecVec(VEC_VEC_DATAS &datas)
{
	//��ÿһ��vec�ĵ�һ���ֶ�Ϊkey����������
	if(datas.empty())
	{
		return;
	}

	MAP_STRING_VEC_DATAS m_alldatas;
	m_alldatas.clear();
	
	VEC_VEC_DATAS::iterator it = datas.begin();
	for( ; it != datas.end(); ++it)
	{
		VEC_STRING_DATAS tempvec = *it;
		string tempkey = it->at(0);
		m_alldatas.insert(make_pair(tempkey, tempvec));
	}

	datas.clear();
	MAP_STRING_VEC_DATAS::iterator it2 = m_alldatas.begin();
	for( ; it2 != m_alldatas.end(); ++it2)
	{
		VEC_STRING_DATAS tempvec = it2->second;
		datas.push_back(tempvec);
	}

	//MYLOG("end");

}

