package com.mainsteam.stm.auditlog.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.auditlog.api.IAuditlogApi;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.auditlog.bo.AuditlogQueryBo;
import com.mainsteam.stm.auditlog.bo.AuditlogRuleBo;
import com.mainsteam.stm.auditlog.bo.AuditlogTemplet;
import com.mainsteam.stm.auditlog.dao.IAuditlogDao;
import com.mainsteam.stm.auditlog.engine.AuditlogEngine;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;

public class AuditlogImpl implements IAuditlogApi {
	
	private static final Log logger = LogFactory.getLog(AuditlogImpl.class);
	@Autowired
	private IAuditlogDao auditlogDao;
	private ISequence seq;
	
	@Value("${third.auditlog.webservice.url}")
	private String webservice_url;
	
	@Value("${third.auditlog.webservice.method}")
	private String webservice_method;
	
	@Value("${third.auditlog.webservice.port}")
	private String webservice_port;
	
	@Autowired
	private AuditlogEngine auditlogEngine;
	
	private static String statusFlag = "";
	
	public void setAuditlogDao(IAuditlogDao auditlogDao) {
		this.auditlogDao = auditlogDao;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}

	@Override
	public int insert(AuditlogBo auditlogBo) {
		auditlogBo.setId(seq.next());
		int count = auditlogDao.insert(auditlogBo);
		return count;
	}

	@Override
	public int del(long id) {

		return auditlogDao.del(id);
	}

	@Override
	public int batchDel(long[] ids) {

		return auditlogDao.batchDel(ids);
	}

	@Override
	public AuditlogBo get(long id) {
		return auditlogDao.get(id);
	}

	@Override
	public List<AuditlogBo> pageSelect(Page<AuditlogBo, AuditlogQueryBo> page) {
		return auditlogDao.pageSelect(page);
	}

	@Override
	public List<AuditlogBo> selectAllList(AuditlogQueryBo condition) {
		return auditlogDao.selectAllList(condition);
	}

	@Override
	public int deleteSelect(long[] ids) {
		return auditlogDao.deleteSelect(ids);
	}

	@Override
	public int deleteAll() {
		return auditlogDao.deleteAll();
	}

	@Override
	public List<AuditlogBo> selectBuList(Page<AuditlogBo, AuditlogQueryBo> page) {
		return auditlogDao.selectBuList(page);
	}

	

	@Override
	public int queryBuCount(AuditlogBo auditlogBo) {
		return auditlogDao.queryBuCount(auditlogBo);
	}

	@Override
	public void insertBuAuditlog() {
		List<AuditlogQueryBo> btime = queryLastTime();
		AuditlogBo aBo = new AuditlogBo();
		Date lastOpDate = null;
		Date bptime = new Date();
		if(btime.get(0).getOper_date()!=null){
			lastOpDate = btime.get(0).getOper_date();
		}
		aBo.setOper_date(lastOpDate);
		int count = queryBuCount(aBo);
		int pageSize = 50;
		int pageCount = count%pageSize == 0?count/pageSize:count/pageSize+1;
		for(int currentPage = 0;currentPage<pageCount;currentPage++){
			Page<AuditlogBo, AuditlogQueryBo> page = new Page<AuditlogBo, AuditlogQueryBo>();
			page.setCondition((AuditlogQueryBo)btime.get(0));
			page.setDatas(null);
			page.setOrder("DESC");
			page.setRowCount(pageSize);
			page.setTotalRecord(0);
			page.setSort("OPER_DATE,ID");
			long startRow = currentPage*pageSize;
			page.setStartRow(startRow);
			List<AuditlogBo> list = selectBuList(page);
			for(AuditlogBo alb:list){
				alb.setBackup_date(bptime);
			}
			auditlogDao.insertBuAuditlog(list);
		}
	}

	@Override
	public List<AuditlogQueryBo> queryLastTime() {
		return auditlogDao.queryLastTime();
	}

	@Override
	public List<AuditlogBo> queryBuList(Page<AuditlogBo, AuditlogQueryBo> page) {
		return auditlogDao.queryBulist(page);
	}

