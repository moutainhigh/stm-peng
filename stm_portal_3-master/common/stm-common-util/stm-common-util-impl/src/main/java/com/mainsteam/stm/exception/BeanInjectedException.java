package com.mainsteam.stm.exception;

/**
 * <li>文件名称: SqlSessionIsNotInjected.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月21日
 * @author   ziwenwen
 */
public class BeanInjectedException extends BaseRuntimeException {
	private static final long serialVersionUID = -3200839778895234950L;

	public static final String CODE_ERROR_INJECTED_MSG="bean注入错误！";
	
	public BeanInjectedException(String msg) {
		super(507, msg);
	}
	public BeanInjectedException() {
		super(507,CODE_ERROR_INJECTED_MSG);
	}
}


