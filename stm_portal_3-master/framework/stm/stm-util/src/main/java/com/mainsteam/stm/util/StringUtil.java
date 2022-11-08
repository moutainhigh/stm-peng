package com.mainsteam.stm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isNull(String v) {
		if (v == null || "".equals(v.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNumber(String v) {
		Pattern p = Pattern.compile("-?\\d+\\.?\\d*");
		Matcher m = p.matcher(v);
		return m.matches();
	}
}
