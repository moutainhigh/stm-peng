package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.topo.api.OtherNodeService;
import com.mainsteam.stm.topo.api.SubTopoService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.api.TopoClipboardService;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.IOthersNodeDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import com.mainsteam.stm.topo.dao.ITopoFindDao;
import com.mainsteam.stm.topo.enums.TopoType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Service
public class SubTopoServiceImpl implements SubTopoService{
	private Logger logger = Logger.getLogger(SubTopoServiceImpl.class);
	@Autowired
	private ISubTopoDao subtopoDao;
	@Autowired
	private ILinkDao linkDao;
	@Autowired
	private INodeDao nodeDao;
	@Autowired
	private ThirdService thirdService;
	@Autowired
	private TopoClipboardService clipService;
	@Autowired
	private ITopoFindDao tdao;
	@Autowired
	@Qualifier("licenseCalcService")
	private ILicenseCalcService license;
	@Autowired
	private IOthersNodeDao otherDao;
	@Autowired
	private OtherNodeService otherService;
	
	/**
	 * 更新拓扑右侧导航菜单排序号
	 * @param subtopoTree
	 */
	@Override
	public void updateNavSort(String subtopoTree) {
		JSONArray tree = JSONArray.parseArray(subtopoTree);
		for(Object subtree:tree){
			JSONObject tmp = (JSONObject) JSONObject.toJSON(subtree);
			SubTopoBo bo = new SubTopoBo();
			bo.setId(tmp.getLong("id"));
			bo.setParentId(tmp.getLong("parentId"));
			bo.setSort(tmp.getLong("sort"));
			
			subtopoDao.updateSort(bo);	//更新排序
			
			String childrenStr = tmp.getString("children");
			if(!StringUtils.isEmpty(childrenStr)){
				this.updateNavSort(childrenStr);
			}
		}
	}
	
