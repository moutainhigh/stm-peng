package com.mainsteam.stm.capbase;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.*;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.caplib.state.ResourceState;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.util.OSUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;

/**
 * 文件名称: CapacityServiceImpl
 * <p>
 * 文件描述: 能力库接口
 * <p>
 * 版权所有: 版权所有(C)2005-2014
 * <p>
 * 公司: 美新翔盛
 * <p>
 * 内容摘要: 能力库接口的实现类，提供诸如获取资源对象，插件对象，分类对象等一系列的接口
 * <p>
 * 其他说明:
 * <p>
 * 完成日期：2019年11月7日
 * <p>
 * 修改记录1: 创建
 *
 * @author sunsht
 * @version 3
 */
public class CapacityServiceImpl implements CapacityService {
    private static final String NULL = "null";
    private static final String GENERAL_SNMP = "GeneralSnmp";
    private static final String _1_3_6_1_4_1 = "1.3.6.1.4.1.";
    public static final int AKM_LEVEL2 = 2;
    public static final int AKM_LEVEL3 = 3;
    public static final int AKM_LEVEL4 = 4;
    public static final int AKM_LEVEL5 = 5;
    public static final int AKM_LEVEL6 = 6;
    public ResourceRespositoy resourceRespositoy;

    public void start() throws Exception {
        if (null == this.resourceRespositoy) {
            this.resourceRespositoy = new ResourceRespositoy();
            this.resourceRespositoy.start();
        }
    }

    @Override
    public List<ResourceDef> getResourceDefList() {
        return this.resourceRespositoy.getResourceDefList();
    }

    @Override
    public ResourceDef getResourceDefById(String id) {
        return this.resourceRespositoy.getResourceDefById(id);
    }

    @Override
    public ResourceMetricDef getResourceMetricDef(String resourceId, String metricId) {
        return this.resourceRespositoy.getResourceMetricDef(resourceId, metricId);
    }

    @Override
    public List<CategoryDef> getCategoryList(int level) {
        return this.resourceRespositoy.getCategoryList(level);
    }

    @Override
    public CategoryDef getRootCategory() {
        return this.resourceRespositoy.getRootCategory();
    }

    @Override
    public List<CategoryDef> getLeafCategoryList(CategoryDef category) {
        return this.resourceRespositoy.getLeafCategoryList(category);
    }

    @Override
    public CategoryDef getCategoryById(String id) {
        return this.resourceRespositoy.getCategoryById(id);
    }

    @Override
    public PluginDef getPluginDef(String pluginId) {
        return this.resourceRespositoy.getPluginDef(pluginId);
    }

    @Override
    public String[] getVendorIds() {
        return this.resourceRespositoy.getVendorIds();
    }

    @Override
    public DeviceType getDeviceType(String sysoid) {
        if (StringUtils.isEmpty(sysoid) || NULL.equalsIgnoreCase(sysoid)) {
            return null;
        }
        if (!StringUtils.startsWith(sysoid, _1_3_6_1_4_1)) {
            return null;
        }
        DeviceType type = this.resourceRespositoy.getDeviceType(sysoid);
        if (null == type || getResourceDefById(type.getResourceId()) == null) {
            type = new DeviceType();
            type.setResourceId(GENERAL_SNMP);
            type.setSysOid(sysoid);
            type.setType(DeviceTypeEnum.Other);
        }
        return type;
    }

    @Override
    public DeviceType[] getDeviceTypesByVendor(String vendorId) {
        return this.resourceRespositoy.getDeviceTypesByVendor(vendorId);
    }

    @Override
    public String getResourceId(String sysoid) {
        if (null == sysoid || NULL.equalsIgnoreCase(sysoid)) {
            return null;
        }
        if (!StringUtils.startsWith(sysoid, _1_3_6_1_4_1)) {
            return null;
        }
        DeviceType devType = this.resourceRespositoy.getDeviceType(sysoid);
        if (null != devType && getResourceDefById(devType.getResourceId()) != null) {
            return devType.getResourceId();
        } else {
            return GENERAL_SNMP;
        }
    }

    @Override
    public DeviceType[] getDeviceTypeByResourceId(String resourceId) {
        return this.resourceRespositoy.getDeviceTypeByResourceId(resourceId);
    }

