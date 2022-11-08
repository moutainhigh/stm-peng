package com.mainsteam.stm.webService.obj;

public enum ResultCodeEnum {
	RESULT_NORMAL_CODE("查询正常","0000"),
	RESULT_NULL_CODE("无匹配值","0001"),
	RESULT_SUCCESS_CODE("执行成功","0003"),
	/* 
	 * 系统异常
	 * */
	RESULT_ERROR_CODE("系统内部错误","9910"),
	
	RESULT_OverAlarmCount_CODE("超过告警数量最大值","0101"),
	
	RESULT_ParameterError_CODE("参数错误","0102"),
	
	RESULT_QueryResourceError_CODE("查询资源时发生异常！","0103"),
	
	RESULT_QueryResourceStateError_CODE("查询资源状态时发生异常！","0104"),
	
	RESULT_QueryResourceProfileError_CODE("查询资源策略时发生异常！","0105"),
	
	RESULT_QueryMetricInfoError_CODE("查询资源指标信息时发生异常！","0106"),
	
	RESULT_ParameterNumError_CODE("参数数量过多","0107"),
	
	RESULT_QueryResourceNullError_CODE("未获取到资源！","0108"),
	/* 
	 * 模块
	 * */
	RESULT_DateFormatError_CODE("字符转换为日期时出错","1001"),
	
	RESULT_QueryMainResourceError_CODE("查询主资源时发生异常！","1002"),
	
	RESULT_QueryResourceMetricValueError_CODE("查询资源指标值时发生异常！","1003"),
	
	RESULT_IPError_CODE("传入的ip不合法！","1004"),
	
	RESULT_QueryResourceByResourceIdError_CODE("根据资源id查询主资源时发生异常！","1005"),
	
	RESULT_QueryResourceByCategoryIdError_CODE("根据资源类型id查询主资源时发生异常！","1006"),
	
	RESULT_QueryChildResourceByInstanceIdError_CODE("根据资源实例id查询子资源时发生异常！","1007"),
	
	RESULT_QueryResourceByInstanceIdError_CODE("根据资源实例id查询资源时发生异常！","1008"),
	
	RESULT_QueryChildResourceMetricValueError_CODE("查询子资源指标值时发生异常！","1009"),
	
	RESULT_QueryEndDateBeforeStartDateError_CODE("查询结束日期在开始日期之前！","1010"),
	
	RESULT_NoMatchTypeError_CODE("没有匹配的类型！","1011"),
	
	RESULT_QUERYISNOTSWITCH_CODE("该资源不是交换机资源！","1012"),
	
	RESULT_QUERYISNOTINTERFACE_CODE("该资源不是交换机端口子资源！","1013"),
	
	RESULT_SMS_PhoneNumOrContentError_CODE("请检查电话号码或短信内容！","1014"),
	
	RESULT_ParentCatagoryIdNull_CODE("主类型不能为空!","1015"),
	
	RESULT_ParentAndChildrenCatagoryIdMissMacth_CODE("主类型子类型不能匹配!","1016"),
	
	RESULT_ParentCatagoryIdError_CODE("主类型错误!","1017"),
	
	RESULT_QueryResourceByCategoryIDError_CODE("根据模型ID获取资源发生异常！","1018")
	;
	
	// 成员变量  
	private String resultDecp;  
	private String resultCode;  
    
	private ResultCodeEnum(String resultDecp,String resultCode) {
		this.resultDecp = resultDecp;
		this.resultCode = resultCode;
	}
	
	public static ResultCodeEnum getByResultCode(String resultCode){
    	
		ResultCodeEnum[] resultCodeEnums=values();
    	for(ResultCodeEnum resultCodeEnum:resultCodeEnums){
    		if(resultCode.equals(resultCodeEnum.getResultCode())){
    			return resultCodeEnum;
    		}
    	}
    	return null;
    }

	public String getResultDecp() {
		return resultDecp;
	}

	public void setResultDecp(String resultDecp) {
		this.resultDecp = resultDecp;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
}
