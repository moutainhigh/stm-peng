package com.mainsteam.stm.util;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.util.IConstant;

/**
 * <li>文件名称: ResponseUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 用于统一定义一些代码值范围，如异常码、响应代码等，同时提供一些响应消息的封装处理方法，</li>
 * <li>其他说明: </li>
 * <pre>
 * 响应代码 
 * 200-299 没有错误 正常业务逻辑返回代码,该段响应代码在前端通常需要业务开发人员处理
 * <strong>所有action中返回结果的code取值范围为 200-299 否则可能会造成未知错误</strong>
 * 300-399 预留代码段
 * 400-499 url地址等资源错误代码段
 * 500-599 系统框架级异常封装代码，如ConfigException代码为CODE_ERROR_CONFIG
 * 600-999 各个业务模块自定义异常的代码取值范围
 * 1000-1099 使用第三方系统接口错误代码段
 * </pre>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class ResponseUtil implements IConstant{

	/**
	 * 业务处理成功
	 */
	public static final int CODE_SUCCESS=200;
	
	public static final int CODE_SUCCESS_GOURPNAMEEXSIT = 201;
	
	public static final int CODE_EEROR_LICENSECOUNTOVERFLOW = 202;
	
	/**
	 * 包装处理结构，用于返回给客户端
	 * @param code 响应码
	 * <pre>
	 * 200-299 没有程序错误或异常 正常业务逻辑返回代码,该段响应代码在前端通常需要业务开发人员处理
	 * <strong>所有action中返回结果的code取值范围为 200-299 否则可能会造成未知错误</strong>
	 * 300-399 预留代码段
	 * 400-499 url地址等资源错误代码段 400没有登录或者登录失效
	 * 500-599 系统框架级异常封装代码，如ConfigException代码为CODE_ERROR_CONFIG
	 * 600-699 系统配置错误
	 * 700-799 server模块自定义异常的代码取值范围
	 * 800-899 capacity模块自定义异常的代码取值范围
	 * 900-999 portal模块自定义异常的代码取值范围
	 * 1000-1099 使用第三方系统接口错误代码段
	 * </pre>
	 * @param msg 响应消息
	 * @return
	 */
	public static JSONObject toJsonObject(int code,Object msg){
		JSONObject json=new JSONObject();
		json.put(str_code,code);
		json.put(str_data,msg);
		return json;
	}
	
	/**
	 * 返回处理成功的消息，响应码为200
	 * @param msg
	 * @return
	 * @see ResponseUtil
	 */
	public static JSONObject toSuccess(Object msg){
		return toJsonObject(CODE_SUCCESS, msg);
	}
	
	/**
	 * 返回处理失败的消息，自定义资源组名称已存在，响应码为901
	 * @param msg
	 * @return
	 * @see ResponseUtil
	 */
	public static JSONObject toFailForGroupNameExsit(Object msg){
		return toJsonObject(CODE_SUCCESS_GOURPNAMEEXSIT, msg);
	}
	
	/**
	 * 返回处理失败的消息，指定模块license数量超出，响应码为202
	 * @param msg
	 * @return
	 * @see ResponseUtil
	 */
	public static JSONObject toFailForLicenseOverFlow(Object msg){
		return toJsonObject(CODE_EEROR_LICENSECOUNTOVERFLOW, msg);
	}
	
}


