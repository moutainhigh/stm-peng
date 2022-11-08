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

import com.mainsteam.stm.instancelib.ExtendPropService;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.obj.ExtendProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.ExatendPropExtendService;
import com.mainsteam.stm.instancelib.util.PropCache;

/**
 * 扩展属性业务类
 * 
 * @author xiaoruqiang
 */
public class ExtendPropServiceImpl implements ExtendPropService,
		ExatendPropExtendService {

	private static final Log logger = LogFactory
			.getLog(ExtendPropServiceImpl.class);

	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.EXTEND;

	private PropDAO propDAO;

	private PropTypeDAO propTypeDAO;

	// private long instanceId;

	private InstancelibEventManager instancelibEventManager;

	private PropCache propCache;

	public void setPropCache(PropCache propCache) {
		this.propCache = propCache;
	}

	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}

	// public void setInstanceId(long instanceId) {
	// this.instanceId = instanceId;
	// }

	public void setPropTypeDAO(PropTypeDAO propTypeDAO) {
		this.propTypeDAO = propTypeDAO;
	}

	public void setPropDAO(PropDAO propDAO) {
		this.propDAO = propDAO;
	}

	/**
	 * 
	 */
	public ExtendPropServiceImpl() {
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

	private List<ExtendProp> convertToDef(List<PropDO> tdos) {
		List<ExtendProp> defs = new ArrayList<>(tdos.size());
		Map<String, ExtendProp> map = new HashMap<String, ExtendProp>();

		for (PropDO tdo : tdos) {
			if (map.containsKey(tdo.getPropKey())) {
				ExtendProp prop = map.get(tdo.getPropKey());
				String[] values = prop.getValues();
				String[] newValues = new String[values.length + 1];
				System.arraycopy(values, 0, newValues, 0, values.length);
				newValues[newValues.length - 1] = tdo.getPropValue();
				prop.setValues(newValues);
				map.put(tdo.getPropKey(), prop);
			} else {
				String[] values = { tdo.getPropValue() };
				ExtendProp def = new ExtendProp();
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
					"Extend property key is null or empty");
		}
		if (StringUtils.isEmpty(tdo.getPropType())) {
			throw new InstancelibRuntimeException(
					InstancelibRuntimeException.CODE_ERROR_VALIDATE,
					"Extend property type is null or empty");
		}
	}

	@Override
	public void addProp(final ExtendProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for extendProp start key= " + prop.getKey()
					+ " instanceId=" + prop.getInstanceId());
		}
		String[] values = prop.getValues();
		if (values == null) {
			return;
		}
		long instanceId = prop.getInstanceId();
		// if(prop.getInstanceId() == 0){
		// prop.setInstanceId(instanceId);
		// }
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop,
				null, EventEnum.EXTEND_PROPERTY_ADD_EVENT);
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

		// 存放属性值
		try {
			propDAO.insertPropDOs(tdos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 存放属性类型值
		propTypeDAO.insertPropTypeDO(convertToDO(prop));
		// 缓存
		propCache.add(instanceId, PROP_TYPE, prop);
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("addProp for extendProp end");
		}
	}

	public void updateProp(final ExtendProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for ExtendProp start key= "
					+ prop.getKey() + " instanceId=" + prop.getInstanceId());
		}
		// 根据一个value值存放一条数据
		String[] values = prop.getValues();
		if (values == null) {
			return;
		}
		long instanceId = prop.getInstanceId();
		ExtendProp cacheExtendProp = getPropByInstanceAndKey(
				prop.getInstanceId(), prop.getKey());
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
				cacheExtendProp, prop, EventEnum.EXTEND_PROPERTY_UPDATE_EVENT);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 缓存
		propCache.update(instanceId, PROP_TYPE, prop);
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for ExtendProp end");
		}
	}

	@Override
	public void removePropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for ModuleProp start instanceId="
					+ instanceId);
		}
		ExtendProp prop = null;
		try {
			prop = getPropByInstanceAndKey(instanceId, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop,
				null, EventEnum.EXTEND_PROPERTY_DELETE_EVENT);
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
			logger.trace("removePropByInstanceAndKey for ModuleProp end");
		}
	}

	@Override
	public void addProps(List<ExtendProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for ExtendProp start");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 保存属性类型值
		List<PropDO> typeDos = new ArrayList<PropDO>();
		for (ExtendProp extendProp : props) {
			String[] values = extendProp.getValues();
			for (String value : values) {
				PropDO tdo = convertToDO(extendProp);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
			typeDos.add(convertToDO(extendProp));
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(props,
				null, EventEnum.EXTEND_PROPERTY_ADD_EVENT);
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
		// for (ExtendProp extendProp : props) {
		// propCache.add(extendProp.getInstanceId(), PROP_TYPE, extendProp);
		// }
		// 后置通知
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for ExtendProp end");
		}
	}

	public void updateProps(List<ExtendProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProps for ExtendProp start");
		}
		long instanceId = props.get(0).getInstanceId();
		List<ExtendProp> cacheExtendProps = null;
		try {
			cacheExtendProps = getPropByInstanceId(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		for (ExtendProp extendProp : props) {
			if (extendProp.getInstanceId() == 0) {
				extendProp.setInstanceId(instanceId);
			}
			// 根据一个value值存放一条数据
			String[] values = extendProp.getValues();
			if (values == null) {
				continue;
			}
			for (String value : values) {
				PropDO tdo = convertToDO(extendProp);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
		}
		if (cacheExtendProps == null) {
			cacheExtendProps = new ArrayList<>();
		}
		// 创建添加实例事件
		final InstancelibEvent instancelibEvent = new InstancelibEvent(
				cacheExtendProps, props, EventEnum.EXTEND_PROPERTY_UPDATE_EVENT);
		// 前置拦截
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			propDAO.updatePropDOs(tdos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 缓存
		for (ExtendProp extendProp : props) {
			propCache.update(extendProp.getInstanceId(), PROP_TYPE, extendProp);
		}
		// 后置通知
		listenerNotification(instancelibEvent);

		if (logger.isTraceEnabled()) {
			logger.trace("updateProps for ExtendProp end");
		}
	}

	@Override
	public void removePropByInstance(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for ExtendProp start  instanceId="
					+ instanceId);
		}
		List<ExtendProp> props = null;
		try {
			props = getPropByInstanceId(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (props != null) {
			// 创建添加实例事件
			final InstancelibEvent instancelibEvent = new InstancelibEvent(
					props, null, EventEnum.EXTEND_PROPERTY_DELETE_EVENT);
			// 前置拦截
			try {
				interceptorNotification(instancelibEvent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				propDAO.removePropDOByInstanceAndType(instanceId, PROP_TYPE.toString());
				propTypeDAO.getPropTypeDOsByInstance(instanceId,
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
			logger.trace("removePropByInstance for ExtendProp end");
		}

	}

	@Override
	public ExtendProp getPropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for ExtendProp  start key= "
					+ key + " instanceId=" + instanceId);
		}
		ExtendProp result = null;
		if (key != null) {
			InstanceProp prop = propCache.get(instanceId, key, PROP_TYPE);
			if (prop == null) {
				List<PropDO> tdos = null;
				try {
					tdos = propDAO.getPropDOByInstanceAndKey(instanceId, key,
							PROP_TYPE.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (tdos != null && !tdos.isEmpty()) {
					List<ExtendProp> results = convertToDef(tdos);
					result = results.get(0);
					propCache.add(result.getInstanceId(), PROP_TYPE, result);
				}
			} else {
				result = (ExtendProp) prop;
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for ExtendProp end");
		}
		return result;
	}

	@Override
	public List<ExtendProp> getPropByInstanceId(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for ExtendProp start instanceId="
					+ instanceId);
		}
		List<ExtendProp> resultMap = null;
		List<PropDO> defs = null;
		try {
			defs = propDAO.getPropDOsByInstance(instanceId,
					PropTypeEnum.EXTEND.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (defs != null && defs.size() > 0) {
			resultMap = convertToDef(defs);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for ExtendProp end");
		}
		return resultMap;
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
}
