package com.mainsteam.stm.portal.vm.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryListPageBo;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryParaBo;

public interface DiscoveryVmService {
	/**
	 * 自动发现
	 * @param domain
	 * @param dcs
	 * @param discoveryType
	 * @param ip
	 * @param userName
	 * @param password
	 * @param user
	 */
	public Map<String, Object> autoDiscovery(VmDiscoveryParaBo dParaBo, ILoginUser user);
	
	/**
	 * 把自动发现的设备加入监控
	 * @param user
	 * @param instanceIds
	 * @return
	 */
	public boolean addMoniterAutoDiscoveryVm(ILoginUser user, List<Long> instanceIds);
	
	/**
	 * 把重新发现的设备加入监控,取消监控
	 * @param user
	 * @param instanceIds
	 * @return
	 */
	public boolean addMoniterReDiscoveryVm(ILoginUser user, HashSet<Long> instanceIds , HashSet<Long> instanceIdsUnchecked);
	
	/**
	 * 补充发现所需要的vmresource
	 * @param user
	 * @return
	 */
	public List<Map<String, String>> suppleDiscoveryVmResources(ILoginUser user);
	
	/**
	 * 补充发现
	 * @param instanceId
	 * @param user
	 * @return
	 */
	public Map<String, Object> suppleDiscovery(String instanceId, ILoginUser user);
	
	/**
	 * 把补充发现的设备加入监控
	 * @param user
	 * @param instanceIds
	 * @return
	 */
	public boolean addMoniterSuppleDiscoveryVm(ILoginUser user, List<Long> instanceIds);
	
	/**
	 * 修改发现属性
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	public int updateDiscoverParamterVm(Map paramter, long instanceId);
	
	/**
	 * 获取发现资源列表
	 * @param pageBo
	 * @param user
	 * @return
	 */
	public VmDiscoveryListPageBo getDiscoveryList(VmDiscoveryListPageBo pageBo, ILoginUser user);

	/**
	 * 测试发现
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	public int testDiscoverVm(Map paramter, long instanceId);
	
	/**
	 * 重新发现
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	public int reDiscoveryVm(Map paramter, long instanceId);
	
	/**
	 * 重新发现treeResult
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	public Map<String, Object> reDiscoveryTreeResult(VmDiscoveryParaBo dParaBo, ILoginUser user);
	public void autoRerfreshVmResourceTest(VmDiscoveryParaBo dParaBo);
	
	/**
	 * 自动重新发现接口
	 * @param paramter
	 * @param instanceId
	 * @return
	 */
	public void autoRerfreshVmResource(ResourceInstanceDiscoveryParameter discoverParameter , DiscoverResourceIntanceResult rir);
	
}
