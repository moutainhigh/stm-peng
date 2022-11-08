package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.dao.CustomModulePropDAO;
import com.mainsteam.stm.instancelib.dao.pojo.CustomModulePropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.util.CustomModulePropCache;

public class CustomModulePropServiceImpl implements CustomModulePropService {

	private static final Log logger =  LogFactory.getLog(CustomModulePropServiceImpl.class);
	
	private CustomModulePropDAO customModulePropDAO;
	
	private CustomModulePropCache customModulePropCache;
	
	private ModulePropService modulePropService;
	
	public void setModulePropService(ModulePropService modulePropService) {
		this.modulePropService = modulePropService;
	}

	public void setCustomModulePropCache(CustomModulePropCache customModulePropCache) {
		this.customModulePropCache = customModulePropCache;
	}

	public void setCustomModulePropDAO(CustomModulePropDAO customModulePropDAO) {
		this.customModulePropDAO = customModulePropDAO;
	}

	private CustomModuleProp convertoBO(CustomModulePropDO propDO){
		CustomModuleProp cProp = new CustomModuleProp();
		cProp.setInstanceId(propDO.getInstanceId());
		cProp.setKey(propDO.getPropKey());
		cProp.setRealtimeValue(propDO.getRealtimeValue());
		cProp.setUserValue(propDO.getUserValue());
		return cProp;
	}
	
	private CustomModulePropDO convertoDO(CustomModuleProp customModuleProp){
		CustomModulePropDO cProp = new CustomModulePropDO();
		cProp.setInstanceId(customModuleProp.getInstanceId());
		cProp.setPropKey(customModuleProp.getKey());
		cProp.setRealtimeValue(customModuleProp.getRealtimeValue());
		cProp.setUserValue(customModuleProp.getUserValue());
		return cProp;
	}
	
	private ModuleProp convertoModuleProp(CustomModuleProp customModuleProp){
		ModuleProp cProp = new ModuleProp();
		cProp.setInstanceId(customModuleProp.getInstanceId());
		cProp.setKey(customModuleProp.getKey());
		cProp.setValues(new String[]{customModuleProp.getRealtimeValue()});
		return cProp;
	}
	
	private ModuleProp convertoModulePropInAdd(CustomModuleProp customModuleProp){
		ModuleProp cProp = new ModuleProp();
		cProp.setInstanceId(customModuleProp.getInstanceId());
		cProp.setKey(customModuleProp.getKey());
		cProp.setValues(new String[]{customModuleProp.getUserValue()});
		return cProp;
	}
	
	@Override
	public void addCustomModuleProp(CustomModuleProp customModuleProp) {
		if(customModuleProp == null){
			if(logger.isInfoEnabled()){
				logger.info("addCustomModuleProp parm is null");
			}
			return;
		}
		if(logger.isInfoEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("addCustomModuleProp start ");
			b.append("CustomModuleProp= ").append(customModuleProp);
			logger.info(b.toString());
		}
		CustomModulePropDO propDO = convertoDO(customModuleProp);
		try {
			customModulePropDAO.addCustomModuleProDO(propDO);
			customModulePropCache.add(customModuleProp.getInstanceId(), customModuleProp);
			//修改模型属性值，同步到dcs
			modulePropService.updateProp(convertoModulePropInAdd(customModuleProp));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isInfoEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("addCustomModuleProp end ");
			b.append("CustomModuleProp= ").append(customModuleProp);
			logger.info(b.toString());
		}
		
	}

	@Override
	public void addCustomModuleProps(List<CustomModuleProp> customModuleProps) {
		if(CollectionUtils.isEmpty(customModuleProps)){
			if(logger.isInfoEnabled()){
				logger.info("addCustomModuleProps parm(list) is empty");
			}
			return;
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("addCustomModuleProps start ");
			b.append("CustomModuleProps= ").append(customModuleProps);
			logger.debug(b.toString());
		}
		List<CustomModulePropDO> propDOs = new ArrayList<>(customModuleProps.size());
		List<ModuleProp> moduleProps = new ArrayList<>();
		for (CustomModuleProp customModuleProp : customModuleProps) {
			propDOs.add(convertoDO(customModuleProp));
			moduleProps.add(convertoModulePropInAdd(customModuleProp));
		}
		try {
			customModulePropDAO.addCustomModuleProDOs(propDOs);
			//修改模型属性值，同步到dcs
			modulePropService.updateProps(moduleProps);
			for (CustomModuleProp customModuleProp : customModuleProps) {
				customModulePropCache.add(customModuleProp.getInstanceId(), customModuleProp);
			}	
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("addCustomModuleProps end ");
			b.append("CustomModuleProps= ").append(customModuleProps);
			logger.debug(b.toString());
		}
	}
	
