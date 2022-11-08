package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.LogicalVolumeMetricDataApi;
import com.mainsteam.stm.portal.resource.api.VolumeGroupMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.LogicalVolumeMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.LogicalVolumeMetricDataPageBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataPageBo;
import com.mainsteam.stm.portal.resource.web.vo.ProcessParameterVo;
import com.mainsteam.stm.portal.resource.web.vo.VolumeParameterVo;

/**
 * 
 * <li>文件名称: LogicalVolumeMetricDataAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月2日
 * @author   tongpl
 */

@Controller
@RequestMapping("/portal/resourceManager/logicalVolume")
public class LogicalVolumeMetricDataAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(LogicalVolumeMetricDataAction.class);
	
	@Resource
	private LogicalVolumeMetricDataApi logicalVolumeMetricDataService;
	
	
	
	/**
	 * 获取指标实时数据
	 */
	@RequestMapping("/getLogicalVolumeData")
	public JSONObject getLogicalVolumeData(Long mainInstanceId) {
//		LogicalVolumeMetricDataPageBo pageBo = new LogicalVolumeMetricDataPageBo();
//		
//		List<LogicalVolumeMetricDataBo> dataList = new ArrayList<LogicalVolumeMetricDataBo>();
//		LogicalVolumeMetricDataBo data1 = new LogicalVolumeMetricDataBo();
//		data1.setInstanceId(new Long(123));
//		data1.setLogicalType("type1");
//		data1.setLogicalName("logicalVolume1");
//		data1.setLogicalPPs("11s");
//		data1.setLogicalLPs("22s");
//		data1.setLogicalPVs("33s");
//		data1.setLogicalPPsValue("11");
//		data1.setLogicalLPsValue("22");
//		data1.setLogicalPVsValue("33");
//		data1.setLogicalState("yes");
//		
//		LogicalVolumeMetricDataBo data2 = new LogicalVolumeMetricDataBo();
//		data2.setInstanceId(new Long(123));
//		data2.setLogicalType("type1");
//		data2.setLogicalName("logicalVolume1");
//		data2.setLogicalPPs("111s");
//		data2.setLogicalLPs("221s");
//		data2.setLogicalPVs("331s");
//		data2.setLogicalPPsValue("111");
//		data2.setLogicalLPsValue("221");
//		data2.setLogicalPVsValue("331");
//		data2.setLogicalState("yes");
//		
//		dataList.add(data1);
//		dataList.add(data2);
//		
//		pageBo.setLogicalVolumeData(dataList);
//		pageBo.setRowCount(dataList.size());
//		pageBo.setStartRow(0);
//		pageBo.setTotalRecord(dataList.size());
		LogicalVolumeMetricDataPageBo pageBo = logicalVolumeMetricDataService.queryRealTimeMetricDatas(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 获取进程实时数据
	 */
	@RequestMapping("/scanLogicalVolumeData")
	public JSONObject scanLogicalVolumeData(long mainInstanceId,String volumnGroupName) {
//		LogicalVolumeMetricDataPageBo pageBo = new LogicalVolumeMetricDataPageBo();
//		
//		List<LogicalVolumeMetricDataBo> dataList = new ArrayList<LogicalVolumeMetricDataBo>();
//		LogicalVolumeMetricDataBo data1 = new LogicalVolumeMetricDataBo();
//		data1.setInstanceId(new Long(123));
//		data1.setLogicalType("type1");
//		data1.setLogicalName("logicalVolume1");
//		dataList.add(data1);
//		
//		pageBo.setLogicalVolumeData(dataList);
//		pageBo.setRowCount(dataList.size());
//		pageBo.setStartRow(0);
//		pageBo.setTotalRecord(dataList.size());
		LogicalVolumeMetricDataPageBo pageBo = logicalVolumeMetricDataService.scanLogicalVolumeData(mainInstanceId,volumnGroupName);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 删除逻辑卷子资源
	 */
	@RequestMapping("/deleteLogicalInstance")
	public JSONObject deleteLogicalInstance(String instanceIds) {
		
		boolean result = logicalVolumeMetricDataService.deleteLogicalInstance(instanceIds);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 加入监控
	 */
	@RequestMapping("/addLogicalVolumeToMonitor")
	public JSONObject addLogicalVolumeToMonitor(VolumeParameterVo parameter,HttpSession session) {
		//当前用户
		ILoginUser user = getLoginUser(session);
		
		List<LogicalVolumeMetricDataBo> processMetricDataBoList = JSONObject.parseArray(parameter.getVolumeList(), LogicalVolumeMetricDataBo.class);
		
		AddInstanceResult result = logicalVolumeMetricDataService.addLogicalVolumeMonitor(processMetricDataBoList,parameter.getMainInstanceId(),user);
		
		return toSuccess(result);
		
	}
	
}
