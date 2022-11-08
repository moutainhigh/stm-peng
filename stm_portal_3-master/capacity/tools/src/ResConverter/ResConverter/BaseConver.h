/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		BaseConver.h
Description:	3.5�����ļ�ת4.0�����ļ��Ļ���������
				��Ҫ��һЩ�����ĺ������ͻ����Ĺ���
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/


#pragma once

#include "ExcelHandle.h"

class BaseConver
{
public:
	BaseConver(void);
	~BaseConver(void);

public:

	/*
	** ���ܣ�	��ʼ������
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void IniData();

	/*
	** ���ܣ�	У������Ĳ�������ȷ��
	** ���룺	 
				�򿪵��ļ��е�·�� xmlpath
				��Ҫ������ļ��� filename 
				��Ҫ������ļ���Ӧ��kpi_info�ļ�  kpifilename
				����OC4�������ļ�collect.xml resource.xml���ļ���·��   outfilepath
	** �����	��
	** ����ֵ�� TRUE���ɹ���  FALSE�����ɹ�
	*/
	BOOL CheckParam(string xmlpath, string filename, string kpifilename, string outfilepath);

	/*
	** ���ܣ�	��ȡ����Դ�������Ϣ��resourceid_��resourcecategory_��resourceicon_��resourcename_��resourcedesc_
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	BOOL GetResourceInfo();

	/*
	** ���ܣ�	����3.5ģ����chipolicy.xml�ļ�����������
	** ���룺	��
	** �����	�������������ݴ洢��map�� mapchipolicydata
	** ����ֵ�� ��
	*/
	void Chipolicy(MAP_VEC_DATAS &mapchipolicydata);

	/*
	** ���ܣ�	����3.5ģ����kpiinfo�ļ�����������
	** ���룺	��
	** �����	�������������ݴ洢��map�� mapdatas
	** ����ֵ�� ��
	*/
	void GetKpiDatas(MAP_VEC_DATAS &mapdatas);

	/*
	** ���ܣ�	��3.5�е�ָ��������ת������Ҫ��ȥ��ָ��ǰ��2λ������ H1***,ȥ��H1
	** ���룺	3.5�е�ָ������ kpiid
	** �����	��
	** ����ֵ�� ������ָ������string
	*/
	string ChangeKpiid(string kpiid);

	/*
	** ���ܣ�	��3.5��ָ���coltypeֵ������4.0�е�pluginid
	** ���룺	3.5�е�ָ���coltypeֵ pluginid
	** �����	��
	** ����ֵ�� ������ָ������string
	*/
	string ChangePluginid(string pluginid);

	/*
	** ���ܣ�	��3.5��ָ���translation���޸�
	** ���룺	ԭֵ sourcevalue
				������ֵ destvalue
				ԭ���� translationsdata
	** �����	�޸ĺ�� translationsdata
	** ����ֵ�� �޸ĺ�� translationsdata
	*/
	string& ChangeTranslations(string &translationsdata, string sourcevalue, string destvalue);	

	/*
	** ���ܣ�	��string�ĵ�һ���ַ�תΪ��д
	** ���룺	��Ҫ�����string 
	** �����	��
	** ����ֵ�� ������ָ������string
	*/
	string FirstLetterToupper(string strvalue);

	/*
	** ���ܣ�	��network�� snmp��host�еģ�������command�õ�method��ֵ
	** ���룺	��Ҫ������command 
	** �����	��
	** ����ֵ�� ������method
	*/
	string GetMethodType(string cmdvalues);


	/*
	** ���ܣ�	��network�� snmp��host�еģ����������Դ��ָ��
				��������ʱ  ��������Դ��indexָ��
	** ���룺	�Ƿ��� host ��snmp �Ľ���
				������3.5�ļ���Ӧ��kpi info�ļ����ݵļ��� ��kpimapcmd
				������3.5�ļ� �� ĳ��ָ�����Ϣ���� resource ������Ϣ���� ��resourceattr
				������3.5�ļ� �� ĳ��ָ�����Ϣ���� resource->collects ��Ϣ���� ��collectattr
	** �����	����Դ��Ӧ������ childindex
	** ����ֵ�� ��
	*/
	void CheckIsIndex(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string &childindex, BOOL ishostsnmp);

	/*
	** ���ܣ�	�ж�commandid���Ƿ����ð�ţ��������ð�ţ���ð��Ϊ�ָ���������Ϊstring��vector
	** ���룺	��Ҫ������command �� tmpcmdid
	** �����	����command����ð��Ϊ�ָ��������ɵ�vector���� veccmdid
	** ����ֵ�� TRUE�����ڣ�  FALSE��������
	*/
	BOOL IsMultiCmdid(const string tmpcmdid, VEC_STRING_DATAS &veccmdid);
	
