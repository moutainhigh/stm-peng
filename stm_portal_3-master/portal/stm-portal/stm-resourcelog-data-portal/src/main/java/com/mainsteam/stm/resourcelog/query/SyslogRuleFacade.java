package com.mainsteam.stm.resourcelog.query;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.resourcelog.dao.SyslogDao;
import com.mainsteam.stm.resourcelog.query.TrapCacheTimeredFresh.DataLoader;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年11月21日 上午10:14:16
 * @version 1.0
 */
public class SyslogRuleFacade implements DataLoader<List<SysLogRuleBo>>{
	private SyslogDao syslogDao;
	private TrapCacheTimeredFresh<List<SysLogRuleBo>> cache;
	public void start(){
		cache = new TrapCacheTimeredFresh<>(this, "SyslogRule");
	}
	public List<SysLogRuleBo> querySysLogRulesByIp(String srcIp){
		return cache.get(srcIp);
	}
	public void sumSyslog(long instanceid){		
	}
	@Override
	public Map<String, List<SysLogRuleBo>> load() {
		// TODO Auto-generated method stub
//		syslogDao.get
		return null;
	}
}
