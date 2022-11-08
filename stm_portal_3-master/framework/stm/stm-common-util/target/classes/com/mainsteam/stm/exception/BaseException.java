package com.mainsteam.stm.exception;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.util.IConstant;
import com.mainsteam.stm.util.ResponseUtil;

/**
 * <li>文件名称: BaseException.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
public class BaseException extends Exception implements Serializable, IConstant {

	private static final long serialVersionUID = 5215415795593713563L;

	/**
	 * 请求数据格式验证错误
	 */
	public static final int CODE_ERROR_VALIDATION = 504;

	/**
	 * 没有登录
	 */
	public static final int CODE_ERROR_AUTHORITY_NO_LOGIN = 505;

	/**
	 * 没有访问权限
	 */
	public static final int CODE_ERROR_AUTHORITY_NO_ACCESS = 506;

	protected int code;

	/**
	 * @param code
	 *            可能发送到客户端的异常代码，子异常类中自定义值 <br/>
	 *            400-499 url地址、参数匹配、请求数据格式等错误<br/>
	 *            500-599 程序错误<br/>
	 *            600-699 系统配置错误 700-799 server模块自定义异常的代码取值范围 800-899
	 *            capacity模块自定义异常的代码取值范围 900-999 portal模块自定义异常的代码取值范围 其他详情见
	 *            {@link ResponseUtil}
	 * @param msg
	 *            可能发送到客户端的异常消息
	 */
	protected BaseException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public BaseException(BaseException cause) {
		super(cause);
		this.code = cause.code;
	}

	public BaseException(String message, BaseException cause) {
		super(message, cause);
		this.code = cause.code;
	}

	public BaseException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public BaseException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public String toString() {
		return "oc-c-e: " + toJsonObject();
	}

	public int getCode() {
		return this.code;
	}

	public JSONObject toJsonObject() {
		return ResponseUtil.toJsonObject(this.getCode(), this.getMessage());
	}
}
