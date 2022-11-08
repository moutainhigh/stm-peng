/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.TerminalBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalConditionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.ITerminalDao;

/**
 * <li>文件名称: DeviceDaoImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

@SuppressWarnings("rawtypes")
public class TerminalDaoImpl extends BaseDao implements ITerminalDao {

	public TerminalDaoImpl(SqlSessionTemplate session) {
		super(session, ITerminalDao.class.getName());
	}

	// 根据某个应用获取终端的协议的流量信息
	@Override
	public List<TerminalBo> getAllTerminals(
			Page<TerminalBo, TerminalConditionBo> page) {
		return getSession()
				.selectList(getNamespace() + "getAllTerminals", page);
	}

	@Override
	public Whole getAllTerminalsFlows(TerminalConditionBo tcBo) {
		return getSession().selectOne(getNamespace() + "getAllTerminalsFlows",
				tcBo);
	}

	@Override
	public List<TerminalBo> getTerminalsChartData(TerminalConditionBo tcBo) {
		return getSession().selectList(
				getNamespace() + "getAllTerminalChartData", tcBo);
	}

	public List<TerminalBo> getAppByTerminal(
			Page<TerminalBo, TerminalConditionBo> page) {
		return getSession().selectList(getNamespace() + "getappbyterminal",
				page);
	}

	@Override
	public Whole getAppFlowsByTerminal(TerminalConditionBo tcBo) {
		return getSession().selectOne(getNamespace() + "getappflowsbyterminal",
				tcBo);
	}

	@Override
	public List<TerminalBo> getAppChartDataByTerminal(TerminalConditionBo tcBo) {
		return getSession().selectList(
				getNamespace() + "getappchartdatabyterminal", tcBo);
	}

	public String getAppNameByIDByTerminal(String app_id) {
		return getSession().selectOne(
				getNamespace() + "getappnamebyidbyterminal", app_id);
	}

	// 根据某个应用获取终端的会话的流量信息
	public List<TerminalBo> getSessionsByTerminal(
			Page<TerminalBo, TerminalConditionBo> page) {
		return getSession().selectList(
				getNamespace() + "getSessionsByTerminal", page);
	}

	public Whole getAllSessionsFlowsByTerminal(TerminalConditionBo tcBo) {
		return getSession().selectOne(
				getNamespace() + "getSessionsFlowsByTerminal", tcBo);
	}

	public List<TerminalBo> getSessionsCharDataByTerminal(
			TerminalConditionBo tcBo) {
		return getSession().selectList(
				getNamespace() + "getSessionChartDataByTerminal", tcBo);
	}

	public List<TerminalBo> getProtocolByTerminal(
			Page<TerminalBo, TerminalConditionBo> page) {
		return getSession().selectList(
				getNamespace() + "getProtocolByterminal", page);
	}

	@Override
	public Whole getAllProtocolFlowsByTerminal(TerminalConditionBo tcBo) {
		return getSession().selectOne(
				getNamespace() + "getAllProtocolFlowsByterminal", tcBo);
	}

	@Override
	public List<TerminalBo> getProtocolChartDataByTerminal(
			TerminalConditionBo tcBo) {
		return getSession().selectList(
				getNamespace() + "getProtocolChartDataByterminal", tcBo);
	}

	public String getProtocolNameByIDByTerminal(String proto) {
		return getSession().selectOne(getNamespace() + "getprotonamebyidbyapp",
				proto);
	}

	public List<TerminalBo> getTosByTerminal(
			Page<TerminalBo, TerminalConditionBo> page) {
		return getSession().selectList(getNamespace() + "getTosByterminal",
				page);
	}

	@Override
	public Whole getAllTosFlowsByTerminal(TerminalConditionBo tcBo) {
		return getSession().selectOne(
				getNamespace() + "getAllTosFlowsByterminal", tcBo);
	}

	@Override
	public List<TerminalBo> getTosChartDataByTerminal(TerminalConditionBo tcBo) {
		return getSession().selectList(
				getNamespace() + "getTosChartDataByterminal", tcBo);
	}

	public String getTosNameByIDByTerminal(String tos) {
		return getSession().selectOne(
				getNamespace() + "gettosnamebyidbyterminal", tos);
	}
}
