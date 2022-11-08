package com.mainsteam.stm.portal.config.web.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.config.api.IConfigDeviceApi;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.portal.config.web.vo.DeviceResourceVo;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
/**
 * <li>文件名称: ConfigDeviceAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/config/device")
public class ConfigDeviceAction extends BaseAction {
	private Logger logger = Logger.getLogger(ConfigDeviceAction.class);
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private IConfigDeviceApi configDeviceApi;
	@Autowired
	private IFileClientApi fileClientApi;
	
	@RequestMapping("/get")
	public JSONObject get(Long id){
		try {
			logger.info("portal.config.device.get successful");
			return toSuccess(configDeviceApi.get(id));
		} catch (Exception e) {
			logger.error("portal.config.device.get failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "查询失败");
		}
	}
	/**
	 * 编辑设备资源
	 * @param bo
	 * @return
	 */
	@RequestMapping("/update")
	public JSONObject update(ConfigDeviceBo bo){
		try {
			int count = configDeviceApi.update(bo);
			if(count == -1){
				return toJsonObject(444, "登录信息错误");
			}
			logger.info("portal.config.device.update successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.device.update failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "操作失败");
		}
	}
	/**
	 * 设备一览分页列表页数据
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/getConfigDevicePage", method=RequestMethod.POST)
	public JSONObject getConfigDevicePage(Page<ConfigDeviceBo, ConfigDeviceBo> page,String groupId,String ipOrName){
		LoginUser user=(LoginUser)getLoginUser();
		
		try {
			configDeviceApi.selectByPage(page,groupId,ipOrName,user);
			logger.info("portal.config.device.getConfigDevicePage successful");
		} catch (Exception e) {
			List<ConfigDeviceBo> returnList = new ArrayList<ConfigDeviceBo>();
			page.setDatas(returnList);
			logger.error("portal.config.device.getConfigDevicePage failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	/**
	 * 查询除去已经添加进配置文件管理资源表中资源的资源
	 * @param searchKey 查询关键字(设备名称或ip地址)
	 * @return
	 */
	@RequestMapping("/getAllExceptionResourceInstanceList")
	public JSONObject getAllExceptionResourceInstanceList(String searchKey){
		List<ResourceInstance> instances;
		Page<DeviceResourceVo, Object> page = new Page<DeviceResourceVo, Object>();
		try {
			instances = configDeviceApi.getAllExceptionResourceInstanceList(searchKey);
			List<DeviceResourceVo> returnList = new ArrayList<DeviceResourceVo>();
			for(int i=0;i<instances.size();i++){
				DeviceResourceVo vo = new DeviceResourceVo();
				ResourceInstance instance = instances.get(i);
				vo.setId(instances.get(i).getId());
				vo.setResourceTypeId(capacityService.getResourceDefById(instances.get(i).getResourceId()).getName());
				vo.setIntanceName(instance.getShowName());
				vo.setIpAddress(instance.getShowIP());
				returnList.add(vo);
			}
			page.setDatas(returnList);
			logger.info("portal.config.device.getAllExceptionResourceInstanceList successful");
		} catch (Exception e) {
			List<DeviceResourceVo> returnList = new ArrayList<DeviceResourceVo>();
			page.setDatas(returnList);
			logger.error("portal.config.device.getAllExceptionResourceInstanceList failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	/**
	 * 添加设备资源
	 * @param 
	 * @return
	 */
	@RequestMapping("/addConfigDevice")
	public JSONObject addConfigDevice(Long[] ids,String groupId) {
		try {
			int count = configDeviceApi.batchInsert(ids,groupId);
			logger.info("portal.config.device.addConfigDevice successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.device.addConfigDevice failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "操作失败");
		}
	}
	
	/**
	 * 删除设备资源
	 * @param id
	 * @return
	 */
	@RequestMapping("/delConfigDevice")
	public JSONObject delConfigDevice(Long[] ids) {
		try {
			int count = configDeviceApi.batchDel(ids);
			logger.info("portal.config.device.delConfigDevice successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.device.delConfigDevice failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "删除设备失败");
		}
	}
	@RequestMapping("/readfilebylist")
    public JSONObject readfilebylist(String filePath){
		try {
			logger.info("portal.config.device.readfilebylist successful");
			return toSuccess(configDeviceApi.readfilebylist(filePath));
		} catch (Exception e) {
			logger.error("portal.config.device.readfilebylist failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "读取文件失败");
		}
    }
	@RequestMapping("/comparecfgfile")
    public JSONObject comparecfgfile(String filePath1,String filePath2){
		try {
			logger.info("portal.config.device.comparecfgfile successful");
			return toSuccess(configDeviceApi.comparecfgfile(filePath1, filePath2));
		} catch (Exception e) {
			logger.error("portal.config.device.comparecfgfile failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "比较文件失败");
		}
    }
	
	/**
	 * 手动备份选中的资源设备
	 * @param ids
	 * @return
	 */
	@RequestMapping("/backupResourcesByIds")
	public JSONObject backupResourcesByIds(Long[] ids){
		try {
			if(ids==null || ids.length<1){
				ids = configDeviceApi.getAllResourceIds().toArray(new Long[]{});
			}
			String msg = configDeviceApi.backupResourcesByIds(ids,true);
			logger.info("portal.config.device.backupResourcesByIds successful");
			return toSuccess(msg);
		} catch (Exception e) {
			logger.error("portal.config.device.backupResourcesByIds failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "手动备份失败，失败原因："+e.getMessage());
		}
	}
	
	/**
	 * 恢复选中的配置文件
	 * @param id
	 * @return
	 */
	@RequestMapping("/recoveryResources")
	public JSONObject recoveryResources(String filePath, Long id){
		try {
			String msg = configDeviceApi.recoveryResources(filePath,id);
			logger.info("portal.config.device.recoveryResources successful");
			return toSuccess(msg);
		} catch (Exception e) {
			logger.error("portal.config.device.recoveryResources failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "恢复失败，失败原因："+e.getMessage());
		}
	}
}
