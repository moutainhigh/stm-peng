package com.mainsteam.stm.portal.report.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ireport.IreportFileTypeEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.report.api.ReportApi;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.api.XMLHandlerApi;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.Report;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.engine.ReportTask;
import com.mainsteam.stm.portal.report.web.vo.FileDownloadVo;


@Controller
@RequestMapping("/portal/report/reportTemplateXmlInfo")
public class ReportTemplateXmlInfoAction extends BaseAction {
	private Logger log = Logger.getLogger(ReportTemplateXmlInfoAction.class);
	@Resource
	private XMLHandlerApi xmlHandlerApi;
	@Resource
	private ReportApi ReportApi;
	@Resource
	private ReportTemplateApi reportTemplateApi;
	
	
	@RequestMapping("/getXmlInfo")
	public JSONObject getXmlInfo(Long reportXmlDataFileId){
		
		ReportTemplateData rtd = xmlHandlerApi.createReportTemplateData(reportXmlDataFileId);
		
		if(null!=rtd){
			String[] str = new String[1];
			str[0] = String.valueOf(reportXmlDataFileId);
			List<Report> reList =  ReportApi.selectByReportXmlData(str);
			if(reList.size()>0){
				Report report = reList.get(0);
				if(report.getReportStatus()==0){
					//更新为已阅
					report.setReportStatus(1);
					ReportApi.updateReportStatus(report);
				}
			}
		}else{
			return toSuccess(null);
		}
		return toSuccess(rtd);
	}
	
	@RequestMapping("/getXmlInfoTest")
	public void getXmlInfoTest(HttpServletResponse response){
		
//		ExportReportTemplate ert = new ExportReportTemplate();
//		
//		writeFileToClient(ert.exportFileTest(IreportFileTypeEnum.PDF),response);
		
		ReportTemplateData rtd = createReport();
		
		xmlHandlerApi.createRTDXmlFile(rtd);
	}
	
