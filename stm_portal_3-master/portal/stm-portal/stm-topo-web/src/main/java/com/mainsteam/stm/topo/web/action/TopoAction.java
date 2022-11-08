package com.mainsteam.stm.topo.web.action;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.ITopoGraphApi;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.OtherNodeService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.QueryNode;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 负责拓扑管理
 * <li>文件名称: TopoAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 拓扑管理初期web服务,访问根地址/topo</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月7日
 * @author  xfq
 */
@Controller
@RequestMapping(value="/topo")
public class TopoAction extends BaseAction{
	Logger logger = Logger.getLogger(TopoAction.class);
	@Autowired
	private ITopoGraphApi tgsvc;
	//调用第三方服务
	@Autowired
	private ThirdService thirdSvc;
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	//配置服务
	@Autowired
	private ISettingApi settingApi;
	@Autowired
	private IUserApi userApi;
	@Autowired
	private LinkService linkService;
	@Autowired
	private OtherNodeService otherNodeService;
	
	/**
	 * 刷新拓扑其他图元告警状态
	 * @param 
	 */
	@RequestMapping(value="refreshOtherState",method=RequestMethod.POST)
	public JSONObject refreshOtherState(Long subTopoId,String linkMetricId,String nodeMetricId){
		JSONObject retn = new JSONObject();
		try{
			JSONArray otherStateResults = new JSONArray();
			otherStateResults = otherNodeService.getOtherState(subTopoId, linkMetricId, nodeMetricId);
			retn.put("status",200);
			return toSuccess(otherStateResults);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("其他图元状态刷新错误",e);
			return toJsonObject(700, "其他图元状态刷新错误");
		}
	}
	
