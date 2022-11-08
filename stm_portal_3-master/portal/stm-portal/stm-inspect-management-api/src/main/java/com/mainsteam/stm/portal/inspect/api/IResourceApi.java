package com.mainsteam.stm.portal.inspect.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.inspect.bo.IndexModel;
import com.mainsteam.stm.portal.inspect.bo.ModelTree;
import com.mainsteam.stm.portal.inspect.bo.Option;
import com.mainsteam.stm.portal.inspect.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.inspect.bo.ResourceCategoryTree;
import com.mainsteam.stm.portal.inspect.bo.ResourceInspect;

public interface IResourceApi {

	List<Option> getUser(long domainId);
	List<Option> getUser(Long[] domainId);

	List<Option> getResources(long userId);

	List<Option> getBusiness(long userId) throws Exception;

	List<ModelTree> getModelTree();

	List<ResourceInspect> getResourceInspect(String modelId, String resourceId);

	List<IndexModel> getIndexModel(String[] modelIds);

	List<ResourceCategoryTree> getAllResourceCategory(String id);

    List<ResourceCategoryTree> getAllResourceCategoryIncludeVM(String id);
	
	List<ResourceCategoryTree> getAllResourceCategoryByInspection (String id);	//自动巡检加入虚拟化单独接口

	List<ResourceCategoryTree> getChildResourceByMainResource(String resourceId);

	List<ReportResourceInstance> getResourceInstanceByCategoryId(
			String categoryId, List<Long> domainId, ILoginUser user);

	List<ReportResourceInstance> filterResourceInstanceList(
			List<Long> instanceIds, String content);

	List<ReportResourceInstance> getInstanceByResource(String resourceId,
			List<Long> domainId, ILoginUser user);

	List<ResourceCategoryTree> getChildResourceByResourceCategory(
			String categoryId);
	
	List<Option> getAllUser();
}
