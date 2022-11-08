package com.mainsteam.stm.topo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.GroupBo;
import com.mainsteam.stm.topo.dao.IGroupDao;

public class IGroupDaoImpl extends BaseDao<GroupBo> implements IGroupDao{
	private ISequence seq;
	
	@Override
	public void truncateAll() {
		this.getSession().delete(getNamespace()+"truncateAll");
	}

	public IGroupDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IGroupDao.class.getName());
		this.seq=seq;
	}
	@Override
	public void save(List<GroupBo> saveGroups) {
		for(GroupBo gb:saveGroups){
			gb.setId(this.seq.next());
			getSession().insert(getNamespace()+"insert",gb);
		}
	}
	@Override
	public void update(List<GroupBo> updateGroups) {
		for(GroupBo gb:updateGroups){
			getSession().update(getNamespace()+"updateForGraph",gb);
		}
	}
	@Override
	public List<GroupBo> getAll() {
		List<GroupBo> groupList = getSession().selectList(getNamespace()+"getAll");
		return groupList;
	}
	@Override
	public void deleteByIds(List<Long> rgroupList) {
		if(rgroupList.size()>0){
			getSession().delete(getNamespace()+"deleteByIds",rgroupList);
		}
	}
	@Override
	public GroupBo getById(Long gid) {
		return getSession().selectOne(getNamespace()+"getById",gid);
	}
	@Override
	public Object getByIds(List<Long> groupIds) {
		if(groupIds.size()>0){
			return getSession().selectList(getNamespace()+"getByIds",groupIds);
		}else{
			return null;
		}
	}
	@Override
	public List<GroupBo> getBySubTopoId(Long id) {
		Map<String,Long> param = new HashMap<String,Long>();
		if(id==null){
			param.put("id",0l);
		}else{
			param.put("id",id);
		}
		return getSession().selectList(getNamespace()+"getBySubTopoId",param);
	}
	
	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	@Override
	public int save(GroupBo groupBo) {
		if(null == groupBo.getId()) groupBo.setId(this.getSeq().next());
		return this.getSession().update(getNamespace()+"insert", groupBo);
	}
}
