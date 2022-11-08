package com.mainsteam.stm.home.workbench.netdevice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.home.layout.api.HomeDefaultInterfaceApi;
import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;
import com.mainsteam.stm.home.workbench.host.constant.HostIndicatorsConstant;
import com.mainsteam.stm.home.workbench.host.vo.BaseInfo;
import com.mainsteam.stm.home.workbench.host.vo.CPU;
import com.mainsteam.stm.home.workbench.host.vo.HostNetInfoVo;
import com.mainsteam.stm.home.workbench.host.vo.Interface;
import com.mainsteam.stm.home.workbench.host.vo.InterfaceIndicators;
import com.mainsteam.stm.home.workbench.host.vo.RAM;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.home.workbench.netdevice.api.NetDeviceApi;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.netdevice.service.impl.NetDeviceImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月20日
 */
@Service
@SuppressWarnings("unchecked")
public class NetDeviceImpl implements NetDeviceApi {
	@Resource(name="resourceDetailInfoApi")
	private IResourceDetailInfoApi detailInfoApi;
	@Autowired
	private IUserWorkBenchApi userWorkBenchApi;
	
	@Resource(name="homeDefaultInterfaceApi")
    private HomeDefaultInterfaceApi homeDefaultInterfaceApi;
	
	@Resource(name="ocProtalHomeDefaultInterfaceSeq")
	private ISequence homeDefaultInterfaceSeq;
	
	
	private Logger log = Logger.getLogger(getClass());
	

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.home.workbench.netdevice.api.NetDeviceApi#getNetDeviceInfo(java.lang.Long)
	 */
	
