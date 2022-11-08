/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.SessionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionConditionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.ISessionDao;

/**
 * <li>文件名称: DeviceDaoImpl.java</li> 
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


public class SessionDaoImpl extends BaseDao implements ISessionDao{

	public SessionDaoImpl(SqlSessionTemplate session) {
		super(session, ISessionDao.class.getName());
	}
	
	
	//取得全网所以应用的流量信息
	public List<SessionBo> getAllSessions(Page<SessionBo,SessionConditionBo> page) {
		return getSession().selectList(getNamespace()+"getallsessions",page);
	}
	
		
	@Override
	public String	getAllSessionFlows(SessionConditionBo scBo){
		return getSession().selectOne(getNamespace()+"getallsessionflows",scBo);
	}


	@Override
	public List<SessionBo>	getSessionChartData(SessionConditionBo scBo){

		return getSession().selectList(getNamespace()+"getsessionchartdata",scBo);
	}
	
	
	//根据某个应用获取相关的协议的流量信息
	public List<SessionBo> getProtoBySession(Page<SessionBo,SessionConditionBo> page) {
			return getSession().selectList(getNamespace()+"getProtocolBySession",page);
		}
		
		
		@Override
	public String	getAllProtoFlowsBySession(SessionConditionBo scBo){
		return getSession().selectOne(getNamespace()+"getAllProtocolFlowsBySession",scBo);
	}


	@Override
	public List<SessionBo>	getProtoChartDataBySession(SessionConditionBo scBo){

		return getSession().selectList(getNamespace()+"getProtocolChartDataBySession",scBo);
	}
	
	public String getProtocolNameByIDBySession(String proto){
		return getSession().selectOne(getNamespace()+"getprotonamebyidbysession",proto);
	}

	
	
	//根据某个会话，获取应用的流量信息
	public List<SessionBo> getAppBySession(Page<SessionBo,SessionConditionBo> page) {
		return getSession().selectList(getNamespace()+"getallappbysession",page);
	}
	
		
	@Override
	public String	getAllAppFlowsBySession(SessionConditionBo scBo){
		return getSession().selectOne(getNamespace()+"getallappflowsbysession",scBo);
	}


	@Override
	public List<SessionBo>	getAppChartDataBySession(SessionConditionBo scBo){

		return getSession().selectList(getNamespace()+"getappchartdatabysession",scBo);
	}
	
	public String getAppNameByIDBySession(String app_id){
		return getSession().selectOne(getNamespace()+"getallnamebyidbysession",app_id);
	}


	@Override
	public Whole getSessionTotals(SessionConditionBo bo) {
		return getSession().selectOne(getNamespace() + "getSessionTotals", bo);
	}	
	
	@Override
	public Whole getSessionProtoTotals(SessionConditionBo bo) {
		return getSession().selectOne(getNamespace() + "getSessionProtoTotals", bo);
	}


	@Override
	public Whole getSessionAppTotals(SessionConditionBo bo) {
		return getSession().selectOne(getNamespace() + "getSessionAppTotals", bo);
	}
}
