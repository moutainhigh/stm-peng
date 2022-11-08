package com.mainsteam.stm.ireport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExportReportTemplate {

	private  final Log logger = LogFactory.getLog(ExportReportTemplate.class);
	
	private final String STM_REPORT_TEMP_DIR=System.getProperties().getProperty("catalina.home")
			+File.separatorChar+"stm_file_repository"+File.separatorChar+"STM_REPORT_TEMP_DIR";
	
	public File exportFileByType(IreportFileTypeEnum ift,JasperPrint jasperPrint){
		
		switch (ift.name()) {
		case "PDF":
			return exportPDF( jasperPrint);

		case "WORD":
			return exportWORD( jasperPrint);
			
		case "EXCEL":
			
			return exportEXCEL( jasperPrint);
		}
		
		return null;
	}
	
	private File exportPDF(JasperPrint jasperPrint){
		BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
		try {
			byte[] pdfByte = JasperExportManager.exportReportToPdf(jasperPrint);// .exportReportToPdfFile(jasperPrint, "C:/Users/pop/Desktop/ireport/mainCopy.pdf");

			Date date = new Date();
			File file=new File(STM_REPORT_TEMP_DIR,date.getTime()+".pdf");
			
			fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(pdfByte);
			
            return file;
		} catch (Exception e) {
			logger.error("exportPDF Error:",e);
			return null;
		}finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
	
	}
	private File exportWORD(JasperPrint jasperPrint){
		
        try {
        	Date date = new Date();
        	File docFile=new File(STM_REPORT_TEMP_DIR,date.getTime()+".docx");
        	JRDocxExporter jrDocxExporter = new JRDocxExporter();
            jrDocxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            jrDocxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(docFile));
            jrDocxExporter.exportReport();
            
            return docFile;
		} catch (Exception e) {
			logger.error("exportWORD Error:",e);
			return null;
		}
        
	}
	private File exportEXCEL(JasperPrint jasperPrint){
		try {
			Date date = new Date();
			File destFile = new File(STM_REPORT_TEMP_DIR,date.getTime()+".xls");
			destFile.createNewFile();
	        JRXlsExporter exporter = new JRXlsExporter();
	        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
//	        SimpleXlsReportConfiguration configuration1 = new SimpleXlsReportConfiguration();
//	        configuration1.setOnePagePerSheet(false);
//	        exporter.setConfiguration(configuration1);
	        exporter.exportReport();
	        
	        return destFile;
		} catch (Exception e) {
			logger.error("exportEXCEL Error:",e);
			return null;
		}
		
	}
	
}
