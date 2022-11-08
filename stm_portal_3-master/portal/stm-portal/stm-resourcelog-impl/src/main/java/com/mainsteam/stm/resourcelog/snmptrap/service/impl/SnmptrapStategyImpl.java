package com.mainsteam.stm.resourcelog.snmptrap.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.resourcelog.dao.IStrategyDao;
import com.mainsteam.stm.resourcelog.dao.SnmpTrapDao;
import com.mainsteam.stm.resourcelog.snmptrap.api.ISnmptrapStrategyApi;
import com.mainsteam.stm.resourcelog.snmptrap.bo.SnmpResourceBo;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.strategy.impl.AbstractStrategyImpl;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

@Service("snmptrapStrategyApi")
public class SnmptrapStategyImpl extends AbstractStrategyImpl implements
		ISnmptrapStrategyApi {
	
	@Autowired
	@Qualifier("snmpTrapDao")
	private SnmpTrapDao snmpTrapDao;
	
	@Autowired
	@Qualifier("strategyDao")
	private IStrategyDao strategyDao;
	
	private ISequence sequence;
	
	@Autowired
	public SnmptrapStategyImpl(SequenceFactory sequenceFactory){
		super(sequenceFactory);
		this.sequence=sequenceFactory.getSeq(SEQ.SEQNAME_STM_SYSLOG_SEQ);
	}

	@Override
	public int updateSnmptrapType(StrategyBo strategyBo) {
		return this.snmpTrapDao.updateSnmptrapType(strategyBo);
	}

	@Override
	public List<AlarmListBo> getSnmptrapHistory(
			Page<AlarmListBo, AlarmListBo> page) {
		return this.snmpTrapDao.getSnmptrapHistory(page);
	}

	@Override
	public int saveIpAddress(SyslogResourceBo resourceBo) {
		resourceBo.setId(sequence.next());
		SnmpResourceBo snmpResourceBo = new SnmpResourceBo();
		snmpResourceBo.setResourceId(resourceBo.getSnmptrapIp());
		snmpResourceBo.setStrategyType(resourceBo.getStrategyType());
		snmpResourceBo.setLastDate(new Date());
		
		//保存IP
		int saveIp = this.snmpTrapDao.saveIpAddress(resourceBo);
		
		//判断resourceSta中的IP是否存在
		int staIp = this.snmpTrapDao.countResourceIp(resourceBo);
		int saveResSta = 0;
		if (staIp == 0) {
			//保存resourceSta
			saveResSta = this.snmpTrapDao.saveSnmpResourceSta(snmpResourceBo);
		}
		return saveIp + saveResSta;
	}

	@Override
	public List<SyslogResourceBo> getTrapLog(Page<SyslogResourceBo, StrategyBo> page) {
		return this.snmpTrapDao.getTrapLog(page);
	}

	@Override
	public boolean isIpExist(SyslogResourceBo syslogResBo) {
		int countIp = this.snmpTrapDao.countIp(syslogResBo);
		if (countIp > 0) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public int saveIpResource(List<SyslogResourceBo> list) {
		if(null != list && 0 != list.size()) {
			for (SyslogResourceBo syslogResourceBo : list) {
				syslogResourceBo.setId(sequence.next());
				syslogResourceBo.setLastDate(new Date());
				syslogResourceBo.setSnmptrapIp(syslogResourceBo.getResourceIp());
				this.strategyDao.delRelationResource(syslogResourceBo);
			}
			//保存资源
			int saveCount = this.strategyDao.saveStrategyResource(list);
			//保存resourceSta
			int saveResSta = this.strategyDao.saveResourceSta(list);
			return saveCount + saveResSta;
		}
		return 0;
	}


}
