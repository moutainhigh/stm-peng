package com.mainsteam.stm.portal.vm.api;

import java.util.List;
import java.util.Map;



import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
import com.mainsteam.stm.portal.vm.bo.VmResourceTreeBo;



public interface VmReportService {
	
	/**
	 * 根据类型查询虚拟报表
	 * @param pageBo
	 * @return
	 */
//	public VmReportPageBo getMonitorList(VmResourcePageBo pageBo, ILoginUser user);
	
	
	public VmResourceTreeBo selectByInstanceId(long instanceId);
	
	public List<VmResourceTreeBo> selectAllChildrenByInstanceId(long instanceId);
	
	public VmResourceTreeBo selectVCenterByInstanceId(long instanceId);
	
	public VmResourceTreeBo selectClusterByInstanceId(long instanceId);
	
	public VmResourceTreeBo selectHostByInstanceId(long instanceId);
	
	/**
	 * 获取虚拟资源统计信息
	 * @param
	 * @return
	 */
	public Map<String,List<Map<String,String>>> getResourceCountInfoByCategoryId(String categoryId);
	
	/**
	 * 根据当前用户获取虚拟资源报表模型
	 * @param
	 * @return
	 */
	public List<ReportTemplateExpand> getVMReportTemplate(ILoginUser user);
	
	/**
	 * 根据当前用户获取虚拟资源报表模型
	 * @param
	 * @return
	 */
	public List<ReportResourceInstance> getVmResourceByType(String type,Long domainId,ILoginUser user);
	
}