	/*
	** ���ܣ�	����Ҫ�����string�� ð�� �滻Ϊ�»��ߣ���������:0�����滻
	** ���룺	��Ҫ������valuedata
	** �����	�����ĵ�valuedata
	** ����ֵ�� ��
	*/
	void ChangeColon(string &valuedata);

	/*
	** ���ܣ�	����resourcevalue�е� $d ��ͷ�ı���
	** ���룺	��Ҫ������resourcevalue
	** �����	��ȡ��$d��������֣��������ִ洢��set������ setdatas
	** ����ֵ�� ��
	*/
	void GetParam(const string resourcevalue, SET_INT_DATAS &setdatas);

	/*
	** ���ܣ�	�ж�ָ���Ƿ���Ҫ�������Ƿ��ظ�
	** ���룺	��Ҫ������ָ��metricid��ָ���Ӧ������collecttype��������ָ������ flag
	** �����	��
	** ����ֵ�� TRUE���ظ�����������  FALSE�����ظ�������
	*/
	BOOL IsRepeatMetricid(const string metricid, const string collecttype, int flag);

	/*
	** ���ܣ�	�ڽ���ָ��Ĺ����У��Զ�Ϊresource.xml �� Properties��ǩ �� Instantiation��ǩ ������׼��
	** ���룺	��Ҫ������ָ��kpiid������Դid��subresourceid
	** �����	��
	** ����ֵ�� ��
	*/
	void PrepareResourceSubData(string kpiid, string subresourceid);

	/*
	** ���ܣ�	collect��PluginDataHandlerΪselectProcessor���� value�Ƿ��� $ ����
	** ���룺	��Ҫ������valueֵ resourcevalue
	** �����	������resourcevalue �� ��ȡ�ı��������� replacedatas
	** ����ֵ�� TRUE���У�  FALSE��û��
	*/
	BOOL ParsePluginDataHandlerValue(string &resourcevalue, SET_STRING_DATAS &replacedatas);

	/*
	** ���ܣ�	db��application��middleware ��������ָ�� **InstanceID 
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void InsertMainInstanceID();

	/*
	** ���ܣ�	���resource.xml���Ƿ���Ҫ��ָ�����ӣ������Ҫ����
	** ���룺	��Ҫ������ָ��propertiesdata���Ƿ�������ԴisChild������Դ����childrestype
	** �����	��
	** ����ֵ�� ��
	*/
	void CheckPropertiesData(string &propertiesdata, BOOL isChild, string childrestype);

	/*
	** ���ܣ�	���ָ���Ƿ��� �����Ϣָ��
	** ���룺	��Ҫ������ָ��id metricid
	** �����	��
	** ����ֵ�� TRUE���ǣ�  FALSE������
	*/
	BOOL CheckIsTableMetric(const string metricid);


	/*
	** ���ܣ�	����3.5ָ���е� collect->merge ��ǩ����
				�ж�3.5ָ���� resource->value �Ƿ���ֵ
				���Ϊ�գ�ֻ������һ�е� merge ������ ֵ ���� value
				�����ֵ���� $di ȫ�� �滻  merge($di)
	** ���룺	�ж�3.5ָ���� resource->value��resourcevalue�� ����3.5ָ���е� collects��collectattr
	** �����	��
	** ����ֵ�� ��
	*/
	void ParseMerge(string &resourcevalue, VEC_VEC_DATAS &collectattr);

	/*
	** ���ܣ�	��xml�е������ַ����з���
				"&gt;" תΪ ">"   
				"&lt;" תΪ "<"
	** ���룺	��Ҫ�����string data
	** �����	������string data
	** ����ֵ�� ���ش�����string data
	*/
	string& ModifyStr(string &datas);

	/*
	** ���ܣ�	����3.5�е�ָ������ תΪ 4.0�е�ָ������
	** ���룺	3.5�е�ָ������ kpitype
	** �����	4.0��ָ�����Ͷ�Ӧ�����кţ������ڲ�ʹ�ã� nkpitype
	** ����ֵ�� ����4.0�е�ָ������
	*/
	string GetKpiType(string kpitype, int &nkpitype);

	/*
	** ���ܣ�	����resource.xml�еĲ�������
				��Ҫ�� �� "��Դid", "category", "icon", "��Դ����name", "��Դ����description", "��Դ����type", "����Դparentid"
	** ���룺	ָ��kpiid��3.5�е�ָ�����ࣨ����Դ��������Դ�ģ�������Դ���� type
	** �����	resource�ļ�¼����
	** ����ֵ�� ��
	*/
	void SetResourceInfo(string kpiid, string reslevel, string type, VEC_STRING_DATAS &resourceinfo);

