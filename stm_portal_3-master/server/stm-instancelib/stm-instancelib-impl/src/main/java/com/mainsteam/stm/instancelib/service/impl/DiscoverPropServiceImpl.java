/**
 * 
 */
package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.DiscoverPropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * 发现输入属性业务类
 * 
 * @author xiaoruqiang
 */
public class DiscoverPropServiceImpl implements DiscoverPropService,
		DiscoverPropExtendService {

	private static final Log logger = LogFactory
			.getLog(DiscoverPropService.class);

	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.DISCOVER;

	private PropDAO propDAO;

	private PropTypeDAO propTypeDAO;

	private InstancelibEventManager instancelibEventManager;

	private PropCache propCache;

	public void setPropCache(PropCache propCache) {
		this.propCache = propCache;
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
	public DiscoverPropServiceImpl() {
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
		List<PropDO> tdos = null;
		if (values != null) {
			tdos = new ArrayList<PropDO>();
			for (String value : values) {
				PropDO tdo = new PropDO();
				tdo.setInstanceId(prop.getInstanceId());
				tdo.setPropKey(prop.getKey());
				tdo.setPropType(PROP_TYPE.toString());
				if(value != null){
					tdo.setPropValue(value.trim());	
				}
				tdos.add(tdo);
			}
		}
		return tdos;
	}

	public List<DiscoverProp> convertToDef(List<PropDO> tdos) {
		List<DiscoverProp> defs = new ArrayList<>(tdos.size());
		Map<String, DiscoverProp> map = new HashMap<String, DiscoverProp>();

		for (PropDO tdo : tdos) {
			String value = tdo.getPropValue();
			String[] values = new String[]{tdo.getPropValue()};
			if(value != null){
				values = new String[]{value};
			}
			
			if (map.containsKey(tdo.getPropKey())) {
				DiscoverProp prop = map.get(tdo.getPropKey());
				String[] newValues = new String[values.length + 1];
				System.arraycopy(values, 0, newValues, 0, values.length);
				newValues[newValues.length - 1] = tdo.getPropValue();
				prop.setValues(newValues);
				map.put(tdo.getPropKey(), prop);
			} else {
				DiscoverProp def = new DiscoverProp();
				def.setInstanceId(tdo.getInstanceId());
				def.setKey(tdo.getPropKey());
				def.setValues(values);
				defs.add(def);
				map.put(tdo.getPropKey(), def);
			}
		}
		return defs;
	}

	/**
	 * 验证输入参数
	 * 
	 * @param tdo
	 */
	private void validate(PropDO tdo) {
		if (StringUtils.isEmpty(tdo.getPropKey())) {
			throw new InstancelibRuntimeException(
					InstancelibRuntimeException.CODE_ERROR_VALIDATE,
					"Discover property key is null or empty");
		}
		if (StringUtils.isEmpty(tdo.getPropType())) {
			throw new InstancelibRuntimeException(
					InstancelibRuntimeException.CODE_ERROR_VALIDATE,
					"Discover property type is null or empty");
		}

	}

	public void addProp(final DiscoverProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for DiscoverProp start key= " + prop.getKey()
					+ " instanceId=" + prop.getInstanceId());
		}
		long instanceId = prop.getInstanceId();
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop,
				null, EventEnum.DISCOVER_PROPERTY_ADD_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"addProp for DiscoverProp interceptor error!");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 根据一个value值存放一条数据
		String[] values = prop.getValues();
		if (values != null) {
			for (String value : values) {
				PropDO tdo = convertToDO(prop);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
		}
		// 存放属性值
		try {
			propDAO.insertPropDOs(tdos);
			// 存放属性类型值
			propTypeDAO.insertPropTypeDO(convertToDO(prop));
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"addProp for discoverProp error!");
		}
		// 更新缓存
		propCache.add(instanceId, PROP_TYPE, prop);
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("addProp for DiscoverProp end");
		}
	}

	@Override
	public void updateProp(final DiscoverProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("updateProp for DiscoverProp start key= ");
			b.append(prop.getKey());
			b.append(" instanceId=");
			b.append(prop.getInstanceId());
			logger.trace(b);
		}
		String[] values = prop.getValues();
		if (values == null) {
			return;
		}
		long instanceId = prop.getInstanceId();
		DiscoverProp cacheDiscoverProp = null;
		try {
			cacheDiscoverProp = getPropByInstanceAndKey(instanceId,
					prop.getKey());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"addProp for discoverProp error!");
		}
		if(cacheDiscoverProp == null){
			StringBuilder b = new StringBuilder(20);
			b.append(" instanceId=").append(prop.getInstanceId());
			b.append("discoverProp key=").append(prop.getKey()).append(" not exist.");
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_PARAMETER_ERROR,b.toString());
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
				cacheDiscoverProp, prop,
				EventEnum.DISCOVER_PROPERTY_UPDATE_EVENT);

		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 根据一个value值存放一条数据
		for (int i = 0; i < values.length ; i++) {
			String value = values[i];
			PropDO tdo = convertToDO(prop);
			tdo.setPropValue(value);
			validate(tdo);
			tdos.add(tdo);
		}
		try {
			propDAO.updatePropDOs(tdos);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateProp for discoverProp error!");
		}
		// 更新缓存
		propCache.update(prop.getInstanceId(), PROP_TYPE, prop);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"updateProps for discoverProp interceptor error!");
		}
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for DiscoverProp end");
		}
	}

	public void removePropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for DiscoverProp start key= "
					+ key + " instanceId=" + instanceId);
		}

		DiscoverProp prop = getPropByInstanceAndKey(instanceId, key);
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop,
				null, EventEnum.DISCOVER_PROPERTY_DELETE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removePropByInstanceAndKey for discoverProp interceptor error!");
		}
		if (StringUtils.isNotEmpty(key)) {
			try {
				propDAO.removePropDOByInstanceAndKey(instanceId, key,
						PROP_TYPE.toString());
				List<PropDO> PropDOs = propDAO.getPropDOsByInstance(instanceId,
						PROP_TYPE.toString());
				if (PropDOs == null || PropDOs.isEmpty()) {
					propTypeDAO.removePropTypeDOByInstanceAndKey(instanceId, key,
							PROP_TYPE.toString());
				}
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
				throw new InstancelibException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"removePropByInstanceAndKey for discoverProp error!");
			}
			// 更新缓存
			propCache.remove(instanceId, key, PROP_TYPE);
		}
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for DiscoverProp end");
		}

	}

	public void addProps(List<DiscoverProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for DiscoverProp start");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 保存属性类型值
		List<PropDO> typeDos = new ArrayList<PropDO>();
		for (DiscoverProp discoverProp : props) {
			String[] values = discoverProp.getValues();
			for (String value : values) {
				PropDO tdo = convertToDO(discoverProp);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
			typeDos.add(convertToDO(discoverProp));
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(props,
				null, EventEnum.DISCOVER_PROPERTY_ADD_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"addProp for DiscoverProp interceptor error!");
		}
		// 存放属性值
		try {
			propDAO.insertPropDOs(tdos);
			// 存放属性类型值
			propTypeDAO.insertPropTypeDOs(typeDos);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new InstancelibException(
					ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"addProp for discoverProp error!");
		}
		// for (DiscoverProp discoverProp : props) {
		// //更新缓存
		// propCache.add(discoverProp.getInstanceId(), PROP_TYPE, discoverProp);
		// }
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("addProps for DiscoverProp end");
		}
	}

	@Override
	public void updateProps(List<DiscoverProp> props)
			throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("updateProps for DiscoverProp start =" + props);
		}
		if (CollectionUtils.isEmpty(props)) {
			return;
		}
		List<DiscoverProp> oldAllDiscoverProps = new ArrayList<>();
		List<PropDO> tdos = new ArrayList<PropDO>();
		for (DiscoverProp discoverProp : props) {
			long instanceId = discoverProp.getInstanceId();
			List<DiscoverProp> oldSignDiscoverProps = getPropByInstanceId(instanceId);
			if (CollectionUtils.isNotEmpty(oldSignDiscoverProps)) {
				oldAllDiscoverProps.addAll(oldSignDiscoverProps);
			}
			String[] values = discoverProp.getValues();
			if (values == null) {
				continue;
			}
			for (String value : values) {
				PropDO tdo = convertToDO(discoverProp);
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
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateDisvoverProps error!");
		}

		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldAllDiscoverProps, props, EventEnum.DISCOVER_PROPERTY_UPDATE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR, "updateProps for DiscoverProp interceptor error!");
		}

		// 更新缓存
		for (DiscoverProp discoverProp : props) {
			propCache.update(discoverProp.getInstanceId(), PROP_TYPE, discoverProp);
		}
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("updateProps for DiscoverProp end");
		}
	}

	public void removePropByInstance(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for DiscoverProp start  instanceId="
					+ instanceId);
		}
		List<DiscoverProp> props = getPropByInstanceId(instanceId);
		if (props != null) {
			// 创建删除实例事件
			final InstancelibEvent instancelibEvent = new InstancelibEvent(
					props, null, EventEnum.DISCOVER_PROPERTY_DELETE_EVENT);
			// 前置拦截
			try {
				interceptorNotification(instancelibEvent);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
				throw new InstancelibException(
						ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
						"removePropByInstance for DiscoverProp interceptor error!");
			}

			try {
				propDAO.removePropDOByInstanceAndType(instanceId, PROP_TYPE.toString());
				List<PropDO> propDOs = propTypeDAO.getPropTypeDOsByInstance(instanceId,
						PROP_TYPE.toString());
				if (propDOs == null || propDOs.isEmpty()) {
					propTypeDAO.removePropTypeDOByInstanceAndType(instanceId,
							PROP_TYPE.toString());
				}
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
				throw new InstancelibException(
						ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"removePropByInstance for discoverProp error!");
			}

			// 更新缓存
			propCache.remove(instanceId);
			// 后置通知
			listenerNotification(instancelibEvent);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for DiscoverProp end");
		}

	}

	@Override
	public List<DiscoverProp> getPropByInstanceId(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for DiscoverProp start  instanceId="
					+ instanceId);
		}
		List<DiscoverProp> resultMap = null;
		List<PropDO> defs = null;
		try {
			defs = propDAO.getPropDOsByInstance(instanceId,
					PropTypeEnum.DISCOVER.toString());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if (defs != null && defs.size() > 0) {
			resultMap = convertToDef(defs);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for DiscoverProp end");
		}
		return resultMap;
	}

	@Override
	public DiscoverProp getPropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for DiscoverProp  start key= "
					+ key + " instanceId=" + instanceId);
		}
		DiscoverProp result = null;
		if (key != null) {
			InstanceProp prop = propCache.get(instanceId, key, PROP_TYPE);
			if (prop == null) {
				List<PropDO> tdos = null;
				try {
					tdos = propDAO.getPropDOByInstanceAndKey(instanceId, key,
							PROP_TYPE.toString());
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("", e);
					}
				}
				if (tdos != null && !tdos.isEmpty()) {
					List<DiscoverProp> results = convertToDef(tdos);
					result = results.get(0);
					propCache.add(result.getInstanceId(), PROP_TYPE, result);
				}
			} else {
				result = (DiscoverProp) prop;
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for DiscoverProp end");
		}
		return result;
	}

	public List<DiscoverProp> getPropByInstanceAndKeys(long instanceId,
			List<String> keys) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for DiscoverProp  start key= "
					+ keys + " instanceId=" + instanceId);
		}
		List<DiscoverProp> resultDiscoverProps = null;
		List<PropDO> tdos = null;
		try {
			tdos = propDAO.getPropDOByInstanceAndKeys(instanceId,
					keys, PROP_TYPE.toString());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if (tdos != null && !tdos.isEmpty()) {
			resultDiscoverProps = new ArrayList<DiscoverProp>();
			List<DiscoverProp> discoverProps = convertToDef(tdos);
			for (DiscoverProp cacheProp : discoverProps) {
				resultDiscoverProps.add(cacheProp);
				propCache.add(instanceId, PROP_TYPE, cacheProp);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for DiscoverProp  start key= "
					+ keys + " instanceId=" + instanceId);
		}
		return resultDiscoverProps;
	}

	/**
	 * 后置通知（数据入库之后）
	 * 
	 * @param instancelibEvent
	 *            事件
	 * @throws Exception
	 */
	private void listenerNotification(final InstancelibEvent instancelibEvent) {
		// 后置监听通知
		try {
			instancelibEventManager.doListen(instancelibEvent);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
	}

	/**
	 * 前置拦截（数据入库之前拦截）
	 * 
	 * @param instancelibEvent
	 *            事件
	 * @throws Exception
	 */
	private void interceptorNotification(final InstancelibEvent instancelibEvent)
			throws Exception {
		// 前置拦截
		instancelibEventManager.doInterceptor(instancelibEvent);
	}

	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}
}
