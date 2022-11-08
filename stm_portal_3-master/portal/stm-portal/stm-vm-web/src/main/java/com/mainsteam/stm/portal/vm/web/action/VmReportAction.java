package com.mainsteam.stm.portal.vm.web.action;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.export.excel.ExcelHeader;
import com.mainsteam.stm.export.excel.ExcelUtil;
import com.mainsteam.stm.export.excel.ExcelExportUtil;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.bo.DefaultAlarmMetric;
import com.mainsteam.stm.portal.report.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.report.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.service.impl.ReportTemplateImpl;
//import com.mainsteam.stm.portal.vm.api.VMReportTemplateApi;
import com.mainsteam.stm.portal.vm.api.VmReportService;
import com.mainsteam.stm.portal.vm.bo.VmResourceTreeBo;
import com.mainsteam.stm.portal.vm.web.vo.VCenterVo;
import com.mainsteam.stm.portal.vm.web.vo.VirtualClusterVo;
import com.mainsteam.stm.portal.vm.web.vo.VirtualHostVo;
import com.mainsteam.stm.portal.vm.web.vo.VirtualVMVo;
import com.mainsteam.stm.portal.vm.web.vo.VirtualStorageVo;
import com.mainsteam.stm.portal.vm.web.vo.VmConutVo;
import com.mainsteam.stm.portal.vm.web.vo.VmReportPageVo;
import com.mainsteam.stm.portal.vm.web.vo.VmReportTemplateVo;
import com.mainsteam.stm.portal.vm.web.vo.VmReportVo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.util.UnitTransformUtil;
import com.mainsteam.stm.util.unit.UnitResult;

/**
 * 
 * <li>文件名称: VmReportAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月12日
 * @author   tongpl
 */

@Controller
@RequestMapping("/portal/vm/vmReport/")
public class VmReportAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(VmReportAction.class);
	
	@Resource
	private ReportTemplateApi reportTemplateApi;
	
