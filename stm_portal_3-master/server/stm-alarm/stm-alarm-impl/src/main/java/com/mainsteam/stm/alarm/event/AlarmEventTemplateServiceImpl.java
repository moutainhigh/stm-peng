package com.mainsteam.stm.alarm.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mainsteam.stm.alarm.event.dao.AlarmEventTemplateDAO;
import com.mainsteam.stm.alarm.event.util.EventTemplateUtil;
import com.mainsteam.stm.alarm.obj.AlarmEventTemplate;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.po.AlarmEventTemplatePO;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("alarmEventTemplateService")
public class AlarmEventTemplateServiceImpl implements AlarmEventTemplateService {

    private static final Log logger = LogFactory.getLog(AlarmEventTemplateServiceImpl.class);

    @Autowired
    @Qualifier("alarmEventTemplateDao")
    private AlarmEventTemplateDAO alarmEventTemplateDAO;

    @Autowired
    @Qualifier("eventTemplateUtil")
    private EventTemplateUtil eventTemplateUtil;

    @Override
    public AlarmEventTemplate getTemplate(AlarmEventTemplate param) {
        AlarmEventTemplate templateWithoutDefault = getTemplateWithoutDefault(param);
        if(null == templateWithoutDefault) {
            if(logger.isInfoEnabled()) {
                logger.info("customized alarm template is undefined, using default :" + param);
            }
            AlarmEventTemplate template;
            try{
                template = this.getDefaultTemplate(param.isMainProfile(),param.getModuleType(),MetricTypeEnum.PerformanceMetric);
            }catch (Exception e) {
                if(logger.isErrorEnabled()){
                    logger.error(e.getMessage() + ",get default template failed.", e);
                }
                return null;
            }
            template.setMetricId(param.getMetricId());
            template.setProfileId(param.getProfileId());
            template.setTimelineId(param.getTimelineId());
            template.setMainProfile(param.isMainProfile());
            return template;
        }
        return templateWithoutDefault;

    }

    @Override
    public AlarmEventTemplate getTemplateWithoutDefault(AlarmEventTemplate param) {
        String uniqueKey = generateUniqueKey(param);
        if(StringUtils.isEmpty(uniqueKey)){
            if(logger.isWarnEnabled()) {
                logger.warn("uniqueKey is missing when query alarm template with " + param);
            }
            return null;
        }
        String fromCache = getFromCache(uniqueKey);
        AlarmEventTemplatePO resultPo = null;
        if(StringUtils.isNotEmpty(fromCache)) {
            resultPo = JSON.parseObject(fromCache, AlarmEventTemplatePO.class);
        }else {
            AlarmEventTemplatePO po = new AlarmEventTemplatePO();
            po.setUniqueKey(uniqueKey);
            List<AlarmEventTemplatePO> alarmEventTemplatePOS = alarmEventTemplateDAO.get(po);
            if(null != alarmEventTemplatePOS && !alarmEventTemplatePOS.isEmpty()){
                resultPo = alarmEventTemplatePOS.get(0);
                this.setToCache(resultPo);
            }
        }
        if(null != resultPo) {
            param.setUniqueKey(resultPo.getUniqueKey());
            param.setContent(JSON.parseObject(resultPo.getJsonContent(), new TypeReference<Map<InstanceStateEnum, String>>() {}));
            return param;
        }
        return null;
    }

    @Override
    public String getDefaultTemplateContent(boolean isMainInstance, SysModuleEnum resourceType, MetricTypeEnum metricTypeEnum, InstanceStateEnum alarmLevel) {
        String key;
        String resourceId = (isMainInstance) ? "default" : "child_default";
        String level = (alarmLevel == InstanceStateEnum.NORMAL ? "normal" : "critical");
        key = resourceId +"_"+ resourceType.name()+"_" + metricTypeEnum.name() +"_"+ level;
        String templateContent = eventTemplateUtil.getTemplate(key);
        if(logger.isDebugEnabled()) {
            logger.debug("using default alarm template with key :" + key);
        }
        return templateContent;
    }

    @Override
    public String getDefaultTemplateContent(boolean isMainInstance, String metric, MetricTypeEnum metricTypeEnum, InstanceStateEnum instanceStateEnum) {
        String key;
        String resourceId = (isMainInstance) ? "default" : "child_default";
        String level = (instanceStateEnum == InstanceStateEnum.NORMAL ? "normal" : "critical");
        key = resourceId +"_"+ metric +"_" + metricTypeEnum.name() +"_"+ level;
        String templateContent = eventTemplateUtil.getTemplate(key);
        if(logger.isDebugEnabled()) {
            logger.debug("using default alarm template with key :" + key);
        }
        return templateContent;
    }

    @Override
    public String getDefaultTemplateContent(String key) {
        return eventTemplateUtil.getTemplate(key);
    }

