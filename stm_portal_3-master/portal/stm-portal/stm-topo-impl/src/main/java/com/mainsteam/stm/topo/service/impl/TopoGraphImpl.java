package com.mainsteam.stm.topo.service.impl;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.ITopoGraphApi;
import com.mainsteam.stm.topo.api.ITopoImageApi;
import com.mainsteam.stm.topo.api.SubTopoService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.GroupBo;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.QueryNode;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.bo.TopoBo;
import com.mainsteam.stm.topo.dao.IGroupDao;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.IOthersNodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import com.mainsteam.stm.topo.dao.ITopoFindDao;
import com.mainsteam.stm.topo.enums.TopoType;
import com.mainsteam.stm.topo.util.CoreIps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TopoGraphImpl extends ThirdServiceBase implements ITopoGraphApi{
	Logger logger = Logger.getLogger(TopoGraphImpl.class);
	private INodeDao ndao;
	private ILinkDao ldao;
	private IGroupDao gdao;
	@Autowired
	private ISubTopoDao sdao;
	@Autowired
	private IOthersNodeDao odao;
	@Autowired
	private ITopoFindDao tdao;
	@Autowired
	private ISettingDao settingDao;
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	//第三方模块服务接口
	@Autowired
	private ThirdService thirdSvc;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private ResourceInstanceService resSvc;
	@Autowired
	private IResourceInstanceExApi resourceExApi;
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService rsvc;
	@Autowired
	private SubTopoService subtopoService;
	@Autowired
	private ITopoImageApi topoImageApi;
	
	private final String defaultConstant = "- -";	//默认字符串常量
	private final String otherStr = "other";
	
	/**
	 * 获取虚拟化的设备定义的父级catgoryId
	 * @param def
	 * @return
	 */
	private Set<String> getAllVMCatgoryId(CategoryDef def){
		Set<String> set = new HashSet<String>();
		if(null != def){
			set.add(def.getId());
			CategoryDef[] defs = def.getChildCategorys();
			for(int i=0;null!= defs && defs.length < i;i++) {
				set.addAll(this.getAllVMCatgoryId(defs[i]));
			}
		}
		return set;
	}

	@Override
	public JSONObject getAllResources() throws InstancelibException {
		JSONObject resources = new JSONObject();
		//1.查询所有资源实例
		List<ResourceInstance> list = resSvc.getAllParentInstance();
		//查询所有虚拟化的设备定义的catgoryId
		Set<String> allVMCategoryIdSet = this.getAllVMCatgoryId(capacityService.getCategoryById("VM"));
		
		//2.转换数据，匹配前台
		JSONArray nodes = new JSONArray();
		//获取所有设备类型列表
		List<Map<String, String>> categorys = thirdSvc.getAllCategory();
		for(ResourceInstance instance:list){
			if(!allVMCategoryIdSet.contains(instance.getCategoryId())){
				JSONObject instanceTmp = new JSONObject();
				instanceTmp.put("id", instance.getId());
				instanceTmp.put("ip", instance.getShowIP());
				String showName = instance.getShowName();
				if(StringUtils.isBlank(instance.getShowName())){
					showName = instance.getName();
				}
				instanceTmp.put("showName",StringUtils.isNotBlank(showName) ?showName:defaultConstant);
				Map<String,String> categoryMap = thirdSvc.getDeviceType(categorys, instance.getCategoryId());
				//{"id": "Router","name": "路由器","pid": "NetworkDevice","type": "网络设备"}
				if(categoryMap.isEmpty()){
					instanceTmp.put("typeName",instance.getCategoryId());
				}else{
					String pid = categoryMap.get("pid").toString();
					if(pid.equals("NetworkDevice")){	//网络设备，则取name
						instanceTmp.put("typeName",categoryMap.get("name").toString());
					}else{
						instanceTmp.put("typeName",categoryMap.get("type").toString());
					}
				}
				
				nodes.add(instanceTmp);
			}
		}
		resources.put("devices", nodes);
		return resources;
	}
	
	@Override
	public boolean checkDeviceName(Long instanceId,String name) {
		if(instanceId==null) return false;
		boolean check = false;
		List<ResourceInstance> resources = null;
		try {
			resources = rsvc.getAllParentInstance();
		} catch (InstancelibException e1) {
			logger.error("获取所有资源实例异常",e1);
		}
		for(ResourceInstance resource:resources){
			if(StringUtils.isNotBlank(resource.getShowName()) && resource.getShowName().trim().equals(name)){
				if(resource.getId() != instanceId){
					check = true;
					break;
				}
			}
		}
		return check;
	}
	
	@Override
	public JSONObject save(NodeBo node) {
		JSONObject retn = new JSONObject();
		//检查该ip在该拓扑图中是否存在
		String ip = node.getIp();
		List<NodeBo> currentNodes = new ArrayList<NodeBo>();
		if(ip!=null && !ip.equals("")){
			currentNodes = ndao.getByIp(node.getIp(), node.getSubTopoId());
		}
		if(currentNodes.isEmpty()){
			node.setId(ndao.getSequence().next());
			if(TopoType.SECOND_TOPO.getId()==node.getSubTopoId()){
				node.setSubTopoId(null);
			}
			ndao.insert(node);
			/*if(node.getSubTopoId()!=null && node.getSubTopoId()>TopoType.THIRD_TOPOD.getId()){//如果是飞2，3层拓扑需要在2层拓扑中添加节点
				//查询在二层拓扑中是否存在该ip，如果存在就不需要添加了
				List<NodeBo> scdNodes = ndao.getByIp(node.getIp(), null);
				if(scdNodes.isEmpty()){
					NodeBo tmp=new NodeBo();
					BeanUtils.copyProperties(node, tmp);
					tmp.setSubTopoId(null);
					tmp.setId(ndao.getSequence().next());
					ndao.insert(tmp);
					node.setParentId(tmp.getId());
					ndao.update(Arrays.asList(node));
				}
			}*/
			retn.put("state",200);
			retn.put("msg","添加成功");
		}else{
			retn.put("msg", node.getIp()+"已经存在");
			retn.put("state", 700);
		}
		return retn;
	}
	public INodeDao getNdao() {
		return ndao;
	}
	public void setNdao(INodeDao ndao) {
		this.ndao = ndao;
	}
	
	public ILinkDao getLdao() {
		return ldao;
	}
	public void setLdao(ILinkDao ldao) {
		this.ldao = ldao;
	}
	public IGroupDao getGdao() {
		return gdao;
	}
	public void setGdao(IGroupDao gdao) {
		this.gdao = gdao;
	}
	@Override
	public TopoBo getTopo() {
		TopoBo tb = new TopoBo();
		tb.setNodes(ndao.getAll());
		tb.setLinks(ldao.getAll());
		return tb;
	}
	@Override
	public void saveOrUpdate(List<LinkBo> linkList, List<GroupBo> groupList,List<NodeBo> nodeList,List<OtherNodeBo> othersList) {
		//保存更新节点
//		List<NodeBo> saveNodes = new ArrayList<NodeBo>();
		List<NodeBo> updateNodes = new ArrayList<NodeBo>();
		Map<Long,NodeBo> nodeSaveMap=new HashMap<Long,NodeBo>();
		for(NodeBo nb : nodeList){
			if(null != nb.getId() && nb.getId()>0){
				updateNodes.add(nb);
			}else{
				if(nb.getSubTopoId()==0){
					nb.setSubTopoId(null);
				}
//				saveNodes.add(nb);
				nodeSaveMap.put(nb.getId(), nb);
			}
		}
		//ndao.save(saveNodes);
		ndao.update(updateNodes);
		//保存其他节点
		//List<OtherNodeBo> saveOthers = new ArrayList<OtherNodeBo>();
		List<OtherNodeBo> updateOthers = new ArrayList<OtherNodeBo>();
		Map<Long,OtherNodeBo> otherSaveMap = new HashMap<Long,OtherNodeBo>();
		for(OtherNodeBo on:othersList){
			if(null == on.getId() || on.getId()<=0l){
				//saveOthers.add(on);
				otherSaveMap.put(on.getId(),on);
			}else{
				updateOthers.add(on);
			}
		}
		odao.updateList(updateOthers);
		//odao.saveList(saveOthers);
		//保存更新组-假设现在组中只有node的类型
		/*List<GroupBo> saveGroups = new ArrayList<GroupBo>();
		List<GroupBo> updateGroups = new ArrayList<GroupBo>();
		Map<Long,GroupBo> groupIdRelation = new HashMap<Long,GroupBo>();
		for(GroupBo nb : groupList){
			if(null != nb.getId() && nb.getId()>0){
				updateGroups.add(nb);
				groupIdRelation.put(nb.getId(),nb);
			}else{
				groupIdRelation.put(nb.getId(),nb);
				saveGroups.add(nb);
			}
			//查找组节点中的子node节点，检查那些待定的id,并替换成真实的id
			List<Long> cds = nb.getChildren();
			List<Long> ncds = new ArrayList<Long>();
			for(Long cd:cds){//这些id只可能存在
				if(cd<=0){
					cd=nodeSaveMap.get(cd).getId();
				}
				ncds.add(cd);
			}
			nb.setChildren(ncds);
		}*/
		//gdao.save(saveGroups);
		//gdao.update(updateGroups);
		//更新因组的id，或者children的变更引起的副作用（比如：1 更新前的组中关联该组的node需要删除groupid 2 通知那些因自己id变动而node的goupid没有做相应变动的节点）
		/*for(GroupBo nb : updateGroups){
			ndao.updateGroupId(nb.getId(),nb.getChildren());
		}
		for(GroupBo nb : saveGroups){
			ndao.updateGroupId(nb.getId(),nb.getChildren());
		}*/
		List<LinkBo> saveLinks = new ArrayList<LinkBo>();
		//保存更新链路-链路不存在更新-只存在新增和删除
		for(LinkBo lb : linkList){
			if(null == lb.getId() || lb.getId()<=0){
				//查找那些待定的from,to链路,然后更新它们的id
				if(lb.getFrom()<=0){
					if(lb.getFromType().equals(LinkBo.NODE)){
						lb.setFrom(nodeSaveMap.get(lb.getFrom()).getId());
					}else if(lb.getFromType().equals(LinkBo.OTHER)){
						lb.setFrom(otherSaveMap.get(lb.getFrom()).getId());
					}/*else if(lb.getFromType().equals(LinkBo.GROUP)){
						lb.setFrom(groupIdRelation.get(lb.getFrom()).getId());
					}*/
				}
				if(lb.getTo()<=0){
					if(lb.getToType().equals(LinkBo.NODE)){
						lb.setTo(nodeSaveMap.get(lb.getTo()).getId());
					}else if(lb.getToType().equals(LinkBo.OTHER)){
						lb.setTo(otherSaveMap.get(lb.getTo()).getId());
					}/*else if(lb.getToType().equals(LinkBo.GROUP)){
						lb.setTo(groupIdRelation.get(lb.getTo()).getId());
					}*/
				}
				saveLinks.add(lb);
			}
		}
		ldao.save(saveLinks);
	}
	
	@Override
	public void updateTopoZindexById(Long id, int zIndex, String currentdate) {
		HashMap<Object, Object> map = new HashMap<>();
		map.put("zIndex", zIndex);
		map.put("id", id);
		map.put("currentdate", currentdate);
		odao.updateOtherZIndexById(map);
	}
	
	@Override
	public String getGraph() {
		List<NodeBo> nodeList = ndao.getAll();
		List<GroupBo> groupList = gdao.getAll();
		List<LinkBo> linkList = ldao.getAll();
		for(GroupBo gb : groupList){
			gb.setChildren(ndao.getGroupNodes(gb.getId()));
		}
		Gson gson = new Gson();
		JsonObject retn = new JsonObject();
		retn.add("nodes", gson.toJsonTree(nodeList));
		retn.add("links", gson.toJsonTree(linkList));
		retn.add("groups", gson.toJsonTree(groupList));
		return retn.toString();
	}
	@Override
	public void deleteGraph(List<Long> rlinkList, List<Long> rgroupList,List<Long> rnodeList,Long[] otherIds) {
		ldao.deleteByIds(rlinkList);
		ndao.deleteByIds(rnodeList,false);
		gdao.deleteByIds(rgroupList);
		odao.deleteByIds(Arrays.asList(otherIds));
	}
	@Override
	public void replaceIcon(Long id, String src) {
		ndao.replaceIcon(id,src);
	}
	@Override
	public List<String> selectIps() {
		return ndao.selectIps();
	}
	@Override
	public JSONObject getSubTopoInfo(Long topoId) {
		JSONObject retn = new JSONObject();
		if(null!=topoId){
			SubTopoBo subtopo = sdao.getById(topoId);
			retn.put("id", subtopo.getId());
			retn.put("bgsrc", subtopo.getBgsrc());
			retn.put("name", subtopo.getName());
			retn.put("parentId", subtopo.getParentId());
			List<NodeBo> nbs = ndao.getBySubTopoId(topoId);
			JSONArray nodes = new JSONArray();
			for(NodeBo nb:nbs){
				JSONObject nbJson = (JSONObject) JSON.toJSON(nb);
				nbJson.put("typeName", nb.getTypeName());
				if(nb.getInstanceId()!=null){
					//获取
					JSONObject inst = thirdSvc.getResourceInstance(nb);
					//合并
					for(Map.Entry<String,Object> entry:inst.entrySet()){
						nbJson.put(entry.getKey(),entry.getValue());
					}
				}
				nodes.add(nbJson);
			}
			retn.put("devices", nodes);
		}
		
		return retn;
	}
	@Override
	public List<NodeBo> query(QueryNode query) {
		return ndao.query(query);
	}

	@Override
	public List<NodeBo> queryOne(String ip, String subtopoid) {
		logger.error("进入service queryone" + ip + subtopoid);
		return ndao.queryOne(ip, subtopoid);
	}

	@Override
	public Long addSubTopo(Long parentId,String name, Long[] ids) {
		//保存子拓扑
		SubTopoBo subTopo = new SubTopoBo();
		subTopo.setName(name);
		subTopo.setParentId(parentId);
		Long sid = sdao.add(subTopo);
		List<Long> resIds = new ArrayList<Long>();
		List<NodeBo> enodes = new ArrayList<NodeBo>();
		for(Long id:ids){
			NodeBo tmp = new NodeBo();
			tmp.setId(id);
			enodes.add(tmp);
		}
		//拷贝链路
		List<LinkBo> elinks = ldao.getLinksByNode(enodes);
		for(Long id:ids){
			if(null!=id){
				NodeBo nb = ndao.getById(id);
				//保存一个节点副本
				nb.setSubTopoId(sid);
				nb.setId(ndao.getSequence().next());
				ndao.insert(nb);
				//保存资源id-为极简模式添加数据做准备
				if(null!=nb.getInstanceId()){
					resIds.add(nb.getInstanceId());
				}
				//拷贝
				for(LinkBo lb:elinks){
					if(id.equals(lb.getFrom())){
						lb.setFrom(nb.getId());
						ldao.save(Arrays.asList(lb));
					}else if(id.equals(lb.getTo())){
						lb.setTo(nb.getId());
						ldao.save(Arrays.asList(lb));
					}
				}
			}
		}
		//添加极简模式资源
		thirdSvc.addExtremSimpleTopoRes(sid,resIds);
		return sid;
	}
	
	@Override
	public String getSubTopo(Long id) {
		//获取拓扑全局配置
		SettingBo sp = settingDao.getCfg("globalSetting");
		int showPc = 0;//0全部显示，1只显示网络设备，2只显示pc
		if(sp!=null){
			JSONObject gs = (JSONObject) JSON.parse(sp.getValue());
			Object tmp = gs.getJSONObject("topo").get("showRes");
			if(tmp instanceof JSONArray){
				showPc=0;
			}else if("net".equals(tmp)){
				showPc=1;
			}else if("server".equals(tmp)){
				showPc=2;
			}
		}
		JSONObject retn = new JSONObject();
		//获取子拓扑的属性,背景图片信息，名称
		SubTopoBo sb = sdao.getSimpleAttr(id);
		if(sb!=null){
			retn.put("name", sb.getName());
			retn.put("bgsrc", sb.getBgsrc());
			retn.put("id",sb.getId());
			retn.put("parentId", sb.getParentId());
			//获取当前拓扑所有node节点
			List<NodeBo> nbs = ndao.getBySubTopoId(id);
			//获取和节点相关的链路信息
			List<LinkBo> nodeLinks = ldao.getLinksByNode(nbs);
			//组装节点和组的信息
			Map<Long,GroupBo> group = new HashMap<Long,GroupBo>();
			JSONArray nodes = new JSONArray();
			for(NodeBo nb:nbs){
				if(nb.getIp()==null){
					nb.setIp(DataHelper.NODATA_FLAG);
				}
				if(nb.getShowName()==null){
					nb.setShowName(DataHelper.NODATA_FLAG);
				}
				if(nb.getOid()==null){
					nb.setOid(DataHelper.NODATA_FLAG);
				}
				JSONObject node = (JSONObject) JSON.toJSON(nb);
				//那些自己修改的而非拓扑发现的设备，默认是非监控的状态
				if(null == nb.getInstanceId()){
					node.put("lifeState",InstanceLifeStateEnum.NOT_MONITORED.name().toLowerCase());
				}
				if(nb.getVisible()){
					//都显示
					if(showPc==0){
						nodes.add(node);
					}else if(showPc==1 && nb.isNetDevice()){//只显示网络设备
						nodes.add(node);
					}else if(showPc==2 && !nb.isNetDevice()){//只显示pc服务器
						nodes.add(node);
					}
				}
				//获取组信息
				GroupBo gb = group.get(nb.getGroupId());
				if(null != gb){
					gb.getChildren().add(nb.getId());
				}
			}
			retn.put("nodes", nodes);
			//获取组
			retn.put("groups", JSON.toJSON(group.values()));
			//其他节点
			List<OtherNodeBo> others = odao.getBySubTopoId(id);
			//获取和其他节点相关的链路
			List<LinkBo> otherLinks = ldao.getLinksByOthersNode(others);
			retn.put("others", JSON.toJSON(others));
			//获取链路信息
			//组合otherLinks和nodeLinks链路信息
			List<LinkBo> linkList = new ArrayList<LinkBo>(nodeLinks);
			linkList.addAll(otherLinks); 
			
			//链路去重
			List<LinkBo> linksTmp = new ArrayList<LinkBo>();
			Set<LinkBo> set = new HashSet<LinkBo>();
			set.addAll(linkList);
			linksTmp.addAll(set);
			
			//多链路去重和数据处理
			Map<Long,LinkBo> tmpLinks = this.mutilLinkDistinct(linksTmp);
			retn.put("links", JSON.toJSON(tmpLinks.values()));
		}else{
			sdao.removeById(id);
		}
		//获取其他信息
		return retn.toJSONString();
	}
	
	/**
	 * 多链路去重和数据组合处理
	 * @param linkList
	 * @return
	 */
	private Map<Long,LinkBo> mutilLinkDistinct(List<LinkBo> linkList){
		Map<Long,LinkBo> tmpLinks = new HashMap<Long,LinkBo>();						//返回唯一的链路数据
		Map<String,List<LinkBo>> countMap = new HashMap<String,List<LinkBo>>();		//同一条链路统计集合
		
		//循环标记出多链路
		int size = linkList.size();
		for(int i=0;i<size;i++){
			LinkBo b1 = linkList.get(i);
			long from1 = b1.getFrom(),to1 =  b1.getTo();
			String f_t = from1+"_"+to1,t_f = to1+"_"+from1;
			List<LinkBo> countLinks = new ArrayList<LinkBo>();
			for(int j=i;j<size;j++){
				LinkBo b2 = linkList.get(j);
				long from2 = b2.getFrom(),to2 =  b2.getTo();
				if(countMap.containsKey(f_t) || countMap.containsKey(t_f)) break;					//计算过的不再计算
				if((from1==from2 && to1==to2) || (from1==to2 && from2==to1)) countLinks.add(b2);	//同一条链路判断（方向可能不同）
				if(j == size-1) countMap.put(f_t, countLinks);	//记录相同链路
			}
		}
		
		//组合链路数据
		for(String key:countMap.keySet()){
			List<LinkBo> links = countMap.get(key);
			//大于等于三条链路显示为多链路
			if(links.size() > 2){	//>=3条显示为多链路
//			if(links.size() > 1){	//>=2条显示为多链路
				LinkBo link = links.get(0);
				for(LinkBo linkTmp:links){
					if(null == link.getInstanceId() && null != linkTmp.getInstanceId()){
						link = linkTmp;
					}
				}
				//组合多链路instanceId，保证多链路中有实例化的id(涉及到前端refreshState时能刷新到多链路状态，前台根据链路instanceId匹配刷新颜色状态)
				link.setMultiNumber(links.size());	//组合条数
				tmpLinks.put(link.getId(), link);	//最终组合的链路数据集合
			}else{
				for(LinkBo linkTmp:links){
					tmpLinks.put(linkTmp.getId(), linkTmp);	//最终组合的链路数据集合
				}
			}
		}
		return tmpLinks;
	}
	
	@Override
	public String subTopos(Long parentId) {
		JSONArray retn = new JSONArray();
		try {
			List<SubTopoBo> topos = sdao.getByParentId(parentId);
			for(SubTopoBo sb:topos){
				JSONObject tmp = new JSONObject();
				if(parentId==null||parentId==0){
					if(sb.getId()==1 || sb.getId()==0 || sb.getId()==2){
						continue;
					}
				}
				//检查当前用户是否有查看权限
				if(authSvc.hasAuth(sb.getId(), new String[]{TopoAuthSettingBo.SELECT})){
					tmp.put("text", sb.getName());
					tmp.put("id", sb.getId());
					tmp.put("parentId", null);
					getSubTopos(tmp,sb.getId());
					retn.add(tmp);
				}
			}
		} catch (Exception e) {
			logger.error("TopoGraphImpl.subTopos",e);
		}
		return retn.toJSONString();
	}
	@Override
	public JSONObject findNodesBySubnetIp(String ip) {
		JSONObject retn = new JSONObject();
		CoreIps ci = new CoreIps();
		String ipadd = ip;
		String mask = "255.255.255.255";
		if(null!=ip && ip.contains("/")){
			String[] parts = ip.split("\\/");
			ipadd=parts[0];
			StringBuilder sb = new StringBuilder();
			int digit = Integer.parseInt(parts[1]);
			int total=0,count=0;
			for(int i=1;i<=digit;++i){
				int tmp = i%8;
				total+=Math.pow(2,(8-tmp)%8);
				if(tmp==0){
					if(count<3){
						sb.append(total).append(".");
					}else{
						sb.append(total);
					}
					count++;
					total=0;
				}else if(i==digit){
					if(count<3){
						sb.append(total).append(".");
					}else{
						sb.append(total);
					}
					count++;
					total=0;
				}
			}
			//补全
			for(int i=1;i<=4-count;++i){
				if(i==4-count){
					sb.append("0");
				}else{
					sb.append("0.");
				}
			}
			mask=sb.toString();
		}
		ci.setSubnetIps(ipadd, mask);
		Set<String> ips = ci.getAllIps();
		List<NodeBo> nodes = ndao.queryByIps(new ArrayList<String>(ips),null);
		
		//查询资源名称
		List<Long> instanceIds = new ArrayList<Long>();
		for(NodeBo node:nodes){
			if(node.getInstanceId()!=null){
				instanceIds.add(node.getInstanceId());
			}
		}
		
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		try {
			instances = resSvc.getResourceInstances(instanceIds);
		} catch (InstancelibException e) {
			logger.error("查询资源实例名称失败",e);
		}
		
		for(NodeBo node:nodes){
			if(node.getInstanceId()!=null){
				for(ResourceInstance instance:instances){
					if(node.getInstanceId() == instance.getId()){
						node.setShowName(instance.getShowName());
					}
				}
			}
		}
		
		JSONArray nodesjson = (JSONArray)JSON.toJSON(nodes);
		retn.put("nodes", nodesjson);
		return retn;
	}
	private void getSubTopos(JSONObject retn,Long id){
		if(id==null || id.equals(0l)) return ;
		List<SubTopoBo> parents = sdao.getByParentId(id);
		if(parents.size()>0){
			JSONArray children = new JSONArray();
			retn.put("children", children);
			retn.put("state", "closed");
			for(SubTopoBo sb:parents){
				JSONObject tmp = new JSONObject();
				tmp.put("text", sb.getName());
				tmp.put("id", sb.getId());
				tmp.put("parentId", sb.getParentId());
				children.add(tmp);
				getSubTopos(tmp,sb.getId());
			}
		}
	}
	@Override
	public JSONArray getSubToposByIp(String ip) {
		List<NodeBo> nodes = ndao.getByIp(ip, new Long(TopoType.SECOND_TOPO.getId()));
		List<Long> nodeIds = new ArrayList<Long>(nodes.size());
		for(NodeBo nb : nodes){
			nodeIds.add(nb.getId());
		}
		List<SubTopoBo> subTopos = sdao.getSubToposByIp(ip);
		//搜索机房
		List<Long> roomSubtopoIds = odao.getRoomByNodeIds(nodeIds);
		for(Long id : roomSubtopoIds){
			subTopos.add(sdao.getById(id));
		}
		return (JSONArray)JSON.toJSON(subTopos);
	}
	@Override
	@Transactional
	public JSONObject addNewElemToSubTopo(Long topoId, Long[] ids) {
		SubTopoBo sb = sdao.getById(topoId);
		JSONObject msg = new JSONObject();
		if(null!=sb){
			//查找该拓扑的所有节点
			List<NodeBo> nbs = ndao.getBySubTopoId(topoId);
			//做一个标记列表
			Map<String,Boolean> existed = new HashMap<String,Boolean>();
			for(NodeBo nb:nbs){
				existed.put(nb.getIp(), true);
			}
			//添加非已添加的节点
			List<Long> toAdd = new ArrayList<Long>();
			for(Long id:ids){
				NodeBo nb = ndao.getById(id);
				if(null==existed.get(nb.getIp())){//如果不存在才添加
					toAdd.add(nb.getId());
				}
			}
			sb.setToAdd(toAdd.toArray(new Long[0]));
			sb.setToDelete(new Long[]{});
			updateSubTopo(sb);
			msg.put("msg", "添加成功");
		}else{
			msg.put("msg", "添加失败");
		}
		return msg;
	}
	@Override
	public JSONObject hasTopo() {
		JSONObject retn = new JSONObject();
		boolean hasTopo = ndao.hasTopo();
		retn.put("hasTopo", hasTopo);
		//如果存在拓扑，返回拓扑首页的id
		if(hasTopo){
			SettingBo sb = settingDao.getCfg("stm_topo_graph_homepage");
			if(null!=sb){
				retn.put("homeTopo", JSON.parse(sb.getValue()));
			}
		}
		return retn;
	}
	@Override
	public JSONObject hasTopo2(String name) {
		JSONObject retn = new JSONObject();
		retn.put("topoid", subtopoService.getSubTopoId(name));
		return retn;
	}
	@Override
	public String allTopos() {
		JSONArray retn = new JSONArray();
		//添加二层主拓扑
		JSONObject twoLevelTopo = new JSONObject();
		twoLevelTopo.put("name", "二层拓扑");        
		twoLevelTopo.put("id",0);             
		twoLevelTopo.put("parentId", null);
		retn.add(twoLevelTopo);
		//添加三层主拓扑
		JSONObject threeLevelTopo = new JSONObject();
		threeLevelTopo.put("name", "三层拓扑");        
		threeLevelTopo.put("id",NodeBo.THIRDTOPO_ID);             
		threeLevelTopo.put("parentId", null);
		retn.add(threeLevelTopo);
		//添加其他子拓扑
		List<SubTopoBo> subtopos = sdao.all();
		for(SubTopoBo sb:subtopos){
			if(sb.getParentId()==null){
				continue;
			}
			JSONObject tmp = new JSONObject();
			tmp.put("name", sb.getName());
			tmp.put("id",sb.getId());
			tmp.put("parentId", sb.getParentId());
			retn.add(tmp);
		}
		return retn.toJSONString();
	}
	private String convertBandWidth(String bw,String unit){
		if(!isBandWidthValid(bw)){
			return bw;
		}else{
			long divide = 1;
			double tmp = Double.valueOf(bw);
			if(tmp<1000000l && tmp>1000){
				unit="Kbps";
			}else if(tmp<1000){
				unit="Bps";
			}else{
				unit="Mbps";
			}
			switch(unit){
			case "Bps":
				divide=1;
				break;
			case "Kbps":
				divide=1000;
				break;
			case "Mbps":
				divide = 1000*1000;
			}
			BigDecimal base = new BigDecimal(bw);
			BigDecimal result = base.divide(new BigDecimal(divide)).setScale(2,BigDecimal.ROUND_HALF_DOWN);
			return result.toString()+unit;
		}
	}
	private boolean isBandWidthValid(String bw){
		if("--".equals(bw)|| defaultConstant.equals(bw) || null == bw){
			return false;
		}else{
			return true;
		}
	}
	private void packageLinkData(Map<String,?> data,String unit,JSONObject collector){
		//上行流量
		String upSpeed = resourceExApi.getMetricVal(data, "ifInOctetsSpeed", null);
		collector.put("upSpeed",convertBandWidth(upSpeed,unit));
		//下行流量
		String downspeed = resourceExApi.getMetricVal(data,"ifOutOctetsSpeed",null);
		collector.put("downSpeed",convertBandWidth(downspeed,unit));
		//总流量
		String totalSpeed = resourceExApi.getMetricVal(data,MetricIdConsts.METRIC_THROUGHPUT,null);
		if(!isBandWidthValid(totalSpeed) && isBandWidthValid(upSpeed) && isBandWidthValid(downspeed)){
			totalSpeed=(Double.parseDouble(upSpeed)+Double.parseDouble(downspeed))+"";
		}
		collector.put("totalSpeed",convertBandWidth(totalSpeed,unit));
	}
	@Override
	public JSONObject getMetricInfoByInstanceId(Long instanceId,String type,String unit) throws InstancelibException {
		JSONObject retn = new JSONObject();
		ResourceInstance parentInstance = resourceService.getResourceInstance(instanceId);
		if(null != parentInstance){
			Long collResourceId=instanceId;
			if("link".equals(type) && null!=collResourceId){//链路数据
				if(InstanceLifeStateEnum.NOT_MONITORED != parentInstance.getLifeState()){
					retn.put("upSpeed", dataHelper.getPortReceiveSpeed(collResourceId, unit));
					retn.put("downSpeed", dataHelper.getPortSendSpeed(collResourceId, unit));
					retn.put("totalSpeed", dataHelper.getPortTotalFlow(collResourceId, unit));
				}else{
					retn.put("upSpeed",DataHelper.NODATA_FLAG);
					retn.put("downSpeed",DataHelper.NODATA_FLAG);
					retn.put("totalSpeed",DataHelper.NODATA_FLAG);
				}
			}else{
				List<Map<String,?>> datas = resourceExApi.getMerictRealTimeVals(new String[]{MetricIdConsts.METRIC_CPU_RATE,MetricIdConsts.METRIC_MEME_RATE}, new long[]{instanceId});
				for(Map<String,?> data:datas){
					//cpu利用率
					String cpuRate = resourceExApi.getMetricVal(data, MetricIdConsts.METRIC_CPU_RATE, null);
					try {
						retn.put("cpuRate",Float.parseFloat(cpuRate)/100);
					} catch (NumberFormatException e) {
						retn.put("cpuRate",cpuRate);
					}
					//内存利用率
					String rameRate = resourceExApi.getMetricVal(data, MetricIdConsts.METRIC_MEME_RATE,null);
					try {
						retn.put("ramRate",Float.parseFloat(rameRate)/100);
					} catch (NumberFormatException e) {
						retn.put("ramRate",rameRate);
					}
					//cpu利用率状态
					JSONObject cpuJson = thirdSvc.getMetricState(instanceId,MetricIdConsts.METRIC_CPU_RATE,false);
					retn.put("cpuState", cpuJson.getString("state"));
					//内存利用率状态
					JSONObject ramJson = thirdSvc.getMetricState(instanceId,MetricIdConsts.METRIC_MEME_RATE,false);
					retn.put("ramState", ramJson.getString("state"));
				}
			}
		}
		return retn;
	}
	
	@Override
	public String updateSubTopo(SubTopoBo sb) {
		//更新子拓扑的基本属性
		sdao.updateAttr(sb);
		Long[] toAdd = sb.getToAdd();
		Long[] toDelete=sb.getToDelete();
		if((toAdd!=null && toAdd.length>0)||(toDelete!=null && toDelete.length>0)){
			ndao.deleteByIds(Arrays.asList(toDelete),false);
			//极简模式删除
			thirdSvc.delExtremSimpleTopoRes(sb.getId(), Arrays.asList(toDelete));
			List<NodeBo> enodes = ndao.getBySubTopoId(sb.getId());
			//获取添加节点已存在的所有链路
			Map<Long,NodeBo> relation = new HashMap<Long,NodeBo>();
			List<Long> tmpToAdd=new ArrayList<Long>();
			for(Long id:toAdd){
				NodeBo nb = ndao.getById(id);
				nb.setSubTopoId(sb.getId());
				nb.setParentId(id);
				ndao.save(Arrays.asList(nb));
				//获取新id
				tmpToAdd.add(nb.getId());
				enodes.add(nb);
				relation.put(id, nb);
			}
			//极简模式添加
			thirdSvc.addExtremSimpleTopoRes(sb.getId(),tmpToAdd);
			List<LinkBo> elinks = ldao.getLinksByNode(enodes);
			//删除老链路
			for(Map.Entry<Long, NodeBo> entry:relation.entrySet()){
				NodeBo nb = entry.getValue();
				for(LinkBo lb:elinks){
					if(entry.getKey().equals(lb.getFrom())){
						lb.setFrom(nb.getId());
						ldao.save(Arrays.asList(lb));
					}else if(entry.getKey().equals(lb.getTo())){
						lb.setTo(nb.getId());
						ldao.save(Arrays.asList(lb));
					}
				}
			}
		}
		return "更新成功";
	}
	@Override
	public JSONObject getProfileIdByInstanceId(Long instanceId) {
		return thirdSvc.getProfileIdByInstanceId(instanceId);
	}
	@Override
	public JSONArray refreshLinkDataByIds(Long[] ids) {
		List<Long> colIdsList = new ArrayList<Long>();
		Map<Long,ResourceInstance> idRelation = new HashMap<Long,ResourceInstance>();
		List<ResourceInstance> reses=new ArrayList<ResourceInstance>();
		try {
			reses = resSvc.getResourceInstances(Arrays.asList(ids));
		} catch (InstancelibException e1) {
			logger.error("获取资源实例异常",e1);
		}
		for(ResourceInstance re:reses){
			try {
				Long id = re.getId();
				colIdsList.add(id);
				idRelation.put(id, re);
			} catch (NumberFormatException e) {
				logger.error("refreshLinkDataByIds", e);
			}
		}
		long[] colIds = new long[ids.length];
		int i=0;
		for(long id:colIdsList){
			colIds[i++]=id;
		}
		JSONArray retn = new JSONArray();
		List<Map<String,?>> datas = resourceExApi.getMerictRealTimeVals(new String[]{"ifInOctetsSpeed","ifOutOctetsSpeed",MetricIdConsts.METRIC_THROUGHPUT},colIds);
		for(Map<String,?> data:datas){
			JSONObject tmp = new JSONObject();
			packageLinkData(data,"Mbps",tmp);
			Long colInstanceId = null;
			try {
				colInstanceId = Long.parseLong(data.get("instanceid").toString());
			} catch (NumberFormatException e) {
				logger.error("long转换异常",e);
			} catch (Exception e){
				logger.error(e);
			}
			if(colInstanceId!=null){
				ResourceInstance re = idRelation.get(colInstanceId);
				tmp.put("instanceId",re.getId());
				tmp.put("note",re.getModulePropBykey("note")[0]);
			}
			retn.add(tmp);
		}
		return retn;
	}
	@Override
	public JSONArray refreshLifeState(long[] ids) {
		JSONArray retn =  thirdSvc.refreshLifeState(ids);
		return retn;
	}
	@Override
	public String deleteSubtopo(Long id,boolean recursive) {
		if(id!=null){
			//极简模式删除资源ids
			Set<Long> topoids = subtopoService.removeById(id,recursive);
			//判断删除的是否拓扑图首页
			this.updateTopoGraphHomePage(topoids);
		}
		return "删除成功";
	}
	
	/**
	 * 检测拓扑图首页设置信息
	 * @param id
	 */
	private void updateTopoGraphHomePage(Set<Long> ids){
		String key = "stm_topo_graph_homepage";
		//获取拓扑首页
		SettingBo po = settingDao.getCfg(key);
		
		if(null != po){
			JSONObject value = JSONObject.parseObject(po.getValue());
			if(ids.contains(value.getLongValue("id"))){
				//删除首页
				settingDao.deleteCfg(key);
				return;
			}
		}
	}
	@Override
	public String refreshVendorName(Long[] ids) {
		JSONArray retn = new JSONArray();
		List<NodeBo> nbs = ndao.getByIds(Arrays.asList(ids));
		for(NodeBo nb : nbs){
			String oid = nb.getOid();
			if(oid==null && nb.getInstanceId()!=null){
				oid=dataHelper.getResourceInstanceOid(nb.getInstanceId());
			}
			if(oid!=null){
				JSONObject tmp = thirdSvc.getVendorInfo(oid);
				tmp.put("oid",nb.getOid());
				tmp.put("id", nb.getId());
				retn.add(tmp);
			}else{
				logger.error(String.format("%s-%s has no sysoid",nb.getId(),nb.getIp()));
			}
		}
		return retn.toJSONString();
	}
	@Override
	public JSONObject newLink(String info) {
		JSONObject linkInfo = (JSONObject)JSON.parseObject(info);
		Long srcIfIndex = null;
		if (linkInfo.containsKey("srcIfIndex")) {
			srcIfIndex = linkInfo.getLong("srcIfIndex");
		}	
		Long desIfIndex = null;
		if (linkInfo.containsKey("desIfIndex")) {
			desIfIndex = linkInfo.getLong("desIfIndex");
		}
		Long instanceId = thirdSvc.newLink(linkInfo);
		if(instanceId!=null){
			LinkBo lb = null;
			if(linkInfo.containsKey("id")){
				lb = ldao.getById(linkInfo.getLong("id"));
				lb.setInstanceId(instanceId);
				lb.setFromIfIndex(srcIfIndex);
				lb.setToIfIndex(desIfIndex);
				lb.setType("link");
				ldao.updateInstanceId(lb);
			}else{
				lb = new LinkBo();
				lb.setFrom(linkInfo.getLong("fromId"));
				lb.setTo(linkInfo.getLong("toId"));
				lb.setFromType(linkInfo.getString("fromType"));
				lb.setToType(linkInfo.getString("toType"));
				lb.setInstanceId(instanceId);
				lb.setFromIfIndex(srcIfIndex);
				lb.setToIfIndex(desIfIndex);
				lb.setType("link");
				ldao.save(Arrays.asList(lb));
			}
			JSONObject retn = (JSONObject)JSON.toJSON(lb);
			return retn;
		}
		return linkInfo;
	}
	@Override
	public int subTopoNameValidation(Long parentId, String subTopoName) {
		return sdao.subTopoNameValidation(parentId, subTopoName);
	}
	@Override
	public void removeNode(List<Long> ids,boolean isPhysicalDelete) {
		//查找所有孩子节点
		List<Long> childrenIds = ndao.getChildrenIdByParentIds(ids);
		childrenIds.addAll(ids);
		ndao.deleteByIds(childrenIds,isPhysicalDelete);
		ldao.deleteByNodeIds(childrenIds,isPhysicalDelete);
	}
	@Override
	public void removeOther(List<Long> ids) {
		//1.删除节点
		odao.deleteByIds(ids);
		//2.删除节点右键关联配置
		//TODO:lo
		for(Long id:ids){
			settingDao.deleteCfg(otherStr+id);
		}
	}
	@Override
	public void removeGroup(List<Long> ids) {
		gdao.deleteByIds(ids);
	}
	@Override
	public void addOther(OtherNodeBo ob) throws Exception {
		String attr = this.getImgSize(ob);
		if(null != attr) 
			ob.setAttr(attr);	//获取图片真实尺寸
		odao.saveList(Arrays.asList(ob));
	}
	
	/**
	 * 获取图片尺寸（宽*高）
	 * @param path
	 * @throws Exception 
	 */
	private String getImgSize(OtherNodeBo ob) throws Exception{
		JSONObject attr = JSONObject.parseObject(ob.getAttr());
		String imgType = attr.getString("type"),result = attr.toJSONString();
		if("image".equals(imgType)){
			String imgSrc = attr.getString("src");
			BufferedImage image = null;
			if(imgSrc.contains(".htm?")){	//获取上传的自定义图片
				Long imgId = Long.valueOf(imgSrc.substring(imgSrc.lastIndexOf("=")+1));
				image = topoImageApi.getImage(imgType, imgId);
			}else{	//获取服务器静态图片
				HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest(); 
				if(imgSrc.indexOf("resource")<0) imgSrc = "resource/"+imgSrc;
//				String realPath = request.getSession().getServletContext().getRealPath(imgSrc);
				String realPath = request.getSession().getServletContext().getRealPath("/")+"/"+imgSrc;
				image = topoImageApi.getImage(imgType,realPath);
			}
			if(null != image){
				attr.put("w", image.getWidth());
				attr.put("h", image.getHeight());
				result = attr.toJSONString();
			}
		}
		
		return result;
	}
	
	@Override
	public void updateOther(OtherNodeBo ob) {
		odao.updateList(Arrays.asList(ob));
	}
	@Override
	public JSONArray getCoreNodesInSubtopoById(Long parentSubTopoId,Long subTopoId) {
		List<NodeBo> nbs = ndao.getCoreNodesInSubtopoById(parentSubTopoId,subTopoId);
		return (JSONArray)JSON.toJSON(nbs);
	}
	@Override
	public NodeBo updateResourceInstance(Long id, Long instanceId,String ip) {
		NodeBo nb = ndao.getById(id);
		if(nb!=null){
			nb.setInstanceId(instanceId);
			nb.setIp(ip);
			ndao.updateInstanceId(nb);
		}
		return nb;
	}
	@Override
	public void hideNodes(Long[] ids) {
		if(ids!=null && ids.length>0){
			ndao.hideNodes(ids);
		}
	}
	@Override
	public JSONArray getHideNodes(Long subTopoId) {
		List<NodeBo> nodes = ndao.getHideNodesBySubtopoId(subTopoId);
		JSONArray retn = new JSONArray();
		for(NodeBo nb : nodes){
			JSONObject tmp = new JSONObject();
			tmp.put("oid", nb.getOid());
			tmp.put("ip", nb.getIp());
			tmp.put("typeName", nb.getTypeName());
			tmp.put("id", nb.getId());
			if(nb.getInstanceId()!=null){
				JSONObject jo = thirdSvc.getResourceInstance(nb);
				if(jo.containsKey("showName")){
					tmp.put("showName", jo.get("showName"));
				}
			}
			if(tmp.containsKey("showName")){
				tmp.put("showName", nb.getIp());
			}
			retn.add(tmp);
		}
		return retn;
	}
	@Override
	public JSONObject showHideNodes(Long[] ids) {
		JSONObject retn = new JSONObject();
		//设置可见标识
		ndao.setVisibleable(ids);
		//获取节点信息
		List<NodeBo> nodes = ndao.getByIds(Arrays.asList(ids));
		retn.put("nodes",JSON.toJSON(nodes));
		//获取节点所对应的链路
		List<LinkBo> links = ldao.getLinksByNode(nodes);
		//多链路去重和数据处理
		Map<Long,LinkBo> tmpLinks = this.mutilLinkDistinct(links);
		List<LinkBo> linkTmps = new ArrayList<LinkBo>(tmpLinks.values());
		retn.put("links", JSON.toJSON(linkTmps));
		return retn;
	}
	private void addIp(JSONObject json,String ip,Long id){
		if(ip==null || "".equals(ip)) return ;
		if(!json.containsKey(ip)){
			json.put(ip, new JSONArray());
		}
		JSONArray nodesArray = json.getJSONArray(ip);
		nodesArray.add(id);
	}
	@Override
	public JSONObject getAllIpsForSubtopo(Long subTopoId) throws InstancelibException {
		logger.error("getAllIpsForSubtopo----ok");
		JSONObject retn = new JSONObject();
		try{
			List<NodeBo> nodes = ndao.getBySubTopoId(subTopoId);
			for(NodeBo nb : nodes){
				if(null!=nb.getInstanceId()){
					ResourceInstance res = rsvc.getResourceInstance(nb.getInstanceId());
					if(res!=null){
						String [] ips = res.getModulePropBykey(MetricIdConsts.METRIC_IP);
						if(null!=ips){
							for(String ip:ips){
								addIp(retn, ip, nb.getId());
							}
						}
					}
				}
				addIp(retn, nb.getIp(), nb.getId());
			}
		}catch(Throwable a){
			logger.error("",a);
		}
		return retn;
	}
	@Override
	public JSONArray getAllIpsForNode(Long id) throws InstancelibException {
		JSONArray retn = new JSONArray();
		if(null!=id){
			NodeBo nb = ndao.getById(id);
			if(null==nb.getInstanceId()){
				retn.add(nb.getIp());
			}else{
				ResourceInstance res = rsvc.getResourceInstance(nb.getInstanceId());
				if(res!=null){
					String [] ips = res.getModulePropBykey(MetricIdConsts.METRIC_IP);
					if(ips!=null){
						for(String ip:ips){
							retn.add(ip);
						}
					}
				}
			}
		}else{
			throw new RuntimeException("id is null");
		}
		return retn;
	}
	@Override
	public JSONObject updateResourceBaseInfo(JSONObject nodeInfo) throws InstancelibException {
		JSONObject retn = new JSONObject();
		//更新显示名称
		if(nodeInfo.containsKey("instanceId")){
			if(nodeInfo.containsKey("name")){
				rsvc.updateResourceInstanceName(nodeInfo.getLongValue("instanceId"), nodeInfo.getString("name"));
				retn.put("name", nodeInfo.getString("name"));
			}
			if(nodeInfo.containsKey("manageIp")){
				rsvc.updateResourceInstanceShowIP(nodeInfo.getLongValue("instanceId"), nodeInfo.getString("manageIp"));
				retn.put("manageIp", nodeInfo.getString("manageIp"));
			}
		}
			
		//更新图元ip
		if(nodeInfo.containsKey("id")){
			NodeBo node = ndao.getById(nodeInfo.getLong("id"));
			node.setIp(nodeInfo.getString("manageIp"));
			JSONObject attr = node.getAttrJson();
			attr.put("name", nodeInfo.getString("name"));
			node.setAttr(attr.toJSONString());
			ndao.update(Arrays.asList(node));
		}
		return retn;
	}

	@Override
	public int deleteSubTopoByName(String name) {
		return sdao.deleteSubTopoByName(name);
	}
}
