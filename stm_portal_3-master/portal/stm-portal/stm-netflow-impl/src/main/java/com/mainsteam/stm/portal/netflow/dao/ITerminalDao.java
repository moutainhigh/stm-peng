/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.TerminalBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalConditionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceDao.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public interface ITerminalDao {

	public List<TerminalBo> getAllTerminals(
			Page<TerminalBo, TerminalConditionBo> page);

	public Whole getAllTerminalsFlows(TerminalConditionBo tcBo);

	public List<TerminalBo> getTerminalsChartData(TerminalConditionBo tcBo);

	public List<TerminalBo> getAppByTerminal(
			Page<TerminalBo, TerminalConditionBo> page);

	public Whole getAppFlowsByTerminal(TerminalConditionBo tcBo);

	public List<TerminalBo> getAppChartDataByTerminal(TerminalConditionBo tcBo);

	public String getAppNameByIDByTerminal(String app_id);

	public List<TerminalBo> getSessionsByTerminal(
			Page<TerminalBo, TerminalConditionBo> page);

	public Whole getAllSessionsFlowsByTerminal(TerminalConditionBo tcBo);

	public List<TerminalBo> getSessionsCharDataByTerminal(
			TerminalConditionBo tcBo);

	public List<TerminalBo> getProtocolByTerminal(
			Page<TerminalBo, TerminalConditionBo> page);

	public Whole getAllProtocolFlowsByTerminal(TerminalConditionBo tcBo);

	public List<TerminalBo> getProtocolChartDataByTerminal(
			TerminalConditionBo tcBo);

	public String getProtocolNameByIDByTerminal(String proto);

	public List<TerminalBo> getTosByTerminal(
			Page<TerminalBo, TerminalConditionBo> page);

	public Whole getAllTosFlowsByTerminal(TerminalConditionBo tcBo);

	public List<TerminalBo> getTosChartDataByTerminal(TerminalConditionBo tcBo);

	public String getTosNameByIDByTerminal(String tos);
}
