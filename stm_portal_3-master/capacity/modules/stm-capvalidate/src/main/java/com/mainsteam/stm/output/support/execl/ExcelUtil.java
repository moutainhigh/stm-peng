package com.mainsteam.stm.output.support.execl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 操作Excel表格的功能类
 * 输出OC4支持哪些厂商的哪些设备以及哪些型号文档
 */

public class ExcelUtil {
	
	private static final Log logger = LogFactory.getLog(ExcelUtil.class);
	
	private static Workbook workbook;

    /**
     * 读取Excel数据内容
     * @param InputStream
     * @return Map 包含单元格数据内容的Map对象
     */
    public static void writeExcelContent(Map<String, List<String[]>> args,String basePath) {
        try {
        	
        	Set<String> keySet = args.keySet();
        	Iterator<String> iterator = keySet.iterator();
        	
        	File dirFiles = new File(basePath + File.separator);
        	
        	if(dirFiles.exists()) {
        		try{
        			FileUtils.deleteDirectory(dirFiles);
        		}catch(Exception e){
        			if(logger.isErrorEnabled())
        				logger.error("Can't delete the directory:" + e.getMessage(), e);	
        		}
        	}
        	
        	while(iterator.hasNext()) {
        		String key = iterator.next();
        		List<String[]> values = args.get(key);
        		String[] keys = key.split("-");
        		
        		if(keys != null && keys.length == 3) {
        			String vendorId = keys[0];
        			String type = keys[1];
        			File file = null;
        			String firstColumn = type; //第一列显示名称
        			String filePath = basePath + File.separator;
        			boolean isNewCreate = false;
        			if(StringUtils.equals(keys[2], "network")) { //网络设备处理方案
        				
        				filePath += "network" + File.separator + vendorId + ".xls";
        			}else { //其他按照类型来划分
        				
        				filePath += "other" + File.separator + type +".xls";
        				firstColumn = vendorId;
        			}
        			
        			file = new File(filePath);
    				if(!file.exists()) { //如果存在则往里面添加行即可
    					isNewCreate = true;
    					file = createExcelReturnFile("Sheet0",filePath);
    					if(StringUtils.equals(keys[2], "network")) { //网络设备处理方案
    						setCellValue(0, 0, "类型", true);
    						setCellValue(0, 1, "型号", true);
    						setCellValue(0, 2, "厂商", true);
    					}else{
    						setCellValue(0, 0, "名称", true);
    						setCellValue(0, 1, "预计支持版本", true);
    						setCellValue(0, 2, "已验证的平台及版本", true);
    					}
    				}else {
    					FileInputStream is = new FileInputStream(file);
    					workbook = new HSSFWorkbook(is);
    				}
    				
    				Sheet sheet = workbook.getSheetAt(0);
    				// 得到总行数
    				int rowNum = sheet.getLastRowNum();
    				for(String[] value : values) {
    					switch(firstColumn){
    					case "L2Switch":
    						firstColumn = "二层交换机";
    						break;
    					case "LoadBalancer":
    						firstColumn = "负载均衡器";
    						break;
    					case "Printer":
    						firstColumn = "打印机";
    						break;
    					case "Copier":
    						firstColumn = "复印机";
    						break;
    					case "Firewall":
    						firstColumn = "防火墙";
    						break;
    					case "Router":
    						firstColumn = "路由器";
    						break;
    					case "RouterSwitch":
    						firstColumn = "三层交换机";
    						break;
    					case "Server":
    						firstColumn = "服务器";
    						break;
    					case "WirelessAP":
    						firstColumn = "无线AP";
    						break;
    					case "Wireless":
    						firstColumn = "无线设备";
    						break;
    					}
    					setCellValue(isNewCreate?rowNum:rowNum+1, 0, firstColumn, false);
    					setCellValue(isNewCreate?rowNum:rowNum+1, 1, value[0], false);
    					setCellValue(isNewCreate?rowNum:rowNum+1, 2, value[1], false);
    					rowNum ++;
					}
        			
        			FileOutputStream ou  = null;
        			try{
        				ou = new FileOutputStream(file);
        				workbook.write(ou);
        			}catch(Exception e) {
        				if(logger.isErrorEnabled()) {
        					logger.error(e.getMessage(), e);
        				}
        			}finally {
        				try {
        					ou.close();
        				} catch (IOException e) {
        					if(logger.isErrorEnabled()) {
            					logger.error(e.getMessage(), e);
            				}
        					ou = null;
        				}
        			}
        			
        		}
        		
        	}
        	
        } catch (Exception e) {
        	if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
        }
        
        
    }

     
    /**
     * 创建Excel文件，返回File文件对象
     * @param excelDataBean
     */
    public static File createExcelReturnFile(String IWPTitle, String filename) {
            //创建WorkBook对象
	    	File file=new File(filename);
	    	
	    	String tmpPath = filename.substring(0, filename.lastIndexOf(File.separator));
	    	
	    	if(tmpPath != null) {
    			File tmpFile = new File(tmpPath);
    			if(!tmpFile.exists()) {
    				tmpFile.mkdirs();
	    		}
	    	}
	    	
			workbook = new HSSFWorkbook();
			
            FileOutputStream out=null;
            try {
                file.createNewFile();
                //创建SHEET
                Sheet sheet = workbook.createSheet();
                workbook.setSheetName(0, IWPTitle);
                //设置默认高度，默认宽度
                sheet.setDefaultColumnWidth((short)25);
                //创建头
                createHeadRow(workbook, sheet,IWPTitle);
                //写入IWP返回的查询信息
                out=new FileOutputStream(file);
            } catch (Exception e) {
            	if(logger.isErrorEnabled()) {
					logger.error("Error path: " + file.getPath() + "," + e.getMessage(), e);
				}
            }finally{
                try {
                    workbook.write(out);
                    out.close();
                } catch (Exception e) {
                	if(logger.isErrorEnabled()) {
    					logger.error("Error path: " + file.getPath() + "," + e.getMessage(), e);
    				}
                }
            }
            return file;
            
            
    }            
            
     
     
