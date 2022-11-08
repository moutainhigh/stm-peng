package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsResourceMain;
import com.mainsteam.stm.ct.dao.ResourceMapper;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ResourceMapperImpl extends BaseDao<MsResourceMain> implements ResourceMapper {

	public ResourceMapperImpl(SqlSessionTemplate session) {
		super(session, ResourceMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getResourceList(Page<MsResourceMain, MsResourceMain> page) {
		// TODO Auto-generated method stub
		this.select("getResourceList",page);
	}

	@Override
	public MsResourceMain getFullResource(MsResourceMain msResourceParams) {
		// TODO Auto-generated method stub
		
		
		return super.getSession().selectOne("getFullResource", msResourceParams);
	}

	@Override
	public List<String> getResourceIdList(MsResourceMain msResourceParams) {
		// TODO Auto-generated method stub
		return this.getSession().selectList("getResourceIdList", msResourceParams);
	}
	@Override
	public int insert(MsResourceMain msResourceParams){
		return super.insert(msResourceParams);
	}
	
	@Override
	public int updateById(MsResourceMain msResourceParams) {
		// TODO Auto-generated method stub
		return super.update(msResourceParams);
	}

	@Override
	public int removeById(String id) {
		// TODO Auto-generated method stub
		return super.del("del", id);
	}

	@Override
	public int removeByIds(Long[] temps) {
		// TODO Auto-generated method stub
		
		return super.batchDel(temps);
	}

	@Override
	public MsResourceMain getById(String id) {
		// TODO Auto-generated method stub
		return super.getSession().selectOne("getOne", id);
	}

	@Override
	public List<MsResourceMain> getResourceIdAndTestName(String testWay) {
		// TODO Auto-generated method stub
		return this.getSession().selectList("getResourceIdAndTestName", testWay);
	}

	@Override
	public int success(String id) {
		// TODO Auto-generated method stub
		return this.getSession().update("success", id);
	}
	@Override
	public int fail(String id) {
		// TODO Auto-generated method stub
		return this.getSession().update("fail", id);
	}

}
