package com.mainsteam.stm.common.metric.report;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.state.dao.MetricStateDAO;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.state.obj.InstanceStateData;

public class AvailableMetricDataReportServiceImpl implements AvailableMetricDataReportService{
	private Log logger=LogFactory.getLog(AvailableMetricDataReportServiceImpl.class);
	private MetricStateDAO metricStateDAO;
	public void setMetricStateDAO(MetricStateDAO metricStateDAO) {
		this.metricStateDAO = metricStateDAO;
	}

	@Override
	public List<AvailableMetricCountData> findAvailableCount(AvailableMetricCountQuery query) {
		if(logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("starts to create instance state report, instance [");
			if(null != query.getInstanceIDes() && !query.getInstanceIDes().isEmpty()) {
				for(long instanceId : query.getInstanceIDes()) {
					sb.append(instanceId).append(",");
				}
			}
			sb.append("]");
			logger.debug(sb.toString());
		}
		Map<Long, TempAvailableData> map = new HashMap<>();
		for(long id : query.getInstanceIDes()) {
			map.put(id, new TempAvailableData(id, 0, 0, 0));
		}

		for(TimePeriod period : query.getTimePeriods()){
			for(long id : query.getInstanceIDes()){
				TempAvailableData da = calculateAvailable(period.getStartTime(),period.getEndTime(),id);
				if(da != null) {
					TempAvailableData tempAvailableData = map.get(da.getInstanceId());
					if(null != tempAvailableData) {
						tempAvailableData.setNumCritical(tempAvailableData.getNumCritical() + da.getNumCritical());
						tempAvailableData.setTimeCritical(tempAvailableData.getTimeCritical() + da.getTimeCritical());
						tempAvailableData.setTimeTotal(tempAvailableData.getTimeTotal() + da.getTimeTotal());
					}
				}
			}
		}

		List<AvailableMetricCountData> result = new ArrayList<>(map.values().size());
		for(TempAvailableData tempAvailableData : map.values()) {
			try{
				AvailableMetricCountData countData = new AvailableMetricCountData();
				countData.setInstanceID(tempAvailableData.getInstanceId());
				int subNumCritical = tempAvailableData.getNumCritical();
				long subTimeTotal = tempAvailableData.getTimeTotal();//总时间
				long subTimeCritical = tempAvailableData.getTimeCritical();

				countData.setNotAvailabilityNum(subNumCritical);
				if(subNumCritical > 0){
					BigDecimal nah = new BigDecimal(subTimeCritical);
					countData.setNotAvailabilityDurationHour(nah.divide(new BigDecimal(3600000),2,BigDecimal.ROUND_HALF_UP).floatValue()); //不可用时长（小时单位）
				}else {
					countData.setAvailabilityRate(1F);
					countData.setNotAvailabilityDurationHour(0F);
				}

				if(subTimeCritical > subTimeTotal) {
					if(logger.isWarnEnabled()) {
						StringBuilder sb = new StringBuilder();
						sb.append("instance state report compute error, timeCritical is ").append(subTimeCritical);
						sb.append(",instance is ").append(tempAvailableData.getInstanceId());
						logger.warn(sb.toString());
					}
					subTimeCritical = subTimeTotal;
				}
				if(subTimeCritical == subTimeTotal){
					countData.setAvailabilityRate(0F);//可用性比
				}else {
					BigDecimal rate = new BigDecimal(subTimeTotal-subTimeCritical);
					countData.setAvailabilityRate(rate.divide(new BigDecimal(subTimeTotal),4,BigDecimal.ROUND_HALF_UP).floatValue()); //可用性比
				}
				if(subTimeCritical == 0 || subNumCritical == 0) { //如果故障次数为0，则表明无故障运行时间为统计时长
					BigDecimal MTBF = new BigDecimal(subTimeTotal);
					countData.setMTBF(MTBF.divide(new BigDecimal(3600000),2,BigDecimal.ROUND_HALF_UP).floatValue());
				}else {
					BigDecimal MTBF = new BigDecimal(subTimeTotal - subTimeCritical); //平均故障间隔，即平均无故障运行时间
					countData.setMTBF(MTBF.divide(new BigDecimal(3600000),2,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(subNumCritical),2,BigDecimal.ROUND_HALF_UP).floatValue()); //平均故障时间
				}
				if(subTimeCritical == 0 || subNumCritical == 0) { //如果故障次数为0，则表示故障修复时间为0
					countData.setMTTR(0F);
				}else {
					BigDecimal MTTR = new BigDecimal(subTimeCritical);
					countData.setMTTR(MTTR.divide(new BigDecimal(3600000),2,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(subNumCritical),2,BigDecimal.ROUND_HALF_UP).floatValue()); //平均修复时间
				}
				result.add(countData);
			}catch (Throwable throwable) {
				if(logger.isWarnEnabled()) {
					StringBuilder sb = new StringBuilder();
					sb.append("available report error : instance is ").append(tempAvailableData.getInstanceId());
					sb.append(",timeCritical:").append(tempAvailableData.getTimeCritical());
					sb.append(",timeTotal:").append(tempAvailableData.getTimeTotal());
					sb.append(",numCritical").append(tempAvailableData.getNumCritical());
					sb.append(",exception message is ").append(throwable.getMessage());
					logger.warn(sb.toString(), throwable);
				}
				continue;
			}
		}
		return result;

	}