    /**
     * 设置CellStyle格式
     * @param workbook
     * @return
     */
    public static HSSFCellStyle createCellStyle(Workbook workbook){
        HSSFCellStyle cellStyle=(HSSFCellStyle) workbook.createCellStyle();
        // 设置单元格边框样式
        // CellStyle.BORDER_DOUBLE      双边线
        // CellStyle.BORDER_THIN        细边线
        // CellStyle.BORDER_MEDIUM      中等边线
        // CellStyle.BORDER_DASHED      虚线边线
        // CellStyle.BORDER_HAIR        小圆点虚线边线
        // CellStyle.BORDER_THICK       粗边线
        //getAllPersonBean
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
         //创建字体
         Font fontHeader=workbook.createFont();
         //字体号码
         fontHeader.setFontHeightInPoints((short)10);
         //字体名称
         fontHeader.setFontName("宋体");
         cellStyle.setFont(fontHeader);
        return cellStyle;
    }
     
     
    /**
     * 功能：创建CELL
     * @param    row     HSSFRow
     * @param    cellNum int
     * @param    style   HSSFStyle
     * @return    HSSFCell
     */
    public static HSSFCell createCell(HSSFRow row,int cellNum,CellStyle style){
        HSSFCell cell=row.createCell(cellNum);
        cell.setCellStyle(style);
        return cell;
    }
    
    /**
     * 功能：创建字体
     * @param    wb          XSSFWorkbook
     * @param    boldweight  short
     * @param    color       short
     * @return    Font
     */
    public static Font createFont(Workbook wb,short boldweight,short color,short size){
        Font font=wb.createFont();
        font.setBold(false);
        font.setColor(color);
        font.setFontHeightInPoints(size);
        return font;
    }
     
