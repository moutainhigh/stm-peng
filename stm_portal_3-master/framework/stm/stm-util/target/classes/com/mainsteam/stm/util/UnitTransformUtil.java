package com.mainsteam.stm.util;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public class UnitTransformUtil {
	private static Logger logger = Logger.getLogger(UnitTransformUtil.class);
	private static DecimalFormat decimalFormat = new DecimalFormat("##.##");
	
	public static String transform(String value, String unit){
		try{
			if(value != null && !"".equals(value.trim())
					&& value.trim().matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
					&& unit != null && !"".equals(unit.trim())){
				// 去掉空格
				value = value.trim();
				switch (unit) {
				case "ms":
				case "毫秒":
					value = transformMillisecond(Double.valueOf(value));
					break;
				case "s":
				case "秒":
					value = transformSecond(Double.valueOf(value));
					break;
				case "Bps":
					value = transformByteps(Double.valueOf(value));
					break;
				case "bps":
				case "比特/秒":
					value = transformBitps(Double.valueOf(value));
					break;
				case "KB/s":
					value = transformKBs(Double.valueOf(value));
					break;
				case "Byte":
					value = transformByte(Double.valueOf(value));
					break;
				case "KB":
					value = transformKB(Double.valueOf(value));
					break;
				case "MB":
					value = transformMb(Double.valueOf(value));
					break;
				case "MHz":
					value = transformMHz(Double.valueOf(value));
					break;
				default:
					value = decimalFormat.format(Double.valueOf(value)) + unit;
					break;
				}
			}else{
				if(value != null && !"".equals(value)){
					if(unit!=null){
						value += unit;
					}else{
						value = value.trim();
					}
					
				}
			}
		}catch(Exception e){
			value += unit;
			logger.error("UnitTransformUtil", e);
		}
		return value;
	}
	private static String transformMHz(double MHzDouble){
		StringBuilder sb = new StringBuilder();
		if(MHzDouble > 1024D){
			sb.append(decimalFormat.format(MHzDouble / 1024D) + "GHz");
		}else{
			sb.append(MHzDouble + "MHz");
		}
		return sb.toString();
	}

	private static String transformByte(double byteDouble){
		StringBuilder sb = new StringBuilder();
		if(byteDouble > 1024D * 1024D * 1024D){
			sb.append(decimalFormat.format(byteDouble / (1024D * 1024D * 1024D)) + "GB");
		}else if(byteDouble > 1024D * 1024D){
			sb.append(decimalFormat.format(byteDouble / (1024D * 1024D)) + "MB");
		}else if(byteDouble > 1024D){
			sb.append(decimalFormat.format(byteDouble / 1024D) + "KB");
		}else{
			sb.append(byteDouble + "Byte");
		}
		return sb.toString();
	}
	private static String transformKB(double KB){
		StringBuilder sb = new StringBuilder();
		if(KB > 1024D * 1024D){
			sb.append(decimalFormat.format(KB / (1024D * 1024D)) + "GB");
		}else if(KB > 1024D){
			sb.append(decimalFormat.format(KB / 1024D) + "MB");
		}else{
			sb.append(KB + "KB");
		}
		return sb.toString();
	}
	
	// 1024Bps=1MBps 1024KBps=1MBps 1024MBps=1GBps
	private static String transformByteps(double bps){
		StringBuilder sb = new StringBuilder();
		if(bps > 1000D * 1000D * 1000D){
			sb.append(decimalFormat.format(bps / (1000D * 1000D * 1000D)) + "GBps");
		}else if(bps > 1000D * 1000D){
			sb.append(decimalFormat.format(bps / (1000D * 1000D)) + "MBps");
		}else if(bps > 1000D){
			sb.append(decimalFormat.format(bps / 1000D) + "KBps");
		}else{
			sb.append(bps + "Bps");
		}
		return sb.toString();
	}
	// 1024bps=1Kbps 1024Kbps=1Mbps 1024Mbps=1Gbps
	private static String transformBitps(double bps){
		StringBuilder sb = new StringBuilder();
		if(bps > 1000D * 1000D * 1000D){
			sb.append(decimalFormat.format(bps / (1000D * 1000D * 1000D)) + "Gbps");
		}else if(bps > 1000D * 1000D){
			sb.append(decimalFormat.format(bps / (1000D * 1000D)) + "Mbps");
		}else if(bps > 1000D){
			sb.append(decimalFormat.format(bps / 1000D) + "Kbps");
		}else{
			sb.append(bps + "bps");
		}
		return sb.toString();
	}
	
	private static String transformKBs(double KBs){
		StringBuilder sb = new StringBuilder();
		if(KBs > 1024D * 1024D){
			sb.append(decimalFormat.format(KBs / (1024D * 1024D)) + "GB/s");
		}else if(KBs > 1024D){
			sb.append(decimalFormat.format(KBs / 1024D) + "MB/s");
		}else{
			sb.append(decimalFormat.format(KBs) + "KB/s");
		}
		return sb.toString();
	}
	private static String transformMb(double mb){
		StringBuilder sb = new StringBuilder();
		if(mb >= 1024D){
			sb.append(decimalFormat.format(mb / 1024D) + "GB");
		}else{
			sb.append(decimalFormat.format(mb) + "MB");
		}
		return sb.toString();
	}
	private static String transformMillisecond(double milliseconds){
		double millisecond = milliseconds;
		double seconds = 0, second = 0;
		double minutes = 0, minute = 0;
		double hours = 0, hour = 0;
		double day = 0;
		if(milliseconds > 1000){
			millisecond = milliseconds % 1000;
			second = seconds = (milliseconds - millisecond) / 1000;
		}
		if(seconds > 60){
			second = seconds % 60;
			minute = minutes = (seconds - second) / 60;
		}
		if(minutes > 60){
			minute = minutes % 60;
			hour = hours = (minutes - minute) / 60;
		}
		if(hours > 24){
			hour = hours % 24;
			day = (hours - hour) / 24;
		}
		StringBuilder sb = new StringBuilder();
		sb = day > 0 ? sb.append((int)day + "天") : sb;
		sb = hour > 0 ? sb.append((int)hour + "小时") : sb;
		sb = minute > 0 ? sb.append((int)minute + "分") : sb;
		sb = second > 0 ? sb.append((int)second + "秒") : sb;
		sb = millisecond > 0 ? sb.append((int)millisecond + "毫秒") : sb;
		sb = "".equals(sb.toString()) ? sb.append((int)millisecond + "毫秒") : sb;
		return sb.toString();
	}
	
	private static String transformSecond(double seconds){
		double second = seconds;
		double minutes = 0, minute = 0;
		double hours = 0, hour = 0;
		double day = 0;
		if(seconds > 60){
			second = seconds % 60;
			minute = minutes = (seconds - second) / 60;
		}
		if(minutes > 60){
			minute = minutes % 60;
			hour = hours = (minutes - minute) / 60;
		}
		if(hours > 24){
			hour = hours % 24;
			day = (hours - hour) / 24;
		}
		StringBuilder sb = new StringBuilder();
		sb = day > 0 ? sb.append((int)day + "天") : sb;
		sb = hour > 0 ? sb.append((int)hour + "小时") : sb;
		sb = minute > 0 ? sb.append((int)minute + "分") : sb;
		sb = second > 0 ? sb.append((int)second + "秒") : sb;
		sb = "".equals(sb.toString()) ? sb.append((int)second + "秒") : sb;
		return sb.toString();
	}
	
}
