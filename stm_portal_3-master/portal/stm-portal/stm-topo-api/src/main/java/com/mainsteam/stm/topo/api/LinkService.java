package com.mainsteam.stm.topo.api;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.LinkBo;

/**
 * 链路的业务处理
 * @author 富强
 *
 */
public interface LinkService {
	/**
	 * 根据链路实例id转换链路状态
	 * @param linkInstanceIds
	 * @param metricId
	 * @return
	 */
	public JSONArray convertLinkState(Long[] linkInstanceIds,String metricId);
	
	/**
	 * 删除多链路全部连线
	 * @param id
	 */
	public void removeMultiLink(Long id);
	
	/**
	 * 根据条件分页查询资源链路列表
	 * @param page
	 * @return List<LinkBo>
	 * @throws InstancelibException
	 */
	public void selectMultiLinkByPage(Page<LinkBo, LinkBo> page) throws InstancelibException;

	JSONObject getDetailInfo(Long id);
	
	boolean refreshLink(String linkInfo);

	boolean addLink(LinkBo lb);
	
	/**
	 * 批量设置资源实例-阈值指标
	 * @param bandInfo
	 */
	public boolean updateBandWidthUtil(String bandInfo);

	void removeLink(List<Long> asList)  throws Exception;
	/**
	 * 链路加入监控
	 * @param 需要加入监控的LinkBo id
	 * @return id和instanceId关联关系
	 */
	JSONObject addMonitor(List<Long> ids);

	JSONObject updateAttr(LinkBo link);

	double getBandWidth(Long instanceId,String unit);

	JSONObject getLinkInfoForTip(Long linkInstanceId);

	void linkSubinstTip(JSONObject collector, ResourceInstance linkResInst,boolean isSrc);

	public Long getValueInstId(Long instId);
	
	public InstanceStateEnum getLinkInstState(Long linkInstId,String metricId);

	public JSONArray getNodeStates(Long[] nodeIds,String nodeMetricId);
}
