package com.mainsteam.stm.webService.cmdb.server;

/**
 * User: kevins
 * Date:2012-12-07
 * Time: 17-05
 */
public class ResultCode {
	public static final String SUCCESS="0000";
    //内部未知错误
    public static final String FAILURE="9910";
    //必须指定MO的ID
    public static final String POINT_MO="0012";
    //指定的MO不存在
    public static final String NON_EXISTENT="0014";
    //关系类型和管理对象类型不能同时为空
    public static final String RELATIONSHIP_AND_OBJECT_NOT_NULL = "0025";
    //表示第三方系統沒有配置OC4的url
    public static final String NO_CONFIG = "9999";
}