	/**
	 * 机房视图，获取所有资源实例
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/all/resources")
	public JSONObject getAllResources(){
		JSONObject result = new JSONObject();
		try{
			result = tgsvc.getAllResources();
		}catch(Exception e){
			logger.error("获取资源失败",e);
		}
		return result;
	}
	
	/**
	 * 拓扑菜单权限检查
	 * @return
	 */
	@RequestMapping("/navMenu/check")
	public JSONObject checkMenuAuth(){
		try {
			ILoginUser user = super.getLoginUser();
			//对于缺省的域管理员，系统管理员，管理者都有权限
			boolean checkRst = (user.isSystemUser() || user.isManagerUser() || user.isDomainUser());
			return super.toSuccess(checkRst);
		} catch (Exception e) {
			logger.error("拓扑菜单权限检查异常",e);
			return toJsonObject(700, "拓扑菜单权限检查失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	/**
	 * 获取设备的简要信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{topoId}/topoinfo",method={RequestMethod.GET,RequestMethod.POST})
	public JSONObject getSubTopoInfo(@PathVariable(value="topoId") Long topoId){
		return tgsvc.getSubTopoInfo(topoId);
	}
	/**
	 * 保存图元信息
	 * @param links 链路
	 * @param groups 组
	 * @param nodes 节点
	 * @param others 其他节点
	 * @param rlinks 删除的链路
	 * @param rgroups 删除的组
	 * @param rnodes 删除的节点
	 * @param rothers 删除的其他节点
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public JSONObject saveTopo(Long topoId,String links,String groups,String nodes,String others,String rlinks,String rgroups,String rnodes,Long[] rothers){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(topoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			Gson gson = new Gson();
			List<Long> rnodeList = (List<Long>) gson.fromJson(rnodes, new TypeToken<List<Long>>() {}.getType());
			List<Long> rgroupList = (List<Long>) gson.fromJson(rgroups, new TypeToken<List<Long>>() {}.getType());
			List<Long> rlinkList = (List<Long>) gson.fromJson(rlinks, new TypeToken<List<Long>>() {}.getType());
			tgsvc.deleteGraph(rlinkList,rgroupList,rnodeList,rothers);
			System.out.println(rlinks);
//			List<GroupBo> groupList = (List<GroupBo>) gson.fromJson(groups, new TypeToken<List<GroupBo>>() {}.getType());	//已经没有组的概念了
			List<LinkBo> linkList=(List<LinkBo>) gson.fromJson(links, new TypeToken<List<LinkBo>>() {}.getType());
			List<NodeBo> nodeList = NodeBo.parseForTopo(nodes);
			List<OtherNodeBo> otherList=(List<OtherNodeBo>) gson.fromJson(others, new TypeToken<List<OtherNodeBo>>() {}.getType());
			tgsvc.saveOrUpdate(linkList,null,nodeList,otherList);
			retn.put("status",200);
			retn.put("msg", "保存成功");
		}else{
			retn.put("status",700);
			retn.put("msg", "无权操作");
		}
		return retn;
	}
	
	@ResponseBody
	@RequestMapping(value="/updateZindex",method=RequestMethod.POST)
	public void updateTopoZindex(Long id,int zIndex,String currentdate){
		tgsvc.updateTopoZindexById(id, zIndex,currentdate);
	}
	
	/**
	 * 获取图元信息（用于界面拓扑图的绘制）
	 * @return
	 */
	@RequestMapping(value="/graph",method=RequestMethod.GET,produces="text/html;charset=UTF-8")
	public String getTopoGrap(){
		return tgsvc.getSubTopo(null);
	}
	
	/**
	 * 替换图标
	 * @param id 图元库node的id
	 * @param src 图标的路径
	 */
	@RequestMapping(value="replaceIcon",method=RequestMethod.POST)
	public void replaceIcon(Long id,String src){
		tgsvc.replaceIcon(id,src);
	}
	/**
	 * 获取IP列表
	 * @return 数组形式的IP列表
	 */
	@RequestMapping(value="ips")
	public String getIps(){
		List<String> ips = tgsvc.selectIps();
		return JSON.toJSONString(ips);
	}
	/**
	 * 查询
	 * @return
	 */
	@RequestMapping(value="query",method=RequestMethod.POST)
	public JSONObject query(QueryNode query){
		List<NodeBo> retn = tgsvc.query(query);
		Page<NodeBo,NodeBo> page = new Page<NodeBo,NodeBo>();
		page.setDatas(retn);
		return toSuccess(page);
	}
	/**
	 * 添加子拓扑
	 * @param parentId 父拓扑id
	 * @param name 子拓扑名称
	 * @param ids 子拓扑包含节点
	 * @return 子拓扑id
	 */
	@RequestMapping(value="{parentId}/addSubTopo",method=RequestMethod.POST)
	public String addSubTopo(@PathVariable(value="parentId") Long parentId,String name,Long[] toAdd){
		//必须得有编辑权限
		if(authSvc.hasAuth(parentId, new String[]{TopoAuthSettingBo.EDIT})){
			if(new Long(0l).equals(parentId)){
				parentId=null;
			}
			//subtopo name validation
			int isNameValid = tgsvc.subTopoNameValidation(parentId, name.trim());
			if(isNameValid > 0) { //name exists
				return "InvalidName";
			} else { // name valid
				Long id =  tgsvc.addSubTopo(parentId,name,toAdd);
				return id.toString();
			}
			
		}else{
			JSONObject failed = new JSONObject();
			failed.put("msg", "您没有拓扑图编辑权限，请向管理员申请！");
			return failed.toJSONString();
		}
	}
	/**
	 * 添加新元素到子拓扑
	 * @param topoId 子拓扑id
	 * @param ids 已有元素id
	 * @return
	 */
	@RequestMapping(value="addNewElemToSubTopo/{topoId}",method=RequestMethod.POST)
	public JSONObject addNewElemToSubTopo(@PathVariable(value="topoId") Long topoId,Long[] ids){
		//必须得有编辑权限
		Object retn = null;
		if(authSvc.hasAuth(topoId, new String[]{TopoAuthSettingBo.EDIT})){
			retn=tgsvc.addNewElemToSubTopo(topoId,ids);
		}else{
			JSONObject failed = new JSONObject();
			failed.put("msg", "您没有拓扑图编辑权限，请向管理员申请！");
			retn=failed;
		}
		return toSuccess(retn);
	}
	/**
	 * 获取子拓扑
	 * @param id 子拓扑id
	 * @return 子拓扑结构JSON
	 */
	@RequestMapping(value="getSubTopo/{id}",method=RequestMethod.POST)
	public String getSubTopo(@PathVariable(value="id") Long id){
		//必须得有查看权限
		if(authSvc.hasAuth(id, new String[]{TopoAuthSettingBo.SELECT})){
			if(new Long(0).equals(id) || new Long(10).equals(id)){
				id=null;
			}
			try {
				return  tgsvc.getSubTopo(id);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("获取子拓扑失败",e);
			}
			return "获取子拓扑失败";
		}else{
			//解决所有人默认都有拓扑图查看权限
			TopoAuthSettingBo bo = authSvc.getAuthSetting(id);
			if(null == bo){
				return tgsvc.getSubTopo(id);
			}else{
				JSONObject failed = new JSONObject();
				failed.put("msg", "您没有拓扑图查看权限，请向管理员申请!");
				return failed.toJSONString();
			}
		}
	}
	@ResponseBody
	@RequestMapping(value="findNodesBySubnetIp",method=RequestMethod.POST)
	public JSONObject findNodesBySubnetIp(String ip){
		try {
			return tgsvc.findNodesBySubnetIp(ip);
		} catch (Exception e) {
			logger.error("TopoAction.findNodesBySubnetIp",e);
		}
		return toSuccess("服务器出错");
	}
	/**
	 * 获取子拓扑
	 * @return
	 */
	@RequestMapping(value="{topoId}/subTopos",method={RequestMethod.POST,RequestMethod.GET})
	public String subTopos(@PathVariable(value="topoId") Long topoId){
		return tgsvc.subTopos(topoId);
	}
	/**
	 * 获取所有的拓扑图列表
	 * @return
	 */
	@RequestMapping(value="allTopos",method=RequestMethod.POST)
	public String allTopos(){
		return tgsvc.allTopos();
	}
	/**
	 * 批量加入/取消监控
	 * @param instanceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="link/batch/monitor",method=RequestMethod.POST)
	public JSONObject addMonitorBatch(Long[] instanceIds,String type){
		StringBuffer tip = new StringBuffer((type.equals("add"))?"加入监控":"取消监控");
		try{
			thirdSvc.monitorBatch(instanceIds,type);
			return super.toSuccess(tip.append("成功"));
		}catch(Exception e){
			logger.error(tip.append("失败"), e);
			return super.toJsonObject(700,tip );
		}
	}
	/**
	 * 加入监控
	 * @param instanceId 资源实例id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="addMonitor/{instanceId}",method=RequestMethod.POST)
	public JSONObject addMonitor(@PathVariable(value="instanceId") Long instanceId){
		JSONObject retn = new JSONObject();
		//必须得有编辑权限
		if(authSvc.hasAuth(instanceId, new String[]{TopoAuthSettingBo.EDIT})){
			boolean status = thirdSvc.addMonitor(instanceId);			
			retn.put("state", status?200:700);
		}else{
			retn.put("msg", "无权操作");
			retn.put("state", 700);
		}

		return retn;
	}
	/**
	 * 取消监控
	 * @param instanceId 资源实例id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="cancelMonitor/{instanceId}",method=RequestMethod.POST)
	public JSONObject cancelMonitor(@PathVariable(value="instanceId") Long instanceId){
		JSONObject retn = new JSONObject();
		//必须得有编辑权限
		if(authSvc.hasAuth(instanceId, new String[]{TopoAuthSettingBo.EDIT})){			
			boolean status = thirdSvc.cancelMonitor(instanceId,retn);
			retn.put("state", status?200:700);			
		}else{
			retn.put("msg", "无权操作");
			retn.put("state", 700);
		}
		
		return retn;

	}
	/**
	 * 是否已经存在拓扑视图,存在的话返回首页topo id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="hasTopo",method=RequestMethod.POST)
	public JSONObject hasTopo(){
		return tgsvc.hasTopo();
	}

	@ResponseBody
	@RequestMapping(value="hasTopo2",method=RequestMethod.POST)
	public JSONObject hasTopo2(){
		return tgsvc.hasTopo2("zzztest");
	}
	/**
	 * 刷新拓扑状态
	 * @param instIds 资源实例id
	 */
	@ResponseBody
	@RequestMapping(value="refreshState",method=RequestMethod.POST)
	public JSONObject refreshTopoState(Long[] nodeIds,Long[] linkIds,String linkMetricId,String nodeMetricId){
		JSONObject retn = new JSONObject();
		try{
			JSONArray nodes = new JSONArray();
			//获取节点的状态
			if(null!=nodeIds){
				nodes.addAll(linkService.getNodeStates(nodeIds,nodeMetricId));
			}
			JSONArray links = new JSONArray();
			//获取链路的状态
			if(null!=linkIds){
				JSONArray linksState = linkService.convertLinkState(linkIds, linkMetricId);
				for(Object state:linksState){
					links.add((JSONObject)state);
				}
			}
			retn.put("nodes", nodes);
			retn.put("links", links);
			retn.put("status",200);
		} catch (Exception e) {
			logger.error("状态刷新错误",e);
			retn.put("msg", "状态刷新错误");
			retn.put("state", 700);
		}
		return retn;
	}
	/**
	 * 通过ip查询包含该ip的所有子拓扑
	 * @param ip 待查询的ip(支持模糊搜索)
	 * @return 子拓扑列表
	 */
	@RequestMapping(value="getSubToposByIp",method=RequestMethod.POST,produces="text/html;charset=UTF-8")
	public String getSubToposByIp(String ip){
		return tgsvc.getSubToposByIp(ip).toJSONString();
	} 
	/**
	 * 获取资源实例的指标信息
	 * @param instanceId
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="metricinfo",method=RequestMethod.POST)
	public JSONObject getMetricInfoByInstanceId(Long instanceId,String type,String unit){
		JSONObject retn = new JSONObject();
		try {
			JSONObject info = tgsvc.getMetricInfoByInstanceId(instanceId,type,unit);
			retn.put("state", 200);
			retn.put("info", info);
		} catch (Throwable e) {
			logger.error("获取指标信息失败",e);
			retn.put("msg", "获取指标信息失败");
			retn.put("state", 700);
		}
		return retn;
	}
	/**
	 * 更新拓扑的信息
	 * @param sb
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="updateSubTopo",method=RequestMethod.POST)
	public String updateSubTopo(SubTopoBo sb){
		return tgsvc.updateSubTopo(sb);
	}
	/**
	 * 拖过资源实例id获取profileid
	 * @param instanceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="getProfileIdByInstanceId",method=RequestMethod.POST)
	public JSONObject getProfileIdByInstanceId(Long instanceId){
		return tgsvc.getProfileIdByInstanceId(instanceId);
	}
	@ResponseBody
	@RequestMapping(value="refreshLinkDataByIds",method=RequestMethod.POST)
	public JSONArray refreshLinkDataByIds(Long[] ids){
		return tgsvc.refreshLinkDataByIds(ids);
	}
	@RequestMapping(value="refreshLifeState",method=RequestMethod.POST)
	public JSONObject refreshLifeState(long[] ids){
		return toSuccess(tgsvc.refreshLifeState(ids));
	}
	@RequestMapping(value="deleteSubtopo",method=RequestMethod.POST,produces="text/html;charset=UTF-8")
	public String deleteSubtopo(Long id,boolean recursive){
		if(authSvc.hasAuth(id, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			return tgsvc.deleteSubtopo(id,recursive);
		}else{
			return "无权限删除";
		}
	}
	@RequestMapping(value="refreshVendorName",method=RequestMethod.POST,produces="text/html;charset=UTF-8")
	public String refreshVendorName(Long[] ids){
		return tgsvc.refreshVendorName(ids);
	}
	@ResponseBody
	@RequestMapping(value="newlink",method=RequestMethod.POST)
	public JSONObject newLink(String info){
		return tgsvc.newLink(info);
	}
	
	/**
	* @Title: checkAuth
	* @Description: 检查用户有没有资源权限
	* @param instanceId
	* @return  JSONObject
	* @throws
	*/
	@ResponseBody
	@RequestMapping(value="resource/checkauth",method=RequestMethod.POST)
	public JSONObject checkAuth(Long instanceId){
		return thirdSvc.checkUserInstanceAuth(getLoginUser(), instanceId);
	}
	@ResponseBody
	@RequestMapping(value="addNode",method=RequestMethod.POST)
	public JSONObject addNode(NodeBo nb){
		if(authSvc.hasAuth(nb.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			String icon = nb.getIcon();
			JSONObject status = tgsvc.save(nb);
			JSONObject retn = (JSONObject) JSON.toJSON(nb);
			retn.put("icon",icon);
			status.put("node", retn);
			return status;
		}else{
			return toSuccess("您没有编辑拓扑权限");
		}
	}
	/**
	 * 删除指定拓扑的指定节点，包括和节点所关联的链路
	 * @param subTopoId
	 * @param id
	 * @return
	 */
	@RequestMapping(value="removeNode",method=RequestMethod.POST)
	public JSONObject removeNode(Long subTopoId,Long[] ids,boolean isPhysicalDelete){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			tgsvc.removeNode(Arrays.asList(ids),isPhysicalDelete);
			retn.put("code",200);
			retn.put("msg", "删除成功");
		}else{
			retn.put("code",700);
			retn.put("msg", "您没有编辑拓扑权限");
		}
		return toSuccess(retn);
	}
	@ResponseBody
	@RequestMapping(value="updateOther",method=RequestMethod.POST)
	public JSONObject updateOther(OtherNodeBo ob){
		JSONObject retn = null;
		if(authSvc.hasAuth(ob.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			if(ob.getId()!=null && ob.getId()>0){
				tgsvc.updateOther(ob);
				retn = (JSONObject)JSON.toJSON(ob);
				retn.put("code",200);
			}else{
				retn = new JSONObject();
				retn.put("code",700);
				retn.put("msg", "该节点不存在");
			}
		}else{
			retn = new JSONObject();
			retn.put("msg", "您没有编辑拓扑权限");
			retn.put("code",700);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="addOther",method=RequestMethod.POST)
	public JSONObject addOther(OtherNodeBo ob) throws Exception{
		JSONObject retn = null;
		if(authSvc.hasAuth(ob.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			tgsvc.addOther(ob);
			retn = (JSONObject)JSON.toJSON(ob);
			retn.put("state",200);
			retn.put("msg", "添加成功");
		}else{
			retn = new JSONObject();
			retn.put("msg", "您没有编辑拓扑权限");
			retn.put("state",700);
		}
		return retn;
	};
	@ResponseBody
	@RequestMapping(value="removeOther",method=RequestMethod.POST)
	public JSONObject removeOther(Long subTopoId,Long[] ids){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			tgsvc.removeOther(Arrays.asList(ids));
			retn.put("code",200);
			retn.put("msg", "删除成功");
		}else{
			retn.put("code",700);
			retn.put("msg","您没有编辑拓扑权限");
		}
		return retn;
	}
	
	@ResponseBody
	@RequestMapping(value="removeGroup",method=RequestMethod.POST)
	public JSONObject removeGroup(Long subTopoId,Long[] ids){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			tgsvc.removeGroup(Arrays.asList(ids));
			retn.put("code",200);
			retn.put("msg", "删除成功");
		}else{
			retn.put("code",700);
			retn.put("msg","您没有编辑拓扑权限");
		}
		return retn;
	}
	//获取子拓扑的所有核心设备在二层拓扑中的id
	@ResponseBody
	@RequestMapping(value="getCoreNodesInSubtopoById",method=RequestMethod.POST)
	public JSONObject getCoreNodesInSubtopoById(Long parentSubTopoId,Long subTopoId){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.SELECT})){//必须包含查看权限
			JSONArray ids = tgsvc.getCoreNodesInSubtopoById(parentSubTopoId,subTopoId);
			retn.put("ids", ids);
			retn.put("code",200);
			retn.put("msg", "获取成功");
		}else{
			retn.put("code",700);
			retn.put("msg","没有查看拓扑权限");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="updateResourceInstance",method=RequestMethod.POST)
	public JSONObject updateResourceInstance(Long subTopoId,Long id,Long instanceId,String ip){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			NodeBo nb = tgsvc.updateResourceInstance(id,instanceId,ip);
			retn.put("code",200);
			retn.put("instanceId", nb.getInstanceId());
			retn.put("ip", nb.getIp());
			retn.put("msg", "更新成功");
		}else{
			retn.put("code",700);
			retn.put("msg","您没有编辑拓扑权限");
		}
		return retn;
	}
	/**
	 * 隐藏资源节点
	 * @param ids
	 * @param subTopoId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="hideNodes",method=RequestMethod.POST)
	public JSONObject hideNodes(Long[] ids,Long subTopoId){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			tgsvc.hideNodes(ids);
			retn.put("state",200);
			retn.put("msg", "操作成功");
		}else{
			retn.put("state",700);
			retn.put("msg","您没有编辑拓扑权限");
		}
		return retn;
	}
	/**
	 * 获取子拓扑下被隐藏的节点
	 * @param subTopoId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="getHideNodes",method=RequestMethod.POST)
	public JSONObject getHideNodes(Long subTopoId){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			JSONArray items = tgsvc.getHideNodes(subTopoId);
			retn.put("state",200);
			retn.put("items",items);
		}else{
			retn.put("state",700);
			retn.put("msg","您没有编辑拓扑权限");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="showHideNodes",method=RequestMethod.POST)
	public JSONObject showHideNodes(Long[] ids,Long subTopoId){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			JSONObject info = tgsvc.showHideNodes(ids);
			retn.put("state",200);
			retn.put("info",info);
		}else{
			retn.put("state",700);
			retn.put("msg","您没有编辑拓扑权限");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="getAllIpsForSubtopo")
	public JSONObject getAllIpsForSubtopo(Long subTopoId){
		JSONObject retn = new JSONObject();
		boolean hasAuth = authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.SELECT});
		//必须包含编辑权限
		if(hasAuth || null == authSvc.getAuthSetting(subTopoId)){//解决所有人默认都有拓扑图查看权限
			try {
				JSONObject info = tgsvc.getAllIpsForSubtopo(subTopoId);
				retn.put("state",200);
				retn.put("info",info);
			} catch (InstancelibException e) {
				e.printStackTrace();
				retn.put("state", 500);
				logger.error(e);
			}
		}else{
			retn.put("state",700);
			retn.put("msg","没有查看拓扑权限");
		}
//		logger.error("getAllIpsForSubtopo info "+retn);
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="getAllIpsForNode",method=RequestMethod.POST)
	public JSONObject getAllIpsForNode(Long id,Long subTopoId) {
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.SELECT})){//必须包含编辑权限
			try {
				JSONArray ips = tgsvc.getAllIpsForNode(id);
				retn.put("state",200);
				retn.put("ips",ips);
			} catch (InstancelibException e) {
				e.printStackTrace();
				retn.put("state", 500);
				logger.error(e);
			}
		}else{
			retn.put("state",700);
			retn.put("msg","您没有查看拓扑权限");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="updateResourceBaseInfo",method=RequestMethod.POST)
	public JSONObject updateResourceBaseInfo(Long subTopoId,String info){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.SELECT})){//必须包含编辑权限
				//检测是否重名
				JSONObject infoJson = JSONObject.parseObject(info);
				Long instanceId = null;
				if(infoJson.containsKey("instanceId")){
					instanceId = infoJson.getLong("instanceId");
				}
				String showName = null;
				if(infoJson.containsKey("name")){
					showName = JSONObject.parseObject(info).getString("name");
				}
				boolean check = tgsvc.checkDeviceName(instanceId,showName);
				if(check){
					retn.put("state", 700);
					retn.put("errorMsg", "此名称已存在");
				}else{
					JSONObject node = tgsvc.updateResourceBaseInfo(infoJson);
					retn.put("state",200);
					retn.put("node",node);
					retn.put("msg","信息修改成功");
				}
			}else{
				retn.put("state",700);
				retn.put("msg","您没有编辑拓扑权限");
			}
		} catch (InstancelibException e) {
			retn.put("state", 500);
			logger.error(e);
		}
		return retn;
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/creatTopoByIp",method=RequestMethod.POST)
	public JSONObject creatTopoByIp(String ip1, String ip2){
		logger.error("creatTopoByIp"+ip1+ip2);
		JSONObject retn = new JSONObject();
		try {
			String subtopoid = "0";
			String ids = "";
			QueryNode query = new QueryNode();
			logger.error("creatTopoByIp 11111111111111111111111111");
			query.setIp(ip1.trim());
			List<NodeBo> nodeList1 = tgsvc.query(query);
			logger.error("creatTopoByIp 22222222222222222222222222");
			logger.error("creatTopoByIp nodeList1 = " + nodeList1.size());
			String id1 = "";
			logger.error("creatTopoByIp 3333333333333333333333333");
			if(nodeList1.size() > 0){
				logger.error("creatTopoByIp 4444444444444444444444444444");
				for (NodeBo nodeBo : nodeList1) {
					if(subtopoid.equals(String.valueOf(nodeBo.getSubTopoId()))){
						id1 = String.valueOf(nodeBo.getId());
					}
				}
			}
			logger.error("creatTopoByIp 555555555555555555555555555555");
			if(id1 == null || "".equals(id1)){
				logger.error("creatTopoByIp 66666666666666666666666666");
				retn.put("status", 204);
				retn.put("msg","输入的第一个IP不在监控范围内");
				return retn;
			}
			logger.error("creatTopoByIp 7777777777777");
			ids += id1;
			query.setIp(ip2.trim());
			List<NodeBo> nodeList2 = tgsvc.query(query);
			logger.error("creatTopoByIp 8888888888888888888" + nodeList2.size());
			String id2 = "";
			if(nodeList2.size() > 0){
				logger.error("creatTopoByIp 9999999999999999");
				for (NodeBo nodeBo : nodeList2) {
					if(subtopoid.equals(String.valueOf(nodeBo.getSubTopoId()))){
						id2 = String.valueOf(nodeBo.getId());
					}
				}
			}
			if(id2 == null || "".equals(id2)){
				logger.error("creatTopoByIp 10101010100101010");
				retn.put("status", 204);
				retn.put("msg","输入的第二个IP不在监控范围内");
				return retn;
			}
			logger.error("creatTopoByIp 121211212121212121");
			ids += ","+id2;
			//链路上所有ip
			List<String> ipList = getIpList(ip1);
			StringBuffer sb1 = new StringBuffer();
			String id3 = "";
			logger.error("ipList.size = " + ipList.size());
			if(ipList != null && ipList.size() > 0){
				for (String string : ipList) {
					List<NodeBo> node = tgsvc.queryOne(string, subtopoid);
					if(node != null && node.size() > 0){
						for (NodeBo nodeBo : node) {
							sb1.append(",").append(nodeBo.getId());
						}
					}
				}
			}
			if(sb1.toString() != null && !"".equals(sb1.toString())){
				id3 = sb1.substring(1);
				ids += ","+id3;
			}
			logger.error("id3 = "+id3);
			List<String> ipList2 = getIpList(ip2);
			StringBuffer sb2 = new StringBuffer();
			String id4 = "";
			logger.error("ipList2.size = " + ipList2.size());
			if(ipList2 != null && ipList2.size() > 0){
				for (String string : ipList2) {
					List<NodeBo> node = tgsvc.queryOne(string, subtopoid);
					if(node != null && node.size() > 0){
						for (NodeBo nodeBo : node) {
							sb2.append(",").append(nodeBo.getId());
						}
					}
				}
			}
			if(sb2.toString() != null && !"".equals(sb2.toString())){
				id4 = sb2.substring(1);
				ids += ","+id4;
			}
			logger.error("id4 = "+id4);
			/*if(!ids.contains("120605")){
				ids += ",120605";
			}*/

			if(ids.length()>0){
				String [] str = ids.split(",");
				Set<String> set = new HashSet();
				for(int i =0; i< str.length; i++){
					set.add(str[i]);
				}
				StringBuffer sb = new StringBuffer();
				for(String string : set){
					sb.append("," + string);
				}
				ids = sb.substring(1);
			}

			//先删除name=zzztest的子拓扑
			tgsvc.deleteSubTopoByName("zzztest");

			retn.put("status", 200);
			retn.put("ids",ids);
			retn.put("name", "zzztest");
			retn.put("parentId", "0");
		} catch (Exception e) {
			retn.put("status", 500);
			logger.error(e);
		}
		return retn;
	}

	private List<String> getIpList(String ip){
		List<String> list = new ArrayList<>();
		switch (ip) {
			case "192.168.10.9":
				list.add("192.168.1.1");
				list.add("192.168.10.2");
				break;
			case "192.168.10.2":
				list.add("192.168.10.2");
				break;
			default:
				break;
		}
		return list;
	}
}