	@Override
	public JSONObject createOrUpdateSubtopo(SubTopoBo sb,Long[] downMoveIds,Long[] upMoveIds,Long[] delIds) {
		//检查名称是否重复
		/*拓扑图允许重名(无锡人民法院需求)
		if(null!=sb.getName() && sb.getId()==null){
			int flag = subtopoDao.subTopoNameValidation(sb.getParentId(), sb.getName());
			if(flag>0){//更新和新建都需要做重复名称判断
				throw new RuntimeException("相同名称已经存在");
			}
		}*/
		//新建
		if(null==sb.getId()){
			String attrStr = sb.getAttr();
			if(attrStr==null){
				attrStr="{}";
			}
            //添加成功后返回的记录ID
            Long recodID = null;
            JSONObject attr = JSON.parseObject(attrStr);
			if(attr.containsKey("type") && "room".equals(attr.getString("type"))){
				if(license.isLicenseEnough(LicenseModelEnum.stmMonitorTopMr)){
                    recodID = subtopoDao.add(sb);
                }else{
					logger.error("license not enough(topo room)");
					throw new RuntimeException("机房数超过license授权数量");
				}
			}else{
                recodID = subtopoDao.add(sb);
            }
            if (recodID != null) {
                //得到当前parentId下子节点数
                long num = subtopoDao.countByParentId(sb.getParentId() == null ? 0 : sb.getParentId());
                sb.setSort(num - 1 < 0 ? 0 : num - 1);
                subtopoDao.updateSort(sb);
            }
        }else{//更新
			//待删除得资源id列表
			List<Long> delInstIds = new ArrayList<Long>();
			//更新拓扑图属性等
			SubTopoBo tmpSubtopo = subtopoDao.getById(sb.getId());
			//判断名称是否重名，但是允许本身不变
			/*拓扑图允许重名(无锡人民法院需求)
			if(!tmpSubtopo.getName().equals(sb.getName())){
				if(subtopoDao.subTopoNameValidation(sb.getParentId(), sb.getName())>0){
					throw new RuntimeException("相同名称已经存在");
				}
			}*/
			tmpSubtopo.setName(sb.getName());
			subtopoDao.updateAttr(tmpSubtopo);
			//更新图元类型type=subtopo的节点名称
			List<NodeBo> subtopoNodes = nodeDao.getSubtopoNodeBySubtopoId(sb.getId());
			for(NodeBo nb : subtopoNodes){
				JSONObject attr = nb.getAttrJson();
				attr.put("name", sb.getName());
				nb.setAttr(attr.toJSONString());
			}
			nodeDao.update(subtopoNodes);
			if(delIds!=null){
				List<Long> ids = Arrays.asList(delIds);
				List<NodeBo> nodes = nodeDao.getByIds(ids);
				for(NodeBo node : nodes){
					if(null!=node.getInstanceId()){
						delInstIds.add(node.getInstanceId());
					}
				}
				nodeDao.deleteByIds(ids, true);
			}
			//删除极简模式的资源
			thirdService.delExtremSimpleTopoRes(sb.getId(),delInstIds);
		}
		//有没有移动的节点
		if(downMoveIds!=null && downMoveIds.length>0){
			clipService.move(sb.getId(), downMoveIds);
		}
		//有没有移动的节点
		if(upMoveIds!=null && upMoveIds.length>0){
			clipService.move(sb.getParentId(), upMoveIds);
		}
		addNewElementToSubTopo(sb);
		JSONObject subtopoJson =(JSONObject) JSON.toJSON(sb);
		return subtopoJson;
	}
	@Override
	public void addNewElementToSubTopo(SubTopoBo sb) {
		if(sb.getId()!=null){
			//记录添加到此拓扑的资源id
			List<Long> instIds = new ArrayList<Long>();
			//复制该parent拓扑下的节点
			if(sb.getIds().length==0) return;
			List<NodeBo> nodes = nodeDao.getByIds(Arrays.asList(sb.getIds()));
			List<NodeBo> saveNodes = new ArrayList<NodeBo>();
			//获取当前拓扑图中的所有节点
			List<NodeBo> dbnodes = nodeDao.getBySubTopoId(sb.getId());
			Map<String,NodeBo> dbIpRela=new HashMap<String,NodeBo>();
			for(NodeBo node : dbnodes){
				dbIpRela.put(node.getIp(),node);
			}
			List<NodeBo> linkNodes = new ArrayList<NodeBo>(nodes);
			//保存id关系
			Map<Long,NodeBo> relation = new HashMap<Long,NodeBo>();
			//更新复制的节点的subtopoid
			for(NodeBo node : nodes){
				if(!dbIpRela.containsKey(node.getIp())){
					//保存上层拓扑的链路的id
					List<Long> tmpLinksId = linkDao.getLinksIdByNode(node);
					JSONObject attr = node.getAttrJson();
					attr.put("links", tmpLinksId);
					node.setAttr(attr.toJSONString());
					/*BUG #40508 拓扑管理：新建的子拓扑，默认应显示在页面中央位置 huangping 2017/6/29 start*/
					node.setX(new Double(0L));
					node.setY(new Double(0L));
					/*BUG #40508 拓扑管理：新建的子拓扑，默认应显示在页面中央位置 huangping 2017/6/29 end*/
					saveNodes.add(node);
				}
				node.setVisible(true);
				node.setSubTopoId(sb.getId());
				node.setParentId(node.getId());
				if(null!=node.getInstanceId()){
					//添加到资源id列表中
					instIds.add(node.getInstanceId());
				}
				relation.put(node.getId(), node);
			}
			//查找dbnodes的二层拓扑对应的节点保存到关系列表中去
			for(NodeBo node : dbnodes){
				List<NodeBo> nbs = nodeDao.getByIp(node.getIp(),0l);
				if(!nbs.isEmpty()){
					NodeBo tmp = nbs.get(0);
					relation.put(tmp.getId(), node);
					linkNodes.add(tmp);
				}
			}
			//复制这些节点的链路
			//List<LinkBo> links = linkDao.getLinksByNode(linkNodes);
			//复制
			nodeDao.save(saveNodes);
			//更新链路的两端连接的节点
			/*List<LinkBo> toCopyLinks = new ArrayList<LinkBo>();
			for(LinkBo link : links){
				if(relation.containsKey(link.getFrom()) && relation.containsKey(link.getTo())){
					Long from = relation.get(link.getFrom()).getId();
					Long to = relation.get(link.getTo()).getId();
					link.setFrom(from);
					link.setTo(to);
					if(linkDao.findLink(link).isEmpty()){
						link.setParentId(link.getId());
						toCopyLinks.add(link);
					}
				}
			}
			//复制链路
			linkDao.save(toCopyLinks);*/
			//复制因复制过来的节点和剪切过来的节点之间的链路关系
			clipService.copyCopyLinks(Arrays.asList(sb.getIds()),sb.getId());
			//添加极简模式
			try {
				thirdService.addExtremSimpleTopoRes(sb.getId(), instIds);
			} catch (Throwable e) {
				logger.error("极简模式添加资源异常",e);
			}
		}
	}
	@Override
	public void updateAttr(SubTopoBo sb) {
		if(sb!=null && sb.getId()!=null){
			SubTopoBo dbSubtopo = subtopoDao.getById(sb.getId());
			if(dbSubtopo!=null){
				String attr = dbSubtopo.getAttr();
				if(attr==null || "".equals(attr)){
					attr="{}";
				}
				JSONObject dbAttr = JSON.parseObject(attr);
				String nattr = sb.getAttr();
				if(nattr==null || "".equals(nattr)){
					nattr="{}";
				}
				JSONObject tmpAttr = JSON.parseObject(nattr);
				for(Map.Entry<String,Object> entry : tmpAttr.entrySet()){
					dbAttr.put(entry.getKey(),entry.getValue());
				}
				sb.setAttr(dbAttr.toJSONString());
			}
			subtopoDao.updateAttr(sb);
		}
	}
	@Override
	public JSONObject getAttr(Long subTopoId) {
		SubTopoBo sb = subtopoDao.getById(subTopoId);
		TopoType type = getTopoType(subTopoId);
		if(sb!=null){
			JSONObject retn = (JSONObject) JSON.toJSON(sb);
			retn.put("topoType", type.getName());
			return retn;
		}else{
			return new JSONObject();
		}
	}