    @Override
    public boolean addTypeBySysoidAndResourceId(String sysoid, String resourceId) {
        DeviceType[] dts = this.resourceRespositoy.getDeviceTypeByResourceId(resourceId);
        if (null == dts || dts.length == 0) {
            return false;
        }
        DeviceType dt = dts[0];

        DeviceType newDT = new DeviceType(dt);
        newDT.setSysOid(sysoid);
        newDT.setSortId(dt.getSortId() + 10);
        this.addType(newDT);
        return true;
    }

    /**
     * 根据sysoid判断模型是否存在是否
     *
     * @param sysoid
     * @return
     */
    @Override
    public boolean isExistResourceBySysoid(String sysoid) {
        if (null == sysoid || NULL.equalsIgnoreCase(sysoid)) {
            return false;
        }
        if (!StringUtils.startsWith(sysoid, _1_3_6_1_4_1)) {
            return false;
        }
        DeviceType devType = this.resourceRespositoy.getDeviceType(sysoid);
        if (null != devType && getResourceDefById(devType.getResourceId()) != null) {
            return true;
        }
        return false;
    }

    /**
     * 前台使用方法：<br/>
     * 先用getDeviceTypeByResourceId获取DeviceType[]，<br/>
     * 再选出第一个并设置页面填写的东西，比如型号， 再调用addType，增加。
     */
    @Override
    public CaplibAPIResult addType(DeviceType devType) {
        if (null == devType) {
            return new CaplibAPIResult(false, CaplibAPIErrorCode.ADD_DEVICE_TYPE_01);
        }
        // 写文件
        CaplibAPIErrorCode sucess = DeviceTypeUtil.addTypeToFile(devType);
        if (!CaplibAPIErrorCode.OK.equals(sucess)) {
            return new CaplibAPIResult(false, sucess);
        }
        // 写内存
        sucess = this.resourceRespositoy.addDeviceType(devType);
        if (!CaplibAPIErrorCode.OK.equals(sucess)) {
            return new CaplibAPIResult(false, sucess);
        }
        return CaplibAPIResult.SUCESSFUL;
    }

    @Override
    public List<AccidentKMType> getAccidentKMTypes() {
        List<AccidentKMType> allTypes = new ArrayList<AccidentKMType>();

        // 增加分类
        CategoryDef rootCategory = this.resourceRespositoy.getRootCategory();
        CategoryDef[] childCategories = rootCategory.getChildCategorys();
        for (CategoryDef childCategory : childCategories) {
            String childId = childCategory.getId();
            AccidentKMType kmCategoryType = new AccidentKMType(rootCategory.getId(), AKM_LEVEL2, childId, childCategory.getName(), KMTypeEnum.Category);
            allTypes.add(kmCategoryType);
            // 下级别分类
            CategoryDef[] nextCategories = childCategory.getChildCategorys();
            if (null == nextCategories) {
                continue;
            }
            for (CategoryDef kmSubCategoryType : nextCategories) {
                String nextId = kmSubCategoryType.getId();
                AccidentKMType kmNextType = new AccidentKMType(childId, AKM_LEVEL3, nextId, kmSubCategoryType.getName(), KMTypeEnum.Category);
                allTypes.add(kmNextType);

                // 模型
                ResourceDef[] mainResources = kmSubCategoryType.getResourceDefs();
                if (null == mainResources) {
                    continue;
                }
                for (ResourceDef mainResource : mainResources) {
                    List<AccidentKMType> resTypes = getKMResourceTypes(kmSubCategoryType.getId(), mainResource);
                    if (null != resTypes && resTypes.size() > 0) {
                        allTypes.addAll(resTypes);
                    }
                }
            }
        }
        return allTypes;
    }

