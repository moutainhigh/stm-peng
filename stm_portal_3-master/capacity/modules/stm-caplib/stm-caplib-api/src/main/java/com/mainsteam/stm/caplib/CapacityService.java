package com.mainsteam.stm.caplib;

import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.AccidentKMType;
import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.caplib.dict.MonitorModelKMType;
import com.mainsteam.stm.caplib.dict.XmlCheckResult;
import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.caplib.state.ResourceState;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

import java.util.List;
import java.util.Map;

/**
 * 文件名称: CapacityService
 * <p>
 * 文件描述: 能力库接口
 * <p>
 * 版权所有: 版权所有(C)2005-2014
 * <p>
 * 公司: 美新翔盛
 * <p>
 * 内容摘要: 能力库接口，提供诸如获取资源对象，插件对象，分类对象等一系列的接口
 * <p>
 * 其他说明:
 * <p>
 * 完成日期：2019年11月7日
 * <p>
 * 修改记录1: 创建
 *
 * @author sunsht
 * @author lich
 * @version 4.2.0
 * @since 4.1.0
 */
public interface CapacityService {

    /**
     * 获取故障分类的知识类型
     * <p>
     * 其中有：
     * <p>
     * 知识分类2（一级分类） 例如：主机
     * <p>
     * 知识分类3（二级分类）Linux
     * <p>
     * 知识分类4（监控模型） 例如：Linux_snmp
     * <p>
     * 知识分类5（子资源） 例如：接口
     * <p>
     * 知识分类6（指标对应故障）带宽利用率
     *
     * @return
     */
    public List<AccidentKMType> getAccidentKMTypes();

    /**
     * 获取能力分类（监控模型）的知识类型
     * <p>
     * 其中有：
     * <p>
     * 知识分类3 主机、网络设备等
     * <p>
     * 知识分类4 Windows服务器、交换机等
     *
     * @return
     */
    public List<MonitorModelKMType> getMonitorModelKMTypes();

    /**
     * 获取资源集合（包含主资源和子资源）
     *
     * @return
     */
    public List<ResourceDef> getResourceDefList();

    /**
     * 根据resource id获取ResourceDef对象
     *
     * @param id
     * @return
     */
    public ResourceDef getResourceDefById(String id);

    /**
     * 根据plugin id获取PluginDef对象
     *
     * @param pluginId
     * @return
     */
    public PluginDef getPluginDef(String pluginId);

    /**
     * 根据resource id和 metric id获取ResourceMetricDef对象
     *
     * @param resourceId
     * @param metricId
     * @return
     */
    public ResourceMetricDef getResourceMetricDef(String resourceId,
                                                  String metricId);

    /**
     * 根据level级别 找到Category对象集合 Level = 0 : 一级对象（顶级对象，根对象） Level = 1 :
     * 二级对象（一级对象的子对象集合） Level = 2 : 三级对象（所有二级对象的子对象集合）
     *
     * @param level
     * @return
     */
    public List<CategoryDef> getCategoryList(int level);

    /**
     * 获取顶级Category对象
     *
     * @return
     */
    public CategoryDef getRootCategory();

    /**
     * 根据category对象 获取它的叶子对象集合 叶子对象含义：当一个对象的子孙对象不在往下派生子对象， 那么这个对象就是它的叶子对象。
     *
     * @param category
     * @return
     */
    public List<CategoryDef> getLeafCategoryList(CategoryDef category);

    /**
     * 根据category id 获取category对象
     *
     * @param id
     * @return
     */
    public CategoryDef getCategoryById(String id);

    /**
     * 返回所有厂商id
     *
     * @return
     */
    public String[] getVendorIds();

    /**
     * 根据sysoid返回模型Id
     *
     * @param sysoid
     * @return
     */
    public String getResourceId(String sysoid);

    /**
     * 根据sysoid返回模型Id，如果不能精确匹配，就返回该厂商默认的resourceId
     *
     * @param sysoid
     * @return
     */
    public String getDefaultResourceIdSameVendor(String sysoid);

    /**
     * 根据resourceId返回所有的相关型号
     */
    public DeviceType[] getDeviceTypeByResourceId(String resourceId);

    /**
     * 根据sysoid返回设备类型
     *
     * @param sysoid
     * @return
     */
    public DeviceType getDeviceType(String sysoid);

    /**
     * 返回所有设备类型
     *
     * @param
     * @return
     */
    public DeviceType[] getAllDeviceTypes();

