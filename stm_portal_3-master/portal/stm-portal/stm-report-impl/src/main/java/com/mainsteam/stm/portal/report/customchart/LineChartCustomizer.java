package com.mainsteam.stm.portal.report.customchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

import net.sf.jasperreports.charts.fill.JRFillCategoryDataset;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import net.sf.jasperreports.engine.JRChartDataset;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;

public class LineChartCustomizer implements JRChartCustomizer {
	@Override
	public void customize(JFreeChart chart, JRChart jasperChart) {
		boolean isLastTrendAnalysis = false;
		boolean isThisTrendAnalysis = false;
		CategoryMarker firstMarker = null;
		int firstLabelPostion = 0;
		int secondLabelPostion = 0;
		CategoryMarker secondMarker = null;
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		// 判断是否为趋势报表
		JRChartDataset jrDataset = jasperChart.getDataset();
		if(jrDataset instanceof JRFillCategoryDataset){
			JRFillCategoryDataset jrCategoryDataset = (JRFillCategoryDataset)jrDataset;
			if(jrCategoryDataset.getDataset() instanceof DefaultCategoryDataset){
				DefaultCategoryDataset defaultCategoryDataset = (DefaultCategoryDataset)jrCategoryDataset.getDataset();
				List<String> keys = defaultCategoryDataset.getRowKeys();
				isLastTrendAnalysis = keys.contains("上周趋势") || keys.contains("上月趋势") ? true : isLastTrendAnalysis;
				isThisTrendAnalysis = keys.contains("本周趋势") || keys.contains("本月趋势") ? true : isThisTrendAnalysis;
				List<UniqueCategoryLabel> columnKeys = defaultCategoryDataset.getColumnKeys();
				if(isLastTrendAnalysis && isThisTrendAnalysis){
					for (int i = 0; i < columnKeys.size(); i++) {
						String columnKey = columnKeys.get(i).toString();
						if(columnKey.startsWith(" ")){
							if(firstMarker == null){
								firstLabelPostion = i;
								firstMarker = new CategoryMarker(columnKeys.get(i));
							}else{
								secondLabelPostion = i;
								secondMarker = new CategoryMarker(columnKeys.get(i));
							}
						}
					}
					firstMarker.setDrawAsLine(true);
					secondMarker.setDrawAsLine(true);
					firstMarker.setPaint(Color.red);
					secondMarker.setPaint(Color.red);
					categoryPlot.addDomainMarker(firstMarker, Layer.BACKGROUND);
					categoryPlot.addDomainMarker(secondMarker, Layer.BACKGROUND);
					LineAndShapeRenderer lasr = (LineAndShapeRenderer)categoryPlot.getRenderer();
					lasr.setBaseShapesVisible(false);
					lasr.setSeriesItemLabelsVisible(0, true);
					// 改变为虚线
					categoryPlot.getRenderer().setSeriesStroke(2, new BasicStroke(1.0F, 1, 1, 1.0F, new float[] {6F, 6F}, 0.0F));
					// 两个点的数据显示
					StandardCategoryItemLabelGeneratorCustomize scigCustomize = new StandardCategoryItemLabelGeneratorCustomize(firstLabelPostion ,secondLabelPostion);
					categoryPlot.getRenderer().setBaseItemLabelGenerator(scigCustomize);
					categoryPlot.getRenderer().setBaseItemLabelsVisible(true);
				}else{
					int maxUcLabelNum = 16;
					int step = (int) Math.ceil(Double.valueOf(columnKeys.size()) / Double.valueOf(Math.min(maxUcLabelNum, columnKeys.size())));
					for (int i = 0; i < columnKeys.size(); i++) {
						if(i % step != 0){
							UniqueCategoryLabel ucLabel = columnKeys.get(i);
							ucLabel.setValue("");
						}
					}
				}
			}
			categoryPlot.getRenderer();
		}
		categoryPlot.getDomainAxis().setLowerMargin(0.01);
		categoryPlot.getDomainAxis().setUpperMargin(0.01);
	}

}
