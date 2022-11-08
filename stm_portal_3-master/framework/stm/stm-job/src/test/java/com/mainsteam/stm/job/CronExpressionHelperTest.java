package com.mainsteam.stm.job;

public class CronExpressionHelperTest {

	public static void main(String[] args) {
		CronExpressionHelper c = new CronExpressionHelper();
		c.set(CronExpressionHelper.SECOND, "30")
				.set(CronExpressionHelper.DAY, "5")
				// .set(CronExpressionHelper.WEEK, "2")
				.set(CronExpressionHelper.YEAR, "2015");
		System.out.println(c.toString());
	}
}
