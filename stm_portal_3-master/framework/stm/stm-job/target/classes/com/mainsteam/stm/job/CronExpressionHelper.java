package com.mainsteam.stm.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.CronExpression;

public class CronExpressionHelper {

	public static final String SECOND = "SECOND";
	public static final String MINUTE = "MINUTE";
	public static final String HOUR = "HOUR";
	public static final String DAY = "DAY";
	public static final String WEEK = "WEEK";
	public static final String MONTH = "MONTH";
	public static final String YEAR = "YEAR";

	private Map<String, String> map = new HashMap<String, String>();

	public CronExpressionHelper set(String key, String value) {
		if (value != null) {
			map.put(key, value);
		}
		return this;
	}

	public boolean isNull(String v) {
		if (v != null && !"".equals(v.trim())) {
			return false;
		}
		return true;
	}

	private String fm(String v) {
		if (this.isNull(v)) {
			return "*";
		}
		return v;
	}

	@Override
	public String toString() {
		StringBuilder v = new StringBuilder();
		v.append(this.fm(map.get(SECOND)))
				.append(" ")
				.append(this.fm(map.get(MINUTE)))
				.append(" ")
				.append(this.fm(map.get(HOUR)))
				.append(" ")
				.append(this.isNull(map.get(DAY)) && this.isNull(map.get(WEEK)) ? "*"
						: this.isNull(map.get(DAY)) ? "?" : map.get(DAY))
				.append(" ")
				.append(this.fm(map.get(MONTH)))
				.append(" ")
				.append(this.isNull(map.get(DAY)) && this.isNull(map.get(WEEK)) ? "?"
						: !this.isNull(map.get(DAY)) ? "?" : map.get(WEEK))
				.append(this.isNull(map.get(YEAR)) ? "" : " " + map.get(YEAR));
		String r = v.toString();
		if (CronExpression.isValidExpression(r)) {
			return r;
		}
		throw new RuntimeException("this is value format no right.");
	}
}
