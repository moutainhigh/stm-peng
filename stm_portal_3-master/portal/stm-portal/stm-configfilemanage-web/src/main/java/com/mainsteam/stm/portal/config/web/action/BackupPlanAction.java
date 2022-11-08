package com.mainsteam.stm.portal.config.web.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.config.api.IBackupPlanApi;
import com.mainsteam.stm.portal.config.api.IConfigCustomGroupApi;
import com.mainsteam.stm.portal.config.api.IConfigDeviceApi;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.portal.config.web.vo.BackupPlanVo;
import com.mainsteam.stm.portal.config.web.vo.DeviceResourceVo;

/**
 * 
 * <li>文件名称: BackupPlanAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日
 * @author   liupeng
 */
@Controller
@RequestMapping("/portal/config/plan")
public class BackupPlanAction extends BaseAction{
	@Autowired
	private IConfigCustomGroupApi configCustomGroupApi;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private CapacityService capacityService;
	@Autowired
	private IConfigDeviceApi configDeviceApi;
	@Autowired
	private IBackupPlanApi backupPlanApi;
	/**
	 * 获取所有资源实例
	 * @return
	 */
	@RequestMapping("/getAllResourceInstanceList")
	public JSONObject getAllResourceInstanceList(){
		List<ResourceInstance> instances = configCustomGroupApi.getAllResourceInstanceList();
		Page<DeviceResourceVo, Object> page = new Page<DeviceResourceVo, Object>();
		List<DeviceResourceVo> returnList = new ArrayList<DeviceResourceVo>();
		for(int i=0;i<instances.size();i++){
			DeviceResourceVo vo = new DeviceResourceVo();
			ResourceInstance instance = instances.get(i);
			vo.setId(instance.getId());
			vo.setResourceTypeId(capacityService.getResourceDefById(instance.getResourceId()).getName());
			vo.setIntanceName(instance.getShowName());
			vo.setIpAddress(instance.getShowIP());
			returnList.add(vo);
		}
		page.setDatas(returnList);
		return toSuccess(page);
	}
	@RequestMapping("/getPlanDevice")
	public JSONObject getPlanDevice(Long planId){
		List<ConfigDeviceBo> data = planId==null?new ArrayList<ConfigDeviceBo>():configDeviceApi.getDeviceByPlanId(planId);
		Page<DeviceResourceVo, Object> page = searchDevice(data);
		return toSuccess(page);
	}
	@RequestMapping("/getNotPlanDevice")
	public JSONObject getNotPlanDevice(Long planId){
		List<ConfigDeviceBo> data = configDeviceApi.getDeviceExcept(planId);
		Page<DeviceResourceVo, Object> page = searchDevice(data);
		return toSuccess(page);
	}
	@RequestMapping("/getPager")
	public JSONObject getPager(@ModelAttribute Page<BackupPlanBo, Object> pager){
		backupPlanApi.queryPlanByPager(pager);
		return toSuccess(pager);
	}
	@RequestMapping("/addPlan")
	public JSONObject addPlan(@ModelAttribute BackupPlanVo vo,HttpSession session){
		BackupPlanBo bo = vo.toBo();
		ILoginUser user = getLoginUser(session);
		bo.setEntryId(user.getId());
		BackupPlanBo returnBo = backupPlanApi.addOrUpdatePlan(bo);
		if(returnBo == null){
			return toJsonObject(201, "该备份计划名称已被使用");
		}
		return toSuccess(bo);
	}
	@RequestMapping("/getPlan")
	public JSONObject getPlan(Long id){
		BackupPlanBo bo = backupPlanApi.queryPlanById(id);
		return toSuccess(bo);
	}
	@RequestMapping("/batchRemove")
	public JSONObject batchRemove(long[] ids){
		return toSuccess(backupPlanApi.batchRemovePlan(ids));
	}
	@RequestMapping("/upateDevicePlan")
	public JSONObject upateDevicePlan(Long[] planDevices,Long[] notPlanDevices,Long planId){
		BackupPlanBo bo = new BackupPlanBo();
		bo.setId(planId);
		bo.getResourceIds().addAll(Arrays.asList(planDevices));
		bo.getResourceIds().add(0L);
		//将资源绑定到该计划下
		int rows1 = backupPlanApi.upateDevicePlan(bo);
		if(notPlanDevices!=null){
			BackupPlanBo notPlanBo = new BackupPlanBo();
			notPlanBo.getResourceIds().addAll(Arrays.asList(notPlanDevices));
			notPlanBo.getResourceIds().add(0L);
			//从该计划下移除资源
			int rows2 = backupPlanApi.upateDevicePlan(notPlanBo);
			return toSuccess(rows1+rows2);
		}else{
			return toSuccess(rows1);
		}
	}
	/**
	 * 从server中查询资源详细信息
	 * @param list
	 * @return
	 */
	private Page<DeviceResourceVo, Object> searchDevice(List<ConfigDeviceBo> list){
		Page<DeviceResourceVo, Object> page = new Page<DeviceResourceVo, Object>();
		List<DeviceResourceVo> data = new ArrayList<DeviceResourceVo>();
		for(ConfigDeviceBo bo :list){
			DeviceResourceVo vo = new DeviceResourceVo();
			try {
				ResourceInstance instance = resourceInstanceService.getResourceInstance(bo.getId());
				if(instance != null){
					vo.setId(instance.getId());
					vo.setIntanceName(instance.getShowName());
					vo.setIpAddress(instance.getShowIP());
					vo.setBackupId(bo.getBackupId());
					vo.setDeviceType(capacityService.getResourceDefById(instance.getResourceId()).getName());
//					String[] sysObjectIds = instance.getModulePropBykey("sysObjectID");
//					if(sysObjectIds!=null&&sysObjectIds.length>0){
//						DeviceType deviceType = capacityService.getDeviceType(sysObjectIds[0]);//根据sysoid取deviceType
//						if(deviceType!=null){
//							vo.setDeviceType(deviceType.getType().name());//设备类型
//						}
//					}
					data.add(vo);
				}
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
		}
		page.setDatas(data);
		return page;
	}
}
