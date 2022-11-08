package com.mainsteam.stm.home.workbench.business.web.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.home.workbench.business.web.vo.Business;
import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.InstanceRelationService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.Instance;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.CategoryBo;

/**
 * 
 * <li>文件名称: HomeBusinessAction</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有:版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年10月27日 下午5:33:27
 * @author xiaolei
 */
@Controller
@RequestMapping("/home/business")
public class HomeBusinessAction extends BaseAction {

	@Autowired
	private CompositeInstanceService compositeInstanceService;

	@Autowired
	private ResourceInstanceService resourceInstanceService;

	@Autowired
	private InstanceRelationService instanceRelationService;

	@Autowired
	private IResourceApi resourceApi;

	@Autowired
	private CapacityService capacityService;

	@RequestMapping("/getBusiness")
	public JSONObject getBusiness(long[] ids) {
		Page<Business, Business> businessPage = new Page<Business, Business>();
		List<CompositeInstance> compositeInstancesList = new ArrayList<CompositeInstance>();
		List<Business> businessesList = new ArrayList<Business>();
		// 获取所有资源种类
		CategoryBo allCategorys = resourceApi.getTreeCategory();
		try {
			// 得到所有的业务
			compositeInstancesList = compositeInstanceService
					.getCompositeInstanceByInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
			for (CompositeInstance compositeInstance : compositeInstancesList) {
				if (businessesList.size() == 6) {
					break;
				}
				if (ids != null) {
					for (int i = 0; i < ids.length; i++) {
						if (compositeInstance.getId() == ids[i]) {
							getBusinessStatistics(businessesList,
									allCategorys.getChildren(), compositeInstance);
						}
					}
				} else {
					getBusinessStatistics(businessesList,
							allCategorys.getChildren(), compositeInstance);
				}
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		businessPage.setDatas(businessesList);
		return toSuccess(businessPage);
	}
	/**
	 * 
	* @Title: getBusinessStatistics
	* @Description: 返回业务统计结果
	* @param businessesList
	* @param resourceCategoryBosList
	* @param compositeInstance  void
	* @throws
	 */
	private void getBusinessStatistics(List<Business> businessesList,
			CategoryBo [] categorys,
			CompositeInstance compositeInstance) {
		List<Instance> instancesList;
		// 主机个数
		int hostCount = 0;
		// 网络设备个数
		int networkDeviceCount = 0;
		// 数据库个数
		int databaseCount = 0;
		// 中间件个数
		int middlewareCount = 0;
		String businessName = compositeInstance.getName();
		long businessId = compositeInstance.getId();
		Business business = new Business();
		try {
			// 根据业务ID获取资源实例集合
			instancesList = instanceRelationService
					.getInstaceCollectPOsByInstanceId(compositeInstance.getId());
			if (instancesList != null && !instancesList.isEmpty()) {
				for (Instance instance : instancesList) {
					if (instance instanceof ResourceInstance) {
						// 查询出来的子资源
						ResourceInstance reInstance = (ResourceInstance) instance;
						// 判断出来的子资源属于哪个主资源
						String mainResourceId = "";
						// 是否跳出循环
						boolean isBreak = false;
						// 主资源
						for (CategoryBo resourceCategoryBo : categorys) {
							CategoryBo[] childList = resourceCategoryBo.getChildren();
//							List<ResourceCategoryBo> childList = resourceCategoryBo
//									.getChildCategorys();
							if (childList != null && childList.length>0) {
								// 一级子资源
								for (CategoryBo childCategoryBo : childList) {
									// 根据子资源判断属于哪个主资源
									if (reInstance.getCategoryId().equals(
											childCategoryBo.getId())) {
										mainResourceId = resourceCategoryBo
												.getId();
										isBreak = true;
										break;
									}
								}
							}
							if (isBreak) {
								break;
							}
						}
						switch (mainResourceId) {
						case "Host":
							hostCount++;
							break;
						case "NetworkDevice":
							networkDeviceCount++;
							break;
						case "Database":
							databaseCount++;
							break;
						case "Middleware":
							middlewareCount++;
							break;
						default:
							break;
						}
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		business.setId(businessId);
		business.setBusinessName(businessName);
		business.setHostCount(hostCount);
		business.setNetworkEquipmentCount(networkDeviceCount);
		business.setDatabaseCount(databaseCount);
		business.setMiddlewareCount(middlewareCount);
		businessesList.add(business);
	}
}
