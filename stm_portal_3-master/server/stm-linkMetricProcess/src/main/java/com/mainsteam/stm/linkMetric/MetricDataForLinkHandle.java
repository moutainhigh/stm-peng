package com.mainsteam.stm.linkMetric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessEngine;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.util.TimerTaskUtil;

public class MetricDataForLinkHandle implements MetricDataProcessor,
		InitializingBean {

	private static final Log logger = LogFactory
			.getLog(MetricDataForLinkHandle.class);
	private CapacityService capacityService;
	private ResourceInstanceService resourceInstanceService;
	private MetricDataProcessEngine metricDataProcessEngine;
	private Map<String, String> metricList = new HashMap<>();
	private ProfileService profileService;

	private final static String ifAvailability = "ifAvailability";
	/**
	 * 部分主机的网卡的可用性指标为ifStatus
	 */
	private final static String ifStatus_host = "ifStatus";

	private final static String AVA_METRIC = "availability";
	/** 用于存放采集端链路接口,设备值*/
	private Map<Long, List<Long>> resLinkMap = new HashMap<>();
	
	/** 用于存放采集端链路接口值*/
	private Map<Long, List<Long>> linkCollSubMap = new HashMap<>();

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	@Override
	public MetricDataPersistence process(MetricCalculateData data,
			ResourceMetricDef rdf, CustomMetric cm,
			Map<String, Object> contextData) throws Exception {

		if (rdf != null && metricList.containsKey(data.getMetricId())) {
			List<Long> linkInsIDes = resLinkMap.get(data.getResourceInstanceId());
			if (linkInsIDes != null && linkInsIDes.size() > 0) {
				/**
				 * ifOperStatus不再用來计算链路的可用性状态，因为已经使用ifAvailability进行了计算。
				 * 
				 * modify by ziw at 2016年8月12日 下午4:05:29
				 */
				String metricId = metricList.get(data.getMetricId());
				/**
				 * 如果是性能指标，只取采集接口值
				 */
				if(!(ifAvailability.equals(metricId) || ifStatus_host.equals(metricId) || AVA_METRIC.equals(metricId))){
					linkInsIDes = linkCollSubMap.get(data.getResourceInstanceId());
				}
				if(linkInsIDes != null){
					for (Long linkInsID : linkInsIDes) {
						MetricCalculateData newData = new MetricCalculateData();
						BeanUtils.copyProperties(data, newData);
						newData.setResourceId(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
						newData.setMetricId(metricId);
						newData.setResourceInstanceId(linkInsID);
						newData.setTimelineId(0);
						newData.setFromInstanceId(data.getResourceInstanceId());
						ProfileInfo pfi = profileService.getBasicInfoByResourceInstanceId(linkInsID);
						if (pfi == null) {
							logger.warn("can't find ProfileInfo[instanceid:" + linkInsID + "],please check!");
							newData.setProfileId(0);
						} else {
							newData.setProfileId(pfi.getProfileId());
						}
						if(logger.isDebugEnabled()){
							logger.debug("src data " + data);
							logger.debug("link add to metricDataProcessEngine = " + newData);
						}
						metricDataProcessEngine.handle(newData);
					}
				}
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("metricid[" + data.getMetricId() + ","
							+ data.getResourceInstanceId()
							+ "] can't find link!");
				}
			}
		}
		return null;
	}

	public void syncResLink() throws InstancelibException {
		List<ResourceInstance> resList = resourceInstanceService
				.getResourceInstanceByResourceId(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
		if (resList == null) {
			resLinkMap.clear();
			linkCollSubMap.clear();
		} else {
			Map<Long, List<Long>> tmpMap = new HashMap<>();
			Map<Long,List<Long>> tmpCollMap=new HashMap<>();
			for (ResourceInstance res : resList) {
				if(res.getLifeState() == InstanceLifeStateEnum.MONITORED){
					String[] src_ids = res.getModulePropBykey(LinkResourceConsts.PROP_SRC_SUBINST_ID);
					String[] dest_ids = res.getModulePropBykey(LinkResourceConsts.PROP_DEST_SUBINST_ID);
					String[] src_main_ids = res.getModulePropBykey(LinkResourceConsts.PROP_SRC_MAININST_ID);
					String[] dest_main_ids = res.getModulePropBykey(LinkResourceConsts.PROP_DEST_MAININST_ID);
					
					String[] coll_ids = res.getModulePropBykey(LinkResourceConsts.PROP_COLL_SUBINST_ID);
					if(coll_ids != null && coll_ids.length > 0){
						Long id=Long.parseLong(coll_ids[0]);
						List<Long> linkes=tmpCollMap.get(id);
						if(linkes==null){
							linkes=new ArrayList<Long>();
							tmpCollMap.put(id, linkes);
						}
						linkes.add(res.getId());
					}
					
					long[] endpointIds = new long[4];
					try{
						if(null != src_ids && src_ids.length >0) {
							endpointIds[0] = Long.parseLong(src_ids[0]);
						}
						if(null != dest_ids && dest_ids.length >0) {
							endpointIds[1] = Long.parseLong(dest_ids[0]);
						}
						if(null != src_main_ids && src_main_ids.length >0) {
							endpointIds[2] = Long.parseLong(src_main_ids[0]);
						}
						if(null != dest_main_ids && dest_main_ids.length >0) {
							endpointIds[3] = Long.parseLong(dest_main_ids[0]);
						}
					}catch(Exception e) {
						if(logger.isWarnEnabled()) {
							logger.warn(e.getMessage(), e);
						}
						continue;
					}
					for(long id : endpointIds) {
						if(id != 0L) {
							List<Long> linkes = tmpMap.get(id);
							if (linkes == null) {
								linkes = new ArrayList<Long>();
								tmpMap.put(id, linkes);
							}
							linkes.add(res.getId());
						}
					}

				}
			}
			resLinkMap = tmpMap;
			linkCollSubMap = tmpCollMap;
			if (logger.isDebugEnabled())
				logger.debug("syncResLink finished:" + JSON.toJSONString(resLinkMap));
		}
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setMetricDataProcessEngine(
			MetricDataProcessEngine metricDataProcessEngine) {
		this.metricDataProcessEngine = metricDataProcessEngine;
	}

	@Override
	public int getOrder() {
		return 5;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ResourceDef rdfes = capacityService.getResourceDefById(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
		if (rdfes != null) {
			for (ResourceMetricDef rdf : rdfes.getMetricDefs()) {
				metricList.put(rdf.getId(), rdf.getId());
			}
		}
		metricList.put(ifAvailability, AVA_METRIC);
		metricList.put(ifStatus_host, AVA_METRIC);

		if (logger.isInfoEnabled()) {
			logger.info("link metricList:" + JSON.toJSONString(metricList));
		}
		TimerTaskUtil.schedule(new TimerTaskUtil.CallBack() {
			@Override
			public void call() {
				try {
					syncResLink();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}, 2000, 30000);
	}

}
