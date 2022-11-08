package com.mainsteam.stm.common.instance.obj;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

public enum InstanceStateEnum {
	CRITICAL(12),
	@Deprecated
	CRITICAL_NOTHING(11),
	@Deprecated
	UNKOWN(10),
	@Deprecated
	UNKNOWN_NOTHING(0),
	NORMAL_CRITICAL(8),
	@Deprecated
	NORMAL_UNKNOWN(2),
	SERIOUS(6),
	WARN(4),
	NORMAL(1),
	@Deprecated
	NORMAL_NOTHING(0),

	MONITORED(-1),
	NOT_MONITORED(-2) ,
	DELETED(-1)
	;

	private int stateVal;

	
	private InstanceStateEnum(int stateVal) {
		this.stateVal = stateVal;
	}

	public int getStateVal() {
		return this.stateVal;
	}
	
	public InstanceStateEnum valueof(int i){
		for(InstanceStateEnum em:InstanceStateEnum.values()){
			if(em.stateVal==i){
				return em;
			}
		}
		return null;
	}

	public static InstanceStateEnum valueIt(int i){
		if(i == InstanceStateEnum.CRITICAL.stateVal)
			return InstanceStateEnum.CRITICAL;
		else if(i == InstanceStateEnum.WARN.stateVal)
			return InstanceStateEnum.WARN;
		else if(i == InstanceStateEnum.SERIOUS.stateVal)
			return InstanceStateEnum.SERIOUS;
		else if(i == InstanceStateEnum.NORMAL_CRITICAL.stateVal)
			return InstanceStateEnum.NORMAL_CRITICAL;
		else if(i == InstanceStateEnum.NORMAL.stateVal)
			return InstanceStateEnum.NORMAL;
		else
			return null;
	}

	public static boolean isUnknownForIns(InstanceStateEnum state){
		return UNKOWN==state || UNKNOWN_NOTHING==state;
	}
	
	public static boolean isCriticalForIns(InstanceStateEnum state){
		return CRITICAL==state || CRITICAL_NOTHING==state;
	}

	public static String getValue(InstanceStateEnum stateEnum) {
		if(stateEnum == InstanceStateEnum.CRITICAL)
			return "致命";
		else if(stateEnum == InstanceStateEnum.NORMAL)
			return "正常";
		else if(stateEnum == InstanceStateEnum.SERIOUS)
			return "严重";
		else if(stateEnum == InstanceStateEnum.WARN)
			return "警告";
		else
			return stateEnum.name();
	}

	public static InstanceStateEnum metricState2InstState(MetricStateEnum metricStateEnum) {
		switch (metricStateEnum) {
			case WARN:
				return InstanceStateEnum.WARN;
			case NORMAL:
				return InstanceStateEnum.NORMAL;
			case SERIOUS:
				return InstanceStateEnum.SERIOUS;
			case CRITICAL:
				return InstanceStateEnum.CRITICAL;
			case UNKOWN:
				return InstanceStateEnum.NORMAL;
		}
		return null;
	}
}
