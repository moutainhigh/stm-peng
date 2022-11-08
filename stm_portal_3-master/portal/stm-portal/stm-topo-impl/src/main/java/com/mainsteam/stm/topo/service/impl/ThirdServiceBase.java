package com.mainsteam.stm.topo.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;


/**
 * <li>【拓扑管理】调用其他模块的接口【基类封装】</li>
 * <li>1.本类只用来注入第三方模块的service接口类,不允许在其他类注入第三方service</li>
 * <li>2.可自己扩展自己的接口封装，但需继承该第三方封装基类</li>
 * <li>3.命名尽量见名识意,如：xx+Service或xx+Api</li>
 * <li>4.一切目的都是为了：方便使用，减小后期维护成本</li>
 * @version  ms.stm
 * @since  2019年12月18日
 * @author zwx
 */
public class ThirdServiceBase {
	//资源api
	@Autowired
	public IResourceApi resourceApi;
	//资源实例
	@Autowired
	public ResourceInstanceService resourceService;
	//能力库服务
	@Autowired
	public CapacityService capacityService;
	//策略服务
	@Autowired
	public ProfileService  profileService;
	//节点
	@Autowired
	public NodeService nodeService;
	//Domain 服务
	@Autowired
	public IDomainApi domainService;
	//模型熟悉服务类
	@Autowired
	public ModulePropService modulePropService;
	//指标服务
	@Autowired
	public MetricDataService metricDataService;
	//资源实例状态服务
	@Autowired
	public InstanceStateService instanceStateService;
	//资源实例发现服务
	@Autowired
	public ResourceInstanceDiscoveryService resourceDiscoverService;
	@Autowired
	public ISearchApi searchService;
	//远程调用服务
	@Autowired
	public OCRPCClient ocrpcClient;
	//指标状态
	@Autowired
	public MetricStateService metricStateService;
	//告警规则
	@Autowired
	public AlarmRuleService alarmRuleService;
	//告警
	@Autowired
	public AlarmService alarmService;
	//系统用户
	@Autowired
	public IUserApi userApi;
	@Resource
	public InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
}
