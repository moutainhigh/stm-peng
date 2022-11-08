package com.mainsteam.stm.portal.vm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.vm.api.TopologyVmService;
import com.mainsteam.stm.portal.vm.bo.VmResourceTreeBo;
import com.mainsteam.stm.portal.vm.dao.IVmResourceTreeDao;
import com.mainsteam.stm.portal.vm.po.VmResourceTreePo;
import com.mainsteam.stm.state.obj.InstanceStateData;
/**
 * <li>文件名称: TopNVmServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月20日
 * @author   caoyong
 */
public class TopologyVmServiceImpl implements TopologyVmService {
	@Autowired
	private IVmResourceTreeDao vmResourceTreeDao;
	@Autowired
	private MetricDataService metricDataService;
	@Autowired
	private InstanceStateService instanceStateService;
	@Autowired
	private ResourceInstanceService instanceService;
	/**
	 * 日志
	 */
	private Logger logger = Logger.getLogger(TopologyVmServiceImpl.class);
	@Override
	public Object getLeftNavigateTree(){
		List<VmResourceTreePo> pos = vmResourceTreeDao.getLeftNavigateTree();
		
		List<VmResourceTreePo> posResult = new ArrayList<VmResourceTreePo>();
		for(VmResourceTreePo vtPo:pos){
			if(vtPo.getVmtype().equals("Datacenter")){
				//验证datacenter下是否有资源,如果没有,去除
				List<VmResourceTreePo> vtpList = vmResourceTreeDao.selectByPuuid(vtPo.getUuid());
				if(null!=vtpList&&vtpList.size()>0){
					posResult.add(vtPo);
				}
			}else {
				posResult.add(vtPo);
			}
		}
		List<VmResourceTreePo> result = new ArrayList<VmResourceTreePo>();
		//如果vcenter下的datacenter都被过滤,则删除vcenter
		for(VmResourceTreePo vtPo:posResult){
			if(vtPo.getVmtype().equals("VCenter")){
				for(VmResourceTreePo vtPoIn:posResult){
					if(vtPo.getUuid().equals(vtPoIn.getPuuid())){
						result.add(vtPo);
						break;
					}
				}
			}else{
				result.add(vtPo);
			}
		}
		
		return convertPos2Bos(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getTopologyData(VmResourceTreeBo bo){
		List result = new ArrayList();
		VmResourceTreePo vmpo = new VmResourceTreePo();
		BeanUtils.copyProperties(bo, vmpo);
		List<VmResourceTreePo> pos = vmResourceTreeDao.getTopologyData(vmpo);
		Map map = new HashMap();
		Map stateMap = new HashMap();
		List<Long> instanceids = new ArrayList<Long>();
		if(null!=pos && pos.size()>0){
			for(VmResourceTreePo po:pos){
				try {
					if(po.getInstanceid() == null){
						continue;
					}
					ResourceInstance instance=	instanceService.getResourceInstance(po.getInstanceid());
					if(instance!=null){
						po.setVmname(instance.getShowName());
					}
				} catch (InstancelibException e) {
					logger.error("malachi " + e);
					e.printStackTrace();
				}
				instanceids.add(po.getInstanceid());
				if("HostSystem".equals(po.getVmtype())){
					MetricData metricDatas = metricDataService.getMetricInfoData(po.getInstanceid(), "DatastoreList");
					if(null!=metricDatas) map.put(po.getInstanceid(), metricDatas.getData());
				
				}
			}
		}
		result.add(convertPos2Bos(pos));
		if(null!=instanceids && instanceids.size()>0){
			List<InstanceStateData> states = instanceStateService.findStates(instanceids);
			if(null!=states && states.size()>0){
				for(InstanceStateData s : states){
					stateMap.put(s.getInstanceID(), s.getState());
				}
			}
		}
		result.add(map);
		result.add(stateMap);
		return result;
	}
	private List<VmResourceTreeBo> convertPos2Bos(List<VmResourceTreePo> pos){
		List<VmResourceTreeBo> bos = new ArrayList<VmResourceTreeBo>();
		if(null!=pos && pos.size()>0){
			for(VmResourceTreePo po: pos){
				VmResourceTreeBo bo = new VmResourceTreeBo();
				BeanUtils.copyProperties(po, bo);
				bos.add(bo);
			}
		}
		return bos;
	}
}
