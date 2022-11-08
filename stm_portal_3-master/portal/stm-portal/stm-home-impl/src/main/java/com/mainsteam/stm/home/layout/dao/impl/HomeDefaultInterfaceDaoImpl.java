package com.mainsteam.stm.home.layout.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;
import com.mainsteam.stm.home.layout.dao.IHomeDefaultInterfaceDao;
import com.mainsteam.stm.platform.dao.BaseDao;
/**
 * <li>文件名称: HomeDefaultInterfaceDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017-6-13
 * @author   zhouw
 */
public class HomeDefaultInterfaceDaoImpl extends BaseDao<HomeDefaultInterfaceBo> implements IHomeDefaultInterfaceDao{

	public HomeDefaultInterfaceDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeDefaultInterfaceDao.class.getName());
	}

	@Override
	public HomeDefaultInterfaceBo getByUserIdAndResourceId(
			HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return get("getByUserIdAndResourceId", homeDefaultInterfaceBo);
	}

	@Override
	public int updateByUserIdAndResourceId(
			HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return update("updateByUserIdAndResourceId", homeDefaultInterfaceBo);
	}

	@Override
	public int insert(HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return insert("insert", homeDefaultInterfaceBo);
	}

	@Override
	public int delete(HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		return del("delete", homeDefaultInterfaceBo);
	}

}
