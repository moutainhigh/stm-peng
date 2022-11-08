package com.mainsteam.stm.alarm.notify;

import java.util.List;

import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplate;
import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplateEnum;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;

/**
 * @author cx
 */
public interface SmsOrEmailNotifyTemplateService {

	List<SmsOrEmailNotifyTemplate> findNotifyTemplateByCondition(SmsOrEmailNotifyTemplate template);

	boolean addNotifyTemplate(SmsOrEmailNotifyTemplate tmp);

	boolean deleteNotifyTemplate(List<Long> templateIDs, SysModuleEnum sysModuleEnum);

	boolean updateNotifyTemplate(SmsOrEmailNotifyTemplate tmp);

	SmsOrEmailNotifyTemplate resetDefaultNotifyTemplate(NotifyTypeEnum notifyTypeEnum, SysModuleEnum sysModuleEnum);

	SmsOrEmailNotifyTemplateEnum[] findNotifyTemplateParameters();

	SmsOrEmailNotifyTemplate findDefaultTemplateByType(String notifyTypeEnum, SysModuleEnum sysModuleEnum);

	boolean checkNotifyTemplateEnabled(long templateId, NotifyTypeEnum notifyTypeEnum);
}
