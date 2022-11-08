package com.mainsteam.stm.resourcelog.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.dao.SnmpTrapDao;
import com.mainsteam.stm.resourcelog.snmptrap.bo.SnmpLogBo;
import com.mainsteam.stm.resourcelog.snmptrap.bo.SnmpResourceBo;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * 
 * <li>文件名称: SnmpTrapDaoImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月24日 下午3:49:23
 * @author   xiaolei
 */
@Repository("snmpTrapDao")
@SuppressWarnings("rawtypes")
public class SnmpTrapDaoImpl extends BaseDao implements SnmpTrapDao {

	@Autowired
	public SnmpTrapDaoImpl(@Qualifier(BaseDao.SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, SnmpTrapDao.class.getName());
	}

	@Override
	public int updateSnmptrapType(StrategyBo strategyBo) {
		return super.getSession().update("updateSnmptrapType", strategyBo);
	}

	@Override
	public List<AlarmListBo> getSnmptrapHistory(
			Page<AlarmListBo, AlarmListBo> page) {
		return super.getSession().selectList("getSnmptrapHistory", page);
	}

	@Override
	public int saveIpAddress(SyslogResourceBo resourceBo) {
		return super.getSession().insert("saveIpAddress", resourceBo);
	}

	@Override
	public int saveSnmptrapHistroy(SnmpLogBo snmpLogBo) {
		return super.getSession().insert("saveSnmptrapHistroy", snmpLogBo);
	}

	@Override
	public int countSnmptrapHistory(SnmpLogBo snmpLogBo) {
		return super.getSession().selectOne("countSnmptrapHistory", snmpLogBo);
	}

	@Override
	public int saveSnmpResourceSta(SnmpResourceBo snmpResourceBo) {
		return super.getSession().insert("saveSnmpResourceSta", snmpResourceBo);
	}

	@Override
	public List<SyslogResourceBo> getTrapLog(Page<SyslogResourceBo, StrategyBo> page) {
		return super.getSession().selectList("getTrapLog", page);
	}

	@Override
	public int countResourceIp(SyslogResourceBo syslogResBo) {
		return super.getSession().selectOne("countResourceIp", syslogResBo);
	}

	@Override
	public int countIp(SyslogResourceBo syslogResBo) {
		return super.getSession().selectOne("countIp", syslogResBo);
	}

	@Override
	public int updateTrapLogNum(SyslogResourceBo syslogResourceBo) {
		return super.getSession().update("updateTrapLogNum", syslogResourceBo);
	}
	
}
