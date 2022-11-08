package com.mainsteam.stm.alarm.event.dao;

import com.mainsteam.stm.alarm.po.AlarmEventTemplatePO;

import java.util.List;

public interface AlarmEventTemplateDAO {

    boolean update(AlarmEventTemplatePO po);

    boolean delete(AlarmEventTemplatePO po);

    List<AlarmEventTemplatePO> get(AlarmEventTemplatePO po);
}
