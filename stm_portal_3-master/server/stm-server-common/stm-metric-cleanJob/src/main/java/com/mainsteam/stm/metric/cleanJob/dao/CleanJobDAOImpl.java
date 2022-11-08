package com.mainsteam.stm.metric.cleanJob.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.util.PartitionDateUtil;
import com.mainsteam.stm.util.PropertiesFileUtil;

public class CleanJobDAOImpl implements CleanJobDAO {
	Logger logger =LoggerFactory.getLogger(CleanJobDAOImpl.class);
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}	
	
	
	@Override
	public void cleanHistory(Date fromTime) {
		List<String> tbnames=this.session.selectList("getMetricTableListForJob", "STM_M_H_%");
		Map<String,Object> param=new HashMap<>();
		String partitionName = PartitionDateUtil.partitionNameFormat(fromTime);
		param.put("fromTime",partitionName );
		for(String str: tbnames){
			param.put("tableName", str);
			try{
				session.update("cleanHistory",param);
			}catch(Exception e){
				//logger.error(e.getMessage(),e);
			}
			if (logger.isInfoEnabled()) {
				logger.info("cleanHistory metricId="+str+" date="+partitionName);
			}
		}
	}

	@Override
	public void cleanSummery(MetricSummaryType type, Date fromTime) {
		List<String> tbnames=session.selectList("getMetricTableListForJob", ("STM_MS_"+type+"_%"));
		
		Map<String,Object> param=new HashMap<>();
		param.put("fromTime", fromTime);
		for(String str: tbnames){
			logger.info("clean table "+str);
			try{
				param.put("tableName", str);
				int update=session.update("cleanSummery",param);
				logger.info("clean table["+str+"],has deleted:"+update);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		
	}

	@Override
	public Date cleanHistoryByComputeMinTime(Date d) {
		Properties pp = PropertiesFileUtil.getProperties(CleanJobDAOImpl.class.getClassLoader(), "properties/stm.properties");
		Integer lifetime =  Integer.parseInt(pp.getProperty("metric.history.lifetime", "7"));
		List<String> tbnames=this.session.selectList("getMetricTableListForJob", "STM_M_H_%");
		Map<String,Object> param=new HashMap<>();
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, -lifetime);
		Date curDate = cal.getTime();
		String minPartitionName = "";
		Date minDate = null;
		long diffDay = 0;
		for(String str: tbnames){
			try{
				param.put("tableName", str);
				if(minDate == null){
					minPartitionName = session.selectOne("selectMinPartitionNmae",param);
					if(minPartitionName == null || "".equals(minPartitionName.trim())){
						continue;
					}
					minDate = PartitionDateUtil.partitionDateFormat(minPartitionName);
					if(minDate == null){
						continue;
					}
					long curLong = curDate.getTime();
					long minLong = minDate.getTime();
					diffDay = ((curLong - minLong)/(24*60*60*1000));
				}
				if(diffDay > 0){
					Calendar deletePartition= Calendar.getInstance();
					deletePartition.setTime(minDate);
					for(int i = 0; i <= diffDay ; i++){
						deletePartition.add(Calendar.DAY_OF_YEAR, i);
						String p_name = "";
						try {
							p_name = PartitionDateUtil.partitionNameFormat(deletePartition.getTime());
							param.put("fromTime", p_name);
							session.update("cleanHistory",param);
						} catch (Exception e) {
							//logger.error(e.getMessage(),e);
						}
						if (logger.isInfoEnabled()) {
							logger.info("cleanHistoryByComputeMinTime metricId="+str+" date="+p_name);
						}
					}
				}else{
					break;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return null;
	}
}