    private List<AccidentKMType> getKMResourceTypes(String categoryId, ResourceDef mainResource) {
        List<AccidentKMType> allTypes = new ArrayList<AccidentKMType>();
        String mainResId = mainResource.getId();
        AccidentKMType mainResType = new AccidentKMType(categoryId, AKM_LEVEL4, mainResId, mainResource.getName(), KMTypeEnum.MainResource);
        mainResType.setDescription(mainResource.getDescription());
        allTypes.add(mainResType);
        // 主指标
        ResourceMetricDef[] mainMetrics = mainResource.getMetricDefs();
        for (ResourceMetricDef mainMetric : mainMetrics) {
            if (MetricTypeEnum.PerformanceMetric.equals(mainMetric.getMetricType())) {
                AccidentKMType mainMetricType = new AccidentKMType(mainResId, AKM_LEVEL6, mainMetric.getId(), mainMetric.getName(), KMTypeEnum.Metric);
                mainMetricType.setDescription(mainMetric.getDescription());
                allTypes.add(mainMetricType);
            }
        }
        // 子资源
        ResourceDef[] childResources = mainResource.getChildResourceDefs();
        if (null == childResources) {
            return allTypes;
        }
        for (ResourceDef childResource : childResources) {
            String childResId = childResource.getId();
            AccidentKMType subResType = new AccidentKMType(mainResId, AKM_LEVEL5, childResId, childResource.getName(), KMTypeEnum.SubResource);
            subResType.setDescription(childResource.getDescription());
            allTypes.add(subResType);
            // 子指标
            ResourceMetricDef[] childMetrics = childResource.getMetricDefs();
            for (ResourceMetricDef childMetric : childMetrics) {
                if (MetricTypeEnum.PerformanceMetric.equals(childMetric.getMetricType())) {
                    AccidentKMType subMetricType = new AccidentKMType(childResId, AKM_LEVEL6, childMetric.getId(), childMetric.getName(), KMTypeEnum.Metric);
                    subMetricType.setDescription(childMetric.getDescription());
                    allTypes.add(subMetricType);
                }
            }
        }
        return allTypes;
    }

    @Override
    public List<MonitorModelKMType> getMonitorModelKMTypes() {
        List<MonitorModelKMType> allTypes = new ArrayList<MonitorModelKMType>();

        CategoryDef rootCategory = this.resourceRespositoy.getRootCategory();
        CategoryDef[] childCategories = rootCategory.getChildCategorys();
        for (CategoryDef childCategory : childCategories) {
            String id = childCategory.getId();
            MonitorModelKMType kmType = new MonitorModelKMType(rootCategory.getId(), AKM_LEVEL3, id, childCategory.getName(), KMTypeEnum.Category);
            allTypes.add(kmType);

            CategoryDef[] nextCategories = childCategory.getChildCategorys();
            if (null == nextCategories) {
                continue;
            }
            for (CategoryDef nextCategory : nextCategories) {
                String nextId = nextCategory.getId();
                MonitorModelKMType kmNextType = new MonitorModelKMType(id, AKM_LEVEL4, nextId, nextCategory.getName(), KMTypeEnum.Category);
                allTypes.add(kmNextType);
            }
        }
        return allTypes;
    }

    public static void main(String[] argv) throws Exception {

        String oc4Path = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
        System.setProperty("caplibs.path", oc4Path);

        String caplibsPath = OSUtil.getEnv("caplibs.path");
        if (null == caplibsPath || caplibsPath.isEmpty()) {
            System.out.println("Please set ENV: caplibs.path");
            System.exit(1);
        }

        CapacityService capSvc = new CapacityServiceImpl();
        ((CapacityServiceImpl) capSvc).start();

        // System.out.println("----------show Accident KM Types----------");
        File file1 = new File("./AccidentKMTypes.txt");
        List<AccidentKMType> akmTypes = capSvc.getAccidentKMTypes();
        List<String> lines1 = new ArrayList<String>();
        for (AccidentKMType akmType : akmTypes) {
            lines1.add(akmType.toString());
        }
        FileUtils.writeLines(file1, lines1);

        // System.out.println("----------Monitor Model KM Types----------");
        File file2 = new File("./MonitorModelKMTypes.txt");
        List<MonitorModelKMType> mmKmTypes = capSvc.getMonitorModelKMTypes();
        List<String> lines2 = new ArrayList<String>();
        for (MonitorModelKMType mmKmType : mmKmTypes) {
            lines2.add(mmKmType.toString());
        }
        FileUtils.writeLines(file2, lines2);
    }

    public static void main1(String[] argv) throws Exception {

        String oc4Path = "/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/cap_libs";
        System.setProperty("caplibs.path", oc4Path);
        CapacityServiceImpl impl = new CapacityServiceImpl();
        impl.start();
        List<XmlCheckResult> list = impl.checkAllFiles(oc4Path);
        System.out.println(list.size());
        for (XmlCheckResult result : list) {
            System.out.println(result.toString());
        }
    }

