/**
 * 
 */
package com.mainsteam.stm.export.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * <li>文件名称: ExcelUtil</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年10月23日 下午5:45:03
 * @author 俊峰
 */
@SuppressWarnings("unchecked")
public class ExcelUtil<T> {

	@SuppressWarnings("rawtypes")
	public void exportExcel(String title, List<ExcelHeader> headers,
			List<T> dataset, OutputStream outStream) {
		// 声明一个工作薄
		Workbook workbook = new SXSSFWorkbook(10000);//在内存中只存10000条数据，防止内存溢出，其他数据写入硬盘
		// 生成一个样式
		CellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 生成一个字体
		Font font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式
		CellStyle style2 = workbook.createCellStyle();
//		style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
//		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		Font font2 = workbook.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);
		
		//自动将超过65535行的数据拆分成多个sheet
		List<List<T>> allDatas = new ArrayList<List<T>>();
		double rowsize = dataset.size();
		double sheetRowSize = 65535d;
		double sheetCount=(double) (rowsize/sheetRowSize);
		int sc = (int) Math.ceil(sheetCount);
		for (int i = 0; i <sc; i++) {
			List<T> sheetList = new ArrayList<>();
			if(i+1==sc){
				sheetList = dataset.subList((int)(i*sheetRowSize), dataset.size());
			}else{
				sheetList = dataset.subList((int)(i*sheetRowSize),(int)((i+1)*sheetRowSize));
			}
			allDatas.add(sheetList);
		}
		int titleIndex = 1;
		for (List<T> sheetList : allDatas) {
			// 生成一个表格
			Sheet sheet = workbook.createSheet(title+"-"+titleIndex);
			// 设置表格默认列宽度为15个字节
			sheet.setDefaultColumnWidth(20);
			
			// 产生表格标题行
			Row row = sheet.createRow(0);
			List<String> fields = new ArrayList<String>();
			for (int i = 0; i < headers.size(); i++) {
				ExcelHeader header = headers.get(i);
				Cell cell = row.createCell(i);
				cell.setCellStyle(style);
				HSSFRichTextString text = new HSSFRichTextString(
						header.getHeaderName());
				cell.setCellValue(text);
				fields.add(header.getObjField());
			}
			
			int index = 0;
			for (T t : sheetList) {
				index++;
				row = sheet.createRow(index);
				// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
				for (int j = 0; j < fields.size(); j++) {
					Cell cell = row.createCell(j);
					cell.setCellStyle(style2);//应用单元格样式
					String fieldName = fields.get(j);
					try {
						Class tCls = t.getClass();
						Field field = tCls.getDeclaredField(fieldName);
						field.setAccessible(true);
						Object value = field.get(t);
						if(null!=value){
							if (value instanceof Integer) {
								cell.setCellValue((Integer) value);
							} else if (value instanceof String) {
								cell.setCellValue(value.toString());
							} else if (value instanceof Double) {
								NumberFormat nf = NumberFormat.getInstance();
								nf.setGroupingUsed(false);
								cell.setCellValue(nf.format((Double) value));
							} else if (value instanceof Date) {
								SimpleDateFormat format = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								cell.setCellValue(format.format(value));
							} else if (value instanceof Float) {
								cell.setCellValue((Float) value);
							} else {
								cell.setCellValue(value.toString());
							}
						}else{
							cell.setCellValue("");
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} finally {
						// 清理资源
					}
				}
			}
			titleIndex++;
		}
		
//		ServletOutputStream outStream = null;
		try {
//			response.reset();
//			response.setContentType("application/x-msdownload");
//			response.setHeader("Content-Disposition", "attachment; filename="
//					+ new String(title.getBytes("gb2312"), "ISO-8859-1")
//					+ ".xlsx");
//			outStream = response.getOutputStream();
			workbook.write(outStream);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				outStream.flush();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public List<T> readExcelContent(ReadExcelArgs excelArgs){
		POIFSFileSystem fs = null;
		HSSFWorkbook wb = null;
		List<T> results = new ArrayList<T>();
		try{
			fs = new POIFSFileSystem(excelArgs.getIn());
			wb = new HSSFWorkbook(fs);
		}catch(Exception e){
			return results;
		}
		int rowSize = 0;
		HSSFSheet sheet = wb.getSheetAt(excelArgs.getSheetIndex());
		IExcelConvert convert = excelArgs.getConvert();
		List<String> values = new ArrayList<String>();
		HSSFCell cell = null;
		
		for(int rowIndex=excelArgs.getIgnoreRows(),len=sheet.getLastRowNum(); rowIndex<=len; rowIndex++){
			HSSFRow row = sheet.getRow(rowIndex);
			 if (row == null) {
                 continue;
             }
             int tempRowSize = row.getLastCellNum() + 1;
             if (tempRowSize > rowSize) {
                 rowSize = tempRowSize;
             }
             
             for (int columnIndex = 0, len1=row.getLastCellNum(); columnIndex<len1; columnIndex++) {
            	 cell = row.getCell(columnIndex);
            	 cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            	 values.add(cell.getStringCellValue());
             }
             Object value = convert.convert(values, rowIndex);
             if(value == null){
            	 return results;
             }
             results.add((T)value);
             values.clear();
		}
		return results;
	}
}
