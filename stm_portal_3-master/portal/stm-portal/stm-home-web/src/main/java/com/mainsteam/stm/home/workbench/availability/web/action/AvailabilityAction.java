package com.mainsteam.stm.home.workbench.availability.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.home.workbench.availability.api.IAvailabilityApi;
import com.mainsteam.stm.home.workbench.availability.bo.AvailabilityBo;
import com.mainsteam.stm.home.workbench.availability.web.vo.AvailabilityVo;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
@Controller
@RequestMapping("/home/availability")
public class AvailabilityAction extends BaseAction{
	@Resource
	private CapacityService capacityService;
	@Resource(name="stm_home_workbench_availability_availabilityApi")
	private IAvailabilityApi availabilityApi;
	@RequestMapping("/getById")
	public JSONObject getResource(Long[] resourceId) {
		List<AvailabilityVo> listVo = new ArrayList<AvailabilityVo>();
		Page<AvailabilityVo, AvailabilityBo> page = new Page<AvailabilityVo, AvailabilityBo>();
		if (resourceId != null) {
			List<AvailabilityBo> list = availabilityApi.getResourceDetailInfo(resourceId);
			for (AvailabilityBo bo : list) {
				AvailabilityVo vo = toVo(bo);
				listVo.add(vo);
			}
			page.setDatas(listVo);
			page.setTotalRecord(listVo.size());
		}
		return toSuccess(page);
	}
	
	public AvailabilityVo toVo(AvailabilityBo bo){
		ResourceInstance resourceInstance = bo.getResourceInstance();
		AvailabilityVo vo=new AvailabilityVo();
		vo.setId(resourceInstance.getId());
		vo.setSourceName(resourceInstance.getShowName());
		vo.setIpAddress(resourceInstance.getShowIP());
		vo.setMonitorType(null==capacityService.getCategoryById(resourceInstance.getCategoryId())?"":capacityService.getCategoryById(resourceInstance.getCategoryId()).getName());
		vo.setInstanceState(resourceInstance.getLifeState().toString());
		vo.setCpuStatus(bo.getCpuStatus());
		vo.setCpuAvailability(bo.getCpuAvailability());
		vo.setMemoryStatus(bo.getMemoryStatus());
		vo.setMemoryAvailability(bo.getMemoryAvailability());
		vo.setResponseTime(bo.getResponseTime());
		vo.setInstanceStatus(bo.getInstanceStatus());
		return vo;
	}
}
