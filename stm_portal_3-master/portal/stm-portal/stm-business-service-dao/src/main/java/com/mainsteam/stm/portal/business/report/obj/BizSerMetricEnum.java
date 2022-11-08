package com.mainsteam.stm.portal.business.report.obj;

/**
 * <li>文件名称: BizSerMetricEnum.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 业务报表指标枚举</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月18日
 * @author   caoyong
 */
public enum BizSerMetricEnum {
	AVAILABLE_RATE("0","可用率(%)"),
	MTTR("1","MTTR(小时)"),
	MTBF("2","MTBF(天)"),
	OUTAGE_TIMES("3","宕机次数(次)"),
	DOWNTIME("4","宕机时长(小时)"),
	WARN_NUM("5","告警数量"),
	UNRECOVERED_WARN_NUM("6","未恢复告警数量");
	
	private String index;
	private String name;
	BizSerMetricEnum(String index,String name){
		this.index = index;
		this.name = name;
	}
	/**
	 * 通过index获取CN_Name;
	 * @param index
	 * @return
	 */
    public static String getName(String index) {  
        for (BizSerMetricEnum b : BizSerMetricEnum.values()) {  
            if (b.getIndex().equals(index)) {  
                return b.name;  
            }  
        }  
        return null;  
    }
    /**
     * 通过index返回枚举对象
     * @param index
     * @return
     */
    public static BizSerMetricEnum getBizSerMetricEnum(String index) {  
    	for (BizSerMetricEnum b : BizSerMetricEnum.values()) {  
    		if (b.getIndex().equals(index)) {  
    			return b;  
    		}  
    	}
		return null;  
    }  
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
