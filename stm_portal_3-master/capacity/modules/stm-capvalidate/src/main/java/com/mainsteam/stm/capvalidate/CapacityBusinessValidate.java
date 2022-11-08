package com.mainsteam.stm.capvalidate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;

public class CapacityBusinessValidate {
	public CategoryDef rootCategory;
	public Map<String, CategoryDef> categoryMap = new HashMap<String, CategoryDef>();
	public Map<String, ResourceDef> resourceMap = new HashMap<String, ResourceDef>();
	Map<String, PluginDef> allPluginMap = new HashMap<String, PluginDef>();
	private List<String> validateError = new ArrayList<String>();

	public void validate(String caplibsPath) {
		if (!caplibsPath.endsWith(File.separator)) {
			caplibsPath += File.separator;
		}
		String caplibsPathDict = caplibsPath + "dictionary" + File.separator;
		String caplibsPathWiserv = caplibsPath + "resources" + File.separator;
		String caplibsPluginsPath = caplibsPath + "plugins";
		// caplibs/dict
		
		// 加载category
		rootCategory = this.loadCategory(caplibsPathDict + "Category.xml");
		this.putCategoryMap(rootCategory);
		
		// 加载plugin(key:pluginid)
		Map<String, PluginDef> pluginMap = this.loadPlugins(caplibsPluginsPath);
		this.allPluginMap.putAll(pluginMap);
		
		// 加载collect和resource
		IOFileFilter fileFilterCollect = new RegexFileFilter("collect.xml");
		IOFileFilter all = FileFilterUtils.trueFileFilter();
		//caplibsPathWiserv += "database/db2";
		Collection<File> collectFiles = FileUtils.listFiles(new File(
				caplibsPathWiserv), fileFilterCollect, all);
		for (File collectFile : collectFiles) {
			// key(metricid+","+pluginid)
			Map<String, MetricCollect> metricPluginMap = this.loadMetricPlugin(collectFile.getPath());
			// key(metricid)
			Map<String, List<MetricCollect>> mpMap = this.bindingPlugin(metricPluginMap, pluginMap, collectFile.getPath());
			// resource
			File resourceFile = new File(collectFile.getParent() + File.separator + "resource.xml");
			ResourceDef rootResource = this.loadResource(resourceFile.getPath());
			this.bindingMetricPlugin(rootResource, mpMap, resourceFile.getPath());
			List<String> tempError = new ArrayList<String>();
			if (!mpMap.isEmpty()) {
				String error = "文件" + resourceFile.getPath() + "节点Capacity/Resource/Metrics/Metric的属性id值缺少(";
				for (String metricid : mpMap.keySet()) {
					error += metricid + ",";
				}
				error += ")(原因：在collect.xml文件中配置了这些metricid)";
				tempError.add(error);
			}
			if(tempError.size() > 0){
				this.validateError.addAll(tempError);
			}
			this.putResourceMap(rootResource, resourceFile.getPath());
				
		}
		
	}
	
	public List<String> getValidateError() {
		return validateError;
	}

	public void setValidateError(List<String> validateError) {
		this.validateError = validateError;
	}

	private void bindingMetricPlugin(ResourceDef resource, Map<String, List<MetricCollect>> mpMap, String filePath) {
		ResourceMetricDef[] metrics = resource.getMetricDefs();
		List<String> errors = new ArrayList<String>();
		if (metrics != null) {
			for (int i = 0; i < metrics.length; i++) {
				ResourceMetricDef metric = metrics[i];
				List<MetricCollect> mpList = mpMap.get(metric.getId());
				if (mpList != null && mpList.size() > 0) {
					MetricCollect[] mps = new MetricCollect[mpList.size()];
					for (int j = 0; j < mps.length; j++) {
						mps[j] = mpList.get(j);
						mps[j].setResourceMetric(metric);
					}
					metric.setMetricPlugins(mps);
				} else {
					errors.add("文件" + filePath + "节点Capacity/Resource/Metrics/Metric的属性id值"
							+ metric.getId() + "不合法"
							+ "(不能在collect.xml文件中找到匹配的metricid)");
				}
				mpMap.remove(metric.getId());
			}
			if(errors.size() > 0)
				this.validateError.addAll(errors);
		}
		
		ResourceDef[] childResources = resource.getChildResourceDefs();
		if (childResources != null) {
			for (int i = 0; i < childResources.length; i++) {
				this.bindingMetricPlugin(childResources[i], mpMap, filePath);
			}
		}
	}

