package com.mainsteam.stm.alarm.notify;

import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplate;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;

public class SmsOrEmailNotifyTemplateUtil {

    public static SmsOrEmailNotifyTemplate queryCacheById(long templateId) {
        IMemcache<SmsOrEmailNotifyTemplate> remoteMemCache = MemCacheFactory.getRemoteMemCache(SmsOrEmailNotifyTemplate.class);
        return (SmsOrEmailNotifyTemplate) remoteMemCache.get(String.valueOf(templateId));
    }

    public static boolean saveCacheById(SmsOrEmailNotifyTemplate template) {
        String key = "SmsOrEmailNotifyTemplate_" + template.getTemplateID();
        IMemcache<SmsOrEmailNotifyTemplate> remoteMemCache = MemCacheFactory.getRemoteMemCache(SmsOrEmailNotifyTemplate.class);
        return remoteMemCache.set(key, template);
    }

    public static boolean removeCacheById(long templateId) {
        IMemcache<SmsOrEmailNotifyTemplate> remoteMemCache = MemCacheFactory.getRemoteMemCache(SmsOrEmailNotifyTemplate.class);
        return remoteMemCache.delete("SmsOrEmailNotifyTemplate_" + templateId);
    }

    public static SmsOrEmailNotifyTemplate queryDefaultTemplateByType(NotifyTypeEnum notifyTypeEnum, SysModuleEnum sysModuleEnum){
        IMemcache<SmsOrEmailNotifyTemplate> remoteMemCache = MemCacheFactory.getRemoteMemCache(SmsOrEmailNotifyTemplate.class);
        return (SmsOrEmailNotifyTemplate) remoteMemCache.get(SendWayEnum.valueOf(notifyTypeEnum.name()) + sysModuleEnum.name());
    }

    public static boolean removeDefaultTemplateByType(NotifyTypeEnum notifyTypeEnum, SysModuleEnum sysModuleEnum) {
        IMemcache<SmsOrEmailNotifyTemplate> remoteMemCache = MemCacheFactory.getRemoteMemCache(SmsOrEmailNotifyTemplate.class);
        return remoteMemCache.delete(SendWayEnum.valueOf(notifyTypeEnum.name()) + sysModuleEnum.name());
    }

    public static boolean saveDefaultTemplateByType(SmsOrEmailNotifyTemplate template) {
        IMemcache<SmsOrEmailNotifyTemplate> remoteMemCache = MemCacheFactory.getRemoteMemCache(SmsOrEmailNotifyTemplate.class);
        return remoteMemCache.set(SendWayEnum.valueOf(template.getTemplateType().name()) + template.getSysModuleEnum().name(), template);
    }
}
