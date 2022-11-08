package com.mainsteam.stm.platform.dao;

import com.mainsteam.stm.exception.BaseException;

/**
 * <li>文件名称: DaoAccessException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 数据库访问错误</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月21日
 * @author   ziwenwen
 */
public class DaoAccessException extends BaseException {

	private static final long serialVersionUID = -5298405372028815876L;
	
	public static final int CODE_ERROR_DAO_ACCESS=506;
	
	public DaoAccessException() {
		super(CODE_ERROR_DAO_ACCESS, "数据访问错误！");
	}
	
	public DaoAccessException(String msg) {
		super(CODE_ERROR_DAO_ACCESS,msg);
	}

}


