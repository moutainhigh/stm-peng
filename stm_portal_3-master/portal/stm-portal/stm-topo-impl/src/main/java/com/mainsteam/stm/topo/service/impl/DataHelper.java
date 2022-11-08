package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.util.TopoHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class DataHelper {
	//资源实例缓存
	private ConcurrentMap<Long,ResourceInstance> instCache = new ConcurrentHashMap<Long,ResourceInstance>();
	private ReentrantLock instCacheLock = new ReentrantLock();
	//资源实例状态缓存
	private ConcurrentMap<Long,InstanceStateEnum> instStateCache = new ConcurrentHashMap<Long,InstanceStateEnum>();
	private ReentrantLock instStateCacheLock = new ReentrantLock();
	
	static Map<InstanceStateEnum,Integer> instanceStateMap = new HashMap<InstanceStateEnum,Integer>();
	static {
		instanceStateMap.put(InstanceStateEnum.CRITICAL, 0);
		instanceStateMap.put(InstanceStateEnum.CRITICAL_NOTHING, 0);
		instanceStateMap.put(InstanceStateEnum.SERIOUS, 1);
		instanceStateMap.put(InstanceStateEnum.WARN, 2);
		instanceStateMap.put(InstanceStateEnum.NORMAL_CRITICAL, 3);
		instanceStateMap.put(InstanceStateEnum.NORMAL_NOTHING, 3);
		instanceStateMap.put(InstanceStateEnum.NORMAL_UNKNOWN, 3);
		instanceStateMap.put(InstanceStateEnum.UNKNOWN_NOTHING, 4);
		instanceStateMap.put(InstanceStateEnum.UNKOWN, 4);
	}
	public final static String NODATA_FLAG="- -";
	private Logger logger = Logger.getLogger(DataHelper.class);
	//资源实例服务
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService rsvc;
	@Autowired
	private AlarmEventService alarmService;
	//资源实例状态服务
	@Autowired
	public InstanceStateService instanceStateService;
	//指标服务
	@Autowired
	private MetricDataService metricDataService;
	@Autowired
	private IResourceInstanceExApi exApi;
	//能力库服务
	@Autowired
	private CapacityService csvc;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	//缓存刷新时间
	private int cacheRefreshTime=1000000;
	private boolean enableCache=false;
	//获取链路源端接口索引
	public String getLinkSrcIfIndex(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("getLinkSrcIfIndex(),参数linkResource=null,返回"+NODATA_FLAG);
			return NODATA_FLAG;
		}
		try {
			String[] ifIndex = linkResource.getModulePropBykey(LinkResourceConsts.PROP_SRC_IFINDEX);
            if (ifIndex == null || ifIndex.length < 1) {
                return NODATA_FLAG;
            }
            return ifIndex[0];
        } catch (Exception e) {
            logger.error("getModulePropBykey(\"srcIfIndex\")异常",e);
			return NODATA_FLAG;
		}
	}
	//获取链路目的端接口索引
	public String getLinkDesIfIndex(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("getLinkDesIfIndex(),参数linkResource=null,返回"+NODATA_FLAG);
			return NODATA_FLAG;
		}
		try {
			String[] ifIndex = linkResource.getModulePropBykey(LinkResourceConsts.PROP_DEST_IFINDEX);
            if (ifIndex == null || ifIndex.length < 1) {
                return NODATA_FLAG;
            }
            return Integer.parseInt(ifIndex[0]) + "";
        } catch (Exception e) {
            logger.error("getModulePropBykey(\"destIfIndex\")异常",e);
			return NODATA_FLAG;
		}
	}
	//获取链路取值接口索引
	public String getLinkValueIfIndex(ResourceInstance linkResource){
		if(null == linkResource) {
			logger.error("dataHelper.getLinkValueIfIndex(),参数linkResource为空");
			return NODATA_FLAG;
		}
		try {
			Long srcInstanceId = getLinkSrcSubInstanceId(linkResource);
			Long valueInstanceId = getLinkValueInstanceId(linkResource);
			if(srcInstanceId.equals(valueInstanceId)){
				return getLinkSrcIfIndex(linkResource);
			}else{
				return getLinkDesIfIndex(linkResource);
			}
		} catch (Exception e) {
			logger.error("获取链路取值接口索引异常",e);
			return NODATA_FLAG;
		}
	}
	//获取链路源端口实例id
	public Long getLinkSrcSubInstanceId(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("获取链路源端口实例id,getLinkSrcSubInstanceId(linkResource),参数linkResource=null,return null");
			return null;
		}
		try {
			String[] instanceId = linkResource.getModulePropBykey(LinkResourceConsts.PROP_SRC_SUBINST_ID);
            if (instanceId == null || instanceId.length < 1) {
                return null;
            }
            return Long.parseLong(instanceId[0]);
        } catch (Exception e) {
            logger.error("getModulePropBykey(\"srcSubInstId\")发生异常",e);
			return null;
		}
	}
	//获取链路目的短裤实例id
	public Long getLinkDesSubInstanceId(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("dataHelper.getLinkDesSubInstanceId(linkResource),参数=null->return null");
			return null;
		}
		try {
			String[] instanceId = linkResource.getModulePropBykey(LinkResourceConsts.PROP_DEST_SUBINST_ID);
            if (instanceId == null || instanceId.length < 1) {
                return null;
            }
            return Long.parseLong(instanceId[0]);
        } catch (Exception e) {
            logger.error("getLinkDesSubInstanceId()获取链路目的端实例id异常",e);
			return null;
		}
	}
	//获取链路源端口主资源实例id
	public Long getLinkSrcMainInstanceId(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("获取链路源端口主资源实例id,getLinkSrcMainInstanceId(),参数linkResource=null,return null");
			return null;
		}
		try {
			String[] instanceId = linkResource.getModulePropBykey(LinkResourceConsts.PROP_SRC_MAININST_ID);
            if (instanceId == null || instanceId.length < 1) {
                return null;
            }
            return Long.parseLong(instanceId[0]);
        } catch (Exception e) {
            logger.error("getModulePropBykey(\"srcMainInstId\")发生异常",e);
			return null;
		}
	}
	//获取链路源端口主资源实例id
	public Long getLinkDesMainInstanceId(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("dataHelper.getLinkDesMainInstanceId(linkResource),参数=null->return null");
			return null;
		}