    /**
     * 创建Head
     * @param wb
     * @param sheet
     */
    private  static void createHeadRow(Workbook wb, Sheet sheet, String IWPTitle){
        HSSFRow row = (HSSFRow) sheet.createRow(0);
        
        row.setHeight((short)(156.75 * 5));
        
        HSSFFont  font = createFont(wb, (short)18);
        
        font.setBold(false);
        
        // 创建单元格样式
        HSSFColor color = new HSSFColor();
        HSSFCellStyle style =createStyle(wb, color);
        style.setFont(font);// 设置字体
        HSSFCell cell=row.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        //cell.setCellStyle(style);
        //合并单元个(第0行到第一行合并，)
        //sheet.addMergedRegion(new CellRangeAddress(0,1,0,10));
        //设置合并单元格的边框(合并一行用row(0)
        //setCellBorder(0,10,row,style);
        //合并两行，必须要用roe(1)才能设置,设置边框
        setCellBorder(0,2,(HSSFRow) sheet.createRow(1),style);
        setCellBorder(0,2,row,style);
        //设置文件头
        cell.setCellValue(IWPTitle);
        cell.setCellStyle(style);
    }
     
     
    /**
     * 合并单元格加边框  水平
     * @param sheet
     * @param region
     * @param cs
     */
    public static void setCellBorder(int start, int end, HSSFRow row, HSSFCellStyle style) {
        for(int i=start;i<=end;i++){
            HSSFCell cell = row.createCell(i);
            cell.setCellValue("");
            cell.setCellStyle(style);
        }
    }
     
    /**
     * 创建字体
     * @param workbook
     * @param size 字体大小
     * @return
     */
    private static HSSFFont createFont(Workbook workbook,short size){
    	HSSFFont font=(HSSFFont) workbook.createFont();
        //字体样式
        font.setBold(false);
        //字体颜色
        font.setColor(HSSFColor.VIOLET.index);
        //字体大小
        if(0==size){
            font.setFontHeightInPoints((short) 12);
        }else{
            font.setFontHeightInPoints(size);
        }
        font.setFontName("微软雅黑");
        return font;
    }
     
