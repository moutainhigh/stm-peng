/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		HostXmlToXml.h
Description:	��3.5��host��������snmp���������ļ�ת��Ϊ4.0�е������ļ����߱�׼��excel�ļ�
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-10		     Creation of the file
**********************************************************************/


#pragma once

#include "BaseConver.h"

class HostXmlToXml : public BaseConver
{
public:

	/*
	** ���ܣ�	���캯��
	** ���룺	
				�����3.5��resourceĿ¼������·������xmlpath
				�����3.5��Ҫ������ļ����·���������xmlpath��·������filename
				�����3.5��Ҫ�����ļ���Ӧ��kpiinfo�����·���������xmlpath��·������kpifilename
				�����collect.xml resource.xml���ļ������ơ���׼excel���ļ���
				�����ת������flag��flag�������£�
						XML_TO_XML_EXCEL 0  ��3.5��xml�ļ�ת��Ϊ4.0��xml�ļ��Լ�4.0��ָ����Ϣ��׼excel�ļ�
						XML_TO_XML		 1	��3.5��xml�ļ�ת��Ϊ4.0��xml�ļ�
						XML_TO_EXCEL	 2	��3.5��xml�ļ�ת��Ϊ4.0��ָ����Ϣ��׼excel�ļ�

	** �����	��
	** ����ֵ�� ��
	*/
	HostXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag = 1);
	~HostXmlToXml(void);

