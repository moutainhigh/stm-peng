package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.GroupBo;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;

public interface ILinkDao {
	/**
	 * 获取需要被删除的链路资源实例ID
	 * @param nodeIds
	 * @return
	 */
	public List<Long> getDelLinkInstancdeIdsByNodeIds(List<Long> nodeIds); 
	
	/**
	 * 根据链路实例id过滤多链路状态
	 * @param instanceId
	 * @return 多链路数据
	 */
	public LinkBo selectMultiByInstanceId(Long instanceId);
	
	/**
	 * 根据链路两端节点id查询多链路
	 * @param fromNodeId
	 * @param toNodeId
	 * @return List<LinkBo>
	 */
	public List<LinkBo> getLinksByFromToId(Long fromNodeId,Long toNodeId);
	
	/**
	 * 分页查询多链路列表数据
	 * @param page
	 * @return
	 */
	public void selectMutilLinkByPage(Page<LinkBo, LinkBo> page);
	
	/**
	 * 根据ids查询链路
	 * @param ids
	 * @return List<LinkBo>
	 */
	public List<LinkBo> getLinksByIds(List<Long> ids);
	
	/**
	 * 根据资源实例ids查询链路
	 * @param instanceIds
	 * @return List<LinkBo>
	 */
	public List<LinkBo> getLinksByInstanceIds(long[] instanceIds);
	/**
	 * 分页查询链路列表数据
	 * @param page
	 * @return
	 */
	public void selectByPage(Page<LinkBo, LinkBo> page);
	
	/**
	 * 查询所有链路实例（instanceId不为空）
	 * List<LinkBo>
	 */
	public List<LinkBo> getAllLinkInstances();
	/**
	 * 查询所有单接口的链路实例（instanceId不为空）
	 * @param
	 * @return List<LinkBo>
	 */
	public List<LinkBo> getSingleInterfaceLinkInstances();

	/**
	 * 查询所有单接口的连线（instanceId为空）
	 * @param
	 * @return List<LinkBo>
	 */
	public List<LinkBo> getSingleInterfaceLine();

	List<LinkBo> getAll();

	void update(List<LinkBo> updateLinks);

	void save(List<LinkBo> saveLinks);

	void delete(Long id);

	void deleteByIds(List<Long> rlinkList);

	LinkBo getById(Long id);
	/**
	 * 复制src的链路到des上
	 * @param srcId 被复制的节点id
	 * @param desId 复制后的节点id
	 */
	void copyLink(Long srcId, Long[] ids);
	/**
	 * 通过其他节点列表获取和它们相关的链路信息
	 * @param others 其他节点列表
	 * @return
	 */
	List<LinkBo> getLinksByOthersNode(List<OtherNodeBo> others);
	/**
	 * 通过节点列表获取和它们相关的链路信息
	 * @param nbs 节点列表
	 * @return
	 */
	List<LinkBo> getLinksByNode(List<NodeBo> nbs);
	/**
	 * 更新链路资源实例id
	 * @param lb
	 */
	void updateInstanceId(LinkBo lb);
	
	/**
	 * 删除资源后，解除链路关系
	 *
	 * @param instanceIds
	 */
	public int updateLinkRelationOnResourceDelete(List<Long> instanceIds);


	List<LinkBo> getLinksByGroups(List<GroupBo> groups);
	
	/**
	 * 通过节点id删除链路
	 * @param ids
	 */
	public void deleteByNodeIds(List<Long> ids,boolean isPhysicalDelete);
	
	public List<LinkBo> findLink(Long id, Long long1);
	/**
	 * 具有相关fromNode=fromIfIndex和toNode=toIfIndex的链路（理论上只有一条）
	 * @param lb
	 * @return
	 */
	public List<LinkBo> findLink(LinkBo lb);
	public int updateAttr(LinkBo link);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 保存
	 * @param linkBo
	 * @return
	 */
	public int save(LinkBo linkBo);

	public List<Long> getLinkInstancesIdByNodeIds(List<Long> nodeIds);

	public List<Long> getLinksIdByNode(NodeBo node);

	public List<LinkBo> getByIds(Long[] ids);

	public List<LinkBo> findLinkByParentId(Long id);

	/**
	 * 链路转换为连线
	 * @param id
	 * @return
	 */
	public void transformLinkToLineById(Long id);
}
