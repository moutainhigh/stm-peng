package com.mainsteam.stm.home.layout.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutDomainBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDomainDao;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.system.um.domain.bo.Domain;

/**
 * <li>文件名称: HomeLayoutDomainDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:02:56
 * @author   dengfuwei
 */
public class HomeLayoutDomainDaoImpl extends BaseDao<HomeLayoutDomainBo> implements IHomeLayoutDomainDao {

	public HomeLayoutDomainDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeLayoutDomainDao.class.getName());
	}

	@Override
	public List<HomeLayoutDomainBo> get(HomeLayoutDomainBo homeLayoutDomainBo) {
		return select("get", homeLayoutDomainBo);
	}

	@Override
	public int insert(HomeLayoutDomainBo homeLayoutDomainBo) {
		return insert("insert", homeLayoutDomainBo);
	}

	@Override
	public int delete(long userId, long layoutId) {
		HomeLayoutDomainBo homeLayoutDomainBo = new HomeLayoutDomainBo();
		homeLayoutDomainBo.setUserId(userId);
		homeLayoutDomainBo.setLayoutId(layoutId);
		return del("delete", homeLayoutDomainBo);
	}

	@Override
	public List<Domain> getDomainByLayoutId(long layoutId, String content) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("layoutId", layoutId);
		map.put("content", content);
		return getSession().selectList(getNamespace()+"getDomainByLayoutId", map);
	}

	@Override
	public List<Domain> getUnDomainByLayoutId(long layoutId, String content) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("layoutId", layoutId);
		map.put("content", content);
		return getSession().selectList(getNamespace()+"getUnDomainByLayoutId", map);
	}

}