	@Override
	public JSONObject getSubTopoIdBySubTopoName(String name) {
		if(StringUtils.isEmpty(name)){
			name="二层拓扑";
		}
		JSONObject retn = new JSONObject();
		List<SubTopoBo> subTopos = subtopoDao.getByName(name);
		if(null!=subTopos && !subTopos.isEmpty()){
			retn.put("status",200);
			retn.put("id",subTopos.get(0).getId());
		}else{
			retn.put("status",700);
			retn.put("msg","没有名为"+name+"的拓扑");
		}
		return retn;
	}
	@Override
	public TopoType getTopoType(Long topoId) {
		//寻根问祖
		try {
			SubTopoBo sb = subtopoDao.getById(topoId);
			while(sb.getParentId()!=null && !sb.getParentId().equals(0l)){
				sb=subtopoDao.getById(sb.getParentId());
			}
			return TopoType.byId(sb.getId());
		}catch (Exception e) {
			return TopoType.UNKNOWN_TOPO;
		}
	}
	@Override
	public Set<Long> removeById(Long id,boolean recursive) {
		Set<Long> topoids = new HashSet<Long>();
		List<NodeBo> nodes = nodeDao.getBySubTopoId(id);
		SubTopoBo topo = subtopoDao.getById(id);
		//处理极简模式业务
		delExtremSimpleTopoRes(nodes,id);
		//获取此拓扑所有子拓扑中非重复的节点
		Map<Long,NodeBo> toMoveNodes = new HashMap<Long,NodeBo>();
		List<SubTopoBo> topos = subtopoDao.getChildrenTopos(id);
		//处理极简模式业务
		for(SubTopoBo sb : topos){
			//如果递归删除，需要把所有的子拓扑节点移动到父拓扑
			if(recursive){
				List<NodeBo> tmpNodes = nodeDao.getBySubTopoId(sb.getId());
				delExtremSimpleTopoRes(tmpNodes,sb.getId());
				nodes.addAll(tmpNodes);
			}
			//非递归删除，只需要将当前的节点移动到父拓扑，但是需要把它的直接子拓扑的移动一级
			else{
				if(sb.getParentId().equals(id)){
					sb.setParentId(topo.getParentId());
					subtopoDao.updateAttr(sb);
				}
			}
		}
		List<Long> ids = new ArrayList<Long>();
		//拓扑级别越高，优先级越高
		for(NodeBo node : nodes){
			if((node.getInstanceId()!=null && !toMoveNodes.containsKey(node.getInstanceId()))||node.getType().equals("subtopo")){
				if(node.getInstanceId()!=null){
					toMoveNodes.put(node.getInstanceId(), node);
					ids.add(node.getId());
				}
				if(node.getType().equals("subtopo") && !recursive){
					ids.add(node.getId());
				}
			}
			
		}
		//因为二层拓扑不能删除，所以不需要判断是否getParentId为null
		//移动节点
		if(topo!=null){
			clipService.move(topo.getParentId(), ids.toArray(new Long[]{}));
			//删除拓扑
			tdao.deleteSubTopo(id);
			//如果有相关的图元还需要删除图元
			List<NodeBo> subtopoNodes = nodeDao.getSubtopoNodeBySubtopoId(id);
			List<Long> subtopoNodeIds = new ArrayList<Long>();
			for(NodeBo n : subtopoNodes){
				subtopoNodeIds.add(n.getId());
			}
			nodeDao.deleteByIds(subtopoNodeIds, true);
			if(recursive){
				for(SubTopoBo sb : topos){
					topoids.add(sb.getId());
					tdao.deleteSubTopo(sb.getId());
				}
			}
		}
		return topoids;
	}
	public void delExtremSimpleTopoRes(List<NodeBo> nodes,Long topoId){
		List<Long> ids = new ArrayList<Long>();
		for(NodeBo n : nodes){
			if(n.getInstanceId()!=null){
				ids.add(n.getInstanceId());
			}
			
		}
		thirdService.delExtremSimpleTopoRes(topoId, ids);
	}
	@Override
	public boolean isTopoRoomEnabled() {
		try {
			boolean retn = License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelTopoMr)==1;
			return retn;
		} catch (LicenseCheckException e) {
			return false;
		}
	}
	@Override
	public void deleteRoom(Long id) {
		//删除机柜
		List<OtherNodeBo> cabinets = otherDao.findCabinetInRoom(id);
		for(OtherNodeBo cabinet : cabinets){
			otherService.removeCabinet(cabinet.getId());
		}
		//删除子拓扑
		removeById(id, false);
	}
	@Override
	public Long getSubTopoId(String name) {
		return subtopoDao.getSubTopoId(name);
	}
}
