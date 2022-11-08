package com.mainsteam.stm.export.pdf;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.mainsteam.stm.export.chart.Circular;
import com.mainsteam.stm.export.chart.CircularData;
import com.mainsteam.stm.export.chart.Line;
import com.mainsteam.stm.export.chart.LineData;
import com.mainsteam.stm.export.data.DataRow;
import com.mainsteam.stm.export.data.DataTable;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFHelper {

	private static BaseFont BASE_FONT = null;
	private static Font FONT_TITLE = null;
	private static Font FONT_NAME = null;
	private static Font FONT_CONTEXT = null;
	private static final int MARGIN_TOP = 20;
	static {
		try {
			BASE_FONT = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
					BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FONT_TITLE = new Font(BASE_FONT, 40, Font.NORMAL);
		FONT_NAME = new Font(BASE_FONT, 20, Font.NORMAL);
		FONT_CONTEXT = new Font(BASE_FONT, 12, Font.NORMAL);
		FONT_TITLE.setStyle(Font.BOLD);
		FONT_NAME.setStyle(Font.BOLD);
	}

	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private Document doc = null;
	private String title;

	public PDFHelper() {
		create(null);
	}

	public PDFHelper(String title) {
		create(title);
	}

	private void create(String title) {
		this.title = title;
		try {
			doc = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(doc, out);
			doc.addAuthor("武汉美新翔盛科技有限公司");
			doc.addCreator("流量分析平台");
			doc.open();
			if (this.getTitle() != null) {
				Paragraph p = new Paragraph(this.getTitle(), FONT_TITLE);
				p.setAlignment(Element.ALIGN_CENTER);
				doc.add(p);
				this.marginTop(10);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return title;
	}

	public void addLine(int width, int height, String title, String xLable,
			String yLable, List<LineData> data) {
		try {
			this.marginTop(MARGIN_TOP);
			PdfPTable table = new PdfPTable(1);
			if (title != null) {
				Paragraph p = new Paragraph(title, FONT_NAME);
				p.setAlignment(Element.ALIGN_CENTER);
				PdfPCell cell = new PdfPCell();
				cell.setBorder(0);
				cell.setMinimumHeight(40);
				cell.addElement(p);
				table.addCell(cell);
			}
			if (data != null && data.size() > 0) {
				Image img = Image.getInstance(new Line().generateLineImageByte(
						width, height, null, xLable, yLable, data));
				img.setAlignment(Image.ALIGN_CENTER);
				img.setBorder(0);
				PdfPCell cell = new PdfPCell();
				cell.setBorderColor(new BaseColor(187, 187, 187));
				cell.addElement(img);
				table.addCell(cell);
			}
			doc.add(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void marginTop(int v) {
		try {
			Paragraph p = new Paragraph();
			p.setSpacingBefore(v);
			doc.add(p);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public void addLine(String title, String xLable, String yLable,
			List<LineData> data) {
		this.addLine(800, 300, title, xLable, yLable, data);
	}

	public void addCircular(String title, List<CircularData> data) {
		this.addCircular(800, 300, title, data);
	}

	public void addCircular(int width, int height, String title,
			List<CircularData> data) {
		try {
			this.marginTop(MARGIN_TOP);
			PdfPTable table = new PdfPTable(1);
			if (title != null) {
				Paragraph p = new Paragraph(title, FONT_NAME);
				p.setAlignment(Element.ALIGN_CENTER);
				PdfPCell cell = new PdfPCell();
				cell.setBorder(0);
				cell.setMinimumHeight(40);
				cell.addElement(p);
				table.addCell(cell);
			}
			if (data != null && data.size() > 0) {
				Image img = Image.getInstance(new Circular().generate(width,
						height, null, data));
				img.setAlignment(Image.ALIGN_CENTER);
				img.setBorder(0);
				PdfPCell cell = new PdfPCell();
				cell.setBorderColor(new BaseColor(187, 187, 187));
				cell.addElement(img);
				table.addCell(cell);
			}
			doc.add(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTable(DataTable dataTable) {
		this.marginTop(MARGIN_TOP);
		try {
			PdfPTable table = new PdfPTable(1);
			if (dataTable.getName() != null) {
				Paragraph p = new Paragraph(dataTable.getName(), FONT_NAME);
				p.setAlignment(Element.ALIGN_CENTER);
				PdfPCell cell = new PdfPCell();
				cell.setBorder(0);
				cell.setMinimumHeight(40);
				cell.addElement(p);
				table.addCell(cell);
			}
			PdfPTable pdfPTable = new PdfPTable(dataTable.getColumnSize());
			pdfPTable.setSplitLate(false);
			pdfPTable.setSplitRows(true);
			pdfPTable.setTotalWidth(617);
			pdfPTable.setLockedWidth(true);
			if (dataTable.getHand() != null) {
				for (String columName : dataTable.getHand().getRow()) {
					Font font = new Font(FONT_CONTEXT);
					font.setColor(new BaseColor(255, 255, 255));
					Paragraph p = new Paragraph(columName, font);
					PdfPCell pdfPCell = new PdfPCell(p);
					pdfPCell.setMinimumHeight(30);
					pdfPCell.setUseAscender(true);
					pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					pdfPCell.setBorderColor(new BaseColor(187, 187, 187));
					pdfPCell.setBackgroundColor(new BaseColor(51, 51, 51));
					pdfPTable.addCell(pdfPCell);
				}
			}
			if (dataTable.getRows() != null) {
				for (int i = 0; i < dataTable.getRows().size(); i++) {
					DataRow row = dataTable.getRows().get(i);
					for (int j = 0; j < row.getRow().size(); j++) {
						Paragraph p = new Paragraph(row.getRow().get(j),
								FONT_CONTEXT);
						p.setAlignment(Element.ALIGN_CENTER);
						PdfPCell pdfPCell = new PdfPCell(p);
						pdfPCell.setMinimumHeight(25);
						pdfPCell.setUseAscender(true);
						pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						pdfPCell.setBorderColor(new BaseColor(187, 187, 187));
						if (i % 2 == 1) {
							pdfPCell.setBackgroundColor(new BaseColor(238, 238,
									238));
						}
						pdfPTable.addCell(pdfPCell);
					}
				}
			}
			PdfPCell cell = new PdfPCell();
			cell.setBorder(0);
			cell.addElement(pdfPTable);
			table.addCell(cell);
			doc.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void close() {
		doc.close();
	}

	public byte[] generate() {
		this.close();
		return out.toByteArray();
	}

	public ByteArrayOutputStream generateOutStream() {
		this.close();
		return out;
	}
}