    @Override
    public List<XmlCheckResult> checkAllFiles(String caplibPath) {
        return CheckUtil.checkAllFiles(caplibPath);
    }

    @Override
    public void reloadDeviceType() {
        this.resourceRespositoy.reloadDeviceType();
    }

    @Override
    public DeviceType[] getAllDeviceTypes() {
        Collection<DeviceType> types = resourceRespositoy.getAllDeviceTypes();
        return types.toArray(new DeviceType[types.size()]);
    }

    // public static void main(String[] argv) {
    // String sysoid = "1.3.6.1.4.1.2011.3.5";
    // int idx = sysoid.indexOf('.', _1_3_6_1_4_1.length());
    // String startPart = sysoid.substring(0, idx + 1);
    // System.out.println(startPart);
    // }

    @Override
    public String getDefaultResourceIdSameVendor(String sysoid) {
        if (StringUtils.isEmpty(sysoid)) {
            return null;
        }
        DeviceType devType = this.resourceRespositoy.getDeviceType(sysoid);
        if (null != devType) {
            return devType.getResourceId();
        }
        // 智能匹配
        // 1.先得到该厂商的所有设备
        int idx = sysoid.indexOf('.', _1_3_6_1_4_1.length());
        String startPart = sysoid.substring(0, idx + 1);
        Collection<DeviceType> allTypes = this.resourceRespositoy.getAllDeviceTypes();
        List<DeviceType> list = new ArrayList<DeviceType>();
        for (DeviceType type : allTypes) {
            if (StringUtils.contains(type.getSysOid(), startPart)) {
                list.add(type);
            }
        }
        int fromIndex = sysoid.length() - 1;
        for (int i = 0; i < 10; i++) {
            if (fromIndex <= idx + 1) {
                break;
            }
            fromIndex = sysoid.lastIndexOf('.', fromIndex);
            String partSysoid = sysoid.substring(0, fromIndex);

            for (DeviceType type : list) {
                String mySysoid = type.getSysOid();
                if (StringUtils.contains(mySysoid, partSysoid)) {
                    return type.getResourceId();
                }
            }
        }
        return null;
    }

    @Override
    public CaplibAPIResult removeType(String sysoid) {
        if (null == sysoid || "".equals(sysoid)) {
            return new CaplibAPIResult(false, CaplibAPIErrorCode.ADD_DEVICE_TYPE_01);
        }
        // 写内存
        CaplibAPIErrorCode sucess = this.resourceRespositoy.removeDeviceType(sysoid);
        if (!CaplibAPIErrorCode.OK.equals(sucess)) {
            return new CaplibAPIResult(false, sucess);
        }
        // 写文件
        sucess = DeviceTypeUtil.removeTypeToFile(sysoid);
        if (!CaplibAPIErrorCode.OK.equals(sucess)) {
            return new CaplibAPIResult(false, sucess);
        }

        return CaplibAPIResult.SUCESSFUL;
    }

    @Override
    public Availability evaluateResourceAvailabilityByMetricState(String resourceId, Map<String, MetricStateEnum> metricStateMap) {
        ResourceDef resourceDef = getResourceDefById(resourceId);
        if (resourceDef == null)
            throw new IllegalArgumentException("Can not find the specified resource.");
        if (metricStateMap == null || metricStateMap.isEmpty())
            return Availability.UNKNOWN;
        Iterator<Map.Entry<String, MetricStateEnum>> iterator = metricStateMap.entrySet().iterator();
        boolean unknown = true;
        if (resourceDef.getCategory() != null && (resourceDef.getCategory().getParentCategory().getId().equals(CapacityConst.NETWORK_DEVICE) || resourceDef.getCategory().getParentCategory().getId().equals(CapacityConst.HOST))) {
            while (iterator.hasNext()) {
                Map.Entry<String, MetricStateEnum> entry = iterator.next();
//                String metricId = entry.getKey();
                MetricStateEnum metricState = entry.getValue();
                if (metricState == MetricStateEnum.NORMAL) {
                    return Availability.AVAILABLE;
                } else if (metricState == MetricStateEnum.CRITICAL) {
                    unknown = false;
                }
            }
            return unknown ? Availability.UNKNOWN : Availability.UNAVAILABLE;
        } else {
            while (iterator.hasNext()) {
                Map.Entry<String, MetricStateEnum> entry = iterator.next();
//                String metricId = entry.getKey();
                MetricStateEnum metricState = entry.getValue();
                if (metricState == MetricStateEnum.CRITICAL) {
                    return Availability.UNAVAILABLE;
                } else if (metricState == MetricStateEnum.NORMAL) {
                    unknown = false;
                }
            }
            return unknown ? Availability.UNKNOWN : Availability.AVAILABLE;
        }
    }

