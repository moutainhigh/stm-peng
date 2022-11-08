package com.mainsteam.stm.portal.resource.web.action;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.IMetricChartsDataApi;
import com.mainsteam.stm.portal.resource.bo.HighChartsDataBo;
import com.mainsteam.stm.portal.resource.web.vo.HighChartsDataVo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;

/**
 * <li>文件名称: MetricChartsDataAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 发现资源相关的操作</li>
 * <li>其他说明:</li>
 * 
 * @version ms.stm
 * @since 2019年9月12日
 * @author tpl
 */
@Controller
@RequestMapping("/portal/resource/metricChartsData")
public class MetricChartsDataAction extends BaseAction {
	private static Logger logger = Logger.getLogger(MetricChartsDataAction.class);
	@Resource
	private IMetricChartsDataApi metricChartsDataApi;
	
	/**
	 * 获取图表数据
	 * 
	 * @return
	 */
	@RequestMapping("/getMetricChartsData")
	public JSONObject getMetricChartsData(HighChartsDataVo highChartsDataVo){
		
		HighChartsDataBo hcdBo  = metricChartsDataApi.getHighChartsData(highChartsDataVo.getQueryTimeType(),highChartsDataVo.getHighChartsQueryNum(),highChartsDataVo.getInstanceId(),highChartsDataVo.getMetricId()[0]);
		
		highChartsDataVo.setDataDouble(hcdBo.getDataDouble());
		highChartsDataVo.setxAxis(hcdBo.getxAxis());
		highChartsDataVo.setMetricUnitName(hcdBo.getMetricUnitName());
		highChartsDataVo.setDataStrArr(hcdBo.getDataStrArr()); 
		highChartsDataVo.setxAxisFull(hcdBo.getxAxisFull());
		highChartsDataVo.setDateStart(hcdBo.getDateStart());
		highChartsDataVo.setMinTimeType(hcdBo.getMinTimeType());
		highChartsDataVo.setDataDoubleStr(hcdBo.getDataDoubleStr());
		
		return toSuccess(highChartsDataVo);
    }
	
	/**
	 * 获取用户选择的时间段内图表数据
	 * 
	 * @return
	 */
	@RequestMapping("/getSpecialMetricChartsData")
	public JSONObject getSpecialMetricChartsData(HighChartsDataVo highChartsDataVo){
		
        HighChartsDataBo hcdBo  = metricChartsDataApi.getSpecialMetricChartsData(highChartsDataVo.getInstanceId(),highChartsDataVo.getMetricId(), highChartsDataVo.getDateStart(), highChartsDataVo.getDateEnd(),highChartsDataVo.getMetricDataType());
		
		highChartsDataVo.setDataDouble(hcdBo.getDataDouble());
		highChartsDataVo.setDataStrArr(hcdBo.getDataStrArr()); 
		highChartsDataVo.setxAxis(hcdBo.getxAxis());
		highChartsDataVo.setxAxisFull(hcdBo.getxAxisFull());
		highChartsDataVo.setMetricUnitName(hcdBo.getMetricUnitName());
		highChartsDataVo.setMinTimeType(hcdBo.getMinTimeType());
		highChartsDataVo.setDataDoubleStr(hcdBo.getDataDoubleStr());
		highChartsDataVo.setDataMap(hcdBo.getDataMap());
		
		return toSuccess(highChartsDataVo);
	}
	
	/**
	 * 获取用户选择的时间段内图表数据
	 * 
	 * @return
	 */
	@RequestMapping("/getSpecialMetricData")
	public JSONObject getSpecialMetricData(HighChartsDataVo highChartsDataVo){
		
        HighChartsDataBo hcdBo  = metricChartsDataApi.getSpecialMetricChartData(highChartsDataVo.getInstanceId(),highChartsDataVo.getMetricId(), highChartsDataVo.getDateStart(), highChartsDataVo.getDateEnd(),highChartsDataVo.getMetricDataType());
		
		highChartsDataVo.setDataDouble(hcdBo.getDataDouble());
		highChartsDataVo.setDataStrArr(hcdBo.getDataStrArr()); 
		highChartsDataVo.setxAxis(hcdBo.getxAxis());
		highChartsDataVo.setxAxisFull(hcdBo.getxAxisFull());
		highChartsDataVo.setMetricUnitName(hcdBo.getMetricUnitName());
		highChartsDataVo.setMinTimeType(hcdBo.getMinTimeType());
		highChartsDataVo.setDataDoubleStr(hcdBo.getDataDoubleStr());
		highChartsDataVo.setDataMap(hcdBo.getDataMap());
		
		return toSuccess(highChartsDataVo);
	}
	
	/**
	 * 获取用户选择的时间段内图表数据
	 * 
	 * @return
	 */
	@RequestMapping("/getMetricRandomTimeChartsData")
	public JSONObject getMetricRandomTimeChartsData(HighChartsDataVo highChartsDataVo){
		
        HighChartsDataBo hcdBo  = metricChartsDataApi.getHighChartsDataByTime(highChartsDataVo.getInstanceId(),highChartsDataVo.getMetricId()[0], highChartsDataVo.getDateStart(), highChartsDataVo.getDateEnd());
		
		highChartsDataVo.setDataDouble(hcdBo.getDataDouble());
		highChartsDataVo.setDataStrArr(hcdBo.getDataStrArr()); 
		highChartsDataVo.setxAxis(hcdBo.getxAxis());
		highChartsDataVo.setxAxisFull(hcdBo.getxAxisFull());
		highChartsDataVo.setMetricUnitName(hcdBo.getMetricUnitName());
		highChartsDataVo.setMinTimeType(hcdBo.getMinTimeType());
		highChartsDataVo.setDataDoubleStr(hcdBo.getDataDoubleStr());
		
		return toSuccess(highChartsDataVo);
	}
	
}
