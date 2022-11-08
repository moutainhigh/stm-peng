/**
 * 
 */
package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.ModulePropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * 模型配置属性业务类
 * 
 * @author xiaoruqiang
 */
public class ModulePropServiceImpl implements ModulePropService, ModulePropExtendService {

	private static final Log logger = LogFactory.getLog(ModulePropService.class);

	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.MODULE;

	private PropDAO propDAO;

	private PropTypeDAO propTypeDAO;

	private InstancelibEventManager instancelibEventManager;

	private PropCache propCache;

	public void setPropCache(PropCache propCache) {
		this.propCache = propCache;
	}

	public void setInstancelibEventManager(InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}

	public void setPropTypeDAO(PropTypeDAO propTypeDAO) {
		this.propTypeDAO = propTypeDAO;
	}

	public void setPropDAO(PropDAO propDAO) {
		this.propDAO = propDAO;
	}

	/**
	 * 
	 */
	public ModulePropServiceImpl() {
	}

	public PropDO convertToDO(InstanceProp prop) {
		PropDO tdo = new PropDO();
		tdo.setInstanceId(prop.getInstanceId());
		tdo.setPropKey(prop.getKey());
		tdo.setPropType(PROP_TYPE.toString());
		return tdo;
	}

	public List<PropDO> convertToDOs(InstanceProp prop) {
		String[] values = prop.getValues();
		List<PropDO> tdos = new ArrayList<PropDO>();
		if (values != null) {
			for (String value : values) {
				PropDO tdo = new PropDO();
				tdo.setInstanceId(prop.getInstanceId());
				tdo.setPropKey(prop.getKey());
				tdo.setPropType(PROP_TYPE.toString());
				if (value != null) {
					tdo.setPropValue(value.trim());
				}
				tdos.add(tdo);
			}
		} else {
			PropDO tdo = new PropDO();
			tdo.setInstanceId(prop.getInstanceId());
			tdo.setPropKey(prop.getKey());
			tdo.setPropType(PROP_TYPE.toString());
			tdos.add(tdo);
		}

		return tdos;
	}

	public List<ModuleProp> convertToDef(List<PropDO> tdos) {
		List<ModuleProp> defs = new ArrayList<>(tdos.size());
		Map<String, ModuleProp> map = new HashMap<String, ModuleProp>();

		for (PropDO tdo : tdos) {
			if (map.containsKey(tdo.getPropKey())) {
				ModuleProp prop = map.get(tdo.getPropKey());
				String[] values = prop.getValues();
				String[] newValues = new String[values.length + 1];
				System.arraycopy(values, 0, newValues, 0, values.length);
				newValues[newValues.length - 1] = tdo.getPropValue();
				prop.setValues(newValues);
				map.put(tdo.getPropKey(), prop);
			} else {
				String[] values = { tdo.getPropValue() };
				ModuleProp def = new ModuleProp();
				def.setInstanceId(tdo.getInstanceId());
				def.setKey(tdo.getPropKey());
				def.setValues(values);
				defs.add(def);
				map.put(tdo.getPropKey(), def);
			}
		}
		map = null;
		return defs;
	}

	/**
	 * 验证输入参数
	 * 
	 * @param tdo
	 */
	private void validate(PropDO tdo) {

		if (StringUtils.isEmpty(tdo.getPropKey())) {
			throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Module property key is null or empty");
		}
		if (StringUtils.isEmpty(tdo.getPropType())) {
			throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Module property type is null or empty");
		}
	}

	public void addProp(ModuleProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for ModuleProp start key= " + prop.getKey() + " instanceId=" + prop.getInstanceId());
		}
		// 根据一个value值存放一条数据
		String[] values = prop.getValues();
		if (values == null) {
			return;
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop, null, EventEnum.MODULE_PROPERTY_ADD_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "addProp for ModuleProp interceptor error!");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();

