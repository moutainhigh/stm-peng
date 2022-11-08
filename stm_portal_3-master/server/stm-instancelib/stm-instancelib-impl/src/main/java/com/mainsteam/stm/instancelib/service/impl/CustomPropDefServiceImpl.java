/**
 * 
 */
package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.CustomPropDefService;
import com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO;
import com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomPropDefinition;

/**
 * @author ziw
 * 
 */
public class CustomPropDefServiceImpl implements CustomPropDefService {

	private static final Log logger = LogFactory
			.getLog(CustomPropDefService.class);
	
	private CustomPropDefinitionDAO propDefinitionDAO;

	public void setPropDefinitionDAO(CustomPropDefinitionDAO propDefinitionDAO) {
		this.propDefinitionDAO = propDefinitionDAO;
	}

	/**
	 * 
	 */
	public CustomPropDefServiceImpl() {
	}

	public void start() {
		if (logger.isInfoEnabled()) {
			logger.info("start CustomPropDefServiceImpl");
		}
	}

	private CustomPropDefinitionDO convertToDO(CustomPropDefinition definition) {
		CustomPropDefinitionDO tdo = new CustomPropDefinitionDO();
		tdo.setCategory(definition.getCategory());
		tdo.setKey(definition.getKey());
		tdo.setName(definition.getName());
		tdo.setUpdateTime(definition.getUpdateTime().getTime());
		return tdo;
	}

	private CustomPropDefinition convertToDef(CustomPropDefinitionDO tdo) {
		CustomPropDefinition def = new CustomPropDefinition();
		def.setCategory(tdo.getCategory());
		def.setKey(tdo.getKey());
		def.setName(tdo.getName());
		def.setUpdateTime(new Date(tdo.getUpdateTime()));
		return def;
	}

	/**
	 * 验证输入参数
	 * 
	 * @param tdo
	 */
	private void validate(CustomPropDefinitionDO tdo) {
		if (StringUtils.isEmpty(tdo.getCategory())) {
			// TODO:throw exception
		}
		if (StringUtils.isEmpty(tdo.getKey())) {
			// TODO:throw exception
		}
		if (StringUtils.isEmpty(tdo.getName())) {
			// TODO:throw exception
		}
	}

	@Override
	public void addCustomPropDefinition(CustomPropDefinition definition)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addCustomPropDefinition start key="
					+ definition.getKey());
		}
		CustomPropDefinitionDO tdo = convertToDO(definition);
		validate(tdo);
		try {
			propDefinitionDAO.insertCustomPropDefinitionDO(tdo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isTraceEnabled()) {
			logger.trace("addCustomPropDefinition end");
		}
	}

	@Override
	public void updateCustomPropDefinition(CustomPropDefinition definition)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateCustomPropDefinition start key="
					+ definition.getKey());
		}
		CustomPropDefinitionDO tdo = convertToDO(definition);
		validate(tdo);
		try {
			propDefinitionDAO.updateCustomPropDefinitionDO(tdo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateCustomPropDefinition end");
		}
	}

	@Override
	public void removeCustomPropDefinitionByKey(List<String> keys)
			throws InstancelibException {
		try {
			propDefinitionDAO.removeCustomPropDefinitionDOByKey(keys);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public CustomPropDefinition getCustomPropDefinitionByKey(String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomPropDefinitionByKey start");
		}
		CustomPropDefinition result = null;
		if (key != null) {
			CustomPropDefinitionDO tdo = null;
			try {
				tdo = propDefinitionDAO
						.getCustomPropDefinitionDOByKey(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (tdo != null) {
				result = convertToDef(tdo);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomPropDefinitionByKey end");
		}
		return result;
	}

	@Override
	public Map<String, CustomPropDefinition> getCustomPropDefinitionsByKeys(
			List<String> keys) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomPropDefinitionsByKeys start");
		}
		Map<String, CustomPropDefinition> resultMap = null;
		if (keys != null && keys.size() > 0) {
			List<CustomPropDefinitionDO> defs = null;
			try {
				defs = propDefinitionDAO.getCustomPropDefinitionDOsByKeys(keys);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getCustomPropDefinitionsByKeys", e);
				}
				// TODO:抛出系统异常
				//throw e;
			}
			if (defs != null && defs.size() > 0) {
				resultMap = new HashMap<>(defs.size());
				for (Iterator<CustomPropDefinitionDO> iterator = defs
						.iterator(); iterator.hasNext();) {
					CustomPropDefinitionDO customPropDefinitionDO = iterator
							.next();
					resultMap.put(customPropDefinitionDO.getKey(),
							convertToDef(customPropDefinitionDO));
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomPropDefinitionsByKeys end");
		}
		return resultMap;
	}

	@Override
	public void addCustomPropDefinition(List<CustomPropDefinition> definitions)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addCustomPropDefinition start");
		}
		if (definitions == null || definitions.size() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("addCustomPropDefinition empty definition."
						+ definitions);
			}
			return;
		}
		List<CustomPropDefinitionDO> definitionDOs = new ArrayList<>(
				definitions.size());
		for (CustomPropDefinition customPropDefinition : definitions) {
			definitionDOs.add(convertToDO(customPropDefinition));
		}
		try {
			propDefinitionDAO.insertCustomPropDefinitionDOs(definitionDOs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isTraceEnabled()) {
			logger.trace("addCustomPropDefinition end");
		}
	}

}
