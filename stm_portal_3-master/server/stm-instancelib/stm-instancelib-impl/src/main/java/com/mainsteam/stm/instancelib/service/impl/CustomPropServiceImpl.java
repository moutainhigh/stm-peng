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

import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.CustomPropExtendService;
import com.mainsteam.stm.instancelib.service.PropService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * 自定义属性业务类
 * 
 * @author xiaoruqiang
 */
public class CustomPropServiceImpl implements CustomPropService,
		CustomPropExtendService, PropService {

	private static final Log logger = LogFactory
			.getLog(CustomPropService.class);

	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.CUSTOM;

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
	public CustomPropServiceImpl() {
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

	private List<CustomProp> convertToDef(List<PropDO> tdos) {
		List<CustomProp> defs = new ArrayList<>(tdos.size());
		Map<String, CustomProp> map = new HashMap<String, CustomProp>();

		for (PropDO tdo : tdos) {
			if (map.containsKey(tdo.getPropKey())) {
				CustomProp prop = map.get(tdo.getPropKey());
				String[] values = prop.getValues();
				String[] newValues = new String[values.length + 1];
				System.arraycopy(values, 0, newValues, 0, values.length);
				newValues[newValues.length - 1] = tdo.getPropValue();
				prop.setValues(newValues);
				map.put(tdo.getPropKey(), prop);
			} else {
				String[] values = { tdo.getPropValue() };
				CustomProp def = new CustomProp();
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
	 *            验证参数
	 */
	private void validate(PropDO tdo) {

		if (StringUtils.isEmpty(tdo.getPropKey())) {
			throw new InstancelibRuntimeException(
					InstancelibRuntimeException.CODE_ERROR_VALIDATE,
					"Custom property key is null or empty");
		}
		if (StringUtils.isEmpty(tdo.getPropType())) {
			throw new InstancelibRuntimeException(
					InstancelibRuntimeException.CODE_ERROR_VALIDATE,
					"Custom property type is null or empty");
		}

	}

	@Override
	public void addProp(final CustomProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for CustomProp start key= " + prop.getKey()
					+ " instanceId=" + prop.getInstanceId());
		}

		long instanceId = prop.getInstanceId();
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop,
				null, EventEnum.CUSTOM_PROPERTY_ADD_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// 存放属性值
			try {
				propDAO.insertPropDOs(tdos);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 存放属性类型值
		propTypeDAO.insertPropTypeDO(convertToDO(prop));
		// 缓存
		propCache.add(instanceId, PROP_TYPE, prop);
		// 后置通知

		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("addProp for CustomProp end");
		}
	}

	public void updateProp(final CustomProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for CustomProp start key= "
					+ prop.getKey() + " instanceId=" + prop.getInstanceId());
		}
		String[] values = prop.getValues();
		if (values == null) {
			return;
		}
		long instanceId = prop.getInstanceId();
		CustomProp cacheProp = getPropByInstanceAndKey(instanceId,
				prop.getKey());
		// 创建实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
				cacheProp, prop, EventEnum.CUSTOM_PROPERTY_UPDATE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 根据一个value值存放一条数据
		for (String value : values) {
			PropDO tdo = convertToDO(prop);
			tdo.setPropValue(value);
			validate(tdo);
			tdos.add(tdo);
		}
		try {
			propDAO.updatePropDOs(tdos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		propCache.update(prop.getInstanceId(), PROP_TYPE, prop);
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for CustomProp end");
		}
	}

	@Override
	public void addProps(List<CustomProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for CustomProp start");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 保存属性类型值
		List<PropDO> typeDos = new ArrayList<PropDO>();
		for (CustomProp customProp : props) {
			String[] values = customProp.getValues();
			if (values == null) {
				continue;
			}
			for (String value : values) {
				PropDO tdo = convertToDO(customProp);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
			typeDos.add(convertToDO(customProp));
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(props,
				null, EventEnum.CUSTOM_PROPERTY_ADD_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 存放属性值
		try {
			propDAO.insertPropDOs(tdos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 存放属性类型值
		propTypeDAO.insertPropTypeDOs(typeDos);
		// 缓存
		// for (CustomProp customProp : props) {
		// propCache.add(customProp.getInstanceId(), PROP_TYPE, customProp);
		// }
		// 后置通知

		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("addProps for CustomProp end");
		}
	}

	public void updateProps(List<CustomProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProps for CustomProp start");
		}

		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		for (CustomProp customProp : props) {
			// 根据一个value值存放一条数据
			String[] values = customProp.getValues();
			if (values == null) {
				continue;
			}
			if (values != null) {
				for (String value : values) {
					PropDO tdo = convertToDO(customProp);
					tdo.setPropValue(value);
					validate(tdo);
					tdos.add(tdo);
				}
			}
		}
		try {
			propDAO.updatePropDOs(tdos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(props,
				null, EventEnum.CUSTOM_PROPERTY_UPDATE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (CustomProp customProp : props) {
			// 缓存
			propCache.update(customProp.getInstanceId(), PROP_TYPE, customProp);
		}
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProps for CustomProp end");
		}
	}

	@Override
	public void removePropByInstance(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for CustomProp start  instanceId="
					+ instanceId);
		}
		List<CustomProp> prop = null;
		try {
			prop = (List<CustomProp>) getPropByInstanceId(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (prop != null) {
			// 创建实例事件
			final InstancelibEvent instancelibEvent = new InstancelibEvent(
					prop, null, EventEnum.CUSTOM_PROPERTY_DELETE_EVENT);
			// 前置拦截
			try {
				interceptorNotification(instancelibEvent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				propDAO.removePropDOByInstanceAndType(instanceId, PROP_TYPE.toString());
				propTypeDAO.removePropTypeDOByInstanceAndType(instanceId,
						PROP_TYPE.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 缓存
			propCache.remove(instanceId);
			// 后置通知
			listenerNotification(instancelibEvent);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for CustomProp end");
		}
	}

	@Override
	public void removePropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for CustomProp start key= "
					+ key + " instanceId=" + instanceId);
		}

		CustomProp prop = null;
		try {
			prop = getPropByInstanceAndKey(instanceId, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 创建实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop,
				null, EventEnum.CUSTOM_PROPERTY_DELETE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (StringUtils.isNotEmpty(key)) {
			try {
				propDAO.removePropDOByInstanceAndKey(instanceId, key,
						PROP_TYPE.toString());
				List<PropDO> PropDOs = propDAO.getPropDOsByInstance(instanceId,
						PROP_TYPE.toString());
				if (PropDOs == null || PropDOs.isEmpty()) {
					propTypeDAO.removePropTypeDOByInstanceAndKey(instanceId,
							key, PROP_TYPE.toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 缓存
			propCache.remove(instanceId, key, PROP_TYPE);
		}
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for CustomProp end");
		}
	}

	@Override
	public CustomProp getPropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for CustomProp start key= "
					+ key + " instanceId=" + instanceId);
		}
		CustomProp result = null;
		if (key != null) {
			InstanceProp prop = propCache.get(instanceId, key, PROP_TYPE);
			if (prop == null) {
				List<PropDO> tdos = null;
				try {
					tdos = propDAO.getPropDOByInstanceAndKey(instanceId, key,
							PropTypeEnum.CUSTOM.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (tdos != null && !tdos.isEmpty()) {
					List<CustomProp> results = convertToDef(tdos);
					result = results.get(0);
					propCache.add(instanceId, PROP_TYPE, result);
				}
			} else {
				result = (CustomProp) prop;
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for CustomProp end");
		}
		return result;
	}

	@Override
	public List<CustomProp> getPropByInstanceId(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for CustomProp start instanceId="
					+ instanceId);
		}
		List<CustomProp> resultList = null;
		List<PropDO> defs = null;
		try {
			defs = propDAO.getPropDOsByInstance(instanceId,
					PropTypeEnum.CUSTOM.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (defs != null && defs.size() > 0) {
			resultList = convertToDef(defs);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for CustomProp end");
		}
		return resultList;
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
			logger.error(e.getMessage());
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
