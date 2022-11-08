package com.mainsteam.stm.portal.threed.web.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.threed.api.ICabinetApi;
import com.mainsteam.stm.portal.threed.api.IModelApi;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;
import com.mainsteam.stm.portal.threed.web.vo.DeviceVo;
import com.mainsteam.stm.portal.threed.xfire.exception.ThreeDException;

@Controller
@RequestMapping("/portal/3d/cabinet")
public class CabinetAction extends BaseAction{
	@Autowired
	ICabinetApi cabinetApi;
	@Autowired
	IModelApi modelApi;
	@Autowired
	ResourceInstanceService resourceInstanceService;
	@Autowired
	CapacityService capacityService;
	private Logger logger = Logger.getLogger(CabinetAction.class);
	
	/**
	 * 获取机柜结构树
	 * @return
	 */
	@RequestMapping("/getNodeTree")
	public JSONObject getNodeTree(){
		String tree;
		try {
			tree = cabinetApi.getNodeTree();
			logger.info("portal.portal.threed.getNodeTree successful");
			return toSuccess(tree);
		} catch (ThreeDException e) {
			logger.error("portal.portal.threed.getNodeTree failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(1000, e.getMessage());
		} catch (Exception e) {
			logger.error("portal.portal.threed.getNodeTree failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "数据库操作失败");
		}
		
	}
	/**
	 * 从server那边得到所有资源
	 * @return
	 */
	@RequestMapping("/getAllResource")
	public JSONObject getAllResource(){
		try {
			List<ResourceInstance> instances = resourceInstanceService.getAllParentInstance();
			List<DeviceVo> devices = new ArrayList<DeviceVo>();
			for(ResourceInstance instance : instances){
				CategoryDef cd = capacityService.getCategoryById(instance.getCategoryId());
				if(null!=cd){
					CategoryDef pcd = cd.getParentCategory();
					if(null!=pcd){
						if(!"Host".equals(pcd.getId()) && !"NetworkDevice".equals(pcd.getId()) && !"VM".equals(pcd.getId())) 
							continue;
						DeviceVo device = new DeviceVo();
						device.setId(instance.getId());
						device.setIp(instance.getShowIP());
						device.setName(instance.getShowName());
						if(cd!=null) device.setType(cd.getName());
						devices.add(device);
					}
				}
			}
			logger.info("portal.portal.threed.getAllResource successful");
			return toSuccess(devices);
		} catch (InstancelibException e) {
			logger.error("portal.portal.threed.getAllResource failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "从server获取设备异常!");
		}
	}
	/**
	 * 得到机柜下面的资源
	 * @param belong 所属机柜
	 * @return
	 */
	@RequestMapping("/getCabinetResource")
	public JSONObject getCabinetResource(Page<CabinetBo, String> page,String belong){
		page.setCondition(belong);
		cabinetApi.getPage(page);
		return toSuccess(page);
	}
	/**
	 * 批量添加设备到机柜
	 * @param data
	 * @param belong
	 * @return
	 */
	@RequestMapping("/batchAdd")
	public JSONObject batchAdd(String data,String belong){
		List<CabinetBo> boList = JSON.parseArray(data, CabinetBo.class);
		for(CabinetBo bo : boList){
			bo.setBelong(belong);
		}
		try {
			int count = cabinetApi.batchAdd(boList);
			logger.info("portal.portal.threed.batchAdd successful");
			return toSuccess(count);
		} catch (ThreeDException e) {
			logger.error("portal.portal.threed.batchAdd failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(1000,e.getMessage());
		}catch (Exception e) {
			logger.error("portal.portal.threed.batchAdd failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "数据库操作失败");
		}
	}
	/**
	 * 从机柜中移除设备
	 * @param ids
	 * @return
	 */
	@RequestMapping("/batchRemove")
	public JSONObject batchRemove(long[] ids){
		try {
			int count = cabinetApi.batchRemove(ids);
			logger.info("portal.portal.threed.batchRemove successful");
			return toSuccess(count);
		} catch (ThreeDException e) {
			logger.error("portal.portal.threed.batchRemove failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(1000, e.getMessage());
		} catch (Exception e) {
			logger.error("portal.portal.threed.batchRemove failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "数据库删除出错!");
		}
	}
	/**
	 * 更改设备布局、U位、型号
	 * @return
	 */
	@RequestMapping("/update")
	public JSONObject update(CabinetBo bo){
		try {
			int count = cabinetApi.update(bo);
			logger.info("portal.portal.threed.update successful");
			return toSuccess(count);
		} catch (ThreeDException e) {
			logger.error("portal.portal.threed.update failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(1000, e.getMessage());
		} catch (Exception e) {
			logger.error("portal.portal.threed.update failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "数据库更新出错!");
		}
	}
	/**
	 * 获取所有机柜中设备
	 * @return
	 */
	@RequestMapping("/getAllCabinetResource")
	public JSONObject getAllCabinetResource(){
		try {
			List<CabinetBo> cabinetBos = cabinetApi.getAllCabinetResource();
			logger.info("portal.portal.threed.getAllCabinetResource successful");
			return toSuccess(cabinetBos);
		} catch (Exception e) {
			logger.error("portal.portal.threed.getAllCabinetResource failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "获取所有机柜中设备失败!");
		}
	}
}
