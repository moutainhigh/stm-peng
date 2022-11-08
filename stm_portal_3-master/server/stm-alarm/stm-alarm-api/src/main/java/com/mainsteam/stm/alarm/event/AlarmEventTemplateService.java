package com.mainsteam.stm.alarm.event;

import com.mainsteam.stm.alarm.obj.AlarmEventTemplate;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

/**
 * 告警模板接口
 */
public interface AlarmEventTemplateService {

    /**
     * 获取模板，传入instanceId+metricId+isMainInstance+moduleType或者uniqueKey
     * @param template
     * @return
     */
    AlarmEventTemplate getTemplate(AlarmEventTemplate template);

    /**
     * 获取自定义模板，不包括默认模板
     * @param template
     * @return
     */
    AlarmEventTemplate getTemplateWithoutDefault(AlarmEventTemplate template);

    /**
     * 获取默认模板
     * @param isMainInstance 是否为主资源，false表示子资源，true代表主资源
     * @param resourceType 资源类型
     * @param metricTypeEnum 指标类型
     * @param instanceStateEnum 告警状态
     * @return
     */
    String getDefaultTemplateContent(boolean isMainInstance, SysModuleEnum resourceType,
                                          MetricTypeEnum metricTypeEnum, InstanceStateEnum instanceStateEnum);

    String getDefaultTemplateContent(boolean isMainInstance, String metric, MetricTypeEnum metricTypeEnum, InstanceStateEnum instanceStateEnum);

    String getDefaultTemplateContent(String key);

    String getDefaultTemplateContent(String resourceId, String metricId, MetricTypeEnum metricTypeEnum, InstanceStateEnum instanceStateEnum);

    /**
     * 获取默认模板
     * @param isMainResource
     * @param resourceType
     * @return
     */
    AlarmEventTemplate getDefaultTemplate(boolean isMainResource, SysModuleEnum resourceType, MetricTypeEnum metricTypeEnum);

    /**
     * 更新模板
     * @param template
     * @return uniqueKey,返回唯一key
     */
    String updateTemplate(AlarmEventTemplate template);

    /**
     * 模板重置，即恢复为默认值,传入uniqueKey或者instanceId+metricId+isMainInstance+moduleType
     * @param template
     * @return
     */
    boolean reset(AlarmEventTemplate template);
}