	/**
	 * 可用性报表计算逻辑：
	 * 根据资源实例和起止时间查出资源状态历史信息数据，按时间的升序排列。由于资源实例的状态还包括严重，警告等状态，对于可用性报表来说，只取资源状态比较即可，
	 * 即只有可用和不可用。
	 * 1.故障时长：连续致命状态的时间差+（致命-正常）时间差，即在连续前后两次状态中，如果前后两次都有故障或者前一次状态为故障，后一次状态为正常，这个时间差就是故障时间
	 * 2.故障次数：每统计一次故障时长，故障次数++
	 * 3.计算公式：
	 * (a) 可用性比率：（总时间-故障时间）/总时间，四舍五入保留小数点后2位
	 * (b) 平均故障间隔（MTBF）：（总时间-故障时间）/小时/故障次数
	 * (c) 平均故障恢复时间（MTTR）：故障时间/小时/故障次数
	 * (d) 不可用次数即故障次数
	 * (e) 故障时长
	 * @param startTime
	 * @param endTime
	 * @param instanceID
     * @return
     */
	private TempAvailableData calculateAvailable(Date startTime, Date endTime, Long instanceID) {
		
		List<InstanceStateData> stateList = null;
		InstanceStateData preState = null;
		long timeCritical = 0L;//故障时间,即故障恢复时间
		int numCritical = 0;//故障次数，即故障修复次数
		try{
			stateList = metricStateDAO.findInstanceStateHistory(startTime,endTime,new Long[]{instanceID});
			//取得距离当前时间段起始时间最近的一条记录
			preState = metricStateDAO.getPreInstanceState(instanceID, startTime);
		}catch (Throwable t) {
			if(logger.isWarnEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("instance state report error, id is ").append(instanceID);
				sb.append(",start from ").append(startTime).append(" end up with ").append(endTime);
				sb.append(",error message is ").append(t.getMessage());
				logger.warn(sb.toString() ,t);
			}
			return null;
		}

		if(preState !=null){
			if(logger.isDebugEnabled()){
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("instance state report, before ");
				stringBuilder.append(startTime);
				stringBuilder.append("history data {").append(preState.getInstanceID());
				stringBuilder.append(":").append(preState.getState()).append(":").append(preState.getCollectTime()).append("}");
				logger.debug(stringBuilder.toString());
			}
			//用于时间和状态计算，格式化数据;
			preState.setCollectTime(startTime);
			preState.setState(convertInstanceState(preState.getState()));
		}else {
			if(logger.isInfoEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("instance state report: cannot find previous state data with ").append(instanceID);
				sb.append(",before ").append(startTime);
				logger.info(sb.toString());
			}
		}

		if(null != stateList && !stateList.isEmpty()) {
			for(InstanceStateData currentState : stateList){
				currentState.setState(convertInstanceState(currentState.getState()));
				if(null == preState) {
					preState = currentState;
					continue;
				}
				try{
					if(currentState.getCollectTime().equals(preState.getCollectTime()))
						continue;
					if(currentState.getState() == preState.getState()) { //连续相同的状态
						if(InstanceStateEnum.CRITICAL == currentState.getState()) {
							numCritical++;
							timeCritical += (currentState.getCollectTime().getTime() - preState.getCollectTime().getTime());
						}
					}else {//状态起伏
						if(InstanceStateEnum.CRITICAL == preState.getState() && InstanceStateEnum.NORMAL == currentState.getState()) { //致命
							numCritical++;
							timeCritical += (currentState.getCollectTime().getTime() - preState.getCollectTime().getTime());
						}
					}
					preState = currentState;
				}catch (Throwable t) {
					if(logger.isWarnEnabled()) {
						StringBuilder sb = new StringBuilder();
						sb.append("occurs exception while looping instance state data, instance is ");
						sb.append(instanceID);
						sb.append(",between ").append(startTime);
						sb.append("and ").append(endTime);
						if(null != currentState) {
							sb.append("exception instance is ").append(currentState.getInstanceID());
						}
						logger.warn(sb.toString(), t);
					}
					continue;
				}
			}

		}else {
			if(logger.isInfoEnabled()) { //未取到任何数据
				StringBuilder sb = new StringBuilder();
				sb.append("cannot query any instance state data with ").append(instanceID);
				sb.append(",between ").append(startTime).append(" and ").append(endTime);
				logger.info(sb.toString());
			}
			if(null == preState) { //表示资源状态从最近一次状态变化过后一直维持在一个状态没有变化
				if(logger.isInfoEnabled()) {
					StringBuilder sb = new StringBuilder();
					sb.append("instance state report can't found any data, so setting normal. instance is ");
					sb.append(instanceID);
					sb.append("start from ").append(startTime);
					sb.append(" end up with ").append(endTime);
					logger.info(sb.toString());
				}
				TempAvailableData tempAvailableData = new TempAvailableData(instanceID,0,0,(endTime.getTime() - startTime.getTime()));
				return tempAvailableData;
			}
		}

		if(null != preState) {
			if(InstanceStateEnum.CRITICAL == preState.getState()) {
				numCritical++;
				if(null != preState.getCollectTime())
					timeCritical += (endTime.getTime() - preState.getCollectTime().getTime());
			}
		}
		long timeTotal = endTime.getTime() - startTime.getTime();
		TempAvailableData tempAvailableData = new TempAvailableData(instanceID,timeCritical,numCritical,timeTotal);
		if(logger.isInfoEnabled()){
			StringBuilder sb = new StringBuilder();
			sb.append("instance state report,instance is ");
			sb.append(instanceID);
			sb.append(",timeCritical:").append(timeCritical);
			sb.append(",timeTotal:").append(timeTotal);
			sb.append(",numCritical").append(numCritical);
			logger.info(sb.toString());
		}
		return tempAvailableData;

	}