	private Map<String, List<MetricCollect>> bindingPlugin(
			Map<String, MetricCollect> metricPluginMap,
			Map<String, PluginDef> pluginMap, String filePath) {
		Map<String, List<MetricCollect>> mpMap = new HashMap<String, List<MetricCollect>>();
		
		Set<Map.Entry<String, MetricCollect>> set = metricPluginMap.entrySet();
		List<String> errors = new ArrayList<String>();
		for (Iterator<Map.Entry<String, MetricCollect>> it = set.iterator(); it
				.hasNext();) {
			Map.Entry<String, MetricCollect> entry = it.next();
			String metricPluginKey = entry.getKey();
			MetricCollect metricPlugin = entry.getValue();
			
			String[] metricPluginId = metricPluginKey.split(",");
			String metricId = metricPluginId[0];
			String pluginId = metricPluginId[1];
			PluginDef plugin = pluginMap.get(pluginId);
			if (plugin == null) {
				errors.add("文件" + filePath + "节点ID为【"+metricId+"】MetricPlugins/MetricPlugin的属性pluginid值【" + pluginId 
						+ "】不能找到匹配的Plugin");
			}
			metricPlugin.setPlugin(plugin);
			
			if (mpMap.get(metricId) == null) {
				mpMap.put(metricId, new ArrayList<MetricCollect>());
			}
			mpMap.get(metricId).add(metricPlugin);
		}
		//如果一个指标有多个collect type,那么其他指标如果配置多个collect type就必须一致
		List<String> collectTypes = new ArrayList<String>();
		for (List<MetricCollect> collects : mpMap.values()) {
			if (collects.size() > 1) {
				for (MetricCollect collect : collects) {
					collectTypes.add(collect.getCollectType());
				}
				break;
			}
		}
		if (!collectTypes.isEmpty()) {
			for (List<MetricCollect> collects : mpMap.values()) {
				if (collects.size() > 1) {
					if (collects.size() != collectTypes.size()) {
						errors.add("文件" + filePath + "节点MetricPlugins/MetricPlugin的属性collectType值不合法"
								+ "(配置多个collectType的所有metricid都必须配置相同的collectType)");
					} else {
						for (MetricCollect collect : collects) {
							if (!collectTypes.contains(collect.getCollectType())) {
								errors.add("文件" + filePath
										+ "节点MetricPlugins/MetricPlugin的属性collectType值不合法"
										+ "(配置多个collectType的所有metricid都必须配置相同的collectType)");
							}
						}
					}
				}
			}
		}
		if(errors.size() > 0)
			this.validateError.addAll(errors);
		return mpMap;
	}
	
	private ResourceDef loadResource(String filePath) {
		ResourceUtil capa = new ResourceUtil();

		ResourceDef rootResource = capa.loadResource(filePath);
		this.validateError.addAll(capa.getValidateError());
		
		String categoryId = capa.getCategoryMap().get(rootResource.getId());
		this.bindingCategory(rootResource, categoryId, filePath);
		
		return rootResource;
	}