//        logger.info("传入参数 linkResource："+JSONObject.toJSONString(linkResource));
        try {
            String[] instanceId = linkResource.getModulePropBykey(LinkResourceConsts.PROP_DEST_MAININST_ID);
            if (instanceId == null || instanceId.length < 1) {
                return null;
            }
            return Long.parseLong(instanceId[0]);
        } catch (Exception e) {
            logger.error("getLinkDesMainInstanceId()",e);
			return null;
		}
	}
	//获取链路取值接口实例id
	public Long getLinkValueInstanceId(ResourceInstance linkResource){
		if(null == linkResource){
			logger.error("dataHelper.getLinkValueInstanceId(linkResource),参数=null->return null");
			return null;
		}
		try {
			String[] instanceId = linkResource.getModulePropBykey(LinkResourceConsts.PROP_COLL_SUBINST_ID);
            if (instanceId == null || instanceId.length < 1) {
                return null;
            }
            return Long.parseLong(instanceId[0]);
        } catch (Exception e) {
            logger.error("getLinkValueInstanceId()异常",e);
			return -1l;
		}
	}
	//获取端口带宽
	public String getPortBandWidth(Long instanceId,String unit){
		String bw = getMetricInfoData(instanceId, "ifSpeed");
		String retn = bw;
		if(!bw.equals(NODATA_FLAG)){
			retn = autoAdjustBandWidthUnit(Double.parseDouble(bw));
		}
		return retn;
	}
	//获取端口带宽利用率
	public String getPortBandWidthRatio(Long instanceId){
		String ratio = getMetricPerformanceData(instanceId, "ifBandWidthUtil");
		if(null != ratio && !ratio.equals(NODATA_FLAG)){
			ratio=formateRatio(ratio);
		}else{
			ratio = NODATA_FLAG;
		}
		return ratio;
	}
	//获取接口接收速率
	public String getPortReceiveSpeed(Long instanceId,String unit){
		String speed = getMetricPerformanceData(instanceId, "ifInOctetsSpeed");
		if(null != speed && !speed.equals(NODATA_FLAG)){
			speed = autoAdjustBandWidthUnit(Double.parseDouble(speed));
		}else{
			speed = NODATA_FLAG;
		}
		return speed;
	}
	//获取接口发送速率
	public String getPortSendSpeed(Long instanceId,String unit){
		String speed = getMetricPerformanceData(instanceId, "ifOutOctetsSpeed");
		if(null !=speed && !speed.equals(NODATA_FLAG)){
			speed = autoAdjustBandWidthUnit(Double.parseDouble(speed));
		}else{
			speed = NODATA_FLAG;
		}
		return speed;
	}
	//获取总流量
	public String getPortTotalFlow(Long instanceId,String unit){
		Double total=getPortTotalFlow(instanceId);
		if(total.equals(0d)){
			return NODATA_FLAG;
		}else{
			return autoAdjustBandWidthUnit(total);
		}
	}
	//获取接口接收带宽利用率
	public String getPortReceiveRatio(Long instanceId){
		String ratio = getMetricPerformanceData(instanceId, "ifInBandWidthUtil");
		if(!ratio.equals(NODATA_FLAG)){
			ratio+="%";
		}
		return ratio;
	}
	//获取接口发送带宽利用率
	public String getPortSendRatio(Long instanceId){
		String ratio = getMetricPerformanceData(instanceId, "ifOutBandWidthUtil");
		if(!ratio.equals(NODATA_FLAG)){
			ratio+="%";
		}
		return ratio;
	}
	//获取接口类型
	public String getPortType(Long instanceId) {
		return getMetricInfoData(instanceId, "ifType");
	}
	//获取端口名称
	public String getPortName(Long instanceId){
		String name=getMetricInfoData(instanceId,"ifName");
		if(name.equals(NODATA_FLAG)){
			name=getMetricInfoData(instanceId,"ifAlias");
		}
		if(name.equals(NODATA_FLAG)){
			name = getResourceInstanceShowName(getResourceInstance(instanceId));
		}
		return name;
	}
	//通过接口索引和主资源实例获取接口名称
	public String getPortName(Long mainInstanceId,Long index){
		TopoHelper.beginLog("DataHelper.getPortName(long,long)");
		ResourceInstance inst = getResourceInstance(mainInstanceId);
		String name = null;
		if(inst!=null){
			List<ResourceInstance> children = inst.getChildren();
			if(children!=null){
				for(ResourceInstance child : children){
					String type = child.getChildType();
					if(ResourceTypeConsts.TYPE_NETINTERFACE.equals(type)){//是否是接口
						String[] ifindex = child.getModulePropBykey("ifIndex");
						if(ifindex!=null && ifindex.length>0 && ifindex[0].equals(index.toString())){
							name = getResourceInstanceShowName(child);
						}
					}
				}
			}
		}
		TopoHelper.endLog("DataHelper.getPortName(long,long)");
		if(name==null){
			return NODATA_FLAG;
		}else{
			return name;
		}
	}
	//获取接口总流量
	public Double getPortTotalFlow(Long instanceId){
		String rspeed = getMetricPerformanceData(instanceId, "ifInOctetsSpeed");
		String sspeed = getMetricPerformanceData(instanceId, "ifOutOctetsSpeed");
		Double total=0d;
		if(null !=rspeed && !rspeed.equals(NODATA_FLAG)){
			total+=Double.valueOf(rspeed);
		}
		if(null !=sspeed && !sspeed.equals(NODATA_FLAG)){
			total+=Double.valueOf(sspeed);
		}
		return total;
	}
	public String getMetricInfoData(Long instanceId,String metricId){
        if (instanceId == null) return NODATA_FLAG;
        try {
			MetricData data = infoMetricQueryAdaptService.getMetricInfoData(instanceId,metricId);
			if(null != data){
				String[] datas = data.getData();
				if(datas.length>0 && datas[0]!=null){
					return datas[0];
				}else{
					throw new RuntimeException("");
				}
			}else{
				return NODATA_FLAG;
			}
		} catch (Exception e) {
			logger.error(String.format("get MetricInfoData from instanceId=%s by %s failed",instanceId,metricId),e);
			return NODATA_FLAG;
		}
	}
	public String getMetricPerformanceData(Long instanceId,String metricId){
        if (instanceId == null) {
            return NODATA_FLAG;
        }
        try {
			MetricData data = metricDataService.getMetricPerformanceData(instanceId,metricId);
			if(null != data){
				String[] datas = data.getData();
				if(datas.length>0 && datas[0]!=null){
					return datas[0];
				}else{
					throw new RuntimeException("");
				}
			}else{
				return NODATA_FLAG;
			}
		} catch (Exception e) {
			logger.error(String.format("get MetricPerformanceData from instanceId=%s by %s failed",instanceId,metricId),e);
			return NODATA_FLAG;
		}
	}
	//获取接口索引
	public String getPortIfIndex(Long instanceId){
		return getMetricInfoData(instanceId, "ifIndex");
	}
	public void refreshCache(){
		//刷新实例缓存
		if(!instCacheLock.isLocked()){
			new Thread(new Runnable(){
				@Override
				public void run() {
					if(instCacheLock.tryLock()){
						logger.error("DataHelper.refreshCache.instCache start");
						for(Map.Entry<Long,ResourceInstance> entry : instCache.entrySet()){
							try {
								ResourceInstance ri = rsvc.getResourceInstance(entry.getKey());
								if(ri!=null){
									instCache.put(entry.getKey(), ri);
								}
							} catch (InstancelibException e) {
								logger.error("DataHelper.refreshCache",e);
							}
						}
						logger.error("DataHelper.refreshCache.instCache over");
						try {
							//给定时间再允许刷新一次
							TimeUnit.MILLISECONDS.sleep(cacheRefreshTime);
						} catch (InterruptedException e) {
							logger.error("DataHelper.refreshCache",e);
						} finally{
							instCacheLock.unlock();
						}
						
					}
				}
			}).start();
		}
		//刷新状态缓存
		if(!instStateCacheLock.isLocked()){
			new Thread(new Runnable(){
				@Override
				public void run() {
					if(instStateCacheLock.tryLock()){
						logger.error("DataHelper.refreshCache.instStateCache start");
						for(Map.Entry<Long,InstanceStateEnum> entry : instStateCache.entrySet()){
							InstanceStateEnum state = getInstAlarmInstanceStateEnum(entry.getKey());
							instStateCache.put(entry.getKey(), state);
						}
						logger.error("DataHelper.refreshCache.instStateCache over");
						try {
							//给定时间再允许刷新一次
							TimeUnit.MILLISECONDS.sleep(cacheRefreshTime);
						} catch (InterruptedException e) {
							logger.error("DataHelper.refreshCache",e);
						} finally{
							instStateCacheLock.unlock();
						}
					}
				}
			}).start();
		}
	}
	//获取资源实例
	public ResourceInstance getResourceInstance(Long instanceId) {
		if(null == instanceId) {
			logger.error("DataHelper.getResourceInstance(instanceId),获取资源instanceId为空,return null");
			return null;
		}
		
		try {
			if(this.enableCache && instCache.containsKey(instanceId)){
				refreshCache();
				return instCache.get(instanceId);
			}else{
				ResourceInstance ri = rsvc.getResourceInstance(instanceId);
				if(ri!=null && instanceId!=null){
					instCache.put(instanceId, ri);
				}
				return ri;
			}
		} catch (Exception e) {
			logger.error(new StringBuilder("resourceInstanceService.getResourceInstance(").append(instanceId).append(")发生异常，return null"));
			return null;
		}
	}
	//获取资源实力
	public List<ResourceInstance> getResourceInstances(List<Long> instanceIds){
		List<ResourceInstance> retn = new ArrayList<ResourceInstance>();
		for(Long id : instanceIds){
			ResourceInstance ri = getResourceInstance(id);
			if(ri!=null){
				retn.add(ri);
			}
		}
		return retn;
	}
	//获取资源实例mac地址
	public String getResourceInstanceMac(Long instanceId){
		return getMetricInfoData(instanceId, "macAddress");
	}
	//获取资源实例mac地址
	public String getResourceInstanceIfMac(Long instanceId){
		return getMetricInfoData(instanceId,"ifMac");
	}
	//获取资源实例mac地址
	public String getResourceInstanceIfMAC(Long instanceId){
		return getMetricInfoData(instanceId,"ifMAC");
	}
	public String getResourceInstanceResourceId(String oid){
		if(StringUtils.isBlank(oid)){
			logger.error("dataHelper.getResourceInstanceResourceId(oid),参数=null->return null");
			return null;
		}
		String resourceId = csvc.getResourceId(oid);
		if(resourceId == null){
			logger.error(oid+"找不到对于的resourceId");
		}
		return resourceId;
	}
	public String getResourceInstanceOid(Long instanceId){
		return getMetricInfoData(instanceId,"sysObjectID");
	}
	//获取资源实例状态
	public String getResourceInstanceState(ResourceInstance resource){
		try {
			switch(resource.getLifeState()){
			case DELETED:
				return "已删除";
			case INITIALIZE:
				return "初始化";
			case MONITORED:
				return "已监控";
			case NOT_MONITORED:
				return "未监控";
			}
			return resource.getLifeState().name();
		} catch (Exception e) {
			logger.error(e);
			return "未知";
		}
	}
	//获取资源实例接口名称
	public String getResourceInstanceIfName(ResourceInstance resourceInstance){
		if(null == resourceInstance){
			logger.error("getResourceInstanceIfName(resourceInstance),resourceInstance=null->return null");
			return null;
		}else{
			return resourceInstance.getName();
		}
	}
	//获取资源实例显示名称
	public String getResourceInstanceShowName(ResourceInstance resourceInstance){
		if(null == resourceInstance){
			logger.error("getResourceInstanceShowName(resourceInstance),resourceInstance=null->return \"--\"");
			return NODATA_FLAG;
		}
		try {
			String name = resourceInstance.getShowName();
			if(name==null){
				name=resourceInstance.getName();
			}
			if(name==null){
				name=NODATA_FLAG;
			}
			return name;
		} catch (Exception e) {
			logger.error("getResourceInstanceShowName()异常",e);
			return NODATA_FLAG;
		}
	}
	//获取资源实例管理状态
	public String getResourceInstanceManageState(Long instanceId){
		return getMetricInfoData(instanceId, "ifAdminStatus");
	}
	//获取资源实例的操作状态
	public String getResourceInstanceOperateState(Long instanceId){
		return getMetricInfoData(instanceId, "ifOperStatus");
	}
	//获取资源实例的oid
	public String getResourceInstanceOid(ResourceInstance resourceInstance){
		return getMetricInfoData(resourceInstance.getId(), "sysObjectID");
	}
	//获取资源实例得厂商信息
	public String[] getResourceInstanceVendorInfo(ResourceInstance resourceInstance){
		if(null == resourceInstance){
			logger.error("getResourceInstanceVendorInfo(resourceInstance),resourceInstance=null->[--,--]");
			return new String[]{NODATA_FLAG,NODATA_FLAG};
		}
		Long instanceId = resourceInstance.getId();
		//修改地图资源实例设备型号和厂商与二层拓扑保持一致
		String oid=null;
		String[] devType = new String[]{"- -","- -"};
		if(instanceId!=null){
			oid=this.getResourceInstanceOid(instanceId);
		}
		if(oid!=null){
			DeviceType dType = csvc.getDeviceType(oid);
			if(null!=dType){
				String verdorName = dType.getVendorName();
				if(!org.springframework.util.StringUtils.hasText(verdorName)){
					logger.error(new StringBuffer("oid=").append(oid).append("无设备厂商名称"));
					devType[0] = "- -";
				}else{
					devType[0] = verdorName;
				}
				String series = dType.getModelNumber();
				if(!org.springframework.util.StringUtils.hasText(series)){
					logger.error(new StringBuffer("oid=").append(oid).append("无设备厂商型号"));
					devType[1] = "- -";
				}else{
					devType[1] = series;
				}
			}else{
				logger.error(new StringBuffer("oid").append(oid).append("无设备厂商信息"));
			}
		}
		return devType;
	}
	//获取资源实例管理ip
	public String getResourceInstanceManageIp(ResourceInstance resourceInstance){
		if(null == resourceInstance){
			logger.error("getResourceInstanceManageIp(resourceInstance),resourceInstance=null->return --");
			return NODATA_FLAG;
		}
		try {
			return resourceInstance.getShowIP();
		} catch (Exception e) {
			logger.error(e);
			return NODATA_FLAG;
		}
	}
	
	public String getInstanceState(Long instanceId){
		String collectState = CollectStateEnum.COLLECTIBLE.name();
		if(null != instanceId){
			InstanceStateData isd = instanceStateService.getState(instanceId);
			if(null!=isd){
				//CollectStateEnum为空认为可采集
				if(null!=isd.getCollectStateEnum()){
					collectState = isd.getCollectStateEnum().name();
				}
			}
		}
		return collectState;
	}
	
	//获取资源实例告警状态
	public String getResourceInstanceAlarmState(Long instanceId){
		
		List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(Arrays.asList(instanceId));
		if(instanceStateDataList!=null && !instanceStateDataList.isEmpty()){
			InstanceStateData state = instanceStateDataList.get(0);
			InstanceStateEnum stateEnum = state.getState();
			return exApi.getResourceStateColor(stateEnum);
		}else{
			return exApi.getResourceStateColor(InstanceStateEnum.UNKOWN);
		}
	}
	public InstanceStateEnum getInstAlarmInstanceStateEnum(Long instId){
		InstanceStateEnum state = null;
		List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(Arrays.asList(instId));
		if(instanceStateDataList!=null && !instanceStateDataList.isEmpty()){
			InstanceStateData stateData = instanceStateDataList.get(0);
			state = stateData.getState();
		}else if(null== state || InstanceStateEnum.UNKOWN == state || InstanceStateEnum.UNKNOWN_NOTHING == state){
			state = InstanceStateEnum.NORMAL;
		}
		return state;
	}
	
	/**
	 * 根据指标id查询状态
	 * @param instId
	 * @param metricId
	 * @return
	 */
	public InstanceStateEnum getInstAlarmInstanceStateEnum(Long instId,String metricId){
		InstanceStateEnum state = null;
		List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(Arrays.asList(instId));
		if(instanceStateDataList!=null && !instanceStateDataList.isEmpty()){
			InstanceStateData stateData = instanceStateDataList.get(0);
			state = stateData.getState();
			String metricIdStr = stateData.getCauseBymetricID();
			//如果选择状态计算链路颜色，则告警后还需要判断是否是状态这个指标告的警，否则认为是正常的
			if("device".equals(metricId) && (InstanceStateEnum.WARN == state || InstanceStateEnum.SERIOUS == state)){
//				System.out.println(state.name()+" merircID="+metricIdStr+" ,ifOctetsSpeed="
//					+(StringUtils.equals(metricIdStr, MetricIdConsts.METRIC_IFOCTETSSPEED))+"  ifBandWidthUtil="+(StringUtils.equals(metricIdStr, MetricIdConsts.IF_IFBANDWIDTHUTIL)));
				if(!StringUtils.equals(metricIdStr, MetricIdConsts.METRIC_AVAILABLE)){
					//拓扑选择的是【链路状态】，并且接口有告警，但是不是状态的告警（是总流量或则带宽利用率等产生的，认为是正常的）
					state = InstanceStateEnum.NORMAL;
				}
			}
		}else if(null== state || InstanceStateEnum.UNKOWN == state || InstanceStateEnum.UNKNOWN_NOTHING == state){
			state = InstanceStateEnum.NORMAL;
		}
//		System.out.println("------------------state------------------"+state.name()+"\n");
		return state;
	}
	public InstanceStateEnum getResourceInstanceAlarmInstanceStateEnum(Long instanceId){
		InstanceStateEnum state = null;
		if(this.enableCache && instStateCache.containsKey(instanceId)){
			refreshCache();
			state = instStateCache.get(instanceId);
		}else{
			state = getInstAlarmInstanceStateEnum(instanceId);
			if(instanceId!=null && state!=null){
				instStateCache.put(instanceId, state);
			}
		}
		return state;
	}
	
	//获取资源实例告警状态
	public Map<Long,String> getResourceInstanceAlarmState(List<Long> ids){
		List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(ids);
		Map<Long,String> retn = new HashMap<Long,String>(ids.size());
		if(instanceStateDataList!=null && !instanceStateDataList.isEmpty()){
			for(InstanceStateData state:instanceStateDataList){
				retn.put(state.getInstanceID(), exApi.getResourceStateColor(state.getState()));
			}
		}
		return retn;
	}
	//获取指标时时值
	public JSONObject getResourceRealTimeVal(Long instanceId,String[] metrics){
		JSONObject retn = new JSONObject();
		MetricRealtimeDataQuery query = new MetricRealtimeDataQuery();	//指标实时数据查询对象
		query.setMetricID(metrics);
		query.setInstanceID(new long[]{instanceId});
		Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(query, 1, 100);
		List<Map<String,?>> data = page.getDatas();
		if(!data.isEmpty()){
			Map<String,?> map = data.get(0);
			for(String m:metrics){
				retn.put(m, TopoHelper.formatStringVal(map.get(m), NODATA_FLAG,"%"));
			}
			retn.put("instanceId", map.get("instanceid"));
		}
		return retn;
	}
	
	public AlarmEvent getLatestAlarmEvent(List<Long> instanceIds,SysModuleEnum sm) {
		List<AlarmEvent> events = getLatestAlarmEventData(instanceIds,sm);
		int max=Integer.MAX_VALUE;
		AlarmEvent event = null;
		for(AlarmEvent ev:events){
			if(instanceStateMap.containsKey(ev.getLevel())){
				int level = instanceStateMap.get(ev.getLevel());
				if(max>level){
					max=level;
					event=ev;
				}
			}
		}
		return event;
	}
	public String getLatestMsgAlarmEventForLink(Long instanceId){
		try {
			List<Long> ids = new ArrayList<Long>(1);
			ids.add(instanceId);
			AlarmEvent alarm = getLatestAlarmEvent(ids,SysModuleEnum.LINK);
			if(null!=alarm){
				return alarm.getContent();
			}else {
				throw new RuntimeException(String.format("instanceid(%s) has no alarm info",instanceId));
			}
		} catch (Exception e) {
			logger.error("获取链路最新最严重告警信息异常",e);
			return NODATA_FLAG;
		} 
	}
	public String getLatestMsgAlarmEventForInstance(Long instanceId){
		try {
			List<Long> ids = new ArrayList<Long>(5);
			ResourceInstance inst = getResourceInstance(instanceId);
			List<ResourceInstance> children = inst.getChildren();
			if(children!=null){
				for(ResourceInstance ri : inst.getChildren()){
					ids.add(ri.getId());
				}
			}
			ids.add(instanceId);
			AlarmEvent alarm = getLatestAlarmEvent(ids,SysModuleEnum.MONITOR);
			if(null!=alarm){
				return alarm.getContent();
			}else {
				logger.error(String.format("instanceid(%s) has no alarm info",instanceId));
				return NODATA_FLAG;
			}
		} catch (Exception e) {
			logger.error("DataHelper.getLatestMsgAlarmEventForInstance",e);
			return NODATA_FLAG;
		} 
	}
	//获取链路最近最严重的一条告警
	public List<AlarmEvent> getLatestAlarmEventData(List<Long> instanceIds,SysModuleEnum sm){
		if(!instanceIds.isEmpty()){
			AlarmEventQuery2 query = new AlarmEventQuery2();
			AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
			detail.setSysID(sm);
			detail.setRecovered(false);
			List<String> ids = new ArrayList<String>(instanceIds.size());
			for(Long id:instanceIds){
				ids.add(id.toString());
			}
			detail.setSourceIDes(ids);
			query.setFilters(Arrays.asList(detail));
			return alarmService.findAlarmEvent(query);
		}
		return new ArrayList<AlarmEvent>(0);
	}
	public JSONArray getResourceRealTimeVal(Long[] instanceIds,String[] metrics){
		JSONArray retn = new JSONArray(instanceIds.length);
		try {
			MetricRealtimeDataQuery query = new MetricRealtimeDataQuery();	//指标实时数据查询对象
			query.setMetricID(metrics);
			query.setInstanceID(TopoHelper.toBaseLong(instanceIds));
			Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(query, 1, 100);
			List<Map<String,?>> data = page.getDatas();
			if(!data.isEmpty()){
				for(Map<String,?> map:data){
					JSONObject tmpJson = new JSONObject();
					for(String m:metrics){
						tmpJson.put(m, TopoHelper.formatStringVal(map.get(m), NODATA_FLAG,"%"));
					}
					tmpJson.put("instanceId", map.get("instanceid"));
					retn.add(tmpJson);
				}
			}
		} catch (Throwable e) {
			logger.error(e);
		}
		return retn;
	}
	public void licenseAvailable(String key){
		//lic.getModleMap();
	}
	//自动调整带宽流量单位
	public String autoAdjustBandWidthUnit(double bw){
		double bandWidth=0d;
		String unit="bps";
		if(bw<1000000l){
			bandWidth = formatBandWidth(bw,"Kbps");
			unit="Kbps";
		}else{
			unit="Mbps";
			bandWidth = formatBandWidth(bw,"Mbps");
		}
		//自动进位
		return autoAdapt(bandWidth)+unit;
	}
	private String autoAdapt(double bw){
		String a= String.valueOf(bw);
		String[] parts = a.split("\\.");
        String intPart=parts[0];
        String pointPart="";
        if(parts.length==2){
            pointPart="."+parts[1];
        }
        int l = intPart.length();
        if(l<=3){
            return a;
        }else{
            int end=l%3,start=0;
            if(end==0){
                end=3;
            }
            StringBuffer sb = new StringBuffer();
            //添加“,”
            for(int i=0;i<l/3;i++){
                sb.append(intPart.substring(start,end));
                start=end;
                end+=3;
                sb.append(",");
                if(end==l){
                    sb.append(intPart.substring(start,l));
                    break;
                }
            }
            sb.append(pointPart);
            return sb.toString();
        }
	}
	private double formatBandWidth(double bw,String unit){
		DecimalFormat df = new DecimalFormat("0.00");
		if(unit==null){
			return Double.valueOf(df.format(Double.valueOf(bw)));
		}else{
			BigDecimal divide=null;
			switch(unit){
			case "Kbps":
				divide = new BigDecimal(1000);
				break;
			case "Mbps":
				divide = new BigDecimal(1000000);
				break;
			default:
				divide = new BigDecimal(1);
			}
			return Double.valueOf(df.format(new BigDecimal(bw).divide(divide).setScale(2,BigDecimal.ROUND_HALF_UP)));
		}
	}
	public String mapToCapacityConst(String type){
		if(null==type || "".equals(type)){
			return "device";
		}else{
			String retn = null;
			switch(type){
			case "cpu"://cpu利用率
				retn = MetricIdConsts.METRIC_CPU_RATE;
				break;
			case "ram"://内存利用率
				retn = MetricIdConsts.METRIC_MEME_RATE;
				break;
			case "totalFlow"://总流量
				retn = MetricIdConsts.METRIC_IFOCTETSSPEED;
				break;
			case "bandWidth"://带宽利用率
				retn = MetricIdConsts.IF_IFBANDWIDTHUTIL;
				break;
			case "device"://设备状态
				retn=type;
				break;
			}
			return retn;
		}
	}
	private String formateRatio(String val){
		DecimalFormat df =new DecimalFormat("0.00");
		BigDecimal base = new BigDecimal(Float.parseFloat(val));
		return df.format(base.doubleValue())+"%";
	}
	//启用缓存，i为毫秒
	public void enableCache(int msecond) {
		this.cacheRefreshTime=msecond;
		this.enableCache=true;
	}
	public void disableCache() {
		this.enableCache=false;
	}
}
