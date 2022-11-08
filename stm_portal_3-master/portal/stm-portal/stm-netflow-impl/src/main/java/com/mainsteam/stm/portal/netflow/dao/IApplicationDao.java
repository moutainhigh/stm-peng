/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationModelBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceDao.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public interface IApplicationDao {
	public List<ApplicationModelBo> getAllApplications(
			Page<ApplicationModelBo, ApplicationConditionBo> page);

	public Whole getAllApplicationFlows(ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getApplicationChartData(
			ApplicationConditionBo apcBo);

	public String getApplicationNameByID(String app_id);

	public List<ApplicationModelBo> getProtocolByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page);

	public Whole getAllProtocolFlowsByApp(ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getProtocolChartDataByApp(
			ApplicationConditionBo apcBo);

	public String getProtocolNameByIDByApp(String app_id);

	public List<ApplicationModelBo> getTerminalsByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page);

	public Whole getAllTerminalsFlows(ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getTerminalsChartData(
			ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getSessionsByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page);

	public Whole getAllSessionsFlows(ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getSessionsCharData(
			ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getIPGsByApp(
			Page<ApplicationModelBo, ApplicationConditionBo> page);

	public Whole getAllIPGsFlowsByApp(ApplicationConditionBo apcBo);

	public List<ApplicationModelBo> getIPGsCharDataByApp(
			ApplicationConditionBo apcBo);

	public String getIPGNamebyID(String ipgid);

}
