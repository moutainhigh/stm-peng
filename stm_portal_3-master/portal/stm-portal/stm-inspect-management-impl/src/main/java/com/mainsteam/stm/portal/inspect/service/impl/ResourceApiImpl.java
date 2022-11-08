package com.mainsteam.stm.portal.inspect.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.inspect.api.IResourceApi;
import com.mainsteam.stm.portal.inspect.bo.IndexModel;
import com.mainsteam.stm.portal.inspect.bo.ModelTree;
import com.mainsteam.stm.portal.inspect.bo.Option;
import com.mainsteam.stm.portal.inspect.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.inspect.bo.ResourceCategoryTree;
import com.mainsteam.stm.portal.inspect.bo.ResourceInspect;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;

@Service("resourceApi")
public class ResourceApiImpl implements IResourceApi {

	@Autowired
	private IUserApi userApi;

	@Autowired
	private CapacityService capacityService;

	@Autowired
	private BizMainApi bizMainApi;

	@Resource
	private com.mainsteam.stm.system.resource.api.IResourceApi stm_system_resourceApi;

	@Autowired
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;

	private static Logger logger = Logger.getLogger(ResourceApiImpl.class);

	@Override
	public List<Option> getUser(long domainId) {
		Page<User, UserConditionVo> page = new Page<>();
		page.setStartRow(0);
		page.setRowCount(Long.MAX_VALUE);
		page.setCondition(new UserConditionVo());
		page.getCondition().setDomainId(new Long[] { domainId });
		page.getCondition().setUserType(1);// 1普通用户
		this.userApi.selectByPage(page);
		List<Option> data = new ArrayList<>();
		if (page.getRows() != null) {
			for (User u : page.getRows()) {
				Option o = new Option();
				data.add(o);
				o.setId(u.getId() + "");
				o.setName(u.getName());
			}
		}
		return data;
	}

	@Override
	public List<Option> getResources(long userId) {
		List<UserResourceRel> list = this.userApi.getUserResources(userId);
		List<Option> data = new ArrayList<>();
		if (list != null) {
			for (UserResourceRel ur : list) {
				ResourceDef r = this.capacityService.getResourceDefById(ur
						.getResourceId() + "");
				if (r != null) {
					Option o = new Option();
					data.add(o);
					o.setId(r.getId());
					o.setName(r.getName());
				}
			}
		}
		return data;
	}

	@Override
	public List<Option> getBusiness(long userId) throws Exception {// TODO 未完成
		ILoginUser user=BaseAction.getLoginUser();
		List<BizMainBo> list = this.bizMainApi.getAllBizList(user);
		List<Option> data = new ArrayList<>();
		if (list != null) {
			for (BizMainBo b : list) {
				Option o = new Option();
				data.add(o);
				o.setId(b.getId() + "");
				o.setName(b.getName());
			}
		}
		return data;
	}

	@Override
	public List<ModelTree> getModelTree() {
		CategoryDef[] categorys = this.capacityService.getRootCategory()
				.getChildCategorys();
		List<ModelTree> tree = new ArrayList<ModelTree>();
		if (categorys != null) {
			model(categorys, tree);
		}
		return tree;
	}

	private void model(CategoryDef[] categorys, List<ModelTree> tree) {
		for (CategoryDef category : categorys) {
			if (!category.isDisplay()) {
				continue;
			}
			ModelTree model = new ModelTree();
			tree.add(model);
			model.setId(category.getId());
			model.setName(category.getName());
			CategoryDef[] cs = category.getChildCategorys();
			if (cs != null && cs.length > 0) {
				model.setChildModelTrees(new ArrayList<ModelTree>());
				this.model(category.getChildCategorys(),
						model.getChildModelTrees());
			} else if (cs == null || cs.length == 0) {
				try {
					List<ResourceInstance> list = resourceInstanceService
							.getParentInstanceByCategoryId(category.getId());
					if (list != null && list.size() > 0) {
						model.setChildModelTrees(new ArrayList<ModelTree>());
						analyticalResourceInstance(list,
								model.getChildModelTrees());
					}
				} catch (InstancelibException e) {
					e.printStackTrace();
				}

			} else {
				ResourceDef[] resource = category.getResourceDefs();
				if (resource != null && resource.length > 0) {
					model.setChildModelTrees(new ArrayList<ModelTree>());
					this.resource(resource, model.getChildModelTrees());
				}
			}
		}
	}

