package com.mainsteam.stm.capbase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.CaplibAPIErrorCode;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.plugin.PluginInitParaChanger;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.util.OSUtil;

public class ResourceRespositoy implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -78721554813681603L;
	
	private static final String COLLECT_XML = "collect.xml";
	private static final String RESOURCE_XML = "resource.xml";
	private static final Log logger = LogFactory
			.getLog(ResourceRespositoy.class);
	private CategoryDef rootCategory;
	private Map<String, CategoryDef> categoryMap = new HashMap<String, CategoryDef>();
	private Map<String, ResourceDef> resourceMap = new HashMap<String, ResourceDef>();
	private Map<String, PluginDef> allPluginMap = new HashMap<String, PluginDef>();
	private Map<String, List<DeviceType>> vendor2DeviceTypes = new HashMap<String, List<DeviceType>>();
	private Map<String, DeviceType> sysoid2DeviceType = new HashMap<String, DeviceType>();
	private Map<String, List<DeviceType>> resourceId2DeviceType = new HashMap<String, List<DeviceType>>();

	public ResourceRespositoy() {
	}

	/**
	 * 解析xml入口
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		this.load();
	}

	/**
	 * 根据插件id获取插件对象
	 * 
	 * @param pluginId
	 *            插件id
	 * @return
	 */
	public PluginDef getPluginDef(String pluginId) {
		return this.allPluginMap.get(pluginId);
	}

	public Collection<String> getAllPluginIds() {
		return this.allPluginMap.keySet();
	}

	/**
	 * 加载能力库相关xml文件，包括分类文件、资源文件、插件文件
	 */
	private void load() throws Exception {

		try {
			String caplibsPath = OSUtil.getEnv("caplibs.path");

			if (!caplibsPath.endsWith(File.separator)) {
				caplibsPath += File.separator;
			}
			String caplibsPathDict = caplibsPath + "dictionary"
					+ File.separator;
			String caplibsPathWiserv = caplibsPath + "resources"
					+ File.separator;
			String caplibsPluginsPath = caplibsPath + "plugins";
			// caplibs/dict

			// 加载category
			rootCategory = this.loadCategory(caplibsPathDict + "Category.xml");
			this.putCategoryMap(rootCategory);

			// 加载devicetypes
			DeviceType[] deviceTypes = this.loadDevTypes(caplibsPathDict
					+ "DeviceType.xml");
			this.putDeviceTypeMap(deviceTypes);

			// 加载plugin(key:pluginid)
			Map<String, PluginDef> pluginMap = this
					.loadPlugins(caplibsPluginsPath);
			this.allPluginMap.putAll(pluginMap);

			// 加载collect和resource
			IOFileFilter fileFilterCollect = new RegexFileFilter(COLLECT_XML);
			IOFileFilter all = FileFilterUtils.trueFileFilter();
			Collection<File> collectFiles = FileUtils.listFiles(new File(
					caplibsPathWiserv), fileFilterCollect, all);
			for (File collectFile : collectFiles) {

				// key(metricid+","+pluginid)
				MetricPluginUtil mpu2 = new MetricPluginUtil();
				Map<String, MetricCollect> metricPluginMap = mpu2
						.loadMetricPlugin(collectFile.getPath(), null);
				List<PluginInitParaChanger> pluginInitParaChangers = mpu2
						.getPluginInitParaChangers();
				// key(metricid)
				if(metricPluginMap == null)
					System.out.println("Null:" + collectFile.getPath());
				Map<String, List<MetricCollect>> mpMap = this.bindingPlugin(
						metricPluginMap, pluginMap);

				// resource
				File resourceFile = new File(collectFile.getParent()
						+ File.separator + RESOURCE_XML);
				ResourceDef rootResource = this.loadResource(resourceFile
						.getPath());

				// other collect
				File dir = new File(collectFile.getParent());
				File[] allFiles = dir.listFiles();
				if (allFiles.length > 2) {
					for (File fileOne : allFiles) {
						if (RESOURCE_XML.equalsIgnoreCase(fileOne.getName())) {
							continue;
						}
						if (COLLECT_XML.equalsIgnoreCase(fileOne.getName())) {
							continue;
						}
						// other collect
						List<String> sysoidList = new ArrayList<String>();
						MetricPluginUtil mpu1 = new MetricPluginUtil();
						Map<String, MetricCollect> metricPluginMapNew = null;
						Map<String, List<MetricCollect>> mpMapNew = null;
						try{
							metricPluginMapNew = mpu1.loadMetricPlugin(fileOne.getPath(), sysoidList);
							mpMapNew = this.bindingPlugin(metricPluginMapNew, pluginMap);
						}catch(Exception e) {
							e.printStackTrace();
							System.err.println("NullPointer File : " + fileOne.getPath());
							logger.warn(e.getMessage() + "; File:" + fileOne.getPath(), e);
						}

						for (Entry<String, List<MetricCollect>> entry : mpMap
								.entrySet()) {
							String metricId = entry.getKey();
							List<MetricCollect> metricCollects = entry
									.getValue();

							List<MetricCollect> collects = mpMapNew
									.get(metricId);
							if (null == collects || collects.isEmpty()) {
								logger.error("ERROR:metric not exist in New Collect:"
										+ metricId
										+ ",collect file:"
										+ fileOne.getName());
								continue;
							}
							for (MetricCollect collect : collects) {
								collect.addSysoids(sysoidList);
								metricCollects.add(collect);
							}
						}
					}
				}

				// bind resource to mpMap
				this.bindingMetricPlugin(rootResource, mpMap);
				this.putResourceMap(rootResource);

				// changers
				if (null != pluginInitParaChangers
						&& !pluginInitParaChangers.isEmpty()) {
					PluginInitParaChanger[] changers = new PluginInitParaChanger[pluginInitParaChangers
							.size()];
					rootResource
							.setPluginInitParaChangers(pluginInitParaChangers
									.toArray(changers));
				}

			}
			if (logger.isInfoEnabled()) {
				int mainResource = 0;
				for (ResourceDef value : this.resourceMap.values()) {
					if (value.getParentResourceDef() == null)
						mainResource++;
				}
				logger.info("Resource Size: " + this.resourceMap.size()
						+ "\tMain Resource Size: " + mainResource);
			}
			for (ResourceDef value : this.resourceMap.values()) {
				this.printLogger(value);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		logger.info("FINISH LOAD RESOURCE!");
	}

	private void putDeviceTypeMap(DeviceType[] deviceTypes) {
		if (null != deviceTypes) {
			for (DeviceType deviceType : deviceTypes) {
				addDeviceType(deviceType);
			}
		}
	}

	/**
	 * 增加设备型号
	 * 
	 * @param deviceType
	 */
	public CaplibAPIErrorCode addDeviceType(DeviceType deviceType) {
		if (null == deviceType) {
			return CaplibAPIErrorCode.ADD_DEVICE_TYPE_01;
		}
		if (sysoid2DeviceType.containsKey(deviceType.getSysOid())) {
			return CaplibAPIErrorCode.ADD_DEVICE_TYPE_02;
		}
		sysoid2DeviceType.put(deviceType.getSysOid(), deviceType);
		refactorMap();
		return CaplibAPIErrorCode.OK;
	}

	private void refactorMap() {
		
		vendor2DeviceTypes.clear();
		resourceId2DeviceType.clear();
		
		for (DeviceType devType : sysoid2DeviceType.values()) {
			
			String vendorId = devType.getVendorId();
			List<DeviceType> vtypes = null;
			if (!vendor2DeviceTypes.containsKey(vendorId)) {
				vtypes = new ArrayList<DeviceType>();
				vendor2DeviceTypes.put(vendorId, vtypes);
			} else {
				vtypes = vendor2DeviceTypes.get(vendorId);
			}
			vtypes.add(devType);

			String resourceId = devType.getResourceId();
			List<DeviceType> rtypes = null;
			if (!resourceId2DeviceType.containsKey(resourceId)) {
				rtypes = new ArrayList<DeviceType>();
				resourceId2DeviceType.put(resourceId, rtypes);
			} else {
				rtypes = resourceId2DeviceType.get(resourceId);
			}
			rtypes.add(devType);
		}
	}

	private DeviceType[] loadDevTypes(String fileName) {
		return new DeviceTypeUtil().loadDeviceType(fileName);
	}

	/**
	 * 绑定MetricPlugin，遍历resource下的所有指标， 每个指标在mpMap里面找到相应的key
	 * 
	 * @param resource
	 *            资源对象
	 * @param mpMap
	 *            以metricid为key，metricCollect集合为value的map
	 */
	private void bindingMetricPlugin(ResourceDef resource,
			Map<String, List<MetricCollect>> mpMap) {
		ResourceMetricDef[] metrics = resource.getMetricDefs();
		if (metrics != null) {
			for (int i = 0; i < metrics.length; i++) {
				ResourceMetricDef metric = metrics[i];
				List<MetricCollect> mpList = mpMap.get(metric.getId());
				if (mpList != null) {
					MetricCollect[] mps = new MetricCollect[mpList.size()];
					for (int j = 0; j < mps.length; j++) {
						mps[j] = mpList.get(j);
						mps[j].setResourceMetric(metric);
					}
					metric.setMetricPlugins(mps);
				}
			}
		}

		ResourceDef[] childResources = resource.getChildResourceDefs();
		if (childResources != null) {
			for (int i = 0; i < childResources.length; i++) {
				this.bindingMetricPlugin(childResources[i], mpMap);
			}
		}
	}

	/**
	 * 绑定Plugin，遍历metricPluginMap，给所有MetricCollect对象设置对应的PluginDef,
	 * 分解metricPluginMap的key(metricid+pluginid),重新创建一个以metricid为可以的
	 * 新map，它的value是一个MetricCollect集合 每个指标在mpMap里面找到相应的key
	 * 
	 * @param metricPluginMap
	 *            以metricid+pluginid为key，MetricCollect为value的map
	 * @param pluginMap
	 *            以pluginid为key，PluginDef为value的map
	 * @return 返回一个以metricid为可以，MetricCollect集合为value的map
	 */
	private Map<String, List<MetricCollect>> bindingPlugin(
			Map<String, MetricCollect> metricPluginMap,
			Map<String, PluginDef> pluginMap) {
		Map<String, List<MetricCollect>> mpMap = new HashMap<String, List<MetricCollect>>();
		if(metricPluginMap == null)
			System.out.println("----------");
		for (Entry<String, MetricCollect> entry : metricPluginMap.entrySet()) {
			String metricPluginKey = entry.getKey();
			MetricCollect metricPlugin = entry.getValue();

			String[] metricPluginId = metricPluginKey.split(",");
			String metricId = metricPluginId[0];
			String pluginId = metricPluginId[1];
			PluginDef thisPlugin;
			// try {
			if(pluginMap.get(pluginId) == null) {
				System.out.println("None pluginId : " + pluginId);
			}
			thisPlugin = (PluginDef) pluginMap.get(pluginId).clone();
			// } catch (CloneNotSupportedException e) {
			// thisPlugin = pluginMap.get(pluginId);
			// logger.warn("加载模型文件时，克隆PluginDef失败,pluginId为" + pluginId +
			// ",指标id：" + metricId);
			// }
			// if(null == thisPlugin){
			// System.out.println("");
			// }
			metricPlugin.setPlugin(thisPlugin);

			if (mpMap.get(metricId) == null) {
				mpMap.put(metricId, new ArrayList<MetricCollect>());
			}
			mpMap.get(metricId).add(metricPlugin);
		}

		return mpMap;
	}

	/**
	 * 解析category.xml文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 返回CategoryDef
	 */
	private CategoryDef loadCategory(String filePath) {
		CategoryDefUtil cutil = new CategoryDefUtil();
		return cutil.loadCategory(filePath);
	}

	/**
	 * 解析resource.xml文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 返回ResourceDef
	 */
	private ResourceDef loadResource(String filePath) {
		ResourceUtil capa = new ResourceUtil();

		ResourceDef rootResource = capa.loadResource(filePath);
		Map<String, String> categoryMap2 = capa.getCategoryMap();
		String categoryId = categoryMap2.get(rootResource.getId());
		this.bindingCategory(rootResource, categoryId);

		return rootResource;
	}

	/**
	 * 绑定Category，通过category id从categoryMap找打categoryDef对象，
	 * 然后赋值给resource主资源，同时categoryDef对象将添加这个主资源
	 * 
	 * @param rootResource
	 *            主资源
	 * @param categoryId
	 */
	private void bindingCategory(ResourceDef rootResource, String categoryId) {
		CategoryDef category = this.categoryMap.get(categoryId);
		ResourceDef[] resources = null;
		if (null == category) {
			logger.error("NULL Category,ID:" + categoryId + ",model:"
					+ rootResource.getId());
		} else {
			rootResource.setCategory(category);
			resources = category.getResourceDefs();
		}
		List<ResourceDef> resourceList = new ArrayList<ResourceDef>();
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
		if (null != category) {
			category.setResourceDefs(newResources);
		}
	}

	/**
	 * 将资源put到一个map对象，同时将他的所有子孙资源也put到map 是一个递归循环
	 * 
	 * @param resource
	 *            资源
	 */
	private void putResourceMap(ResourceDef resource) {
		this.resourceMap.put(resource.getId(), resource);
		ResourceDef[] childResources = resource.getChildResourceDefs();
		if (childResources == null)
			return;

		for (int i = 0; i < childResources.length; i++) {
			this.putResourceMap(childResources[i]);
		}
	}

	/**
	 * 打印资源和指标的日志
	 * 
	 * @param resource
	 *            资源
	 */
	private void printLogger(ResourceDef resource) {
		ResourceMetricDef[] metrics = resource.getMetricDefs();
		if (logger.isInfoEnabled() && null != metrics) {
			logger.info("Resource " + resource.getId() + ",Metric Size: "
					+ metrics.length);
			Map<String, Integer> countMap = new HashMap<String, Integer>();
			for (ResourceMetricDef metric : metrics) {
				Integer countValue = countMap
						.get(metric.getMetricType().name());
				int count = countValue == null ? 0 : countValue.intValue();
				countMap.put(metric.getMetricType().name(), ++count);
			}
			String infoStr = "";
			for (Entry<String, Integer> countEntry : countMap.entrySet()) {
				infoStr += "---Metric Type: " + countEntry.getKey();
				infoStr += ",Size: " + countEntry.getValue();
			}
			if (logger.isInfoEnabled()) {
				logger.info(infoStr);
			}
		}
	}

	// /**
	// * 解析collect.xml文件
	// *
	// * @param filePath
	// * 文件路径
	// * @return 返回Map<String, MetricCollect> key是（metricid+pluginid）
	// */
	// private Map<String, MetricCollect> loadMetricPlugin(String filePath) {
	// }

	/**
	 * 解析plugin.xml文件
	 * 
	 * @param path
	 *            文件路径
	 * @return Map<String, PluginDef> 插件id-插件对象的Map
	 */
	private Map<String, PluginDef> loadPlugins(String path) {
		Map<String, PluginDef> map = new HashMap<String, PluginDef>();
		File pathFile = new File(path);
		Collection<File> fileNames = FileUtils.listFiles(pathFile,
				new String[] { "xml", "XML" }, false);
		PluginDefUtil pu = new PluginDefUtil();
		for (File file : fileNames) {
			PluginDef plugin = pu.loadPlugin(file);
			if (null != plugin) {
				map.put(plugin.getId(), plugin);
			}
		}
		return map;
	}

	/**
	 * 将categoryput到一个map对象，同时将他的所有子孙资源也put到map 是一个递归循环
	 * 
	 * @param category
	 */
	private void putCategoryMap(CategoryDef category) {
		categoryMap.put(category.getId(), category);
		CategoryDef[] childCategorys = category.getChildCategorys();
		if (childCategorys == null)
			return;
		for (int i = 0; i < childCategorys.length; i++) {
			this.putCategoryMap(childCategorys[i]);
		}
	}

	/**
	 * 获取所有资源集合
	 * 
	 * @return List<ResourceDef> 资源集合
	 */
	public List<ResourceDef> getResourceDefList() {
		List<ResourceDef> resourceList = new ArrayList<ResourceDef>();

		Collection<ResourceDef> coll = resourceMap.values();
		Iterator<ResourceDef> it = coll.iterator();
		for (; it.hasNext();) {
			resourceList.add(it.next());
		}

		return resourceList;
	}

	public ResourceDef getResourceDefById(String id) {
		return resourceMap.get(id);
	}

	// public ResourceDef getResourceDefSummaryInfoById(String id) {
	// return null;
	// }

	/**
	 * 根据资源id和指标id获取指标对象
	 * 
	 * @param resourceId
	 *            资源id
	 * @param metricId
	 *            指标id
	 * @return ResourceMetricDef 指标对象
	 */
	public ResourceMetricDef getResourceMetricDef(String resourceId,
			String metricId) {
		ResourceDef resource = resourceMap.get(resourceId);
		if (resource != null) {
			ResourceMetricDef[] resMetrics = resource.getMetricDefs();
			for (int i = 0; i < resMetrics.length; i++) {
				if (resMetrics[i].getId().equalsIgnoreCase(metricId)) {
					return resMetrics[i];
				}
			}
		}

		return null;
	}

	/**
	 * 资源分类
	 * 
	 * @param level
	 *            = 0，代表根节点，第一级，以此类推，1代表第二级...
	 * @return
	 */
	public List<CategoryDef> getCategoryList(int level) {
		List<CategoryDef> categoryList = new ArrayList<CategoryDef>();
		categoryList.add(rootCategory);
		return getCategoryList(level, 0, categoryList);
	}

	/**
	 * getCategoryList()方法的内部方法
	 */
	private List<CategoryDef> getCategoryList(int level, int currLevel,
			List<CategoryDef> categoryList) {
		if (level == currLevel) {
			return categoryList;
		}
		List<CategoryDef> childList = new ArrayList<CategoryDef>();
		for (int i = 0; i < categoryList.size(); i++) {
			CategoryDef[] childs = categoryList.get(i).getChildCategorys();
			if (childs != null) {
				for (int j = 0; j < childs.length; j++) {
					childList.add(childs[j]);
				}
			}
		}
		if (childList.isEmpty())
			return null;

		return getCategoryList(level, currLevel + 1, childList);
	}

	/**
	 * 获取顶级Category
	 * 
	 * @return CategoryDef
	 */
	public CategoryDef getRootCategory() {
		return this.rootCategory;
	}

	/**
	 * 根据category找到他的叶子对象 也就是找它的子孙对象，当他的子孙对象不再派生子对象时，那么这个 子孙对象就是他的叶子对象
	 * 
	 * @param category
	 * @return List<CategoryDef> Category集合
	 */
	public List<CategoryDef> getLeafCategoryList(CategoryDef category) {
		List<CategoryDef> categoryList = new ArrayList<CategoryDef>();
		this.addLeafCategory(category, categoryList);
		return categoryList;
	}

	/**
	 * getLeafCategoryList()方法的内部方法
	 * 
	 * @param category
	 * @param categoryList
	 */
	private void addLeafCategory(CategoryDef category,
			List<CategoryDef> categoryList) {
		CategoryDef[] categorys = category.getChildCategorys();
		if (categorys == null) {
			categoryList.add(category);
			return;
		}
		for (int i = 0; i < categorys.length; i++) {
			this.addLeafCategory(categorys[i], categoryList);
		}
	}

	/**
	 * 根据category id找到category对象
	 * 
	 * @param id
	 *            (category id)
	 * @return CategoryDef
	 */
	public CategoryDef getCategoryById(String id) {
		return this.categoryMap.get(id);
	}

	/**
	 * 获取所有厂商
	 * 
	 * @return
	 */
	public String[] getVendorIds() {
		String[] ids = new String[vendor2DeviceTypes.size()];
		return this.vendor2DeviceTypes.keySet().toArray(ids);
	}

	/**
	 * 根据sysoid获取设备类型
	 * 
	 * @param sysoid
	 * @return
	 */
	public DeviceType getDeviceType(String sysoid) {
		return this.sysoid2DeviceType.get(sysoid);
	}

	/**
	 * 获取设备类型
	 * 
	 * @param
	 * @return
	 */
	Collection<DeviceType> getAllDeviceTypes() {
		return this.sysoid2DeviceType.values();
	}

	/**
	 * 根据模型ID获取设备类型
	 * 
	 * @param resourceId
	 * @return
	 */
	public DeviceType[] getDeviceTypeByResourceId(String resourceId) {
		List<DeviceType> types = this.resourceId2DeviceType.get(resourceId);
		if (null == types) {
			return null;
		}
		return types.toArray(new DeviceType[types.size()]);
	}

	/**
	 * 根据某厂商获取系统中支持的设备类型列表
	 * 
	 * @param vendorId
	 * @return
	 */
	public DeviceType[] getDeviceTypesByVendor(String vendorId) {
		List<DeviceType> list = this.vendor2DeviceTypes.get(vendorId);
		DeviceType[] types = new DeviceType[list.size()];
		return list.toArray(types);
	}

	public void reloadDeviceType() {
		String caplibsPath = OSUtil.getEnv("caplibs.path");

		if (!caplibsPath.endsWith(File.separator)) {
			caplibsPath += File.separator;
		}
		String caplibsPathDict = caplibsPath + "dictionary" + File.separator;
		DeviceType[] deviceTypes = this.loadDevTypes(caplibsPathDict
				+ "DeviceType.xml");
		this.putDeviceTypeMap(deviceTypes);
	}

	public CaplibAPIErrorCode removeDeviceType(String sysoid) {
		if (sysoid2DeviceType.containsKey(sysoid)) {
			sysoid2DeviceType.remove(sysoid);
			refactorMap();
			return CaplibAPIErrorCode.OK;
		} else {
			return CaplibAPIErrorCode.ADD_DEVICE_TYPE_04;
		}
	}
}
