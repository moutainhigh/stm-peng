package com.mainsteam.stm.home.layout.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleConfigDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: HomeLayoutModuleConfigDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:05:10
 * @author   dengfuwei
 */
public class HomeLayoutModuleConfigDaoImpl extends BaseDao<HomeLayoutModuleConfigBo> implements IHomeLayoutModuleConfigDao {

	public HomeLayoutModuleConfigDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeLayoutModuleConfigDao.class.getName());
	}

	@Override
	public List<HomeLayoutModuleConfigBo> getByLayoutId(long layoutId) {
		return select("getByLayoutId", layoutId);
	}

	@Override
	public int insert(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		return insert("insert", homeLayoutModuleConfigBo);
	}

	@Override
	public int updateProps(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		return update("updateProps", homeLayoutModuleConfigBo);
	}

	@Override
	public int delete(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		return del("delete", homeLayoutModuleConfigBo);
	}

	@Override
	public HomeLayoutModuleConfigBo getById(long id) {
		return get("getById", id);
	}
	
	@Override
	public int updateCurrLayoutId(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		return update("updateCurrLayoutId", homeLayoutModuleConfigBo);
	}

}
