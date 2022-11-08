package com.mainsteam.stm.alarm.cache;

import com.mainsteam.stm.alarm.obj.AlarmConfirm;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Xiaopf on 7/3/2016.
 */
public class AlarmConfirmCacheUtils implements InitializingBean{

    private static final Log logger = LogFactory.getLog(AlarmConfirmCacheUtils.class);

    private IMemcache<AlarmConfirm> alarmConfirmCacheUtilsIMemcache;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("Starts to init AlarmConfirmCacheUtils...");
        }
        if(alarmConfirmCacheUtilsIMemcache == null)
            alarmConfirmCacheUtilsIMemcache = MemCacheFactory.getRemoteMemCache(AlarmConfirm.class);

    }

    public boolean setAlarmConfirmCache(AlarmConfirm obj) {
        if(alarmConfirmCacheUtilsIMemcache !=null) {
            if(alarmConfirmCacheUtilsIMemcache.isActivate()) {
                String key = obj.getInstanceId()+obj.getMetricId();
                AlarmConfirm alarmConfirm = alarmConfirmCacheUtilsIMemcache.get(obj.getInstanceId()+obj.getMetricId());
                if(alarmConfirm !=null) {
                    boolean updated = alarmConfirmCacheUtilsIMemcache.update(key, obj);
                    if(!updated) {
                        if(logger.isWarnEnabled()) {
                            logger.warn("failed to update alarm confirm cache, try to cache again. " + obj);
                        }
                        boolean isDelete = alarmConfirmCacheUtilsIMemcache.delete(key);
                        if(isDelete) {
                            boolean setAgain = alarmConfirmCacheUtilsIMemcache.set(key, obj);
                            if(!setAgain){
                                if(logger.isWarnEnabled()) {
                                    logger.warn("Can not set up alarm confirm cache " + obj);
                                }
                                return false;
                            }
                            return setAgain;
                        }else{
                            if(logger.isWarnEnabled()) {
                                logger.warn("Can not delete alarm confirm cache " + obj);
                            }
                        }
                    }
                    return updated;
                }else {
                    boolean flag = alarmConfirmCacheUtilsIMemcache.set(key, obj);
                    if(!flag){
                        if(logger.isWarnEnabled()) {
                            logger.warn("Failed to cached alarm state " + obj);
                        }
                    }
                    return flag;
                }
            }else {
                if(logger.isWarnEnabled()) {
                    logger.warn("alarmConfirmCacheUtilsIMemcache is invalid." + obj);
                }
                return false;
            }
        }else {
            if(logger.isWarnEnabled())
                logger.warn("alarmConfirmCacheUtilsIMemcache is null.");
            return false;
        }
    }

    public AlarmConfirm getAlarmConfirmCache(AlarmConfirm alarmConfirm) {
        if(alarmConfirmCacheUtilsIMemcache != null){
            if(alarmConfirmCacheUtilsIMemcache.isActivate()) {
                return alarmConfirmCacheUtilsIMemcache.get(alarmConfirm.getInstanceId() + alarmConfirm.getMetricId());
            }else {
                if(logger.isWarnEnabled()) {
                    logger.warn("Alarm Confirm cache is invalid {" + alarmConfirm + "}");
                }
                return null;
            }
        } else{
            if(logger.isWarnEnabled())
                logger.warn("alarmConfirmCacheUtilsIMemcache is null.");
        }
        return null;
    }

    public boolean removeAlarmConfirmCache(AlarmConfirm alarmConfirm) {
        if(alarmConfirmCacheUtilsIMemcache !=null) {
            boolean flag = alarmConfirmCacheUtilsIMemcache.delete(alarmConfirm.getInstanceId() + alarmConfirm.getMetricId());
            if(!flag){
                if(logger.isWarnEnabled()) {
                    logger.warn("Failed to remove alarm confirm cache " + alarmConfirm);
                }
            }
            return flag;
        }else {
            if(logger.isWarnEnabled())
                logger.warn("alarmConfirmCacheUtilsIMemcache is null.");
        }
        return false;
    }
}
