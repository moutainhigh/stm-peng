package com.mainsteam.stm.portal.report.service.impl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mainsteam.stm.portal.report.customchart.LineChartCustomizer;
import com.mainsteam.stm.portal.report.customchart.UniqueCategoryLabel;

public class ReportModelLine {
	private Document document = DocumentHelper.createDocument();
	private Element root;
	private String reportName;
	private int width;
	private int height;
	private int instanceCnt;
	private int margin = 5;
	private String fieldNextNode = "summary";
	private Element summaryBand;

	public ReportModelLine(String reportName, int width, int height, int instanceCnt) {
		this.reportName = reportName;
		this.width = width;
		this.height = height;
		this.instanceCnt = instanceCnt;
	}
	/**
	 * 创建报表
	 * 
	 * @return
	 */
	public Document createReport(){
		this.root = ReportModelUtil.createReport(document, reportName, width, height, margin);
		Element summary = this.root.addElement("summary");
		this.summaryBand = summary.addElement("band");
		this.summaryBand.addAttribute("height", String.valueOf(height - 2 * margin));
		this.summaryBand.addAttribute("splitType", "Prevent");
		
		addLineChartNode();
		
		return document;
	}
	
	/**
	 * 新增linechart
	 * $P{title}、"$P{" + instanceId + "}"、$F{category}、"$F{" + instanceId + "}"、
	 * @return
	 */
	public Element addLineChartNode(){
		//chart
		String title = "title";
		Element lineChart = this.summaryBand.addElement("lineChart");
		ReportModelUtil.addParameter(document, title, String.class.getName());
		
		Element chart = lineChart.addElement("chart");
		chart.addAttribute("customizerClass", LineChartCustomizer.class.getName());
		Element reportElement = chart.addElement("reportElement");
		reportElement.addAttribute("x", "0");
		reportElement.addAttribute("y", "0");
		reportElement.addAttribute("width", String.valueOf(ReportModelUtil.getRepCurWidth(document)));
		reportElement.addAttribute("height", this.summaryBand.attributeValue("height"));
		reportElement.addAttribute("uuid", ReportModelUtil.getUUID());
		ReportModelUtil.addBorderElement(chart, 1);
		
		Element chartTitle = chart.addElement("chartTitle");
		chartTitle.addAttribute("position", "Top");
		chartTitle.addAttribute("color", "#00FF00");
		Element chartTitleFont = chartTitle.addElement("font");
		chartTitleFont.addAttribute("size", "14");
		chartTitleFont.addAttribute("isBold", "true");
		Element titleExpression = chartTitle.addElement("titleExpression");
		titleExpression.addCDATA("$P{" + title + "}");
		chart.addElement("chartSubtitle");
		Element chartLegend = chart.addElement("chartLegend");
		chartLegend.addAttribute("position", "Right");
		//categoryDataset
		String category = "category";
		ReportModelUtil.addField(document, category, UniqueCategoryLabel.class.getName(), fieldNextNode);
		Element categoryDataset = lineChart.addElement("categoryDataset");
		for (int i = 0; i < instanceCnt; i++) {
			String instanceId = "v" + i;
			Element categorySeries = categoryDataset.addElement("categorySeries");
			Element seriesExpression = categorySeries.addElement("seriesExpression");
			ReportModelUtil.addParameter(document, instanceId, String.class.getName());
			seriesExpression.addCDATA("$P{" + instanceId + "}");
			Element categoryExpression = categorySeries.addElement("categoryExpression");
			categoryExpression.addCDATA("$F{" + category + "}");
			Element valueExpression = categorySeries.addElement("valueExpression");
			ReportModelUtil.addField(document, instanceId, Double.class.getName(), fieldNextNode);
			valueExpression.addCDATA("$F{" + instanceId + "}");
		}
		//linePlot
		Element linePlot = lineChart.addElement("linePlot");
		//控制导出线图是否需要点
//		linePlot.addAttribute("isShowShapes", "false");
		Element plot = linePlot.addElement("plot");
		plot.addAttribute("labelRotation", "60.0");
		Element categoryAxisFormat = linePlot.addElement("categoryAxisFormat");
		categoryAxisFormat.addAttribute("labelRotation", "60.0");
		categoryAxisFormat.addElement("axisFormat");
		Element valueAxisFormat = linePlot.addElement("valueAxisFormat");
		valueAxisFormat.addElement("axisFormat");
		return lineChart;
	}
}
