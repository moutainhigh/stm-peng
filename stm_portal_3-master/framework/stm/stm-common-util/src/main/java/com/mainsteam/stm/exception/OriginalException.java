package com.mainsteam.stm.exception;

/**
 * <li>文件名称: OriginalException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class OriginalException extends BaseRuntimeException {

	private static final long serialVersionUID = 6157257238292634466L;
	private static final String _msg="对不属于本系统封装异常的其他异常的封装-";

	public OriginalException(Throwable t){
		super(502, _msg+t.getMessage());
		this.addSuppressed(t);
	}
}


