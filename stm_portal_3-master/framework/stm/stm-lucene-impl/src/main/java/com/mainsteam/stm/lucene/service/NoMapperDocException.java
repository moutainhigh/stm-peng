package com.mainsteam.stm.lucene.service;

import com.mainsteam.stm.exception.BaseRuntimeException;

/**
 * <li>文件名称: NoMapperDocException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class NoMapperDocException extends BaseRuntimeException {
	private static final String msg="没有在lucene.xml中配置doc元素:";
	protected NoMapperDocException(String doc_obj) {
		super(750,msg+doc_obj);
	}
	private static final long serialVersionUID = 6610397506235274084L;
}


