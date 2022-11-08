package com.mainsteam.stm.topo.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.MaplineService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.dao.MapLineDao;
@Service
public class MaplineServiceImpl implements MaplineService{
	private Logger logger = Logger.getLogger(MaplineServiceImpl.class);
	@Autowired
	private MapLineDao maplineDao;
	@Autowired
	private ThirdService thirdSvc;
	@Autowired
	private LinkService linkService;
	@Autowired
	private DataHelper dataHelper;
	//资源实例服务
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService rsvc;
	
	@Override
	public void addLine(MapLineBo line) {
		if(!maplineDao.isExsisted(line)){
			maplineDao.add(line);
		}else{
			throw new RuntimeException("重复线路");
		}
	}
	@Override
	public void remove(Long id) {
		//1.删除链路实例
		MapLineBo line = maplineDao.getLineById(id);
		if(null != line && null != line.getInstanceId()){
			List<Long> ids = new ArrayList<Long>();
			ids.add(line.getInstanceId());
			try {
				rsvc.removeResourceInstanceByLinks(ids);
			} catch (InstancelibException e) {
				logger.error("删除地图链路异常",e);
			}
		}
		//2.删除连线
		maplineDao.remove(id);
	}
	@Override
	public Long convertToLink(JSONObject linkInfo) {
		Long instanceId = thirdSvc.newLink(linkInfo);
		if(instanceId!=null){
			//更新instanceId
			MapLineBo line = maplineDao.getLineById(linkInfo.getLong("id"));
			if(null!=line){
				line.setInstanceId(instanceId);
				maplineDao.updateLink(line);
				return instanceId;
			}
		}
		return null;
	}
	@Override
	public JSONArray unbindLinks(Long[] ids) {
		JSONArray retn = new JSONArray(ids.length);
		for(Long id : ids){
			try {
				maplineDao.unbindLink(id);
				retn.add(id);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return retn;
	}
}
