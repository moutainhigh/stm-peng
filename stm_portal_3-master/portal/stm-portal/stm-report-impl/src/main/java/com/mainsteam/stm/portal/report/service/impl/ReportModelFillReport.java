package com.mainsteam.stm.portal.report.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;

import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.customchart.UniqueCategoryLabel;

/**
 * ReportModelFillReport rmfr = new ReportModelFillReport();
 * rmfr.fillLineChart();
 * ...
 * rmfr.fillReportEnd();
 */
public class ReportModelFillReport {
	private static String INFO_REGEX_STR = ",";
	private static String DATA_REGEX_STR = "#--!!--#";
	private FileModel mainJasperFileModel;
	private Map<String, Object> paramMap = new HashMap<String, Object>();
	private int counter = 0;
	private int barNormalSize = 10;
	
	public ReportModelFillReport(FileModel mainJasperFileModel) {
		this.mainJasperFileModel = mainJasperFileModel;
	}
	
	public void fillLineReport(Chart chart) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		subParamMap.put("title", chart.getName());
		Map<String, Class> propMap = new HashMap<String, Class>();
		propMap.put("category", UniqueCategoryLabel.class);
		List<ChartData> chartDataList = chart.getChartData();
		for (int i = 0; i < chartDataList.size(); i++) {
			ChartData series = chartDataList.get(i);
			propMap.put("v" + i, Double.class);
			if(series.getIp() == null){
				subParamMap.put("v" + i, series.getName());
			}else{
				subParamMap.put("v" + i, series.getIp() + "\n" + series.getName());
			}
		}
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
		try {
			String[] xAxis = chart.getInfo().split(INFO_REGEX_STR);
			for (int i = 0; i < xAxis.length; i++) {
				DynaBean bean = dynaC.newInstance();
				bean.set("category", new UniqueCategoryLabel(i, xAxis[i]));
				for (int j = 0; j < chartDataList.size(); j++) {
					ChartData series = chartDataList.get(j);
					String[] data = series.getValue().split(DATA_REGEX_STR);
					String value = i >= data.length ? "null" : data[i];
					value = value.trim();
					bean.set("v" + j, "null".equals(value) ? null : Double.valueOf(value));
				}
				beanList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
	}

	public void fillStackedBarReport(Chart chart) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		List<ChartData> chartDataList = chart.getChartData();
		int barCnt = chartDataList == null ? 0 : chartDataList.size();
		
		subParamMap.put("title", chart.getName());
		Map<String, Class> propMap = new HashMap<String, Class>();
		propMap.put("series", String.class);
		propMap.put("category", String.class);
		propMap.put("value", Double.class);
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
		try {
			for (int i = 0; i < barCnt; i++) {
				ChartData chartData = chartDataList.get(i);
				DynaBean bean = dynaC.newInstance();
				if(chartData.getIp() == null){
					bean.set("series", chartData.getName());
				}else{
					bean.set("series", chartData.getIp() + "\n" + chartData.getName());
				}
				bean.set("category", chartData.getName());
				bean.set("value", "null".equals(chartData.getValue()) ? null : Double.valueOf(chartData.getValue()));
				beanList.add(bean);
			}
			// 如果值为空
			if(barCnt == 0){
				DynaBean bean = dynaC.newInstance();
				bean.set("series", "-");
				bean.set("category", "");
				bean.set("value", null);
				beanList.add(bean);
			}
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
	}

	public void fillBarReport(Chart chart) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		subParamMap.put("title", chart.getName());
		Map<String, Class> propMap = new HashMap<String, Class>();
		propMap.put("category", String.class);
		List<ChartData> chartDataList = chart.getChartData();
		for (int i = 0; i < chartDataList.size(); i++) {
			ChartData series = chartDataList.get(i);
			propMap.put("v" + i, Double.class);
			subParamMap.put("v" + i, series.getName());
		}
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
		try {
			String[] xAxis = chart.getInfo().split(INFO_REGEX_STR);
			for (int i = 0; i < xAxis.length; i++) {
				DynaBean bean = dynaC.newInstance();
				bean.set("category", xAxis[i]);
				for (int j = 0; j < chartDataList.size(); j++) {
					ChartData series = chartDataList.get(j);
					String[] data = series.getValue().split(DATA_REGEX_STR);
					String value = i >= data.length ? "null" : data[i];
					value = value.trim();
					bean.set("v" + j, "null".equals(value) || "".equals(value) ? null : Double.valueOf(value));
				}
				beanList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
	
	}
	public void fillPieReport(Chart chart) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		Map<String, Class> propMap = new HashMap<String, Class>();
		propMap.put("key", String.class);
		propMap.put("value", Double.class);
		List<ChartData> chartDataList = chart.getChartData();
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
		try {
			double amount = 0D;
			for (int i = 0; i < chartDataList.size(); i++) {
				ChartData chartData = chartDataList.get(i);
				DynaBean bean = dynaC.newInstance();
				String key = "";
				if(chartData.getIp() == null){
					key = chartData.getName();
				}else{
					if(chartData.getValue().equals("null") || chartData.getValue().equals("0")){
						continue;
					}
					key = chartData.getName() + "\n" + chartData.getIp();
				}
				bean.set("key", key);
				Double value = "null".equals(chartData.getValue()) ? 0D : Double.valueOf(chartData.getValue());
				bean.set("value", value);
				beanList.add(bean);
				amount += value == null ? 0 : value;
			}
			if(beanList == null || beanList.size() <= 0){
				DynaBean bean = dynaC.newInstance();
				bean.set("key", "暂无数据");
				bean.set("value", 0D);
				beanList.add(bean);
			}
			String tmp = " (告警总数：" + (int)amount + "个)";
			subParamMap.put("amount", " ");
			subParamMap.put("title", chart.getName() + tmp);
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
	}

	public void fillPieReport4StatQuery(Chart chart) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		Map<String, Class> propMap = new HashMap<String, Class>();
		propMap.put("key", String.class);
		propMap.put("value", Double.class);
		List<ChartData> chartDataList = chart.getChartData();
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
		try {
			double amount = 0D;
			for (int i = 0; i < chartDataList.size(); i++) {
				ChartData chartData = chartDataList.get(i);
				DynaBean bean = dynaC.newInstance();
				String key = "";
				if(chartData.getIp() == null){
					key = chartData.getName();
				}else{
					key = chartData.getName() + "\n" + chartData.getIp();
				}
				bean.set("key", key);
				Double value = "null".equals(chartData.getValue()) ? 0D : Double.valueOf(chartData.getValue());
				bean.set("value", value);
				beanList.add(bean);
				amount += value == null ? 0 : value;
			}
			subParamMap.put("amount", " ");
			subParamMap.put("title", chart.getName());
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
	}
	public void fillTableReport(String title, ColumnsTitle columnsTitle, ColumnsData columnsData) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		int colCnt = 0;
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		subParamMap.put("title", title);
		List<Columns> columnsList = columnsTitle.getColumns();
		for (int i = 0; i < columnsList.size(); i++) {
			Columns columns = columnsList.get(i);
			subParamMap.put("th" + i, columns.getText());
			if(null != columns.getApart() && !"".equals(columns.getApart())){
				String[] apart = columns.getApart().split(INFO_REGEX_STR);
				for(int j = 0; j < apart.length; j++){
					subParamMap.put("th" + i + "_" + j, apart[j]);
				}
				colCnt += apart.length;
			}else{
				colCnt ++ ;
			}
		}
		
		List<TableData> tableDataList = columnsData.getTableData();
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		try {
			Map<String, Class> propMap = new HashMap<String, Class>();
			for (int i = 0; i < colCnt; i++) {
				propMap.put("td" + i, String.class);
			}
			DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
			if(tableDataList != null){
				for (int i = 0; i < tableDataList.size(); i++) {
					TableData tableData = tableDataList.get(i);
					DynaBean bean = dynaC.newInstance();
					String[] data = tableData.getValue().split(DATA_REGEX_STR);
					for (int j = 0; j < Math.min(colCnt, data.length); j++) {
						if("null".equals(data[j])){
							bean.set("td" + j, "-");
						}else{
							bean.set("td" + j, data[j]);
						}
					}
					beanList.add(bean);
				}
			}else{
				DynaBean bean = dynaC.newInstance();
				bean.set("td1", null);
				beanList.add(bean);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
	}
	
	public void fillTitleReport(String title) {
		String newReportName = ReportModelUtil.createNewReportName(counter++);
		HashMap<String, Object> subParamMap = new HashMap<String, Object>();
		subParamMap.put("title", title);
		// 数据无用 只有加了这个才能显示数据...
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		Map<String, Class> propMap = new HashMap<String, Class>();
		propMap.put("key", String.class);
		DynaClass dynaC = ReportModelUtil.createJavaBean(newReportName, propMap);
		try {
			DynaBean bean = dynaC.newInstance();
			bean.set("key", "key");
			beanList.add(bean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		subParamMap.put("subList", beanList);
		paramMap.put(newReportName, subParamMap);
		
	}
	public JasperPrint fillReportEnd(){
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = JasperFillManager.fillReport(mainJasperFileModel.getFilePath(), paramMap, new JREmptyDataSource());
		} catch (JRException e) {
			e.printStackTrace();
		}
		return jasperPrint;
	}
}
