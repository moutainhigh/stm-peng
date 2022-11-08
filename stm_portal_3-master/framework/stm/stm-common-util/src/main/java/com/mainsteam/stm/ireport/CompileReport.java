package com.mainsteam.stm.ireport;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

public class CompileReport {
	public static int compile(String jrxmlFile, String jasperFile) {
		int resultCode = 0;
		try {
			JasperCompileManager.compileReportToFile(jrxmlFile, jasperFile);
		} catch (JRException e) {
			resultCode = 1;
			e.printStackTrace();
		}
		return resultCode;
	}

	public static JasperReport compile(String jrxmlFile) {
		JasperReport jasperReport = null;
		try {
			jasperReport = JasperCompileManager.compileReport(jrxmlFile);
		} catch (JRException e) {
			e.printStackTrace();
		}
		return jasperReport;
	}
}
