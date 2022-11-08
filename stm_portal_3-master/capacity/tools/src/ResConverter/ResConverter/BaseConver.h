/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		BaseConver.h
Description:	3.5配置文件转4.0配置文件的基础公共类
				主要是一些公共的函数，和基础的功能
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
	** 功能：	初始化数据
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void IniData();

	/*
	** 功能：	校验输入的参数的正确性
	** 输入：	 
				打开的文件夹的路径 xmlpath
				需要处理的文件名 filename 
				需要处理的文件对应的kpi_info文件  kpifilename
				生成OC4的配置文件collect.xml resource.xml的文件夹路径   outfilepath
	** 输出：	无
	** 返回值： TRUE：成功；  FALSE：不成功
	*/
	BOOL CheckParam(string xmlpath, string filename, string kpifilename, string outfilepath);

	/*
	** 功能：	获取主资源的相关信息：resourceid_、resourcecategory_、resourceicon_、resourcename_、resourcedesc_
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	BOOL GetResourceInfo();

	/*
	** 功能：	解析3.5模型中chipolicy.xml文件的所有数据
	** 输入：	无
	** 输出：	将解析到的数据存储到map中 mapchipolicydata
	** 返回值： 无
	*/
	void Chipolicy(MAP_VEC_DATAS &mapchipolicydata);

	/*
	** 功能：	解析3.5模型中kpiinfo文件的所有数据
	** 输入：	无
	** 输出：	将解析到的数据存储到map中 mapdatas
	** 返回值： 无
	*/
	void GetKpiDatas(MAP_VEC_DATAS &mapdatas);

	/*
	** 功能：	对3.5中的指标名进行转换，主要是去掉指标前面2位，比如 H1***,去掉H1
	** 输入：	3.5中的指标名称 kpiid
	** 输出：	无
	** 返回值： 处理后的指标名称string
	*/
	string ChangeKpiid(string kpiid);

	/*
	** 功能：	对3.5中指标的coltype值，生成4.0中的pluginid
	** 输入：	3.5中的指标的coltype值 pluginid
	** 输出：	无
	** 返回值： 处理后的指标名称string
	*/
	string ChangePluginid(string pluginid);

	/*
	** 功能：	对3.5中指标的translation的修改
	** 输入：	原值 sourcevalue
				翻译后的值 destvalue
				原来的 translationsdata
	** 输出：	修改后的 translationsdata
	** 返回值： 修改后的 translationsdata
	*/
	string& ChangeTranslations(string &translationsdata, string sourcevalue, string destvalue);	

	/*
	** 功能：	将string的第一个字符转为大写
	** 输入：	需要处理的string 
	** 输出：	无
	** 返回值： 处理后的指标名称string
	*/
	string FirstLetterToupper(string strvalue);

	/*
	** 功能：	在network和 snmp（host中的），根据command得到method的值
	** 输入：	需要分析的command 
	** 输出：	无
	** 返回值： 处理后的method
	*/
	string GetMethodType(string cmdvalues);


	/*
	** 功能：	在network和 snmp（host中的），检查子资源的指标
				符合条件时  新增子资源的index指标
	** 输入：	是否是 host 的snmp 的解析
				解析的3.5文件对应的kpi info文件内容的集合 ：kpimapcmd
				解析的3.5文件 中 某个指标的信息集合 resource 属性信息集合 ：resourceattr
				解析的3.5文件 中 某个指标的信息集合 resource->collects 信息集合 ：collectattr
	** 输出：	子资源对应的索引 childindex
	** 返回值： 无
	*/
	void CheckIsIndex(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string &childindex, BOOL ishostsnmp);

	/*
	** 功能：	判断commandid中是否存在冒号，如果存在冒号，以冒号为分隔符，解析为string的vector
	** 输入：	需要分析的command ： tmpcmdid
	** 输出：	根据command，以冒号为分隔符，生成的vector集合 veccmdid
	** 返回值： TRUE：存在；  FALSE：不存在
	*/
	BOOL IsMultiCmdid(const string tmpcmdid, VEC_STRING_DATAS &veccmdid);
	
	/*
	** 功能：	将需要处理的string中 冒号 替换为下划线，但是最后的:0不能替换
	** 输入：	需要分析的valuedata
	** 输出：	处理后的的valuedata
	** 返回值： 无
	*/
	void ChangeColon(string &valuedata);

	/*
	** 功能：	解析resourcevalue中的 $d 开头的变量
	** 输入：	需要分析的resourcevalue
	** 输出：	提取出$d后面的数字，并将数字存储在set集合中 setdatas
	** 返回值： 无
	*/
	void GetParam(const string resourcevalue, SET_INT_DATAS &setdatas);

	/*
	** 功能：	判断指标是否需要解析，是否重复
	** 输入：	需要分析的指标metricid、指标对应的类型collecttype、解析的指标种类 flag
	** 输出：	无
	** 返回值： TRUE：重复，不解析；  FALSE：不重复，解析
	*/
	BOOL IsRepeatMetricid(const string metricid, const string collecttype, int flag);

	/*
	** 功能：	在解析指标的过程中，自动为resource.xml 的 Properties标签 和 Instantiation标签 数据做准备
	** 输入：	需要分析的指标kpiid、子资源id：subresourceid
	** 输出：	无
	** 返回值： 无
	*/
	void PrepareResourceSubData(string kpiid, string subresourceid);

	/*
	** 功能：	collect中PluginDataHandler为selectProcessor，中 value是否含有 $ 变量
	** 输入：	需要分析的value值 resourcevalue
	** 输出：	处理后的resourcevalue 、 获取的变量名集合 replacedatas
	** 返回值： TRUE：有；  FALSE：没有
	*/
	BOOL ParsePluginDataHandlerValue(string &resourcevalue, SET_STRING_DATAS &replacedatas);

	/*
	** 功能：	db、application、middleware ，新增的指标 **InstanceID 
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void InsertMainInstanceID();

	/*
	** 功能：	检查resource.xml中是否需要将指标增加，如果需要增加
	** 输入：	需要分析的指标propertiesdata、是否是子资源isChild、子资源类型childrestype
	** 输出：	无
	** 返回值： 无
	*/
	void CheckPropertiesData(string &propertiesdata, BOOL isChild, string childrestype);

	/*
	** 功能：	检查指标是否是 表的信息指标
	** 输入：	需要分析的指标id metricid
	** 输出：	无
	** 返回值： TRUE：是；  FALSE：不是
	*/
	BOOL CheckIsTableMetric(const string metricid);


	/*
	** 功能：	解析3.5指标中的 collect->merge 标签内容
				判断3.5指标中 resource->value 是否有值
				如果为空，只解析第一行的 merge ，并将 值 赋给 value
				如果有值，将 $di 全部 替换  merge($di)
	** 输入：	判断3.5指标中 resource->value：resourcevalue， 解析3.5指标中的 collects：collectattr
	** 输出：	无
	** 返回值： 无
	*/
	void ParseMerge(string &resourcevalue, VEC_VEC_DATAS &collectattr);

	/*
	** 功能：	将xml中的特殊字符进行翻译
				"&gt;" 转为 ">"   
				"&lt;" 转为 "<"
	** 输入：	需要处理的string data
	** 输出：	处理后的string data
	** 返回值： 返回处理后的string data
	*/
	string& ModifyStr(string &datas);

	/*
	** 功能：	根据3.5中的指标类型 转为 4.0中的指标类型
	** 输入：	3.5中的指标类型 kpitype
	** 输出：	4.0的指标类型对应的序列号（程序内部使用） nkpitype
	** 返回值： 返回4.0中的指标类型
	*/
	string GetKpiType(string kpitype, int &nkpitype);

	/*
	** 功能：	生成resource.xml中的部分内容
				主要有 ： "资源id", "category", "icon", "资源名称name", "资源描述description", "资源类型type", "父资源parentid"
	** 输入：	指标kpiid、3.5中的指标种类（主资源还是子资源的）、子资源类型 type
	** 输出：	resource的记录集合
	** 返回值： 无
	*/
	void SetResourceInfo(string kpiid, string reslevel, string type, VEC_STRING_DATAS &resourceinfo);

	/*
	** 功能：	生成resource.xml中的部分内容
				主要是 后面的3组阀值信息 ，并默认一些阀值或修改一下指标种类
	** 输入：	4.0的指标类型对应的序列号（程序内部使用） nkpitype
				3.5中的阀值相关信息集合 kpipolicyattr
	** 输出：	resource的记录集合
	** 返回值： 无
	*/
	void ParseThreshold(int nkpitype, VEC_STRING_DATAS &resourcedata, VEC_STRING_DATAS &kpipolicyattr);

	/*
	** 功能：	根据3.5中的指标阀值信息，得到4.0指标的阀值信息
	** 输入：	3.5中的阀值相关信息集合 kpipolicyattr
	** 输出：	阀值的信息集合thresholdValues
	** 返回值： 无
	*/
	void ParseThresholdData(MAP_INT_STRING_DATAS &thresholdValues, VEC_STRING_DATAS &kpipolicyattr);

	/*
	** 功能：	生成resource.xml中的所有内容
	** 输入：	3.5中的资源信息集合 deviceattr
				3.5中的指标信息集合 deviceattr
				3.5中的阀值相关信息集合 kpipolicyattr
	** 输出：	无
	** 返回值： 无
	*/
	void PrepareResourceAllData(VEC_STRING_DATAS &deviceattr, VEC_STRING_DATAS &resourceattr, VEC_STRING_DATAS &kpipolicyattr);
	

	/*
	** 功能：	检查InstantiationName 中有没有需要处理的属性
				如果有需要往属性中增加的，则增加，如果没有，不做任何处理
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void CheckProperties();

	/*
	** 功能：	将map数据转为vec数据、将vec数据进行排序
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void PreperDatasForWrite();
	
	/*
	** 功能：	将解析到的所有vec数据，写入到标准的excel中
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void WriteExcel();


	/*
	** 功能：	根据excel内容生成 collect.xml
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void WriteCollectXml();	

	/*
	** 功能：	根据excel内容生成 collect.xml 的 GlobalMetricSetting 标签 
	** 输入：	xml文件的 xml_node<>* MetricPlugins
	** 输出：	无
	** 返回值： 无
	*/
	void WriteCollectGlobalMetricSetting(xml_node<>* MetricPlugins);

	/*
	** 功能：	根据excel内容生成 collect.xml 的 PluginClassAliasList 标签 
	** 输入：	xml文件的 xml_node<>* MetricPlugins
	** 输出：	无
	** 返回值： 无
	*/
	void WriteCollectPluginClassAliasList(xml_node<>* MetricPlugins);

	/*
	** 功能：	根据excel内容生成 collect.xml 的 MetricPlugin 标签 
	** 输入：	xml文件的 xml_node<>* MetricPlugins
	** 输出：	无
	** 返回值： 无
	*/
	void WriteCollectMetricPlugin(xml_node<>* MetricPlugins);

	/*
	** 功能：	根据excel内容生成 resource.xml
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void WriteResourceXml();

	/*
	** 功能：	根据excel内容生成 resource.xml 的 GlobalMetricSetting 标签 
	** 输入：	xml文件的 xml_node<>* Capacity
	** 输出：	无
	** 返回值： 无
	*/
	void WriteResourceGlobalMetricSetting(xml_node<>* Capacity);

	/*
	** 功能：	根据excel内容生成 resource.xml 的 Resource 标签 
	** 输入：	xml文件的 xml_node<>* Capacity
	** 输出：	无
	** 返回值： 无
	*/
	void WriteResource(xml_node<>* Capacity);

	/*
	** 功能：	根据属性vec集PropertiesDatas_ 转换为 属性的map集合mappropertiesDatas
	**			目的：以方便resource.xml的Resource标签中Properties标签生成
	** 输入：	无
	** 输出：	map集合mappropertiesDatas
	** 返回值： 无
	*/
	void ParsePropertiesDatas(MAP_VEC_VEC_DATAS &mappropertiesDatas);

	/*
	** 功能：	根据属性vec集InstantiationDatas_ 转换为 属性的map集合mapInstantiationDatas
	**			目的：以方便resource.xml的Resource标签中Instantiation标签生成
	** 输入：	无
	** 输出：	map集合mapInstantiationDatas
	** 返回值： 无
	*/
	void ParseInstantiationDatas(MAP_VEC_VEC_DATAS &mapInstantiationDatas);

	/*
	** 功能：	属性的map集合mappropertiesDatas 生成 resource.xml 的 Properties 标签 
	** 输入：	xml文件的 xml_node<>* Resource ， 资源id resouceid
	** 输出：	无
	** 返回值： 无
	*/
	void WriteResourceProperties(xml_node<>* Resource, const string resouceid);

	/*
	** 功能：	属性的map集合mappropertiesDatas 生成 resource.xml 的 Instantiation 标签 
	** 输入：	xml文件的 xml_node<>* Resource ， 资源id resouceid
	** 输出：	无
	** 返回值： 无
	*/
	void WriteResourceInstantiation(xml_node<>* Resource, const string resouceid);

	
