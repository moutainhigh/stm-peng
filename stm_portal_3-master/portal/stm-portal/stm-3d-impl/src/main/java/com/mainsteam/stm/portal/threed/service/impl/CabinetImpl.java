package com.mainsteam.stm.portal.threed.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.threed.api.ICabinetApi;
import com.mainsteam.stm.portal.threed.api.IUrlApi;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.dao.ICabinetDao;
import com.mainsteam.stm.portal.threed.dao.IUrlDao;
import com.mainsteam.stm.portal.threed.dao.impl.Adapter3DInterfaceImpl;
import com.mainsteam.stm.portal.threed.dao.impl.DataStreamImpl;
import com.mainsteam.stm.portal.threed.util.MetricUtil;
import com.mainsteam.stm.portal.threed.util.jaxb.Metric;
import com.mainsteam.stm.portal.threed.util.jaxb.Metrics;
import com.mainsteam.stm.portal.threed.xfire.exception.ThreeDException;

public class CabinetImpl implements ICabinetApi, InstancelibListener {
	private IUrlApi urlApi;
//	private DataStreamImpl dataStreamImpl;
	@Resource
	private Adapter3DInterfaceImpl adapter3DInterfaceImpl;
	
	@Autowired
	private ICabinetDao cabinetDao;
	@Autowired
	private IUrlDao urlDao;

	private MetricDataService metricDataService;

	private ResourceInstanceService resourceInstanceService;
	private Logger logger = Logger.getLogger(CabinetImpl.class);
	
	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setUrlApi(IUrlApi urlApi) {
		this.urlApi = urlApi;
	}

//	public void setDataStreamImpl(DataStreamImpl dataStreamImpl) {
//		this.dataStreamImpl = dataStreamImpl;
//	}

	@Override
	public int batchAdd(List<CabinetBo> boList) throws Exception {
		// ???????????????,?????????????????????
		cabinetDao.batchRemoveByList(boList);
		int rows = cabinetDao.batchAdd(boList);
		adapter3DInterfaceImpl.deleteRackEquipmentByList(boList);
		BaseResult result = adapter3DInterfaceImpl.addRackEquipment(boList);
		if (!result.isSuccess())
			throw new ThreeDException(result.getData());
		/**
		 * ????????????????????????3D?????????????????????????????????????????????3D??????????????????,???????????????
		 */
		// //????????????????????????
		// List<CabinetBo> updateList = new ArrayList<CabinetBo>();
		// //????????????????????????
		// List<CabinetBo> addList = new ArrayList<CabinetBo>();
		// for(CabinetBo bo : boList){
		// if(StringUtils.isEmpty(bo.getBelong())){
		// addList.add(bo);
		// }else{
		// updateList.add(bo);
		// }
		// bo.setBelong(cabinet);
		// }
		// if(!updateList.isEmpty()){
		// BaseResult result = dataStreamImpl.updateRackEquipment(updateList);
		// if(!result.isSuccess()) throw new ThreeDException(result.getData());
		// }
		// if(!addList.isEmpty()){
		// BaseResult result = dataStreamImpl.addRackEquipment(addList);
		// if(!result.isSuccess()) throw new ThreeDException(result.getData());
		// }
		return rows;
	}

	@Override
	public int update(CabinetBo bo) throws Exception {
		BaseResult result = adapter3DInterfaceImpl.updateRackEquipment(bo);
		if (!result.isSuccess())
			throw new ThreeDException(result.getData());
		return cabinetDao.update(bo);
	}

	@Override
	public int batchRemove(long[] ids) throws Exception {
		if (ids.length < 1)
			return 0;
		BaseResult result = adapter3DInterfaceImpl.deleteRackEquipmentByArray(ids);
		if (!result.isSuccess())
			throw new ThreeDException(result.getData());
		return cabinetDao.batchRemoveByArray(ids);
	}

	@Override
	public String getNodeTree() throws Exception {
		String nodeTree = adapter3DInterfaceImpl.getNodeTree();
		// ??????????????????
		UrlBo bo = new UrlBo();
		UrlBo info = urlApi.get3DInfo();
		int port = info.getPort();
		bo.setPort(port);
		
		bo.setNodeTree(nodeTree);
		urlApi.update(bo);
		return nodeTree;
	}

	@Override
	public void getPage(Page<CabinetBo, String> page) {
		String sort = page.getSort();
		if(!StringUtils.isEmpty(sort) 
				&& ("ip".equals(sort)||"name".equals(sort)||"type".equals(sort))){
			page.setSort(null);
		}
		cabinetDao.getPage(page);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent) {
		try {
			UrlBo urlBo = urlDao.get3DInfo();
			if(null != urlBo && urlBo.getStatus()!=null&&("1").equals(urlBo.getStatus())){
				if (instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT) {
					// deleteIds ???????????????????????????Id ??????
					List<Long> deleteIds = (List<Long>) instancelibEvent
							.getSource();
					if (deleteIds != null && deleteIds.size() > 0) {
						long[] ids = new long[deleteIds.size()];
						for (int i = 0; i < deleteIds.size(); i++) {
							ids[i] = deleteIds.get(i);
						}
						// ??????????????????????????????????????????????????????
						this.batchRemove(ids);
						logger.info("del relations of 3d resource successful");
					}
				}
			}
		} catch (Exception e) {
			logger.error("del relations of 3d resource failure");
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Override
	public BaseResult pushMoniter() throws Exception {
		// ??????????????????????????????????????????
		Metrics metrics = MetricUtil.getMetrics();
		List<Metric> metricList = metrics.getMetricList();
		String[] metricID = new String[metricList.size()];
		for (int i = 0; i < metricID.length; i++) {
			metricID[i] = metricList.get(i).getId();
		}
		// ???????????????????????????????????????
		List<CabinetBo> bos = cabinetDao.queryAllDevice();
		List<Long> ids = new ArrayList<Long>();
		for (CabinetBo bo : bos) {
			ids.add(bo.getId());
		}
		if (metricID.length == 0 || ids.size() == 0) {
			BaseResult result = new BaseResult();
			result.setData("??????????????????????????????????????????????????????");
			return result;
		}

		// ???server?????????????????????
		List<ResourceInstance> instances = resourceInstanceService.getResourceInstances(ids);
		// ???????????????????????????
		List<ResourceInstance> moniteredInstances = new ArrayList<ResourceInstance>();
		for (ResourceInstance instance : instances) {
			if (instance.getLifeState() == InstanceLifeStateEnum.MONITORED) {
				moniteredInstances.add(instance);
			}
		}
		long[] instanceID = new long[moniteredInstances.size()];
		for (int i = 0; i < instanceID.length; i++) {
			instanceID[i] = moniteredInstances.get(i).getId();
		}
		// ????????????????????????????????????
		MetricRealtimeDataQuery query = new MetricRealtimeDataQuery();
		query.setMetricID(metricID);
		query.setInstanceID(instanceID);
		// ???????????????????????????
		List<Map<String, ?>> instanceMetrics = metricDataService.queryRealTimeMetricData(query);
		// ??????????????????
		BaseResult result = adapter3DInterfaceImpl.pushMonitor(instanceMetrics,metricList);
		return result;
	}

	@Override
	public List<CabinetBo> getAllCabinetResource() throws Exception {
		return cabinetDao.queryAllDevice();
	}
}
