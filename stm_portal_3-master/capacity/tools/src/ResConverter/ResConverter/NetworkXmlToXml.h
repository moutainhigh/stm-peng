/**********************************************************************
Copyright (C), 2014, China WiSERV Technologies Co., Ltd.
File name:		NetworkXmlToXml.h
Description:	将3.5中network网络设备的配置文件转换为4.0中的配置文件或者标准的excel文件
Function List:

Histroy:
Author     CR/PR       Date        Reason / Description of Change
--------  --------  -----------  ----------------------------------
huoq             2014-08		     Creation of the file
**********************************************************************/

#pragma once

#include "BaseConver.h"

class NetworkXmlToXml : public BaseConver
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
	NetworkXmlToXml(string xmlpath, string filename, string kpifilename, string outfilepath, int flag = 1);

	~NetworkXmlToXml(void);

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
	void ParseNetwork();

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
				3.5指标中的 resource->value值 ： resourcevalue
				需要处理的文件对应的kpiinfo信息集合 kpimapcom
				指标对应的命令集合 collectattr

	** 输出：	获取到的4.0指标中的 column值 ：columnvalue
				3.5指标中的 resource->value值 ： resourcevalue
	** 返回值： TRUE：生成；  FALSE：不生成
	*/
	BOOL GetColumnData(string tmpkpiid, string &resourcevalue, string &columnvalue, MAP_VEC_DATAS &kpimapcmd, VEC_VEC_DATAS &collectattr);
	

	/*
	** 功能：	解析的指标是否需要特殊处理
	** 输入：	指标的id
	** 输出：	无
	** 返回值： TRUE：是；  FALSE：否
	*/
	BOOL SpecialHandling(string kpiid);

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

	/*
	** 功能：	给 Collect 中 GlobalMetricSetting 标签赋值
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void SetCollectGlobalMetricSettingDatas();

	/*
	** 功能：	检查是否需要合并同类型的产品，如果需要，将路径修改以及合并
	** 输入：	合并的产品 findstr
				合并的类型 findtype
	** 输出：	无
	** 返回值： 无
	*/
	void ChangeOutFolderSub(const string findstr, const string findtype);

	/*
	** 功能：	检查是否需要合并同类型的产品，如果需要，将路径修改以及合并
	** 输入：	无
	** 输出：	无
	** 返回值： 无
	*/
	void ChangeOutFolder();


public:

	BOOL isWriteResourceXml_;	//是否写resource.xml文件
	VEC_STRING_DATAS vetsysoid_;	//sysoid的记录集合

};
