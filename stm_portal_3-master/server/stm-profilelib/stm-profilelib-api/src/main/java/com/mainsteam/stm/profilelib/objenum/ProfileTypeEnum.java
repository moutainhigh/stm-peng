package com.mainsteam.stm.profilelib.objenum;

public enum ProfileTypeEnum {
	
	/**
	 * 默认策略
	 */
	DEFAULT,
	/**
	 * 自定义策略
	 */
	SPECIAL,
	/**
	 * 个性化策略
	 */
	PERSONALIZE,
	
	/**
	 * 所有的策略
	 */
	ALL;
	
//	/**
//	 * 默认策略
//	 */
//	DEFAULT("0"),
//	/**
//	 * 自定义策略
//	 */
//	SPECIAL("1"),
//	/**
//	 * 个性化策略
//	 */
//	PERSONALIZE("2");
//	
//	private String code;
//	
//	private ProfileTypeEnum(String code){
//		this.code = code;
//	}
//	
//	public String getCode(){
//		return this.code;
//	}
//	
//	public static ProfileTypeEnum getProfileType(String code){
//		switch(code){
//		case "0":
//			return ProfileTypeEnum.DEFAULT;
//		case "1":
//			return ProfileTypeEnum.SPECIAL;
//		case "2":
//			return ProfileTypeEnum.PERSONALIZE;
//		default:
//			return null;
//		}
//	}
}
