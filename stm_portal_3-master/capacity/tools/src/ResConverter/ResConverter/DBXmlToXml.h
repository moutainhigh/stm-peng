/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		DBXmlToXml.h
Description:	��3.5��database���ݿ�������ļ�ת��Ϊ4.0�е������ļ����߱�׼��excel�ļ�
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-10		     Creation of the file
**********************************************************************/


#pragma once

#include "BaseConver.h"

class DBXmlToXml : public BaseConver
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
	DBXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag = 1);

	~DBXmlToXml(void);

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
	void ParseDatabase();

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
	BOOL GetColumnData(string tmpkpiid, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr, string childrestype, BOOL &isSpecial);
	

	/*
	** ���ܣ�	������ָ���Ƿ���Ҫ���⴦��
	** ���룺	ָ����Ϣ���� resourceattr
	** �����	��
	** ����ֵ�� TRUE���ǣ�  FALSE����
	*/
	BOOL SpecialHandling(VEC_STRING_DATAS &resourceattr);
	
	/*
	** ���ܣ�	��command������д���
	** ���룺	4.0ָ���е� collectType ֵ �� tempCollectType
				3.5ָ���� resource->collects->collect->select��ֵ��tmpselect
				3.5ָ���commandֵ �� command
	** �����	
				������commandֵ�� command
				4.0��ArrayType��keyֵ�� tempkey
				command���Ƿ��� $childid ��isCommandChildid  ��TRUE �����У�FALSE��������
				command���Ƿ��� ${dbName} ��isCommandDB  ��TRUE �����У�FALSE��������
	** ����ֵ�� ��
	*/
	void ChangeCommand(string tempCollectType, string tmpselect, string &command, string &tempkey, BOOL &isCommandChildid, BOOL &isCommandDB);
	
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

	/*
	** ���ܣ�	�������Դ(��ȥmysql)�Ĵ���ʽ����  resource->collects->collect->select  ֵ�����⴦��
	** ���룺	
				��Ҫ�����selectֵ strvalue
				3.5ָ������ childrestype				
				3.5ָ���е� resource->valueֵ �� resourcevalue
	** �����	������selectֵ strvalue
				4.0�����ɵ�selectProcessor�е�valueֵ selectvalue
				�����4.0��ʹ�õ�resource->valueֵ �� resourcevalue
				��ȡ����4.0ָ���е� columnֵ ��columnvalue
				��ȡ����4.0ָ���е� IndexColumnTitle ��Ӧ��valueֵ ��indexcolumntitle
				��ȡ����4.0ָ���е� ValueColumnTitle ��Ӧ��valueֵ ��valuecolumntitle
	** ����ֵ�� ��
	*/
	BOOL ParseSelectValue(string &strvalue, string childrestype, string &selectvalue, string &resourcevalue, string &columnvalue, string &indexcolumntitle, string &valuecolumntitle);
	
	/*
	** ���ܣ�	�������Դ(��ȥmysql)�Ĵ���ʽ��
				����3.5���ļ���ĳ��ָ����Ϣ����4.0��collect.xml�е�MetricPlugin��ǩ��Ϣ
	** ���룺	
				��Ҫ������ļ���Ӧ��kpiinfo��Ϣ���� kpimapcmd
				ָ����Ϣ���� resourceattr
				ָ���Ӧ������� collectattr
				ָ���Ӧ����Ϣת������ translationsdata
	** �����	��
	** ����ֵ�� ��
	*/
	void PrepareChildMetricData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata);

public:
	
	BOOL ismysql_;		//�Ƿ���mysql
};


