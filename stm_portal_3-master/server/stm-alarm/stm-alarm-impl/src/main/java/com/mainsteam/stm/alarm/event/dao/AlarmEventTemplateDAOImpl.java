package com.mainsteam.stm.alarm.event.dao;

import com.mainsteam.stm.alarm.po.AlarmEventTemplatePO;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("alarmEventTemplateDao")
public class AlarmEventTemplateDAOImpl implements AlarmEventTemplateDAO {

    @Autowired
    @Qualifier("sqlSession")
    private SqlSession session;

    @Override
    public boolean update(AlarmEventTemplatePO po) {
        String databaseId = session.getConfiguration().getDatabaseId();
        if(StringUtils.equals(databaseId, "mysql")){
            return session.insert("com.mainsteam.stm.alarm.event.dao.AlarmEventTemplateDAO.addAlarmEventTemplateForMysql", po) > 0 ? true : false;
        }else if(StringUtils.equals(databaseId, "oracle")){
            return session.insert("com.mainsteam.stm.alarm.event.dao.AlarmEventTemplateDAO.addAlarmEventTemplateForOracle", po) > 0 ? true : false;
        }else{
            session.delete("deleteTemplate", po);
            return session.insert("com.mainsteam.stm.alarm.event.dao.AlarmEventTemplateDAO.addAlarmEventTemplate") > 0 ? true : false;
        }
    }

    @Override
    public boolean delete(AlarmEventTemplatePO po) {
        return session.delete("com.mainsteam.stm.alarm.event.dao.AlarmEventTemplateDAO.deleteTemplate", po) > 0 ? true : false;
    }

    @Override
    public List<AlarmEventTemplatePO> get(AlarmEventTemplatePO po) {
        return session.selectList("com.mainsteam.stm.alarm.event.dao.AlarmEventTemplateDAO.selectTemplate", po);
    }
}
