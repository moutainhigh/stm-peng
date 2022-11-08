package com.mainsteam.stm.portal.business.api;

import java.util.Date;
import java.util.List;


import java.util.Map;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.bo.BizBandwidthCapacityMetric;
import com.mainsteam.stm.portal.business.bo.BizCalculateCapacityMetric;
import com.mainsteam.stm.portal.business.bo.BizCanvasAutoBuildDataBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizDatabaseCapacityMetric;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizMainDataBo;
import com.mainsteam.stm.portal.business.bo.BizMetricDataBo;
import com.mainsteam.stm.portal.business.bo.BizMetricHistoryDataBo;
import com.mainsteam.stm.portal.business.bo.BizMetricHistoryValueBo;
import com.mainsteam.stm.portal.business.bo.BizResourceInstance;
import com.mainsteam.stm.portal.business.bo.BizStatusDefineParameter;
import com.mainsteam.stm.portal.business.bo.BizStoreCapacityMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerMetric;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;

public interface BizMainApi {

	/**
	 * 添加一个业务系统(基本信息)
	 * @param bo
	 * @return
	 */
	public long insertBasicInfo(BizMainBo bo);
	
	/**
	 * 获取一个业务系统的基本信息
	 * @param id
	 * @return
	 */
	public BizMainBo getBasicInfo(long id);
	
	/**
	 * 获取一个业务系统的状态定义
	 * @param id
	 * @return
	 */
	public String getCanvasStatusDefine(long id);
	
	/**
	 * 获取所有业务系统
	 * @param id
	 * @return
	 */
	public List<BizMainBo> getAllBizList(ILoginUser user);
	
	/**
	 * 获取所有创建者及责任人信息
	 * @return
	 */
	public List<BizMainBo> getAllPermissionsInfoList();
	
	/**
	 * 获取分页业务集合(汇总界面)
	 * @return
	 */
	public Page<BizMainBo, Object> getPageListForSummary(ILoginUser user,int startNum,int pageSize);
	
	/**
	 * 获取业务top5响应速度(汇总界面)
	 * @return
	 */
	public List<BizMetricDataBo> getTopFiveResponseTime(ILoginUser user);
	
	/**
	 * 获取业务告警次数(汇总界面)
	 * @return
	 */
	public List<BizMetricDataBo> getBizAlarmCount(ILoginUser user,Date startTime,Date endTime);
	
	/**
	 * 获取分页业务集合运行情况(汇总界面)
	 * @return
	 */
	public List<BizMetricDataBo> getPageListRunInfo(ILoginUser user,Date startTime,Date endTime);
	
	/**
	 * 获取业务计算容量(汇总界面)
	 * @return
	 */
	public Page<BizCalculateCapacityMetric, Object> getCalculateCapacityInfo(ILoginUser user,int startNum,int pageSize);
	
	/**
	 * 获取业务存储容量(汇总界面)
	 * @return
	 */
	public Page<BizStoreCapacityMetric, Object> getStoreCapacityInfo(ILoginUser user,int startNum,int pageSize);
	
	/**
	 * 获取业务数据库容量(汇总界面)
	 * @return
	 */
	public Page<BizDatabaseCapacityMetric, Object> getDatabaseCapacityInfo(ILoginUser user,int startNum,int pageSize);
	
	/**
	 * 获取业务带宽容量(汇总界面)
	 * @return
	 */
	public Page<BizBandwidthCapacityMetric, Object> getBandwidthCapacityInfo(ILoginUser user,int startNum,int pageSize);
	
	/**
	 * 获取业务详情运行情况(详情界面)
	 * @return
	 */
	public BizMainDataBo getRunInfoForDetail(long bizId,Date startTime,Date endTime);
	
	/**
	 * 获取单个业务详情运行情况(绘图tooltip)
	 * @return
	 */
	public BizMainDataBo getRunInfoForTooltip(long bizId);
	
	/**
	 * 获取业务健康度情况(详情界面)
	 * @return
	 */
	public List<BizInstanceNodeBo> getHealthDetail(long bizId);
	
	/**
	 * 获取分页业务集合(列表界面)
	 * @param user 登录用户
	 * @param status 查询状态
	 * @param startTime 指标统计开始时间
	 * @param endTime 指标统计结束时间
	 * @param queryName 查询业务名称
	 * @param startNum 分页开始行数
	 * @param pageSize 分页每页行数
	 * @return
	 */
	public Page<BizMainDataBo, Object> getPageListForGrid(ILoginUser user,int status,Date startTime,Date endTime,
			String queryName, int startNum,int pageSize);
	/**
	 * 
	 * @param user
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param queryName
	 * @param startNum
	 * @param pageSize
	 * @param order 排序字段
	 * @param sort 排序方式
	 * @return
	 */
	public Page<BizMainDataBo, Object> getPageListForGridOrder(ILoginUser user,int status,Date startTime,Date endTime,
			String queryName, int startNum,int pageSize,String order,String sort);
	