    @Override
    public String getDefaultTemplateContent(String resourceId, String metricId, MetricTypeEnum metricTypeEnum, InstanceStateEnum instanceStateEnum) {
        String key = resourceId + "_" + metricId + "_" + metricTypeEnum.name() + "_" + (instanceStateEnum == InstanceStateEnum.NORMAL ? "normal" : "critical");
        return eventTemplateUtil.getTemplate(key);
    }

    @Override
    public AlarmEventTemplate getDefaultTemplate(boolean isMainResource, SysModuleEnum resourceType, MetricTypeEnum metricTypeEnum) {

        String key ;
        String resourceId = (isMainResource) ? "default" : "child_default";
        InstanceStateEnum[] stateLevels = new InstanceStateEnum[]{InstanceStateEnum.SERIOUS,InstanceStateEnum.WARN,InstanceStateEnum.NORMAL};
        AlarmEventTemplate defaultTemplate = new AlarmEventTemplate();
        defaultTemplate.setMainProfile(isMainResource);
        defaultTemplate.setModuleType(resourceType);
        Map<InstanceStateEnum, String> content = new HashMap<>(3);
        defaultTemplate.setContent(content);
        for (InstanceStateEnum level:
             stateLevels) {
            key = resourceId + "_" + resourceType.name() + "_" + metricTypeEnum.name();
            if(level == InstanceStateEnum.NORMAL){
                 key += "_normal";
            }else{
                key += "_critical";
            }
            String template = eventTemplateUtil.getTemplate(key);
            content.put(level, template);
        }
        return defaultTemplate;
    }

    @Override
    public String updateTemplate(AlarmEventTemplate template) {
        AlarmEventTemplatePO po = new AlarmEventTemplatePO();
        po.setUniqueKey(generateUniqueKey(template));
        if(StringUtils.isEmpty(po.getUniqueKey())) {
            if(logger.isWarnEnabled()) {
                logger.warn("miss uniqueKey when updating alarm template with " + template);
            }
            return null;
        }
        po.setJsonContent(JSON.toJSONString(template.getContent()));
        if(alarmEventTemplateDAO.update(po)){
            setToCache(po);
            return po.getUniqueKey();
        }else{
            if(logger.isErrorEnabled()) {
                logger.error("failed to update alarm template using " + template);
            }
        }
        return null;
    }

    @Override
    public boolean reset(AlarmEventTemplate template) {
        String uniqueKey = generateUniqueKey(template);
        if(StringUtils.isEmpty(uniqueKey)) {
            if(logger.isWarnEnabled()){
                logger.warn("miss uniqueKey when resetting alarm template with " + template);
            }
            return false;
        }
        AlarmEventTemplatePO po = new AlarmEventTemplatePO();
        po.setUniqueKey(uniqueKey);
        removeFromCache(po.getUniqueKey());
        return alarmEventTemplateDAO.delete(po);
    }

    private String getFromCache(String key) {
        IMemcache<String> remoteMemCache = MemCacheFactory.getRemoteMemCache(String.class);
        String jsonStr = remoteMemCache.get("eventTemplate_" + key);
        return jsonStr;
    }

    private void  setToCache(AlarmEventTemplatePO template) {
        IMemcache<String> remoteMemCache = MemCacheFactory.getRemoteMemCache(String.class);
        remoteMemCache.set("eventTemplate_" + template.getUniqueKey(), JSON.toJSONString(template));
    }

    private void removeFromCache(String key) {
        if(StringUtils.isNotBlank(key)) {
            IMemcache<String> remoteMemCache = MemCacheFactory.getRemoteMemCache(String.class);
            remoteMemCache.delete(key);
        }
    }

    private String generateUniqueKey(AlarmEventTemplate template) {
        String uniqueKey = template.getUniqueKey();
        if(StringUtils.isEmpty(uniqueKey)) {
            try{
                if(template.getProfileId() !=0 || template.getTimelineId() != 0) {
                    uniqueKey = (template.getTimelineId() !=0 ? ("timeline_"+template.getTimelineId()) : template.getProfileId()) + template.getMetricId()
                            + (template.isMainProfile()?"1":"0")+template.getModuleType().name();
                }else{
                    if(logger.isWarnEnabled()){
                        logger.warn("missing profile id or timeline id when updating alarm template,it maybe a custom metric:" + template);
                    }
                    if(StringUtils.isNotEmpty(template.getMetricId())) {
                        return "custom_" + template.getMetricId();
                    }
                }
            }catch (RuntimeException e) {
                if(logger.isErrorEnabled()){
                    logger.error(e.getMessage() + ",missing parameters when create unique key:" + template, e);
                }
                return null;
            }
        }
        return uniqueKey;
    }
}
