package com.mainsteam.stm.portal.report.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.ireport.ExportReportTemplate;
import com.mainsteam.stm.ireport.IreportFileTypeEnum;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.portal.report.api.XMLHandlerApi;
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;
import com.mainsteam.stm.portal.report.bo.Table;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLHandlerImpl implements XMLHandlerApi{

	private  final Log logger = LogFactory.getLog(XMLHandlerImpl.class);
	
	private IFileClientApi fileClient;
	
	
	public void setFileClient(IFileClientApi fileClient) {
		this.fileClient = fileClient;
	}

	public Long createRTDXmlFile(ReportTemplateData rtd){
		Date date = new Date();
		String fileName = date.getTime()+".xml";
		File file = createXml(rtd ,new File(fileName));
		try {
			Long id = fileClient.upLoadFile(FileGroupEnum.STM_REPORT.name(), file);
			
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			file.delete();
		}
	}
	
	//通过fileID,获取xml文件,转化为ReportTemplateData类
	public ReportTemplateData createReportTemplateData(Long fileID){
		
		try {
			return (ReportTemplateData)parseXml(new ReportTemplateData(),fileClient.getFileByID(fileID));
		} catch (Exception e) {
			logger.error("createReportTemplateData "+e);
			return null;
		}
	}
	
//	public File exportFileByType(Long fileID,String type,String jasperPath){
//		ReportTemplateData rtd = createReportTemplateData( fileID);
//		
//		JasperPrint jp = fillData(rtd,jasperPath);
//		
//		ExportReportTemplate ert = new ExportReportTemplate();
//		switch (type) {
//		case "PDF":
//			return ert.exportFileByType(IreportFileTypeEnum.PDF, jp);
//		case "WORD":
//			return ert.exportFileByType(IreportFileTypeEnum.WORD, jp);
//		case "EXCEL":
//			return ert.exportFileByType(IreportFileTypeEnum.EXCEL, jp);
//		}
//		return null;
//	}
	
	public File exportFileByType(Long xmlFileID,IreportFileTypeEnum type,Long modelFileId){
		ReportTemplateData rtd = createReportTemplateData( xmlFileID);
		FileModel fileModel = new FileModel();
		try {
			fileModel = fileClient.getFileModelByID(modelFileId);
			JasperPrint jp = fillData(rtd,fileModel);
			ExportReportTemplate ert = new ExportReportTemplate();
			File file=ert.exportFileByType(type, jp);
			
			return file;
		} catch (Exception e) {
			logger.error("Export File ByType Error:"+type+","+fileModel.getFilePath(),e);
			return null;
		}
	}
	
	private JasperPrint fillData(ReportTemplateData rtd,FileModel fileModel){
//		ReportModelMain rmm = setReportModelMain(folderPath,rtd);
		
        ReportModelFillReport rmf = setReportModelFillReport(fileModel,rtd);
		
		return rmf.fillReportEnd();
	}
	
	private static ReportModelFillReport setReportModelFillReport(FileModel fileModel,ReportTemplateData rtd){
		ReportModelFillReport rmf = new ReportModelFillReport(fileModel);
		rmf.fillTitleReport(rtd.getName());
		for(ReportDirectory rd:rtd.getReportDirectory()){
			rmf.fillTitleReport(rd.getName());
			for(Chapter chapter : rd.getChapter()){
				rmf.fillTitleReport(chapter.getName());
				//sort:顺序(1.表格在上2.图表在上)
				if("1".equals(chapter.getSort())){
					for(Table table : chapter.getTable()){
						rmf.fillTableReport(table.getName(), table.getColumnsTitle(), table.getColumnsData());
					}
					if(chapter.getChart() != null)
					for(Chart chart : chapter.getChart()){
						//类型(1.堆叠柱状图2.折线图3.饼状图4.柱状图)
						switch (chart.getType()) {
						case "1":
							rmf.fillStackedBarReport(chart);
							break;
						case "2":
							rmf.fillLineReport(chart);
							break;
						case "3":
							rmf.fillPieReport(chart);
							break;
						case "4":
							rmf.fillBarReport(chart);
							break;
						}
					}
				}else{
					if(chapter.getChart() != null)
					for(Chart chart : chapter.getChart()){
						//类型(1.堆叠柱状图2.折线图3.饼状图4.柱状图)
						switch (chart.getType()) {
						case "1":
							rmf.fillStackedBarReport(chart);
							break;
						case "2":
							rmf.fillLineReport(chart);
							break;
						case "3":
							rmf.fillPieReport(chart);
							break;
						case "4":
							rmf.fillBarReport(chart);
							break;
						}
					}
					for(Table table : chapter.getTable()){
						rmf.fillTableReport(table.getName(), table.getColumnsTitle(), table.getColumnsData());
					}
				}
			}
		}
		return rmf;
	}
	
//	private static ReportModelMain setReportModelMain(String folderPath,ReportTemplateData rtd){
//		int index = 0;
//		
//		
//		ReportModelMain rmm = new ReportModelMain();
//		rmm.addTitleReport(index(index++));
//		for(ReportDirectory rd:rtd.getReportDirectory()){
//			rmm.addTitleReport(index(index++));
//			for(Table table:rd.getTable()){
//				rmm.addTableReport( table.getColumnsData().getTableData().size(), table.getColumnsTitle());
//				
//				if(null!=table.getIsHaveChart()&&"true".equals(table.getIsHaveChart())){
//					for(Chart chart:table.getChart()){
//						//类型(1.柱状图2.折线图3.饼状图)
//						switch (chart.getType()) {
//						case "1":
//							rmm.addBarReport( chart.getChartData().size());
//							break;
//						case "2":
//							rmm.addLineReport( chart.getChartData().size());
//							break;
//						case "3":
//							rmm.addPieReport(index(index++));
//							break;
//						}
//					}
//				}
//				
//			}
//		}
//		rmm.writeAndComplieJrxmlFile(folderPath);
//		return rmm;
//	}
	
	//解析xml
	public Object parseXml(Object obj,File file){
		XStream sx = new XStream(new DomDriver("utf-8"));
		Class[] cla = {Object.class,ReportTemplateData.class,ReportDirectory.class,Table.class,ColumnsTitle.class,ColumnsData.class,Columns.class,Chart.class,ChartData.class};
		sx.processAnnotations(cla);
		sx.alias(obj.getClass().getSimpleName(), obj.getClass());
		
		return (Object)sx.fromXML(file);
	}
	
	//生成xml
	public File createXml(Object obj,File file){
		
		XStream sx = new XStream(new DomDriver("utf-8"));
		
		Class[] cla = {Object.class,ReportTemplateData.class,ReportDirectory.class,Table.class,ColumnsTitle.class,ColumnsData.class,Columns.class,Chart.class,ChartData.class};
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

	@Override
	public File exportFileByTempReport(IreportFileTypeEnum type, ReportTemplateData rtd, ReportTemplate reportTemplate) {
		FileModel fileModel = new FileModel();
		try {
			fileModel = fileClient.getFileModelByID(Long.valueOf(reportTemplate.getReportTemplateModelName()));
			JasperPrint jp = fillData(rtd,fileModel);
			ExportReportTemplate ert = new ExportReportTemplate();
			File file=ert.exportFileByType(type, jp);
			
			return file;
		} catch (Exception e) {
			logger.error("exportFileByTempReport Error:"+type+","+fileModel.getFilePath(),e);
			return null;
		}
	}
	
}