public:

	xml_document<>		xml_doc_;		//xml文件
	stringstream		loglog;			//写日志专用

	string filename_;			//需要处理的文件名
	string kpifilename_;		//需要处理的文件对应的kpi_info文件
	string strXmlPath_;			//打开的文件夹的路径
	string outfilepath_;		//生成OC4的配置文件collect.xml resource.xml的文件夹名称，以及生成的标准excel的文件名

	string resourceid_;			//主资源id
	string resourcecategory_;	//主资源category
	string resourceicon_;		//主资源icon
	string resourcename_;		//主资源name
	string resourcedesc_;		//主资源description
	
	VEC_VEC_DATAS CGlobalMetricSettingDatas_;	//标准excle中的collectGlobalMetricSetting页,collect.xml 中的 GlobalMetricSetting 标签的内容
	VEC_VEC_DATAS PluginClassAliasListDatas_;	//标准excle中的PluginClassAliasList页,collect.xml 中的 PluginClassAliasList 标签的内容
	VEC_VEC_DATAS MetricPluginDatas_;			//标准excle中的MetricPlugin页,collect.xml 中的 MetricPlugin 标签的内容
	VEC_VEC_DATAS RGlobalMetricSettingDatas_;	//标准excle中的resourceGlobalMetricSetting页,resource.xml 中的 GlobalMetricSetting 标签的内容
	VEC_VEC_DATAS ResourceDatas_;				//标准excle中的Resource页,resource.xml 中的 Resource 标签的内容
	VEC_VEC_DATAS PropertiesDatas_;				//标准excle中的Properties页,resource.xml 中的 Properties 标签的内容
	VEC_VEC_DATAS InstantiationDatas_;			//标准excle中的Instantiation页,resource.xml 中的 Instantiation 标签的内容

	//将属性和实例以map形式存储
	MAP_VEC_VEC_DATAS	mapPropertiesDatas_;	//resource.xml 中的 Properties 标签的内容
	MAP_VEC_VEC_DATAS	mapInstantiationDatas_;	//resource.xml 中的 Instantiation 标签的内容

	MAP_STRING_DATAS mapinstantiationId_;		//resource.xml 中的 Instantiation 标签的 InstanceId 内容
	MAP_STRING_DATAS mapinstantiationName_;		//resource.xml 中的 Instantiation 标签的 InstanceName 内容

	MAP_VEC_DATAS mapkpidatas_;					//处理的kpi文件中的所有数据集合
	MAP_VEC_DATAS mapchipolicydatas_;			//3.5模型中chipolicy.xml文件的所有数据集合
	MAP_STRING_DATAS mapKPIchildDatas_;			//记录kpi中的ischildresid为Y时，对应的指标

	MAP_STRING_DATAS mapResTypeDatas_;	//resource.xml 中 的type值
	
	SET_STRING_DATAS MetricidCollectDatas_;		//collect.xml中的metric的记录，为了排除重复记录
	SET_STRING_DATAS MetricidResourceDatas_;	//resource.xml中的metric的记录，为了排除重复记录
	SET_STRING_DATAS PropertiesData_;			//resource.xml 中的Properties标签的内容集合，为了排除重复记录
	SET_STRING_DATAS setChildIndexDatas_;		//在network 和 host snmp 中，会自动生成 childIndex 指标，记录重复性
	
	BOOL isWindowsWmi_;					//是否是host资源的 windows_wmi.xml 文件处理
	BOOL ismodifyhostname_;						//判断是那类行的文件
	VEC_STRING_DATAS inputdata_;		//记录输入的参数，为了失败而重复调用

	string collectfilename_;			//collect.xml 的文件名称

	SET_STRING_DATAS setInstanceID_;	//db、application、middleware ，新增的指标 **InstanceID 中 pluginid 的集合
	SET_STRING_DATAS setTablemetric_;	//表的信息指标id集合
};
