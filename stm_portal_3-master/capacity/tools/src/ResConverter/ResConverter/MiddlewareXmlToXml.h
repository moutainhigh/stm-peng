/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		MiddlewareXmlToXml.h
Description:	将3.5中middleware中间件的配置文件转换为4.0中的配置文件或者标准的excel文件
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-10		     Creation of the file
**********************************************************************/


#pragma once

#include "BaseConver.h"

class MiddlewareXmlToXml : public BaseConver
{
public:

	/*
	** 功能：	构造函数
	** 输入：	
				传入的3.5的resource目录（绝对路径）：xmlpath
				传入的3.5需要处理的文件相对路径（相对于xmlpath的路径）：filename
				传入的3.5需要处理文件对应的kpiinfo的相对路径（相对于xmlpath的路径）：kpifilename
				传入的collect.xml resource.xml的文件夹名称、标准excel的文件名
				传入的转换类型flag：flag含义如下：
								XML_TO_XML_EXCEL 0  将3.5的xml文件转换为4.0的xml文件以及4.0的指标信息标准excel文件
								XML_TO_XML		 1	将3.5的xml文件转换为4.0的xml文件
								XML_TO_EXCEL	 2	将3.5的xml文件转换为4.0的指标信息标准excel文件

	** 输出：	无
	** 返回值： 无
	*/
	MiddlewareXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag = 1);

	~MiddlewareXmlToXml(void);

private:
	
	/*
	** 功能：	初始化数据
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void InitDatas();

	/*
	** 功能：	文件处理主函数
	** 输入：	处理方式flag：
				XML_TO_XML_EXCEL 0  将3.5的xml文件转换为4.0的xml文件以及4.0的指标信息标准excel文件
				XML_TO_XML		 1	将3.5的xml文件转换为4.0的xml文件
				XML_TO_EXCEL	 2	将3.5的xml文件转换为4.0的指标信息标准excel文件
	** 输出：	无
	** 返回值： 无
	*/
	void AnalysisMain(int flag);

	/*
	** 功能：	处理需要解析的文件
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void ParseMiddleware();
	
	/*
	** 功能：	解析3.5的文件，并将信息存储的内存中，为生成4.0的信息做准备
	** 输入：	需要解析的3.5文件：pathfilename
	** 输出：	无
	** 返回值： 无
	*/
	void ParseFile(string pathfilename);


	/*
	** 功能：	对从3.5中提取的指标信息进行一个初步的校验
	** 输入：	指标信息集合 resourceattr
	** 输出：	无
	** 返回值： TRUE：合法；  FALSE：不合法
	*/
	BOOL CheckResourceattr(VEC_STRING_DATAS &resourceattr);


	/*
	** 功能：	将3.5的文件中某个指标信息进行分析提取，为生成4.0的信息做准备
	** 输入：	
				设备信息集合 deviceattr
				需要处理的文件对应的kpiinfo信息集合 kpimapcom
				指标信息集合 resourceattr
				指标对应的命令集合 collectattr
				指标对应的告警级别信息集合 kpipolicyattr
				指标对应的信息转换翻译 translationsdata
	** 输出：	无
	** 返回值： 无
	*/
	void ParseMetricData(VEC_STRING_DATAS &deviceattr, MAP_VEC_DATAS &kpimapcom, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, VEC_STRING_DATAS &kpipolicyattr, string translationsdata);


	/*
	** 功能：	根据3.5中的指标信息，生成4.0指标的column值，判断是否需要生成4.0中select选择器
	** 输入：	
				指标id ：tmpkpiid
				指标中的 collecttype
				3.5指标中的 resource->value值 ： resourcevalue
				需要处理的文件对应的kpiinfo信息集合 kpimapcom
				指标对应的命令集合 collectattr
				3.5指标类型 childrestype
				
	** 输出：	获取到的4.0指标中的 column值 ：columnvalue
				参数的个数比实际的collect个数是否多： isSpecial ，true 多，false：正常
	** 返回值： TRUE：生成；  FALSE：不生成
	*/
	BOOL GetColumnData(string tmpkpiid, string collecttype, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr, string childrestype, BOOL &isSpecial);
	
	/*
	** 功能：	给 Collect 中 GlobalMetricSetting 标签赋值
	** 输入：	pluginid标签值 pluginid
	parameterId标签值 parameterid
	** 输出：	无
	** 返回值： 无
	*/
	void SetCollectGlobalMetricSettingDatas(const string pluginid, const string parameterid);
	
	/*
	** 功能：	对command命令进行处理，判断命令中是否含有 $childid	
	** 输入：	4.0指标中的 pluginid 值 ： pluginid
				4.0指标中的 collectType 值 ： tempCollectType
				3.5指标中的子资源类型childrestype：childrestype
				3.5指标的command值 ： command
				4.0中需要处理的指标column值： columnvalue
	** 输出：	
				处理后的command值： command
				提取出的childid值： childidvalue
				处理后的4.0中的column值： columnvalue
				提出去command中的 ${***}, 将值存储到集合 cmdparam 中
	** 返回值： TRUE：含有$childid；  FALSE：不含有$childid
	*/
	BOOL ChangeCommand(const string pluginid, const string tempCollectType, const string childrestype, string &command, string &childidvalue, string &columnvalue, SET_STRING_DATAS &cmdparam);
	
	
	/*
	** 功能：	3.5的指标中的 resource->collects->collect->select 值的特殊替换处理
				对 $key 的特殊处理，以及$key 和另外一个 括号 的先后顺序问题
	** 输入：	
				需要处理的 select 值：strvalue
				子资源的类型：childrestype
	** 输出：	改变后的select值：strvalue
	** 返回值： TRUE：$key在前面；  FALSE：$key在后面
	*/
	BOOL ReplaceSpecial(string &strvalue, string childrestype);

	/*
	** 功能：	根据解析的文件种类，将3.5指标中 coltype=INNER_PROC 的指标，生成对应的pluginid
	** 输入：	无
	** 输出：	无
	** 返回值： 返回pluginid
	*/
	string GetMiddlewarePluginid();

	/*
	** 功能：	根据3.5的文件中某个指标信息生成4.0的collect.xml中的MetricPlugin标签信息
	** 输入：	
				需要处理的文件对应的kpiinfo信息集合 kpimapcmd
				指标信息集合 resourceattr
				指标对应的命令集合 collectattr
				指标对应的信息转换翻译 translationsdata
	** 输出：	无
	** 返回值： 无
	*/
	void PrepareMetricPluginAllData(MAP_VEC_DATAS &kpimapcmd, VEC_STRING_DATAS &resourceattr, VEC_VEC_DATAS &collectattr, string translationsdata);

public:

	SET_STRING_DATAS setCGlobalMetricSettingData_;		//Collect 中 GlobalMetricSetting 的数据set集合

	BOOL isInsertInstanceID_;		//是否需要新增 ***InstanceID指标
};
