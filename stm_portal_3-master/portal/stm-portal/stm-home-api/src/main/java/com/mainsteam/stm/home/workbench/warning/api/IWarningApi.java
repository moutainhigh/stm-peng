package com.mainsteam.stm.home.workbench.warning.api;

import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.home.workbench.warning.bo.Warning;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

public interface IWarningApi {
	 void getAllWarning(Page<Warning, AlarmEventQuery> page);
	 
	 /**
	  * 根据条件查询告警信息
	  * ps：getAllWarning接口实现的方法实现时内部默认填充了一些参数，导致执行更多条件的查询时候失效,
	  *    本接口是对此问题的改进
	  * @author tandl
	  * @param page
	  */
	 void getWarning(Page<Warning, AlarmEventQuery> page);
	 
	 /**
	  * 根据条件查询告警信息
	  * ps：getAllWarning接口实现的方法实现时内部默认填充了一些参数，导致执行更多条件的查询时候失效,
	  *    本接口是对此问题的改进
	  * @author tandl
	  * @param page
	  */
	 void getWarning(Page<Warning, AlarmEventQuery> page,ILoginUser user);
}
