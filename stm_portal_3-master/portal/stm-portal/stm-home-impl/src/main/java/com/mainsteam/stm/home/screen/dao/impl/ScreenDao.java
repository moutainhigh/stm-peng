package com.mainsteam.stm.home.screen.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.home.screen.dao.IScreenDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: ScreenDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月20日
 * @author   ziwenwen
 */
@Repository
public class ScreenDao extends BaseDao<Biz> implements IScreenDao {

	@Autowired
	public ScreenDao(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IScreenDao.class.getName());
	}

	@Override
	public List<Biz> getUserBizRels(Long userId) {
		return select("getUserBizRels", userId);
	}

	@Override
	public Biz getBiz(long id) {
		// TODO Auto-generated method stub
		return get("getBizByid", id);
	}
}


