package com.mainsteam.stm.topo.dao;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.QueryNode;

public interface INodeDao {
	/**
	 * 修改图元所有图元(2层、3层)尺寸
	 * @param node
	 */
	public void updatePelSize(NodeBo node);
	
	/**
	 * 匹配ip-mac-port设备是否在snmp发现的node表中
	 * @param ip
	 * @return List<NodeBo>
	 */
	public List<NodeBo> matchedIp(String ip);
	
	/**
	 * 分页查询节点列表数据
	 * @param page
	 * @return
	 */
	public void selectByPage(Page<NodeBo, NodeBo> page);
	
	/**
	 * 获取所有节点的资源实例ids
	 * @return List<Long>
	 */
	public List<Long> getAllInstanceIds();
	
	/**
	 * 根据资源实例id查询节点
	 * @param instanceId
	 * @return
	 */
	public NodeBo getByInstanceId(Long instanceId);
	public List<Long> getNodeIdByInstanceIds(List<Long> ids);
	public List<NodeBo> getAllInstances();
	int insert(NodeBo node);
	List<NodeBo> getAll();
	void save(List<NodeBo> saveNodes);
	void update(List<NodeBo> updateNodes);
	void updateGroupId(Long id, List<Long> children);
	List<Long> getGroupNodes(Long id);
	void deleteByIds(List<Long> rnodeList,boolean isPhysicalDelete);
	ISequence getSequence();
	Long getIdByDeviceId(String srcId);
	void replaceIcon(Long id, String src);
	List<String> selectIps();
	NodeBo getById(Long from);
	List<NodeBo> getDeviceInOrder(Page<NodeBo, NodeBo> page);
	List<NodeBo> query(QueryNode query);
	List<NodeBo> queryOne(String ip, String subtopoid);
	int updateInstanceId(NodeBo node);
	/**
	 * 通过id列表返回节点实例列表
	 * @param ids
	 * @return 节点实例列表
	 */
	List<NodeBo> getByIds(List<Long> ids);
	/**
	 * 通过子拓扑的id获取实例列表
	 * @param id 子拓扑id
	 * @return 节点实例列表
	 */
	List<NodeBo> getBySubTopoId(Long id);
	/**
	 * 通过设备id获取NodeBo
	 * @param string
	 * @return
	 */
	NodeBo getByDeviceId(String deviceId);
	/**
	 * 通过ip返回二层的节点
	 * @param ip
	 * @return
	 */
	List<NodeBo> getByIp(String ip,Long subTopoId);
	/**
	 * 是否有拓视图
	 * @return
	 */
	public boolean hasTopo();
	
	NodeBo getFortopoFindByIp(String ip);

	public List<NodeBo> queryByIps(ArrayList<String> arrayList,Long subTopoId);

	/**
	 * 删除资源后，解除node关系
	 *
	 * @param instanceIds
	 */
	public int updateNodeRelationOnResourceDelete(List<Long> instanceIds);

	public NodeBo getByInstanceIdOrIpForSubtopo(Long topoId, Long instanceId,String ip);
	/**
	 * 获取和该设备相连的核心设备列表
	 * @param id
	 * @return
	 */
	public List<NodeBo> getCoreNodesById(Long id,Long subTopoId);

	public void deleteSubTopoElementsByIds(List<Long> ids,Long subTopoId,boolean isPhysicalDelete);

	public List<NodeBo> getCoreNodesInSubtopoById(Long parentSubTopoId,Long subTopoId);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	public void hideNodes(Long[] ids);

	public List<NodeBo> getHideNodesBySubtopoId(Long subTopoId);

	public void setVisibleable(Long[] ids);

	public List<Long> getChildrenIdByParentIds(List<Long> ids);

	public List<NodeBo> listLogicalDeleted();

	void recoverLogicalDelete(Long[] ids);

	public List<NodeBo> getByInstanceId(Long instanceId, Long topoId);

	public List<NodeBo> findCopyNodesForTopo(Long topoId);

	public List<NodeBo> getSubtopoNodeBySubtopoId(Long id);

	public void unbindAllRelation(List<Long> instanceIds);

}
