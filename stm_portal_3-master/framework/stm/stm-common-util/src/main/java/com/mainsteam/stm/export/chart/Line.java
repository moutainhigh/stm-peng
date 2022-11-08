package com.mainsteam.stm.export.chart;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class Line {

	public byte[] generateLineImageByte(int width, int height, String title,
			String xLable, String yLable, List<LineData> data)
			throws IOException, ParseException {
		if (data != null && data.size() > 0) {
			TimeSeriesCollection tsc = new TimeSeriesCollection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for (LineData ld : data) {
				TimeSeries ts = new TimeSeries(ld.getName());
				if (ld.getPoints() != null) {
					for (Point p : ld.getPoints()) {
						ts.addOrUpdate(new Minute(sdf.parse(p.getName())),
								p.getValue());
					}
				}
				tsc.addSeries(ts);
			}
			JFreeChart chart = ChartFactory.createTimeSeriesChart(title,
					xLable, yLable, tsc, true, true, false);
			XYPlot xyPlot = chart.getXYPlot();
			xyPlot.setBackgroundPaint(null);// 设置图表内背景色
			DateAxis dateaxis = (DateAxis) xyPlot.getDomainAxis();
			// dateaxis.setVerticalTickLabels(true);// 到着显示x名称
			// 水平底部列表
			dateaxis.setLabelFont(new Font("宋体", Font.BOLD, 14));
			// 水平底部标题
			dateaxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));
			// 垂直标题
			ValueAxis rangeAxis = xyPlot.getRangeAxis();// 获取柱状
			rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 15));
			// 横轴框里的标题字体
			chart.getLegend().setItemFont(new Font("宋体", Font.ITALIC, 15));
			boolean year = true;
			boolean month = true;
			boolean day = true;
			String xname = data.get(0).getPoints().get(0).getName();
			String yearName = xname.substring(5, xname.length());
			String monthName = xname.substring(8, xname.length());
			String dayName = xname.substring(10, xname.length());
			for (int i = 0; i < data.get(0).getPoints().size(); i++) {
				if (year
						&& !data.get(0).getPoints().get(i).getName()
								.endsWith(yearName)) {
					year = false;
				}
				if (month
						&& !data.get(0).getPoints().get(i).getName()
								.endsWith(monthName)) {
					month = false;
				}
				if (day
						&& !data.get(0).getPoints().get(i).getName()
								.endsWith(dayName)) {
					day = false;
				}
			}
			String format = year ? "yyyy" : month ? "yyyy-MM"
					: day ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm";
			dateaxis.setDateFormatOverride(new SimpleDateFormat(format));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsJPEG(out, chart, width, height);
			return out.toByteArray();
		}
		return null;
	}
}
