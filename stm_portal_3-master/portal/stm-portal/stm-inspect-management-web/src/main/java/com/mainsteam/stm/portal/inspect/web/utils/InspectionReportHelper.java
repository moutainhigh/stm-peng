package com.mainsteam.stm.portal.inspect.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import com.mainsteam.stm.portal.inspect.web.utils.entity.Body;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Conclusion;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Head;
import com.mainsteam.stm.portal.inspect.web.utils.entity.InspectionReport;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Item;
import com.mainsteam.stm.util.StringUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class InspectionReportHelper {

	public byte[] excel(InspectionReport report) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("巡检报告");
		int rowIndex = 0;
		Row row = sheet.createRow(rowIndex++);
		int clumnSize = 10;
		Cell cell = row.createCell(0);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, clumnSize));
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setBorderTop(CellStyle.SOLID_FOREGROUND);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.index);
		cellStyle.setBorderBottom(CellStyle.SOLID_FOREGROUND);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
		cellStyle.setBorderLeft(CellStyle.SOLID_FOREGROUND);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
		cellStyle.setBorderRight(CellStyle.SOLID_FOREGROUND);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.index);
		cellStyle.setFont(font);

		CellStyle cellStyleBasic = workbook.createCellStyle();
		cellStyleBasic.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Font fontBasic = workbook.createFont();
		fontBasic.setFontName("宋体");
		fontBasic.setFontHeightInPoints((short) 10);
		cellStyleBasic.setBorderTop(CellStyle.SOLID_FOREGROUND);
		cellStyleBasic.setTopBorderColor(IndexedColors.BLACK.index);
		cellStyleBasic.setBorderBottom(CellStyle.SOLID_FOREGROUND);
		cellStyleBasic.setBottomBorderColor(IndexedColors.BLACK.index);
		cellStyleBasic.setBorderLeft(CellStyle.SOLID_FOREGROUND);
		cellStyleBasic.setLeftBorderColor(IndexedColors.BLACK.index);
		cellStyleBasic.setBorderRight(CellStyle.SOLID_FOREGROUND);
		cellStyleBasic.setRightBorderColor(IndexedColors.BLACK.index);
		cellStyleBasic.setFont(fontBasic);

		CellStyle cellStyleBasicRed = workbook.createCellStyle();
		cellStyleBasicRed.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Font fontBasicRed = workbook.createFont();
		fontBasicRed.setFontName("宋体");
		fontBasicRed.setFontHeightInPoints((short) 10);
		fontBasicRed.setColor(IndexedColors.RED.index);
		cellStyleBasicRed.setBorderTop(CellStyle.SOLID_FOREGROUND);
		cellStyleBasicRed.setTopBorderColor(IndexedColors.BLACK.index);
		cellStyleBasicRed.setBorderBottom(CellStyle.SOLID_FOREGROUND);
		cellStyleBasicRed.setBottomBorderColor(IndexedColors.BLACK.index);
		cellStyleBasicRed.setBorderLeft(CellStyle.SOLID_FOREGROUND);
		cellStyleBasicRed.setLeftBorderColor(IndexedColors.BLACK.index);
		cellStyleBasicRed.setBorderRight(CellStyle.SOLID_FOREGROUND);
		cellStyleBasicRed.setRightBorderColor(IndexedColors.BLACK.index);
		cellStyleBasicRed.setFont(fontBasicRed);

		CellStyle cellStyleConclusion = workbook.createCellStyle();
		cellStyleConclusion.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		Font fontConclusion = workbook.createFont();
		fontConclusion.setFontName("宋体");
		fontConclusion.setFontHeightInPoints((short) 10);
		cellStyleConclusion.setBorderTop(CellStyle.SOLID_FOREGROUND);
		cellStyleConclusion.setTopBorderColor(IndexedColors.BLACK.index);
		cellStyleConclusion.setBorderBottom(CellStyle.SOLID_FOREGROUND);
		cellStyleConclusion.setBottomBorderColor(IndexedColors.BLACK.index);
		cellStyleConclusion.setBorderLeft(CellStyle.SOLID_FOREGROUND);
		cellStyleConclusion.setLeftBorderColor(IndexedColors.BLACK.index);
		cellStyleConclusion.setBorderRight(CellStyle.SOLID_FOREGROUND);
		cellStyleConclusion.setRightBorderColor(IndexedColors.BLACK.index);
		cellStyleConclusion.setFont(fontConclusion);

		CellStyle cellStyleYellow = workbook.createCellStyle();
		cellStyleYellow.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleYellow.setFillForegroundColor(IndexedColors.YELLOW.index);
		cellStyleYellow.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyleYellow.setBorderTop(CellStyle.SOLID_FOREGROUND);
		cellStyleYellow.setTopBorderColor(IndexedColors.BLACK.index);
		cellStyleYellow.setBorderBottom(CellStyle.SOLID_FOREGROUND);
		cellStyleYellow.setBottomBorderColor(IndexedColors.BLACK.index);
		cellStyleYellow.setBorderLeft(CellStyle.SOLID_FOREGROUND);
		cellStyleYellow.setLeftBorderColor(IndexedColors.BLACK.index);
		cellStyleYellow.setBorderRight(CellStyle.SOLID_FOREGROUND);
		cellStyleYellow.setRightBorderColor(IndexedColors.BLACK.index);
		cellStyleBasic.setFont(fontBasic);

		CellStyle cellStyleBlue = workbook.createCellStyle();
		cellStyleBlue.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleBlue.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
		cellStyleBlue.setFillBackgroundColor(IndexedColors.ROYAL_BLUE.index);
		cellStyleBlue.setFillPattern(CellStyle.BIG_SPOTS);
		Font fontBlue = workbook.createFont();
		fontBlue.setFontName("宋体");
		fontBlue.setFontHeightInPoints((short) 10);
		fontBlue.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyleBlue.setBorderTop(CellStyle.SOLID_FOREGROUND);
		cellStyleBlue.setTopBorderColor(IndexedColors.BLACK.index);
		cellStyleBlue.setBorderBottom(CellStyle.SOLID_FOREGROUND);
		cellStyleBlue.setBottomBorderColor(IndexedColors.BLACK.index);
		cellStyleBlue.setBorderLeft(CellStyle.SOLID_FOREGROUND);
		cellStyleBlue.setLeftBorderColor(IndexedColors.BLACK.index);
		cellStyleBlue.setBorderRight(CellStyle.SOLID_FOREGROUND);
		cellStyleBlue.setRightBorderColor(IndexedColors.BLACK.index);
		cellStyleBlue.setFont(fontBlue);

		cell.setCellStyle(cellStyle);
		cell.setCellValue(report.getName());
		for (int i = 2; i < clumnSize + 1; i++) {
			row.createCell(i).setCellStyle(cellStyle);
		}
		if (report.getHeads() != null && report.getHeads().size() > 0) {
			int clumnIndex = 0;
			for (int i = 0; i < report.getHeads().size(); i++) {
				if (i % 4 == 0) {
					if (i != 0) {
						for (int j = clumnIndex; j < clumnSize; j++) {
							row.createCell(j).setCellStyle(cellStyleBasic);
						}
					}
					row = sheet.createRow(rowIndex++);
					clumnIndex = 0;
				}
				Head h = report.getHeads().get(i);
				cell = row.createCell(clumnIndex++);
				cell.setCellStyle(cellStyleBasic);
				cell.setCellValue(h.getName());
				cell = row.createCell(clumnIndex++);
				cell.setCellStyle(cellStyleBasic);
				cell.setCellValue(h.getValue());
			}
			for (int j = clumnIndex; j < clumnSize; j++) {
				row.createCell(j).setCellStyle(cellStyleBasic);
			}
		}
		if (report.getBodys() != null && report.getBodys().size() > 0) {
			for (int i = 0; i < report.getBodys().size(); i++) {
				Body b = report.getBodys().get(i);
				int rowNow = rowIndex;
				row = sheet.createRow(rowIndex++);
				sheet.addMergedRegion(new CellRangeAddress(rowNow, rowNow, 0,
						clumnSize));
				cell = row.createCell(0);
				cell.setCellStyle(cellStyleYellow);
				cell.setCellValue((i + 1) + ". " + b.getName());
				for (int j = 1; j < clumnSize + 1; j++) {
					row.createCell(j).setCellStyle(cellStyle);
				}
				int[] indexs = { 0, 3, 5, 6, 7, 8, 10};
				if (b.getTitles() != null && b.getTitles().size() > 0) {
					sheet.addMergedRegion(new CellRangeAddress(rowIndex,
							rowIndex, 0, 2));
					sheet.addMergedRegion(new CellRangeAddress(rowIndex,
							rowIndex, 3, 4));
					sheet.addMergedRegion(new CellRangeAddress(rowIndex,
							rowIndex, 8, 9));
					row = sheet.createRow(rowIndex++);
					for (int j = 0; j < b.getTitles().size(); j++) {
						cell = row.createCell(indexs[j]);
						cell.setCellStyle(cellStyleBlue);
						cell.setCellValue(b.getTitles().get(j));
					}
					for (int j = 0; j < indexs[indexs.length - 1]; j++) {
						if (row.getCell(j) == null) {
							row.createCell(j).setCellStyle(cellStyleBlue);
						}
					}
				}
				if (b.getItems() != null) {
					for (Item it : b.getItems()) {
						sheet.addMergedRegion(new CellRangeAddress(rowIndex,
								rowIndex, 0, 2));
						sheet.addMergedRegion(new CellRangeAddress(rowIndex,
								rowIndex, 3, 4));
						sheet.addMergedRegion(new CellRangeAddress(rowIndex,
								rowIndex, 8, 9));
						row = sheet.createRow(rowIndex++);
						int itIndex = 0;
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle(cellStyleBasic);
						cell.setCellValue(it.getName());
						
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle(cellStyleBasic);
						cell.setCellValue(it.getDescription());
						
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle(cellStyleBasic);
						cell.setCellValue(it.getReferenceValue());
						
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle(cellStyleBasic);
						cell.setCellValue(it.getValue());
						
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle(cellStyleBasic);
						cell.setCellValue(it.getCheckingType());
						
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle(cellStyleBasic);
						cell.setCellValue(it.getSummary());
						
						cell = row.createCell(indexs[itIndex++]);
						cell.setCellStyle("异常".equals(it.getStatus()) ? cellStyleBasicRed
								: cellStyleBasic);
						cell.setCellValue(it.getStatus());
						
						for (int j = 0; j < indexs[indexs.length - 1]; j++) {
							if (row.getCell(j) == null) {
								row.createCell(j).setCellStyle(cellStyleBlue);
							}
						}
						if (it.getItems() != null) {
							sheet.addMergedRegion(new CellRangeAddress(
									rowIndex, rowIndex + it.getItems().size()
											- 1, 0, 0));
							for (Item itc : it.getItems()) {
                                sheet.addMergedRegion(new CellRangeAddress(
                                        rowIndex, rowIndex, 1, 2));
								sheet.addMergedRegion(new CellRangeAddress(
										rowIndex, rowIndex, 3, 4));
								sheet.addMergedRegion(new CellRangeAddress(
										rowIndex, rowIndex, 8, 9));
								row = sheet.createRow(rowIndex++);
								cell = row.createCell(0);
								int itcIndex = 0;
								cell = row.createCell(indexs[itcIndex++] + 1);
								cell.setCellStyle(cellStyleBasic);
								cell.setCellValue(itc.getName());
								
								cell = row.createCell(indexs[itcIndex++]);
								cell.setCellStyle(cellStyleBasic);
								cell.setCellValue(itc.getDescription());
								
								cell = row.createCell(indexs[itcIndex++]);
								cell.setCellStyle(cellStyleBasic);
								cell.setCellValue(itc.getReferenceValue());
								
								cell = row.createCell(indexs[itcIndex++]);
								cell.setCellStyle(cellStyleBasic);
								cell.setCellValue(itc.getValue());
								
								cell = row.createCell(indexs[itcIndex++]);
								cell.setCellStyle(cellStyleBasic);
								cell.setCellValue(itc.getCheckingType());
								
								cell = row.createCell(indexs[itcIndex++]);
								cell.setCellStyle(cellStyleBasic);
								cell.setCellValue(itc.getSummary());
								
								cell = row.createCell(indexs[itcIndex++]);
								cell.setCellStyle("异常".equals(itc.getStatus()) ? cellStyleBasicRed
										: cellStyleBasic);
								cell.setCellValue(itc.getStatus());
								for (int j = 0; j < indexs[indexs.length - 1]; j++) {
									if (row.getCell(j) == null) {
										row.createCell(j).setCellStyle(
												cellStyleBlue);
									}
								}
                                if (itc.getItems() != null) {
                                    sheet.addMergedRegion(new CellRangeAddress(
                                            rowIndex, rowIndex + itc.getItems().size()
                                            - 1, 0, 1));
                                    for (Item itcc : itc.getItems()) {
                                        sheet.addMergedRegion(new CellRangeAddress(
                                                rowIndex, rowIndex, 3, 4));
                                        sheet.addMergedRegion(new CellRangeAddress(
                                                rowIndex, rowIndex, 8, 9));
                                        row = sheet.createRow(rowIndex++);
                                        cell = row.createCell(0);
                                        int itccIndex = 0;
                                        cell = row.createCell(indexs[itccIndex++] + 2);
                                        cell.setCellStyle(cellStyleBasic);
                                        cell.setCellValue(itcc.getName());

                                        cell = row.createCell(indexs[itccIndex++]);
                                        cell.setCellStyle(cellStyleBasic);
                                        cell.setCellValue(itcc.getDescription());

                                        cell = row.createCell(indexs[itccIndex++]);
                                        cell.setCellStyle(cellStyleBasic);
                                        cell.setCellValue(itcc.getReferenceValue());

                                        cell = row.createCell(indexs[itccIndex++]);
                                        cell.setCellStyle(cellStyleBasic);
                                        cell.setCellValue(itcc.getValue());

                                        cell = row.createCell(indexs[itccIndex++]);
                                        cell.setCellStyle(cellStyleBasic);
                                        cell.setCellValue(itcc.getCheckingType());

                                        cell = row.createCell(indexs[itccIndex++]);
                                        cell.setCellStyle(cellStyleBasic);
                                        cell.setCellValue(itcc.getSummary());

                                        cell = row.createCell(indexs[itccIndex++]);
                                        cell.setCellStyle("异常".equals(itcc.getStatus()) ? cellStyleBasicRed
                                                : cellStyleBasic);
                                        cell.setCellValue(itcc.getStatus());
                                        for (int j = 0; j < indexs[indexs.length - 1]; j++) {
                                            if (row.getCell(j) == null) {
                                                row.createCell(j).setCellStyle(
                                                        cellStyleBlue);
                                            }
                                        }
                                    }
                                }
							}
						}
					}
				}
				sheet.createRow(rowIndex++);
			}
		} else {
			row = sheet.createRow(rowIndex++);
		}

		if (report.getConclusions() != null) {
			int rowNow = rowIndex;
			row = sheet.createRow(rowIndex++);
			sheet.addMergedRegion(new CellRangeAddress(rowNow, rowNow, 0,
					clumnSize));
			cell = row.createCell(0);
			cell.setCellStyle(cellStyleYellow);
			String h = (report.getBodys() != null ? report.getBodys().size() + 1
					: 1)
					+ "";
			cell.setCellValue(h + ". 结论");
			for (int j = 1; j < clumnSize + 1; j++) {
				row.createCell(j).setCellStyle(cellStyleYellow);
			}
			int hh = 1;
			for (Conclusion c : report.getConclusions()) {
				if (!StringUtil.isNull(c.getName())) {
					rowNow = rowIndex;
					row = sheet.createRow(rowIndex++);
					sheet.addMergedRegion(new CellRangeAddress(rowNow, rowNow,
							0, clumnSize));
					cell = row.createCell(0);
					cell.setCellStyle(cellStyleBasic);
					cell.setCellValue(h + "." + (hh++) + " " + c.getName());
					for (int j = 1; j < clumnSize + 1; j++) {
						row.createCell(j).setCellStyle(cellStyleBasic);
					}
				}
				rowNow = rowIndex;
				row = sheet.createRow(rowIndex++);
				row.setHeightInPoints((short) 40);
				sheet.addMergedRegion(new CellRangeAddress(rowNow, rowNow, 0,
						clumnSize));
				cell = row.createCell(0);
				cell.setCellStyle(cellStyleConclusion);
				cell.setCellValue(c.getValue());
				for (int j = 1; j < clumnSize + 1; j++) {
					row.createCell(j).setCellStyle(cellStyleConclusion);
				}
				row = sheet.createRow(rowIndex++);
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		out.flush();
		byte[] data = out.toByteArray();
		out.close();
		return data;
	}

	public byte[] word(InspectionReport report) throws IOException {
		int width = 8524;
		int fontSizeBasic = 10;
		String fontFamily = "宋体";
		XWPFDocument doc = new XWPFDocument();
		XWPFParagraph titleParagraph = doc.createParagraph();
		titleParagraph.setAlignment(ParagraphAlignment.CENTER);
		titleParagraph.setVerticalAlignment(TextAlignment.TOP);
		XWPFRun titleRun = titleParagraph.createRun();
		titleRun.setBold(true);
		titleRun.setFontSize(12);
		titleRun.setFontFamily(fontFamily);
		titleRun.setText(report.getName());

		if (report.getHeads() != null && report.getHeads().size() > 0) {
			int rowSize = report.getHeads().size();
			if (rowSize % 2 != 0) {
				rowSize = (rowSize / 2) + 1;
			} else {
				rowSize = (rowSize / 2);
			}
			XWPFTable table = doc.createTable(rowSize, 4);
			CTTblPr ctTblPr = table.getCTTbl().getTblPr();
			ctTblPr.addNewTblLayout().setType(STTblLayoutType.FIXED);
			ctTblPr.getTblW().setW(BigInteger.valueOf(width));
			ctTblPr.getTblW().setType(STTblWidth.DXA);
			int cellWidth = width / 4;
			for (int i = 0, j = 0; i < rowSize; i++) {
				List<XWPFTableCell> cells = table.getRow(i).getTableCells();
				Head h = report.getHeads().get(j++);
				XWPFTableCell cell = cells.get(0);
				CTTblWidth ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
				ctTblWidth.setW(BigInteger.valueOf(cellWidth));
				ctTblWidth.setType(STTblWidth.DXA);
				XWPFRun run = cell.getParagraphs().get(0).createRun();
				run.setFontSize(fontSizeBasic);
				run.setFontFamily(fontFamily);
				run.setText(h.getName());

				cell = cells.get(1);
				ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
				ctTblWidth.setW(BigInteger.valueOf(cellWidth));
				ctTblWidth.setType(STTblWidth.DXA);
				run = cell.getParagraphs().get(0).createRun();
				run.setFontSize(fontSizeBasic);
				run.setFontFamily(fontFamily);
				run.setText(h.getValue());

				if (j < report.getHeads().size()) {
					Head h2 = report.getHeads().get(j++);
					cell = cells.get(2);
					ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
					ctTblWidth.setW(BigInteger.valueOf(cellWidth));
					ctTblWidth.setType(STTblWidth.DXA);
					run = cell.getParagraphs().get(0).createRun();
					run.setFontSize(fontSizeBasic);
					run.setFontFamily(fontFamily);
					run.setText(h2.getName());

					cell = cells.get(3);
					ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
					ctTblWidth.setW(BigInteger.valueOf(cellWidth));
					ctTblWidth.setType(STTblWidth.DXA);
					run = cell.getParagraphs().get(0).createRun();
					run.setFontSize(fontSizeBasic);
					run.setFontFamily(fontFamily);
					run.setText(h2.getValue());
				} else {
					cell = cells.get(2);
					ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
					ctTblWidth.setW(BigInteger.valueOf(cellWidth));
					ctTblWidth.setType(STTblWidth.DXA);
					run = cell.getParagraphs().get(0).createRun();
					run.setFontSize(fontSizeBasic);
					run.setFontFamily(fontFamily);
					cell = cells.get(3);
					ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
					ctTblWidth.setW(BigInteger.valueOf(cellWidth));
					ctTblWidth.setType(STTblWidth.DXA);
					run = cell.getParagraphs().get(0).createRun();
					run.setFontSize(fontSizeBasic);
					run.setFontFamily(fontFamily);
				}
			}
		}
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.setFontSize(10);
		run.setFontFamily(fontFamily);
		run.setText("");
		int index = 1;
		XWPFTable table;
		if (report.getBodys() != null && report.getBodys().size() > 0) {
			for (Body b : report.getBodys()) {
				paragraph = doc.createParagraph();
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily(fontFamily);
				run.setText(index++ + ". " + b.getName());
				int cellSize = 8;
				table = doc.createTable(0, cellSize);
				table.setWidth(width);
				CTTblPr ctTblPr = table.getCTTbl().getTblPr();
				ctTblPr.addNewTblLayout().setType(STTblLayoutType.FIXED);
				ctTblPr.getTblW().setW(BigInteger.valueOf(width));
				ctTblPr.getTblW().setType(STTblWidth.DXA);
				int[] cellWidths = { 200, 1220, 1420, 1420, 1420, 1420, 1420, 1420};
				CTTblGrid ctTblGrid = table.getCTTbl().addNewTblGrid();
				for (int i = 0; i < cellWidths.length; i++) {
					ctTblGrid.addNewGridCol().setW(
							BigInteger.valueOf(cellWidths[i]));
				}
				//设置表格的头
				if (b.getTitles() != null && b.getTitles().size() > 0) {
					XWPFTableRow row = table.getRow(0);
					for (int i = 0; i < cellSize; i++) {
						XWPFTableCell cell = null;
						if (i != 0) {
							cell = row.createCell();
						} else {
							cell = row.getCell(i);
						}
						CTTcPr ctTcPr = cell.getCTTc().addNewTcPr();
						CTShd ctShd = ctTcPr.addNewShd();
						ctShd.setFill("0070c0");//背景颜色为蓝色
						CTTblWidth ctTblWidth = ctTcPr.addNewTcW();
						ctTblWidth.setW(BigInteger.valueOf(cellWidths[i]));
						ctTblWidth.setType(STTblWidth.DXA);
						cell.setColor("0070c0");
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setBold(true);
						if (i == 1) {
							continue;
						}
						String title = b.getTitles().get(i == 0 ? 0 : i - 1);
						run.setText(title);
					}
					mergeCellsHorizontal(table, 0, 0, 1);
					table.getRow(0).getCell(0).getCTTc().getTcPr().getTcW()
							.setW(BigInteger.valueOf(cellWidths[3]));
				}
				//设置具体的值
				if (b.getItems() != null && b.getItems().size() > 0) {
					for (int i = 0; i < b.getItems().size(); i++) {
						Item it = b.getItems().get(i);
						XWPFTableRow row = table.createRow();
						int indexIt = 0;
						XWPFTableCell cell = row.getCell(indexIt);
						CTTblWidth ctTblWidth = cell.getCTTc().addNewTcPr()
								.addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setText(it.getName());//设置巡检项

						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						
						//设置描述信息
						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setText(it.getDescription());
						
						//设置参照值
						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setText(it.getReferenceValue());
						
						//设置巡检值
						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setText(it.getValue());
						
						//设置巡检类型
						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setText(it.getCheckingType());
						
						//设置情况概要
						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						run.setText(it.getSummary());
						
						//设置巡检结果
						cell = row.getCell(indexIt);
						ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
						ctTblWidth.setW(BigInteger
								.valueOf(cellWidths[indexIt++]));
						ctTblWidth.setType(STTblWidth.DXA);
						run = cell.getParagraphs().get(0).createRun();
						run.setFontSize(fontSizeBasic);
						run.setFontFamily(fontFamily);
						if ("异常".equals(it.getStatus())) {
							run.setColor("ff0000");
						}
						run.setText(it.getStatus());
						mergeCellsHorizontal(table, row, 0, 1);
						if (it.getItems() != null && it.getItems().size() > 0) {
							for (Item itc : it.getItems()) {
								row = table.createRow();
								indexIt = 0;

								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);

								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								// clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								run.setText(itc.getName());

								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								// clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								run.setText(itc.getDescription());

								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								// clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								run.setText(itc.getReferenceValue());

								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								// clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								run.setText(itc.getValue());
								
								//巡检类型
								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								run.setText(itc.getCheckingType());
								
								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								// clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								run.setText(itc.getSummary());

								cell = row.getCell(indexIt);
								ctTblWidth = cell.getCTTc().addNewTcPr()
										.addNewTcW();
								ctTblWidth.setW(BigInteger
										.valueOf(cellWidths[indexIt++]));
								ctTblWidth.setType(STTblWidth.DXA);
								// clearBorders(cell);
								run = cell.getParagraphs().get(0).createRun();
								run.setFontSize(fontSizeBasic);
								run.setFontFamily(fontFamily);
								if ("异常".equals(itc.getStatus())) {
									run.setColor("ff0000");
								}
								run.setText(itc.getStatus());
                                if (itc.getItems() != null && itc.getItems().size() > 0) {
                                    for (Item itcc : itc.getItems()) {
                                        row = table.createRow();
                                        indexIt = 0;

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        // clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        run.setText(itcc.getName());

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        // clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        run.setText(itcc.getDescription());

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        // clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        run.setText(itcc.getReferenceValue());

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        // clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        run.setText(itcc.getValue());

                                        //巡检类型
                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        run.setText(itcc.getCheckingType());

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        // clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        run.setText(itcc.getSummary());

                                        cell = row.getCell(indexIt);
                                        ctTblWidth = cell.getCTTc().addNewTcPr()
                                                .addNewTcW();
                                        ctTblWidth.setW(BigInteger
                                                .valueOf(cellWidths[indexIt++]));
                                        ctTblWidth.setType(STTblWidth.DXA);
                                        // clearBorders(cell);
                                        run = cell.getParagraphs().get(0).createRun();
                                        run.setFontSize(fontSizeBasic);
                                        run.setFontFamily(fontFamily);
                                        if ("异常".equals(itcc.getStatus())) {
                                            run.setColor("ff0000");
                                        }
                                        run.setText(itcc.getStatus());
                                    }
                                    int endRow = table.getRows().size() - 1;
                                    int startRow = endRow - it.getItems().size() + 1;
                                    if (startRow < endRow) {
                                        this.mergeCellsVertically(table, 0, startRow,
                                                endRow);
                                    }
                                }
							}
							int endRow = table.getRows().size() - 1;
							int startRow = endRow - it.getItems().size() + 1;
							if (startRow < endRow) {
								this.mergeCellsVertically(table, 0, startRow,
										endRow);
							}
						}
						if (i != b.getItems().size() - 1) {
							row = table.createRow();
							for (int j = 0; j < cellSize; j++) {
								clearBorders(row.getCell(j));
							}
							this.mergeCellsHorizontal(table, row, 0,
									cellSize - 1);
						}
					}
				}
				paragraph = doc.createParagraph();
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily(fontFamily);
				run.setText("");
			}
		} else {
			paragraph = doc.createParagraph();
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setFontFamily(fontFamily);
			run.setText("");
		}
		int ConclusionHeight = 700;
		if (report.getConclusions() != null
				&& report.getConclusions().size() > 0) {
			run = doc.createParagraph().createRun();
			run.setFontSize(10);
			run.setFontFamily(fontFamily);
			run.setText(index + ". 结论");
			int indexConclusion = 1;
			for (Conclusion c : report.getConclusions()) {
				if (c.getName() != null && !"".equals(c.getName().trim())) {
					paragraph = doc.createParagraph();
					run = paragraph.createRun();
					run.setFontSize(10);
					run.setFontFamily(fontFamily);
					run.setText(index + "." + indexConclusion++ + " "
							+ c.getName());
				}
				table = doc.createTable(1, 1);
				table.getCTTbl().getTblPr().addNewTblLayout()
						.setType(STTblLayoutType.FIXED);
				table.getCTTbl().getTblPr().getTblW()
						.setW(BigInteger.valueOf(width));
				table.getCTTbl().getTblPr().getTblW().setType(STTblWidth.DXA);
				XWPFTableRow row = table.getRow(0);
				XWPFTableCell cell = row.getCell(0);
				CTTblWidth ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
				ctTblWidth.setW(BigInteger.valueOf(width));
				ctTblWidth.setType(STTblWidth.DXA);
				row.getCtRow().addNewTrPr().addNewTrHeight()
						.setVal(BigInteger.valueOf(ConclusionHeight));
				run = cell.getParagraphs().get(0).createRun();
				run.setFontSize(fontSizeBasic);
				run.setFontFamily(fontFamily);
				run.setText(c.getValue());

				paragraph = doc.createParagraph();
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setFontFamily(fontFamily);
				run.setText("");
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		doc.write(out);
		out.flush();
		byte[] data = out.toByteArray();
		out.close();
		return data;
	}

	private void clearBorders(XWPFTableCell cell) {
		String color = "ffffff";
		int sz = 0;
		int space = 0;
		STBorder.Enum e = null;
		CTTcPr ctTcPr = cell.getCTTc().getTcPr();
		if (ctTcPr == null) {
			ctTcPr = cell.getCTTc().addNewTcPr();
		}
		CTTcBorders borders = ctTcPr.getTcBorders();
		if (borders == null) {
			borders = ctTcPr.addNewTcBorders();
		}

		CTBorder hBorder = borders.getInsideH();
		if (hBorder == null) {
			hBorder = borders.addNewInsideH();
		}
		hBorder.setVal(e);
		hBorder.setSz(BigInteger.valueOf(sz));
		hBorder.setColor(color);
		CTBorder vBorder = borders.getInsideV();
		if (vBorder == null) {
			vBorder = borders.addNewInsideV();
		}
		vBorder.setVal(e);
		vBorder.setSz(BigInteger.valueOf(sz));
		vBorder.setColor(color);
		vBorder.setSpace(BigInteger.valueOf(space));

		CTBorder lBorder = borders.getLeft();
		if (lBorder == null) {
			lBorder = borders.addNewLeft();
		}
		lBorder.setVal(e);
		lBorder.setSz(BigInteger.valueOf(sz));
		lBorder.setColor(color);
		lBorder.setSpace(BigInteger.valueOf(space));

		CTBorder rBorder = borders.getRight();
		if (rBorder == null) {
			rBorder = borders.addNewRight();
		}
		rBorder.setVal(e);
		rBorder.setSz(BigInteger.valueOf(sz));
		rBorder.setColor(color);
		rBorder.setSpace(BigInteger.valueOf(space));

		CTBorder tBorder = borders.getTop();
		if (tBorder == null) {
			tBorder = borders.addNewTop();
		}
		tBorder.setVal(e);
		tBorder.setSz(BigInteger.valueOf(sz));
		tBorder.setColor(color);
		tBorder.setSpace(BigInteger.valueOf(space));

		CTBorder bBorder = borders.getBottom();
		if (bBorder == null) {
			bBorder = borders.addNewBottom();
		}
		bBorder.setVal(e);
		bBorder.setSz(BigInteger.valueOf(sz));
		bBorder.setColor(color);
		bBorder.setSpace(BigInteger.valueOf(space));

		CTBorder brBorder = borders.getTl2Br();
		if (brBorder == null) {
			brBorder = borders.addNewTl2Br();
		}
		bBorder.setVal(e);
		brBorder.setSz(BigInteger.valueOf(sz));
		brBorder.setColor(color);
		brBorder.setSpace(BigInteger.valueOf(space));

		CTBorder blBorder = borders.getTr2Bl();
		if (blBorder == null) {
			blBorder = borders.addNewTr2Bl();
		}
		bBorder.setVal(e);
		blBorder.setSz(BigInteger.valueOf(sz));
		blBorder.setColor(color);
		blBorder.setSpace(BigInteger.valueOf(space));
	}

	private void mergeCellsHorizontal(XWPFTable table, int row, int fromCell,
			int toCell) {
		for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
			XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
			CTTcPr ctTcPr = cell.getCTTc().getTcPr();
			if (ctTcPr == null) {
				ctTcPr = cell.getCTTc().addNewTcPr();
			}
			CTHMerge cthMerge = ctTcPr.getHMerge();
			if (cthMerge == null) {
				cthMerge = ctTcPr.addNewHMerge();
			}
			if (cellIndex == fromCell) {
				cthMerge.setVal(STMerge.RESTART);
			} else {
				cthMerge.setVal(STMerge.CONTINUE);
			}
		}
	}

	private void mergeCellsHorizontal(XWPFTable table, XWPFTableRow row,
			int fromCell, int toCell) {
		for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
			XWPFTableCell cell = row.getCell(cellIndex);
			CTTcPr ctTcPr = cell.getCTTc().getTcPr();
			if (ctTcPr == null) {
				ctTcPr = cell.getCTTc().addNewTcPr();
			}
			CTHMerge cthMerge = ctTcPr.getHMerge();
			if (cthMerge == null) {
				cthMerge = ctTcPr.addNewHMerge();
			}
			if (cellIndex == fromCell) {
				cthMerge.setVal(STMerge.RESTART);
			} else {
				cthMerge.setVal(STMerge.CONTINUE);
			}
		}
	}

	public void mergeCellsVertically(XWPFTable table, int col, int fromRow,
			int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			CTTcPr ctTcPr = cell.getCTTc().getTcPr();
			if (ctTcPr == null) {
				ctTcPr = cell.getCTTc().addNewTcPr();
			}
			CTVMerge ctvMerge = ctTcPr.getVMerge();
			if (ctvMerge == null) {
				ctvMerge = ctTcPr.addNewVMerge();
			}
			if (rowIndex == fromRow) {
				ctvMerge.setVal(STMerge.RESTART);
			} else {
				ctvMerge.setVal(STMerge.CONTINUE);
			}
		}
	}

	public byte[] pdf(InspectionReport report) throws IOException,
			DocumentException {
		//1.新建document对象
		//第一个参数是页面大小，接下来的参数分别是左、右、上和下的页边距
		Document doc = new Document(PageSize.A4, 80, 80, 80, 80);
		//2.建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		//
		PdfWriter.getInstance(doc, out);
		//3.打开文档
		doc.open();
//		BaseFont baseFont = BaseFont.createFont("STSongStd-Light",
//				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		BaseFont baseFont = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		com.itextpdf.text.Font font = new com.itextpdf.text.Font(baseFont);
		font.setStyle(com.itextpdf.text.Font.BOLD);
		font.setSize(12);
		//4.通过Paragraph来添加文本。
		Paragraph paragraph = new Paragraph(report.getName(), font);
		paragraph.setAlignment(Element.ALIGN_CENTER);
		doc.add(paragraph);

		PdfPTable table;
		BaseColor borderColor = new BaseColor(91, 91, 91);
		BaseColor borderContext = new BaseColor(100, 100, 100);
		if (report.getHeads() != null && report.getHeads().size() > 0) {
			Paragraph p = new Paragraph();
			p.setSpacingBefore(3);
			doc.add(p);
			int headColumn = 4;
			table = new PdfPTable(headColumn);
			table.setWidthPercentage(100);
			font = new com.itextpdf.text.Font(baseFont);
			font.setSize(10);
			font.setColor(borderContext);
			for (Head h : report.getHeads()) {
				paragraph = new Paragraph(h.getName(), font);
				PdfPCell cell = new PdfPCell();
				cell.addElement(paragraph);
				cell.setUseAscender(true);
				cell.setUseDescender(true);
				cell.setPaddingLeft(5);
				cell.setBorderColor(borderColor);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);

				paragraph = new Paragraph(h.getValue(), font);
				cell = new PdfPCell();
				cell.addElement(paragraph);
				cell.setUseAscender(true);
				cell.setUseDescender(true);
				cell.setPaddingLeft(5);
				cell.setBorderColor(borderColor);
				table.addCell(cell);
			}
			int length = (report.getHeads().size() * 2) % headColumn;
			for (int i = 0; i < length; i++) {
				table.addCell("");
			}
			doc.add(table);
		}

		int index = 1;
		if (report.getBodys() != null && report.getBodys().size() > 0) {
			for (Body b : report.getBodys()) {
				Paragraph p = new Paragraph();
				p.setSpacingBefore(10);
				doc.add(p);
				font = new com.itextpdf.text.Font(baseFont);
				font.setSize(10);
				font.setColor(borderContext);
				paragraph = new Paragraph("  " + index++ + ".  " + b.getName(),
						font);
				doc.add(paragraph);

				p = new Paragraph();
				p.setSpacingBefore(3);
				doc.add(p);

				table = new PdfPTable(8);
				table.setWidthPercentage(100);
				table.setWidths(new float[] { 2, 14, 16, 16, 16, 16, 16, 16 });
				if (b.getTitles() != null && b.getTitles().size() > 0) {
					font = new com.itextpdf.text.Font(baseFont);
					font.setStyle(com.itextpdf.text.Font.BOLD);
					font.setSize(10);
					for (int i = 0; i < b.getTitles().size(); i++) {
						String title = b.getTitles().get(i);
						paragraph = new Paragraph(title, font);
						PdfPCell cell = new PdfPCell(paragraph);
						cell.setBackgroundColor(new BaseColor(0, 112, 192));
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setPaddingLeft(5);
						if (i == 0) {
							cell.setColspan(2);
						}
						cell.setBorderColor(borderColor);
						table.addCell(cell);
					}
				}
				if (b.getItems() != null && b.getItems().size() > 0) {
					font = new com.itextpdf.text.Font(baseFont);
					font.setSize(10);
					font.setColor(borderContext);

					com.itextpdf.text.Font fontColor = new com.itextpdf.text.Font(
							baseFont);
					fontColor.setSize(10);
					fontColor.setColor(255, 0, 0);
					for (int i = 0; i < b.getItems().size(); i++) {
						if (i != 0) {
							paragraph = new Paragraph("", font);
							PdfPCell cell = new PdfPCell(paragraph);
							cell.setPaddingLeft(5);
							cell.setBorderWidth(0);
							cell.setColspan(8);
							cell.setFixedHeight(15);
							cell.setUseAscender(true);
							cell.setUseDescender(true);
							cell.setBorderColor(borderColor);
							table.addCell(cell);
						}
						Item it = b.getItems().get(i);
						paragraph = new Paragraph(it.getName(), font);
						PdfPCell cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setColspan(2);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);

						paragraph = new Paragraph(it.getDescription(), font);
						cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);

						paragraph = new Paragraph(it.getReferenceValue(), font);
						cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);

						paragraph = new Paragraph(it.getValue(), font);
						cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);
						
						//巡检类型
						paragraph = new Paragraph(it.getCheckingType(), font);
						cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);
						
						paragraph = new Paragraph(it.getSummary(), font);
						cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);

						paragraph = new Paragraph(it.getStatus(),
								"异常".equals(it.getStatus()) ? fontColor : font);
						cell = new PdfPCell(paragraph);
						cell.setPaddingLeft(5);
						cell.setUseAscender(true);
						cell.setUseDescender(true);
						cell.setBorderColor(borderColor);
						table.addCell(cell);
						if (it.getItems() != null && it.getItems().size() > 0) {
							for (Item itc : it.getItems()) {
								paragraph = new Paragraph("", font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setBorderWidthTop(0);
								cell.setBorderWidthBottom(0);
								cell.setBorderWidthLeft(0);
								cell.setBorderWidthRight(0);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);

								paragraph = new Paragraph(itc.getName(), font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);

								paragraph = new Paragraph(itc.getDescription(),
										font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);

								paragraph = new Paragraph(
										itc.getReferenceValue(), font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);

								paragraph = new Paragraph(itc.getValue(), font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);
								
								//巡检类型
								paragraph = new Paragraph(itc.getCheckingType(), font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);
								
								paragraph = new Paragraph(itc.getSummary(),
										font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);

								paragraph = new Paragraph(
										itc.getStatus(),
										"异常".equals(itc.getStatus()) ? fontColor
												: font);
								cell = new PdfPCell(paragraph);
								cell.setPaddingLeft(5);
								cell.setUseAscender(true);
								cell.setUseDescender(true);
								cell.setBorderColor(borderColor);
								table.addCell(cell);
                                if (itc.getItems() != null && itc.getItems().size() > 0) {
                                    for (Item itcc : itc.getItems()) {
                                        paragraph = new Paragraph("", font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(10);
                                        cell.setBorderWidthTop(0);
                                        cell.setBorderWidthBottom(0);
                                        cell.setBorderWidthLeft(0);
                                        cell.setBorderWidthRight(0);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        paragraph = new Paragraph(itcc.getName(), font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        paragraph = new Paragraph(itcc.getDescription(),
                                                font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        paragraph = new Paragraph(
                                                itcc.getReferenceValue(), font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        paragraph = new Paragraph(itcc.getValue(), font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        //巡检类型
                                        paragraph = new Paragraph(itcc.getCheckingType(), font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        paragraph = new Paragraph(itcc.getSummary(),
                                                font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);

                                        paragraph = new Paragraph(
                                                itcc.getStatus(),
                                                "异常".equals(itcc.getStatus()) ? fontColor
                                                        : font);
                                        cell = new PdfPCell(paragraph);
                                        cell.setPaddingLeft(5);
                                        cell.setUseAscender(true);
                                        cell.setUseDescender(true);
                                        cell.setBorderColor(borderColor);
                                        table.addCell(cell);
                                    }
                                }
							}
						}
					}
				}
				doc.add(table);
			}
		}

		if (report.getConclusions() != null
				&& report.getConclusions().size() > 0) {
			Paragraph p = new Paragraph();
			p.setSpacingBefore(10);
			doc.add(p);
			font = new com.itextpdf.text.Font(baseFont);
			font.setSize(10);
			font.setColor(borderContext);
			paragraph = new Paragraph("  " + index + ".  结论", font);
			doc.add(paragraph);

			p = new Paragraph();
			p.setSpacingBefore(3);
			doc.add(p);
			int cIndex = 1;
			for (Conclusion c : report.getConclusions()) {
				if (!StringUtil.isNull(c.getName())) {
					font = new com.itextpdf.text.Font(baseFont);
					font.setSize(10);
					font.setColor(borderContext);
					paragraph = new Paragraph("  " + index + "." + cIndex++
							+ "  " + c.getName(), font);
					doc.add(paragraph);
					p = new Paragraph();
					p.setSpacingBefore(3);
					doc.add(p);
				}
				font = new com.itextpdf.text.Font(baseFont);
				font.setSize(10);
				font.setColor(borderContext);
				paragraph = new Paragraph(c.getValue(), font);
				PdfPCell cell = new PdfPCell(paragraph);
				cell.setPaddingLeft(5);
				cell.setUseAscender(true);
				cell.setUseDescender(true);
				cell.setFixedHeight(40);
				cell.setBorderColor(borderColor);
				table = new PdfPTable(1);
				table.setWidthPercentage(100);
				table.addCell(cell);
				doc.add(table);
			}
		}
		doc.close();
		out.flush();
		byte[] data = out.toByteArray();
		out.close();
		return data;
	}
}