	@Override
	public HostNetInfoVo getNetDeviceInfo(Long id, String interfaceType) {
		int isHave=0;
		HostNetInfoVo hostInfoVo = new HostNetInfoVo();
		Map<String, Object > data = null;
		try{
			data = (Map<String, Object >)detailInfoApi.getResourceDetailInfo(id).get("parent");
			if(data == null){
				return null;
			}
		}catch(Exception e){
			log.error("获取网络设备资源指标失败" + e.getMessage());
			return null;
		}
		/***********组装主机基本信息start************/
		BaseInfo baseInfo = new BaseInfo();
		baseInfo.setIps((List<Map<String, Object>>)data.get(HostIndicatorsConstant.IP_FIELD));
		Object netName = data.get(HostIndicatorsConstant.NAME_FIELD);
		baseInfo.setName(netName!=null ? netName.toString() : "");
		Object resourceName = data.get(HostIndicatorsConstant.OS_FIELD);
		baseInfo.setSystem(resourceName!=null ? resourceName.toString() : "");
		Map<String, Object> metric = (Map<String, Object>)data.get(HostIndicatorsConstant.METRIC_FIELD);
		if(metric!=null){
			baseInfo.setStatus(metric.containsKey(HostIndicatorsConstant.AVAILABILITY_FIELD) ? metric.get(HostIndicatorsConstant.AVAILABILITY_FIELD).toString() : "");
		}else{
//			baseInfo.setStatus("UNKOWN");
			baseInfo.setStatus("NORMAL");
		}
		hostInfoVo.setBase(baseInfo);
		/***********组装主机基本信息end************/
		
		/***********组装主机CPU和RAM信息start************/
		CPU cpu = new CPU();
		RAM ram = new RAM();
		
		if(metric!=null){
			Object cpuRate = metric.get(HostIndicatorsConstant.CPU_RATE_FIELD);
			cpu.setRate(cpuRate != null ? cpuRate.toString() : "");
			cpu.setStatus(metric.containsKey(HostIndicatorsConstant.CPU_RATE_STATE_FIELD) ? metric.get(HostIndicatorsConstant.CPU_RATE_STATE_FIELD).toString() : "");
			
			Object ramRate = metric.get(HostIndicatorsConstant.RAM_RATE_FIELD);
			ram.setRate(ramRate!=null ? ramRate.toString() : "");
			ram.setStatus(metric.containsKey(HostIndicatorsConstant.RAM_RATE_STATE_FIELD) ? metric.get(HostIndicatorsConstant.RAM_RATE_STATE_FIELD).toString() : "");
		}
		hostInfoVo.setCpu(cpu);
		hostInfoVo.setRam(ram);
		/***********组装主机CPU和RAM信息end************/
		
		
		/***********组装网络接口信息start************/
		
		List<Map<String, Object>> interfacesDatas = null;
		try{
			interfacesDatas = detailInfoApi.getChildInstance(id, ResourceTypeConsts.TYPE_NETINTERFACE);
		}catch(Exception e){
			log.error("获取网络设备资源接口失败" + e.getMessage());
		}
		ArrayList<Interface> hostPartitions = new ArrayList<Interface>();
		
		if(interfacesDatas!=null){
			List<WorkBench> benchs=	userWorkBenchApi.getUserWorkBenchs(BaseAction.getLoginUser().getId());
	
			for(Map<String, Object> interfaceData : interfacesDatas){
				Interface interface1 = new Interface();
				Object name = interfaceData.get(HostIndicatorsConstant.INTERFACE_NAME_FIELD);
				interface1.setName(name!=null ? name.toString() : "");
				Object status = interfaceData.get(HostIndicatorsConstant.INTERFACE_STATE_FIELD);
				interface1.setStatus(status!=null ? status.toString() : "");
				interface1.setId(interfaceData.containsKey(HostIndicatorsConstant.INTERFACE_ID_FIELD) ? interfaceData.get(HostIndicatorsConstant.INTERFACE_ID_FIELD).toString() : "0");
				for (WorkBench workBench : benchs) {
					if(workBench.getDefaultId()!=null && String.valueOf(workBench.getDefaultId()).equals(interface1.getId()) && workBench.getSelfExt1().equals(interfaceType)){
						interface1.setIsCheck(true);
						
					}
				}
				if("real".equals(interfaceType)){
					if("ethernetCsmacd".equals(interfaceData.get("ifType")) || "gigabitEthernet".equals(interfaceData.get("ifType"))){
						hostPartitions.add(interface1);
					}
				}else if("logic".equals(interfaceType)){
					if(!"ethernetCsmacd".equals(interfaceData.get("ifType")) && !"gigabitEthernet".equals(interfaceData.get("ifType"))){
						hostPartitions.add(interface1);
					}
				}else{
					hostPartitions.add(interface1);
					
				}
			}
			for (WorkBench workBench : benchs) {
				for (int i=0;i<hostPartitions.size();i++) {
					if(workBench.getDefaultId()!=null && String.valueOf(workBench.getDefaultId()).equals(hostPartitions.get(i).getId()) ){
						isHave=1;
						
					}
				}
				
			}
		}
			if(isHave==0){//没有默认接口
				for (int i=0;i<hostPartitions.size();i++) {
					hostPartitions.get(0).setIsCheck(true);
				}
				}else{
					ArrayList<Interface> hostPartitionsTemp = new ArrayList<Interface>();	
					for (int i=0;i<hostPartitions.size();i++) {
						if(hostPartitions.get(i).getIsCheck()==true){
							hostPartitionsTemp.add(0, hostPartitions.get(i));
							for (int j=0;j<hostPartitions.size();j++) {
								if(j!=i){
									hostPartitionsTemp.add(hostPartitions.get(j));	
								}
							}
							
						}
					}
					hostPartitions=hostPartitionsTemp;
			}			
		hostInfoVo.setInterfaces(hostPartitions);
	
		/***********组装网络接口信息end************/
		return hostInfoVo;
	}
	
