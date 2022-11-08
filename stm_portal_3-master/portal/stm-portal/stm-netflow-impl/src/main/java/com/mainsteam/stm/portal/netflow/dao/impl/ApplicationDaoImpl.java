/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationModelBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.IApplicationDao;

/**
 * <li>文件名称: DeviceDaoImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

@SuppressWarnings("rawtypes")
public class ApplicationDaoImpl extends BaseDao implements IApplicationDao {

	public ApplicationDaoImpl(SqlSessionTemplate session) {
		super(session, IApplicationDao.class.getName());
	}

	// 取得全网所以应用的流量信息
	public List<ApplicationModelBo> getAllApplications(
			Page<ApplicationModelBo, ApplicationConditionBo> page) {
		return getSession().selectList(getNamespace() + "getallapplications",
				page);
	}

	@Override
	public Whole getAllApplicationFlows(ApplicationConditionBo apcBo) {
		return getSession().selectOne(
				getNamespace() + "getallapplicationflows", apcBo);
	}

	@Override
	public List<ApplicationModelBo> getApplicationChartData(
			ApplicationConditionBo apcBo) {

		return getSession().selectList(
				getNamespace() + "getapplicationchartdata", apcBo);
	}

	public String getApplicationNameByID(String app_id) {
		return getSession().selectOne(
				getNamespace() + "getallapplicationnamebyid", app_id);
	}

	// 根据某个应用获取相关的协议的流量信息
	public List<ApplicationModelBo> getProtocolByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page) {
		return getSession().selectList(getNamespace() + "getProtocolByApp",
				page);
	}

	@Override
	public Whole getAllProtocolFlowsByApp(ApplicationConditionBo apcBo) {
		return getSession().selectOne(
				getNamespace() + "getAllProtocolFlowsByApp", apcBo);
	}

	@Override
	public List<ApplicationModelBo> getProtocolChartDataByApp(
			ApplicationConditionBo apcBo) {

		return getSession().selectList(
				getNamespace() + "getProtocolChartDataByApp", apcBo);
	}

	public String getProtocolNameByIDByApp(String app_id) {
		return getSession().selectOne(getNamespace() + "getprotonamebyidbyapp",
				app_id);
	}

	// 根据某个应用获取终端的协议的流量信息
	@Override
	public List<ApplicationModelBo> getTerminalsByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page) {
		return getSession().selectList(getNamespace() + "getTerminalsByapp",
				page);
	}

	@Override
	public Whole getAllTerminalsFlows(ApplicationConditionBo apcBo) {
		return getSession().selectOne(
				getNamespace() + "getTerminalsFlowsByApp", apcBo);
	}

	@Override
	public List<ApplicationModelBo> getTerminalsChartData(
			ApplicationConditionBo apcBo) {

		return getSession().selectList(
				getNamespace() + "getTerminalChartDataByApp", apcBo);
	}

	// 根据某个应用获取终端的会话的流量信息
	public List<ApplicationModelBo> getSessionsByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page) {
		return getSession().selectList(getNamespace() + "getSessionsByApp",
				page);
	}

	public Whole getAllSessionsFlows(ApplicationConditionBo apcBo) {
		return getSession().selectOne(getNamespace() + "getSessionsFlowsByApp",
				apcBo);
	}

	public List<ApplicationModelBo> getSessionsCharData(
			ApplicationConditionBo apcBo) {

		return getSession().selectList(
				getNamespace() + "getSessionChartDataByApp", apcBo);
	}

	// 根据某个应用获取IP分组的流量信息
	public List<ApplicationModelBo> getIPGsByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page) {
		return getSession().selectList(getNamespace() + "getIPGsByApp", page);
	}

	public Whole getAllIPGsFlowsByApp(ApplicationConditionBo apcBo) {
		return getSession().selectOne(getNamespace() + "getIPGsFlowByApp",
				apcBo);
	}

	public List<ApplicationModelBo> getIPGsCharDataByApp(
			ApplicationConditionBo apcBo) {

		return getSession().selectList(getNamespace() + "getIPGChartDataByApp",
				apcBo);
	}

	public String getIPGNamebyID(String ipgid) {
		return getSession().selectOne(getNamespace() + "getipgsnamebyidbyapp",
				ipgid);
	}
}
