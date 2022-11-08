package com.mainsteam.stm.caplib.dict;

/**
 * 
 * <li>文件名称: MetricTypeEnum.java</li><br/>
 * <li>文件描述: 操作符枚举</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li><br/>
 * <li>内容摘要: 操作符枚举定义</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */
@Deprecated
public enum OperatorEnum {

	Less("<"),

	LessEqual("<="),

	Equal("="),

	GreatEqual(">="),

	Great(">"),

	NotEqual("<>");

	private String oprateChar;

	private OperatorEnum(String oprateChars) {
		this.oprateChar = oprateChars;
	}

	public String toString() {
		return this.oprateChar;
	}
	
	public String getOprateChar() {
		return oprateChar;
	}

	public void setOprateChar(String oprateChar) {
		this.oprateChar = oprateChar;
	}

	public static OperatorEnum fromString(String oprateChars) {
		switch (oprateChars) {
		case "<":
			return Less;
		case "<=":
			return LessEqual;
		case "=":
			return Equal;
		case ">=":
			return GreatEqual;
		case ">":
			return Great;
		case "<>":
			return NotEqual;
		}
		return null;
	}

	public static void main(String[] argv) {
		OperatorEnum e = OperatorEnum.fromString("<=");
		System.out.println(e.name());
	}
}
