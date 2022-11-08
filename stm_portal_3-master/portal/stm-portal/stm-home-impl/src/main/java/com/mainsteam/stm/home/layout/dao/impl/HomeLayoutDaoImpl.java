package com.mainsteam.stm.home.layout.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDao;
import com.mainsteam.stm.home.layout.vo.HomeLayoutVo;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: HomeLayoutDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午1:17:35
 * @author   dengfuwei
 */
public class HomeLayoutDaoImpl extends BaseDao<HomeLayoutBo> implements IHomeLayoutDao {

	public HomeLayoutDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeLayoutDao.class.getName());
	}

	@Override
	public List<HomeLayoutBo> get(HomeLayoutBo homeLayoutBo) {
		return get(homeLayoutBo);
	}
	

	@Override
	public HomeLayoutBo getOne(HomeLayoutBo homeLayoutBo) {
		return get("get", homeLayoutBo);
	}

	@Override
	public HomeLayoutBo getById(long id) {
		return get("getById", id);
	}

	@Override
	public List<HomeLayoutBo> getByUserId(long userId) {
		return select("getByUserId", userId);
	}

	@Override
	public List<HomeLayoutBo> getTemplateByUserId(long userId) {
		return select("getTemplateByUserId", userId);
	}

	@Override
	public int insert(HomeLayoutBo homeLayoutBo) {
		return insert("insert", homeLayoutBo);
	}

	@Override
	public int updateLayout(HomeLayoutBo homeLayoutBo) {
		return update("updateLayout", homeLayoutBo);
	}

	@Override
	public int updateBaseInfo(HomeLayoutBo homeLayoutBo) {
	
		return update("updateBaseInfo", homeLayoutBo);
	}

	@Override
	public int delete(long id, long userId) {
		HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
		homeLayoutBo.setId(id);
		homeLayoutBo.setUserId(userId);
		return del("delete", homeLayoutBo);
	}

	@Override
	public List<HomeLayoutBo> getTemplates() {
		// TODO Auto-generated method stub
		return getSession().selectList("getTemplates");
	}

	@Override
	public void selectByPage(Page<HomeLayoutBo, HomeLayoutVo> page) {
		this.select("pageSelect", page);
	
		
	}

	@Override
	public int updateLayoutInfo(HomeLayoutBo homeLayoutBo) {
		// TODO Auto-generated method stub
		return update("updateLayoutInfo", homeLayoutBo);
	}

	@Override
	public List<HomeLayoutBo> getTempsById(HomeLayoutVo layoutVo) {
	//System.out.println(domains.size());
		// TODO Auto-generated method stub
	
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("userId", layoutVo.getUserId());
		map.put("domains", layoutVo.getDomainids());
		return this.select("getTempsById",map);
	}

}