	@RequestMapping("/getFileDownload")
	public String getFileDownload(FileDownloadVo fileDownloadVo,HttpServletResponse response){
		
		IreportFileTypeEnum ift = IreportFileTypeEnum.PDF;
		switch (fileDownloadVo.getType()) {
		case "pdf":
			ift = IreportFileTypeEnum.PDF;
			break;
		case "word":
			ift = IreportFileTypeEnum.WORD;
			break;
		case "excel":
			ift = IreportFileTypeEnum.EXCEL;
			break;
		}
		
		FileOutputStream fous = null;
		ZipOutputStream zipOut = null;
		List<File> fileList = new ArrayList<File>();
		
		String fullNamePartOne = null;
		String[] reportCreateTimeStr = fileDownloadVo.getReportCreateTimeStr();
		ReportTemplateExpand  rt= reportTemplateApi.getSimpleReportTemplateById(fileDownloadVo.getReportTemplateId());
		//模板类型,1.性能报告2.告警统计3.TOPN报告4.可用性报告5.趋势报告.6.分析报告7.综合性报告8.性能报表(虚拟化)9.告警统计(虚拟化)
		switch(rt.getReportTemplateType()){
		case 1:
			fullNamePartOne = "性能报告-"+rt.getReportTemplateName();
			break;
		case 2:
			fullNamePartOne = "告警统计-"+rt.getReportTemplateName();
			break;
		case 3:
			fullNamePartOne = "TOPN报告-"+rt.getReportTemplateName();
			break;
		case 4:
			fullNamePartOne = "可用性报告-"+rt.getReportTemplateName();
			break;
		case 5:
			fullNamePartOne = "趋势报告-"+rt.getReportTemplateName();
			break;
		case 6:
			fullNamePartOne = "分析报告-"+rt.getReportTemplateName();
			break;
		case 7:
			fullNamePartOne = "综合性报告-"+rt.getReportTemplateName();
			break;
		case 8:
			fullNamePartOne = "虚拟化-性能报表-"+rt.getReportTemplateName();
			break;
		case 9:
			fullNamePartOne = "虚拟化-告警统计-"+rt.getReportTemplateName();
			break;
		default :
			fullNamePartOne = "报表-"+rt.getReportTemplateName();
			break;
		}
		
		try{
			Long[] xmlFileIDArr = fileDownloadVo.getXmlFileID();
			Long[] modelFileIdArr = fileDownloadVo.getModelFileId();
			int length = xmlFileIDArr.length;
			for(int i=0;i<length;i++){
				File file = xmlHandlerApi.exportFileByType(xmlFileIDArr[i], ift, modelFileIdArr[i]);
				if(null!=file){
					fileList.add(file);
				}
				
			}
			if(fileList.size()==1){
				String fullFileName = fullNamePartOne+"-"+reportCreateTimeStr[0];
				writeFileToClient(fileList.get(0),fullFileName,response);
				return null;
			}else if(fileList.size()>1){
				Date date = new Date();
				String fileName = date.getTime()+".rar";
				File fileAll = new File(fileName);
				
				fous = new FileOutputStream(fileAll); 
				zipOut = new ZipOutputStream(fous);
		        for(int i = 0; i < fileList.size(); i++) {
		            File file = fileList.get(i);
		            String fullFileName = fullNamePartOne+"-"+reportCreateTimeStr[i];
		            zipFile(file, zipOut,fullFileName);
		            
		        }
		        
		        zipOut.flush();
		        zipOut.close();
		        fous.close();
		        
		        writeFileToClient(fileAll,fullNamePartOne+"-批量导出",response);
				return null;
			}else{
				return "下载失败!";
			}
			
		}catch(Exception e){
			e.printStackTrace();
			if(null!=fous){
				try {
					fous.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(null!=zipOut){
				try {
					zipOut.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(fileList.size()>1){
				for(File file:fileList){
					if(null!=file){
						file.delete();
					}
				}
			}
			return "下载失败!";
		}finally{
			if(null!=fous){
				try {
					fous.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(null!=zipOut){
				try {
					zipOut.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(fileList.size()>1){
				for(File file:fileList){
					if(null!=file){
						file.delete();
					}
				}
			}
		}
		
	}
	
	/**
	 * 把文件写到前台
	 * @param file
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	private void writeFileToClient(File file,String downloadName, HttpServletResponse response) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			fis = new FileInputStream(file);
			downloadName = downloadName+"."+file.getName().split("\\.")[1];
			String fileName = URLEncoder.encode(downloadName, "UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			//   filename*=utf-8'zh_cn'   兼容ie,chrome,firefox浏览器
			response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + fileName);
			response.setContentType("application/octet-stream");
			
			int contentLength = fis.available();
			response.setContentLength(contentLength);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
		} catch (Exception e) {
			log.error(e);
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e1) {
					log.error(e1);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e2) {
					log.error(e2);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e3) {
					log.error(e3);
				}
			}
			if(null!=file){
				file.delete();
			}
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
			if(null!=file){
				file.delete();
			}
		}
	}
	
	
	/**  
     * 根据输入的文件与输出流对文件进行打包
     */
    public static void zipFile(File inputFile,
            ZipOutputStream ouputStream,String fileName) {
        try {
            if(inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN);
                    //org.apache.tools.zip.ZipEntry
                    fileName = fileName+"."+inputFile.getName().split("\\.")[1];
                    ZipEntry entry = new ZipEntry(fileName);
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据   
                    int nNumber;
                    byte[] buffer = new byte[2048];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
//                    ouputStream.flush();
                    // 关闭创建的流对象   
                    bins.close();
                    IN.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public ReportTemplateData createReport(){
		ReportTemplateData rtd = new ReportTemplateData();
		ReportDirectory rdy = new ReportDirectory();
		
		List<Table> tableList = new ArrayList<Table>();
		Table table = createTable1();
		tableList.add(table);
		
		
		
		rdy.setName("交换机");
		List<ReportDirectory> rdList = new ArrayList<ReportDirectory>();
		
		
		
		rdList.add(rdy);
		
		rtd.setCycle("1");
		rtd.setName("集团总部交换机性能概况");
		rtd.setReportDirectory(rdList);
		rtd.setTimeScope("2014-04-19 00:00 --- 2014-04-19 24:00");
		rtd.setType("1");
		
		return rtd;
	}
	
	private Table createTable1(){
		ColumnsTitle cTitle = new ColumnsTitle();
		ColumnsData columData = new ColumnsData();
		
		
		List<Chart> chartList = new ArrayList<Chart>();
		Chart chart1 = new Chart();
		List<ChartData> chartListData = new ArrayList<ChartData>();
		ChartData chartData1 = new ChartData();
		ChartData chartData2 = new ChartData();
		ChartData chartData3 = new ChartData();
		chartData1.setIp("127.0.0.1");
		chartData1.setName("Switch_14532_1");
		chartData1.setValue("34");
		chartData2.setIp("127.0.0.1");
		chartData2.setName("Switch_14532_2");
		chartData2.setValue("37");
		chartData3.setIp("127.0.0.1");
		chartData3.setName("Switch_14532_3");
		chartData3.setValue("30");
		chartListData.add(chartData1);
		chartListData.add(chartData2);
		chartListData.add(chartData3);
		chart1.setChartData(chartListData);
		chart1.setType("1");
		chart1.setName("CPU平均利用率(%)");
		chartList.add(chart1);
		
		Chart chart2 = new Chart();
		List<ChartData> chartListData2 = new ArrayList<ChartData>();
		ChartData chartData11 = new ChartData();
		ChartData chartData22 = new ChartData();
		ChartData chartData33 = new ChartData();
		chartData11.setIp("127.0.0.1");
		chartData11.setName("Switch_14532_1");
		chartData11.setValue("4,24,15,51,17,9,49");
		chartData22.setIp("127.0.0.1");
		chartData22.setName("Switch_14532_2");
		chartData22.setValue("24,21,45,25,67,21,41");
		chartData33.setIp("127.0.0.1");
		chartData33.setName("Switch_14532_3");
		chartData33.setValue("34,59,25,10,47,12,42");
		chartListData2.add(chartData11);
		chartListData2.add(chartData22);
		chartListData2.add(chartData33);
		chart2.setChartData(chartListData2);
		chart2.setType("2");
		chart2.setName("内存利用率(%)");
		chart2.setInfo("1:00,2:00,3:00,4:00,5:00,6:00,7:00");
		chartList.add(chart2);
		
		Chart chart3 = new Chart();
		List<ChartData> chartListData3 = new ArrayList<ChartData>();
		ChartData chartData111 = new ChartData();
		ChartData chartData222 = new ChartData();
		ChartData chartData333 = new ChartData();
		chartData111.setIp("127.0.0.1");
		chartData111.setName("Switch_14532_1");
		chartData111.setValue("100");
		chartData222.setIp("127.0.0.1");
		chartData222.setName("Switch_14532_2");
		chartData222.setValue("120");
		chartData333.setIp("127.0.0.1");
		chartData333.setName("Switch_14532_3");
		chartData333.setValue("140");
		chartListData3.add(chartData111);
		chartListData3.add(chartData222);
		chartListData3.add(chartData333);
		chart3.setChartData(chartListData3);
		chart3.setType("3");
		chart3.setName("告警分布(%)");
		chart3.setInfo("360");
		chartList.add(chart3);
		
		Chart chart4 = new Chart();
		List<ChartData> chartListData4 = new ArrayList<ChartData>();
		ChartData chartData1111 = new ChartData();
		ChartData chartData2222 = new ChartData();
		ChartData chartData3333 = new ChartData();
		chartData1111.setIp("127.0.0.1");
		chartData1111.setName("Switch_14532_1");
		chartData1111.setValue("120");
		chartData2222.setIp("127.0.0.1");
		chartData2222.setName("Switch_14532_2");
		chartData2222.setValue("120");
		chartData3333.setIp("127.0.0.1");
		chartData3333.setName("Switch_14532_3");
		chartData3333.setValue("120");
		chartListData4.add(chartData1111);
		chartListData4.add(chartData2222);
		chartListData4.add(chartData3333);
		chart4.setChartData(chartListData4);
		chart4.setType("3");
		chart4.setName("级别分布(%)");
		chart4.setInfo("360");
		chartList.add(chart4);
		
		List<TableData> tablaDataList = new ArrayList<TableData>();
		TableData tData1 = new TableData();
		TableData tData2 = new TableData();
		TableData tData3 = new TableData();
		tData1.setValue("1,Latte ZB Switch_14532_1,172.0.0.1,12,12,34,54,16,67,34,12,34,34,12,34");
		tData2.setValue("2,Latte ZB Switch_14532_2,172.0.0.1,22,32,11,24,23,34,34,12,34,34,12,34");
		tData3.setValue("3,Latte ZB Switch_14532_3,172.0.0.1,34,6,57,14,8,14,34,12,34,34,12,34");
		tablaDataList.add(tData1);
		tablaDataList.add(tData2);
		tablaDataList.add(tData3);
		columData.setTableData(tablaDataList);
		
		List<Columns> columList = new ArrayList<Columns>();
		Columns colum1 = new Columns();
		Columns colum2 = new Columns();
		Columns colum3 = new Columns();
		Columns colum4 = new Columns();
		Columns colum5 = new Columns();
		Columns colum6 = new Columns();
		Columns colum7 = new Columns();
		colum1.setText("序列号");
		colum2.setText("资源名称");
		colum3.setText("IP地址");
		colum4.setText("CPU平均利用率(%)");
		colum4.setApart("true");
		colum5.setText("内存利用率(%)");
		colum5.setApart("true");
		colum6.setText("吞吐量(%)");
		colum6.setApart("true");
		colum7.setText("丢包率(%)");
		colum7.setApart("true");
		columList.add(colum1);
		columList.add(colum2);
		columList.add(colum3);
		columList.add(colum4);
		columList.add(colum5);
		columList.add(colum6);
		columList.add(colum7);
		cTitle.setColumns(columList);
		
		
		Table table = new Table();
		table.setColumnsData(columData);
		table.setColumnsTitle(cTitle);
		table.setName("汇总报告");
		
		return table;
	}

	@RequestMapping("/exportReportListByReportTemplateIdAndTime")
	public String exportReportByReportTemplateIdAndTime(Long reportTemplateId,String dateStartStr, String dateEndStr, String type, HttpServletResponse response) throws ParseException{
		ReportTemplate reportTemplate = reportTemplateApi.getReportTemplateForCurrentReport(reportTemplateId);
		DateFormat parseDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		boolean instanceIsNull = true;
		//判断模板资源是否被删除后为空
		for(ReportTemplateDirectory directory : reportTemplate.getDirectoryList()){
			if(directory.getDirectoryInstanceList().size() > 0){
				instanceIsNull = false;
				break;
			}
		}
		if(instanceIsNull){
			return "下载失败";
		}
		ReportTask reportTask = new ReportTask(reportTemplate);
		Date dateStart = parseDateTime.parse(dateStartStr);
		Date dateEnd = parseDateTime.parse(dateEndStr);
		ReportTemplateData rtd = reportTask.getReportDate(dateStart,dateEnd);

		IreportFileTypeEnum ift = IreportFileTypeEnum.EXCEL;
		switch (type) {
		case "pdf":
			ift = IreportFileTypeEnum.PDF;
			break;
		case "word":
			ift = IreportFileTypeEnum.WORD;
			break;
		case "excel":
			ift = IreportFileTypeEnum.EXCEL;
			break;
		}
		File file = xmlHandlerApi.exportFileByTempReport(ift, rtd, reportTemplate);
		if(file == null){
			return "下载失败";
		}
		writeFileToClient(file, reportTemplate.getReportTemplateName(), response);
		
		return null;
	}
}
