package com.mainsteam.stm.portal.report.engine;

public class Test {

	private static final int MAX_POINTS = 10;

	private double E;

	/**
	 * Main program.
	 * 
	 * @param args
	 *            the array of runtime arguments
	 */
	public static void main(String args[]) {
		TrendAnalysisUtil line = new TrendAnalysisUtil();

		line.addDataPoint(new DataPoint(1, 136));
		line.addDataPoint(new DataPoint(2, 143));
		line.addDataPoint(new DataPoint(3, 132));
		line.addDataPoint(new DataPoint(4, 142));
		line.addDataPoint(new DataPoint(5, 147));
		
		for(int i=6;i<10;i++){
			DataPoint dataPoint=line.getNextDataPoint();
		
			line.addDataPoint(dataPoint);

			System.out.println(dataPoint.y);
		}


	}

	/**
	 * Print the computed sums.
	 * 
	 * @param line
	 *            the regression line
	 */
	private static void printSums(TrendAnalysisUtil line) {
		System.out.println("\n数据点个数 n = " + line.getDataPointCount());
		System.out.println("\nSum x  = " + line.getSumX());
		System.out.println("Sum y  = " + line.getSumY());
		System.out.println("Sum xx = " + line.getSumXX());
		System.out.println("Sum xy = " + line.getSumXY());
		System.out.println("Sum yy = " + line.getSumYY());

	}

	/**
	 * Print the regression line function.
	 * 
	 * @param line
	 *            the regression line
	 */
	private static void printLine(TrendAnalysisUtil line) {
		System.out.println("\n回归线公式:  y = " + line.getA1() + "x + "
				+ line.getA0());
		System.out.println("误差：     R^2 = " + line.getR());
		
		System.out.println(line.getA1()*(line.getDataPointCount()+1)+ line.getA0());
		
		
	}
}
