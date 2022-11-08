package com.mainsteam.stm.alarm.notify.dao;

import java.util.List;

import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplate;
import org.apache.ibatis.session.SqlSession;

public class SmsOrEmailNotifyTemplateDAOImpl implements SmsOrEmailNotifyTemplateDAO {
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public List<SmsOrEmailNotifyTemplate> findTemplateByCondition(SmsOrEmailNotifyTemplate tmp) {
		return session.selectList("findTemplateByCondition", tmp);
	}

	@Override
	public boolean addTemplate(SmsOrEmailNotifyTemplate tmp) {
		return  (session.insert("addTemplate", tmp) > 0) ? true : false;
	}

	@Override
	public boolean updateTemplate(SmsOrEmailNotifyTemplate tmp) {
		return (session.update("updateTemplate", tmp) > 0) ? true : false;
	}

	@Override
	public boolean deleteTemplate(List<Long> templateIDs) {
		return (session.delete("deleteTemplate", templateIDs) > 0) ? true : false;
	}

	@Override
	public boolean resetDefaultTemplate(SmsOrEmailNotifyTemplate tmp) {
		return (session.delete("resetDefaultTemplate", tmp) > 0 ? true : false);
	}

	@Override
	public List<SmsOrEmailNotifyTemplate> findDefaultTemplate(SmsOrEmailNotifyTemplate tmp) {
		return session.selectList("findDefaultTemplate", tmp);
	}
}
