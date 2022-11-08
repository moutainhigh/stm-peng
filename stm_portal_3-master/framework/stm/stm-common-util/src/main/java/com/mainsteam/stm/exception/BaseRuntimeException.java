package com.mainsteam.stm.exception;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.util.IConstant;
import com.mainsteam.stm.util.ResponseUtil;

/**
 * <li>文件名称: BaseRuntimeException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月11日
 * @author   ziwenwen
 */
public class BaseRuntimeException extends RuntimeException implements Serializable,IConstant{
	
	private static final long serialVersionUID = 5215415795593713563L;
	
	protected int code;
	
	public BaseRuntimeException(Throwable cause) {
		super(cause);
	}

	public BaseRuntimeException(String message, Throwable cause) {
		super(message,cause);
	}

	/**
	 * @param code 
	 * 可能发送到客户端的异常代码，子异常类中自定义值 <br/>
	 * 400-499 url地址、参数匹配、请求数据格式等错误<br/>
	 * 500-599 程序错误<br/>
	 * 600-699 系统配置错误
	 * 700-799 server模块自定义异常的代码取值范围
	 * 800-899 capacity模块自定义异常的代码取值范围
	 * 900-999 portal模块自定义异常的代码取值范围
	 * 其他详情见 {@link ResponseUtil}
	 * @param msg 可能发送到客户端的异常消息
	 */
	protected BaseRuntimeException(int code,String msg) {
		super(msg);
		this.code=code;
	}
	
	public String toString(){
		return "oc-r-e: "+toJsonObject();
	}
	
	public int getCode(){
		return this.code;
	}
	
	public JSONObject toJsonObject(){
		return ResponseUtil.toJsonObject(this.getCode(), this.getMessage());
	}
}
