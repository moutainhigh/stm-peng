package com.mainsteam.stm.platform.sequence.exception;

import com.mainsteam.stm.exception.BaseRuntimeException;

/**
 * <li>文件名称: NoSequenceException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
public class NoSequenceException extends BaseRuntimeException {

	private static final long serialVersionUID = 945625786611862365L;
	
	public NoSequenceException(String seqName) {
		super(601, "没有以 "+seqName+" 命名的序列！");
	}

}