    // /**
    // * 根据传入的是网络设备还是主机，返回对应的设备类型
    // *
    // * @param category
    // * ：Server返回主机的 否则返回网络设备的
    // * @return
    // */
    // public DeviceType[] getDeviceTypeByCategory(DeviceTypeCategoryEnum
    // category);

    /**
     * 根据厂商类型返回设备类型数组
     *
     * @param vendorId
     * @return
     */
    public DeviceType[] getDeviceTypesByVendor(String vendorId);

    /**
     * 增加设备型号，前提是模型ID必须已经存在
     *
     * @param sysoid     新增的主机或网络设备的sysoid
     * @param resourceId 新增的主机或网络设备的模型ID
     * @return true 表示成功 false 表示失败
     */
    public boolean addTypeBySysoidAndResourceId(String sysoid, String resourceId);

    /**
     * 增加设备型号，用于知道该设备类型的所有细节：厂商、模型id、sysoid等（如果模型id存在，
     * 推荐用上面的方法addTypeBySysoidAndResourceId）
     *
     * @param type
     * @return CaplibAPIResult, 里面有成功还是错误的标志，以及错误码
     */
    public CaplibAPIResult addType(DeviceType type);


    /**
     * 删除设备型号，根据sysoid
     *
     * @return CaplibAPIResult, 里面有成功还是错误的标志，以及错误码
     */
    public CaplibAPIResult removeType(String sysoid);

    // /**
    // * 模型文件校验
    // *
    // * @param resourceFileName
    // * 模型文件
    // * @param collectFileName
    // * 模型采集文件，可以为多个，也可以为null，如果为null只检查模型文件
    // * @return CapXmlCheckResult
    // */
    // public XmlCheckResult checkResourceCollect(String resourceFileName,
    // String[] collectFileName);

    /**
     * 所有模型文件校验，校验包括：模型文件自身的校验以及关联性校验
     *
     * @param caplibPath 模型文件的目录
     * @return CapXmlCheckResult
     */
    public List<XmlCheckResult> checkAllFiles(String caplibPath);

    /**
     * 重新装载设备类型，用于重新部署设备型号文件以后
     */
    public void reloadDeviceType();


    /**
     * 根据sysoid判断模型是否存在是否
     *
     * @param sysoid
     * @return
     */
    boolean isExistResourceBySysoid(String sysoid);

    /**
     * Evaluate the resource availability by metric states using built-in rules, any types of metrics will be examined.
     *
     * @param resourceId ID of the resource to evaluate
     * @param metricStateMap  a map of metrics for evaluating, the key is metric ID and the value is metric state
     * @return the availability of the resource
     * @throws NullPointerException     if <code>resourceId</code> is null
     * @throws IllegalArgumentException if <code>resourceId</code> do not exist
     */
    public Availability evaluateResourceAvailabilityByMetricState(String resourceId, Map<String, MetricStateEnum> metricStateMap);

    /**
     * Evaluate the resource availability with metrics using built-in rules, any types of metrics will be examined.
     *
     * @param resourceId ID of the resource to evaluate
     * @param metricMap  a map of metrics for evaluating, the key is metric ID and the value is metric value
     * @return the availability of the resource
     * @throws NullPointerException     if <code>resourceId</code> is null
     * @throws IllegalArgumentException if <code>resourceId</code> do not exist
     */
    public Availability evaluateResourceAvailability(String resourceId, Map<String, String> metricMap);

    /**
     * Evaluate the resource collectibility with metrics using built-in rules, any types of metrics will be examined.
     *
     * @param resourceId ID of the resource to evaluate
     * @param metricMap  a map of metrics for evaluating, the key is metric ID and the value is metric value
     * @return the collectibility of the resource
     * @throws NullPointerException     if <code>resourceId</code> is null
     * @throws IllegalArgumentException if <code>resourceId</code> do not exist.
     */
    public Collectibility evaluateResourceCollectibility(String resourceId, Map<String, String> metricMap);

    /**
     * Evaluate the resource state with metrics using built-in rules, any types of metrics will be examined.
     *
     * @param resourceId ID of the resource to evaluate
     * @param metricMap  a map of metrics for evaluating, the key is metric ID and the value is metric value
     * @return the state of the resource
     * @throws NullPointerException     if <code>resourceId</code> is null
     * @throws IllegalArgumentException if <code>resourceId</code> do not exist
     */
    public ResourceState evaluateResourceState(String resourceId, Map<String, String> metricMap);
}