//	@Resource
//	private VMReportTemplateApi vmReportTemplateApi;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ProfileService profileService;
	
	@Resource
	private MetricDataService metricDataService;
	
	@Resource
	private VmReportService vmReportService;
	
	/**
	 * 虚拟报表左菜单
	 * @param
	 * @return
	 */
	@RequestMapping("getAllVmReportForMenu")
	public JSONObject getAllVmReportForMenu(HttpSession session){
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		
		List<ReportTemplateExpand> rtle = vmReportService.getVMReportTemplate(user);
		
		return toSuccess(rtle);
	}
	
	
	
	/**
	 * 获取虚拟资源统计信息
	 * @param
	 * @return
	 */
	@RequestMapping("getVCenterInfo")
	public JSONObject getVCenterInfo(){
		Map<String,List<Map<String,String>>> map = new HashMap<String, List<Map<String,String>>>();
		map.putAll(vmReportService.getResourceCountInfoByCategoryId("VMware"));
		map.putAll(vmReportService.getResourceCountInfoByCategoryId("VMware5.5"));
		map.putAll(vmReportService.getResourceCountInfoByCategoryId("VMware6"));
		map.putAll(vmReportService.getResourceCountInfoByCategoryId("VMware6.5"));
		return toSuccess(map);
	}
	
	
	/**
	 * 获取虚拟化资源类型
	 * @param
	 * @return
	 */
	@RequestMapping("getVmResourceCategory")
	public JSONObject getVmResourceCategory(){
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		
		CategoryDef categoryDef = capacityService.getCategoryById("VM");
		if(null != categoryDef ){
			CategoryDef[] childCategoryDef =categoryDef.getChildCategorys();
			if(null!=childCategoryDef && childCategoryDef.length>0){
				for(CategoryDef child : childCategoryDef){
					
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("id", child.getId());
					map.put("name", child.getName());
					map.put("pid", "Resource");
					map.put("state", "closed");
					
					listMap.add(map);
					
					CategoryDef[] childSecondCategoryDef = child.getChildCategorys();
					if(childSecondCategoryDef != null && childSecondCategoryDef.length > 0){
						for(CategoryDef secondChild : childSecondCategoryDef){
							
							if(secondChild.getId().equals("VCenter") ||
								secondChild.getId().equals("VCenter5.5") ||
								secondChild.getId().equals("VCenter6") ||
								secondChild.getId().equals("VCenter6.5")){
								continue;
							}
							
							Map<String,Object> childMap = new HashMap<String,Object>();
							childMap.put("id", secondChild.getId());
							childMap.put("name", secondChild.getName());
							childMap.put("pid", child.getId());
							childMap.put("state", null);
							
							listMap.add(childMap);
						}
					}
					
				}
			}
		}
		
		return toSuccess(listMap);
	}
	
	/**
	 * 报表添加,资源类型选择
	 * @param
	 * @return
	 */
	@RequestMapping("getVmResourceByType")
	public JSONObject getVmResourceByType(String type,Long domainId,HttpSession session){
		
		List<ReportResourceInstance> riList = new ArrayList<ReportResourceInstance>();
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		riList = vmReportService.getVmResourceByType(type, domainId, user);
		
		if(null!=riList && riList.size()>0){
			for(ReportResourceInstance ri:riList){
				Map<String,Object> map = new HashMap<String,Object>();
				
				map.put("id", ri.getId());
				map.put("showName", ri.getShowName());
				map.put("resourceType", ri.getResourceName());
				map.put("resourceId", ri.getResourceId());
				
				listMap.add(map);
			}
		}
		
		return toSuccess(listMap);
	}
	
	/**
	 * 报表添加,指标列表
	 * @param
	 * @return
	 */
	@RequestMapping("getVmResourceMetricListByType")
	public JSONObject getVmResourceMetricListByType(String type){

		Set<String> idSet = new HashSet<String>();
		if(type.contains(",")){
			for(String id : type.split(",")){
				idSet.add(id);
			}
		}else{
			idSet.add(type);
		}
		
		List<String> ids = new ArrayList<String>(idSet);
		
		List<ReportResourceMetric> defs = new ArrayList<ReportResourceMetric>();
		
		defs = reportTemplateApi.getMetricListByResource(ids,-1,ReportTypeEnum.VM_PERFORMANCE_REPORT.getIndex(),-1);

		return toSuccess(defs);
	}
	
	/**
	 * 获取固定告警指标数据
	 */
	@RequestMapping("/getDefaultAlarmMetricData")
	public JSONObject getDefaultAlarmMetricData() {
		return toSuccess(new DefaultAlarmMetric().getMetricData());
	}
	@RequestMapping(value="exportVm")
	public void exportLog(HttpServletResponse response, HttpServletRequest request,String sheetName) throws IOException{
		ExcelExportUtil<VmConutVo> exportUtil = new ExcelExportUtil<VmConutVo>();
//		Map<String,List<Map<String,String>>> map = new HashMap<String, List<Map<String,String>>>();		
		List<Map<String, String>> vcenter = new ArrayList<Map<String,String>>();
		List<Map<String, String>> virtualcluster = new ArrayList<Map<String,String>>();
		List<Map<String, String>> virtualhost = new ArrayList<Map<String,String>>();
		List<Map<String, String>> virtualstorage = new ArrayList<Map<String,String>>();
		List<Map<String, String>> virtualvm = new ArrayList<Map<String,String>>();
		
		Map<String,List<Map<String,String>>> vmware5_0 = vmReportService.getResourceCountInfoByCategoryId("VMware");
		List<Map<String, String>> vcenter5_0 = vmware5_0.get("VCenter");
		List<Map<String, String>> virtualcluster5_0 = vmware5_0.get("VirtualCluster");
		List<Map<String, String>> virtualhost5_0 = vmware5_0.get("VirtualHost");
		List<Map<String, String>> virtualstorage5_0 = vmware5_0.get("VirtualStorage");
		List<Map<String, String>> virtualvm5_0 = vmware5_0.get("VirtualVM");
		
		Map<String,List<Map<String,String>>> vmware5_5 = vmReportService.getResourceCountInfoByCategoryId("VMware5.5");
		List<Map<String, String>> vcenter5_5 = vmware5_5.get("VCenter5.5");
		List<Map<String, String>> virtualcluster5_5 = vmware5_5.get("VirtualCluster5.5");
		List<Map<String, String>> virtualhost5_5 = vmware5_5.get("VirtualHost5.5");
		List<Map<String, String>> virtualstorage5_5 = vmware5_5.get("VirtualStorage5.5");
		List<Map<String, String>> virtualvm5_5 = vmware5_5.get("VirtualVM5.5");
		
		Map<String,List<Map<String,String>>> vmware6_0 = vmReportService.getResourceCountInfoByCategoryId("VMware6");
		List<Map<String, String>> vcenter6_0 = vmware6_0.get("VCenter6");
		List<Map<String, String>> virtualcluster6_0 = vmware6_0.get("VirtualCluster6");
		List<Map<String, String>> virtualhost6_0 = vmware6_0.get("VirtualHost6");
		List<Map<String, String>> virtualstorage6_0 = vmware6_0.get("VirtualStorage6");
		List<Map<String, String>> virtualvm6_0 = vmware6_0.get("VirtualVM6");
		
		Map<String,List<Map<String,String>>> vmware6_5 = vmReportService.getResourceCountInfoByCategoryId("VMware6.5");
		List<Map<String, String>> vcenter6_5 = vmware6_5.get("VCenter6.5");
		List<Map<String, String>> virtualcluster6_5 = vmware6_5.get("VirtualCluster6.5");
		List<Map<String, String>> virtualhost6_5 = vmware6_5.get("VirtualHost6.5");
		List<Map<String, String>> virtualstorage6_5 = vmware6_5.get("VirtualStorage6.5");
		List<Map<String, String>> virtualvm6_5 = vmware6_5.get("VirtualVM6.5");
		
		vcenter.addAll(vcenter5_0);
		vcenter.addAll(vcenter5_5);
		vcenter.addAll(vcenter6_0);
		vcenter.addAll(vcenter6_5);
		
		virtualcluster.addAll(virtualcluster5_0);
		virtualcluster.addAll(virtualcluster5_5);
		virtualcluster.addAll(virtualcluster6_0);
		virtualcluster.addAll(virtualcluster6_5);
		
		virtualhost.addAll(virtualhost5_0);
		virtualhost.addAll(virtualhost5_5);
		virtualhost.addAll(virtualhost6_0);
		virtualhost.addAll(virtualhost6_5);
		
		virtualstorage.addAll(virtualstorage5_0);
		virtualstorage.addAll(virtualstorage5_5);
		virtualstorage.addAll(virtualstorage6_0);
		virtualstorage.addAll(virtualstorage6_5);
		
		virtualvm.addAll(virtualvm5_0);
		virtualvm.addAll(virtualvm5_5);
		virtualvm.addAll(virtualvm6_0);
		virtualvm.addAll(virtualvm6_5);
		
		List<Map<String,Object>> dataset=getCount(vcenter,virtualcluster,virtualhost,virtualstorage,virtualvm);
//		 for (Map<String, Object> map : dataset) {
////		System.out.println(map);
//	List<Object> list=(List<Object>) map.get("content");
////		List<ExcelHeader> list=	(List<ExcelHeader>) map.get("head");
////		for (ExcelHeader excelHeader : list) {
////			System.out.println(excelHeader.getHeaderName());
////		
////		}
//		System.out.println("-------------");
//	}
		WebUtil.initHttpServletResponse("虚拟资源统计报表.xlsx", response, request);
		
		exportUtil.exportExcel("虚拟资源统计报表", null, dataset, response.getOutputStream());
	}
	public  List<Map<String,Object>> getCount(List<Map<String, String>> VCenter,List<Map<String, String>> VirtualCluster,List<Map<String, String>> VirtualHost,List<Map<String, String>> VirtualStorage ,List<Map<String, String>> VirtualVM){
		  DecimalFormat df = new DecimalFormat("#.00");  
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> centerVomap=new HashMap<String, Object>();
		Map<String,Object> clusterVomap=new HashMap<String, Object>();
		Map<String,Object> hostVomap=new HashMap<String, Object>();
		Map<String,Object> storageVomap=new HashMap<String, Object>();
		Map<String,Object> virtualVMVomap=new HashMap<String, Object>();
		List<VCenterVo> centerVos=new ArrayList<VCenterVo>();
		List<VirtualClusterVo> clusterVos = new ArrayList<VirtualClusterVo>();
		List<VirtualHostVo> hostVos = new ArrayList<VirtualHostVo>();
		List<VirtualVMVo> vmVos = new ArrayList<VirtualVMVo>();
		List<VirtualStorageVo> storageVos = new ArrayList<VirtualStorageVo>();
		List<ExcelHeader> centerVoheaders = new ArrayList<>();
		centerVoheaders.add(new ExcelHeader("vCenter","vCenter"));
		centerVoheaders.add(new ExcelHeader("cluster","cluster(个)"));
		centerVoheaders.add(new ExcelHeader("esxi","ESXi主机(台)"));
		centerVoheaders.add(new ExcelHeader("vMare","VM(台)"));
		centerVoheaders.add(new ExcelHeader("dataStorage","数据存储(个)"));
		centerVoheaders.add(new ExcelHeader("CPUCountGhz","总CPU"));
		centerVoheaders.add(new ExcelHeader("memCount","总内存"));
		centerVoheaders.add(new ExcelHeader("storageCount","总存储"));
		VCenterVo centerCount = new VCenterVo();
		Double cputotle=0.00,Memtotle=0.00,Storage=0.00;
		Integer clustertotle=0,esxitotle=0,dataStoragetotle=0,vMaretotle=0;
		for (Map<String, String> map : VCenter) { 
			VCenterVo centerVo= new VCenterVo();
			centerVo.setCluster(map.get("cluster"));
			clustertotle+=Integer.parseInt(map.get("cluster"));
			if(map.get("cluster").equals("--")){
//				centerCount.setCluster("");
			}else{
				centerCount.setCluster(String.valueOf(clustertotle));
				
			}
			centerVo.setCPUCountGhz(map.get("CPUCountGhz").equals("--")?map.get("CPUCountGhz"): map.get("CPUCountGhz")+map.get("CPUCountGhz_Unit"));
			if(!"--".equals(map.get("CPUCountGhz"))){
				cputotle+= Double.parseDouble(map.get("CPUCountGhz"));
				
				centerCount.setCPUCountGhz(cputotle==0.00?"0.00"+map.get("CPUCountGhz_Unit"):String.valueOf(df.format(cputotle))+map.get("CPUCountGhz_Unit"));
			}else{
//				centerCount.setCPUCountGhz("");
			}
			centerVo.setDataStorage(map.get("dataStorage"));
			dataStoragetotle+=Integer.parseInt(map.get("dataStorage"));
			if(map.get("dataStorage")==null || map.get("dataStorage").equals("--")){
//				centerCount.setDataStorage("");
			}else{
				centerCount.setDataStorage(String.valueOf(dataStoragetotle));
				
			}
			centerVo.setEsxi(map.get("esxi"));
			esxitotle+=Integer.parseInt(map.get("esxi"));
			if(map.get("esxi")==null || map.get("esxi").equals("--")){
//				centerCount.setEsxi("");
			}else{
				centerCount.setEsxi(String.valueOf(esxitotle));
				
			}
			String centerM="--";
			if(!"--".equals(map.get("memCount"))){
				centerM=String.valueOf(UnitTransformUtil.transform(map.get("memCount"), map.get("memCount_Unit")));
			}
			
			centerVo.setMemCount(map.get("memCount").equals("--")?map.get("memCount"):centerM);
			if(!"--".equals(map.get("memCount"))){
				Memtotle+= Double.parseDouble(UnitTransformUtil.transform(map.get("memCount"), map.get("memCount_Unit")).replace("GB", ""));
				
				centerCount.setMemCount(Memtotle==0.00?"0.00GB":String.valueOf(df.format(Memtotle))+"GB");
			}else{
//				centerCount.setMemCount("");
			}
			
			centerVo.setStorageCount(map.get("storageCount").equals("--")?map.get("storageCount"):map.get("storageCount")+map.get("storageCount_Unit"));
			if(!"--".equals(map.get("storageCount"))){
				Storage+= Double.parseDouble(map.get("storageCount"));
				
				centerCount.setStorageCount(Storage==0.00?"0.00"+map.get("storageCount_Unit"):String.valueOf(df.format(Storage))+map.get("storageCount_Unit"));
			}else{
//				centerCount.setStorageCount("");
			}
			centerVo.setvCenter(map.get("vCenter"));
			centerCount.setvCenter("总计");
			centerVo.setvMare(map.get("vMare"));
			vMaretotle+=Integer.parseInt(map.get("vMare"));
			if(map.get("vMare").equals("--")){
//				centerCount.setvMare("");
			}else{
				centerCount.setvMare(String.valueOf(vMaretotle));
				
			}
			centerVos.add(centerVo);
		}
		centerVos.add(centerCount);
		List<ExcelHeader> VirtualClusterheaders = new ArrayList<>();
		VirtualClusterheaders.add(new ExcelHeader("cluster","Cluster"));
		VirtualClusterheaders.add(new ExcelHeader("TotalCPU","总CPU"));
		VirtualClusterheaders.add(new ExcelHeader("TotalMemSize","总内存"));
		VirtualClusterheaders.add(new ExcelHeader("DatastoreSize","总存储"));
		VirtualClusterheaders.add(new ExcelHeader("HostNum","ESXi主机(台)"));
		VirtualClusterheaders.add(new ExcelHeader("VMNum","VM(台)"));
		VirtualClusterheaders.add(new ExcelHeader("HAStatus","HA状态"));
		VirtualClusterheaders.add(new ExcelHeader("DRSStatus","DRS状态"));
		VirtualClusterheaders.add(new ExcelHeader("EVCMode","EVC模式"));
		VirtualClusterheaders.add(new ExcelHeader("vCenter","vCenter"));
		VirtualClusterVo clustercount = new VirtualClusterVo();
		Double cluMem=0.00,cluDatastore=0.00,totalCpu=0.00;;
		Integer cluhost=0,cluvm=0;
		for (Map<String, String> map : VirtualCluster) { 
			VirtualClusterVo clusterVo = new VirtualClusterVo();
			clusterVo.setCluster(map.get("cluster"));
			clustercount.setCluster("总计");
			clusterVo.setTotalMemSize(map.get("TotalMemSize").equals("--")?map.get("TotalMemSize"):map.get("TotalMemSize")+map.get("TotalMemSize_Unit"));
			if(map.get("TotalMemSize")==null || map.get("TotalMemSize").equals("--")){
//				clustercount.setTotalMemSize("");	
			}else{
				cluMem+=Double.parseDouble(map.get("TotalMemSize"));
//				cluMem=0.00;
				clustercount.setTotalMemSize(cluMem==0.00?"0.00"+map.get("TotalMemSize_Unit"):String.valueOf(df.format(cluMem))+map.get("TotalMemSize_Unit"));	
			}
			
			clusterVo.setDRSStatus(map.get("DRSStatus").equals("--")?map.get("DRSStatus"):map.get("DRSStatus")+map.get("DRSStatus_Unit"));
			clustercount.setDRSStatus("");
			clusterVo.setEVCMode(map.get("EVCMode").equals("--")?map.get("EVCMode"):map.get("EVCMode")+map.get("EVCMode_Unit"));
			clustercount.setEVCMode("");
			clusterVo.setHAStatus(map.get("HAStatus").equals("--")?map.get("HAStatus"):map.get("HAStatus")+map.get("HAStatus_Unit"));
			clustercount.setHAStatus("");
			clusterVo.setHostNum(map.get("HostNum").equals("--")?map.get("HostNum"):map.get("HostNum")+map.get("HostNum_Unit"));
			if(map.get("HostNum")==null ||map.get("HostNum").equals("--")){
				clustercount.setHostNum("");
			}else{
				cluhost+=Integer.parseInt(map.get("HostNum"));
				clustercount.setHostNum(String.valueOf(cluhost)+map.get("HostNum_Unit"));
			}
			clusterVo.setTotalCPU(map.get("TotalCPU").equals("--")?map.get("TotalCPU"):map.get("TotalCPU")+map.get("TotalCPU_Unit"));
			
			if(map.get("TotalCPU")==null || map.get("TotalCPU").equals("--")){
//				clustercount.setTotalCPU("");	
			}else{
				totalCpu+=Double.parseDouble(map.get("TotalCPU"));
				
				clustercount.setTotalCPU(totalCpu==0.00?"0.00"+map.get("TotalCPU_Unit"):String.valueOf(df.format(totalCpu))+map.get("TotalCPU_Unit"));	
			}
			clusterVo.setvCenter(map.get("vCenter"));
			clustercount.setvCenter("");
			clusterVo.setVMNum(map.get("VMNum").equals("--")?map.get("VMNum"):map.get("VMNum")+map.get("VMNum_Unit"));
			
			if(map.get("VMNum")==null || map.get("VMNum").equals("--")){
//				clustercount.setVMNum("");
			}else{
				cluvm+=Integer.parseInt(map.get("VMNum"));
				clustercount.setVMNum(String.valueOf(cluvm)+map.get("HostNum_Unit"));
			}
			clusterVo.setDatastoreSize(map.get("DatastoreSize").equals("--")?map.get("DatastoreSize"):map.get("DatastoreSize")+map.get("DatastoreSize_Unit"));
			if(map.get("DatastoreSize")==null || map.get("DatastoreSize").equals("--")){
//				clustercount.setDatastoreSize("");	
			}else{
				cluDatastore+=Double.parseDouble(map.get("DatastoreSize"));
				
				clustercount.setDatastoreSize(cluDatastore==0.00?"0.00"+map.get("DatastoreSize_Unit"):String.valueOf(df.format(cluDatastore))+map.get("DatastoreSize_Unit"));	
			}
			//cluDatastore
			clusterVos.add(clusterVo);
		}
		clusterVos.add(clustercount);
		List<ExcelHeader> hostheaders = new ArrayList<>();
		hostheaders.add(new ExcelHeader("ip","ESXi地址"));
		hostheaders.add(new ExcelHeader("TotalCPU","总CPU"));
		hostheaders.add(new ExcelHeader("TotalMemSize","总内存"));
		hostheaders.add(new ExcelHeader("DatastoreSize","总存储"));
		hostheaders.add(new ExcelHeader("VMNum","VM(台)"));
		hostheaders.add(new ExcelHeader("VMotion","Vmotion状态"));
		hostheaders.add(new ExcelHeader("EVC","EVC模式"));
		hostheaders.add(new ExcelHeader("cluster","Cluster"));
		hostheaders.add(new ExcelHeader("vCenter","vCenter"));
		VirtualHostVo hostcount = new VirtualHostVo();
		Double hostCPU=0.00,hostmem=0.00,hostdatastore=0.00;
		Integer hostvm=0;
		for (Map<String, String> map   : VirtualHost) {
			VirtualHostVo hostVo = new VirtualHostVo();
			hostVo.setCluster(map.get("cluster"));
			hostcount.setCluster("");
			hostVo.setEVC(map.get("EVC"));
			hostcount.setEVC("");
			hostVo.setIp(map.get("ip"));
			hostcount.setIp("总计");
			hostVo.setTotalCPU(map.get("TotalCPU").equals("--")?map.get("TotalCPU"):map.get("TotalCPU")+map.get("TotalCPU_Unit"));
			if(map.get("TotalCPU")==null || map.get("TotalCPU").equals("--")){
//				hostcount.setTotalCPU("");	
			}else{
				hostCPU+=Double.parseDouble(map.get("TotalCPU"));
				
				hostcount.setTotalCPU(hostCPU==0.00?"0.00"+map.get("TotalCPU_Unit"):String.valueOf(df.format(hostCPU))+map.get("TotalCPU_Unit"));	
			}
			
			String hostM="--";
			if(!"--".equals(map.get("hostMem"))){
				hostM=String.valueOf(UnitTransformUtil.transform(map.get("TotalMemSize"), map.get("TotalMemSize_Unit")));
			}
			
			hostVo.setTotalMemSize(map.get("TotalMemSize").equals("--")?map.get("TotalMemSize"):hostM);
			if(map.get("TotalMemSize")==null || map.get("TotalMemSize").equals("--") || map.get("TotalMemSize").indexOf("null")>-1){
//				hostcount.setTotalMemSize("");	
			}else{
				hostmem+=Double.parseDouble(UnitTransformUtil.transform(map.get("TotalMemSize"), map.get("TotalMemSize_Unit")).replace("GB", ""));
				hostcount.setTotalMemSize(hostmem==0.00?"0.00GB":String.valueOf(df.format(hostmem))+"GB");	
			}
			hostVo.setDatastoreSize(map.get("DatastoreSize").equals("--")?map.get("DatastoreSize"):map.get("DatastoreSize")+map.get("DatastoreSize_Unit"));
			if(map.get("DatastoreSize")==null || map.get("DatastoreSize").equals("--")){
//				hostcount.setDatastoreSize("");	
			}else{
				hostdatastore+=Double.parseDouble(map.get("DatastoreSize"));
				
				hostcount.setDatastoreSize(hostdatastore==0.00?"0.00"+map.get("DatastoreSize_Unit"):String.valueOf(df.format(hostdatastore))+map.get("DatastoreSize_Unit"));	
			}
			hostVo.setvCenter(map.get("vCenter"));
			hostcount.setvCenter("");
			hostVo.setVMNum(map.get("VMNum"));
			if(map.get("VMNum")==null || map.get("VMNum").equals("--")){
//				hostcount.setVMNum("");
			}else{
				hostvm+=Integer.parseInt(map.get("VMNum"));
				hostcount.setVMNum(String.valueOf(hostvm));
			}
			hostVo.setVMotion(map.get("VMotion"));
			hostcount.setVMotion("");
			hostVos.add(hostVo);
		}
		hostVos.add(hostcount);
		List<ExcelHeader> VirtualStorageheaders = new ArrayList<>();
		VirtualStorageheaders.add(new ExcelHeader("storageName","存储名称"));
		VirtualStorageheaders.add(new ExcelHeader("HostNum","连接主机数目(个)"));
		VirtualStorageheaders.add(new ExcelHeader("VMNum","虚拟机和模板数(个)"));
		VirtualStorageheaders.add(new ExcelHeader("DataStorageVolume","容量"));
		VirtualStorageheaders.add(new ExcelHeader("DataStorageFreeSpace","可用空间"));
		VirtualStorageVo storagecount = new VirtualStorageVo();
		Double storagespace=0.00,storageVolum=0.00;
		Integer storagevm=0,storagehost=0;
		
		for (Map<String, String> map   :  VirtualStorage) {
			VirtualStorageVo	 storageVo = new VirtualStorageVo();
			storageVo.setStorageName(map.get("storageName"));
			storagecount.setStorageName("总计");
			storageVo.setDataStorageFreeSpace(map.get("DataStorageFreeSpace").equals("--")?map.get("DataStorageFreeSpace"):map.get("DataStorageFreeSpace")+map.get("DataStorageVolume_Unit"));
			if(map.get("DataStorageFreeSpace")==null || map.get("DataStorageFreeSpace").equals("--")){
//				storagecount.setDataStorageFreeSpace("");	
			}else{
				storagespace+=Double.parseDouble(map.get("DataStorageFreeSpace"));
				
				storagecount.setDataStorageFreeSpace(storagespace==0.00?"0.00"+map.get("DataStorageVolume_Unit"):String.valueOf(df.format(storagespace))+map.get("DataStorageVolume_Unit"));	
			}
			storageVo.setDataStorageVolume(map.get("DataStorageVolume").equals("--")?map.get("DataStorageVolume"):map.get("DataStorageVolume")+map.get("DataStorageFreeSpace_Unit"));
			
			if(map.get("DataStorageVolume")==null || map.get("DataStorageVolume").equals("--")){
//				storagecount.setDataStorageVolume("");	
			}else{
				storageVolum+=Double.parseDouble(map.get("DataStorageVolume"));
				
				storagecount.setDataStorageVolume(storageVolum==0.00?"0.00"+map.get("DataStorageFreeSpace_Unit"):String.valueOf(df.format(storageVolum))+map.get("DataStorageFreeSpace_Unit"));	
			}
			storageVo.setVMNum(map.get("VMNum"));
			if(map.get("VMNum")==null || map.get("VMNum").equals("--")){
//				storagecount.setVMNum("");
			}else{
				storagehost+=Integer.parseInt(map.get("VMNum"));
				storagecount.setVMNum(String.valueOf(storagehost));
			}
			storageVo.setHostNum(map.get("HostNum"));
			if(map.get("HostNum")==null || map.get("HostNum").equals("--")){
//				storagecount.setHostNum("");
			}else{
				storagevm+=Integer.parseInt(map.get("HostNum"));
				storagecount.setHostNum(String.valueOf(storagevm));
			}
			storageVos.add(storageVo);
		}
		storageVos.add(storagecount);
		List<ExcelHeader> vmheaders = new ArrayList<>();
	
		
		vmheaders.add(new ExcelHeader("vMareName","VM名称"));
		vmheaders.add(new ExcelHeader("osVersion","客户机操作系统"));
		vmheaders.add(new ExcelHeader("DiskAssignedSpace","占用空间"));
		vmheaders.add(new ExcelHeader("cpuNum","CPU数(个)"));
		vmheaders.add(new ExcelHeader("MEMVMSize","内存大小"));
		vmheaders.add(new ExcelHeader("hostPC","主机"));
		vmheaders.add(new ExcelHeader("vCenter","vCenter"));
		VirtualVMVo vmVocount = new VirtualVMVo();
		Double vmspace=0.00,vmMem=0.00;
		int vmcpu=0;
		for (Map<String, String> map   : VirtualVM) {
			VirtualVMVo vmVo = new VirtualVMVo();
			vmVo.setDiskAssignedSpace(map.get("DiskAssignedSpace").equals("--")?map.get("DiskAssignedSpace"):map.get("DiskAssignedSpace")+map.get("DiskAssignedSpace_Unit"));
			if(map.get("DiskAssignedSpace").equals("--")){
//				vmVocount.setDiskAssignedSpace("");	
			}else{
				vmspace+=Double.parseDouble(map.get("DiskAssignedSpace"));
				
				vmVocount.setDiskAssignedSpace(vmspace==0.00?"0.00"+map.get("DiskAssignedSpace_Unit"):String.valueOf(df.format(vmspace))+map.get("DiskAssignedSpace_Unit"));	
			}
			vmVo.setCpuNum(map.get("cpuNum"));
			if(map.get("cpuNum")==null || map.get("cpuNum").equals("--")){
//				vmVo.setCpuNum("");
			}else{
				vmcpu+=Integer.parseInt(map.get("cpuNum"));
				vmVocount.setCpuNum(String.valueOf(vmcpu));
			}
//			vmVo.setHostCPUGhz(map.get("hostCPUGhz").equals("--")?map.get("hostCPUGhz"):map.get("hostCPUGhz")+map.get("hostCPUGhz_Unit"));
//			if(map.get("hostCPUGhz").equals("--")){
//				vmVocount.setHostCPUGhz("");	
//			}else{
//				vmcpu+=Double.parseDouble(map.get("hostCPUGhz"));
//				vmVocount.setHostCPUGhz(String.valueOf(df.format(vmcpu))+map.get("hostCPUGhz_Unit"));	
//			}
			//UnitTransformUtil
			String hostM="--";
					if(!"--".equals(map.get("MEMVMSize"))){
						hostM=String.valueOf(UnitTransformUtil.transform(map.get("MEMVMSize"), map.get("MEMVMSize_Unit")));
					}
			vmVo.setMEMVMSize((map.get("MEMVMSize")==null || map.get("MEMVMSize").equals("--")) ? map.get("MEMVMSize"):hostM);
			if(map.get("MEMVMSize")==null || map.get("MEMVMSize").equals("--")){
//				vmVocount.setMEMVMSize("");	
			}else{
				if(Double.parseDouble(map.get("MEMVMSize"))>=1024D){
					
					vmMem+=Double.parseDouble(UnitTransformUtil.transform(map.get("MEMVMSize"), map.get("MEMVMSize_Unit")).replace("GB", ""));
				}else{
				//	vmMem+=Double.parseDouble(UnitTransformUtil.transform(map.get("MEMVMSize"), map.get("MEMVMSize_Unit")).replace("MB", ""));
				}
				
				vmVocount.setMEMVMSize(vmMem==0.00?"0.00GB":String.valueOf(df.format(vmMem))+"GB");	
			}
			vmVo.setHostPC(map.get("hostPC"));
			vmVocount.setHostPC("");
			vmVo.setOsVersion(map.get("osVersion"));
			vmVocount.setOsVersion("");
			vmVo.setvCenter(map.get("vCenter"));
			vmVocount.setvCenter("");
			vmVo.setvMareName(map.get("vMareName"));
			vmVocount.setvMareName("总计");
			vmVos.add(vmVo);
			
		
		}
		vmVos.add(vmVocount);
		
		centerVomap.put("sheet", "vCenter-报表");
		centerVomap.put("content", centerVos);
		centerVomap.put("head", centerVoheaders);
		centerVomap.put("vo", VCenterVo.class);
		
		clusterVomap.put("sheet", "Cluster-报表");
		clusterVomap.put("content", clusterVos);
		clusterVomap.put("head", VirtualClusterheaders);
		clusterVomap.put("vo", VirtualClusterVo.class);
		
		hostVomap.put("sheet", "ESXi-报表");
		hostVomap.put("content", hostVos);
		hostVomap.put("head", hostheaders);
		hostVomap.put("vo", VirtualHostVo.class);
		
		storageVomap.put("sheet", "数据存储-报表");
		storageVomap.put("content", storageVos);
		storageVomap.put("head", VirtualStorageheaders);
		storageVomap.put("vo", VirtualVMVo.class);
		
		virtualVMVomap.put("sheet", "VM-报表");
		virtualVMVomap.put("content", vmVos);
		virtualVMVomap.put("head", vmheaders);
		virtualVMVomap.put("vo", VirtualStorageVo.class);
		
		list.add(centerVomap);
		list.add(clusterVomap);
		list.add(hostVomap);
		list.add(virtualVMVomap);
		list.add(storageVomap);
//		list.add(conutVo);
		return list;
		
	}
}