	/**
	 * 修改业务状态定义
	 * @param main
	 * @return
	 */
	public boolean updateBizStatusDefine(BizMainBo main);
	
	/**
	 * IP搜索自动构建架构
	 * @return
	 */
	public List<BizResourceInstance> getInstancesByAutoBuild(ILoginUser user,String ip);
	
	/**
	 * 手动构建获取资源
	 */
	public List<BizResourceInstance> getInstancesByManualBuild(ILoginUser user,String searchContent);
	
	/**
	 * 手动构建获取业务系统
	 */
	public List<BizMainBo> getBussinessByManualBuild(ILoginUser user,long bizId,String searchContent);
	
	/**
	 * 根据资源集合自动构建业务图
	 * @param bizId
	 * @param instanceIds
	 * @return
	 */
	public BizCanvasAutoBuildDataBo autoBuildBussiness(long bizId,Long[] instanceIds);
	
	/**
	 * 修改业务的基本信息
	 * @param main
	 * @return
	 */
	public int updateBasicInfo(BizMainBo main,String oldName);
	
	/**
	 * 删除业务系统
	 * @param id
	 * @return
	 */
	public boolean deleteBiz(long[] ids);
	
	/**
	 * 查询指定业务的状态定义可加入参数
	 * @param bizId
	 * @return
	 */
	public List<BizStatusDefineParameter> getBizStatusDefineParameter(long bizId);
	
	/**
	 * 计算业务健康度
	 * @param bizId
	 */
	public void calculateBizHealth(long mainBizId,int type,boolean isAddChildBiz,BizCanvasNodeBo canvasNode,long updateInstanceId);
	
	/**
	 * 删除节点修改自定义规则的表达式
	 */
	public void updateStatusDefineByDeleteNode(List<Long> deleteIds,long bizId,BizCanvasNodeBo firstDeleteNode);
	
	
	/**
	 * 获取指定业务的容量指标信息
	 * @param bizId
	 * @return
	 */
	public List<Object> getCapacityMetric(long bizId);
	
	/**
	 * 获取健康度历史数据
	 * @return
	 */
	public BizMetricHistoryDataBo getHealthHistoryData(long bizId,Date startTime,Date endTime,String timeType);
	
	/**
	 * 获取健康度历史数据为首页关注指标提供数据
	 * @return
	 */
	public List<BizMetricHistoryValueBo> getHealthHistoryDataForHomeMetric(long bizId,Date startTime,Date endTime);
	
	/**
	 * 获取响应速度历史数据
	 * @param bizId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BizMetricHistoryDataBo getResponseTimeHistoryData(long bizId,Date startTime,Date endTime,String timeType);
	
	/**
	 * 获取响应速度历史数据为首页关注指标提供数据
	 * @param bizId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<BizMetricHistoryValueBo> getResponseTimeHistoryDataForHomeMetric(long bizId,Date startTime,Date endTime,MetricSummaryType mtyp);
	
	
	public List<BizMetricHistoryValueBo> getHistoryDataForHomeMetric(long bizId,Date startTime,Date endTime,MetricSummaryType mtyp,String metricId);
	
	/**
	 * 提供报表需要的业务指标
	 * @return
	 */
	public List<BizSerMetric> getBizReportMetrics();
	
	/**
	 * 获取报表数据
	 * @return
	 */
	public List<BizSerReport> getBizReportData(List<Long> ids,List<TimePeriod> timePeriods);
	
	/**
	 * 删除子资源计算业务状态
	 * @param childInstanceId
	 */
	public void deleteChildInstanceChangeBizStatus(List<Long> childInstanceId);
	
	/**
	 * 删除主资源计算业务状态
	 * @param childInstanceId
	 */
	public void calculateBizHealthForDeleteMainInstances(List<Long> instanceIds);
	
	public List<BizMainBo> getAllViewBiz(ILoginUser user);
	
	public List<BizMainBo> getBizListByInstanceId(ILoginUser user,long instanceId);
	
	/**
	 * 判断用户是否为业务系统责任人
	 * @param manangerId
	 * @return
	 */
	public int getBizCountByManagerId(long manangerId);
	
	/**
	 * 资源监控状态变化
	 * @param manangerId
	 * @return
	 */
	public void instanceMonitorChangeBiz(List<Long> mainInstanceId);
	
	/**
	 * 指标监控状态的修改
	 * @param profileId
	 * @param metrics
	 */
	public void metricMonitorOrAlarmChange(long profileId,List<String> metrics);
	
	/**
	 * (获取业务数据)供首页使用
	 * @param user
	 * @param request
	 * @return
	 */
	public List<BizMainBo> getBizsForHome(ILoginUser user);
	
	/**
	 * 为首页提供业务topn排序
	 * @return
	 */
	public Map<String, Object> getMetricTopForHome(ILoginUser user,String resource,String metric,Integer top,String sortMethod);
	
	public Map<String, Object> getFocusMetricDataForHome(Long bizId,String metric);
	
}
