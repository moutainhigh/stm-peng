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

import com.mainsteam.stm.instancelib.CompositePropService;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.CompositeProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.CompositePropExtendService;

/**
 * 模型配置属性业务类
 * 
 * @author xiaoruqiang
 */
public class CompositePropServiceImpl implements CompositePropService,CompositePropExtendService {

	private static final Log logger = LogFactory
			.getLog(CompositePropService.class);
	
	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.COMPOSITE;
	
	private PropDAO propDAO;

	private PropTypeDAO propTypeDAO;
	
	public void setPropTypeDAO(PropTypeDAO propTypeDAO) {
		this.propTypeDAO = propTypeDAO;
	}

	public void setPropDAO(PropDAO propDAO) {
		this.propDAO = propDAO;
	}

	/**
	 * 
	 */
	public CompositePropServiceImpl() {
	}

	private PropDO convertToDO(InstanceProp prop) {
		PropDO tdo = new PropDO();
		tdo.setInstanceId(prop.getInstanceId());
		tdo.setPropKey(prop.getKey());
		tdo.setPropType(PROP_TYPE.toString());
		return tdo;
	}

	public List<PropDO> convertToDOs(InstanceProp prop) {
		String[] values = prop.getValues();
		List<PropDO> tdos = null;
		if(values != null){
			tdos = new ArrayList<PropDO>();
			for (String value : values) {
				PropDO tdo = new PropDO();
				tdo.setInstanceId(prop.getInstanceId());
				tdo.setPropKey(prop.getKey());
				tdo.setPropType(PROP_TYPE.toString());
				tdo.setPropValue(value);
				tdos.add(tdo);
			}
		}
		return tdos;
	}

	
	private List<CompositeProp> convertToDef(List<PropDO> tdos) {
		List<CompositeProp> defs = new ArrayList<>(tdos.size());
		Map<String, CompositeProp> map = new HashMap<String, CompositeProp>();

		for (PropDO tdo : tdos) {
			if (map.containsKey(tdo.getPropKey())) {
				CompositeProp prop = map.get(tdo.getPropKey());
				String[] values = prop.getValues();
				String[] newValues = new String[values.length + 1];
				System.arraycopy(values, 0, newValues, 0, values.length);
				newValues[newValues.length-1] = tdo.getPropValue();
				prop.setValues(newValues);
				map.put(tdo.getPropKey(), prop);
			} else {
				String[] values = {tdo.getPropValue()};
				CompositeProp def = new CompositeProp();
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
	private void validate(PropDO tdo)  {
		
		if (StringUtils.isEmpty(tdo.getPropKey())) {
			throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,"Module property key is null or empty");
		}
		if (StringUtils.isEmpty(tdo.getPropType())) {
			throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,"Module property type is null or empty");
		}
	}

	
	public void addProp(CompositeProp prop) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for CompositeProp start key= " + prop.getKey()
					+ " instanceId=" + prop.getInstanceId());
		}
