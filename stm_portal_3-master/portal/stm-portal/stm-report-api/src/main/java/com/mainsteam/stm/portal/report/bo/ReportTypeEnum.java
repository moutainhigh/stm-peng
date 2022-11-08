package com.mainsteam.stm.portal.report.bo;

public enum ReportTypeEnum {

	PERFORMANCE_REPORT("性能报告", 1), 
	ALARM_REPORT("告警统计", 2),  
	TOPN_REPORT("TOPN报告", 3),
	AVAILABILITY_REPORT("可用性报告", 4), 
	TREND_REPORT("趋势报告", 5), 
	ANALYSIS_REPORT("分析报告", 6), 
	BUSINESS_REPORT("业务报告", 7), 
	VM_PERFORMANCE_REPORT("性能报告(虚拟化)", 8), 
	VM_ALARM_REPORT("告警统计(虚拟化)", 9),
	COMPREHENSIVE_REPORT("综合性报告", 100);  
    // 成员变量  
    private String name;  
    private int index;  
    // 构造方法  
    private ReportTypeEnum(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }  
    
    public static ReportTypeEnum getReportTypeEnum(int index){
    	
    	ReportTypeEnum[] reportTypeEnums=values();
    	for(ReportTypeEnum reportTypeEnum:reportTypeEnums){
    		if(index==reportTypeEnum.getIndex()){
    			return reportTypeEnum;
    		}
    	}
    	
    	return null;
    }
    
    // 普通方法  
    public static String getName(int index) {  
        for (ReportTypeEnum c : ReportTypeEnum.values()) {  
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
