package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.AlarmRuleMainPO;

/**
 * 告警规则主表操作
 * 
 * @author ziw
 * 
 */
public interface AlarmRuleMainDAO {

	/**
	 * 插入
	 * 
	 * @param mainPO
	 */
	public int insert(AlarmRuleMainPO mainPO);

	/**
	 * 根据id更新
	 * 
	 * @param mainPO
	 */
	public int updateById(AlarmRuleMainPO mainPO);

	/**
	 * 删除
	 * 
	 * @param mainPO
	 */
	public int delete(AlarmRuleMainPO mainPO);

	/**
	 * 查询
	 * 
	 * @param mainPO
	 * @return List<AlarmRuleMainPO>
	 */
	public List<AlarmRuleMainPO> get(AlarmRuleMainPO mainPO);
}
