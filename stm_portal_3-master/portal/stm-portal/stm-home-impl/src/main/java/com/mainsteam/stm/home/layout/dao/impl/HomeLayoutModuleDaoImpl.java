package com.mainsteam.stm.home.layout.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: HomeLayoutModuleDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:06:40
 * @author   dengfuwei
 */
public class HomeLayoutModuleDaoImpl extends BaseDao<HomeLayoutModuleBo> implements IHomeLayoutModuleDao {

	public HomeLayoutModuleDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeLayoutModuleDao.class.getName());
	}

	@Override
	public List<HomeLayoutModuleBo> get() {
		return select("get", null);
	}

	@Override
	public HomeLayoutModuleBo getById(long id) {
		return get("getById", id);
	}

	@Override
	public int insert(HomeLayoutModuleBo homeLayoutModuleBo) {
		return insert("insert", homeLayoutModuleBo);
	}

	@Override
	public int update(HomeLayoutModuleBo homeLayoutModuleBo) {
		return update("update", homeLayoutModuleBo);
	}

	@Override
	public int delete(long id) {
		return del("delete", id);
	}

}