    @Override
    public Availability evaluateResourceAvailability(String resourceId, Map<String, String> metricMap) {
        if (resourceId == null)
            throw new NullPointerException("resourceId is null.");
        ResourceDef resourceDef;
        if ((resourceDef = getResourceDefById(resourceId)) == null)
            throw new IllegalArgumentException("Can not find the specified resource.");
        if (metricMap == null || metricMap.isEmpty())
            return Availability.UNKNOWN;
//        ResourceMetricDef[] metricDefs = resourceDef.getMetricDefs();
        Iterator<Map.Entry<String, String>> iterator = metricMap.entrySet().iterator();
        boolean unknown = true;
        if (resourceDef.getCategory() != null && (resourceDef.getCategory().getParentCategory().getId().equals(CapacityConst.NETWORK_DEVICE) || resourceDef.getCategory().getParentCategory().getId().equals(CapacityConst.HOST))) {

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String metricId = entry.getKey();
                int metricValue;
                try {
                    metricValue = Integer.decode(entry.getValue());
                } catch (Exception e) {
                    continue;
                }
//            for (ResourceMetricDef metricDef : metricDefs) {
//                if (metricDef.getId().equals(metricId)) {
//                    if (metricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric)) {
                if (Availability.valueOf(metricValue) == Availability.AVAILABLE) {
                    return Availability.AVAILABLE;
                } else if (Availability.valueOf(metricValue) == Availability.UNAVAILABLE) {
                    unknown = false;
                }
//                    }
//                    break;
//                }
//            }
            }
            return unknown ? Availability.UNKNOWN : Availability.UNAVAILABLE;
        } else {
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String metricId = entry.getKey();
                int metricValue;
                try {
                    metricValue = Integer.decode(entry.getValue());
                } catch (Exception e) {
                    continue;
                }
//            for (ResourceMetricDef metricDef : metricDefs) {
//                if (metricDef.getId().equals(metricId)) {
//                    if (metricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric)) {
                if (Availability.valueOf(metricValue) == Availability.UNAVAILABLE) {
                    return Availability.UNAVAILABLE;
                } else if (Availability.valueOf(metricValue) == Availability.AVAILABLE) {
                    unknown = false;
                }
//                    }
//                    break;
//                }
//            }
            }
            return unknown ? Availability.UNKNOWN : Availability.AVAILABLE;
        }

    }

    @Override
    public Collectibility evaluateResourceCollectibility(String resourceId, Map<String, String> metricMap) {
        if (resourceId == null)
            throw new NullPointerException("resourceId is null.");
        ResourceDef resourceDef;
        if ((resourceDef = getResourceDefById(resourceId)) == null)
            throw new IllegalArgumentException("can not find the specified resource.");
        if (metricMap == null || metricMap.isEmpty())
            return Collectibility.COLLECTIBLE;
//        ResourceMetricDef[] metricDefs = resourceDef.getMetricDefs();
        Iterator<Map.Entry<String, String>> iterator = metricMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String metricId = entry.getKey();
            int metricValue;
            try {
                metricValue = Integer.decode(entry.getValue());
            } catch (Exception e) {
                continue;
            }
//            for (ResourceMetricDef metricDef : metricDefs) {
//                if (metricDef.getId().equals(metricId)) {
//                    if (metricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric)) {
            if (Collectibility.valueOf(metricValue) == Collectibility.UNCOLLECTIBLE) {
                return Collectibility.UNCOLLECTIBLE;
            }
//                    }
//                    break;
//                }
//            }
        }
        return Collectibility.COLLECTIBLE;
    }

    @Override
    public ResourceState evaluateResourceState(String resourceId, Map<String, String> metricMap) {
        return new ResourceState(evaluateResourceAvailability(resourceId, metricMap), evaluateResourceCollectibility(resourceId, metricMap));
    }
}
