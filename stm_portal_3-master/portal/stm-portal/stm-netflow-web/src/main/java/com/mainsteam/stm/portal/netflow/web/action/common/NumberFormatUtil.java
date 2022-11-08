package com.mainsteam.stm.portal.netflow.web.action.common;

import com.mainsteam.stm.util.StringUtil;

public class NumberFormatUtil {

	public static double byteToMB(double v) {
		return v / (1024 * 1024);
	}

	private static final String[] CAPACITY_UNIT = { "", "K", "M", "G", "T",
			"P", "E", "Z", "Y", "B" };
	private static final int HEXADECIMAL = 1024;

	private static String autoUnit(String v) {
		try {
			return autoUnit(StringUtil.isNull(v) ? 0 : Double.parseDouble(v), 0);
		} catch (Exception e) {

		}
		return autoUnit(0, 0);
	}

	private static String autoUnit(double v, int index) {
		if (v >= HEXADECIMAL) {
			return autoUnit(v / HEXADECIMAL, ++index);
		}
		return twoDecimal(v) + "  " + CAPACITY_UNIT[index];
	}

	public static String autoFlowUnit(String v) {
		return autoUnit(v) + "B";
	}

	public static String autoFlowRoteUnit(String v) {
		return autoFlowUnit(v) + "ps";
	}

	public static String autoPacketUnit(String v) {
		return autoUnit(v) + "P";
	}

	public static String autoPacketRoteUnit(String v) {
		return autoPacketUnit(v) + "ps";
	}

	public static String autoConectUnit(String v) {
		return autoUnit(v) + "F";
	}

	public static String autoConectRoteUnit(String v) {
		return autoConectUnit(v) + "ps";
	}

	public static String twoDecimal(double v) {
		return String.format("%.2f", v);
	}

	public static String per(String v) {
		double p = Double
				.parseDouble("null".equals(v) || StringUtil.isNull(v) ? "0" : v) * 100;
		if (p > 0 && p < 0.01) {
			return "<0.01%";
		}
		return String.format("%.2f", p) + "%";
	}
}
