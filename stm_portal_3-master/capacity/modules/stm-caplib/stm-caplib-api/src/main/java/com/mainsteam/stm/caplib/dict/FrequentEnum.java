package com.mainsteam.stm.caplib.dict;

import java.util.concurrent.TimeUnit;

/**
 * 
 * <li>文件名称: FrequentEnum.java</li><br/>
 * <li>文件描述: 采集频度</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li> <br/>
 * <li>内容摘要: 系统采集频度定义</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */

public enum FrequentEnum {

	sec30(30, TimeUnit.SECONDS),

	min1(1, TimeUnit.MINUTES),

	min5(5, TimeUnit.MINUTES),

	min10(10, TimeUnit.MINUTES),

	min30(30, TimeUnit.MINUTES),

	hour1(1, TimeUnit.HOURS),
	
	hour4(4, TimeUnit.HOURS),
	
	hour8(8, TimeUnit.HOURS),

	day1(24, TimeUnit.HOURS);

	private long seconds;
	private int freq;
	private TimeUnit unit;

	private FrequentEnum(int freq, TimeUnit unit) {
		this.freq = freq;
		this.unit = unit;
		this.seconds = computeSeconds();
	}

	public int getFreq() {
		return this.freq;
	}

	public long getSeconds() {
		return this.seconds;
	}

	private long computeSeconds() {
		long s = 1;
		switch (this.unit) {
		case MINUTES:
			s = 60;
			break;
		case HOURS:
			s = 3600;
			break;
		case DAYS:
			s = 86400;
			break;
		default:
			break;
		}
		return s * this.freq;
	}

	public TimeUnit getUnit() {
		return this.unit;
	}

	// public static FrequentEnum fromString(String oprateChars) {
	// switch (oprateChars) {
	// case "<":
	// return Less;
	// case "<=":
	// return LessEqual;
	// case "=":
	// return Equal;
	// case ">=":
	// return GreatEqual;
	// case ">":
	// return Great;
	// case "<>":
	// return NotEqual;
	// }
	// return null;
	// }
	//
	public static void main(String[] argv) {
		System.out.println(FrequentEnum.valueOf("day1").name());
		System.out.println(FrequentEnum.valueOf("day1").toString());
		System.out.println(FrequentEnum.valueOf("day1").getSeconds());
	}

	public String toString() {
		String s = "分钟";
		switch (this.unit) {
		case SECONDS:
			s = "秒";
			break;
		// case MINUTES:
		// s= "分钟";
		// break;
		case HOURS:
			s = "小时";
			break;
		case DAYS:
			s = "天";
			break;
		default:
			break;
		}
		return String.valueOf(this.freq) + s;
	}

}