	private void analyticalResourceInstance(List<ResourceInstance> list,
			List<ModelTree> tree) {
		for (ResourceInstance resourceInstance : list) {
			ModelTree model = new ModelTree();
			tree.add(model);
			model.setId(resourceInstance.getId() + "");
			model.setName(resourceInstance.getName());
		}
	}

	private void resource(ResourceDef[] resource, List<ModelTree> tree) {
		for (ResourceDef r : resource) {
			ModelTree res = new ModelTree();
			tree.add(res);
			res.setId(r.getId());
			res.setName(r.getName());
			res.setResource(true);
			ResourceDef[] resources = r.getChildResourceDefs();
			if (resources != null && resources.length > 0) {
				res.setChildModelTrees(new ArrayList<ModelTree>());
				this.resource(resources, res.getChildModelTrees());
			}
		}
	}

	/**
	 * 查询资源实例
	 */
	@Override
	public List<ResourceInspect> getResourceInspect(String modelId,
			String resourceId) {
		List<ResourceInspect> resources = new ArrayList<>();
		try {
			List<ResourceInstance> data = this.resourceInstanceService
					.getAllParentInstance();
			if ((modelId == null || "".equals(modelId))
					&& (resourceId == null || "".equals(resourceId))) {
				analyticalResource(data, resources);
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		return resources;
	}

	private void analyticalResource(List<ResourceInstance> data,
			List<ResourceInspect> resources) {
		if (data != null && data.size() > 0) {
			for (ResourceInstance r : data) {
				ResourceInspect res = new ResourceInspect();
				resources.add(res);
				res.setId(r.getId() + "");
				res.setName(r.getName());
				res.setIp(r.getShowIP());
				res.setModelId(r.getResourceId());
				this.analyticalResource(r.getChildren(), resources);
			}
		}
	}

	@Override
	public List<IndexModel> getIndexModel(String[] modelIds) {
		List<IndexModel> data = new ArrayList<>();
		if (modelIds != null && modelIds.length > 0) {
			for (int i = 0; i < modelIds.length; i++) {
				List<IndexModel> list = new ArrayList<>();
				CategoryDef category = this.capacityService
						.getCategoryById(modelIds[i]);
				if (category != null) {
					ResourceDef[] resources = category.getResourceDefs();
					if (resources != null) {
						for (ResourceDef r : resources) {
							ResourceMetricDef[] metrics = r.getMetricDefs();
							if (metrics != null) {
								for (ResourceMetricDef m : metrics) {
									IndexModel in = new IndexModel();
									list.add(in);
									in.setId(m.getId());
									in.setName(m.getName());
									in.setUnit(m.getUnit());
									in.setDescription(m.getDescription());
								}
							}
						}
					}
				}
				if (i == 0) {
					data = list;
				} else {
					data.retainAll(list);
				}
			}
		}
		return data;
	}

	/**
	 * 根据资源类别ID获取主资源实例
	 */
	@Override
	public List<ReportResourceInstance> getResourceInstanceByCategoryId(
			String categoryId, List<Long> domainId, ILoginUser user) {
		List<ReportResourceInstance> instanceList = new ArrayList<ReportResourceInstance>();
		CategoryDef def = capacityService.getCategoryById(categoryId);
		if (def == null) {
			logger.error("Get categoryDef is null,categoryId = " + categoryId);
			return instanceList;
		}

		// 获取叶子类别
		List<CategoryDef> leafCategoryList = capacityService
				.getLeafCategoryList(def);
		List<String> leafCategoryNameList = new ArrayList<String>();

		if (leafCategoryList == null) {
			logger.error("Get leafCategryList is null,categoryId = "
					+ categoryId);
			return instanceList;
		}

		for (CategoryDef leafDef : leafCategoryList) {
			if (!leafDef.isDisplay()) {
				continue;
			}
			leafCategoryNameList.add(leafDef.getId());
		}

		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setCategoryIds(leafCategoryNameList);
//		List<Long> domainIds = new ArrayList<Long>();
//		domainIds.add(domainId);
		queryBo.setDomainIds(domainId);
		List<ResourceInstanceBo> instances = stm_system_resourceApi
				.getResources(queryBo);
		if (instances != null && instances.size() > 0) {
			for (ResourceInstanceBo instance : instances) {
				// 判断实例是否监控和是否属于当前用户域
				if (instance.getLifeState() != InstanceLifeStateEnum.MONITORED) {
					continue;
				}
				ReportResourceInstance reportInstance = new ReportResourceInstance();
				BeanUtils.copyProperties(instance, reportInstance);
				if (reportInstance.getShowName() == null
						|| reportInstance.getShowName().equals("")) {
					reportInstance.setShowName(instance.getName());
				}
				ResourceDef resourceDef = capacityService
						.getResourceDefById(reportInstance.getResourceId());
				if (resourceDef == null) {
					logger.error("Get resourceDef is null ,resourceId : "
							+ reportInstance.getResourceId());
				} else {
					reportInstance.setResourceName(resourceDef.getName());
					instanceList.add(reportInstance);
				}
			}
		}
		return instanceList;
	}

	/**
	 * 根据资源获取资源实例
	 */
	@Override
	public List<ReportResourceInstance> getInstanceByResource(
			String resourceId, List<Long> domainId, ILoginUser user) {
		List<ReportResourceInstance> instances = new ArrayList<ReportResourceInstance>();

		if (resourceId.contains(",")) {
			// 有多个资源
			HashSet<String> resourceIds = new HashSet<String>();
			for (String singleId : resourceId.split(",")) {
				resourceIds.add(singleId);
			}
			try {
				List<ResourceInstance> instanceList = resourceInstanceService
						.getResourceInstanceByResourceId(resourceIds);
				if (instanceList != null && instanceList.size() > 0) {
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					for (ResourceInstance instance : instanceList) {
						// 找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(
							mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
//					List<Long> domainIds = new ArrayList<Long>();
//					domainIds.add(domainId);
					queryBo.setDomainIds(domainId);
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi
							.accessFilter(queryBo, mainResourceIntanceIds);
					for (ResourceInstance instance : instanceList) {
						// 判断该子资源是否属于用户
						if (!accessMainResourceIntancesIds.contains(instance
								.getParentId())) {
							continue;
						}
						// 判断实例是否监控和是否属于当前用户域
						if (instance.getLifeState() != InstanceLifeStateEnum.MONITORED) {
							continue;
						}
						ReportResourceInstance reportInstance = new ReportResourceInstance();
						BeanUtils.copyProperties(instance, reportInstance);
						if (reportInstance.getShowName() == null
								|| reportInstance.getShowName().equals("")) {
							reportInstance.setShowName(instance.getName());
						}
						ResourceDef def = capacityService
								.getResourceDefById(reportInstance
										.getResourceId());
						if (def == null) {
							logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = "
									+ reportInstance.getResourceId());
							continue;
						}
						reportInstance.setResourceName(def.getName());
						reportInstance.setDiscoverIP(resourceInstanceService
								.getResourceInstance(instance.getParentId())
								.getShowIP());
						instances.add(reportInstance);
					}
				}
			
			} catch (InstancelibException e) {
				logger.error(e.getMessage());
			}
		} else {
			// 一个资源
			try {
				ResourceDef resourceDef = capacityService
						.getResourceDefById(resourceId);
				if (resourceDef.getParentResourceDef() != null) {
					// 子资源
					List<ResourceInstance> instanceList = resourceInstanceService
							.getResourceInstanceByResourceId(resourceId);
					if (instanceList == null || instanceList.size() <= 0) {
						return instances;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					for (ResourceInstance instance : instanceList) {
						// 找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(
							mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
//					List<Long> domainIds = new ArrayList<Long>();
//					domainIds.add(domainId);
					queryBo.setDomainIds(domainId);
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi
							.accessFilter(queryBo, mainResourceIntanceIds);
					if (instanceList != null && instanceList.size() > 0) {
						for (ResourceInstance instance : instanceList) {
							// 判断该子资源是否属于用户
							if (!accessMainResourceIntancesIds
									.contains(instance.getParentId())) {
								continue;
							}
							// 判断实例是否监控和是否属于当前用户域
							if(queryBo.getDomainIds()!=null){
								for (int i=0;i<queryBo.getDomainIds().size();i++) {
									if (instance.getLifeState() != InstanceLifeStateEnum.MONITORED
											|| queryBo.getDomainIds().get(i) != instance.getDomainId()) {
										continue;
									}	
								}
							}
						
							ReportResourceInstance reportInstance = new ReportResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if (reportInstance.getShowName() == null
									|| reportInstance.getShowName().equals("")) {
								reportInstance.setShowName(instance.getName());
							}
							ResourceDef def = capacityService
									.getResourceDefById(reportInstance
											.getResourceId());
							if (def == null) {
								logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = "
										+ reportInstance.getResourceId());
								continue;
							}
							reportInstance.setResourceName(def.getName());
							if (instance.getParentId() > 0) {
								reportInstance
										.setDiscoverIP(resourceInstanceService
												.getResourceInstance(
														instance.getParentId())
												.getShowIP());
							}
							instances.add(reportInstance);
						}
					}
				} else {
					// 主资源
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
//					List<Long> domainIds = new ArrayList<Long>();
//					domainIds.add(domainId);
					queryBo.setModuleId(resourceId);
					queryBo.setDomainIds(domainId);
					List<ResourceInstanceBo> instanceList = stm_system_resourceApi
							.getResources(queryBo);
					if (instanceList != null && instanceList.size() > 0) {
						for (ResourceInstanceBo instance : instanceList) {
							// 判断实例是否监控和是否属于当前用户域
							if (instance.getLifeState() != InstanceLifeStateEnum.MONITORED) {
								continue;
							}
							ReportResourceInstance reportInstance = new ReportResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if (reportInstance.getShowName() == null
									|| reportInstance.getShowName().equals("")) {
								reportInstance.setShowName(instance.getName());
							}
							ResourceDef def = capacityService
									.getResourceDefById(reportInstance
											.getResourceId());
							if (def == null) {
								logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = "
										+ reportInstance.getResourceId());
								continue;
							}
							reportInstance.setResourceName(def.getName());
							instances.add(reportInstance);
						}
					}
				}
			} catch (InstancelibException e) {
				logger.error(e.getMessage());
			}
		}
		return instances;
	}

	/**
	 * 根据资源类别获取子资源
	 */
	@Override
	public List<ResourceCategoryTree> getChildResourceByResourceCategory(
			String categoryId) {
		Map<String, ResourceCategoryTree> childResourceMap = new HashMap<String, ResourceCategoryTree>();

		CategoryDef def = capacityService.getCategoryById(categoryId);

		this.loadChildResource(def, childResourceMap);

		List<ResourceCategoryTree> childResourceList = new ArrayList<ResourceCategoryTree>(
				childResourceMap.values());
		return childResourceList;
	}

	/**
	 * 过滤资源实例
	 */
	@Override
	public List<ReportResourceInstance> filterResourceInstanceList(
			List<Long> instanceIds, String content) {
		List<ReportResourceInstance> childInstances = new ArrayList<ReportResourceInstance>();

		for (long instanceId : instanceIds) {
			ResourceInstanceBo instance = stm_system_resourceApi
					.getResource(instanceId);
			ReportResourceInstance reportInstance = new ReportResourceInstance();
			BeanUtils.copyProperties(instance, reportInstance);
			if (reportInstance.getShowName() == null
					|| reportInstance.getShowName().equals("")) {
				reportInstance.setShowName(instance.getName());
			}
			ResourceDef def = capacityService.getResourceDefById(reportInstance
					.getResourceId());
			if (def == null) {
				logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = "
						+ reportInstance.getResourceId());
				continue;
			}
			reportInstance.setResourceName(def.getName());
			if (reportInstance.getShowName().contains(content)
					|| reportInstance.getDiscoverIP().contains(content)
					|| reportInstance.getResourceName().contains(content)) {
				childInstances.add(reportInstance);
			}

		}
		return childInstances;
	}

	/**
	 * 获取所有资源类别
	 */
	@Override
	public List<ResourceCategoryTree> getAllResourceCategory(String id) {
		List<ResourceCategoryTree> category = new ArrayList<ResourceCategoryTree>();
		if (id != null && !"".equals(id)) {
			CategoryDef def = capacityService.getCategoryById(id);
			if (def != null) {
				this.loadResourceCategory(category, def);
			} else {
				this.loadResourceCategory(category,
						capacityService.getRootCategory());
			}
		} else {
			this.loadResourceCategory(category,
					capacityService.getRootCategory());
		}
		return category;
	}

    @Override
    public List<ResourceCategoryTree> getAllResourceCategoryIncludeVM(String id) {
        List<ResourceCategoryTree> category = new ArrayList<ResourceCategoryTree>();
        if (id != null && !"".equals(id)) {
            CategoryDef def = capacityService.getCategoryById(id);
            if (def != null) {
                this.loadResourceCategoryIncludeVM(category, def);
            } else {
                this.loadResourceCategoryIncludeVM(category,
                        capacityService.getRootCategory());
            }
        } else {
            this.loadResourceCategoryIncludeVM(category,
                    capacityService.getRootCategory());
        }
        return category;
    }


    @Override
	public List<ResourceCategoryTree> getAllResourceCategoryByInspection(String id) {
		List<ResourceCategoryTree> category = new ArrayList<ResourceCategoryTree>();
		if (id != null && !"".equals(id)) {
			CategoryDef def = capacityService.getCategoryById(id);
			if (def != null) {
				this.loadResourceCategoryByInspection(category, def);
			} else {
				this.loadResourceCategoryByInspection(category,
						capacityService.getRootCategory());
			}
		} else {
			this.loadResourceCategoryByInspection(category,
					capacityService.getRootCategory());
		}
		return category;
	}

	private void loadResourceCategory(
			List<ResourceCategoryTree> resourceCategory, CategoryDef def) {
		if (!def.isDisplay()) {
			return;
		}
		if(!licenseCapacityCategory.isAllowCategory(def.getId())){
			return;
		}
		ResourceCategoryTree category = new ResourceCategoryTree();
		category.setId(def.getId());
		category.setName(def.getName());
		if (null != def.getParentCategory()) {
			category.setPid(def.getParentCategory().getId());
			category.setState("closed");
			category.setType(1);
			resourceCategory.add(category);
		}
		// 判断是否还有child category
		if (null != def.getChildCategorys()) {
			CategoryDef[] categoryDefs = def.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; i++) {
				if (categoryDefs[i].isDisplay())
					loadResourceCategory(resourceCategory, categoryDefs[i]);
			}
		} else {
			if (null != def.getResourceDefs()) {
				ResourceDef[] resourceDefs = def.getResourceDefs();
				for (int i = 0; i < resourceDefs.length; i++) {
					ResourceDef resourceDef = resourceDefs[i];
					ResourceCategoryTree resource = new ResourceCategoryTree();
					resource.setId(resourceDef.getId());
					resource.setName(resourceDef.getName());
					resource.setType(2);
					resource.setPid(def.getId());
					resourceCategory.add(resource);
				}
			}
		}
	}

