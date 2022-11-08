package com.mainsteam.stm.profilelib.util;

import com.mainsteam.stm.cache.MemcacheManager;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 告警规则缓存工具类，只能使用远程缓存
 */
public class AlarmRuleCacheUtil {

    private static final Log logger = LogFactory.getLog(AlarmRuleService.class);

    private static MemCachedClient memCachedClient = null;

    static {
        try{
            memCachedClient = MemcacheManager.initMemCachedClient();
        }catch (RuntimeException e) {
            if(logger.isWarnEnabled()){
                logger.warn("can't use remote cache:" + e.getMessage());
            }
        }
    }

    /**
     * 缓存告警规则
     * @param alarmRuleList
     * @param key
     * @return
     */
    public static boolean saveAlarmRules(List<AlarmRule> alarmRuleList, String key) {
        key = "RemoteMemCache-AlarmRule-" + key;
        return memCachedClient.set(key, alarmRuleList);
    }

    public static boolean saveAlarmRules(AlarmRule alarmRule, String key) {
        key = "RemoteMemCache-AlarmRule-" + key;
        List<AlarmRule> collection = (List<AlarmRule>) memCachedClient.get(key);
        if(null != collection) {
            collection.add(alarmRule);
        }else {
            collection = new ArrayList(1);
            collection.add(alarmRule);
        }
        return memCachedClient.set(key, collection);
    }

    public static boolean removeAlarmRules(String key) {
        key = "RemoteMemCache-AlarmRule-" + key;
        return memCachedClient.delete(key);
    }

    public static boolean removeAlarmRulesByRuleID(String key, long ruleId) {
        key = "RemoteMemCache-AlarmRule-" + key;
        List<AlarmRule> alarmRuleList = (List<AlarmRule>) memCachedClient.get(key);
        if(null != alarmRuleList && !alarmRuleList.isEmpty()) {
            Iterator<AlarmRule> iterator = alarmRuleList.iterator();
            while (iterator.hasNext()) {
                AlarmRule next = iterator.next();
                if(next.getId() == ruleId){
                    iterator.remove();
                }
            }
            if(alarmRuleList.isEmpty()){
                return memCachedClient.delete(key);
            }else
                return memCachedClient.set(key, alarmRuleList);
        }
        return false;
    }

    public static List<AlarmRule> catchAlarmRules(String key) {
        key = "RemoteMemCache-AlarmRule-" + key;
        return (List<AlarmRule>) memCachedClient.get(key);
    }

    /**
     * 缓存告警发送条件
     * @param condition
     * @return
     */
    public static boolean saveAlarmSendCondition(AlarmSendCondition condition) {
        String key = "RemoteMemCache-AlarmSendCondition-" + String.valueOf(condition.getRuleId());
        List<AlarmSendCondition> collection = (List<AlarmSendCondition>) memCachedClient.get(key);
        if(null != collection) {
            collection.add(condition);
        }else{
            collection = new ArrayList(1);
            collection.add(condition);
        }
        return memCachedClient.set(key, collection);
    }

    public static boolean saveAllAlarmSendCondition(List<AlarmSendCondition> conditions, String ruleId){
        String key = "RemoteMemCache-AlarmSendCondition-" + ruleId;
        return memCachedClient.set(key, conditions);
    }

    public static boolean updateAlarmSendConditionBySendWay(AlarmSendCondition condition) {
        String key = "RemoteMemCache-AlarmSendCondition-" + String.valueOf(condition.getRuleId());
        List<AlarmSendCondition> collection = (List<AlarmSendCondition>) memCachedClient.get(key);
        if(null == collection){
            collection = new ArrayList(1);
            collection.add(condition);
        }else{
            Iterator<AlarmSendCondition> iterator = collection.iterator();
            while (iterator.hasNext()) {
                AlarmSendCondition next = iterator.next();
                if(next.getRuleId() == condition.getRuleId() && next.getSendWay() == condition.getSendWay()){
                    iterator.remove();
                    break;
                }
            }
            collection.add(condition);
        }
        return memCachedClient.set(key, collection);
    }

    public static boolean updateAlarmSendConditionEnabled(String key, SendWayEnum sendWayEnum, boolean enabled){
        String tk = "RemoteMemCache-AlarmSendCondition-" + key;
        List<AlarmSendCondition> collection = (List<AlarmSendCondition>) memCachedClient.get(tk);
        if(null != collection){
            for(AlarmSendCondition next : collection){
                if(next.getRuleId() == Integer.parseInt(key) && next.getSendWay() == sendWayEnum){
                    next.setEnabled(enabled);
                    break;
                }
            }
            return memCachedClient.set(tk, collection);
        }
        return false;
    }

    public static boolean removeAlarmSendCondition(String key) {
        key = "RemoteMemCache-AlarmSendCondition-" + key;
        return memCachedClient.delete(key);
    }

    public static boolean removeAlarmSendConditionBySendWay(String key, SendWayEnum sendWayEnum){
        key = "RemoteMemCache-AlarmSendCondition-" + key;
        List<AlarmSendCondition> alarmSendConditionList = (List<AlarmSendCondition>) memCachedClient.get(key);
        if(null != alarmSendConditionList && !alarmSendConditionList.isEmpty()) {
            Iterator<AlarmSendCondition> iterator = alarmSendConditionList.iterator();
            while (iterator.hasNext()){
                AlarmSendCondition next = iterator.next();
                if(next.getSendWay() == sendWayEnum){
                    iterator.remove();
                    break;
                }
            }
            if(alarmSendConditionList.isEmpty())
                memCachedClient.delete(key);
            else
                return memCachedClient.set(key, alarmSendConditionList);
        }
        return false;
    }

    public static List<AlarmSendCondition> catchAlarmSendCondition(String key) {
        key = "RemoteMemCache-AlarmSendCondition-" + key;
        return (List<AlarmSendCondition>) memCachedClient.get(key);
    }

}
