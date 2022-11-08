package com.mainsteam.stm.portal.resource.web.action;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.ResourceInstanceShowApi;
import com.mainsteam.stm.portal.resource.bo.ResourceInstancePageBo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceInstancePageVo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceInstanceVo;

/**
 * <li>文件名称: ResourceInstanceShowAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月7日
 * @author   pengl
 */
@Controller
@RequestMapping("/resourceManagement/resourceInstanceShow")
public class ResourceInstanceShowAction extends BaseAction{
	
	private static final Log logger = LogFactory.getLog(ResourceInstanceShowAction.class);
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ResourceInstanceShowApi resourceInstanceShowApi;
	
	@RequestMapping("/pageSelect")
	public JSONObject pageSelect(ResourceInstancePageVo instancePageVo){
		
		ResourceInstancePageBo pageBo = resourceInstanceShowApi.pageSelect(instancePageVo.getStartRow(), 
				instancePageVo.getRowCount(), instancePageVo.getCondition());
		instancePageVo.setTotalRecord(pageBo.getTotalRecord());
		
		List<ResourceInstanceVo> voList = new ArrayList<ResourceInstanceVo>();
		
		if(pageBo.getResources() != null && pageBo.getResources().size() > 0){
			
			for(ResourceInstance instance : pageBo.getResources()){
				
				voList.add(this.instanceToMonitorBo(instance));
				
			}
			
		}else{
			instancePageVo.setRowCount(0);
		}
		
		instancePageVo.setResources(voList);
		
		return toSuccess(instancePageVo);
	}

	/**
	 * BO对象转换VO对象
	 * @param resourceBo
	 * @return
	 */
	private ResourceInstanceVo instanceToMonitorBo(ResourceInstance resourceInstance){
		ResourceInstanceVo resourcerVo = new ResourceInstanceVo();
		resourcerVo.setId(resourceInstance.getId());
		resourcerVo.setSourceName(resourceInstance.getName());
		resourcerVo.setIpAddress(resourceInstance.getShowIP());
		resourcerVo.setMonitorType(capacityService.getCategoryById(resourceInstance.getCategoryId()).getName());
	    
		return resourcerVo;
	}
	
}
