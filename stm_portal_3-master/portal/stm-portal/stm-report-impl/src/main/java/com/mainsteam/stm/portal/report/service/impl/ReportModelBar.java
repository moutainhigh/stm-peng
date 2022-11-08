package com.mainsteam.stm.portal.report.service.impl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mainsteam.stm.portal.report.customchart.BarChartCustomizer;

public class ReportModelBar {
	private Document document = DocumentHelper.createDocument();
	private Element root;
	private String reportName;
	private int width;
	private int height;
	private int margin = 5;
	private int instanceCnt;
	private int rotationCnt = 10;
	public final static int horizontalCnt = 25;
	public final static int oneBarWidth = 30;
	private String fieldNextNode = "summary";
	private Element summaryBand;
	
	public ReportModelBar(String reportName, int width, int height, int instanceCnt) {
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
		
		addBarChartNode();
		
		return document;
	}

	/**
	 * 新增barchart
	 * $P{title}、"$P{" + instanceId + "}"、$F{category}、"$F{" + instanceId + "}"、
	 * @return
	 */
	public Element addBarChartNode(){
		
		String title = "title";
		Element bar3DChart = this.summaryBand.addElement("bar3DChart");
		ReportModelUtil.addParameter(document, title, String.class.getName());
		//chart
		Element chart = bar3DChart.addElement("chart");
		chart.addAttribute("customizerClass", BarChartCustomizer.class.getName());
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
		ReportModelUtil.addField(document, category, String.class.getName(), fieldNextNode);
		Element categoryDataset = bar3DChart.addElement("categoryDataset");
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
		//barPlot
		Element bar3DPlot = bar3DChart.addElement("bar3DPlot");
		Element plot = bar3DPlot.addElement("plot");
		bar3DPlot.addElement("itemLabel");
		Element categoryAxisFormat = bar3DPlot.addElement("categoryAxisFormat");
		categoryAxisFormat.addElement("axisFormat");
		Element valueAxisFormat = bar3DPlot.addElement("valueAxisFormat");
		valueAxisFormat.addElement("axisFormat");
		
		// 处理数据量过多显示问题
		if(instanceCnt > rotationCnt && instanceCnt <= horizontalCnt){
			plot.addAttribute("labelRotation", "60.0");
			categoryAxisFormat.addAttribute("labelRotation", "60.0");
		}else if(instanceCnt > horizontalCnt){
			plot.addAttribute("orientation", "Horizontal");
		}
		
		return bar3DChart;
	}
}