    private void loadResourceCategoryIncludeVM(
            List<ResourceCategoryTree> resourceCategory, CategoryDef def) {
        if (!def.isDisplay()) {
            return;
        }
        if(!licenseCapacityCategory.isAllowCategory(def.getId())){
            return;
        }
        ResourceCategoryTree category = new ResourceCategoryTree();
        category.setId(def.getId());
        category.setName(def.getName());
        if (null != def.getParentCategory()) {
            category.setPid(def.getParentCategory().getId());
            category.setState("closed");
            category.setType(1);
            resourceCategory.add(category);
        }
        // 判断是否还有child category
        if (null != def.getChildCategorys()) {
            CategoryDef[] categoryDefs = def.getChildCategorys();
            for (int i = 0; i < categoryDefs.length; i++) {
                if("VM".equals(categoryDefs[i].getId())){
                    categoryDefs[i].setDisplay(true);
                }
                if (categoryDefs[i].isDisplay())
                    loadResourceCategoryIncludeVM(resourceCategory, categoryDefs[i]);
            }
        } else {
            if (null != def.getResourceDefs()) {
                ResourceDef[] resourceDefs = def.getResourceDefs();
                for (int i = 0; i < resourceDefs.length; i++) {
                    ResourceDef resourceDef = resourceDefs[i];
                    ResourceCategoryTree resource = new ResourceCategoryTree();
                    resource.setId(resourceDef.getId());
                    resource.setName(resourceDef.getName());
                    resource.setType(2);
                    resource.setPid(def.getId());
                    resourceCategory.add(resource);
                }
            }
        }
    }
	
