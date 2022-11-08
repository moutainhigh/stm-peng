package com.mainsteam.stm.alarm.notify.dao;

import java.util.List;

import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplate;

/**
 * @author cx
 *
 */
public interface SmsOrEmailNotifyTemplateDAO {

	List<SmsOrEmailNotifyTemplate> findTemplateByCondition(SmsOrEmailNotifyTemplate tmp);

	boolean addTemplate(SmsOrEmailNotifyTemplate tmp);

	boolean updateTemplate(SmsOrEmailNotifyTemplate tmp);

	boolean deleteTemplate(List<Long> templateIDs);

	boolean resetDefaultTemplate(SmsOrEmailNotifyTemplate tmp);

	List<SmsOrEmailNotifyTemplate> findDefaultTemplate(SmsOrEmailNotifyTemplate tmp);

}
