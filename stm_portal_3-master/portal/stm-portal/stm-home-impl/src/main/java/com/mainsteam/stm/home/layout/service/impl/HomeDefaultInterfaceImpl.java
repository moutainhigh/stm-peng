package com.mainsteam.stm.home.layout.service.impl;

import javax.annotation.Resource;

import com.mainsteam.stm.home.layout.api.HomeDefaultInterfaceApi;
import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;
import com.mainsteam.stm.home.layout.dao.IHomeDefaultInterfaceDao;
/**
 * <li>文件名称: HomeLayoutApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017-6-13
 * @author   zhouw
 */
public class HomeDefaultInterfaceImpl implements HomeDefaultInterfaceApi{
	@Resource(name="stm_home_homeDefaultInterfaceDao")
	private IHomeDefaultInterfaceDao homeDefaultInterfaceDao;
	
	@Override
	public HomeDefaultInterfaceBo getByUserIdAndResourceId(
			HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return homeDefaultInterfaceDao.getByUserIdAndResourceId(homeDefaultInterfaceBo);
	}

	@Override
	public int updateByUserIdAndResourceId(
			HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return homeDefaultInterfaceDao.updateByUserIdAndResourceId(homeDefaultInterfaceBo);
	}

	@Override
	public int insert(HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return homeDefaultInterfaceDao.insert(homeDefaultInterfaceBo);
	}

	@Override
	public int delete(HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return homeDefaultInterfaceDao.delete(homeDefaultInterfaceBo);
	}

}
