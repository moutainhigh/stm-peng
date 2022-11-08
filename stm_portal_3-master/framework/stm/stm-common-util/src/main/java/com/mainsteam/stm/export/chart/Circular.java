package com.mainsteam.stm.export.chart;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

public class Circular {

	public byte[] generate(int width, int height, String title,
			List<CircularData> data) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (CircularData c : data) {
				dataset.setValue(c.getName(), c.getValue());
			}
			JFreeChart chart = ChartFactory.createRingChart(title, dataset,
					true, false, false);
			// chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
			PiePlot piePlot = (PiePlot) chart.getPlot();// 获取图表区域对象
			piePlot.setLabelFont(new Font("宋体", Font.BOLD, 10));
			chart.getLegend().setItemFont(new Font("宋体", Font.BOLD, 10));
			RingPlot plot = (RingPlot) chart.getPlot();
			plot.setBackgroundPaint(null);
			plot.setOutlineVisible(false);// 设置绘图区边框是否可见
			plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
					"{0} ({2})", NumberFormat.getNumberInstance(),
					new DecimalFormat("0.00%"))); // 设置图例数据格式
			plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}",
					NumberFormat.getNumberInstance(),
					new DecimalFormat("0.00%")));
			plot.setLabelBackgroundPaint(null);
			plot.setLabelOutlinePaint(null);
			plot.setLabelShadowPaint(null);
			plot.setStartAngle(100);
			plot.setShadowPaint(null);// 去掉环形阴影
			plot.setLabelBackgroundPaint(null);
			// plot.setOutlinePaint(null);
			// plot.setOutlinePaint(null);

			// plot.setOutlinePaint(null);
			// plot.setParent(null);
			// plot.setOutlinePaint(null);
			// plot.setCircular(false);
			// plot.setCircular(false, false);
			// plot.setBaseSectionPaint(Color.WHITE);
			// plot.setBaseSectionOutlinePaint(Color.WHITE);
			// plot.setLabelPaint(Color.WHITE);
			// 图例样式的设定,图例含有M 和P 方法
			LegendTitle legendTitle = chart.getLegend();// 获得图例标题
			legendTitle.setPosition(RectangleEdge.RIGHT);// 图例右边显示
			legendTitle.setBorder(0, 0, 0, 0);// 设置图例上下左右线
			legendTitle.setPadding(0, 0, 0, 15);
			// plot.setLabelLinksVisible(false);// 设置图形和tooltip之间连线
			// plot.setLabelLinkPaint(plot.getBackgroundPaint());
			// plot.setLabelBackgroundPaint(plot.getBackgroundPaint());
			// plot.setLabelOutlinePaint(plot.getBackgroundPaint());
			// plot.setLabelShadowPaint(plot.getBackgroundPaint());
			// plot.setLabelPaint(plot.getBackgroundPaint());
			// plot.setCircular(true);// 设置饼图是否为原型
			// plot.setLabelGap(0.09);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsJPEG(out, chart, width, height);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
