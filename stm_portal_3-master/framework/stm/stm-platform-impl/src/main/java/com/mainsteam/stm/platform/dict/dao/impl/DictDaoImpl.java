package com.mainsteam.stm.platform.dict.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.dict.bo.Dict;
import com.mainsteam.stm.platform.dict.dao.IDictDao;

/**
 * <li>文件名称: DictDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月11日
 * @author   ziwenwen
 */
public class DictDaoImpl extends BaseDao<Dict> implements IDictDao {

	public DictDaoImpl(SqlSessionTemplate session) {
		super(session, IDictDao.class.getName());
	}

	@Override
	public List<Dict> getByType(String type) {
		return select("getByType", type);
	}
}


