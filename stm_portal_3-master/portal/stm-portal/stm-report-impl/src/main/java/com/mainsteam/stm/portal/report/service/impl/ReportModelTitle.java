package com.mainsteam.stm.portal.report.service.impl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ReportModelTitle {
	private Document document = DocumentHelper.createDocument();
	private Element root;
	private String reportName;
	private int width;
	private int height;
	private int margin = 0;
	private Element detailBand;

	public ReportModelTitle(String reportName, int width, int height) {
		this.reportName = reportName;
		this.width = width;
		this.height = height;
	}

	public Document createReport() {
		this.root = ReportModelUtil.createReport(document, reportName, width, height, margin);
		
		Element detail = this.root.addElement("detail");
		this.detailBand = detail.addElement("band");
		this.detailBand.addAttribute("height", String.valueOf(height - 2 * margin));
		this.detailBand.addAttribute("splitType", "Prevent");
		
		addTextNode();
		
		return document;
	}

	public Element addTextNode() {
		Element textField = this.detailBand.addElement("textField");
		textField.addAttribute("isBlankWhenNull", "true");
		Element reportElement = textField.addElement("reportElement");
		reportElement.addAttribute("x", "0");
		reportElement.addAttribute("y", "0");
		reportElement.addAttribute("width", String.valueOf(ReportModelUtil.getRepCurWidth(document)));
		reportElement.addAttribute("height", this.detailBand.attributeValue("height"));
		reportElement.addAttribute("uuid", ReportModelUtil.getUUID());
		Element textElement = textField.addElement("textElement");
		textElement.addAttribute("textAlignment", "Left");
		textElement.addAttribute("verticalAlignment", "Middle");
		Element font = textElement.addElement("font");
		font.addAttribute("size", "12");
		font.addAttribute("isBold", "true");
		font.addAttribute("pdfFontName", "STSong-Light");
		font.addAttribute("pdfEncoding", "UniGB-UCS2-H");
		Element textFieldExpression = textField.addElement("textFieldExpression");
		ReportModelUtil.addParameter(document, "title", String.class.getName());
		textFieldExpression.addCDATA("$P{title}");
		return textField;
	}
}
