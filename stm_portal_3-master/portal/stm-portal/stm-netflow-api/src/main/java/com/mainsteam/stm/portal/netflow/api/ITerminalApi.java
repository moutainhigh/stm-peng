package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationPageBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalConditionBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalPageBo;




public interface ITerminalApi {
	public TerminalPageBo getAllTerminal(TerminalConditionBo tcBo);
	public TerminalChartDataBo getTerminalChartData(TerminalConditionBo tcBo);
	
	public TerminalPageBo getAppByTerminal(TerminalConditionBo tcBo);
	public TerminalChartDataBo getAppChartDataByTerminal(TerminalConditionBo tcBo);
	
	
	public TerminalPageBo getSessionsByTerminal(TerminalConditionBo tcBo);
	public TerminalChartDataBo getSessionChartonDataByTerminal(TerminalConditionBo tcBo);
	
	public TerminalPageBo getProtocolByTerminal(TerminalConditionBo tcBo);
	public TerminalChartDataBo getProtocolChartDataByTerminal(TerminalConditionBo tcBo);
	
	public TerminalPageBo getTosByTerminal(TerminalConditionBo tcBo);
	public TerminalChartDataBo getTosChartDataByTerminal(TerminalConditionBo tcBo);
}
