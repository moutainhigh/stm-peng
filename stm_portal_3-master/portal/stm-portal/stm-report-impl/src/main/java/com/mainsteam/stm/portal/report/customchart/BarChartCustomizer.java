package com.mainsteam.stm.portal.report.customchart;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.ui.TextAnchor;

public class BarChartCustomizer implements JRChartCustomizer {

	@Override
	public void customize(JFreeChart chart, JRChart jasperChart) {
		BarRenderer renderer = (BarRenderer) chart.getCategoryPlot()
				.getRenderer();
		renderer.setMaximumBarWidth(0.1);
		renderer.setMinimumBarLength(5D);

		CategoryPlot plot = chart.getCategoryPlot();
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setUpperMargin(0.1);
		
		plot.getDomainAxis().setMaximumCategoryLabelLines(2);
		
		ItemLabelPosition positiveIlp = null;
		ItemLabelPosition negativeIlp = null;
		// bar or 3D bar
		if(jasperChart.getChartType() == 2 || jasperChart.getChartType() == 3){
			positiveIlp = new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,
					TextAnchor.BASELINE_CENTER, 0.4);
			negativeIlp = new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,
					TextAnchor.BASELINE_CENTER, 0.4);
		}else if(jasperChart.getChartType() == 11 && PlotOrientation.HORIZONTAL == jasperChart.getPlot().getOrientation()){
			positiveIlp = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.BASELINE_RIGHT);
			negativeIlp = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.BASELINE_RIGHT);
			renderer.setItemLabelAnchorOffset(35D);
		}else{
			positiveIlp = new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER);
			negativeIlp = new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER);
		}
		renderer.setBasePositiveItemLabelPosition(positiveIlp);
		renderer.setNegativeItemLabelPosition(negativeIlp);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true, true);
	}

}