	@Override
	public CustomModuleProp getCustomModulePropByInstanceIdAndKey(long instanceId, String key) {
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("getCustomModulePropByInstanceIdAndKey start ");
			b.append("instanceId= ").append(instanceId);
			b.append(" key =").append(key);
			logger.debug(b.toString());
		}
		CustomModuleProp customModuleProp = customModulePropCache.get(instanceId, key);
		if(customModuleProp != null){
			return customModuleProp;
		}
		try {
			CustomModulePropDO po = customModulePropDAO.getCustomModulePropDOsByIdAndKey(instanceId, key);
			if(po != null){
				customModuleProp = convertoBO(po);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("getCustomModulePropByInstanceIdAndKey end ");
			b.append("instanceId = ").append(instanceId);
			b.append(" key = ").append(key);
			logger.debug(b.toString());
		}
		return customModuleProp;
	}

	@Override
	public List<CustomModuleProp> getCustomModulePropByInstanceId(long instanceId) {
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("getCustomModulePropByInstanceId start ");
			b.append("instanceId =").append(instanceId);
			logger.debug(b.toString());
		}
		List<CustomModuleProp> result = customModulePropCache.get(instanceId);
		if(CollectionUtils.isNotEmpty(result)){
			return result;
		}
		try {
			List<CustomModulePropDO> pos = customModulePropDAO.getCustomModulePropDOsById(instanceId);
			if(CollectionUtils.isNotEmpty(pos)){
				result = new ArrayList<>(pos.size());
				for (CustomModulePropDO propDO : pos) {
					CustomModuleProp cProp = convertoBO(propDO);
					result.add(cProp);
					customModulePropCache.add(instanceId, cProp);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("getCustomModulePropByInstanceId end ");
			b.append("instanceId =").append(instanceId);
			logger.debug(b.toString());
		}
		return result;
	}
	
	@Override
	public List<CustomModuleProp> getCustomModuleProp() {
		if(logger.isDebugEnabled()){
			logger.debug("getCustomModuleProp start");
		}
		List<CustomModuleProp> result = null;
		try {
			List<CustomModulePropDO> pos = customModulePropDAO.getCustomPropDOs();
			if(CollectionUtils.isNotEmpty(pos)){
				result = new ArrayList<>(pos.size());
				for (CustomModulePropDO propDO : pos) {
					CustomModuleProp cProp = convertoBO(propDO);
					result.add(cProp);
					customModulePropCache.add(cProp.getInstanceId(), cProp);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("getCustomModuleProp end");
		}
		return result;
	}

	@Override
	public void removeCustomModulePropByInstanceIdAndKey(long instanceId,String key) {
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("removeCustomModulePropByInstanceIdAndKey start ");
			b.append("instanceId=").append(instanceId);
			b.append(" key=").append(key);
			logger.debug(b.toString());
		}
		CustomModuleProp prop = getCustomModulePropByInstanceIdAndKey(instanceId, key);
		if(prop != null){
			customModulePropDAO.removeCustomProDOByIdAndKey(instanceId, key);
			customModulePropCache.remove(instanceId, key);
			/*
			 * 修改模型属性，同步到DCS
			 */
			try {
				modulePropService.updateProp(convertoModuleProp(prop));
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("removeCustomModulePropByInstanceIdAndKey end ");
			b.append("instanceId=").append(instanceId);
			b.append(" key=").append(key);
			logger.debug(b.toString());
		}
	}

	@Override
	public void removeCustomModulePropByInstanceId(long instanceId) {
		if(logger.isInfoEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("removeCustomModulePropByInstanceId start");
			b.append(" instanceId=").append(instanceId);
			logger.info(b.toString());
		}
		/*
		 * 修改模型属性，同步到DCS
		 */
		List<CustomModuleProp> props = getCustomModulePropByInstanceId(instanceId);
		if(CollectionUtils.isNotEmpty(props)){
			customModulePropDAO.removeCustomProDOById(instanceId);
			customModulePropCache.remove(instanceId);
			List<ModuleProp> moduleProps = new ArrayList<>(props.size());
			for (CustomModuleProp customModuleProp : props) {
				moduleProps.add(convertoModuleProp(customModuleProp));
			}
			try {
				modulePropService.updateProps(moduleProps);
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
		
		if(logger.isInfoEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("removeCustomModulePropByInstanceId end");
			b.append(" instanceId=").append(instanceId);
			logger.info(b.toString());
		}
	}

	@Override
	public void removeCustomModulePropByInstanceIds(List<Long> instanceIds) {
		if(CollectionUtils.isEmpty(instanceIds)){
			if(logger.isInfoEnabled()){
				logger.info("removeCustomModulePropByInstanceIds parms is empty");
			}
			return;
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("removeCustomModulePropByInstanceIds start");
			b.append(" instanceIds=").append(instanceIds);
			logger.debug(b.toString());
		}
		
		Map<Long,Long> parmInstanceMapIds = new HashMap<>();
		for (Long id : instanceIds) {
			parmInstanceMapIds.put(id, id);
		}
		
		List<CustomModuleProp> result = new ArrayList<>();
		
		List<CustomModuleProp> allProps = getCustomModuleProp();
		if (allProps != null) {
			for (CustomModuleProp customModuleProp : allProps) {
				if(parmInstanceMapIds.containsKey(customModuleProp.getInstanceId())){
					result.add(customModuleProp);
				}
			}
		}
		customModulePropDAO.removeCustomProDOByIds(instanceIds);
		for (long instanceId : instanceIds) {
			customModulePropCache.remove(instanceId);
		}
		
		/*
		 * 修改模型属性，同步到DCS
		 */
		List<ModuleProp> moduleProps = new ArrayList<>();
		
		for (CustomModuleProp customModuleProp : result) {
			moduleProps.add(convertoModuleProp(customModuleProp));
		}
		if(CollectionUtils.isNotEmpty(moduleProps)){
			try {
				modulePropService.updateProps(moduleProps);
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("removeCustomModulePropByInstanceIds end");
			b.append(" instanceIds=").append(instanceIds);
			logger.debug(b.toString());
		}
	}
	
	@Override
	public void updateCustomModuleProp(CustomModuleProp prop){
		if(prop == null){
			if(logger.isInfoEnabled()){
				logger.info("updateCustomModuleProp start parm is null");
			}
			return;
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("updateCustomModuleProp start");
			b.append(" CustomModuleProp=").append(prop);
			logger.debug(b.toString());
		}
		CustomModulePropDO propDO = convertoDO(prop);
		try {
			customModulePropDAO.updateCustomModulePropDO(propDO);
			customModulePropCache.update(prop.getInstanceId(), prop);
			//修改模型属性值，同步到dcs
			modulePropService.updateProp(convertoModulePropInAdd(prop));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("updateCustomModuleProp end");
			b.append(" CustomModuleProp=").append(prop);
			logger.debug(b.toString());
		}
	}

	@Override
	public void updateCustomModuleProps(List<CustomModuleProp> props){
		if(CollectionUtils.isEmpty(props)){
			if(logger.isInfoEnabled()){
				logger.info("updateCustomModuleProps start parm(list) is empty");
			}
			return;
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("updateCustomModuleProp start");
			b.append(" CustomModuleProps=").append(props);
			logger.debug(b.toString());
		}
		List<CustomModulePropDO> propDOs = new ArrayList<>(props.size());
		List<ModuleProp> moduleProps = new ArrayList<ModuleProp>();
		for (CustomModuleProp customModuleProp : props) {
			propDOs.add(convertoDO(customModuleProp));
			moduleProps.add(convertoModulePropInAdd(customModuleProp));
		}
		try {
			customModulePropDAO.updateCustomModulePropDOs(propDOs);
			//修改模型属性值，同步到dcs
			modulePropService.updateProps(moduleProps);
			for (CustomModuleProp customModuleProp : props) {
				customModulePropCache.update(customModuleProp.getInstanceId(), customModuleProp);
			}	
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if(logger.isDebugEnabled()){
			StringBuilder b = new StringBuilder(100);
			b.append("updateCustomModuleProp end");
			b.append(" CustomModuleProps=").append(props);
			logger.debug(b.toString());
		}
	}
}
