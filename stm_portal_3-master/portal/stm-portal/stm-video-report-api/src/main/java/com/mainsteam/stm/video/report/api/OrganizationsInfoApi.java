package com.mainsteam.stm.video.report.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.video.report.bo.Organizayions;
import com.mainsteam.stm.video.report.bo.VedioOnlineRate;
import com.mainsteam.stm.video.report.bo.zTreeVo;
 
public interface OrganizationsInfoApi {
	
	public List<Organizayions> getAllOrg() throws InstancelibException;

	List<ResourceInstance> queryAllOrg() throws InstancelibException, MetricExecutorException;
	public List<Organizayions> getAllOrgByLevel(Integer level);

	
	public List<Map<String, Object>> getReportBySelLevel(String id,String level);

	public Map<String, Object>  getAnalysisReportBy(String id,String level);
	public List<VedioOnlineRate> getVedioOnlineRate(zTreeVo tree,zTreeVo rootZtree, String startTime, String endTime);
	
	public List<VedioOnlineRate> getIntactRate(zTreeVo tree,zTreeVo rootZtree, String startTime, String endTime);
	public Map<String, Object> getVedioReportBy4Metric(zTreeVo tree, String id) ;
	
	public List<VedioOnlineRate> getFaultReport(zTreeVo tree,zTreeVo rootZtree, String startTime, String endTime);
	
	public void exportReport(String reportType, String exportType, String svgCode, List<VedioOnlineRate> list,
			String filePath,HttpServletResponse response, String orgName, String sTime, String eTime);
	
    public List<Map<String, Object>> getHomeReportByTime(zTreeVo tree);
    
    public List<TableData> getInspectionReport(zTreeVo tree,zTreeVo rootZtree, String startTime,String endTime);
}
