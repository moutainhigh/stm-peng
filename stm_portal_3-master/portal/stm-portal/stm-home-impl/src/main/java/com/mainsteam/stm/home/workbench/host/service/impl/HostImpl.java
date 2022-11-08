package com.mainsteam.stm.home.workbench.host.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.home.workbench.host.api.HostApi;
import com.mainsteam.stm.home.workbench.host.constant.HostIndicatorsConstant;
import com.mainsteam.stm.home.workbench.host.vo.BaseInfo;
import com.mainsteam.stm.home.workbench.host.vo.CPU;
import com.mainsteam.stm.home.workbench.host.vo.HostNetInfoVo;
import com.mainsteam.stm.home.workbench.host.vo.Interface;
import com.mainsteam.stm.home.workbench.host.vo.Partition;
import com.mainsteam.stm.home.workbench.host.vo.RAM;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.host.service.impl.HostImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月15日
 */
@Service
@SuppressWarnings("unchecked")
public class HostImpl implements HostApi{
	
	@Resource(name="resourceDetailInfoApi")
	private IResourceDetailInfoApi detailInfoApi;
	
	private Logger log = Logger.getLogger(getClass());
	

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.home.workbench.host.api.HostApi#getHostBaseInfo(java.lang.Long)
	 */
	
	@Override
	public HostNetInfoVo getHostInfo(Long id) {
		HostNetInfoVo hostInfoVo = new HostNetInfoVo();
		Map<String, Object > data = null;
		try{
			data = (Map<String, Object >)detailInfoApi.getResourceDetailInfo(id).get("parent");
			if(data == null){
				return new HostNetInfoVo();
			}
		}catch(Exception e){
			log.error("获取主机资源指标失败" + e.getMessage());
			return null;
		}
		
		 
		/***********组装主机基本信息start************/
		BaseInfo baseInfo = new BaseInfo();
		baseInfo.setIps((List<Map<String, Object>>)data.get(HostIndicatorsConstant.IP_FIELD));
		Object hostName = data.get(HostIndicatorsConstant.NAME_FIELD);
		baseInfo.setName(hostName!=null ? hostName.toString() : "");
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
			cpu.setRate(cpuRate!=null ? cpuRate.toString() : "");
			cpu.setStatus(metric.containsKey(HostIndicatorsConstant.CPU_RATE_STATE_FIELD) ? metric.get(HostIndicatorsConstant.CPU_RATE_STATE_FIELD).toString() : "");
			Object ramRate = metric.get(HostIndicatorsConstant.RAM_RATE_FIELD);
			ram.setRate(ramRate !=null ? ramRate.toString() : "");
			ram.setStatus(metric.containsKey(HostIndicatorsConstant.RAM_RATE_STATE_FIELD) ? metric.get(HostIndicatorsConstant.RAM_RATE_STATE_FIELD).toString() : "");
		}
		hostInfoVo.setCpu(cpu);
		hostInfoVo.setRam(ram);
		/***********组装主机CPU和RAM信息end************/
		
		/***********组装主机分区信息start************/
		if(metric!=null){
			List<Map<String, Object>> partitionsMap = (List<Map<String, Object>>)metric.get(HostIndicatorsConstant.PARTITIONS_FIELD);
			ArrayList<Partition> hostPartitions = new ArrayList<Partition>();
			Iterator<Map<String, Object>> iter = partitionsMap.iterator();
			while(iter.hasNext()){
				Map<String, Object> map = iter.next();
				Partition partition = new Partition();
				partition.setLetter(map.containsKey(HostIndicatorsConstant.PARTITION_NAME_FIELD) ? map.get(HostIndicatorsConstant.PARTITION_NAME_FIELD).toString() : "");
				Object rate = map.get(HostIndicatorsConstant.PARTITION_RATE_FIELD);
				partition.setRate(rate!=null ? rate.toString() : "");
				partition.setStatus(map.containsKey(HostIndicatorsConstant.PARTITION_RATE_STATE_FIELD) ? map.get(HostIndicatorsConstant.PARTITION_RATE_STATE_FIELD).toString() : "");
				if(map.get(HostIndicatorsConstant.INTERFACE_STATE_FIELD).toString().equals("NOT_MONITORED")){//INTERFACE_STATE_FIELD
					continue;
				}
				hostPartitions.add(partition);
			}
			hostInfoVo.setPartitions(hostPartitions);
		}
		/***********组装主机分区信息end************/
		
		/***********组装网络接口信息start************/
		List<Map<String, Object>> interfacesDatas = null;
		try{
			interfacesDatas = detailInfoApi.getChildInstance(id, ResourceTypeConsts.TYPE_NETINTERFACE);
		}catch(Exception e){
			log.error("获取主机资源接口失败" + e.getMessage());
		}
		ArrayList<Interface> hostPartitions = new ArrayList<Interface>();
		if(interfacesDatas!=null){
			for(Map<String, Object> interfaceData : interfacesDatas){
				Interface interface1 = new Interface();
				Object name = interfaceData.get(HostIndicatorsConstant.INTERFACE_NAME_FIELD);
				interface1.setName(name!=null ? name.toString() : "");
				Object status = interfaceData.get(HostIndicatorsConstant.INTERFACE_STATE_FIELD);
				interface1.setStatus(status!=null ? status.toString() : "");
				interface1.setId(interfaceData.containsKey(HostIndicatorsConstant.INTERFACE_ID_FIELD) ? interfaceData.get(HostIndicatorsConstant.INTERFACE_ID_FIELD).toString() : "0");
				hostPartitions.add(interface1);
			}
		}
		hostInfoVo.setInterfaces(hostPartitions);
		return hostInfoVo;
	}
}
