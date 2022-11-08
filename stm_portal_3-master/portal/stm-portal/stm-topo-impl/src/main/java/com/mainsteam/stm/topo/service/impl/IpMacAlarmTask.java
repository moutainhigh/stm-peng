package com.mainsteam.stm.topo.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.topo.api.IIpMacAlarmTaskApi;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.api.TopoAlarmExApi;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.collector.TopoMacCollectorMBean;
import com.mainsteam.stm.topo.dao.IMacBaseDao;
import com.mainsteam.stm.topo.dao.IMacHistoryDao;
import com.mainsteam.stm.topo.dao.IMacLatestDao;
import com.mainsteam.stm.topo.dao.IMacRuntimeDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.util.InetAddressExUtil;
import com.mainsteam.stm.topo.util.TopoConfig;
import com.qwserv.itm.netprober.bean.IpmacItemBean;

/**
 * <li>网络设备IP-MAC-PORT告警任务业务</li>
 * 
 * @version ms.stm
 * @since 2019年12月13日
 * @author zwx
 */
@Component
public class IpMacAlarmTask implements IIpMacAlarmTaskApi {
	private final Log logger = LogFactory.getLog(IpMacAlarmTask.class);
	
	private static final String IpMacPort_KEY = "IpMacPort-Alarm-job";
	@Autowired
	private ScheduleManager scheduleManager;
	@Autowired
	private TopoAlarmExApi alarmExApi;
	@Autowired
	private IMacRuntimeDao macRuntimeDao;
	@Autowired
	private IMacBaseDao macBaseDao;
	@Autowired
	private IMacLatestDao latestMacDao;
	@Autowired
	private IMacHistoryDao macHistoryDao;
	@Autowired
	private ISettingDao settingDao;
	@Autowired
	private INodeDao nodeDao;
	// 远程调用服务
	@Autowired
	private OCRPCClient client;
	@Autowired
	private ISettingApi settingApi;
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private IResourceInstanceExApi resourceExApi;
	
	private final String defaultConstant = "- -";	//默认字符串常量

	/**
	 * 启动处理器立即初始化IpMac告警Job
	 */
	@PostConstruct
	public void startIpMacAlarmJobTask() {
		try {
			IpMacAlarmJob alarmJob = new IpMacAlarmJob();
			String cronExpression = TopoConfig.getValue("alarm.jobTime");
			
			IJob ipMacJob = new IJob(IpMacPort_KEY, alarmJob, cronExpression);
			this.scheduleManager.updateJob(IpMacPort_KEY, ipMacJob);
			logger.info("IpMac告警Job启动成功!\n[" + IpMacPort_KEY + "],jobTime["+ cronExpression + "],isAuto["+TopoConfig.getValue("alarm.isAuto")+"]");
		} catch (Exception e) {
			logger.error("类：IpMacAlarmTask ,方法：startIpMacAlarmJobTask：开启IP-MAC-PORT告警job任务发生异常!", e);
		}
	}

	/**
	 * 删除IP-MAC-PORT告警job任务
	 */
	@Override
	public void delIpMacAlarmJobTask() {
		 try {
			 if (scheduleManager.isExists(IpMacPort_KEY)) {
				 scheduleManager.deleteJob(IpMacPort_KEY);
			 }
		 } catch (Exception e) {
			 logger.error("类：IpMacAlarmTask ,方法：delIpMacAlarmJobTask：删除IP-MAC-PORT告警job任务发生异常!", e);
		 }
	}

