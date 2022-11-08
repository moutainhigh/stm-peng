/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.SessionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionConditionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceDao.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public interface ISessionDao {
	
	public List<SessionBo> getAllSessions(Page<SessionBo,SessionConditionBo> page);
	@Deprecated
	public String	getAllSessionFlows(SessionConditionBo scBo);
	public List<SessionBo>	getSessionChartData(SessionConditionBo scBo);
	
	public List<SessionBo> getProtoBySession(Page<SessionBo,SessionConditionBo> page);
	@Deprecated
	public String	getAllProtoFlowsBySession(SessionConditionBo scBo);
	public List<SessionBo>	getProtoChartDataBySession(SessionConditionBo scBo);
	public String getProtocolNameByIDBySession(String proto);
	
	
	public List<SessionBo> getAppBySession(Page<SessionBo,SessionConditionBo> page);
	@Deprecated
	public String	getAllAppFlowsBySession(SessionConditionBo scBo);
	public List<SessionBo>	getAppChartDataBySession(SessionConditionBo scBo);
	public String getAppNameByIDBySession(String app_id);
	
	/**
	 * 查询session流量，包，连接数
	 *
	 * @param bo
	 * @return
	 */
	Whole getSessionTotals(SessionConditionBo bo);
	
	/**
	 * 会话协议的流量，包，连接数
	 *
	 * @param bo
	 * @return
	 */
	Whole getSessionProtoTotals(SessionConditionBo bo);
	
	/**
	 * 会话应用流量，包，连接数
	 *
	 * @param bo
	 * @return
	 */
	Whole getSessionAppTotals(SessionConditionBo bo);
	
}