	@Override
	public HostNetInfoVo getNewNetDeviceInfo(Long id, String interfaceType,long userId) {
		int isHave=0;
		HostNetInfoVo hostInfoVo = new HostNetInfoVo();
		Map<String, Object > data = null;
		try{
			data = (Map<String, Object >)detailInfoApi.getResourceDetailInfo(id).get("parent");
			if(data == null){
				return null;
			}
		}catch(Exception e){
			log.error("获取网络设备资源指标失败" + e.getMessage());
			return null;
		}
		/***********组装主机基本信息start************/
		BaseInfo baseInfo = new BaseInfo();
		baseInfo.setIps((List<Map<String, Object>>)data.get(HostIndicatorsConstant.IP_FIELD));
		Object netName = data.get(HostIndicatorsConstant.NAME_FIELD);
		baseInfo.setName(netName!=null ? netName.toString() : "");
		Object resourceName = data.get(HostIndicatorsConstant.OS_FIELD);
		baseInfo.setSystem(resourceName!=null ? resourceName.toString() : "");
		Map<String, Object> metric = (Map<String, Object>)data.get(HostIndicatorsConstant.METRIC_FIELD);
		if(metric!=null){
			baseInfo.setStatus(metric.containsKey(HostIndicatorsConstant.AVAILABILITY_FIELD) ? metric.get(HostIndicatorsConstant.AVAILABILITY_FIELD).toString() : "");
		}else{
//			baseInfo.setStatus("UNKOWN");
			baseInfo.setStatus("NORMAL");
		}
		hostInfoVo.setBase(baseInfo);
		/***********组装主机基本信息end************/
		
		/***********组装主机CPU和RAM信息start************/
		CPU cpu = new CPU();
		RAM ram = new RAM();
		
		if(metric!=null){
			Object cpuRate = metric.get(HostIndicatorsConstant.CPU_RATE_FIELD);
			cpu.setRate(cpuRate != null ? cpuRate.toString() : "");
			cpu.setStatus(metric.containsKey(HostIndicatorsConstant.CPU_RATE_STATE_FIELD) ? metric.get(HostIndicatorsConstant.CPU_RATE_STATE_FIELD).toString() : "");
			
			Object ramRate = metric.get(HostIndicatorsConstant.RAM_RATE_FIELD);
			ram.setRate(ramRate!=null ? ramRate.toString() : "");
			ram.setStatus(metric.containsKey(HostIndicatorsConstant.RAM_RATE_STATE_FIELD) ? metric.get(HostIndicatorsConstant.RAM_RATE_STATE_FIELD).toString() : "");
		}
		hostInfoVo.setCpu(cpu);
		hostInfoVo.setRam(ram);
		/***********组装主机CPU和RAM信息end************/
		
		
		/***********组装网络接口信息start************/
		
		List<Map<String, Object>> interfacesDatas = null;
		try{
			interfacesDatas = detailInfoApi.getChildInstance(id, ResourceTypeConsts.TYPE_NETINTERFACE);
		}catch(Exception e){
			log.error("获取网络设备资源接口失败" + e.getMessage());
		}
		ArrayList<Interface> hostPartitions = new ArrayList<Interface>();
		HomeDefaultInterfaceBo hb = null;
		if(interfacesDatas!=null){
			//通过用户ID和资源ID查询是否存在默认接口
			hb = new HomeDefaultInterfaceBo();
			hb.setResourceId(id);
			hb.setUserId(userId);
			hb = homeDefaultInterfaceApi.getByUserIdAndResourceId(hb);
			
//			List<WorkBench> benchs=	userWorkBenchApi.getUserWorkBenchs(BaseAction.getLoginUser().getId());
	
			for(Map<String, Object> interfaceData : interfacesDatas){
				Interface interface1 = new Interface();
				Object name = interfaceData.get(HostIndicatorsConstant.INTERFACE_NAME_FIELD);
				interface1.setName(name!=null ? name.toString() : "");
				Object status = interfaceData.get(HostIndicatorsConstant.INTERFACE_STATE_FIELD);
				interface1.setStatus(status!=null ? status.toString() : "");
				interface1.setId(interfaceData.containsKey(HostIndicatorsConstant.INTERFACE_ID_FIELD) ? interfaceData.get(HostIndicatorsConstant.INTERFACE_ID_FIELD).toString() : "0");
//				for (WorkBench workBench : benchs) {
//					if(workBench.getDefaultId()!=null && String.valueOf(workBench.getDefaultId()).equals(interface1.getId()) && workBench.getSelfExt1().equals(interfaceType)){
//						interface1.setIsCheck(true);
//						
//					}
//				}
				if(hb != null && (String.valueOf(hb.getDefaultInterfaceId()).equals(interface1.getId()))){
					interface1.setIsCheck(true);
					isHave = 1;
				}
				
				if("real".equals(interfaceType)){
					if("ethernetCsmacd".equals(interfaceData.get("ifType")) || "gigabitEthernet".equals(interfaceData.get("ifType"))){
						hostPartitions.add(interface1);
					}
				}else if("logic".equals(interfaceType)){
					if(!"ethernetCsmacd".equals(interfaceData.get("ifType")) && !"gigabitEthernet".equals(interfaceData.get("ifType"))){
						hostPartitions.add(interface1);
					}
				}else{
					hostPartitions.add(interface1);
					
				}
			}
//			for (WorkBench workBench : benchs) {
//				for (int i=0;i<hostPartitions.size();i++) {
//					if(workBench.getDefaultId()!=null && String.valueOf(workBench.getDefaultId()).equals(hostPartitions.get(i).getId()) ){
//						isHave=1;
//						
//					}
//				}
//				
//			}
		}
		if(isHave==0){//没有默认接口
			if(hostPartitions != null && hostPartitions.size() > 0){
				hostPartitions.get(0).setIsCheck(true);
			}
//			for (int i=0;i<hostPartitions.size();i++) {
//				hostPartitions.get(0).setIsCheck(true);
//			}
		}else{
			ArrayList<Interface> hostPartitionsTemp = new ArrayList<Interface>();	
			for (int i=0;i<hostPartitions.size();i++) {
				if(hostPartitions.get(i).getIsCheck()==true){
					hostPartitionsTemp.add(0, hostPartitions.get(i));
					for (int j=0;j<hostPartitions.size();j++) {
						if(j!=i){
							hostPartitionsTemp.add(hostPartitions.get(j));	
						}
					}
					
				}
			}
			hostPartitions=hostPartitionsTemp;
		}			
		hostInfoVo.setInterfaces(hostPartitions);
		if(hb == null){
			hb = new HomeDefaultInterfaceBo();
			hb.setId(-1);	//-1表示不存在
			hb.setResourceId(id);
			hb.setDefaultInterfaceId(-1);	//-1表示不存在默认的接口id
		}
		hostInfoVo.setHomeDefaultInterfaceBo(hb);
	
		/***********组装网络接口信息end************/
		return hostInfoVo;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.home.workbench.netdevice.api.NetDeviceApi#getInterfaceIndicators(java.lang.Long)
	 */
	@Override
	public List<InterfaceIndicators> getInterfaceIndicators(Long id) {
		List<Map<String, Object>> indicators = null;
		try{
			indicators = detailInfoApi.getAllMetric(id);
		}catch(Exception e){
			log.error("获取网络设备资源接口指标失败" + e.getMessage());
		}
		ArrayList<InterfaceIndicators> results = new ArrayList<InterfaceIndicators>();
		if(indicators!=null){
			for(Map<String, Object> indicator : indicators){
				InterfaceIndicators result = new InterfaceIndicators();
				result.setId(indicator.containsKey(HostIndicatorsConstant.INTERFACE_INDICATORS_ID_FIELD) ? indicator.get(HostIndicatorsConstant.INTERFACE_INDICATORS_ID_FIELD).toString() : "");
				result.setText(indicator.containsKey(HostIndicatorsConstant.INTERFACE_INDICATORS_TEXT_FIELD) ? indicator.get(HostIndicatorsConstant.INTERFACE_INDICATORS_TEXT_FIELD).toString() : "");
				result.setValue(indicator.containsKey(HostIndicatorsConstant.INTERFACE_INDICATORS_VALUE_FIELD) ? indicator.get(HostIndicatorsConstant.INTERFACE_INDICATORS_VALUE_FIELD).toString() : "");
				results.add(result);
			}
		}
		return results;
	}
	@Override
	public HomeDefaultInterfaceBo setDefaultInterface(HomeDefaultInterfaceBo hb) {
		//查询是否存在当前用户该网络资源默认接口
		HomeDefaultInterfaceBo byUserIdAndResourceId = homeDefaultInterfaceApi.getByUserIdAndResourceId(hb);
		//如果默认接口为-1则是取消接口,否则添加默认接口
		if(hb.getDefaultInterfaceId() == -1){
			if(byUserIdAndResourceId != null){
				//删除
				homeDefaultInterfaceApi.delete(hb);
			}
		}else{
			if(byUserIdAndResourceId != null){
				homeDefaultInterfaceApi.updateByUserIdAndResourceId(hb);
			}else{
				hb.setId(homeDefaultInterfaceSeq.next());
				homeDefaultInterfaceApi.insert(hb);
			}
		}
		return hb;
	}
}
