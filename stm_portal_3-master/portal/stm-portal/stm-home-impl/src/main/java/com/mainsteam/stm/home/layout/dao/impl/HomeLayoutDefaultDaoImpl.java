package com.mainsteam.stm.home.layout.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDefaultDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: HomeLayoutDefaultDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:01:10
 * @author   dengfuwei
 */
public class HomeLayoutDefaultDaoImpl extends BaseDao<HomeLayoutDefaultBo> implements IHomeLayoutDefaultDao {

	public HomeLayoutDefaultDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeLayoutDefaultDao.class.getName());
	}

	@Override
	public HomeLayoutDefaultBo getByUserId(long userId) {
		return get("getByUserId", userId);
	}

	@Override
	public int insert(HomeLayoutDefaultBo homeLayoutDefaultBo) {
		return insert("insert", homeLayoutDefaultBo);
	}

	@Override
	public int update(HomeLayoutDefaultBo homeLayoutDefaultBo) {
		return update("update", homeLayoutDefaultBo);
	}

	@Override
	public int delete(long userId) {
		return del("delete", userId);
	}

}