	private InstanceStateEnum convertInstanceState(InstanceStateEnum instanceStateEnum) {
		if(InstanceStateEnum.CRITICAL_NOTHING == instanceStateEnum || InstanceStateEnum.CRITICAL == instanceStateEnum)
			return InstanceStateEnum.CRITICAL;
		else if(InstanceStateEnum.MONITORED != instanceStateEnum && InstanceStateEnum.NOT_MONITORED != instanceStateEnum
				&& InstanceStateEnum.DELETED != instanceStateEnum)
			return InstanceStateEnum.NORMAL;
		else
			return instanceStateEnum;
	}
	//临时保存相关指标的数据
	class TempAvailableData{
		private long instanceId;
		private long timeCritical = 0L;//故障时间
		private int numCritical = 0;//故障次数
		private long timeTotal = 0L;//总时间

		public TempAvailableData(long instanceId, long timeCritical, int numCritical, long timeTotal) {
			this.instanceId = instanceId;
			this.timeCritical = timeCritical;
			this.numCritical = numCritical;
			this.timeTotal = timeTotal;
		}

		public long getInstanceId() {
			return instanceId;
		}

		public void setInstanceId(long instanceId) {
			this.instanceId = instanceId;
		}

		public long getTimeCritical() {
			return timeCritical;
		}

		public void setTimeCritical(long timeCritical) {
			this.timeCritical = timeCritical;
		}

		public int getNumCritical() {
			return numCritical;
		}

		public void setNumCritical(int numCritical) {
			this.numCritical = numCritical;
		}

		public long getTimeTotal() {
			return timeTotal;
		}

		public void setTimeTotal(long timeTotal) {
			this.timeTotal = timeTotal;
		}
	}

}
