package com.mainsteam.stm.portal.vm.api;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.vm.bo.VmResourcePageBo;

public interface VmResourceListService {

	/**
	 * 获取已监控资源列表
	 * @param pageBo
	 * @return
	 */
	public VmResourcePageBo getMonitorList(VmResourcePageBo pageBo, ILoginUser user);
	
	/**
	 * 获取未监控资源列表
	 * @param pageBo
	 * @return
	 */
	public VmResourcePageBo getUnMonitorList(VmResourcePageBo pageBo, ILoginUser user);
	
	/**
	 * 删除虚拟化资源
	 * @param resourceMonitorIds
	 * @return
	 */
	public boolean deleteVmResources(long[] resourceMonitorIds) throws Exception;
	
	public boolean deleteResourcePools(String[] uuids) ;
	
	/**
	 * 获取虚拟化资源池结构数据
	 * @param pageBo
	 * @return
	 */
	public VmResourcePageBo getResourcePoolList(VmResourcePageBo pageBo, ILoginUser user);
	
	/**
	 * 获取虚拟化资源池结构数据
	 * @param pageBo
	 * @return
	 */
	public VmResourcePageBo getResourcePoolVMByPage(VmResourcePageBo pageBo);
	
}
