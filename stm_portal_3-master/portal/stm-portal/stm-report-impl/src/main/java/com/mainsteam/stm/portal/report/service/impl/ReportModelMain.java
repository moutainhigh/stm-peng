package com.mainsteam.stm.portal.report.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseSubreport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;

/**
 * 生成jasper模版的使用
 * ReportModelMain rmm = new ReportModelMain(user, fileClient);
 * rmm.addLineReport();
 * ...
 * rmm.writeAndComplieJrxmlFile();
 */
public class ReportModelMain {
	private Document document = DocumentHelper.createDocument();
	private Element root;
	private int reportInitWidth = 1000;
	private int reportInitHeight = 10;
	private int reportInitMargin = 5;
	private int subReportInitHeight = 250;
	private int subReportTitleInitHeight = 30;
	private int reportContentWidth;
	private int pieChartWidth;
	private String mainReportName = "main";
	private String jasperSuffix = ".jasper";
	private String userId;
	private IFileClientApi fileClient;
	private List<Document> subReportDocList = new ArrayList<Document>();
	private List<Element> subreportDomList = new ArrayList<Element>();
	private String SUBREPORT_DIR_STRING = "SUBREPORT_DIR";
	private int pieCounter = 0;
	private int counter = 0;
	
	public ReportModelMain(String userId, IFileClientApi fileClient) {
		this.userId = userId;
		this.fileClient = fileClient;
		this.root = ReportModelUtil.createReport(document, mainReportName, reportInitWidth, reportInitHeight, reportInitMargin);
		this.root.addElement("detail");
		this.reportContentWidth = ReportModelUtil.getRepCurWidth(document);
		this.pieChartWidth = reportContentWidth / 2;
		// 新增子报表相对路径参数
		Element SUBREPORT_DIR = ReportModelUtil.addParameter(document, SUBREPORT_DIR_STRING, String.class.getName());
		SUBREPORT_DIR.addAttribute("isForPrompting", "false");
		Element defaultValueExpression = SUBREPORT_DIR.addElement("defaultValueExpression");
		defaultValueExpression.addCDATA("\"\"");
	}
	/**
	 * 新增线图
	 * @param reportName
	 * @param instanceCnt
	 */
	public void addLineReport(int instanceCnt) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		Element subreport = addSubReportNode("line", newReportName, reportContentWidth, subReportInitHeight);
		ReportModelLine rml = new ReportModelLine(newReportName, reportContentWidth, subReportInitHeight, instanceCnt);
		Document lineDocument = rml.createReport();
		subReportDocList.add(lineDocument);
		subreportDomList.add(subreport);
	}
	/**
	 * 新增堆叠柱图
	 * @param reportName
	 * @param instanceCnt
	 */
	public void addStackedBarReport(int instanceCnt) {
		int barChartHeight = subReportInitHeight;
		if(instanceCnt > ReportModelStackedBar.horizontalCnt){
			barChartHeight = instanceCnt * ReportModelStackedBar.oneBarWidth;
		}
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		Element subreport = addSubReportNode("bar", newReportName, reportContentWidth, barChartHeight);
		ReportModelStackedBar rmb = new ReportModelStackedBar(newReportName, reportContentWidth, barChartHeight, instanceCnt);
		Document barDocument = rmb.createReport();
		subReportDocList.add(barDocument);
		subreportDomList.add(subreport);
	}
	/**
	 * 新增堆叠柱图
	 * @param reportName
	 * @param instanceCnt
	 */
	public void addBarReport(int instanceCnt) {
		int barChartHeight = subReportInitHeight;
		if(instanceCnt > ReportModelStackedBar.horizontalCnt){
			barChartHeight = instanceCnt * ReportModelStackedBar.oneBarWidth;
		}
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		Element subreport = addSubReportNode("bar", newReportName, reportContentWidth, barChartHeight);
		ReportModelBar rmb = new ReportModelBar(newReportName, reportContentWidth, barChartHeight, instanceCnt);
		Document barDocument = rmb.createReport();
		subReportDocList.add(barDocument);
		subreportDomList.add(subreport);
	}
	/**
	 * 新增饼图
	 * @param reportName
	 */
	public void addPieReport() {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		Element subreport = addSubReportNode("pie", newReportName, pieChartWidth, subReportInitHeight);
		ReportModelPie rmp = new ReportModelPie(newReportName, pieChartWidth, subReportInitHeight);
		Document pieDocument = rmp.createReport();
		subReportDocList.add(pieDocument);
		subreportDomList.add(subreport);
	}
	/**
	 * 新增表格
	 * @param reportName
	 * @param instanceCnt
	 * @param columnsTitle
	 */
	public void addTableReport(ColumnsTitle columnsTitle) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		int height = (2 * reportInitMargin) + ReportModelTable.tableHeadRowCount * ReportModelTable.tableRowHeight;
		Element subreport = addSubReportNode("table", newReportName, reportContentWidth, height);
		ReportModelTable rmt = new ReportModelTable(newReportName, reportContentWidth, height, columnsTitle);
		Document tableDocument = rmt.createReport();
		subReportDocList.add(tableDocument);
		subreportDomList.add(subreport);
	}
	/**
	 * 新增标题
	 * @param reportName
	 */
	public void addTitleReport() {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		Element subreport = addSubReportNode("title", newReportName, reportContentWidth, subReportTitleInitHeight);
		ReportModelTitle rmt = new ReportModelTitle(newReportName, reportContentWidth, subReportTitleInitHeight);
		Document titleDocument = rmt.createReport();
		subReportDocList.add(titleDocument);
		subreportDomList.add(subreport);
	}
	
	/**
	 * 在主报表中新增一个子报表节点
	 * "$P{" + subReportName + "}"、subList、subReportName + ".jasper"、、
	 * @param subReportName
	 * @param width
	 * @param height
	 * @return
	 */
	private Element addSubReportNode(String chartType, String subReportName, int width, int height) {
		Element detail = ReportModelUtil.findDetailElement(root);
		Element band = null;
		// 如果是pie
		if("pie".equals(chartType) && pieCounter % 2 == 1){
			List<Element> bandList = detail.elements("band");
			band = bandList.get(bandList.size() - 1);
		}else{
			band = detail.addElement("band");
			band.addAttribute("height", "0");
			ReportModelUtil.addRepHeight(document, band, height);
			// 图表都不能扩展
			if("table".equals(chartType)){
				band.addAttribute("splitType", "Stretch");
			}else{
				band.addAttribute("splitType", "Prevent");
			}
		}
		
		Element subreport = band.addElement("subreport");
		Element reportElement = subreport.addElement("reportElement");
		reportElement.addAttribute("x", "0");
		reportElement.addAttribute("y", "0");
		
		// 如果是pie要特殊处理位置和高度
		if("pie".equals(chartType)){
			if(pieCounter % 2 == 1){
				reportElement.attribute("x").setValue(String.valueOf(pieChartWidth));
			}
			pieCounter ++;
		}else{
			pieCounter = 0;
		}
		
		reportElement.addAttribute("width", String.valueOf(width));
		reportElement.addAttribute("height", String.valueOf(height));
		reportElement.addAttribute("uuid", ReportModelUtil.getUUID());
		
		ReportModelUtil.addParameter(document, subReportName, Object.class.getName());
		String parameterName = "$P{" + subReportName + "}";
		Element parametersMapExpression = subreport.addElement("parametersMapExpression");
		parametersMapExpression.addCDATA(parameterName);

		String dataSourceName = "new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(((Map<String, List>)"
				+ parameterName + ").get(\"subList\"))";
		Element dataSourceExpression = subreport.addElement("dataSourceExpression");
		dataSourceExpression.addCDATA(dataSourceName);

//		Element subreportExpression = subreport.addElement("subreportExpression");
//		subreportExpression.addCDATA("\"" + subReportName + "\"");
		return subreport;
	}
	
	/**
	 * 写出所有jrxmlfile
	 * 
	 * @param path
	 */
	public Long writeAndComplieJrxmlFile(){
		// 编译jrxml文件
		Long mainJasperFileId = null;
		try {
			List<String> tmpFilePath = new ArrayList<String>();
			for (int i = 0; i < subReportDocList.size(); i++) {
				Document document = subReportDocList.get(i);
				File subJrxmlFile = ReportModelUtil.writeJrxmlFile(document, userId);
				String subJasperFilePath = subJrxmlFile.getAbsolutePath() + jasperSuffix;
				ReportModelUtil.compile(subJrxmlFile.getAbsolutePath(), subJasperFilePath);
				long subJasperFileId = fileClient.upLoadFile(FileGroupEnum.STM_REPORT, new File(subJasperFilePath));
				// 在主模块上加上子模块的引用
				Element subreportExpression = subreportDomList.get(i).addElement("subreportExpression");
				subreportExpression.addCDATA("$P{" + SUBREPORT_DIR_STRING + "} + \"" + String.valueOf(subJasperFileId) + "\"");
				
				tmpFilePath.add(subJrxmlFile.getAbsolutePath());
				tmpFilePath.add(subJasperFilePath);
			}
			
			File mainJrxmlFile = ReportModelUtil.writeJrxmlFile(this.document, userId);
			String mainJasperFilePath = mainJrxmlFile.getAbsolutePath() + jasperSuffix;
			ReportModelUtil.compile(mainJrxmlFile.getAbsolutePath(), mainJasperFilePath);
			mainJasperFileId = fileClient.upLoadFile(FileGroupEnum.STM_REPORT, new File(mainJasperFilePath));
			
			tmpFilePath.add(mainJrxmlFile.getAbsolutePath());
			tmpFilePath.add(mainJasperFilePath);
			
			// 删除临时文件
//			for (int i = 0; i < tmpFilePath.size(); i++) {
//				new File(tmpFilePath.get(i)).delete();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainJasperFileId;
	}
	
	/**
	 * 删除报表模版文件
	 * @param fileClient
	 * @param rmp
	 */
	public static void deleteReportModel(IFileClientApi fileClient, long mainJasperFileId){
		try {
			File mainJasper = fileClient.getFileByID(mainJasperFileId);
			JasperReport jr = (JasperReport) JRLoader.loadObject(mainJasper);
			JRBand[] jRBands = jr.getAllBands();
			for(int i = 0; i < jRBands.length; i ++){
				JRBand jRBand = jRBands[i];
				JRChild jrChild = jRBand.getChildren().get(0);
				if(jrChild instanceof JRBaseSubreport){
					JRBaseSubreport jrBaseSubreport = (JRBaseSubreport)jrChild;
					String subReportName = jrBaseSubreport.getExpression().getChunks()[1].getText();
					subReportName = subReportName.substring(subReportName.indexOf("\"") + 1, subReportName.lastIndexOf("\""));
					fileClient.deleteFile(Long.valueOf(subReportName));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
