package com.mainsteam.stm.portal.config.bo;


public enum ConfigScriptLevelEnum {

	ONE("一级目录", 1), 
	TWO("二级目录", 2),  
	THREE("三级目录", 3),
	FOUR("四级目录", 4), 
	FIVE("五级目录", 5);
	
	// 成员变量  
    private String name;  
    private int index;  
 // 构造方法  
    private ConfigScriptLevelEnum(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }  
    
public static ConfigScriptLevelEnum getReportTypeEnum(int index){
    	
	ConfigScriptLevelEnum[] configScriptLevelEnums=values();
    	for(ConfigScriptLevelEnum csEnum:configScriptLevelEnums){
    		if(index==csEnum.getIndex()){
    			return csEnum;
    		}
    	}
    	
    	return null;
    }
    
    // 普通方法  
    public static String getName(int index) {  
        for (ConfigScriptLevelEnum c : ConfigScriptLevelEnum.values()) {  
            if (c.getIndex() == index) {  
                return c.name;  
            }  
        }  
        return null;  
    }  
    // get set 方法  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    }
}
