package com.mainsteam.stm.portal.report.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;
import com.mainsteam.stm.portal.report.bo.Table;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class TestReportModel {

	@Autowired
	private IFileClientApi fileClient;

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("caplibs.path", "F:/wiserv/OC/cap_libs");
		System.setProperty("testCase", "case");
	}

	private static String index(int index){
		return String.valueOf(index);
	}
	
	@Test
	public void createReportModel() {
		ReportModelMain rmm = new ReportModelMain("1", fileClient);
		rmm.addLineReport(3);
		rmm.writeAndComplieJrxmlFile();
	}

	@Test
	public void fillReportModel() throws Exception {
		Chart chart = new Chart();
		chart.setInfo("1:00,2:00,3:00,4:00,5:00,6:00,7:00");
		chart.setName("内存利用率(%)");
		List<ChartData> chartDataList = new ArrayList<ChartData>();
		for (int i = 0; i < 6; i++) {
			ChartData chartData = new ChartData();
			chartData.setName("Latte ZB Switch_14532_" + i);
			chartData.setIp("127.0.0.1");
			int j = i * 2;
			chartData.setValue("3" + j + ",2" + j + ",4" + j + ",5" + j + ",6"
					+ j + ",2" + j + ",4" + j + "");
			chartDataList.add(chartData);
		}
		chart.setChartData(chartDataList);
		File file = fileClient.getFileByID(3561002);
		System.out.println(file.getPath());
//		ReportModelFillReport rmfr = new ReportModelFillReport(file);
//		rmfr.fillLineChart("0", chart);
//		JasperPrint jasperPrint = rmfr.fillReportEnd();
//		JasperExportManager.exportReportToPdfFile(jasperPrint, "C:/Users/pop/Desktop/ireport/main.pdf");
	}
}
