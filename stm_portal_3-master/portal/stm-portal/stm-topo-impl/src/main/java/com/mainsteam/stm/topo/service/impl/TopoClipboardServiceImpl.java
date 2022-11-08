package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.SubTopoService;
import com.mainsteam.stm.topo.api.TopoClipboardService;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TopoClipboardServiceImpl implements TopoClipboardService {
	@Autowired
	private INodeDao nodeDao;
	@Autowired
	private ILinkDao linkDao;
	@Autowired
	private ISubTopoDao subtopoDao; 
	@Autowired
	private SubTopoService subtopoService;
	private List<NodeBo> getDuplicateNodes(NodeBo node,Long topoId){
		List<NodeBo> tmpNodes = new ArrayList<NodeBo>();
		if(node.getInstanceId()!=null){
			tmpNodes=nodeDao.getByInstanceId(node.getInstanceId(),topoId);
		}else if(node.getIp()!=null){
			tmpNodes = nodeDao.getByIp(node.getIp(), topoId);
		}
		return tmpNodes;
	}
	@Override
	public JSONArray copy(Long topoId, Long[] ids) {
		JSONArray retn = new JSONArray();
		//将要复制的节点
		List<Long> toCopyIds = new ArrayList<Long>();
		//查找ids的节点
		List<NodeBo> copyNodes=nodeDao.getByIds(Arrays.asList(ids));
		//检查topoId的拓扑有没有重复的
		for(NodeBo node : copyNodes){
			List<NodeBo> tmpNodes = getDuplicateNodes(node,topoId);
			//不存在重复，就直接拷贝
			if(tmpNodes.isEmpty()){
				toCopyIds.add(node.getId());
			}else{//重复的返回给用户待处理
				boolean flag = false;
				for(NodeBo tmp : tmpNodes){
					if(tmp.getDeleteFlag()){
						tmp.setDeleteFlag(false);
						nodeDao.recoverLogicalDelete(new Long[]{tmp.getId()});
						flag=true;
					}
				}
				if(!flag){
					retn.add(getDuplicateInfo(node,tmpNodes.get(0)));
				}
			}
		}
		SubTopoBo sb = new SubTopoBo();
		sb.setId(topoId);
		sb.setIds(toCopyIds.toArray(new Long[]{}));
		subtopoService.addNewElementToSubTopo(sb);
		return retn;
	}
	private JSONObject getDuplicateInfo(NodeBo duplicateNode,NodeBo originalNode){
		JSONObject tmp = new JSONObject();
		tmp.put("ip",originalNode.getIp());
		tmp.put("originalId", originalNode.getId());
		tmp.put("id",duplicateNode.getId());
		return tmp;
	}
	@Override
	public void copyCopyLinks(List<Long> toCopyIds, Long topoId) {
		List<NodeBo> nodes = nodeDao.getBySubTopoId(topoId);
		List<NodeBo> copyNodes = new ArrayList<NodeBo>();
		Map<String,NodeBo> dbNodes = new HashMap<String,NodeBo>();
		//分离当前拷贝的节点和其他已存的节点
		for(NodeBo node : nodes){
			if(toCopyIds.contains(node.getParentId())){
				copyNodes.add(node);
			}
			dbNodes.put(node.getIp(), node);
		}
		//查询拷贝的节点和已经存在的节点直接是否有链路
		for(NodeBo node : copyNodes){
			copyNodeLinks(node,dbNodes);
		}
	}
	private void copyNodeLinks(NodeBo node,Map<String,NodeBo> relation) {
		JSONObject attr = node.getAttrJson();
		if(attr.containsKey("links")){
			JSONArray ids = attr.getJSONArray("links");
            Long[] tmpIds = new Long[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                tmpIds[i] = ids.getLong(i);
            }
            List<LinkBo> tmpLinks = linkDao.getByIds(tmpIds);
            for(LinkBo link : tmpLinks){
				Long from = link.getFrom(),to=link.getTo();
				NodeBo fromNode = nodeDao.getById(from);
				NodeBo toNode = nodeDao.getById(to);
				if(fromNode!=null && toNode!=null && relation.containsKey(fromNode.getIp()) && relation.containsKey(toNode.getIp())){
					link.setFrom(relation.get(fromNode.getIp()).getId());
					link.setTo(relation.get(toNode.getIp()).getId());
					link.setParentId(link.getId());	//被复制的链路Id
					link.setId(null);
					List<LinkBo> lks = linkDao.findLink(link);
					if(!lks.isEmpty()){
						Long instId = link.getInstanceId();
						//多链路保存
						//查找所有的lks，如果有资源实例相等的，证明存在同一链路，那么不保存
						boolean isInstIdAllNull=true,hasSameLink=false;
						for(LinkBo lb : lks){
							Long _instId=lb.getInstanceId();
							if(_instId!=null){
								isInstIdAllNull=false;
								if(_instId.equals(instId)){
									hasSameLink=true;
									break;
								}
							}
						}
						//下边两个条件不可能同时成立的，所以，最多一个执行
						//保存连线
						if(isInstIdAllNull){
                            continue;
                        }
						//保存链路
						if(!hasSameLink){
							linkDao.save(link);
						}
					}else{
						linkDao.save(link);
					}
				}
			}
		}
	}
	@Override
	public JSONArray move(Long topoId, Long[] ids) {
		JSONArray retn = new JSONArray();
		//查找ids的节点
		List<NodeBo> nodes = nodeDao.getByIds(Arrays.asList(ids));
		//待移动的节点
		List<NodeBo> toMoveNodes = new ArrayList<NodeBo>();
		//检查topoId的拓扑有没有重复的
		for(NodeBo node : nodes){
			List<NodeBo> tmpNodes = getDuplicateNodes(node,topoId);
			//不存在重复，就直接移动
			if(tmpNodes.isEmpty()){
				//移动到子拓扑去
				node.setSubTopoId(topoId);
				toMoveNodes.add(node);
			}else{//重复的返回给用户待处理
				//检查重复的节点是否是删除的，是删除的就物理删除，然后把再把节点移动过来
				boolean flag =false;
				for(NodeBo tmpNode : tmpNodes){
					if(tmpNode.getDeleteFlag()){
						nodeDao.deleteByIds(Arrays.asList(tmpNode.getId()), true);
						toMoveNodes.add(node);
						//如果是拓扑类型的节点，需要调整拓扑的从属关系
						if(node.getType().equals("subtopo")){
							JSONObject attr = node.getAttrJson();
							if(attr.containsKey("subtopoId")){
								Long tmpTopoId = attr.getLong("subtopoId");
								SubTopoBo topobo = subtopoDao.getById(tmpTopoId);
								if(topobo!=null){
									topobo.setParentId(topoId);
									subtopoDao.updateAttr(topobo);
								}
							}
						}
						flag=true;
					}
				}
				if(!flag){
					retn.add(getDuplicateInfo(node,tmpNodes.get(0)));
				}
			}
		}
		//更新移动的节点
		nodeDao.update(toMoveNodes);
		//复制剪切过来的节点和复制过来的节点之间的链路关系
		copyMoveLink(toMoveNodes,topoId);
		//返回重复的设备
		return retn;
	}
	/**
	 * 复制移动的节点和当前拓扑连接的链路
	 * @param toMoveNodes 待移动的节点
	 * @param topoId 移动到哪一个拓扑图id
	 */
	private void copyMoveLink(List<NodeBo> toMoveNodes,Long topoId) {
		Map<String,NodeBo> relation = new HashMap<String,NodeBo>();
		for(NodeBo node : toMoveNodes){
			relation.put(node.getIp(), node);
		}
		List<NodeBo> nodes = nodeDao.findCopyNodesForTopo(topoId);
		for(NodeBo node : nodes){
			copyNodeLinks(node,relation);
		}
	}
	@Override
	public void replace(JSONObject replaceMap,Long topoId,boolean isMove) {
		List<Long> toDelete=new ArrayList<Long>();
		List<Long> toMove=new ArrayList<Long>();
		for(Map.Entry<String,Object> entry : replaceMap.entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			Long originalId = Long.valueOf(key);
			Long replaceId = Long.valueOf(value.toString());
			//排除同一拓扑图的剪切复制
			if(!originalId.equals(replaceId)){
				toDelete.add(originalId);
				toMove.add(replaceId);
			}
		}
		linkDao.deleteByNodeIds(toDelete, true);
		nodeDao.deleteByIds(toDelete, true);
		if(!isMove){
			copy(topoId, toMove.toArray(new Long[]{}));
		}else{
			move(topoId,toMove.toArray(new Long[]{}));
		}
	}
}
