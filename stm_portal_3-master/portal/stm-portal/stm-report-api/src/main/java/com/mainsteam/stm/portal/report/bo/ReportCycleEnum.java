package com.mainsteam.stm.portal.report.bo;

public enum ReportCycleEnum {
	
	DAY("日报", 1), WEEK("周报", 2), MONTH("月报", 3);  
    // 成员变量  
    private String name;  
    private int index;  
    // 构造方法  
    private ReportCycleEnum(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }  
    // 普通方法  
    public static String getName(int index) {  
        for (ReportCycleEnum c : ReportCycleEnum.values()) {  
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
