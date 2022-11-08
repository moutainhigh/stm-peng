package com.mainsteam.stm.portal.report.customchart;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

public class StandardCategoryItemLabelGeneratorCustomize extends
		StandardCategoryItemLabelGenerator {
	
	private static final long serialVersionUID = -4442861152860938197L;
	
	private int firstLabelPostion;
	private int secondLabelPostion;
	
	public StandardCategoryItemLabelGeneratorCustomize(int firstLabelPostion, int secondLabelPostion){
		this.firstLabelPostion = firstLabelPostion;
		this.secondLabelPostion = secondLabelPostion;
	}
	
	@Override
	public String generateLabel(CategoryDataset dataset, int row, int column) {
		if(row == 0 && (column == firstLabelPostion || column == secondLabelPostion)){
			return super.generateLabel(dataset, row, column);
		}else{
			return "";
		}
	}
	
}