//		//创建添加实例事件
//		final InstancelibEvent instancelibEvent = new InstancelibEvent(prop, null, EventEnum.MODULE_PROPERTY_ADD_EVENT);
//		//前置拦截
//		interceptorNotification(instancelibEvent);
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 根据一个value值存放一条数据
		String[] values = prop.getValues();
		if(values != null){
			for (String value : values) {
				PropDO tdo = convertToDO(prop);
				tdo.setPropValue(value);
				validate(tdo);
				tdos.add(tdo);
			}
		}
		// 存放属性值
		propDAO.insertPropDOs(tdos);
		// 存放属性类型值
		propTypeDAO.insertPropTypeDO(convertToDO(prop));
		
		//放入到缓存
		//addToCache(prop);
		//后置通知
		//listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("addProp for CompositeProp end");
		}
	}

	@Override
	public void updateProp(CompositeProp prop) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for CompositeProp start key= "
					+ prop.getKey() + " instanceId=" + prop.getInstanceId());
		}
		//CompositeProp cacheProp = getPropByInstanceAndKey(instanceId, prop.getKey());
		
		//创建添加实例事件
		//final InstancelibEvent instancelibEvent = new InstancelibEvent(cacheProp, prop, EventEnum.MODULE_PROPERTY_UPDATE_EVENT);
		//前置拦截
		//interceptorNotification(instancelibEvent);
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 根据一个value值存放一条数据
		String[] values = prop.getValues();
		if(values != null){
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
			//更新缓存
			//updateToCache(prop);
		}
		//后置通知
		//listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProp for CompositeProp end");
		}
	}

	public void removePropByInstanceAndKey(long instanceId, String key) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for CompositeProp start instanceId="
					+ instanceId);
		}
		//CompositeProp prop = getPropByInstanceAndKey(instanceId, key);
		
		//创建添加实例事件
		//final InstancelibEvent instancelibEvent = new InstancelibEvent(prop, null, EventEnum.MODULE_PROPERTY_DELETE_EVENT);
		//前置拦截
		//interceptorNotification(instancelibEvent);
		if ( StringUtils.isNotEmpty(key)) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//从缓存中移除删除的属性值
			//deleteFromCache(prop);
		}

		//后置通知
		//listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstanceAndKey for CompositeProp end");
		}
		
	}

	
	public void addProps(List<CompositeProp> props) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for CompositeProp start");
		}
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		// 保存属性类型值
		List<PropDO> typeDos = new ArrayList<PropDO>();
		if(CollectionUtils.isNotEmpty(props)){
			for (CompositeProp CompositeProp : props) {
				String[] values = CompositeProp.getValues();
				for (String value : values) {
					PropDO tdo = convertToDO(CompositeProp);
					tdo.setPropValue(value);
					validate(tdo);
					tdos.add(tdo);
				}
				typeDos.add(convertToDO(CompositeProp));
			}
		}
		
		//创建添加实例事件
		//final InstancelibEvent instancelibEvent = new InstancelibEvent(props, null, EventEnum.MODULE_PROPERTY_ADD_EVENT);
		//前置拦截
		//interceptorNotification(instancelibEvent);
		if(CollectionUtils.isNotEmpty(tdos)){
		// 存放属性值
			propDAO.insertPropDOs(tdos);
			// 存放属性类型值
			propTypeDAO.insertPropTypeDOs(typeDos);
		}
		//从缓存中获取数据-将新模型属性添加到缓存
		//addToCache(props);
	
		//后置通知
		//listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("addProps for CompositeProp end");
		}
	}

	@Override
	public void updateProps(List<CompositeProp> props) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProps for CompositeProp start");
		}
	
		//List<CompositeProp> oldmCompositeProps = getPropByInstanceId(instanceId);
		
		// 保存属性参数值
		List<PropDO> tdos = new ArrayList<PropDO>();
		if(CollectionUtils.isNotEmpty(props)){
			for (CompositeProp CompositeProp : props) {
				// 根据一个value值存放一条数据
				String[] values = CompositeProp.getValues();
				for (String value : values) {
					PropDO tdo = convertToDO(CompositeProp);
					tdo.setPropValue(value);
					validate(tdo);
					tdos.add(tdo);
				}
			}
		}
		
		//创建添加实例事件
		//final InstancelibEvent instancelibEvent = new InstancelibEvent(oldmCompositeProps, props, EventEnum.MODULE_PROPERTY_UPDATE_EVENT);
		//前置拦截
		//interceptorNotification(instancelibEvent);
		if(!tdos.isEmpty())
			try {
				propDAO.updatePropDOs(tdos);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//更新缓存
		//updateToCache(props);
		
		//后置通知
		//listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProps for CompositeProp end");
		}
	}

	public void removePropByInstance(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for CompositeProp start  instanceId="
					+ instanceId);
		}
		//List<CompositeProp> props = getPropByInstanceId(instanceId);
		//创建添加实例事件
		//final InstancelibEvent instancelibEvent = new InstancelibEvent(props, null, EventEnum.MODULE_PROPERTY_DELETE_EVENT);
		//前置拦截
		//interceptorNotification(instancelibEvent);
		
		/*
		 * 从数据库中移除数据
		 */
		try {
			propDAO.removePropDOByInstanceAndType(instanceId,
						PROP_TYPE.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		propTypeDAO.getPropTypeDOsByInstance(instanceId,
					PROP_TYPE.toString());
		
		//从缓存移除
		//deleteFromCache(instanceId);
		
		//后置通知
		//listenerNotification(instancelibEvent);
				
		if (logger.isTraceEnabled()) {
			logger.trace("removePropByInstance for CompositeProp end");
		}
	
	}
	
	@Override
	public CompositeProp getPropByInstanceAndKey(long instanceId,
			String key) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for CompositeProp  start key= "
					+ key + " instanceId=" + instanceId);
		}
		
		/*
		 * 从缓存中加载该资源实例的模型属性（没有从数据库中加载）
		 */
		//List<CompositeProp> instanceIdResult = getPropByInstanceId(instanceId);
		
		List<CompositeProp> result = null;
		CompositeProp compositeProp = null;
		/*
		 * 从缓存中获取
		 */
		//if (key != null && instanceIdResult != null) {
//			for (CompositeProp CompositeProp : instanceIdResult) {
//				if(key.equals(CompositeProp.getKey())){
//					result = CompositeProp;
//					break;
//				}
//			}
			List<PropDO> tdos = null;
			try {
				tdos = propDAO.getPropDOByInstanceAndKey(instanceId,key, PROP_TYPE.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (tdos != null && !tdos.isEmpty()) {
				result = convertToDef(tdos);
				compositeProp = result.get(0);
			}
	//	}
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceAndKey for CompositeProp end");
		}
		return compositeProp;
	}

	@Override
	public List<CompositeProp> getPropByInstanceId(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for CompositeProp start instanceId="
					+ instanceId);
		}
//		@SuppressWarnings("unchecked")
//		List<CompositeProp> resultList = (List<CompositeProp> )propCache.get(instanceId,PROP_TYPE);
		List<CompositeProp> resultList = null;
		//if(resultList == null){
			List<PropDO> defs = null;
			try {
				defs = propDAO.getPropDOsByInstance(instanceId,
							PROP_TYPE.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(CollectionUtils.isNotEmpty(defs)){
				resultList = convertToDef(defs);
				//放入到缓存
				//addToCache(resultList);
			}
			
		//}
		
		if (logger.isTraceEnabled()) {
			logger.trace("getPropByInstanceId for CompositeProp end");
		}
		return resultList;
	}
	
	
	/**
	 * 前置拦截（数据入库之前拦截）
	 * @param instancelibEvent 事件
	 * @throws Exception 
	 */
//	private void listenerNotification(final InstancelibEvent instancelibEvent) throws Exception {
//		threadPoolTaskExecutor.execute(new Runnable() {
//			@Override
//			public void run() {
//				//后置监听通知
//				try {
//					instancelibEventManager.doListen(instancelibEvent);
//				} catch (Exception e) {
//					logger.error(e.getMessage());
//				}
//			}
//		});
//		instancelibEventManager.doListen(instancelibEvent);
//	}
	
	/**
	 * 后置通知（数据入库之后）
	 * @param instancelibEvent 事件
	 * @throws Exception
	 */
//	private void interceptorNotification(final InstancelibEvent instancelibEvent) throws Exception {
//		//前置拦截
//		instancelibEventManager.doInterceptor(instancelibEvent);
//	}
	
	
}