    /**
     * 创建CellStyle
     * @param workbook
     * @param   HSSFColor color  颜色
     */
    private static HSSFCellStyle createStyle(Workbook workbook,HSSFColor color){
     
        HSSFCellStyle style=(HSSFCellStyle) workbook.createCellStyle();
        //对齐样式

		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
//        style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
//        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      
        // 设置边框  
        style.setBottomBorderColor(HSSFColor.RED.index);  
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        
        //设置自动换行
        style.setWrapText(true);
        return style;
    }
     
     
    /**
     * 将数据填充到Excel中去
     * @param bean
     */
    private static void setCellValue(int rowIndex, int cellIndex, String value, boolean isTitle){
    	
        HSSFCellStyle cellStyle = createStyle(workbook, new HSSFColor());
        HSSFFont font = null;
        if(isTitle) {
        	 //设置字体
            font = createFont(workbook,(short)0);
        } else {
        	font = createFont(workbook,(short)12);
        	font.setBold(false);
        	
        }
        cellStyle.setFont(font);
        Sheet sheet = workbook.getSheetAt(0);
        //每个服务方法结果时间空一行，所以加2
        HSSFRow row = (HSSFRow)sheet.getRow(rowIndex);
        if(row == null)
        	row = (HSSFRow) sheet.createRow(rowIndex);
        
        //row.setHeight((short)(156.75 * 3));
        
        HSSFCell keyCell = row.createCell(cellIndex);
        keyCell.setCellValue(value);
        
        keyCell.setCellStyle(cellStyle);
        
    }
    
    
    public static void main(String[] args) {
    	
    	System.setProperty("caplibs.path", "E:\\OneCenter4.0.1\\Capacity\\cap_libs");
    	
        // 对读取Excel表格标题测试
    	String xmlPattern = "(?:resource|DeviceType).xml";
    	String xmlPath = System.getProperty("caplibs.path", "");
    	String dir = xmlPath + File.separator + "supportDocs";
    	
    	if(args != null && args.length == 2){
    		xmlPath = args[0];
    		dir = args[1];
    	}
    	
    	if(StringUtils.isBlank(xmlPath)) {
    		if(logger.isErrorEnabled()) {
    			logger.error("Can't find the caplibs path...");
    		}
    		return ;
    	}
    	
    	if(logger.isErrorEnabled()) {
    		logger.error("Source path is " + xmlPath + "; Dist path is " + dir);
    	}
    	
    	IOFileFilter fileFilterCollect = new RegexFileFilter(xmlPattern);
		IOFileFilter all = FileFilterUtils.trueFileFilter();
		Collection<File> resourceFiles = FileUtils.listFiles(new File(xmlPath), fileFilterCollect, all);
		
		SAXReader reader = new SAXReader();
		Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();
		for (File file : resourceFiles) {
			try {
				if(StringUtils.contains(file.getPath(), File.separator + "network") || 
						StringUtils.contains(file.getPath(), File.separator + "snmpother") || 
						StringUtils.contains(file.getPath(), File.separator + "universalmodel") ||
						StringUtils.contains(file.getPath(), File.separator + "general")) {
					continue;
				}
				
				logger.info("Output support file path: " + file.getPath());
				InputStream ifile = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
				Document document = reader.read(ir);// 读取XML文件
				Element root = document.getRootElement();// 得到根节点
				
				if(StringUtils.endsWithIgnoreCase(file.getPath(), "DeviceType.xml")) {
					//读取网络设备的DeviceType.xml
					List<?> deviceTypeNodes = root.selectNodes("//DeviceType");
					if(deviceTypeNodes != null && deviceTypeNodes.size() > 0) {
						for(Object node : deviceTypeNodes) {
							
							Element deviceTypeEl = (Element)node;
							String vendorId = deviceTypeEl.attributeValue("VendorId");
							String vendorName = deviceTypeEl.attributeValue("VendorName");
							String type = deviceTypeEl.attributeValue("Type");
							String model = deviceTypeEl.elementText("ModelNumber");
							if(map.get(vendorId + "-" + type + "-network") != null) {
								List<String[]> list = map.get(vendorId + "-" + type + "-network");
								list.add(new String[]{model, vendorName});
							}else {
								List<String[]> list = new ArrayList<String[]>();
								list.add(new String[]{model, vendorName});
								map.put(vendorId + "-" + type + "-network", list);
							}
						}
					}
				}else{ //读取Resource.xml
					Element supportInfo = root.element("SupportInfo");
					if(supportInfo == null){
						logger.error("Can't find the support info in the path : " + file.getPath());
						continue;
					}
					String supportName = supportInfo.elementText("SupportName");
					String preSupportVersion = supportInfo.elementText("PreSupportVersion");
					String ActualSupportVersion = supportInfo.elementText("ActualSupportVersion");
					
					String type = "主机操作系统";
					if(StringUtils.contains(file.getPath(), File.separator + "application"))
						type = "应用服务器";
					else if(StringUtils.contains(file.getPath(), File.separator + "database"))
						type = "数据库";
					else if(StringUtils.contains(file.getPath(), File.separator + "hardware"))
						type = "硬件服务器";
					else if(StringUtils.contains(file.getPath(), File.separator + "middleware"))
						type = "中间件";
					else if(StringUtils.contains(file.getPath(), File.separator + "vm"))
						type = "虚拟化";
					else if(StringUtils.contains(file.getPath(), File.separator + "storage"))
						type = "存储";
					
					if(map.get(supportName + "-" + type + "-other") != null) {
						List<String[]> list = map.get(supportName + "-" + type + "-other");
						if(list != null) {
							String[] values = list.get(0);
							values[0] += "\n" + preSupportVersion;
							values[1] += "\n" + ActualSupportVersion;
							
							list.set(0, values);
						}
					}else {
						List<String[]> list = new ArrayList<String[]>();
						list.add(new String[]{preSupportVersion, ActualSupportVersion});
						map.put(supportName + "-" + type + "-other", list);
					}
					
				}
				
				
			}catch(Exception e) {
				if(logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		writeExcelContent(map, dir);
        	
    }
}