	private void bindingCategory(ResourceDef rootResource,
			String categoryId, String filePath) {
		CategoryDef category = this.categoryMap.get(categoryId);
		List<String> errors = new ArrayList<String>();
		if (category == null) {
			errors.add("文件" + filePath + "节点Capacity/Resource的属性category值" + categoryId + "不合法"
					+ "(不能在Category.xml文件中找到匹配值)");
			return;
		}
		rootResource.setCategory(category);

		List<ResourceDef> resourceList = new ArrayList<ResourceDef>();
//		if(category == null){
//			System.out.println();
//		}
		ResourceDef[] resources = category.getResourceDefs();
		if (resources != null) {
			for (int i = 0; i < resources.length; i++) {
				resourceList.add(resources[i]);
			}
		}
		resourceList.add(rootResource);
		ResourceDef[] newResources = new ResourceDef[resourceList.size()];
		for (int i = 0; i < newResources.length; i++) {
			newResources[i] = resourceList.get(i);
		}
		category.setResourceDefs(newResources);
		if(errors.size() > 0)
			this.validateError.addAll(errors);
	}

	private void putResourceMap(ResourceDef resource, String filePath) {
		List<String> errors = new ArrayList<String>();
		if (resourceMap.get(resource.getId()) != null) {
			errors.add("文件" + filePath + "节点Capacity/Resource的属性id值"
					+ resource.getId() + "不合法"
					+ "(原因：在其他resource.xml文件中找到相同的id值)");
			//System.err.println(resource.getId() + "不合法");
			if(errors.size() > 0)
				this.validateError.addAll(errors);
		}
		this.resourceMap.put(resource.getId(), resource);
		//System.out.println(resource.getId() + "                  :" + filePath);
		ResourceDef[] childResources = resource.getChildResourceDefs();
		if (childResources == null)
			return;

		for (int i = 0; i < childResources.length; i++) {
			this.putResourceMap(childResources[i], filePath);
		}
	}
	
	private Map<String, MetricCollect> loadMetricPlugin(String filePath) {
		MetricPluginUtil mpu = new MetricPluginUtil();
		Map<String, MetricCollect> map = mpu.loadMetricPlugin(filePath);
		this.validateError.addAll(mpu.getValidateError());
		
		return map;
	}

	private Map<String, PluginDef> loadPlugins(String path) {
		Map<String, PluginDef> map = new HashMap<String, PluginDef>();
		File pathFile = new File(path);
		Collection<File> fileNames = FileUtils.listFiles(pathFile,
				new String[] { "xml", "XML" }, false);
		PluginDefUtil pu = new PluginDefUtil();
		for (File file : fileNames) {
			List<String> errors = new ArrayList<String>();
			PluginDef plugin = pu.loadPlugin(file);
			if (plugin == null) {
				errors.add("文件" + file.getPath() + "解析失败");
				return map;
			}
			if (map.get(plugin.getId()) != null) {
				errors.add("文件" + file.getPath() + "节点Plugin的属性id值" + plugin.getId()
						+ "不能与其它pluginid相同");
			}
			try {
				Class.forName(plugin.getClassUrl());
			} catch (ClassNotFoundException e) {
				errors.add("文件" + file.getPath() + "节点Plugin的属性class值" + plugin.getClassUrl()
						+ "不能找到对应的Class");
			}
			if(errors.size() > 0)
				this.validateError.addAll(errors);
			map.put(plugin.getId(), plugin);
		}
		
		return map;
	}

	private void putCategoryMap(CategoryDef category) {
		categoryMap.put(category.getId(), category);
		CategoryDef[] childCategorys = category.getChildCategorys();
		if (childCategorys == null)
			return;
		for (int i = 0; i < childCategorys.length; i++) {
			this.putCategoryMap(childCategorys[i]);
		}
	}
	
	private CategoryDef loadCategory(String filePath) {
		CategoryDefUtil cutil = new CategoryDefUtil();
		CategoryDef category = cutil.loadCategory(filePath);
		if(cutil.getValidateError().size() > 0)
			this.validateError.addAll(cutil.getValidateError());
		return category;
	}
	
}
