package com.mainsteam.stm.portal.report.service.impl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ReportModelPie {
	private Document document = DocumentHelper.createDocument();
	private Element root;
	private String reportName;
	private int width;
	private int height;
	private int margin = 5;
	private String fieldNextNode = "summary";
	private Element summaryBand;

	public ReportModelPie(String reportName, int width, int height) {
		this.reportName = reportName;
		this.width = width;
		this.height = height;
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
		
		addPieChartNode();
		
		return document;
	}
	/**
	 * 新增linechart
	 * $P{title}、"$P{" + instanceId + "}"、$F{category}、"$F{" + instanceId + "}"、
	 * @return
	 */
	public Element addPieChartNode(){
		Element textField = this.summaryBand.addElement("textField");
		textField.addAttribute("isBlankWhenNull", "true");
		Element textFieldreportElement = textField.addElement("reportElement");
		textFieldreportElement.addAttribute("x", "0");
		textFieldreportElement.addAttribute("y", "0");
		textFieldreportElement.addAttribute("width", "120");
		textFieldreportElement.addAttribute("height", "60");
		textFieldreportElement.addAttribute("uuid", ReportModelUtil.getUUID());
		Element textElement = textField.addElement("textElement");
		textElement.addAttribute("textAlignment", "Center");
		textElement.addAttribute("verticalAlignment", "Middle");
		Element font = textElement.addElement("font");
		font.addAttribute("size", "24");
		font.addAttribute("isBold", "true");
		font.addAttribute("pdfFontName", "STSong-Light");
		font.addAttribute("pdfEncoding", "UniGB-UCS2-H");
		Element textFieldExpression = textField.addElement("textFieldExpression");
		ReportModelUtil.addParameter(document, "amount", String.class.getName());
		textFieldExpression.addCDATA("$P{amount}");
		
		Element pie3DChart = this.summaryBand.addElement("pie3DChart");
		String title = "title";
		ReportModelUtil.addParameter(document, title, String.class.getName());
		//chart
		Element chart = pie3DChart.addElement("chart");
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
		chartLegend.addAttribute("position", "Left");
		//pieDataset
		Element pieDataset = pie3DChart.addElement("pieDataset");
		Element keyExpression = pieDataset.addElement("keyExpression");
		ReportModelUtil.addField(document, "key", String.class.getName(), fieldNextNode);
		keyExpression.addCDATA("$F{key}");
		Element valueExpression = pieDataset.addElement("valueExpression");
		ReportModelUtil.addField(document, "value", Double.class.getName(), fieldNextNode);
		valueExpression.addCDATA("$F{value}");
		//linePlot
		Element pie3DPlot = pie3DChart.addElement("pie3DPlot");
		pie3DPlot.addAttribute("isShowLabels", "true");
		pie3DPlot.addAttribute("isCircular", "true");
		pie3DPlot.addAttribute("labelFormat", "{2} ({1})");
		pie3DPlot.addElement("plot");
		pie3DPlot.addElement("itemLabel");
		return pie3DChart;
	}
}