	@Override
	public int updateAuditlog(AuditlogBo auditlogBo) {
		return auditlogDao.updateAuditlog(auditlogBo);
	}

	@Override
	public int insertBuAuditlogRule(AuditlogRuleBo auditlogRuleBo) {
		auditlogRuleBo.setId((long)1);
		return auditlogDao.insertBuAuditlogRule(auditlogRuleBo);
	}

	@Override
	public AuditlogRuleBo selectAuditlogRule() {
		List<AuditlogRuleBo> auditlogRuleBos = auditlogDao.selectAuditlogRule();
		AuditlogRuleBo arB = new AuditlogRuleBo();
		if(auditlogRuleBos.size()==0){
			return arB;
		}
		arB = auditlogRuleBos.get(0);
		if(null!=arB.getBackup_time()){
			JSONArray jsonArray = JSONObject.parseArray(arB.getBackup_time());
			JSONObject json = jsonArray.getJSONObject(0);
			String hour = "", minute = "";
			if (StringUtils.isNotEmpty(json.getString("hour"))) {
				hour = json.getString("hour");
			}
			if (StringUtils.isNotEmpty(json.getString("minute"))) {
				minute = json.getString("minute");
			}
			arB.setBackupDateHour(hour);
			arB.setBackupDateMinute(minute);
		}
		return arB;
	}

	@Override
	public int updateAuditlogRule(AuditlogRuleBo auditlogRuleBo) {
		AuditlogTemplet auditlogTemplet = new AuditlogTemplet();
		auditlogTemplet.setAuditlogTempletId(1);
		auditlogTemplet.setAuditlogTempletStatus(auditlogRuleBo.getStatus());
		auditlogTemplet.setAuditlogTempletDay(String.valueOf(auditlogRuleBo.getBackup_day()));
		auditlogTemplet.setAuditlogTempletHour(auditlogRuleBo.getBackupDateHour());
		auditlogTemplet.setAuditlogTempletMinute(auditlogRuleBo.getBackupDateMinute());
		if(auditlogRuleBo.getStatus().equals("0")){
			if(statusFlag.equals(auditlogRuleBo.getStatus())){
				//update
				this.stopJob(auditlogTemplet);
				this.startJob(auditlogTemplet);
			}else{
				//start
				this.startJob(auditlogTemplet);
			}
		}else{
			//stop
			this.stopJob(auditlogTemplet);
		}
		setStatusFlag(auditlogRuleBo.getStatus());
		return auditlogDao.updateAuditlogRule(auditlogRuleBo);
	}
	
	//开始执行job
	private void startJob(AuditlogTemplet auditlogTemplet){
		//调度任务.....
		try {
			auditlogEngine.startEngine(auditlogTemplet);
		} catch (ClassNotFoundException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (InstancelibException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (SchedulerException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		}
	}
	
	//更新执行job
	private void updateJob(AuditlogTemplet auditlogTemplet){
		//调度任务.....
		try {
			auditlogEngine.updateEngine(auditlogTemplet);
		} catch (ClassNotFoundException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (InstancelibException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (SchedulerException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		}
	}
	
	//停止执行job
	private void stopJob(AuditlogTemplet auditlogTemplet){
		//调度任务.....
		try {
			auditlogEngine.stopEngine(auditlogTemplet.getAuditlogTempletId());
		} catch (ClassNotFoundException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		} catch (SchedulerException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		}
	}

	public static String getStatusFlag() {
		return statusFlag;
	}

	public static void setStatusFlag(String statusFlag) {
		AuditlogImpl.statusFlag = statusFlag;
	}
	
	@Override
	public int setLogByWebservice(String log) {
		// TODO Auto-generated method stub
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client client = dcf.createClient(webservice_url);
		Object[] objects = null;
		Object[] result = null;
		try {
			objects = new Object[]{new String(log.getBytes(),"utf-8"), new String(webservice_port.getBytes(),"utf-8"), null};
			result = client.invoke(webservice_method, objects);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("webservice推送日志error:"+e.getMessage());
			}
			return 1;
		}
		return Integer.parseInt(result[0].toString());
	}
}
