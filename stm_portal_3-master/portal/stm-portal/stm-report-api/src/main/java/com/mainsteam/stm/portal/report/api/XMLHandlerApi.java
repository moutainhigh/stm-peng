package com.mainsteam.stm.portal.report.api;

import java.io.File;

import com.mainsteam.stm.ireport.IreportFileTypeEnum;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;


public interface XMLHandlerApi {

	//通过ReportTemplateData,转化为xml,并保存到文件系统
	public Long createRTDXmlFile(ReportTemplateData rtd);
	
	//通过fileID,获取xml文件,转化为ReportTemplateData类
	public ReportTemplateData createReportTemplateData(Long fileID);
	
	//导出文件
//	public File exportFileByType(Long fileID,String type,String jasperPath);
	public File exportFileByType(Long fileID,IreportFileTypeEnum type,Long templateFileileID);
	
	
	public File exportFileByTempReport(IreportFileTypeEnum type, ReportTemplateData rtd, ReportTemplate reportTemplate);
	
	//解析xml
	public Object parseXml(Object obj,File file);
	//生成xml
	public File createXml(Object obj,File file);

}