	/*
	** ���ܣ�	����resource.xml�еĲ�������
				��Ҫ�� �����3�鷧ֵ��Ϣ ����Ĭ��һЩ��ֵ���޸�һ��ָ������
	** ���룺	4.0��ָ�����Ͷ�Ӧ�����кţ������ڲ�ʹ�ã� nkpitype
				3.5�еķ�ֵ�����Ϣ���� kpipolicyattr
	** �����	resource�ļ�¼����
	** ����ֵ�� ��
	*/
	void ParseThreshold(int nkpitype, VEC_STRING_DATAS &resourcedata, VEC_STRING_DATAS &kpipolicyattr);

	/*
	** ���ܣ�	����3.5�е�ָ�귧ֵ��Ϣ���õ�4.0ָ��ķ�ֵ��Ϣ
	** ���룺	3.5�еķ�ֵ�����Ϣ���� kpipolicyattr
	** �����	��ֵ����Ϣ����thresholdValues
	** ����ֵ�� ��
	*/
	void ParseThresholdData(MAP_INT_STRING_DATAS &thresholdValues, VEC_STRING_DATAS &kpipolicyattr);

	/*
	** ���ܣ�	����resource.xml�е���������
	** ���룺	3.5�е���Դ��Ϣ���� deviceattr
				3.5�е�ָ����Ϣ���� deviceattr
				3.5�еķ�ֵ�����Ϣ���� kpipolicyattr
	** �����	��
	** ����ֵ�� ��
	*/
	void PrepareResourceAllData(VEC_STRING_DATAS &deviceattr, VEC_STRING_DATAS &resourceattr, VEC_STRING_DATAS &kpipolicyattr);
	

	/*
	** ���ܣ�	���InstantiationName ����û����Ҫ���������
				�������Ҫ�����������ӵģ������ӣ����û�У������κδ���
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void CheckProperties();

	/*
	** ���ܣ�	��map����תΪvec���ݡ���vec���ݽ�������
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void PreperDatasForWrite();
	
	/*
	** ���ܣ�	��������������vec���ݣ�д�뵽��׼��excel��
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteExcel();


	/*
	** ���ܣ�	����excel�������� collect.xml
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteCollectXml();	

	/*
	** ���ܣ�	����excel�������� collect.xml �� GlobalMetricSetting ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* MetricPlugins
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteCollectGlobalMetricSetting(xml_node<>* MetricPlugins);

	/*
	** ���ܣ�	����excel�������� collect.xml �� PluginClassAliasList ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* MetricPlugins
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteCollectPluginClassAliasList(xml_node<>* MetricPlugins);

	/*
	** ���ܣ�	����excel�������� collect.xml �� MetricPlugin ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* MetricPlugins
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteCollectMetricPlugin(xml_node<>* MetricPlugins);

	/*
	** ���ܣ�	����excel�������� resource.xml
	** ���룺	��
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteResourceXml();

	/*
	** ���ܣ�	����excel�������� resource.xml �� GlobalMetricSetting ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* Capacity
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteResourceGlobalMetricSetting(xml_node<>* Capacity);

	/*
	** ���ܣ�	����excel�������� resource.xml �� Resource ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* Capacity
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteResource(xml_node<>* Capacity);

	/*
	** ���ܣ�	��������vec��PropertiesDatas_ ת��Ϊ ���Ե�map����mappropertiesDatas
	**			Ŀ�ģ��Է���resource.xml��Resource��ǩ��Properties��ǩ����
	** ���룺	��
	** �����	map����mappropertiesDatas
	** ����ֵ�� ��
	*/
	void ParsePropertiesDatas(MAP_VEC_VEC_DATAS &mappropertiesDatas);

	/*
	** ���ܣ�	��������vec��InstantiationDatas_ ת��Ϊ ���Ե�map����mapInstantiationDatas
	**			Ŀ�ģ��Է���resource.xml��Resource��ǩ��Instantiation��ǩ����
	** ���룺	��
	** �����	map����mapInstantiationDatas
	** ����ֵ�� ��
	*/
	void ParseInstantiationDatas(MAP_VEC_VEC_DATAS &mapInstantiationDatas);

