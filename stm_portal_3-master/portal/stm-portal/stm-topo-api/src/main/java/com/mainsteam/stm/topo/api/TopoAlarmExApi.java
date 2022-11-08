package com.mainsteam.stm.topo.api;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;


/**
 * <li>封装调用其他模块的接口</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * @version  ms.stm
 * @since  2019年11月29日
 * @author zwx
 */
public interface TopoAlarmExApi {
	/**
	 * 说明：
	 * @return
	 */
//	public int getCollectDCS();
	
	/**
	 * 分析发送告警信息
	 * @param base 基准mac
	 * @param runtimeBo 实时mac
	 * @param alarmSet	告警设置信息
	 */
	public void sendAlarmNotice(MacBaseBo base,MacRuntimeBo runtimeBo,String alarmSet);
	
	/**
	 * 修改链路告规则
	 * @param ruleId
	 * @param condition
	 */
	public void updateAlarmConditon(long ruleId,AlarmSendCondition condition);

	/**
	 * 改变链路告规则发送条件是否启用
	 * @param enableInfos
	 */
	public void changeLinkAlarmConditionEnabled(List<AlarmConditonEnableInfo> enableInfos);
	
	/**
	 * 获取告警人
	 * @param alarmType 告警类型（linkalarm、ipmacport）
	 * @param type (email、sms)
	 * @return List<User>
	 */
	public List<User> getAlarmSenders(String alarmType,String type);
	
	/**
	 * 根据ids删除告警规则
	 */
	public void deleteAlarmSetting(long[] ids);
	
	/**
	 * 获取链路告警设置信息
	 * @return List<AlarmRule> 
	 */
	public List<AlarmRule> getLinkAlarmSetting();
	
	/**
	 * 保存链路告警设置信息
	 * @param alarmRule
	 */
	@Transactional
	public void saveLinkAlarmSetting(AlarmRule alarmRule);
}
