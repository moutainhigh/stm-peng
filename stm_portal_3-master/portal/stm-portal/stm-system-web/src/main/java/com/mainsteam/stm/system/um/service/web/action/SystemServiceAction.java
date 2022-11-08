package com.mainsteam.stm.system.um.service.web.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.cache.MemcacheManager;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.um.service.web.vo.NodeTreeVo;
import com.mainsteam.stm.system.um.service.web.vo.NodeVo;

/**
 * <li>文件名称: AccessControl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 系统服务Action</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月5日
 * @author zhangjunfeng
 */
@Controller
@RequestMapping("system/service")
public class SystemServiceAction extends BaseAction {
	
	@Autowired
	private NodeService nodeService;

	@RequestMapping("getNodeForTree")
	public JSONObject getAllNodeForTree() {
		try {
			List<NodeTreeVo> nodeTreeVos = new ArrayList<NodeTreeVo>();
			NodeTable table = nodeService.getNodeTable();
			if (null != table) {
				List<Node> pNodes = table.selectNodesByType(NodeFunc.processer);// 获取处理器列表
				List<Node> cNodes = table.selectNodesByType(NodeFunc.collector);// 获取采集器列表
				for (Node pNode : pNodes) {
					NodeTreeVo pTreeVo = new NodeTreeVo();
					Boolean state = true;
					List<NodeTreeVo> cNodeTreeVos = new ArrayList<NodeTreeVo>();
					for (Node cNode : cNodes) {
						if(cNode.getParentNodeId()==pNode.getId()){
							if (!cNode.isAlive()) {
								state = false;
							}
							NodeTreeVo cTreeVo = new NodeTreeVo();
							BeanUtils.copyProperties(cNode, cTreeVo);
							if(pNode.isAlive()){
								cTreeVo.setIconCls(cNode.isAlive() ? "alert-ico lightnormal"
										: "alert-ico redlight");
							}else{
								cTreeVo.setIconCls("alert-ico grizzly");
							}
							cNodeTreeVos.add(cTreeVo);
						}
					}
					BeanUtils.copyProperties(pNode, pTreeVo);
					if(pTreeVo.isAlive()){
						if (state) {//tree-icon-width25 
							pTreeVo.setIconCls(pTreeVo.isAlive() ? "alert-ico lightnormal"
									: "alert-ico redlight");
						} else {
							pTreeVo.setIconCls("alert-ico yellowlight");
						}
					}else{
						pTreeVo.setIconCls("alert-ico redlight");
					}
					pTreeVo.setChildren(cNodeTreeVos);
					nodeTreeVos.add(pTreeVo);
				}
			}
			return toSuccess(nodeTreeVos);
		} catch (NodeException e) {
			e.printStackTrace();
			return toSuccess(null);
		}
	}

	@RequestMapping("getNodeById")
	public JSONObject getNodeById(Integer id) {
		if (null != id && id != 0) {
			try {
				Node node = nodeService.getNodeById(id);
				NodeVo nodeVo = new NodeVo();
				BeanUtils.copyProperties(node, nodeVo);
				return toSuccess(nodeVo);
			} catch (NodeException e) {
				e.printStackTrace();
			}
		}
		return toSuccess(null);
	}

	@RequestMapping("updateNodeName")
	public JSONObject updateNodeName(Integer id, String name,String description) {
		if (null != id && id != 0) {
			try {
				Node node = nodeService.getNodeById(id);
				node.setName(name);
				node.setDescription(description==null?"":description);
				nodeService.updateNode(node);
				return toSuccess(true);
			} catch (NodeException e) {
				e.printStackTrace();
			}
		}
		return toSuccess(false);
	}
	
	@RequestMapping("getMemcachedStatus")
	public JSONObject getMemcachedStatus(){
		return toSuccess(MemCacheFactory.isActivate());
	}
	
	@RequestMapping("getMemcachedData")
	public JSONObject getMemcachedData(){
		int nodeId = 1;
		List<NodeTreeVo> nodeTreeVos = new ArrayList<NodeTreeVo>();
		NodeTreeVo pTreeVo = new NodeTreeVo();
		pTreeVo.setChildren(new ArrayList<NodeTreeVo>());
		pTreeVo.setName("缓存服务");
		pTreeVo.setId(nodeId ++);
		
		if(MemCacheFactory.isActivate()){
			pTreeVo.setIconCls("alert-ico lightnormal");
		}
		
		Map<String, Map<String, String>> map = MemcacheManager.getMemcachedDaemon().stats();
		Iterator<Entry<String, Map<String, String>>> entries = map.entrySet().iterator();  
		while (entries.hasNext()) {
		    Entry<String, Map<String, String>> entry = entries.next();
		    String key = entry.getKey();
		    Map<String, String> value = entry.getValue();
		    NodeTreeVo children = new NodeTreeVo();
			children.setName(key);
			children.setDescription(JSON.toJSONString(value));
			children.setId(nodeId ++);
			pTreeVo.getChildren().add(children);
		}
		nodeTreeVos.add(pTreeVo);
		return toSuccess(nodeTreeVos);
	}
	
	@RequestMapping("getPortalRegisterStatus")
	public JSONObject getPortalRegisterStatus(){
		
		IMemcache<Boolean> icache=MemCacheFactory.getLocalMemCache(Boolean.class);

		return toSuccess(icache.get("ITM_REGISER"));
	}
}