	/*
	** ���ܣ�	���Ե�map����mappropertiesDatas ���� resource.xml �� Properties ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* Resource �� ��Դid resouceid
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteResourceProperties(xml_node<>* Resource, const string resouceid);

	/*
	** ���ܣ�	���Ե�map����mappropertiesDatas ���� resource.xml �� Instantiation ��ǩ 
	** ���룺	xml�ļ��� xml_node<>* Resource �� ��Դid resouceid
	** �����	��
	** ����ֵ�� ��
	*/
	void WriteResourceInstantiation(xml_node<>* Resource, const string resouceid);

	
public:

	xml_document<>		xml_doc_;		//xml�ļ�
	stringstream		loglog;			//д��־ר��

	string filename_;			//��Ҫ������ļ���
	string kpifilename_;		//��Ҫ������ļ���Ӧ��kpi_info�ļ�
	string strXmlPath_;			//�򿪵��ļ��е�·��
	string outfilepath_;		//����OC4�������ļ�collect.xml resource.xml���ļ������ƣ��Լ����ɵı�׼excel���ļ���

	string resourceid_;			//����Դid
	string resourcecategory_;	//����Դcategory
	string resourceicon_;		//����Դicon
	string resourcename_;		//����Դname
	string resourcedesc_;		//����Դdescription
	
	VEC_VEC_DATAS CGlobalMetricSettingDatas_;	//��׼excle�е�collectGlobalMetricSettingҳ,collect.xml �е� GlobalMetricSetting ��ǩ������
	VEC_VEC_DATAS PluginClassAliasListDatas_;	//��׼excle�е�PluginClassAliasListҳ,collect.xml �е� PluginClassAliasList ��ǩ������
	VEC_VEC_DATAS MetricPluginDatas_;			//��׼excle�е�MetricPluginҳ,collect.xml �е� MetricPlugin ��ǩ������
	VEC_VEC_DATAS RGlobalMetricSettingDatas_;	//��׼excle�е�resourceGlobalMetricSettingҳ,resource.xml �е� GlobalMetricSetting ��ǩ������
	VEC_VEC_DATAS ResourceDatas_;				//��׼excle�е�Resourceҳ,resource.xml �е� Resource ��ǩ������
	VEC_VEC_DATAS PropertiesDatas_;				//��׼excle�е�Propertiesҳ,resource.xml �е� Properties ��ǩ������
	VEC_VEC_DATAS InstantiationDatas_;			//��׼excle�е�Instantiationҳ,resource.xml �е� Instantiation ��ǩ������

	//�����Ժ�ʵ����map��ʽ�洢
	MAP_VEC_VEC_DATAS	mapPropertiesDatas_;	//resource.xml �е� Properties ��ǩ������
	MAP_VEC_VEC_DATAS	mapInstantiationDatas_;	//resource.xml �е� Instantiation ��ǩ������

	MAP_STRING_DATAS mapinstantiationId_;		//resource.xml �е� Instantiation ��ǩ�� InstanceId ����
	MAP_STRING_DATAS mapinstantiationName_;		//resource.xml �е� Instantiation ��ǩ�� InstanceName ����

	MAP_VEC_DATAS mapkpidatas_;					//�����kpi�ļ��е��������ݼ���
	MAP_VEC_DATAS mapchipolicydatas_;			//3.5ģ����chipolicy.xml�ļ����������ݼ���
	MAP_STRING_DATAS mapKPIchildDatas_;			//��¼kpi�е�ischildresidΪYʱ����Ӧ��ָ��

	MAP_STRING_DATAS mapResTypeDatas_;	//resource.xml �� ��typeֵ
	
	SET_STRING_DATAS MetricidCollectDatas_;		//collect.xml�е�metric�ļ�¼��Ϊ���ų��ظ���¼
	SET_STRING_DATAS MetricidResourceDatas_;	//resource.xml�е�metric�ļ�¼��Ϊ���ų��ظ���¼
	SET_STRING_DATAS PropertiesData_;			//resource.xml �е�Properties��ǩ�����ݼ��ϣ�Ϊ���ų��ظ���¼
	SET_STRING_DATAS setChildIndexDatas_;		//��network �� host snmp �У����Զ����� childIndex ָ�꣬��¼�ظ���
	
	BOOL isWindowsWmi_;					//�Ƿ���host��Դ�� windows_wmi.xml �ļ�����
	BOOL ismodifyhostname_;						//�ж��������е��ļ�
	VEC_STRING_DATAS inputdata_;		//��¼����Ĳ�����Ϊ��ʧ�ܶ��ظ�����

	string collectfilename_;			//collect.xml ���ļ�����

	SET_STRING_DATAS setInstanceID_;	//db��application��middleware ��������ָ�� **InstanceID �� pluginid �ļ���
	SET_STRING_DATAS setTablemetric_;	//�����Ϣָ��id����
};
