package com.mainsteam.stm.platform.dict.service.impl;

import java.util.List;

import com.mainsteam.stm.platform.dict.api.IDictApi;
import com.mainsteam.stm.platform.dict.bo.Dict;
import com.mainsteam.stm.platform.dict.dao.IDictDao;

/**
 * <li>文件名称: DictImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月8日
 * @author   ziwenwen
 */
public class DictImpl implements IDictApi {

	private IDictDao dictDao;
	
	@Override
	public List<Dict> get(String type) {
		return dictDao.getByType(type);
	}

	public void setDictDao(IDictDao dictDao) {
		this.dictDao = dictDao;
	}

	@Override
	public void insert() {
		System.out.println(1/0);
	}
}
