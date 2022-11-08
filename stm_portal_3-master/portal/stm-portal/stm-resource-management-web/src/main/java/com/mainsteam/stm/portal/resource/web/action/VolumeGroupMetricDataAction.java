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
import com.mainsteam.stm.portal.resource.api.VolumeGroupMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.VolumeGroupMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.VolumeGroupMetricDataPageBo;
import com.mainsteam.stm.portal.resource.web.vo.VolumeParameterVo;

/**
 * 
 * <li>文件名称: VolumeGroupMetricDataAction.java</li>
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
@RequestMapping("/portal/resourceManager/volumeGroup")
public class VolumeGroupMetricDataAction extends BaseAction {

	private static final Log logger = LogFactory.getLog(VolumeGroupMetricDataAction.class);
	
	@Resource
	private VolumeGroupMetricDataApi volumeGroupMetricDataService;
	
	/**
	 * 获取指标实时数据
	 */
	@RequestMapping("/getVolumeGroupData")
	public JSONObject getLogicalVolumeData(Long mainInstanceId) {
//		VolumeGroupMetricDataPageBo pageBo = new VolumeGroupMetricDataPageBo();
//		List<VolumeGroupMetricDataBo> list = new ArrayList<VolumeGroupMetricDataBo>();
//		VolumeGroupMetricDataBo bo = new VolumeGroupMetricDataBo();
//		bo.setInstanceId(new Long(321));
//		bo.setVolumeGroupName("321");
//		bo.setLvsNumber("11ge");
//		bo.setLvsNumberValue("11");
//		bo.setPvsNumber("22ge");
//		bo.setPvsNumberValue("22");
//		bo.setPpSize("33ge");
//		bo.setPpSizeValue("33");
//		bo.setTotalPpSize("44ge");
//		bo.setTotalPpSizeValue("44");
//		bo.setVolumeGroupState("yes");
//		
//		VolumeGroupMetricDataBo bo1 = new VolumeGroupMetricDataBo();
//		bo1.setInstanceId(new Long(4321));
//		bo1.setVolumeGroupName("4321");
//		bo1.setLvsNumber("111ge");
//		bo1.setLvsNumberValue("111");
//		bo1.setPvsNumber("222ge");
//		bo1.setPvsNumberValue("222");
//		bo1.setPpSize("333ge");
//		bo1.setPpSizeValue("333");
//		bo1.setTotalPpSize("444ge");
//		bo1.setTotalPpSizeValue("444");
//		bo1.setVolumeGroupState("yes");
//		
//		list.add(bo);
//		list.add(bo1);
//		pageBo.setVolumeGroupData(list);
//		pageBo.setRowCount(list.size());
//		pageBo.setStartRow(0);
//		pageBo.setTotalRecord(list.size());
		
		VolumeGroupMetricDataPageBo pageBo = volumeGroupMetricDataService.queryRealTimeMetricDatas(mainInstanceId);

		return toSuccess(pageBo);
		
	}
	
	/**
	 * 获取进程实时数据
	 */
	@RequestMapping("/scanVolumeGroupData")
	public JSONObject scanLogicalVolumeData(long mainInstanceId) {
//		VolumeGroupMetricDataPageBo pageBo = new VolumeGroupMetricDataPageBo();
//		List<VolumeGroupMetricDataBo> list = new ArrayList<VolumeGroupMetricDataBo>();
//		VolumeGroupMetricDataBo bo = new VolumeGroupMetricDataBo();
//		bo.setInstanceId(new Long(321));
//		bo.setVolumeGroupName("321");
//		
//		VolumeGroupMetricDataBo bo1 = new VolumeGroupMetricDataBo();
//		bo1.setInstanceId(new Long(4321));
//		bo1.setVolumeGroupName("4321");
//		
//		list.add(bo);
//		list.add(bo1);
//		pageBo.setVolumeGroupData(list);
//		pageBo.setRowCount(list.size());
//		pageBo.setStartRow(0);
//		pageBo.setTotalRecord(list.size());
		
		VolumeGroupMetricDataPageBo pageBo = volumeGroupMetricDataService.scanVolumeGroupData(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 删除进程子资源
	 */
	@RequestMapping("/deleteVolumeGroupInstance")
	public JSONObject deleteVolumeGroupInstance(String instanceIds) {
		
		boolean result = volumeGroupMetricDataService.deleteVolumeGroupInstance(instanceIds);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 加入监控
	 */
	@RequestMapping("/addVolumeGroupToMonitor")
	public JSONObject addLogicalVolumeToMonitor(VolumeParameterVo parameter,HttpSession session) {
		//当前用户
		ILoginUser user = getLoginUser(session);
		
		List<VolumeGroupMetricDataBo> volumeMetricDataBoList = JSONObject.parseArray(parameter.getVolumeList(), VolumeGroupMetricDataBo.class);
		
		AddInstanceResult result = volumeGroupMetricDataService.addVolumeGroupMonitor(volumeMetricDataBoList,parameter.getMainInstanceId(),user);
		
		return toSuccess(result);
		
	}
	
}