private:

	/*
	** ���ܣ�	��ʼ������
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void InitDatas();


	/*
	** ���ܣ�	�ļ�����������
	** ���룺	����ʽflag��
					XML_TO_XML_EXCEL 0  ��3.5��xml�ļ�ת��Ϊ4.0��xml�ļ��Լ�4.0��ָ����Ϣ��׼excel�ļ�
					XML_TO_XML		 1	��3.5��xml�ļ�ת��Ϊ4.0��xml�ļ�
					XML_TO_EXCEL	 2	��3.5��xml�ļ�ת��Ϊ4.0��ָ����Ϣ��׼excel�ļ�
	** �����	��
	** ����ֵ�� ��
	*/
	void AnalysisMain(int flag);

	/*
	** ���ܣ�	������Ҫ�������ļ�
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void ParseHost();

	/*
	** ���ܣ�	����3.5���ļ���������Ϣ�洢���ڴ��У�Ϊ����4.0����Ϣ��׼��
	** ���룺	��Ҫ������3.5�ļ���pathfilename
	** �����	��
	** ����ֵ�� ��
	*/
	void ParseFile(string pathfilename);


	/*
	** ���ܣ�	�Դ�3.5����ȡ��ָ����Ϣ����һ��������У��
	** ���룺	ָ����Ϣ���� resourceattr
	** �����	��
	** ����ֵ�� TRUE���Ϸ���  FALSE�����Ϸ�
	*/
	BOOL CheckResourceattr(VEC_STRING_DATAS &resourceattr);

	/*
	** ���ܣ�	��3.5���ļ���ĳ��ָ����Ϣ���з�����ȡ��Ϊ����4.0����Ϣ��׼��
	** ���룺	
				�豸��Ϣ���� deviceattr
				��Ҫ������ļ���Ӧ��kpiinfo��Ϣ���� kpimapcom
				ָ����Ϣ���� resourceattr
				ָ���Ӧ������� collectattr
				ָ���Ӧ�ĸ澯������Ϣ���� kpipolicyattr
				ָ���Ӧ����Ϣת������ translationsdata
	** �����	��
	** ����ֵ�� ��
	*/
	void ParseMetricData(VEC_STRING_DATAS &deviceattr, MAP_VEC_DATAS &kpimapcom, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, VEC_STRING_DATAS &kpipolicyattr, string translationsdata);

	/*
	** ���ܣ�	����3.5�е�ָ����Ϣ������4.0ָ���columnֵ���ж��Ƿ���Ҫ����4.0��selectѡ����
	** ���룺	
				ָ��id ��tmpkpiid
				ָ���е� collecttype
				3.5ָ���е� resource->valueֵ �� resourcevalue
				��Ҫ������ļ���Ӧ��kpiinfo��Ϣ���� kpimapcom
				ָ���Ӧ������� collectattr
				3.5ָ������ childrestype

	** �����	��ȡ����4.0ָ���е� columnֵ ��columnvalue
				�����ĸ�����ʵ�ʵ�collect�����Ƿ�ࣺ isSpecial ��true �࣬false������
	** ����ֵ�� TRUE�����ɣ�  FALSE��������
	*/
	BOOL GetColumnData(string tmpkpiid, string collecttype, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr, string childrestype, BOOL &isSpecial);

	/*
	** ���ܣ�	������ָ���Ƿ���Ҫ���⴦��
	** ���룺	ָ����Ϣ���� resourceattr
	** �����	��
	** ����ֵ�� TRUE���ǣ�  FALSE����
	*/
	BOOL SpecialHandling(VEC_STRING_DATAS &resourceattr);
	
	/*
	** ���ܣ�	��command������д����ж��������Ƿ��� $childid	
	** ���룺	4.0ָ���е� collectType ֵ �� tempCollectType
				3.5ָ���е�����Դ����childrestype��childrestype
				3.5ָ���commandֵ �� command
	** �����	
				������commandֵ�� command
				��ȡ����childidֵ�� childidvalue
				������4.0�е�columnֵ�� columnvalue
	** ����ֵ�� TRUE������$childid��  FALSE��������$childid
	*/
	BOOL ChangeCommand(const string tempCollectType, const string childrestype, string &command, string &childidvalue, string &columnvalue);
	
	/*
	** ���ܣ�	3.5��ָ���е� resource->collects->collect->select ֵ�������滻����
				�� $key �����⴦���Լ�$key ������һ�� ���� ���Ⱥ�˳������
	** ���룺	
				��Ҫ����� select ֵ��strvalue
				����Դ�����ͣ�childrestype
	** �����	�ı���selectֵ��strvalue
	** ����ֵ�� TRUE��$key��ǰ�棻  FALSE��$key�ں���
	*/
	BOOL ReplaceSpecial(string &strvalue, string childrestype);

	/*
	** ���ܣ�	����WMI�������ַ�����column�е� size �滻Ϊ size1
	** ���룺	��Ҫ����� column ֵ��columnvalue
				�����columnvalue������ flag��1��columnvalue�����˶��ţ�2��columnvalue���û�д�����
	** �����	��
	** ����ֵ�� �ı���columnֵ��columnvalue
	*/
	string WMIChangeSize(string columnvalue, int flag);


	/*
	** ���ܣ�	����3.5���ļ��� WMI ������Դ ָ����Ϣ����4.0��collect.xml�е�MetricPlugin��ǩ��Ϣ
	** ���룺	
				��Ҫ������ļ���Ӧ��kpiinfo��Ϣ���� kpimapcom
				ָ����Ϣ���� resourceattr
				ָ���Ӧ������� collectattr
				ָ���Ӧ����Ϣת������ translationsdata
	** �����	��
	** ����ֵ�� ��
	*/
	void WMIMetricPluginData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata);

	/*
	** ���ܣ�	�ַ������滻
	** ���룺	��Ҫ�滻���ַ��� strvalue
				�滻�����ַ���1��newvalue1
				�滻�����ַ���2��newvalue2
	** �����	��
	** ����ֵ�� TRUE���ǣ�  FALSE����
	*/
	string ParseValue(const string strvalue, const string newvalue1, const string newvalue2);


	/*
	** ���ܣ�	����3.5ָ���е� collect->merge ��ǩ����
				�ж�3.5ָ���� resource->value �Ƿ���ֵ
				���Ϊ�գ�ֻ������һ�е� merge ������ ֵ ���� value
				�����ֵ���� $di ȫ�� �滻  merge($di)
	** ���룺	�ж�3.5ָ���� resource->value��resourcevalue�� ����3.5ָ���е� collects��collectattr
	** �����	��
	** ����ֵ�� ��
	*/
	void ParseHostMerge(string &resourcevalue, VEC_VEC_DATAS &collectattr);

	/*
	** ���ܣ�	����3.5���ļ���ĳ��ָ����Ϣ����4.0��collect.xml�е�MetricPlugin��ǩ��Ϣ
	** ���룺	
				��Ҫ������ļ���Ӧ��kpiinfo��Ϣ���� kpimapcmd
				ָ����Ϣ���� resourceattr
				ָ���Ӧ������� collectattr
				ָ���Ӧ����Ϣת������ translationsdata
	** �����	��
	** ����ֵ�� ��
	*/
	void PrepareMetricPluginAllData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata);

public:

	BOOL issolaris_;
};
