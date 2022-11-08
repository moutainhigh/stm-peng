package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.qwserv.itm.netprober.bean.StatusProcess;

import java.util.List;
import java.util.Map;

/**
 * 专门用来请求其他模块服务的接口
 * @author 富强
 *
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:53:56
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:53:58
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:53:59
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:54:01
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:54:01
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:54:02
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:54:11
 * @author   zhangjunfeng
 */
/**
 * <li>文件名称: ThirdService</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 上午9:54:12
 * @author   zhangjunfeng
 */
public interface ThirdService {
	/**
	 * 根据指定categoryId获取设备类型定义
	 * @param categorys
	 * @param categoryId
	 * @return
	 */
	public Map<String, String> getDeviceType(List<Map<String, String>> categorys,String categoryId);
	
	/**
	 * 获取能力库定义的所有设备的类型
	 * @return {"id": "Windows","name": "Windows服务器","pid": "Host","type": "主机"}
	 */
	public List<Map<String, String>> getAllCategory();
	
	/**
	 * 链路批量加入/取消监控
	 * @param instanceIds	链路资源实例ids
	 * @param type (add:加入，cancel：取消)
	 */
	public void monitorBatch(Long[] instanceIds,String type)  throws ProfilelibException;
	
	/**
	 * 通过oid获取 ResourceDef
	 * @param oid
	 * @return 资源实例定义
	 */
	JSONArray getResourceDef(String oid);
	/**
	 * 通过oid获取ResourceId
	 * @param oid sysoid
	 * @return ResourceId
	 */
	String getResourceId(String oid);
	/**
	 * 通过资源实例id获取序列化后的资源实例信息
	 * @param instanceId 资源实例id
	 * @return 序列化json格式的资源实例信息
	 */
	JSONObject getResourceInstance(NodeBo node);
	/**
	 * 获取模型属性的值
	 * @param nb 图元节点实例
	 * @return 该资源实例的模型属性键值对
	 */
	JSONObject getModuleProps(NodeBo nb);
	/**
	 * 获取指标的值
	 * @param nb 图元节点实例
	 * @return 该资源实例的指标属性键值对
	 */
	String[] getMetrics(Long instId,String metricId,MetricTypeEnum type);
	/**
	 * 加入监控
	 * @param instanceId 资源实例id
	 * @return 是否添加监控成功
	 */
	boolean addMonitor(Long instanceId);
	/**
	 * 取消监控
	 * @param instanceId 资源实例id
	 * @return 是否取消成功
	 */
	boolean cancelMonitor(Long instanceId,JSONObject retn);
	/**
	 * 抽取domain和node的一些关键信息
	 * @return groupId domainId
	 */
	@Deprecated
	JSONObject extractDomainInfo();
	/**
	 * 
	 * @return
	 */
	boolean isTopoRunning();
	/**
	 * 拓扑发现
	 * @param params 拓扑发现的配置参数
	 * @param idomains 域集合
	 * @return 采集器启动状态
	 */
	StatusProcess topoFind(JSONObject params,ITopoFindHandler handler);
	/**
	 * 实例化链路
	 * @param lbs 链路列表
	 */
	void addInstanceLinks(List<LinkBo> lbs);
	/**
	 * 实例化设备
	 * @param node
	 */
	void addInstanceNode(NodeBo node);
	/**
	 * 添加极简模式子拓扑资源
	 * @param sid 子拓扑id
	 * @param resIds 子拓扑包含的资源实例id
	 */
	void addExtremSimpleTopoRes(Long bid,List<Long> resIds);
	/**
	 * 获取资源实例的状态
	 * @param instId 资源实例id
	 * @return
	 */
	JSONObject getInstanceState(Long instId);
	/**'
	 * 获取指标状态
	 * @param instId 资源实例id
	 * @param metricId 指标id
	 * @return 状态信息
	 */
	JSONObject getMetricState(Long instId,String metricId,boolean isLink);
	/**
	 * 获取资源实例的接口
	 * @param instanceId
	 */
	JSONObject getLinkModuleProps(Long instanceId);
	/**
	 * 获取阈值
	 * @param instId
	 * @param metricId
	 * @return
	 */
	JSONObject getThreshold(Long instId,String metricId);
	/**
	 * 更新阈值
	 * @param instId
	 * @param metricId
	 * @param min
	 * @param max
	 */
	JSONObject refreshThreshold(Long instId,String metricId,Float min,Float max);
	/**
	 * 添加模型属性
	 * @param instanceId
	 * @param key
	 * @param values
	 */
	void refreshModuleProp(Long instanceId,String key,String[] values);
	/**
	 * 获取资源实例的所有接口
	 * @param instanceId	主资源实例id
	 * @param isConvert 是否过滤已经选择的接口
	 * @return
	 */
	JSONArray getInstancesIfs(Long instanceId,boolean isConvert);

    JSONArray getInstancesIfs2(Long instanceId, boolean isConvert);
	/**
	 * 获取链路的某些属性
	 * @param instanceId
	 */
	JSONObject getLinkResourceInstanceAttr(Long instanceId);

	/**
	 * 添加资源实例
	 * @param nb
	 * @return 资源实例id
	 */
	Long addResourceInstance(NodeBo nb);
	/**
	 * 
	 * @param instanceId
	 * @return
	 */
	JSONObject getProfileIdByInstanceId(Long instanceId);
	JSONArray refreshLifeState(long[] ids);
	int cancelDiscover();
	JSONObject getVendorInfo(String oid);
	Long newLink(JSONObject linkInfo);
	void delExtremSimpleTopoRes(Long bid,List<Long> resIds);
	
	
	/**
	* @Title: checkUserInstanceAuth
	* @Description: 查询用户有没有资源权限
	* @param user 用户
	* @param insanceId 资源ID
	* @return  boolean 
	* @throws
	*/
	JSONObject checkUserInstanceAuth(ILoginUser user,Long insanceId);
}