	/**
	 * 1. 获取要刷新的设备ips列表 
	 * 2. 调用采集器接口，开始发现刷新数据
	 * 刷新ipmac数据
	 * @param groupId
	 * @return
	 */
	@Override
	public void refreshIpMacPort(Integer groupId) {
		if(null == groupId) {	//自动刷新，非手动刷新
			//检测是否开启自动刷新功能
			boolean isAuto = Boolean.valueOf(TopoConfig.getValue("alarm.isAuto"));
			if(!isAuto) return;
			groupId = settingApi.getTopoFindGroupId();
		}
		
		if(null != groupId){
			// 1.判断是否【全网发现】过
			boolean havenFind = nodeDao.hasTopo();
			
			// 2.开始刷新数据，并产生告警
			if (havenFind) {
				// 2.1 获取要刷新的设备ips列表(topo发现的节点ip地址)
				List<String> nodeIps = nodeDao.selectIps();
				if (null == nodeIps || nodeIps.size() == 0) {
					logger.error("没有刷新的设备ips,ip-mac-port不刷新任何数据");
					return;
				}
	
				// 2.2 调用采集器接口，开始发现刷新数据
				try {
					final TopoMacCollectorMBean macCollector = client.getRemoteSerivce(groupId, TopoMacCollectorMBean.class);
					try{
						macCollector.startRefresh(nodeIps,null);
					}catch(NullPointerException e){
						logger.error("OCRPCClient.getRemoteSerivce()方法获取远程MBean连接失败，可能的原因是获取的采集器节点["+groupId+"]不存在", e);
					}
					
					 //2.3 分析刷新的数据(开启监听,解析采集器数据)
					Thread refreshIpMacPortThread = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								if (!macCollector.isRunning()) {
									List<IpmacItemBean> ipmacItems = macCollector.getIpmacItems();
									if (null == ipmacItems || ipmacItems.size() == 0) break;
									
									//2.4 删除所有实时、新增mac数据
									macRuntimeDao.deleteAll();
									latestMacDao.deleteAll();
									
									//2.5 更新数据
									refreshMacs(ipmacItems);
									break;
								}
								try {
									Thread.sleep(1000);
								} catch (Exception e) {
									logger.error("IP-MAC-PORT刷新数据睡眠异常",e);
								}
							}
						}
						
					},"refreshIpMacPort-"+System.currentTimeMillis());
					refreshIpMacPortThread.start();
				}catch (Exception e) {
					logger.error("刷新IP-MAC-PORT数据失败", e);
				}
			}else{
				logger.warn("还未进行过全网发现，无法采集数据！");
			}
		}else {
			logger.warn("不存在采集器groupId，无法采集数据！");
		}
	}

	/**
	 * 更新mac数据
	 * @param ipmacItems
	 */
	private void refreshMacs(List<IpmacItemBean> ipmacItems) {
		// 获取ipmac告警设置信息
		SettingBo setting = settingDao.getCfg("ipMacAlarmSetting");
		
		List<ResourceInstance> resources = null;
		try {
			resources = resourceInstanceService.getAllParentInstance();
		} catch (InstancelibException e1) {
			logger.error("获取所有资源实例异常",e1);
		}
		for (IpmacItemBean tem : ipmacItems) {
			Object[] bos = this.parseMsgBo(tem,resources);	//转换数据
			MacRuntimeBo runtimeBo = (MacRuntimeBo) bos[0];

			// 1.与基准表比较
			MacBaseBo base = macBaseDao.getMacBaseByMac(runtimeBo.getMac());
			if (base == null) {	// 不存在则加入【新增MAC表】(0：新增，1：历史)
				latestMacDao.insert((MacLatestBo) bos[2]);
				runtimeBo.setExist(0);
			} else {	// 存在且发生了变化则加入【历史变更表】
				runtimeBo.setExist(1);
				MacHistoryBo history = (MacHistoryBo) bos[1];
				boolean ipChange = this.judgeChange(base.getIp(), history.getIp());
				boolean upDeviceIpChange = this.judgeChange(base.getUpDeviceIp(), history.getUpDeviceIp());
				boolean upDeviceInterfaceChange = this.judgeChange(base.getUpDeviceInterface(), history.getUpDeviceInterface());
				boolean upDeviceNameChange = this.judgeChange(base.getUpDeviceName(), history.getUpDeviceName());
				
				if(ipChange || upDeviceIpChange || upDeviceInterfaceChange || upDeviceNameChange){
					macHistoryDao.insert((MacHistoryBo) bos[1]);
					
					// 2.分析发送告警信息
					if (null != setting) {
						try {
							alarmExApi.sendAlarmNotice(base, runtimeBo,setting.getValue());
						} catch (Exception e) {
							logger.error("发送IP-MAC-PORT告警信息产生异常!", e);
						}
					}else{
						logger.warn("未设置告警规则，采集数据后无法计算告警！");
					}
				}
			}
			
			// 3.新增数据到实时表
			macRuntimeDao.insert(runtimeBo);
		}
		//4.开启线程更新设备名称
		this.updateHostName(ipmacItems);
	}
	
	/**
	 * 判断是否改变
	 * @param base
	 * @param history
	 * @return true(变化)，false(未变化)
	 */
	private boolean judgeChange(String base,String history){
		//都为空，则无变化
		boolean change = StringUtils.isBlank(base) && StringUtils.isBlank(history)?false:true;
		//不都为空，则再判断是否变化
		if(change){
			//一个为空，一个不为空，则表示发生了变化
			change = (StringUtils.isBlank(base) && StringUtils.isNotBlank(history)) || (StringUtils.isNotBlank(base) && StringUtils.isBlank(history));
			//没有一个为空，再判断是否相等
			if(!change){
				//不相等则表示变化了
				change = !base.equals(history);
			}
		}
		return change;
	}

	/**
	 * 解析ip-mac-port数据为MacRuntimeBo、MacHistoryBo对象
	 * @param item、resources
	 * @return [MacRuntimeBo,MacHistoryBo,MacLatestBo]
	 */
	private Object[] parseMsgBo(IpmacItemBean item,List<ResourceInstance> resources) {
		Date time = item.getTime(); 		// 更新时间
		String devName = item.getDevName(); // 上联设备名称
		String ifName = item.getIfName(); 	// 上联设备接口名称
		String mac = item.getMac(); 		// 设备mac地址
		Set<String> ips = item.getIps(); 	// 设备ips
		StringBuffer ipsTem = new StringBuffer();
		String ipsStr = "";
		for (String ip : ips) {
			ipsTem.append(ip).append(",");
		}
		if (ipsTem.length() > 0) {
			ipsStr = ipsTem.toString().substring(0, ipsTem.length() - 1);
		}
//		logger.error(new StringBuffer("设备[").append(mac).append("]的ip是[").append(ipsStr).append("]"));
		String devIp = item.getDevIp(); 		// 上联设备ip(有可能不在地址表)
		Set<String> devIps = item.getDevIps();	// 上联设备ip集合(取自地址表)
		if(null == devIps) devIps = new HashSet<String>();
		devIps.add(devIp);
		StringBuffer devIpsTem = new StringBuffer();
		String devIpsStr = "";
		for (String deIp : devIps) {
			devIpsTem.append(deIp).append(",");
		}
		if (devIpsTem.length() > 0) {
			devIpsStr = devIpsTem.toString().substring(0, devIpsTem.length() - 1);
		}
		
		//获取主机名称
//		logger.info("获取主机名开始：ips=>"+ips+"<,mac=>"+mac+"<");
		String hostName = this.getHostName(ips,mac,resources);
		hostName = StringUtils.isBlank(hostName)?defaultConstant:hostName;
//		logger.info("获取主机名结束：hostName="+hostName);
		
		// 实时表对象
		MacRuntimeBo macRuntime = new MacRuntimeBo();
		macRuntime.setHostName(hostName);
		macRuntime.setIp(ipsStr);
		macRuntime.setMac(mac);
		macRuntime.setUpDeviceName(devName);
		macRuntime.setUpDeviceIp(devIpsStr);
		macRuntime.setUpDeviceInterface(ifName);
		macRuntime.setUpdateTime(time);

		// 历史变更对象
		MacHistoryBo history = new MacHistoryBo();
		history.setHostName(hostName);
		history.setIp(ipsStr);
		history.setMac(mac);
		history.setUpDeviceName(devName);
		history.setUpDeviceIp(devIp);
		history.setUpDeviceInterface(ifName);
		history.setUpdateTime(time);

		// 新增MAC对象
		MacLatestBo macLatest = new MacLatestBo();
		macLatest.setHostName(hostName);
		macLatest.setIp(ipsStr);
		macLatest.setMac(mac);
		macLatest.setUpDeviceName(devName);
		macLatest.setUpDeviceIp(devIp);
		macLatest.setUpDeviceInterface(ifName);
		macLatest.setUpdateTime(time);

		Object[] bos = new Object[3];
		bos[0] = macRuntime;
		bos[1] = history;
		bos[2] = macLatest;

		return bos;
	}
	
	/**
	 * 根据ip或mac获取设备名称
	 * @param ip
	 * @param mac
	 * @return hostName
	 * @throws InstancelibException
	 */
	private String getHostName(Set<String> ips,String mac,List<ResourceInstance> resources){
		String hostName = "";
		try {
			for(ResourceInstance resource:resources){
				String parentCategoryId = resource.getCategoryId();
				if(null != parentCategoryId && !parentCategoryId.equals(CapacityConst.FIREWALL) && !parentCategoryId.equals(CapacityConst.NETWORK_DEVICE)){
					boolean contain = false;
					//1.根据ip查询
					if(null != ips && ips.size()>0){
						String devIpStr = resourceExApi.getPropVal(resource, MetricIdConsts.METRIC_IP);
						for(String ip:ips){
							contain = devIpStr.contains(ip);
							if(contain) break;
						}
						if(contain){
							hostName = resource.getShowName();
							hostName = StringUtils.isBlank(hostName)?resource.getName():hostName;
//							logger.info("=======>instanceId="+resource.getId()+",资源返回IP="+devIpStr+"查询:getName>>"+resource.getName()+"<<,getShowName>>"+resource.getShowName()+"<<");
						}
					}
					//2.ip没查询出来，再根据mac查询
					if(StringUtils.isBlank(hostName) && StringUtils.isNotBlank(mac)){
						String macStr = resourceExApi.getPropVal(resource,MetricIdConsts.METRIC_MACADDRESS);
						contain = macStr.contains(mac);
						if(contain){
							hostName = resource.getShowName();
							hostName = StringUtils.isBlank(hostName)?resource.getName():hostName;
//							logger.info("========>instanceId="+resource.getId()+",资源返回MAC="+macStr+"查询:getName>>"+resource.getName()+"<<,getShowName>>"+resource.getShowName()+"<<");
						}
					}
					if(contain) break;
					
					//3.还是没查询到主机名称，则根据ip查询dns获取主机名（时间会很长）
					if(StringUtils.isBlank(hostName)){
						for (String ip : ips) {	// 处理多ip情况
							hostName = ip;	//暂时取一个即可，解决慢的问题
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("解析ip-mac-port设备名称发生异常",e);
		}
		return hostName;
	}
	
	/**
	 * 更新ip-mac-port设备名称
	 * 说明：一个dns名称查询大约需要时间0~10秒
	 */
	private void updateHostName(final List<IpmacItemBean> ipmacItems){
		//查询是否需要更新的数量
		Thread hostNameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String mac = "";
				String hostName = "";
				Set<String> ips = null;
				//查询所有实时表数据
				final List<MacRuntimeBo> runtimes = macRuntimeDao.getAll();
				Map<String, Set<String>> itmsMap = new HashMap<String, Set<String>>();
				for(IpmacItemBean itm: ipmacItems){
					itmsMap.put(itm.getMac(), itm.getIps());
				}
				
				for(MacRuntimeBo runtime:runtimes){
					if(StringUtils.isBlank(runtime.getHostName()) || defaultConstant.equals(runtime.getHostName())){
						ips = itmsMap.get(runtime.getMac());	// 设备ips
						for (String ip : ips) {	// 处理多ip情况
							if(StringUtils.isNotBlank(runtime.getIp()) && runtime.getIp().contains(ip)){
								hostName = InetAddressExUtil.getHostName(ip);
								break;	//取第一个即可
							}
						}
						if(StringUtils.isNotBlank(hostName)){
							mac = runtime.getMac();
							//更新实时表和最新表；暂未更新基准表和历史表
							macRuntimeDao.updateByMac(mac, hostName);
							latestMacDao.updateByMac(mac, hostName);
						}
					}
				}
			}
		},"hostNameThread-"+System.currentTimeMillis());
		hostNameThread.start();
	}
}
