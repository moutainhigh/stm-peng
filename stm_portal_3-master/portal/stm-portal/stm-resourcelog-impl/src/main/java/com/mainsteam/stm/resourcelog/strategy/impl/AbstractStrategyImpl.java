package com.mainsteam.stm.resourcelog.strategy.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mainsteam.stm.resourcelog.strategy.cache.TrapAndSyslogCacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.resourcelog.dao.IStrategyDao;
import com.mainsteam.stm.resourcelog.strategy.api.IStrategyApi;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * <li>文件名称: AbstractStrategyImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 提供策略的默认实现</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月10日
 * @author   ziwenwen
 */
public abstract class AbstractStrategyImpl implements IStrategyApi {

	@Autowired
	@Qualifier("strategyDao")
	private IStrategyDao strategyDao;
	private ISequence sequence;
	@Autowired
	@Qualifier("trapAndSyslogCacheUtils")
	private TrapAndSyslogCacheUtils trapAndSyslogCacheUtils;

	@Autowired
	public AbstractStrategyImpl(SequenceFactory sequenceFactory){
		this.sequence=sequenceFactory.getSeq(SEQ.SEQNAME_STM_SYSLOG_SEQ);
	}
	@Override
	public int saveStrategyBasic(StrategyBo strategy) {
		if(null != strategy) {
			Date date = new Date();
			strategy.setId(sequence.next());
			strategy.setCreateDate(date);
//			strategy.setUpdateDate(date);
//			strategy.setUpdaterId(strategy.getCreatorId());
			int resultFlag = -1;
			resultFlag = strategyDao.saveStrategyBasic(strategy);
			return resultFlag;
		}
		return 0;
	}

	@Override
	public int saveStrategyResource(Long strategyId, List<SyslogResourceBo> listRes,int strategyType) {
		if(null != strategyId && null != listRes && 0 != listRes.size()) {
			// 已存在的资源
			Map<Long, SyslogResourceBo> srBoMap = new HashMap<Long, SyslogResourceBo>();
			List<SyslogResourceBo> srBoList = strategyDao.getStrategyResource(strategyId);
			for (int i = 0; srBoList != null && i < srBoList.size(); i++) {
				SyslogResourceBo srBo = srBoList.get(i);
				srBoMap.put(srBo.getResourceId(), srBo);
			}
			//保存之前先删除
//			this.strategyDao.delResourceByStrategyId(strategyId);
			// 需要新增的资源
			List<SyslogResourceBo> addList = new ArrayList<SyslogResourceBo>();
			Map<Long, SyslogResourceBo> mapResBo = new HashMap<Long, SyslogResourceBo>();
			for (SyslogResourceBo resBo : listRes) {
				resBo.setId(sequence.next());
				resBo.setStrategyId(strategyId);
				resBo.setStrategyType(strategyType);
				resBo.setLastDate(new Date());
				resBo.setSnmptrapIp(resBo.getResourceIp());
//				this.strategyDao.delRelationResource(resBo);
				mapResBo.put(resBo.getResourceId(), resBo);
				if(!srBoMap.containsKey(resBo.getResourceId())){
					addList.add(resBo);
				}
			}
			// 需要删除的资源
			for (int i = 0; srBoList != null && i < srBoList.size(); i++) {
				SyslogResourceBo srBo = srBoList.get(i);
				if(!mapResBo.containsKey(srBo.getResourceId())){
					strategyDao.delRelationByResourceIdAndStrategyId(srBo);
				}
			}
			//保存资源
			if(!addList.isEmpty()){
				int saveCount = this.strategyDao.saveStrategyResource(addList);
				//保存resourceSta
				int saveResSta = this.strategyDao.saveResourceSta(addList);

				Map<String, List<SyslogResourceBo>> map = new HashMap<String, List<SyslogResourceBo>>();
				for(SyslogResourceBo syslogResourceBo : addList) {
					List<SyslogResourceBo> syslogResourceBoList = null;
					if(map.containsKey(syslogResourceBo.getResourceIp() + "-" + syslogResourceBo.getStrategyType())) {
						syslogResourceBoList = map.get(syslogResourceBo.getResourceIp() + "-" + syslogResourceBo.getStrategyType());
						syslogResourceBoList.add(syslogResourceBo);
					}else {
						syslogResourceBoList = new ArrayList<>(2);
						syslogResourceBoList.add(syslogResourceBo);
					}
					map.put(syslogResourceBo.getResourceIp() + "-" + syslogResourceBo.getStrategyId(), syslogResourceBoList);
				}
				if(!map.isEmpty()) {
					Set<String> keySet = map.keySet();
					Iterator<String> iterator = keySet.iterator();
					while(iterator.hasNext()) {
						String key = iterator.next();
						trapAndSyslogCacheUtils.setSyslogResource(map.get(key), key.split("-")[0] , key.split("-")[1]);
					}
				}

				return saveCount + saveResSta;
			}else{
				return 1;
			}
		}
		return 0;
	
	}

	@Override
	public int delStrategy(Long[] strategyIds) {
		//删除策略相关的信息
		int delBase = this.strategyDao.batchDelBase(strategyIds);
		int delRes = this.strategyDao.batchDelResource(strategyIds);
		if(delRes > 0) {
			for(Long strategyId : strategyIds) {
				List<SyslogResourceBo> syslogResourceBoList = this.strategyDao.getStrategyResource(strategyId);
				if(null != syslogResourceBoList) {
					for(SyslogResourceBo syslogResourceBo : syslogResourceBoList) {
						this.trapAndSyslogCacheUtils.removeSyslogResource(syslogResourceBo.getResourceIp(),
								String.valueOf(syslogResourceBo.getStrategyType()),null);
					}
				}
			}
		}
		int delRule = this.strategyDao.batchDelRule(strategyIds);
		if(delRule > 0) {
			for(Long strategyId : strategyIds) {
				this.trapAndSyslogCacheUtils.removeSyslogRuleCache(strategyId, null);
			}
		}
		return delBase + delRes + delRule;
	}
	
	public void queryStrategy(Page<StrategyBo, StrategyBo> page) {
		page.setDatas(this.strategyDao.queryStrategy(page));
	}
	
	@Override
	public List<SyslogResourceBo> getResource(Long strategyId) {
		return this.strategyDao.getStrategyResource(strategyId);
	}

	@Override
	public int updateMonitorState(Long[] id, int isMonitor) {
		return this.strategyDao.updateMonitorState(id,isMonitor);
	}
	
	@Override
	public StrategyBo getStrategyBasic(Long strategyId) {
		return this.strategyDao.get(strategyId);
	}
	
	@Override
	public int updateStrategyBasic(StrategyBo strategy) {
		try {
			//设置创建时间
			strategy.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strategy.getCreateTime()));
			
			//设置修改时间
			strategy.setUpdateDate(new Date());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return this.strategyDao.updateStrategyBasic(strategy);
	}
	
	public List<SyslogResourceBo> getResourceBos(Page<SyslogResourceBo, StrategyBo> page) {
		return this.strategyDao.getResourceBos(page);
	}

	@Override
	public int updateResourceStrategy(SyslogResourceBo syslogResourceBo) {
		int resultFlag = -1;
		resultFlag = this.strategyDao.updateResourceStrategy(syslogResourceBo);
		return resultFlag;
	}
	@Override
	public int countRes(SyslogResourceBo resBo) {
		return this.strategyDao.countRes(resBo);
	}
	
	@Override
	public boolean isStrategyNameExist(StrategyBo strategyBo) {
		return this.strategyDao.isStrategyNameExist(strategyBo);
	}
}


