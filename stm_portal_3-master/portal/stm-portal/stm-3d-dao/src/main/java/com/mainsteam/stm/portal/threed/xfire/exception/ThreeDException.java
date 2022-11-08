package com.mainsteam.stm.portal.threed.xfire.exception;
/**
 * 
 * <li>文件名称: ThreeDException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 3D webservice相关异常</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月1日
 * @author   liupeng
 */
public class ThreeDException extends Exception{

	private static final long serialVersionUID = 1032194542342502905L;

	public ThreeDException() {
		super();
	}

	public ThreeDException(String message, Throwable cause) {
		super(message, cause);
	}

	public ThreeDException(String message) {
		super(message);
	}

	public ThreeDException(Throwable cause) {
		super(cause);
	}
	
	
}
