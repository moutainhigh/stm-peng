package com.mainsteam.stm.topo.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.RecycleService;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
@Service
public class RecycleServiceImpl implements RecycleService {
	private Logger logger = Logger.getLogger(RecycleServiceImpl.class);
	@Autowired
	private INodeDao nodeDao;
	@Autowired
	private ISubTopoDao subtopoDao;
	@Autowired
	private DataHelper dataHelper;
	@Autowired
	private ILinkDao linkDao;
	//资源实例服务
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService rsvc;
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@Override
	public void del(Long[] ids) {
		//根据节点找到链路删除
		List<Long> nodeIds = Arrays.asList(ids);
		List<Long> linkInstanceIds = linkDao.getDelLinkInstancdeIdsByNodeIds(nodeIds);
		if(null != linkInstanceIds){
			try {
				rsvc.removeResourceInstanceByLinks(linkInstanceIds);
			} catch (InstancelibException e) {
				logger.error("删除实例链路异常",e);
			}
		}
		//物理删除
		nodeDao.deleteByIds(nodeIds, true);
	}
	@Override
	public JSONArray list() {
		List<NodeBo> nodes = nodeDao.listLogicalDeleted();
		JSONArray items = new JSONArray();
		for(NodeBo node : nodes){
			Long instanceId = node.getInstanceId();
			if(null!=instanceId && authSvc.hasAuth(node.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//只返回有操作权限的
				ResourceInstance inst = dataHelper.getResourceInstance(instanceId);
				JSONObject item = new JSONObject();
				item.put("id", node.getId());
				item.put("instanceId",instanceId);
				item.put("showName",dataHelper.getResourceInstanceShowName(inst));
				item.put("ip", dataHelper.getResourceInstanceManageIp(inst));
				item.put("typeName", node.getTypeName());
				SubTopoBo sb = subtopoDao.getById(node.getSubTopoId());
				item.put("topoName", sb.getName());
				items.add(item);
			}
		}
		return items;
	}
	@Override
	public void recover(Long[] ids) {
		nodeDao.recoverLogicalDelete(ids);
	}
}
