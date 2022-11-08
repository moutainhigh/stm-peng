package com.mainsteam.stm.common.metric.obj;


public enum MetricStateEnum {
	CRITICAL(10),CRITICAL_NOTHING(9), SERIOUS(8), WARN(6), NORMAL_UNKNOWN(3),UNKOWN(5),UNKNOWN_NOTHING(3), NORMAL(1),NORMAL_NOTHING(0);

	private int stateVal;

	MetricStateEnum(int stateVal) {
		this.stateVal = stateVal;
	}

	public int getStateVal() {
		return this.stateVal;
	}
	
	public MetricStateEnum valueof(int i){
		for(MetricStateEnum em: values()){
			if(em.stateVal==i){
				return em;
			}
		}
		return null;
	}

	public static MetricStateEnum valueIt(int i){
		if(i == MetricStateEnum.CRITICAL.stateVal)
			return MetricStateEnum.CRITICAL;
		else if(i == MetricStateEnum.WARN.stateVal)
			return MetricStateEnum.WARN;
		else if(i == MetricStateEnum.SERIOUS.stateVal)
			return MetricStateEnum.SERIOUS;
		else if(i == MetricStateEnum.NORMAL.stateVal)
			return MetricStateEnum.NORMAL;
		else
			return null;
	}
	
	public static boolean isUnknown(MetricStateEnum state){
		return UNKOWN==state || UNKNOWN_NOTHING==state || NORMAL_UNKNOWN==state;
	}
	
	public static boolean isNotAlarm(MetricStateEnum state){
		return NORMAL_NOTHING==state || UNKNOWN_NOTHING==state || NORMAL_UNKNOWN==state ||CRITICAL_NOTHING==state;
	}

	public static String getValue(MetricStateEnum metricTypeEnum) {
		switch (metricTypeEnum) {
			case CRITICAL:
				return "致命";
			case SERIOUS:
				return "严重";
			case WARN:
				return "警告";
			case NORMAL:
				return "正常";
			default:
					return metricTypeEnum.name();
		}
	}

}
