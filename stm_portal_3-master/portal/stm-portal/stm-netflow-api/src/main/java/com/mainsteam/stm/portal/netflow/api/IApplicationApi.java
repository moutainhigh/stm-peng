package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationPageBo;




public interface IApplicationApi {
	public ApplicationPageBo getAllApplication(ApplicationConditionBo apcBo);
	public ApplicationChartDataBo getApplicationChartData(ApplicationConditionBo apcBo);
	public ApplicationPageBo getProtocolByApp(ApplicationConditionBo apcBo);
	public ApplicationChartDataBo getProtocolChartDataByApp(ApplicationConditionBo apcBo);
	public ApplicationPageBo getTerminalsByApp(ApplicationConditionBo apcBo);
	public ApplicationChartDataBo getTerminalChartonDataByApp(ApplicationConditionBo apcBo);
	public ApplicationPageBo getSessionsByApp(ApplicationConditionBo apcBo);
	public ApplicationChartDataBo getSessionChartonDataByApp(ApplicationConditionBo apcBo);
	public ApplicationPageBo getIPGsByApp(ApplicationConditionBo apcBo);
	public ApplicationChartDataBo getipgChartDataByApp(ApplicationConditionBo apcBo);
}
