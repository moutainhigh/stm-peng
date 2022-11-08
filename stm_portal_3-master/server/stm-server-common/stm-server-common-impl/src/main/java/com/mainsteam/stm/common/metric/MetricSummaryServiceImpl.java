package com.mainsteam.stm.common.metric;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.common.metric.dao.MetricSummaryDAO;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;

public class MetricSummaryServiceImpl implements MetricSummaryService {

	private MetricSummaryDAO metricSummaryDAO;
	
	public void setMetricSummaryDAO(MetricSummaryDAO metricSummaryDAO) {
		this.metricSummaryDAO = metricSummaryDAO;
	}

	@Override
	public List<List<MetricSummaryData>> queryMetricSummary(List<MetricSummaryQuery> queries) {
		List<List<MetricSummaryData>> list=new ArrayList<List<MetricSummaryData>>(queries.size());
		for(MetricSummaryQuery q:queries){
			list.add( queryMetricSummary(q));
		}
		return list;
	}
	

	@Override
	public List<MetricSummaryData> queryMetricSummary(MetricSummaryQuery query) {
		List<MetricSummaryData> list= metricSummaryDAO.query(query);
		if(list==null ||list.size()<1){
			list=new ArrayList<MetricSummaryData>();
			MetricSummaryType type=MetricSummaryType.valueOf(query.getSummaryType());
			if(type!=null && type.next()!=null){
				MetricSummaryQuery query2=new MetricSummaryQuery();
				BeanUtils.copyProperties(query, query2);
				query2.setSummaryType(type.next());
				List<MetricSummaryData> list2=queryMetricSummary(query2);
				query2.setStartTime(type.next().getPrePeriodStart(query.getEndTime()));
				query2.setEndTime(type.next().getPrePeriodEnd(query.getEndTime()));
				if(list2!=null && list2.size()>0){
					MetricSummaryData data=new MetricSummaryData();
					data.setInstanceId(query.getInstanceID());
					data.setMetricId(query.getMetricID());
					data.setStartTime(query2.getStartTime());
					data.setEndTime(query.getEndTime());
					float fd=0;
					for(MetricSummaryData data2:list2){
						fd+=data2.getMetricData();
					}
					data.setMetricData(fd);
					list.add(data);
				}
			}
		}
		return list;
	}

	@Override
	public List<MetricSummaryData> queryCustomMetricSummary(MetricSummaryQuery query) {
		return metricSummaryDAO.queryCustomMetricSummary(query);
	}
}