	private void loadResourceCategoryByInspection(
			List<ResourceCategoryTree> resourceCategory, CategoryDef def) {
		if (!def.isDisplay()) {
			return;
		}
		if(!licenseCapacityCategory.isAllowCategory(def.getId())){
			return;
		}
		ResourceCategoryTree category = new ResourceCategoryTree();
		category.setId(def.getId());
		category.setName(def.getName());
		if (null != def.getParentCategory()) {
			category.setPid(def.getParentCategory().getId());
			category.setState("closed");
			category.setType(1);
			resourceCategory.add(category);
		}
		// 判断是否还有child category
		if (null != def.getChildCategorys()) {
			CategoryDef[] categoryDefs = def.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; i++) {
				if("VM".equals(categoryDefs[i].getId())) {
					categoryDefs[i].setDisplay(true);
				}
				if (categoryDefs[i].isDisplay())
					loadResourceCategory(resourceCategory, categoryDefs[i]);
			}
		} else {
			if (null != def.getResourceDefs()) {
				ResourceDef[] resourceDefs = def.getResourceDefs();
				for (int i = 0; i < resourceDefs.length; i++) {
					ResourceDef resourceDef = resourceDefs[i];
					ResourceCategoryTree resource = new ResourceCategoryTree();
					resource.setId(resourceDef.getId());
					resource.setName(resourceDef.getName());
					resource.setType(2);
					resource.setPid(def.getId());
					resourceCategory.add(resource);
				}
			}
		}
	}

	private void loadChildResource(CategoryDef def,
			Map<String, ResourceCategoryTree> childResourceMap) {
		if (null != def.getChildCategorys()) {
			for (CategoryDef childDef : def.getChildCategorys()) {
				if (!childDef.isDisplay()) {
					continue;
				}
				loadChildResource(childDef, childResourceMap);
			}
		} else {
			ResourceDef[] mainDefs = def.getResourceDefs();
			if (mainDefs == null) {
				logger.error("Get main resource error,categoryId = "
						+ def.getId());
				return;
			}

			for (ResourceDef parentResource : mainDefs) {
				ResourceDef[] childDefs = parentResource.getChildResourceDefs();
				if (childDefs == null) {
					logger.error("ParentResource.getChildResourceDefs() is null,resourceId = "
							+ parentResource.getId());
					continue;
				}
				for (ResourceDef childResource : childDefs) {
					if (!childResourceMap.containsKey(childResource.getName())) {
						ResourceCategoryTree resultDef = new ResourceCategoryTree();
						resultDef.setId(childResource.getId());
						resultDef.setName(childResource.getName());
						childResourceMap
								.put(childResource.getName(), resultDef);
					} else {
						String id = childResourceMap.get(
								childResource.getName()).getId();
						id += "," + childResource.getId();
						childResourceMap.get(childResource.getName()).setId(id);
					}
				}

			}
		}
	}

	@Override
	public List<ResourceCategoryTree> getChildResourceByMainResource(
			String resourceId) {
		List<ResourceCategoryTree> childResourceList = new ArrayList<ResourceCategoryTree>();
		ResourceDef mainDef = capacityService.getResourceDefById(resourceId);
		if (mainDef == null) {
			logger.error("Get main resource error,resourceId = " + resourceId);
			return childResourceList;
		}

		ResourceDef[] childDefs = mainDef.getChildResourceDefs();
		if(childDefs == null || childDefs.length <= 0){
			logger.error("Get child resource error,MainResourceId = " + resourceId);
			return childResourceList;
		}
		for (ResourceDef childResource : childDefs) {
			ResourceCategoryTree resultDef = new ResourceCategoryTree();
			resultDef.setId(childResource.getId());
			resultDef.setName(childResource.getName());
			childResourceList.add(resultDef);
		}

		return childResourceList;
	}

	@Override
	public List<Option> getAllUser() {
		List<Option> data = new ArrayList<>();
		List<User> list =userApi.queryAllUserNoPage();
		if(list!=null){
			for (User user : list) {
				Option option = new Option();
				option.setId(String.valueOf(user.getId()));
				option.setName(user.getName());
				data.add(option);
			}
		}
		return data;
	}

	@Override
	public List<Option> getUser(Long[] domainId) {
		Page<User, UserConditionVo> page = new Page<>();
		page.setStartRow(0);
		page.setRowCount(Long.MAX_VALUE);
		page.setCondition(new UserConditionVo());
		page.getCondition().setDomainId(domainId);
		page.getCondition().setUserType(1);// 1普通用户
		this.userApi.selectByPage(page);
		List<Option> data = new ArrayList<>();
		if (page.getRows() != null) {
			for (User u : page.getRows()) {
				Option o = new Option();
				data.add(o);
				o.setId(u.getId() + "");
				o.setName(u.getName());
			}
		}
		return data;
	}
}
