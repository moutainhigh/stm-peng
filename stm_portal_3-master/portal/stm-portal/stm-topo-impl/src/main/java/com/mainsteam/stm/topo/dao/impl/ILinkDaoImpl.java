package com.mainsteam.stm.topo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.GroupBo;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.dao.ILinkDao;

public class ILinkDaoImpl extends BaseDao<LinkBo> implements ILinkDao{
	private ISequence seq;
	
	@Override
	public List<LinkBo> getLinksByIds(List<Long> ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ids", ids);
		return this.select("selectByLinkIds", map);
	}

	@Override
	public List<Long> getDelLinkInstancdeIdsByNodeIds(List<Long> nodeIds) {
		if(null==nodeIds || nodeIds.isEmpty()){
			return new ArrayList<Long>();
		}
		return getSession().selectList(getNamespace()+"getDelLinkInstancdeIdsByNodeIds",nodeIds);
	}
	
	@Override
	public LinkBo selectMultiByInstanceId(Long instanceId) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("instanceId", instanceId);
		List<LinkBo> links = this.select("selectMultiByInstanceId",map);
		if(null != links && links.size() >0){
			return links.get(0);
		}else{
			return null;
		}
	}
	
	@Override
	public List<LinkBo> getLinksByFromToId(Long fromNodeId, Long toNodeId) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("fromNodeId", fromNodeId);
		map.put("toNodeId", toNodeId);
		return this.select("selectByFromToId", map);
	}

	@Override
	public void selectMutilLinkByPage(Page<LinkBo, LinkBo> page) {
		this.select("mutilLinkPageSelect", page);
	}
	
	@Override
	public List<LinkBo> getLinksByInstanceIds(long[] instanceIds){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("instanceIds", instanceIds);
		return this.select("selectByInstanceIds", map);
	}
	
	@Override
	public void selectByPage(Page<LinkBo, LinkBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	@Override
	public List<LinkBo> getAllLinkInstances() {
		return getSession().selectList(getNamespace() + "getAllLinkInstances");
	}
	
	public ILinkDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, ILinkDao.class.getName());
		this.seq=seq;
	}
	@Override
	public List<LinkBo> getAll() {
		List<LinkBo> results = getSession().selectList(getNamespace()+"getAll");
		return results;
	}
	@Override
	public void save(List<LinkBo> saveLinks) {
		for(LinkBo lb:saveLinks){
			Long from=lb.getFrom();
			Long to=lb.getTo();
			//不能连接自己
			if(from!=null&&to!=null&&!from.equals(to)){
				lb.setId(this.seq.next());
				getSession().insert(getNamespace()+"insert",lb);
			}
		}
	}
	@Override
	public void update(List<LinkBo> updateLinks) {
		for(LinkBo lb:updateLinks){
			getSession().update(getNamespace()+"updateForGraph",lb);
		}
	}
	@Override
	public void delete(Long id) {
		if(null!=id && id>0){
			getSession().delete(getNamespace()+"deleteById",id);
		}
	}
	@Override
	public void deleteByIds(List<Long> rlinkList) {
		if(rlinkList.size()>0){
			getSession().delete(getNamespace()+"deleteByIds",rlinkList);
		}
	}
	@Override
	public LinkBo getById(Long id) {
		if(null != id){
			return getSession().selectOne(getNamespace()+"getById",id);
		}else{
			throw new RuntimeException("query id can't be null");
		}
	}
	@Override
	public void copyLink(Long srcId, Long[] ids) {
	}
	@Override
	public List<LinkBo> getLinksByNode(List<NodeBo> nbs) {
		if(nbs.size()<=0) return new ArrayList<LinkBo>();
		return getSession().selectList(getNamespace()+"getLinksByNode",nbs);
	}
	@Override
	public List<Long> getLinksIdByNode(NodeBo node) {
		return getSession().selectList(getNamespace()+"getLinksIdByNode",node);
	}
	@Override
	public List<Long> getLinkInstancesIdByNodeIds(List<Long> nodeIds) {
		if(null==nodeIds || nodeIds.isEmpty()){
			return new ArrayList<Long>();
		}
		return getSession().selectList(getNamespace()+"getLinkInstancesIdByNodeIds",nodeIds);
	}
	@Override
	public List<LinkBo> getLinksByOthersNode(List<OtherNodeBo> others) {
		if(others.size()<=0) return new ArrayList<LinkBo>();
		return getSession().selectList(getNamespace()+"getLinksByOthersNode",others);
	}
	@Override
	public void updateInstanceId(LinkBo lb) {
		getSession().update(getNamespace()+"updateInstanceId",lb);
	}
	@Override
	public int updateLinkRelationOnResourceDelete(List<Long> instanceIds) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ids", instanceIds);
		return getSession().update(getNamespace() + "updateLinkRelationOnResourceDelete", paramMap);
	}

	@Override
	public List<LinkBo> getLinksByGroups(List<GroupBo> groups) {
		if(groups==null || groups.isEmpty()){
			return new ArrayList<LinkBo>();
		}else{
			return getSession().selectList(getNamespace()+"getLinksByGroups",groups);
		}
	}

	@Override
	public void deleteByNodeIds(List<Long> ids,boolean isPhysicalDelete) {
		if(ids==null||ids.isEmpty()) return;
		if(isPhysicalDelete){
			getSession().delete(getNamespace()+"physicalDeleteByNodeIds",ids);
		}else{
			getSession().update(getNamespace()+"logicalDeleteByNodeIds",ids);
		}
	}

	@Override
	public List<LinkBo> findLink(Long from, Long to) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("from", from);
		param.put("to", to);
		return getSession().selectList(getNamespace()+"findLink",param);
	}

	@Override
	public List<LinkBo> findLink(LinkBo lb) {
		if(null!=lb.getFrom()&&null!=lb.getTo()){
			return findLink(lb.getFrom(),lb.getTo());
		}
		return new ArrayList<LinkBo>();
	}
	@Override
	public int updateAttr(LinkBo link) {
		return getSession().update(getNamespace()+"updateAttr",link);
	}
	
	@Override
	public ISequence getSeq() {
		return this.seq;
	}
	
	@Override
	public int save(LinkBo linkBo) {
		if(null == linkBo.getId()) linkBo.setId(this.getSeq().next());
		int c=0;
		try {
			c = this.getSession().update(getNamespace()+"insert", linkBo);
		} catch (Exception e) {
			c=0;
		}
		return c;
	}
	
	@Override
	public List<LinkBo> getByIds(Long[] ids) {
		if(ids!=null && ids.length>0){
			Map<String,Object[]> param = new HashMap<String,Object[]>(1);
			param.put("ids", ids);
			return getSession().selectList(getNamespace()+"getByIds", param);
		}else{
			return new ArrayList<LinkBo>();
		}
	}
	@Override
	public List<LinkBo> findLinkByParentId(Long id) {
		if(id!=null){
			Map<String,Long> param = new HashMap<String,Long>(1);
			param.put("parentId", id);
			return getSession().selectList(getNamespace()+"findLinkByParentId", param);
		}
		return new ArrayList<LinkBo>();
	}
	@Override
	public void transformLinkToLineById(Long id) {
		getSession().update(getNamespace()+"transformLinkToLineById",id);
	}
	@Override
	public List<LinkBo> getSingleInterfaceLinkInstances() {
		return getSession().selectList(getNamespace() + "getSingleInterfaceLinkInstances");
	}
	@Override
	public List<LinkBo> getSingleInterfaceLine() {
		return getSession().selectList(getNamespace() + "getSingleInterfaceLine");
	}
}
