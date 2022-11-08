package com.mainsteam.stm.portal.inspect.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.portal.inspect.api.InspectXMLHandlerApi;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportData;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <li>文件名称: InspectXMLHandlerImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:41:00
 * @author   dfw
 */
public class InspectXMLHandlerImpl implements InspectXMLHandlerApi{

	private  final Log logger = LogFactory.getLog(InspectXMLHandlerImpl.class);
	
	private IFileClientApi fileClient;
	
	
	public void setFileClient(IFileClientApi fileClient) {
		this.fileClient = fileClient;
	}

	public Long createITDXmlFile(InspectReportData ird){
		Date date = new Date();
		String fileName = date.getTime()+".xml";
		File file = createXml(ird ,new File(fileName));
		try {
			Long id = fileClient.upLoadFile(FileGroupEnum.STM_INSPECT_REPORT.name(), file);
			
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			file.delete();
		}
	}
	
	//通过fileID,获取xml文件,转化为ReportTemplateData类
	public InspectReportData createInspectReportData(Long fileID){
		
		try {
			return (InspectReportData)parseXml(new InspectReportData(),fileClient.getFileByID(fileID));
		} catch (Exception e) {
			logger.error("createReportTemplateData "+e);
			return null;
		}
	}
	
	//解析xml
	public Object parseXml(Object obj,File file){
		XStream sx = new XStream(new DomDriver("utf-8"));
		Class<?>[] cla = {Object.class,InspectReportData.class,InspectReportBasicBo.class,InspectReportSelfItemBo.class,InspectReportResultsSettingBo.class,InspectReportContentBo.class};
		sx.processAnnotations(cla);
		sx.alias(obj.getClass().getSimpleName(), obj.getClass());
		
		return (Object)sx.fromXML(file);
	}
	
	//生成xml
	public File createXml(Object obj,File file){
		XStream sx = new XStream(new DomDriver("utf-8"));
		Class<?>[] cla = {Object.class,InspectReportData.class,InspectReportBasicBo.class,InspectReportSelfItemBo.class,InspectReportResultsSettingBo.class,InspectReportContentBo.class};
		sx.processAnnotations(cla);		
		sx.alias(obj.getClass().getSimpleName(), obj.getClass());
		
		OutputStream fileOutput = null;
		try {
			fileOutput =  new FileOutputStream(file);
			
			sx.toXML(obj, fileOutput);
		} catch (FileNotFoundException e) {
			logger.error("Create Xml Error:"+file.getAbsolutePath(),e);
		}finally{
			if (fileOutput != null) {
				try {
					fileOutput.close();
				} catch (Exception e) {
					logger.error("fileOutput close error:",e);
				}
			}
		}
		
		return file;
	}
	
}
