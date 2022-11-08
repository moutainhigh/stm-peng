package com.mainsteam.stm.knowledge.modelset.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.CaplibAPIErrorCode;
import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.caplib.dict.DeviceTypeEnum;
import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.knowledge.modelset.api.IModelSetApi;
import com.mainsteam.stm.knowledge.modelset.bo.ModuleBo;
import com.mainsteam.stm.knowledge.modelset.bo.ModuleQueryBo;
import com.mainsteam.stm.knowledge.modelset.constants.ModelSetConstants;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.sysOid.SysOidReloadService;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.modelset.service.impl.ModelSetImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月2日
 */
@Service
public class ModelSetImpl implements IModelSetApi{
	
	@Autowired
	private CapacityService capacityService;
	
	@Autowired
	private SysOidReloadService sysOidReloadService;
	
	private Logger logger = Logger.getLogger(getClass());

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.modelset.api.IModelSetApi#getPage(com.mainsteam.stm.platform.mybatis.plugin.pagination.Page)
	 */
	@Override
	public void getPage(Page<ModuleBo, ModuleQueryBo> page) {
		ModuleQueryBo condition = page.getCondition();
		List<ModuleBo> result = new ArrayList<ModuleBo>();
		DeviceType[] datas = null;
		String sysioid = condition.getKeyword();
		String resourceType = condition.getResourceType();
		List<DeviceType> devices = new ArrayList<DeviceType>();
		page.setDatas(result);
		datas = capacityService.getAllDeviceTypes();
		
		if(StringUtils.isNotBlank(sysioid)&&datas!=null){
			for(DeviceType device : datas){
				if(StringUtils.isBlank(resourceType)
						||(device.getType()!=null&&device.getType().isHost()==(resourceType.equals(ModelSetConstants.RESOURCE_TYPE_HOST)))){
					String sysOid = device.getSysOid();
					if(StringUtils.isNotBlank(sysOid)&&sysOid.startsWith(sysioid)){
						devices.add(device);
					}
				}
			}
			page.setTotalRecord(devices.size());
			transalation(devices, result, page);
			return ;
		}
		if(resourceType!=null&&datas!=null){
			for(DeviceType device : datas){
				if(device.getType()!=null&&device.getType().isHost()==(resourceType.equals(ModelSetConstants.RESOURCE_TYPE_HOST))){
					devices.add(device);
				}
			}
		}else{
			devices = Arrays.asList(datas);
		}
		page.setTotalRecord(devices.size());
		transalation(devices, result, page);
	}
	
	private void transalation(List<DeviceType> datas, List<ModuleBo> result, Page<ModuleBo, ModuleQueryBo> page){
		int begin = (int)page.getStartRow(),
				end = (int)page.getRowCount()+ begin;
		
		if(begin>0){	//此处是由于前端传入分页参数原因
			begin--;
			end--;
		}
		for(int i=begin,len=datas.size(); i<end&&i<len; i++){
			ModuleBo moduleBo = new ModuleBo();
			try {
				DeviceType device = datas.get(i);
				BeanUtils.copyProperties(moduleBo, device);
				DeviceTypeEnum type = device.getType();
				if(type!=null){
					moduleBo.setResourceType(type.isHost() ? ModelSetConstants.RESOURCE_TYPE_HOST : ModelSetConstants.RESOURCE_TYPE_NETDEVICE);
				}
				result.add(moduleBo);
			} catch (IllegalAccessException e) {
				logger.warn("transalation " +e.getMessage(), e);
			} catch (InvocationTargetException e) {
				logger.warn("transalation " +e.getMessage(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.modelset.api.IModelSetApi#save(com.mainsteam.stm.knowledge.modelset.bo.ModuleBo)
	 */
	@Override
	public CaplibAPIResult save(ModuleBo moduleBo) {
		com.mainsteam.stm.sysOid.obj.ModuleBo module = new com.mainsteam.stm.sysOid.obj.ModuleBo();
		try {
			BeanUtils.copyProperties(module, moduleBo);
			List<DeployRecord> results = sysOidReloadService.sysOidReload(module);
			for(DeployRecord deployRecord : results){
				if(NodeFunc.portal.equals(deployRecord.getNodeFun())){
					return deployRecord.getResult() ? CaplibAPIResult.SUCESSFUL : new CaplibAPIResult(false, deployRecord.getResultCode()==null ? null : CaplibAPIErrorCode.valueOf(deployRecord.getResultCode()));
				}
			}
		} catch (IllegalAccessException e) {
			logger.error("save " +e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error("save " +e.getMessage(), e);
		}
		
		return null;
//		String resourceId = moduleBo.getResourceId();
//		if(StringUtils.isBlank(resourceId)){
//			return null;
//		}
//		DeviceType [] devices = capacityService.getDeviceTypeByResourceId(resourceId);
//		DeviceType device = null;
//		
//		try {
//			if(devices==null||devices.length==0){
//				return new CaplibAPIResult(false, CaplibAPIErrorCode.ADD_DEVICE_TYPE_05);
//			}else{
//				device = new DeviceType(devices[0]);
//			}
//			BeanUtils.copyProperties(device, moduleBo);
//			CaplibAPIResult result = capacityService.addType(device);
//			return result;
//		} catch (IllegalAccessException e) {
//			logger.error("save " +e.getMessage(), e);
//		} catch (InvocationTargetException e) {
//			logger.error("save " +e.getMessage(), e);
//		}
//		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.modelset.api.IModelSetApi#getModuleBoBysisOid(java.lang.String)
	 */
	@Override
	public Boolean getModuleBoBysisOid(String sysOid) {
		return capacityService.isExistResourceBySysoid(sysOid);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.modelset.api.IModelSetApi#delete(com.mainsteam.stm.knowledge.modelset.bo.ModuleBo)
	 */
	@Override
	public CaplibAPIResult delete(String sysOid) {
		return capacityService.removeType(sysOid);
	}
}
