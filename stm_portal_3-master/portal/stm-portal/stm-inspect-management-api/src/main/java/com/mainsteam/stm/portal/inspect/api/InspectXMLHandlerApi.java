package com.mainsteam.stm.portal.inspect.api;

import java.io.File;

import com.mainsteam.stm.portal.inspect.bo.InspectReportData;

/**
 * <li>文件名称: InspectXMLHandlerApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:41:17
 * @author   dfw
 */
public interface InspectXMLHandlerApi {

	/**
	 * 通过InspectReportData,转化为xml,并保存到文件系统
	 * @param ird
	 * @return
	 */
	public Long createITDXmlFile(InspectReportData ird);
	
	/**
	 * 通过fileID,获取xml文件,转化为InspectReportData类
	 * @param fileID
	 * @return
	 */
	public InspectReportData createInspectReportData(Long fileID);
	
	/**
	 * 解析xml
	 * @param obj
	 * @param file
	 * @return
	 */
	public Object parseXml(Object obj,File file);
	
	/**
	 * 生成xml
	 * @param obj
	 * @param file
	 * @return
	 */
	public File createXml(Object obj,File file);

}
