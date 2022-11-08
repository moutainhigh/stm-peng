/**
 * 
 */
package com.mainsteam.stm.resourcelog.syslog.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mainsteam.stm.resourcelog.strategy.cache.TrapAndSyslogCacheUtils;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.resourcelog.dao.SyslogDao;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.impl.AbstractStrategyImpl;
import com.mainsteam.stm.resourcelog.syslog.api.ISyslogStrategyApi;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;


/**
 * <li>文件名称: SyslogStrategeImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月12日
 * @author   lil
 */
@Service("syslogStrategyApi")
public class SyslogStrategyImpl extends AbstractStrategyImpl implements ISyslogStrategyApi,InstancelibListener {
	
	/**
	 * 日志管理
	 */
	private static final Logger LOGGER = Logger.getLogger(SyslogStrategyImpl.class);
	
	@Autowired
	@Qualifier("syslogStrategyDao")
	private SyslogDao syslogStrategyDao;
	
	private ISequence sequence;

	@Autowired
	@Qualifier("trapAndSyslogCacheUtils")
	private TrapAndSyslogCacheUtils trapAndSyslogCacheUtils;
	
	@Autowired
	public SyslogStrategyImpl(SequenceFactory sequenceFactory){
		super(sequenceFactory);
		this.sequence=sequenceFactory.getSeq(SEQ.SEQNAME_STM_SYSLOG_SEQ);
	}
	
	@Override
	public int saveStrategyRule(Long strategyId, List<SysLogRuleBo> rules) {
		List<SysLogRuleBo> saveList = new ArrayList<SysLogRuleBo>();
		for (SysLogRuleBo sysLogRuleBo : rules) {
			sysLogRuleBo.setStrategyId(strategyId);
			sysLogRuleBo.setId(sequence.next());
			saveList.add(sysLogRuleBo);
		}
		int flag = -1;
		flag = this.syslogStrategyDao.saveStrategyRule(saveList);
		if(flag > 0) {
			this.trapAndSyslogCacheUtils.setSyslogRuleBo(saveList, strategyId);
		}
		return flag;
	}

	@Override
	public int saveSingleStrateRule(Long strategyId, SysLogRuleBo rule) {
		if(null != strategyId && null != rule) {
			rule.setId(sequence.next());
			rule.setStrategyId(strategyId);
			int flag = 0;
			flag = this.syslogStrategyDao.saveStrategyRule(rule);
			if(flag > 0) {
				this.trapAndSyslogCacheUtils.setSingleSyslogRuleBo(rule, strategyId);
			}
		}
		return 0;
	}

	@Override
	public List<SysLogRuleBo> getRules(Long strategyId) {
		List<SysLogRuleBo> sysLogRuleBoList = this.trapAndSyslogCacheUtils.getSyslogRule(strategyId);
		if(null != sysLogRuleBoList){
			if(LOGGER.isDebugEnabled()) {
				StringBuffer stringBuffer = new StringBuffer(100);
				stringBuffer.append("query cache syslog rule, strategy is ");
				stringBuffer.append(strategyId);
				LOGGER.debug(stringBuffer.toString());
			}
			return sysLogRuleBoList;
		}else {
			sysLogRuleBoList = this.syslogStrategyDao.getSysLogRule(strategyId);
			if(null != sysLogRuleBoList && !sysLogRuleBoList.isEmpty()) {
				this.trapAndSyslogCacheUtils.setSyslogRuleBo(sysLogRuleBoList, strategyId);
			}
		}
		return sysLogRuleBoList;
	}
	
	@Override
	public int updateRules(SysLogRuleBo sysLogRuleBo) {
		int flag = -1;
		flag = this.syslogStrategyDao.updateRules(sysLogRuleBo);
		if(flag > 0) {
			this.trapAndSyslogCacheUtils.updateSingleSyslogRule(sysLogRuleBo, sysLogRuleBo.getStrategyId());
		}
		return flag;
	}

	@Override
	public int delRules(Long[] ids) {
		if(null != ids && ids.length > 0) {
			SysLogRuleBo sysLogRuleBo = this.syslogStrategyDao.getRuleById(ids[0]);
			Long strategyID = sysLogRuleBo.getStrategyId();
			int flag = this.syslogStrategyDao.batchRules(ids);
			if(flag > 0) {
				this.trapAndSyslogCacheUtils.removeSyslogRuleCache(strategyID, Arrays.asList(ids));
			}
			return flag;
		}
		return 0;
	}

	@Override
	public SysLogRuleBo getRuleById(Long sysLogRuleId) {
		return this.syslogStrategyDao.getRuleById(sysLogRuleId);
	}

	@Override
	public int updateStrateRule(SysLogRuleBo sysLogRuleBo) {
		int flag = -1;
		flag =  this.syslogStrategyDao.updateStrateRule(sysLogRuleBo);
		if(flag > 0) {
			this.trapAndSyslogCacheUtils.updateSingleSyslogRule(sysLogRuleBo, sysLogRuleBo.getStrategyId());
		}
		return flag;
	}

	@Override
	public List<AlarmListBo> getAlarmList(Page<AlarmListBo, AlarmListBo> page) {
		return this.syslogStrategyDao.getSyslogHistory(page);
	}

	@Override
	public int updateRuleLevel(String alarmLevel, Long ruleId) {
		int flag = -1;
		flag = this.syslogStrategyDao.updateRuleLevel(alarmLevel, ruleId);
		if(flag > 0) {
			SysLogRuleBo sysLogRuleBo = this.syslogStrategyDao.getRuleById(ruleId);
			this.trapAndSyslogCacheUtils.updateSingleSyslogRule(sysLogRuleBo, sysLogRuleBo.getStrategyId());
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType()==EventEnum.INSTANCE_DELETE_EVENT){
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			long[] resourceIds = new long[deleteIds.size()];
			for(int i = 0; i < deleteIds.size(); i ++){
				resourceIds[i] = deleteIds.get(i);
			}
			try {
				this.syslogStrategyDao.deleteResourceByResourceId(resourceIds);
			} catch (Exception e) {
				LOGGER.debug("删除资源相关信息失败!!!", e);
			}
		}
	
	}

	@Override
	public boolean isRuleNameExist(SysLogRuleBo ruleBo) {
		return this.syslogStrategyDao.isRuleNameExist(ruleBo);
	}

	@Override
	public List<SyslogResourceBo> getResource(SyslogResourceBo syslogResourceBo) {
		List<SyslogResourceBo> syslogResourceBoList = this.trapAndSyslogCacheUtils.getSyslogResource(syslogResourceBo.getResourceIp(),
				String.valueOf(syslogResourceBo.getStrategyType()));

		if(null == syslogResourceBoList || syslogResourceBoList.isEmpty()) {
			syslogResourceBoList = this.syslogStrategyDao.getResourceBo(syslogResourceBo);
			if(null == syslogResourceBoList)
				return null;
			else{
				this.trapAndSyslogCacheUtils.setSyslogResource(syslogResourceBoList, syslogResourceBo.getResourceIp(),
						String.valueOf(syslogResourceBo.getStrategyType()));
				return syslogResourceBoList;
			}
		}else{
			if(LOGGER.isDebugEnabled()) {
				StringBuffer stringBuffer = new StringBuffer(100);
				stringBuffer.append("query cache syslog resource {");
				stringBuffer.append(syslogResourceBo.getResourceIp());
				stringBuffer.append(":");
				stringBuffer.append(syslogResourceBo.getStrategyType());
				stringBuffer.append("}");
				LOGGER.debug(stringBuffer.toString());
			}
			return syslogResourceBoList;
		}

	}


}
