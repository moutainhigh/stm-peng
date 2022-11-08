package com.mainsteam.stm.alarm;

import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;

public interface AlarmService {

	/**统一告警接口<br />
	 * @param paramter
	 */
	void notify(AlarmSenderParamter paramter);
}