		for (String value : values) {
			PropDO tdo = convertToDO(prop);
			tdo.setPropValue(value);
			validate(tdo);
			tdos.add(tdo);
		}
		// 存放属性值
		try {
			propDAO.insertPropDOs(tdos);
			// 存放属性类型值
			propTypeDAO.insertPropTypeDO(convertToDO(prop));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "addProp for ModuleProp error!");
		}
		// 放入到缓存
		propCache.add(prop.getInstanceId(), PROP_TYPE, prop);
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for ModuleProp end");
		}
	}

	@Override
	public void updateProp(ModuleProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for ModuleProp start key= " + prop.getKey() + " instanceId=" + prop.getInstanceId());
		}

		// 根据一个value值存放一条数据
		String[] values = prop.getValues();
		if (values == null) {
			return;
		}

		ModuleProp cacheProp = getPropByInstanceAndKey(prop.getInstanceId(), prop.getKey());

		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(cacheProp, prop, EventEnum.MODULE_PROPERTY_UPDATE_EVENT);

		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();

		for (String value : values) {
			PropDO tdo = convertToDO(prop);
			tdo.setPropValue(value);
			validate(tdo);
			tdos.add(tdo);
		}
		try {
			propDAO.updatePropDOs(tdos);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateProp error!");
		}
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "updateProp interceptor error!");
		}
		// 更新缓存
		propCache.update(prop.getInstanceId(), PROP_TYPE, prop);
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for ModuleProp end");
		}
	}

	public void removePropByInstanceAndKey(long instanceId, String key) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for ModuleProp start instanceId=" + instanceId);
		}
		ModuleProp prop = getPropByInstanceAndKey(instanceId, key);

		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop, null, EventEnum.MODULE_PROPERTY_DELETE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "removePropByInstanceAndKey for ModuleProp interceptor error!");
		}
		if (StringUtils.isNotEmpty(key)) {
			try {
				propDAO.removePropDOByInstanceAndKey(instanceId, key, PROP_TYPE.toString());
				List<PropDO> propDOs = propDAO.getPropDOsByInstance(instanceId, PROP_TYPE.toString());
				if (propDOs == null || propDOs.isEmpty()) {
					propTypeDAO.removePropTypeDOByInstanceAndKey(instanceId, key, PROP_TYPE.toString());
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "removePropByInstanceAndKey for ModuleProp error!");
			}
			// 从缓存中移除删除的属性值
			propCache.remove(instanceId, key, PROP_TYPE);
		}

		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for ModuleProp end");
		}

	}

	public void addProps(List<ModuleProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for ModuleProp start");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 保存属性类型值
		List<PropDO> typeDos = new ArrayList<PropDO>();

		for (ModuleProp moduleProp : props) {
			String[] values = moduleProp.getValues();
			if (values == null) {
				continue;
			}
			for (String value : values) {
				PropDO tdo = convertToDO(moduleProp);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
			typeDos.add(convertToDO(moduleProp));
		}

		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(props, null, EventEnum.MODULE_PROPERTY_ADD_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "addProps for ModuleProp interceptor error!");
		}

		// 存放属性值
		try {
			propDAO.insertPropDOs(tdos);
			// 存放属性类型值
			propTypeDAO.insertPropTypeDOs(typeDos);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "addProp for ModuleProp error!");
		}
		// TODO
		// 模型属性添加到缓存
		// for (ModuleProp moduleProp : props) {
		// propCache.add(moduleProp.getInstanceId(), PROP_TYPE, moduleProp);
		// }

		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for ModuleProp end");
		}
	}

	@Override
	public void updateProps(List<ModuleProp> props) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("updateProps for ModuleProp start =" + props);
		}
		if (CollectionUtils.isEmpty(props)) {
			return;
		}
		List<ModuleProp> oldAllModuleProps = new ArrayList<>();
		List<PropDO> tdos = new ArrayList<PropDO>();
		for (ModuleProp moduleProp : props) {
			long instanceId = moduleProp.getInstanceId();
			List<ModuleProp> oldSignModuleProps = getPropByInstanceId(instanceId);
			if (!CollectionUtils.isEmpty(oldSignModuleProps)) {
				oldAllModuleProps.addAll(oldSignModuleProps);
			}
			String[] values = moduleProp.getValues();
			if (values == null) {
				continue;
			}
			for (String value : values) {
				PropDO tdo = convertToDO(moduleProp);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
		}

		try {
			propDAO.updatePropDOs(tdos);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateModules error!");
		}

		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldAllModuleProps, props, EventEnum.MODULE_PROPERTY_UPDATE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "updateProps for ModuleProp interceptor error!");
		}

		// 更新缓存
		for (ModuleProp moduleProp : props) {
			propCache.update(moduleProp.getInstanceId(), PROP_TYPE, moduleProp);
		}

		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("updateProps for ModuleProp end");
		}
	}

	public void removePropByInstance(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for ModuleProp start  instanceId=" + instanceId);
		}
		List<ModuleProp> props = getPropByInstanceId(instanceId);
		if (props != null) {
			// 创建添加实例事件
			final InstancelibEvent instancelibEvent = new InstancelibEvent(props, null, EventEnum.MODULE_PROPERTY_DELETE_EVENT);
			// 前置拦截
			try {
				interceptorNotification(instancelibEvent);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "removePropByInstance for ModuleProp interceptor error!");
			}
			/*
			 * 从数据库中移除数据
			 */
			try {
				propDAO.removePropDOByInstanceAndType(instanceId, PROP_TYPE.toString());
				List<PropDO> propDOs = propDAO.getPropDOsByInstance(instanceId, PROP_TYPE.toString());
				if (propDOs == null || propDOs.isEmpty()) {
					propTypeDAO.removePropTypeDOByInstanceAndType(instanceId, PROP_TYPE.toString());
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "removePropDOByInstanceAndType error!");
			}
			// 从缓存移除
			propCache.remove(instanceId);
			// 后置通知
			listenerNotification(instancelibEvent);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for ModuleProp end");
		}

	}

	@Override
	public ModuleProp getPropByInstanceAndKey(long instanceId, String key) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for ModuleProp  start key= " + key + " instanceId=" + instanceId);
		}

		ModuleProp moduleProp = null;
		/*
		 * 从缓存中获取
		 */
		if (key != null) {
			/*
			 * 从缓存中加载该资源实例的模型属性（没有从数据库中加载）
			 */
			InstanceProp instanceProp = propCache.get(instanceId, key, PROP_TYPE);
			if (instanceProp == null) {
				List<PropDO> tdos = null;
				try {
					tdos = propDAO.getPropDOByInstanceAndKey(instanceId, key, PROP_TYPE.toString());
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("", e);
					}
				}
				if (tdos != null && !tdos.isEmpty()) {
					List<ModuleProp> moduleProps = convertToDef(tdos);
					moduleProp = moduleProps.get(0);
					propCache.add(instanceId, PROP_TYPE, moduleProp);
				}
			} else {
				moduleProp = (ModuleProp) instanceProp;
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for ModuleProp end");
		}
		return moduleProp;
	}

	public List<ModuleProp> getPropByInstanceAndKeys(long instanceId, List<String> keys) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for ModuleProp  start key= " + keys + " instanceId=" + instanceId);
		}
		List<ModuleProp> resultModuleProps = null;
		List<PropDO> tdos = propDAO.getPropDOByInstanceAndKeys(instanceId, keys, PROP_TYPE.toString());
		if (tdos != null && !tdos.isEmpty()) {
			resultModuleProps = new ArrayList<ModuleProp>();
			List<ModuleProp> moduleProps = convertToDef(tdos);
			for (ModuleProp cacheProp : moduleProps) {
				resultModuleProps.add(cacheProp);
				propCache.add(instanceId, PROP_TYPE, cacheProp);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for ModuleProp  start key= " + keys + " instanceId=" + instanceId);
		}
		return resultModuleProps;
	}

	@Override
	public List<ModuleProp> getPropByInstanceId(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for ModuleProp start instanceId=" + instanceId);
		}
		List<ModuleProp> resultList = null;
		List<PropDO> defs = null;
		try {
			defs = propDAO.getPropDOsByInstance(instanceId, PropTypeEnum.MODULE.toString());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if (defs != null && defs.size() > 0) {
			resultList = convertToDef(defs);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for ModuleProp end");
		}
		return resultList;
	}

	/**
	 * 前置拦截（数据入库之前拦截）
	 * 
	 * @param instancelibEvent
	 *            事件
	 * @throws Exception
	 */
	private void listenerNotification(final InstancelibEvent instancelibEvent) throws InstancelibException {
		// 后置监听通知
		try {
			instancelibEventManager.doListen(instancelibEvent);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 后置通知（数据入库之后）
	 * 
	 * @param instancelibEvent
	 *            事件
	 * @throws Exception
	 */
	private void interceptorNotification(final InstancelibEvent instancelibEvent) throws Exception {
		// 前置拦截
		instancelibEventManager.doInterceptor(instancelibEvent);
	}
}
