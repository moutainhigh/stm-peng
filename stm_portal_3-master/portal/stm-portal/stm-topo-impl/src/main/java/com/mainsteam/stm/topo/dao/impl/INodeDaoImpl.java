package com.mainsteam.stm.topo.dao.impl;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.QueryNode;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.enums.TopoType;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class INodeDaoImpl extends BaseDao<NodeBo> implements INodeDao {
	private ISequence seq;
	
	@Override
	public void updatePelSize(NodeBo node) {
		this.getSession().update(getNamespace() + "updatePelSize",node);
	}

	@Override
	public List<NodeBo> matchedIp(String ip) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("ip", ip);
		return this.getSession().selectList(getNamespace()+"matchedIp", param);
	}
	@Override
	public void selectByPage(Page<NodeBo, NodeBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	@Override
	public NodeBo getByInstanceId(Long instanceId) {
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("instanceId", instanceId);
		List<NodeBo> nbs = getSession().selectList(getNamespace()+"getByInstanceId",param);
		if(!nbs.isEmpty()){
			return nbs.get(0);
		}else{
			return null;
		}
	}
	@Override
	public List<NodeBo> getByInstanceId(Long instanceId, Long topoId) {
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("instanceId", instanceId);
		param.put("topoId", topoId);
		List<NodeBo> nbs = getSession().selectList(getNamespace()+"getByInstanceIdInSubtopo",param);
		return nbs;
	}
	@Override
	public List<Long> getNodeIdByInstanceIds(List<Long> ids) {
		if(ids==null || ids.isEmpty()){
			return new ArrayList<Long>();
		}
		return getSession().selectList(getNamespace() + "getNodeIdByInstanceIds",ids);
	}
	@Override
	public List<NodeBo> getAllInstances() {
		return getSession().selectList(getNamespace() + "getAllInstances");
	}

	@Override
	public List<Long> getAllInstanceIds() {
		return getSession().selectList(getNamespace() + "getAllInstanceIds");
	}
	
	
	@Override
	public ISequence getSeq() {
		return this.seq;
	}

	public INodeDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, INodeDao.class.getName());
		this.seq=seq;
	}
	@Override
	public List<NodeBo> getAll() {
		List<NodeBo> results = getSession().selectList(getNamespace() + "getAll");
		return results;
	}
	@Override
	public void update(List<NodeBo> updateNodes) {
		for(NodeBo nb:updateNodes){
			getSession().update(getNamespace() + "updateForGraph",nb);
		}
	}
	@Override
	public void save(List<NodeBo> saveNodes) {
		for(NodeBo nb : saveNodes){
			nb.setId(this.seq.next());
			getSession().insert(getNamespace() + "insert",nb);
		}
	}
	@Override
	public void updateGroupId(Long groupId, List<Long> children) {
		if(null!=groupId && children.size()>0){
			getSession().update(getNamespace() + "clearGroup",groupId);
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("groupId", groupId);
			params.put("ids", children);
			getSession().update(getNamespace() + "addGroup",params);
		}
	}
	@Override
	public List<Long> getGroupNodes(Long groupId) {
		List<Long> retn = getSession().selectList(getNamespace() + "groupNodes",groupId);
		return retn;
	}
	@Override
	public void deleteByIds(List<Long> rnodeList,boolean isPhysicalDelete) {
		if(rnodeList.size()>0){
			if(isPhysicalDelete){
				getSession().delete(getNamespace() + "physicalDeleteNodeByIds", rnodeList);
				getSession().delete(getNamespace() + "physicalDeleteLinkByIds", rnodeList);
			}else{
				getSession().update(getNamespace() + "logicalDeleteNodeByIds", rnodeList);
				getSession().update(getNamespace() + "logicalDeleteLinkByIds", rnodeList);
			}
		}
	}
	@Override
	public ISequence getSequence() {
		return this.seq;
	}
	@Override
	public Long getIdByDeviceId(String deviceId) {
		if(null != deviceId){
			return getSession().selectOne(getNamespace() + "getIdByDeviceId", deviceId);
		}
		return null;
	}
	@Override
	public void replaceIcon(Long id, String src) {
		if(null != id && null != src){
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("id", id);
			param.put("src", src);
			getSession().update(getNamespace() + "replaceIcon", param);
		}else{
			throw new RuntimeException("replaceIcon 参数  id 和  src 不能为空");
		}
	}
	@Override
	public List<String> selectIps() {
		return getSession().selectList(getNamespace() + "selectIps");
	}
	@Override
	public NodeBo getById(Long id) {
		if(null != id){
			return getSession().selectOne(getNamespace() + "selectById",id);
		}else{
			throw new RuntimeException("query id can't be null");
		}
	}
	@Override
	public List<NodeBo> getDeviceInOrder(Page<NodeBo, NodeBo> page) {
		Map<String,String> param = new HashMap<String,String>();
		param.put("sort", page.getSort());
		param.put("order", page.getOrder());
		return getSession().selectList(getNamespace() + "getDeviceInOrder",param);
	}
	@Override
	public List<NodeBo> query(QueryNode query) {
		return getSession().selectList(getNamespace() + "query",query);
	}
	@Override
	public int updateInstanceId(NodeBo node) {
		return getSession().update(getNamespace() + "updateInstanceId",node);
	}
	@Override
	public List<NodeBo> getByIds(List<Long> ids) {
		if(!ids.isEmpty()){
			return getSession().selectList(getNamespace() + "getByIds",ids);
		}else{
			return new ArrayList<NodeBo>();
		}
	}
	@Override
	public List<NodeBo> getBySubTopoId(Long id) {
		if(id!=null && 0l==id){
			id=null;
		}
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("id", id);
		return getSession().selectList(getNamespace()+"getBySubTopoId",param);
	}
	@Override
	public NodeBo getByDeviceId(String deviceId) {
		if(null != deviceId){
			return getSession().selectOne(getNamespace()+"getByDeviceId",deviceId);
		}
		return null;
	}
	@Override
	public List<NodeBo> getByIp(String ip,Long subTopoId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("ip",ip);
		if(subTopoId!=null && subTopoId==TopoType.SECOND_TOPO.getId()){
			subTopoId=null;
		}
		param.put("subTopoId",subTopoId);
		return getSession().selectList(getNamespace()+"getByIp",param);
	}

	@Override
	public boolean hasTopo() {
		long count = getSession().selectOne(getNamespace()+"hasTopo");
		return count>0;
	}
	@Override
	public NodeBo getFortopoFindByIp(String ip) {
		Map<String,String> param = new HashMap<String,String>();
		param.put("ip",ip);
		List<NodeBo> nodes = getSession().selectList(getNamespace()+"getFortopoFindByIp",param);
		if(nodes.isEmpty()){
			return null;
		}else{
			return nodes.get(0);
		}
	}

	@Override
	public List<NodeBo> queryByIps(ArrayList<String> ips,Long subTopoId) {
		if(null!=ips && !ips.isEmpty()){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("ips", ips);
			params.put("subTopoId", subTopoId);
			List<NodeBo> nodes = getSession().selectList(getNamespace()+"queryByIps",params);
			return nodes;
		}else{
			return null;
		}
	}
	
	@Override
	public int updateNodeRelationOnResourceDelete(List<Long> instanceIds) {
		if(instanceIds==null || instanceIds.isEmpty()){
			return 0;
		}else{
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ids", instanceIds);
			return getSession().update(getNamespace() + "updateNodeRelationOnResourceDelete", paramMap);
		}
	}

	@Override
	public NodeBo getByInstanceIdOrIpForSubtopo(Long topoId, Long instanceId,String ip) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("topoId", topoId);
		paramMap.put("instanceId", instanceId);
		paramMap.put("ip", ip);
		List<NodeBo> nbs = getSession().selectList(getNamespace() + "getByInstanceIdOrIpForSubtopo", paramMap);
		if(nbs.isEmpty()){
			return null;
		}else{
			return nbs.get(0);
		}
	}
	
	@Override
	public List<NodeBo> getCoreNodesById(Long id,Long subTopoId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		paramMap.put("subTopoId",subTopoId);
		List<NodeBo> nbs = getSession().selectList(getNamespace()+"getCoreNodesById",paramMap);
		return nbs;
	}

	@Override
	public void deleteSubTopoElementsByIds(List<Long> ids,Long subTopoId,boolean isPhysicalDelete) {
		//删除核心设备cores
		List<Long> tmp = new ArrayList<Long>();
		for(Long id:ids){
			List<NodeBo> nbs = getCoreNodesById(id,subTopoId);
			for(NodeBo nb:nbs){
				tmp.add(nb.getId());
			}
		}
		for(Long tid:tmp){
			ids.add(tid);
		}
		deleteByIds(ids,isPhysicalDelete);
	}
	@Override
	public List<NodeBo> getCoreNodesInSubtopoById(Long parentSubTopoId,Long subTopoId) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("subTopoId", subTopoId);
		m.put("parentSubTopoId", parentSubTopoId);
		return getSession().selectList(getNamespace() + "getCoreNodesInSubtopoById",m);
	}
	
	@Override
	public int insert(NodeBo nodebo) {
		if(null == nodebo.getId()) nodebo.setId(this.getSeq().next());
		return this.getSession().update(getNamespace()+"insert", nodebo);
	}

	@Override
	public void hideNodes(Long[] ids) {
		this.getSession().update(getNamespace()+"hideNodes", ids);
	}

	@Override
	public List<NodeBo> getHideNodesBySubtopoId(Long subTopoId) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("subTopoId", subTopoId);
		return this.getSession().selectList(getNamespace()+"getHideNodesBySubtopoId", m);
	}
	@Override
	public void setVisibleable(Long[] ids) {
		if(ids!=null && ids.length>0){
			this.getSession().update(getNamespace()+"setVisibleable",ids);
		}
	}
	@Override
	public List<Long> getChildrenIdByParentIds(List<Long> ids) {
		Map<String, List<Long>> m = new HashMap<String, List<Long>>();
		m.put("ids", ids);
		return this.getSession().selectList(getNamespace()+"getChildrenIdByParentIds", m);
	}
	@Override
	public List<NodeBo> listLogicalDeleted() {
		return this.getSession().selectList(getNamespace()+"listLogicalDeleted");
	}
	@Override
	public void recoverLogicalDelete(Long[] ids){
		this.getSession().update(getNamespace()+"recoverNodeLogicalDelete",ids);
		this.getSession().update(getNamespace()+"recoverLinkLogicalDelete",ids);
	}
	@Override
	public List<NodeBo> findCopyNodesForTopo(Long topoId) {
		Map<String, Object> paramMap = new HashMap<String, Object>(2);
		paramMap.put("topoId", topoId);
		paramMap.put("attr", "'%links%'");
		return getSession().selectList(getNamespace()+"findCopyNodesForTopo",paramMap);
	}
	@Override
	public List<NodeBo> getSubtopoNodeBySubtopoId(Long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>(2);
		paramMap.put("topoId", id);
		paramMap.put("attr", "'%\"subtopoId\":"+id+"%'");
		return getSession().selectList(getNamespace()+"getSubtopoNodeBySubtopoId",paramMap);
	}
	@Override
	public void unbindAllRelation(List<Long> instanceIds) {
		if(instanceIds.size()>0){
			Map<String, Object> paramMap = new HashMap<String, Object>(2);
			paramMap.put("ids", instanceIds);
//			paramMap.put("table", "STM_TOPO_MAP_NODE");
			paramMap.put("instanceId", 0);
//			getSession().update(getNamespace()+"unbindAllRelation",paramMap);
			getSession().update(getNamespace()+"unbindAllMapRelation",paramMap);
			
			paramMap.put("table", "STM_TOPO_MAP_LINE");
			paramMap.put("instanceId", 0);
			getSession().update(getNamespace()+"unbindAllRelation",paramMap);
			paramMap.put("table", "STM_TOPO_NODE");
			paramMap.put("instanceId", 1);
            getSession().update(getNamespace() + "unbindAllRelation2", paramMap);
            paramMap.put("table", "STM_TOPO_LINK");
			paramMap.put("instanceId", 1);
            getSession().update(getNamespace() + "unbindAllRelation2", paramMap);
        }
	}

	@Override
	public List<NodeBo> queryOne(String ip, String subtopoid) {
		Map<String,String> param = new HashMap<String,String>();
		param.put("ip", ip);
		param.put("subtopoid", subtopoid);

		return getSession().selectList(getNamespace() + "queryOne",param);
		/*long count = getSession().selectOne(getNamespace()+"queryCount",param);
		logger.error("INodeDao " + ip +" ," + subtopoid + " ," +count);
		if(count == 1){
			return getSession().selectOne(getNamespace() + "queryOne",param);
		}else if(count > 1){
			param.put("type", "ROUTER");
			return getSession().selectOne(getNamespace() + "queryOne",param);
		}else{
			return null;
		}*/
	}
}
