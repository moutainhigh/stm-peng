/**
 * 
 */
package com.mainsteam.stm.resourcelog.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.dao.SyslogDao;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * <li>文件名称: SyslogDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月12日
 * @author   lil
 */
@Repository("syslogStrategyDao")
@SuppressWarnings("rawtypes")
public class SyslogDaoImpl extends BaseDao implements SyslogDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	@Autowired
	public SyslogDaoImpl(@Qualifier(BaseDao.SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, SyslogDao.class.getName());
	}

	@Override
	public int saveStrategyRule(SysLogRuleBo rule) {
		return super.getSession().insert("saveStrategyRule", rule);
	}

	@Override
	public List<SysLogRuleBo> getSysLogRule(Long strategyId) {
		return super.getSession().selectList("getSysLogRule", strategyId);
	}

	@Override
	public int updateRules(SysLogRuleBo sysLogRuleBo) {
		return super.getSession().update("updateRules", sysLogRuleBo);
	}

	@Override
	public int batchRules(Long[] ids) {
		SqlSession session = super.getSession();
		int count = 0;
		for (int i = 0; i < ids.length; i++) {
			count += session.delete("batchRules", ids[i]);
		}
		return count;
	}

	@Override
	public SysLogRuleBo getRuleById(Long sysLogRuleId) {
		return super.getSession().selectOne("getRuleById", sysLogRuleId);
	}
	
	@Override
	public int updateStrateRule(SysLogRuleBo sysLogRuleBo) {
		return super.getSession().update("updateStrateRule", sysLogRuleBo);
	}

	@Override
	public List<AlarmListBo> getSyslogHistory(Page<AlarmListBo, AlarmListBo> page) {
		return super.getSession().selectList("getSyslogHistory", page);
	}

	@Override
	public int saveStrategyRule(List<SysLogRuleBo> rules) {
		return super.getSession().insert("saveStrategyRuleList", rules);
	}

	@Override
	public List<SyslogResourceBo> getResourceBo(SyslogResourceBo resBo) {
		return super.getSession().selectList("getResourceBo", resBo);
	}

	@Override
	public int saveSyslogHistroy(SyslogBo syslogBo) {
		return super.getSession().insert("saveSyslogHistroy", syslogBo);
	}

	@Override
	public int countHistory(SyslogBo syslogBo) {
		return super.getSession().selectOne("countSyslogHistory", syslogBo);
	}

	@Override
	public int updateResourceSta(SyslogResourceBo resBo) {
		return super.getSession().update("updateResourceSta", resBo);
	}

	@Override
	public int updateRuleLevel(String alarmLevel, Long ruleId) {
		SysLogRuleBo ruleBo = new SysLogRuleBo();
		ruleBo.setAlarmLevel(alarmLevel);
		ruleBo.setId(ruleId);
		return super.getSession().update("updateRuleLevel", ruleBo);
	}

	@Override
	public int deleteResourceByResourceId(long[] resourceId) {
		return super.getSession().delete("batchResByResourceId",resourceId);
	}

	@Override
	public boolean isRuleNameExist(SysLogRuleBo ruleBo) {
		int countName = super.getSession().selectOne("countRuleName", ruleBo);
		boolean isExist = false;
		if (countName > 0) {//大于0代表存在
			isExist = true;
		}
		return isExist;
	}
}
