package com.mainsteam.stm.home.layout.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.mainsteam.stm.home.layout.api.HomeLayoutModuleApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleConfigDao;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleDao;

/**
 * <li>文件名称: HomeLayoutModuleImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午4:23:58
 * @author   dengfuwei
 */
public class HomeLayoutModuleImpl implements HomeLayoutModuleApi {
	
	@Resource(name="stm_home_homeLayoutModuleDao")
	private IHomeLayoutModuleDao homeLayoutModuleDao;
	
	@Resource(name="stm_home_homeLayoutModuleConfigDao")
	private IHomeLayoutModuleConfigDao homeLayoutModuleConfigDao;
	
	@Override
	public List<HomeLayoutModuleBo> get() {
		return homeLayoutModuleDao.get();
	}

	@Override
	public HomeLayoutModuleConfigBo query(long id) {
		return homeLayoutModuleConfigDao.getById(id);
	}

	@Override
	public List<HomeLayoutModuleConfigBo> queryByLayoutId(long layoutId) {
		return homeLayoutModuleConfigDao.getByLayoutId(layoutId);
	}

	@Override
	public Long add(HomeLayoutModuleConfigBo configure) {
		return (long)homeLayoutModuleConfigDao.insert(configure);
	}

	@Override
	public void update(HomeLayoutModuleConfigBo configure) {
		homeLayoutModuleConfigDao.updateProps(configure);
		
	}

	@Override
	public void delete(long id) {
		HomeLayoutModuleConfigBo configure = new HomeLayoutModuleConfigBo();
		configure.setId(id);
		homeLayoutModuleConfigDao.delete(configure);
		
	}

	@Override
	public void deleteByLayoutId(long layoutId) {
		HomeLayoutModuleConfigBo configure = new HomeLayoutModuleConfigBo();
		configure.setLayoutId(layoutId);
		homeLayoutModuleConfigDao.delete(configure);
	}
	@Override
	public int updateCurrLayoutId(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		return homeLayoutModuleConfigDao.updateCurrLayoutId(homeLayoutModuleConfigBo);
	}
}
