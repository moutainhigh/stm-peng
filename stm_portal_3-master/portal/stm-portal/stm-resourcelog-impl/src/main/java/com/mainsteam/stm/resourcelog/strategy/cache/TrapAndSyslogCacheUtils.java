package com.mainsteam.stm.resourcelog.strategy.cache;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TrapAndSyslogCacheUtils {
	private static final Log logger = LogFactory.getLog(TrapAndSyslogCacheUtils.class);
	private IMemcache<SysLogRuleBo> sysLogRuleBoIMemcache;// 日志策略缓存
	private IMemcache<SyslogResourceBo> syslogResourceBoIMemcache; //策略绑定资源缓存

	public TrapAndSyslogCacheUtils() {
		if (logger.isInfoEnabled()) {
			logger.info("Starts to init trap & syslog caches ...");
		}
		sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
		syslogResourceBoIMemcache = (IMemcache<SyslogResourceBo>) getIMemcache(SyslogResourceBo.class, true);

	}

	private Object getIMemcache(Class objClass, boolean isCreateNew){
		switch (objClass.getSimpleName()){
			case "SysLogRuleBo":
				if(isCreateNew || null == sysLogRuleBoIMemcache){
					sysLogRuleBoIMemcache = MemCacheFactory.getRemoteMemCache(objClass);
				}
				return sysLogRuleBoIMemcache;

			case "SyslogResourceBo":
				if(isCreateNew || null == syslogResourceBoIMemcache){
					syslogResourceBoIMemcache = MemCacheFactory.getRemoteMemCache(objClass);
				}
				return syslogResourceBoIMemcache;

		}
		return null;
	}

	/**
	 * 设置syslog策略缓存
	 * @param obj
	 * @param strategyID
	 * @return
     */
	public boolean setSyslogRuleBo(List<SysLogRuleBo> obj, Long strategyID) {

		List<SysLogRuleBo> sysLogRuleBo = getSyslogRule(strategyID);
		if(sysLogRuleBo !=null) {
			boolean updated = sysLogRuleBoIMemcache.updateCollection(String.valueOf(strategyID), obj, 60*60*24);
			if(!updated) {
				if(!sysLogRuleBoIMemcache.isActivate()){
					sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
				}
				boolean isSuccess = sysLogRuleBoIMemcache.updateCollection(String.valueOf(strategyID), obj, 60*60*24);
				if(!isSuccess){
					if(logger.isWarnEnabled()) {
						logger.warn("failed to update syslog rule cache, try to cache again. " + obj);
					}
				}
				return isSuccess;
			}
			return updated;
		}else {
			boolean flag = sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), obj, 60*60*24);
			if(!flag){
				if(sysLogRuleBoIMemcache.isActivate()) {
					if(logger.isWarnEnabled()) {
						logger.warn("Failed to cached syslog rule " + obj);
					}
				}else {
					sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
					boolean finalFlag = sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), obj, 60*60*24);
					if(!finalFlag) {
						if (logger.isWarnEnabled()) {
							logger.warn("retry to cached syslog rule, but still failed " + obj);
						}
					}
					return finalFlag;
				}
			}
			return flag;
		}
	}

	/**
	 * 设置syslog策略缓存
	 * @param obj
	 * @param strategyID
	 * @return
	 */
	public boolean setSingleSyslogRuleBo(SysLogRuleBo obj, Long strategyID) {

		List<SysLogRuleBo> sysLogRuleBo = getSyslogRule(strategyID);
		if(sysLogRuleBo !=null) {
			Iterator<SysLogRuleBo> iterator = sysLogRuleBo.iterator();
			boolean isUpdated = false;
			while(iterator.hasNext()) {
				SysLogRuleBo sysLogRuleBo1 = iterator.next();
				if(sysLogRuleBo1.getId().longValue() == obj.getId().longValue()) {
					isUpdated = true;
					try {
						BeanUtils.copyProperties(obj, sysLogRuleBo1);
					} catch (BeansException e) {
						logger.error("update syslog rule {"+strategyID+":"+obj.getId()+"}error," + e.getMessage(), e);
						return false;
					}
				}
			}
			if(!isUpdated) {
				sysLogRuleBo.add(obj);
				isUpdated = true;
			}
			if(isUpdated) {
				boolean updated = sysLogRuleBoIMemcache.updateCollection(String.valueOf(strategyID), sysLogRuleBo, 60*60*24);
				if(!updated) {
					if(!sysLogRuleBoIMemcache.isActivate()){
						sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
					}
					boolean isSuccess = sysLogRuleBoIMemcache.updateCollection(String.valueOf(strategyID), sysLogRuleBo, 60*60*24);
					if(!isSuccess){
						if(logger.isWarnEnabled()) {
							logger.warn("failed to update syslog rule cache, try to cache again. " + obj);
						}
					}
					return isSuccess;
				}
				return updated;

			}
			return isUpdated;
		}else {
			List<SysLogRuleBo> sysLogRuleBoList = new ArrayList<>(1);
			sysLogRuleBoList.add(obj);
			boolean flag = sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), sysLogRuleBoList, 60*60*24);
			if(!flag){
				if(sysLogRuleBoIMemcache.isActivate()) {
					if(logger.isWarnEnabled()) {
						logger.warn("Failed to cached syslog rule " + obj);
					}
				}else {
					sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
					boolean finalFlag = sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), sysLogRuleBoList, 60*60*24);
					if(!finalFlag) {
						if (logger.isWarnEnabled()) {
							logger.warn("retry to cached syslog rule, but still failed " + obj);
						}
					}
					return finalFlag;
				}
			}
			return flag;
		}
	}

	/**
	 * 设置syslog策略缓存
	 * @param obj
	 * @param strategyID
	 * @return
	 */
	public boolean updateSingleSyslogRule(SysLogRuleBo obj, Long strategyID) {

		List<SysLogRuleBo> sysLogRuleBo = getSyslogRule(strategyID);
		if(sysLogRuleBo !=null) {
			Iterator<SysLogRuleBo> iterator = sysLogRuleBo.iterator();
			while(iterator.hasNext()) {
				SysLogRuleBo sysLogRuleBo1 = iterator.next();
				if(sysLogRuleBo1.getId().longValue() == obj.getId().longValue()) {
					try {
						Field[] fields = obj.getClass().getDeclaredFields();
						List<String> ignoreFields = new ArrayList<>(5);
						for(Field field : fields) {
							if(StringUtils.equals(field.getType().getName(), "java.lang.String")) {
								ignoreFields.add(field.getName());
							}
						}
						Method[] methods = obj.getClass().getDeclaredMethods();
						for(Method method : methods) {
							if(StringUtils.equals("java.lang.String", method.getReturnType().getName())) {
								Object result = MethodUtils.invokeMethod(obj, method.getName(), null);
								if(null != result) {
									String fieldName = StringUtils.uncapitalize(method.getName().substring(3));
									if(ignoreFields.contains(fieldName)) {
										ignoreFields.remove(fieldName);
									}
								}
							}
						}
						if(ignoreFields.isEmpty())
							BeanUtils.copyProperties(obj, sysLogRuleBo1);
						else
							BeanUtils.copyProperties(obj, sysLogRuleBo1, ignoreFields.toArray(new String[ignoreFields.size()]));

					} catch (BeansException e) {
						logger.error("update syslog rule {"+strategyID+":"+obj.getId()+"}error," + e.getMessage(), e);
						return false;
					} catch (NoSuchMethodException e) {
						logger.error("update syslog rule {"+strategyID+":"+obj.getId()+"}error," + e.getMessage(), e);
						return false;
					} catch (IllegalAccessException e) {
						logger.error("update syslog rule {"+strategyID+":"+obj.getId()+"}error," + e.getMessage(), e);
						return false;
					} catch (InvocationTargetException e) {
						logger.error("update syslog rule {"+strategyID+":"+obj.getId()+"}error," + e.getMessage(), e);
						return false;
					}
				}
			}

			return sysLogRuleBoIMemcache.updateCollection(String.valueOf(strategyID), sysLogRuleBo, 60*60*24);

		}else {
			List<SysLogRuleBo> list = new ArrayList<SysLogRuleBo>(1);
			list.add(obj);
			boolean flag = sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), list, 60*60*24);
			if(!flag){
				if(sysLogRuleBoIMemcache.isActivate()) {
					if(logger.isWarnEnabled()) {
						logger.warn("Failed to cached syslog rule " + obj);
					}
				}else {
					sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
					boolean finalFlag = sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), list, 60*60*24);
					if(!finalFlag) {
						if (logger.isWarnEnabled()) {
							logger.warn("retry to cached syslog rule, but still failed " + obj);
						}
					}
					return finalFlag;
				}
			}
			return flag;
		}
	}

	/**
	 * 设置syslog缓存对象
	 * @param obj
	 * @param resourceIP
	 * @param strategyType
     * @return
     */
	public boolean setSyslogResource(List<SyslogResourceBo> obj, String resourceIP, String strategyType) {

		List<SyslogResourceBo> syslogResourceBoList = getSyslogResource(resourceIP, strategyType);
		if(syslogResourceBoList !=null) {
			boolean updated = syslogResourceBoIMemcache.updateCollection(resourceIP + "__" + strategyType, obj, 60*60*24);
			if(logger.isDebugEnabled()) {
				logger.debug("update syslog resource cache, using {" + resourceIP + ":" + strategyType + "}, result is " + updated);
			}
			if(!updated) {
				if(!syslogResourceBoIMemcache.isActivate()){
					syslogResourceBoIMemcache = (IMemcache<SyslogResourceBo>) getIMemcache(SyslogResourceBo.class, true);
				}
				boolean isSuccess = syslogResourceBoIMemcache.updateCollection(resourceIP + "__" + strategyType, obj, 60*60*24);
				if(!isSuccess){
					if(logger.isWarnEnabled()) {
						logger.warn("failed to update syslog resource cache, try to cache again. " + obj);
					}
				}
				return isSuccess;
			}
			return updated;
		}else {
			boolean flag = syslogResourceBoIMemcache.setCollection(resourceIP + "__" + strategyType, obj, 60*60*24);
			if(logger.isDebugEnabled()) {
				logger.debug("set syslog resource cache, using {" + resourceIP + ":" + strategyType + "}, result is " + flag);
			}
			if(!flag){
				if(syslogResourceBoIMemcache.isActivate()) {
					if(logger.isWarnEnabled()) {
						logger.warn("Failed to cached syslog resource " + obj);
					}
				}else {
					syslogResourceBoIMemcache = (IMemcache<SyslogResourceBo>) getIMemcache(SyslogResourceBo.class, true);
					boolean finalFlag = syslogResourceBoIMemcache.setCollection(resourceIP + "__" + strategyType, obj, 60*60*24);
					if(!finalFlag) {
						if (logger.isWarnEnabled()) {
							logger.warn("retry to cached syslog resource, but still failed " + obj);
						}
					}
					return finalFlag;
				}
			}
			return flag;
		}
	}

	/**
	 * 根据策略ID和资源IP获取资源缓存
	 * @param strategyType
	 * @param resourceIP
	 * @return
	 */
	public List<SyslogResourceBo> getSyslogResource(String resourceIP, String strategyType) {
		if(null == syslogResourceBoIMemcache) {
			syslogResourceBoIMemcache = (IMemcache<SyslogResourceBo>) getIMemcache(SyslogResourceBo.class, true);
		}
		List<SyslogResourceBo> syslogResourceBoList = (List<SyslogResourceBo>) syslogResourceBoIMemcache.getCollection(resourceIP + "__" + strategyType);
		if(logger.isDebugEnabled()) {
			logger.debug("query syslog resource cache, resource ip is " + resourceIP + ", strategyType is " + strategyType);
		}
		if(null == syslogResourceBoList) {
			if(logger.isDebugEnabled()) {
				logger.debug("query syslog resource cache result is empty, resource ip is " + resourceIP + ", strategyType is " + strategyType);
			}
			if(syslogResourceBoIMemcache.isActivate())
				return null;
			else {
				if(logger.isInfoEnabled()) {
					logger.info("retry to obtain syslogResourceBoIMemcache while querying cache,using {" + strategyType +":" + resourceIP + "}");
				}
				syslogResourceBoIMemcache = (IMemcache<SyslogResourceBo>) getIMemcache(SyslogResourceBo.class, true);
			}
			return (List<SyslogResourceBo>) syslogResourceBoIMemcache.getCollection(resourceIP + "__" + strategyType);
		}else {
			return syslogResourceBoList;
		}

	}

	/**
	 * 根据策略ID获取syslog策略
	 * @param strategyID
	 * @return
	 */
	public List<SysLogRuleBo> getSyslogRule(Long strategyID) {
		if(null == sysLogRuleBoIMemcache){
			sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
		}
		List<SysLogRuleBo> sysLogRuleBo = (List<SysLogRuleBo>) sysLogRuleBoIMemcache.getCollection(String.valueOf(strategyID));
		if(null == sysLogRuleBo) {
			if(sysLogRuleBoIMemcache.isActivate())
				return null;
			else {
				if(logger.isInfoEnabled()) {
					logger.info("retry to obtain sysLogRuleBoIMemcache while querying cache,using " + strategyID);
				}
				sysLogRuleBoIMemcache = (IMemcache<SysLogRuleBo>) getIMemcache(SysLogRuleBo.class, true);
			}
			return (List<SysLogRuleBo>) sysLogRuleBoIMemcache.getCollection(String.valueOf(strategyID));
		}else {
			return sysLogRuleBo;
		}

	}

	/**
	 * 移除syslog 策略缓存
	 * @param strategyID
	 * @param ruleIDs
     */
	public void removeSyslogRuleCache(long strategyID, List<Long> ruleIDs) {
		if(sysLogRuleBoIMemcache != null && sysLogRuleBoIMemcache.isActivate()) {
			if(null == ruleIDs) {
				sysLogRuleBoIMemcache.delete(String.valueOf(strategyID));
				sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), new ArrayList<SysLogRuleBo>(), 60*60*24);
			}else {
				List<SysLogRuleBo> sysLogRuleBoList = getSyslogRule(strategyID);
				if(null != sysLogRuleBoList && !sysLogRuleBoList.isEmpty()) {
					Iterator<SysLogRuleBo> iterator = sysLogRuleBoList.iterator();
					while(iterator.hasNext()) {
						SysLogRuleBo sysLogRuleBo = iterator.next();
						if(ruleIDs.contains(sysLogRuleBo.getId())) {
							iterator.remove();
						}
					}
					//缓存删除存在问题
//					if(!sysLogRuleBoList.isEmpty()) {
//						sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), sysLogRuleBoList, 60*60*24);
//					}else {
//						sysLogRuleBoIMemcache.delete(String.valueOf(strategyID));
//					}

					sysLogRuleBoIMemcache.setCollection(String.valueOf(strategyID), sysLogRuleBoList, 60*60*24);
				}
			}
		}else {
			if(logger.isWarnEnabled())
				logger.warn("sysLogRuleBoIMemcache is invalid while remove cache using " + strategyID);
		}
	}

	/**
	 * 移除syslog绑定资源缓存
	 * @param strategyType
	 * @param resourceIP
	 */
	public void removeSyslogResource(String resourceIP, String strategyType, String resourceID) {
		if(syslogResourceBoIMemcache != null && syslogResourceBoIMemcache.isActivate()) {
			if(StringUtils.isBlank(resourceID)) {
				//syslogResourceBoIMemcache.delete(resourceIP + "__" + strategyType);
				syslogResourceBoIMemcache.updateCollection(resourceIP + "__" + strategyType, new ArrayList<SyslogResourceBo>(), 60*60*24);
			}else {
				List<SyslogResourceBo> syslogResourceBoList = getSyslogResource(resourceIP, strategyType);
				if(null != syslogResourceBoList) {
					Iterator<SyslogResourceBo> iterator = syslogResourceBoList.iterator();
					while(iterator.hasNext()) {
						SyslogResourceBo syslogResourceBo = iterator.next();
						if(StringUtils.equals(String.valueOf(syslogResourceBo.getResourceId()), resourceID)) {
							iterator.remove();
						}
					}
				}
				//删除缓存有问题
//				if(syslogResourceBoList.isEmpty()) {
//					syslogResourceBoIMemcache.delete(resourceIP + "__" + strategyType);
//				}else
//					syslogResourceBoIMemcache.updateCollection(resourceIP + "__" + strategyType, syslogResourceBoList, 60*60);
				syslogResourceBoIMemcache.updateCollection(resourceIP + "__" + strategyType, syslogResourceBoList, 60*60*24);
			}
		}else {
			if(logger.isWarnEnabled())
				logger.warn("syslogResourceBoIMemcache is invalid while remove cache using {" + strategyType + ":" + resourceIP + "}");
		}
	}

}
