package com.mainsteam.stm.topo.api;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.topo.bo.GroupBo;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.QueryNode;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.bo.TopoBo;

public interface ITopoGraphApi {
	
	/**
	 * 查询所有资源实例（机房视图）
	 * @return
	 */
	public JSONObject getAllResources() throws InstancelibException ;

	JSONObject save(NodeBo node);

	TopoBo getTopo();
	/**
	 * 更新或保存
	 * @param linkList 链路列表
	 * @param groupList 组列表
	 * @param nodeList 节点列表
	 * @param othersList 其他节点列表
	 */
	void saveOrUpdate(List<LinkBo> linkList, List<GroupBo> groupList,List<NodeBo> nodeList,List<OtherNodeBo> othersList);
	
	/**
	 * 更新topo图层属性值
	 * @param id topoId
	 * @param zIndex 图层属性值
	 * @param currentdate 操作图层属性值时间
	 */
	void updateTopoZindexById(Long id, int zIndex, String currentdate);
			
	String getGraph();
	/**
	 * 获取指定子拓扑的节点
	 * @param topoId 拓扑id
	 * @param page 分页选项
	 * @return
	 */
	JSONObject getSubTopoInfo(Long topoId);
	/**
	 * 删除图元元素
	 * @param rlinkList 删除链路id
	 * @param rgroupList 删除的组节点id
	 * @param rnodeList 删除的节点id
	 * @param rothers 删除的其他节点id
	 */
	void deleteGraph(List<Long> rlinkList, List<Long> rgroupList,List<Long> rnodeList,Long[] rothers);
	/**
	 * 替换图元的图标
	 * @param id 图元库node的id
	 * @param src 图标路径
	 */
	void replaceIcon(Long id, String src);
	/**
	 * 获取网络设备的ip
	 * @return 网络设备的ip列表
	 */
	List<String> selectIps();
	
	/**
	 * 查询
	 * @param query 查询参数对象
	 * @return
	 */
	List<NodeBo> query(QueryNode query);
	//duandaoduan
	List<NodeBo> queryOne(String ip, String subtopoid);
	/**
	 * 添加子拓扑
	 * @param parentId 父拓扑id
	 * @param name 子拓扑名称
	 * @param ids 子拓扑初始化包含的节点id
	 * @return 新建子拓扑的id
	 */
	Long addSubTopo(Long parentId,String name, Long[] ids);
	/**
	 * 获取子拓扑
	 * @param id 子拓扑id
	 * @return 子拓扑图元结构
	 */
	String getSubTopo(Long id);
	/**
	 * 获取子拓扑列表
	 * @param parentId 父拓扑id
	 * @return [{
	 * 		id,name
	 * }]
	 */
	String subTopos(Long parentId);
	/**
	 * 通过ip查询包含该ip的所有子拓扑
	 * @param ip 待查询的ip(支持模糊搜索)
	 * @return 子拓扑列表
	 */
	JSONArray getSubToposByIp(String ip);
	/**
	 * 添加新元素到子拓扑
	 * @param topoId 子拓扑id
	 * @param ids 已有元素id
	 * @return 是否添加成功
	 */
	JSONObject addNewElemToSubTopo(Long topoId, Long[] ids);
	/**
	 * 是否有拓扑视图
	 * @return
	 */
	JSONObject hasTopo();
	JSONObject hasTopo2(String name);
	/**
	 * 获取所有的子拓扑列表
	 * @return
	 */
	String allTopos();
	/**
	 * 通过实例id获取指标信息
	 * @param instanceId 资源实例id
	 * @return
	 */
	JSONObject getMetricInfoByInstanceId(Long instanceId,String type,String unit) throws InstancelibException ;
	/**
	 * 更新子拓扑的属性
	 * @param sb
	 * @return
	 */
	String updateSubTopo(SubTopoBo sb);
	/**
	 * 通过资源实例id获取profileId
	 * @param instanceId
	 * @return
	 */
	JSONObject getProfileIdByInstanceId(Long instanceId);

	JSONArray refreshLinkDataByIds(Long[] ids);
	/**
	 * 
	 * @param ids
	 * @return
	 */
	JSONArray refreshLifeState(long[] ids);

	String deleteSubtopo(Long id,boolean recursive);

	JSONObject findNodesBySubnetIp(String ip);

	String refreshVendorName(Long[] ids);

	JSONObject newLink(String info);
	
	/**
	 * 子拓扑名字验证
	 *
	 * @param parentId
	 * @param subTopoName
	 * @return >1：名字已存在 0：名字不存在
	 */
	int subTopoNameValidation(Long parentId, String subTopoName);
	/**
	 * 删除节点包括其链路
	 * @param id
	 */
	void removeNode(List<Long> ids,boolean isPhysicalDelete);

	void removeOther(List<Long> asList);

	void removeGroup(List<Long> asList);

	void addOther(OtherNodeBo ob)  throws Exception;

	void updateOther(OtherNodeBo ob);

	JSONArray getCoreNodesInSubtopoById(Long parentSubTopoId,Long subTopoId);

	NodeBo updateResourceInstance(Long id, Long instanceId,String ip);

	void hideNodes(Long[] ids);

	JSONArray getHideNodes(Long subTopoId);

	JSONObject showHideNodes(Long[] ids);

	JSONObject getAllIpsForSubtopo(Long subTopoId) throws InstancelibException;

	JSONArray getAllIpsForNode(Long id) throws InstancelibException;

	JSONObject updateResourceBaseInfo(JSONObject infoJson) throws InstancelibException;
	
	/**
	 * 检查设备showName是否重复
	 * @param instanceId
	 * @param name
	 * @return
	 */
	boolean checkDeviceName(Long instanceId,String name);

	int deleteSubTopoByName(String name);
}
