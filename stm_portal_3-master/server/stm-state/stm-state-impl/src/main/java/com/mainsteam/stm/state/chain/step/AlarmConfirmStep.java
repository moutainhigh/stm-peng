package com.mainsteam.stm.state.chain.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.alarm.confirm.AlarmConfirmService;
import com.mainsteam.stm.alarm.obj.AlarmConfirm;
import com.mainsteam.stm.state.chain.StateChainStep;
import com.mainsteam.stm.state.chain.StateChainStepContext;
import com.mainsteam.stm.state.chain.StateComputeChain;

/**
 * 判断是否告警阻塞
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午11:09:33
 * @version 1.0
 */
public class AlarmConfirmStep implements StateChainStep {

	private static final Log logger = LogFactory.getLog(AlarmConfirmStep.class);

	private final AlarmConfirmService alarmConfirmService;

	public AlarmConfirmStep(AlarmConfirmService alarmConfirmService) {
		super();
		this.alarmConfirmService = alarmConfirmService;
	}

	@Override
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain) {
		// 判断是否已进行告警确认
		AlarmConfirm condition = new AlarmConfirm();
		condition.setInstanceId(context.getMetricData().getResourceInstanceId());
		condition.setMetricId(context.getMetricData().getMetricId());
		AlarmConfirm alarmConfirm = alarmConfirmService.findAlarmConfirm(condition);
		if (alarmConfirmService.getAlarmConfirmBlocked(alarmConfirm)) {
			if (logger.isDebugEnabled()){
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("calculate performance metric state has been blocked by ");
				stringBuilder.append(alarmConfirm);
			}
			return;
		}
		context.getContextData().put("alarmConfirm", condition);
		chain.doChain();
	}

}
