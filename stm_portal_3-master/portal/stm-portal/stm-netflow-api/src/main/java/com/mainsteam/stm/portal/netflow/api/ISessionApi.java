package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.portal.netflow.bo.SessionChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.SessionConditionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionPageBo;




public interface ISessionApi {
	public SessionPageBo getAllSession(SessionConditionBo scBo);
	public SessionChartDataBo getSessionChartData(SessionConditionBo scBo);
	public SessionPageBo getProtocolBySession(SessionConditionBo scBo);
	public SessionChartDataBo getProtocolChartDataBySession(SessionConditionBo scBo);
	public SessionPageBo getAppBySession(SessionConditionBo scBo);
	public SessionChartDataBo getAppChartDataBySession(SessionConditionBo scBo);
}
