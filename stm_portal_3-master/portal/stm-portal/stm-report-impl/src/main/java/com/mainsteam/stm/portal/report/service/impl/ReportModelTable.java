package com.mainsteam.stm.portal.report.service.impl;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;

public class ReportModelTable {
	private Document document = DocumentHelper.createDocument();
	private Element root;
	private String reportName;
	private int width;
	private int height;
	private int colCnt;
	private ColumnsTitle columnsTitle;
	private int rowHeight = 30;
	private int colWidth;
	private int margin = 5;
	private int excessWidth;
	public final static int tableRowHeight = 30;
	public final static int tableHeadRowCount = 4;
	private Element columnHeaderBand;
	private Element detailBand;
	private String fieldNextNode = "columnHeader";
	
	public ReportModelTable(String reportName, int width, int height, ColumnsTitle columnsTitle) {
		this.reportName = reportName;
		this.width = width;
		this.height = height;
		this.columnsTitle = columnsTitle;
	}
	/**
	 * 创建报表
	 * 
	 * @return
	 */
	public Document createReport(){
		List<Columns> columnsList = columnsTitle.getColumns();
		for (int i = 0; i < columnsList.size(); i++) {
			Columns columns = columnsList.get(i);
			if(Integer.valueOf(columns.getApart()) > 1){
				this.colCnt += Integer.valueOf(columns.getApart());
			}else{
				this.colCnt += 1;
			}
		}
		
		this.root = ReportModelUtil.createReport(document, reportName, width, height, margin);

		this.colWidth = ReportModelUtil.getRepCurWidth(document) / colCnt;
		this.excessWidth = ReportModelUtil.getRepCurWidth(document) - (colCnt * this.colWidth);
		
		Element columnHeader = this.root.addElement("columnHeader");
		this.columnHeaderBand = columnHeader.addElement("band");
		this.columnHeaderBand.addAttribute("height", String.valueOf((height - rowHeight) - (2 * margin)));
		this.columnHeaderBand.addAttribute("splitType", "Prevent");

		Element detail = this.root.addElement("detail");
		this.detailBand = detail.addElement("band");
		this.detailBand.addAttribute("height", String.valueOf(rowHeight));
		this.detailBand.addAttribute("splitType", "Stretch");
		
		addTableChartNode();
		
		return document;
	}
	public Element addTableChartNode(){
		// 标题
		createTextFile(0, 0, ReportModelUtil.getRepCurWidth(document), rowHeight, "title", "TITLE");
		// 表头
		List<Columns> columnsList = columnsTitle.getColumns();
		int thX = 0;
		for (int i = 0; i < columnsList.size(); i++) {
			Columns columns = columnsList.get(i);
			int y = rowHeight;
			int width = colWidth;
			int height = rowHeight * 2;
			String parameter = "th" + i;
			if(Integer.valueOf(columns.getApart()) > 1){
				int apart = Integer.valueOf(columns.getApart());
				height = height / 2;
				width = width * apart;
				// 如果是最后一列
				if(i == columnsList.size() - 1){
					width += excessWidth;
				}
				createTextFile(thX, y, width, height, parameter, "TH");
				for (int j = 0; j < apart; j++) {
					String childParameter = parameter + "_" + j;
					int childX = thX + (j * colWidth);
					int childY = y + height;
					int childWidth = colWidth;
					if(i == columnsList.size() - 1 && j == apart - 1){
						childWidth += excessWidth;
					}
					int childHeight = height;
					createTextFile(childX, childY, childWidth, childHeight, childParameter, "TH");
				}
			}else{
				// 如果是最后一列
				if(i == columnsList.size() - 1){
					width += excessWidth;
				}
				createTextFile(thX, y, width, height, parameter, "TH");
			}
			thX += width;
		}
		// 内容
		for (int colNo = 0; colNo < colCnt; colNo++) {
			int width = colWidth;
			// 如果是最后一列
			if(colNo == colCnt - 1){
				width += excessWidth;
			}
			int x = colNo * colWidth;
			int y = 0; // 3 * rowHeight;
			String paramter = "td" + colNo;
			createTextFile(x, y, width, rowHeight, paramter, "TD");
		}
		return null;
	}
	/**
	 * 新增一个文本框
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param paramter
	 * @return
	 */
	private Element createTextFile(int x, int y, int width, int height, String paramter, String kind){
		Element band = null;
		if(kind.equals("TITLE") || kind.equals("TH")){
			band = columnHeaderBand;
		}else{
			band = detailBand;
		}
		Element textField = band.addElement("textField");
		textField.addAttribute("isBlankWhenNull", "true");
		// 标题不加入自动扩展高度
		if(kind.equals("TD")){
			textField.addAttribute("isStretchWithOverflow", "true");
		}
		Element reportElement = textField.addElement("reportElement");
		// 标题不加入自动扩展高度
		if(kind.equals("TD")){
			reportElement.addAttribute("stretchType", "RelativeToBandHeight");
		}
		reportElement.addAttribute("x", String.valueOf(x));
		reportElement.addAttribute("y", String.valueOf(y));
		reportElement.addAttribute("width", String.valueOf(width));
		reportElement.addAttribute("height", String.valueOf(height));
		reportElement.addAttribute("uuid", ReportModelUtil.getUUID());
		if(!kind.equals("TITLE"))
			ReportModelUtil.addBorderElement(textField, 0.5);
		Element textElement = textField.addElement("textElement");
		if(!kind.equals("TITLE"))
			textElement.addAttribute("textAlignment", "Center");
		textElement.addAttribute("verticalAlignment", "Middle");
		Element font = textElement.addElement("font");
		if(kind.equals("TH")){
			font.addAttribute("isBold", "true");
		}
		font.addAttribute("pdfFontName", "STSong-Light");
		font.addAttribute("pdfEncoding", "UniGB-UCS2-H");
		Element paragraph = textElement.addElement("paragraph");
		paragraph.addAttribute("lineSpacing", "AtLeast");
		Element textFieldExpression = textField.addElement("textFieldExpression");
		if(kind.equals("TD")){
			ReportModelUtil.addField(document, paramter, String.class.getName(), fieldNextNode);
			textFieldExpression.addCDATA("$F{" + paramter + "}");
		}else{
			ReportModelUtil.addParameter(document, paramter, String.class.getName());
			textFieldExpression.addCDATA("$P{" + paramter + "}");
		}
		return textField;
	}

}
