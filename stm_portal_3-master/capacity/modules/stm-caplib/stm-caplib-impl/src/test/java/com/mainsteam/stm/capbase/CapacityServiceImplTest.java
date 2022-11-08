package com.mainsteam.stm.capbase;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.*;

import javax.annotation.Resource;

import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.caplib.state.ResourceState;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.DeviceTypeEnum;
import com.mainsteam.stm.caplib.plugin.PluginConnectSetting;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/services/public-caplib-beans.xml"})
public class CapacityServiceImplTest {
    @Test
    public void evaluateResourceState() throws Exception {
        HashMap<String, String> metricMap = new HashMap<>();
        metricMap.put("availability", "1");
        metricMap.put("WMIAvailability", "15");
        ResourceState state = capacityService.evaluateResourceState("windowswmi", metricMap);
        System.out.println(state.availability);
        System.out.println(state.collectibility);
    }

    @Resource
    private CapacityService capacityService;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//		set caplibs.path to your own path
        System.setProperty("caplibs.path", "F:\\OC4\\Capacity\\cap_libs");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDefaultResourceId() {

    }

    @Test
    public void testGetDeviceTypeList() {
        System.out.println("TESTCASE_CAP000---测试读取设备类型列表...");
        String[] vendorIds = capacityService.getVendorIds();
        System.out.println(vendorIds.length);
    }

    @Test
    public void testRemoveDeviceAndAddDeviceType() {
    }

    @Test
    public void testAddDeviceType() {
        DeviceType devType = new DeviceType();
        devType.setModelNumber("model1");
        devType.setOsType("windows");
        devType.setResourceId("windowssnmp");
        devType.setSeries("series1");
        devType.setSortId(9999);
        devType.setSysOid("1.3.6.31.xx11");
        devType.setType(DeviceTypeEnum.Host);
        devType.setVendorIcon("windows.png");
        devType.setVendorName("微软");
        devType.setVendorId("Microsoft");
        devType.setVendorNameEn("Microsoft");
        capacityService.addType(devType);

        DeviceType dd = capacityService.getDeviceType("1.3.6.31.xx11");
        org.junit.Assert.assertNotNull(dd);
        System.out.println("增加完成");
    }

    @Test
    public void testAddTypeBySysoidAndResourceId() {
        String sysoid = "1.3.6.1.4.1.9.2.451";
        String resourceId = "CiscoFirewall";
        boolean b = capacityService.addTypeBySysoidAndResourceId(sysoid,
                resourceId);
        org.junit.Assert.assertTrue(b);
        DeviceType dd = capacityService.getDeviceType(sysoid);
        org.junit.Assert.assertNotNull(dd);
        System.out.println("增加完成");
    }

    @Test
    public void testGetPluginInitParameterMap() {
        System.out.println("TESTCASE_CAP000---测试读取参数...");
        ResourceDef mysqlResourceDef = capacityService
                .getResourceDefById("MySQL");
        Map<String, PluginInitParameter[]> map = mysqlResourceDef
                .getPluginInitParameterMap();
        for (PluginInitParameter[] ps : map.values()) {
            for (PluginInitParameter p : ps) {
                System.out.println(p.getId() + ",display=" + p.isDisplay());
            }
        }
    }

    @Test
    public void testGetCategoryList() {
        System.out.println("TESTCASE_CAP001---测试读取模型分类列表...");
        {
            List<CategoryDef> list = capacityService.getCategoryList(0);
            System.out.println("0级分类：" + list.size());
            org.junit.Assert.assertTrue(list.size() == 1);
            assertTrue(list.get(0).getId().equals("Resource"));
            assertTrue(list.get(0).getName().equals("资源"));
            assertTrue(list.get(0).getParentCategory() == null);

            CategoryDef[] childCategorys = list.get(0).getChildCategorys();
            assertTrue(childCategorys.length > 4);
            // assertTrue(childCategorys[0].getId().equals("NetworkDevice"));
            // assertTrue(childCategorys[1].getId().equals("Host"));
            // assertTrue(childCategorys[0].getParentCategory().hashCode() ==
            // list
            // .get(0).hashCode());
            // assertTrue(childCategorys[1].getParentCategory().hashCode() ==
            // list
            // .get(0).hashCode());
            //
            // ResourceDef[] res = list.get(0).getChildCategorys()[0]
            // .getChildCategorys()[0].getResourceDefs();
            // assertTrue(res.length == 1);
            // assertTrue(res[0].getId().equals("CiscoSwitch"));
            // assertTrue(res[0].getParentResourceDef() == null);
        }
        {
            List<CategoryDef> list = capacityService.getCategoryList(1);
            System.out.println("1级分类：" + list.size());
            org.junit.Assert.assertTrue(list.size() > 4);
            // assertTrue(list.get(0).getId().equals("NetworkDevice"));
            // assertTrue(list.get(1).getId().equals("Host"));
            // assertTrue(list.get(0).getParentCategory().getId()
            // .equals("Resource"));
            // assertTrue(list.get(1).getParentCategory().getId()
            // .equals("Resource"));
            //
            // assertTrue(list.get(1).getChildCategorys() == null);
            // CategoryDef[] childCategorys = list.get(0).getChildCategorys();
            // assertTrue(childCategorys.length == 3);
            // assertTrue(childCategorys[0].getId().equals("Switch"));
            // assertTrue(childCategorys[1].getId().equals("Router"));
            // assertTrue(childCategorys[2].getId().equals("Firewall"));
            // assertTrue(childCategorys[0].getParentCategory().hashCode() ==
            // list
            // .get(0).hashCode());
            // assertTrue(childCategorys[1].getParentCategory().hashCode() ==
            // list
            // .get(0).hashCode());
            // assertTrue(childCategorys[2].getParentCategory().hashCode() ==
            // list
            // .get(0).hashCode());
            //
            // assertTrue(list.get(0).getResourceDefs() == null);
            // assertTrue(list.get(1).getResourceDefs() == null);
        }
        {
            List<CategoryDef> list = capacityService.getCategoryList(2);
            System.out.println("2级分类：" + list.size());
            // org.junit.Assert.assertTrue(list.size() == 3);
            // assertTrue(list.get(0).getId().equals("Switch"));
            // assertTrue(list.get(1).getId().equals("Router"));
            // assertTrue(list.get(2).getId().equals("Firewall"));
            // assertTrue(list.get(0).getParentCategory().getId()
            // .equals("NetworkDevice"));
            // assertTrue(list.get(1).getParentCategory().getId()
            // .equals("NetworkDevice"));
            // assertTrue(list.get(2).getParentCategory().getId()
            // .equals("NetworkDevice"));
            //
            // assertTrue(list.get(0).getChildCategorys() == null);
            // assertTrue(list.get(1).getChildCategorys() == null);
            // assertTrue(list.get(2).getChildCategorys() == null);
            //
            // assertTrue(list.get(0).getResourceDefs() != null);
            // assertTrue(list.get(1).getResourceDefs() == null);
            // assertTrue(list.get(2).getResourceDefs() == null);
        }

        {
            List<CategoryDef> list = capacityService.getCategoryList(3);
            System.out.println("3级分类：" + (list == null ? 0 : list.size()));
            assertTrue(list == null);
        }
    }

    @Test
    public void testGetRootCategory() {
        System.out.println("TESTCASE_CAP002---测试读取根模型分类...");
        CategoryDef rootCategory = capacityService.getRootCategory();
        org.junit.Assert.assertNotNull(rootCategory);
        System.out.println("根分类id：" + rootCategory.getId());
        assertTrue(rootCategory.getId().equals("Resource"));
        assertNull(rootCategory.getParentCategory());

        CategoryDef[] childCategorys = rootCategory.getChildCategorys();
        assertTrue(childCategorys.length > 4);
        // assertTrue(childCategorys[0].getId().equals("NetworkDevice"));
        // assertTrue(childCategorys[1].getId().equals("Host"));
        // assertTrue(childCategorys[0].getParentCategory().hashCode() ==
        // rootCategory
        // .hashCode());
        // assertTrue(childCategorys[1].getParentCategory().hashCode() ==
        // rootCategory
        // .hashCode());
        //
        // ResourceDef[] res = rootCategory.getResourceDefs();
        // assertTrue(res == null);
    }

    @Test
    public void testGetLeafCategoryList() {
        System.out.println("TESTCASE_CAP003---测试读取叶模型分类...");
        CategoryDef rootCategory = capacityService.getRootCategory();
        List<CategoryDef> leafCategoryDefs = capacityService
                .getLeafCategoryList(rootCategory);
        org.junit.Assert.assertNotNull(leafCategoryDefs);

        System.out.println("叶子分类：" + leafCategoryDefs.size());
        org.junit.Assert.assertTrue(leafCategoryDefs.size() > 6);
    }

    @Test
    public void testGetCategoryById() {
        {
            System.out.println("TESTCASE_CAP004---测试根据CategoryID读取Category...");
            CategoryDef rootCategory = capacityService
                    .getCategoryById("Resource");
            org.junit.Assert.assertNotNull(rootCategory);
            CategoryDef[] childCategorys = rootCategory.getChildCategorys();
            org.junit.Assert.assertNotNull(childCategorys);
            System.out.println("子分类：" + childCategorys.length);
            org.junit.Assert.assertTrue(childCategorys.length > 0);
        }

        {
            CategoryDef category = capacityService
                    .getCategoryById("NetworkDevice");
            org.junit.Assert.assertNotNull(category);
            assertTrue(category.getParentCategory().getId().equals("Resource"));
            CategoryDef[] childCategorys = category.getChildCategorys();
            org.junit.Assert.assertNotNull(childCategorys);
            System.out.println("子分类：" + childCategorys.length);
            org.junit.Assert.assertTrue(childCategorys.length > 3);

            Set<String> networkResources = new HashSet<String>();
            networkResources.add("Router");
            networkResources.add("Switch");
            networkResources.add("Firewall");
            assertTrue(networkResources.contains(childCategorys[0].getId()));
            assertTrue(childCategorys[0].getParentCategory().hashCode() == category
                    .hashCode());
            assertTrue(networkResources.contains(childCategorys[1].getId()));
            assertTrue(childCategorys[1].getParentCategory().hashCode() == category
                    .hashCode());
            assertTrue(networkResources.contains(childCategorys[2].getId()));
            assertTrue(childCategorys[2].getParentCategory().hashCode() == category
                    .hashCode());
        }

    }

    @Test
    public void testGetResourceDefList() {
        System.out.println("TESTCASE_CAP005---测试所有模型列表读取...");
        List<ResourceDef> list = capacityService.getResourceDefList();
        org.junit.Assert.assertNotNull(list);
        System.out.println("所有resource:" + list.size());
        org.junit.Assert.assertTrue(list.size() > 2);
        // if (list.get(1).getId().contains("Cisco")) {
        // assertTrue(list.get(1).getId().equals("CiscoSwitch"));
        // assertTrue(list.get(0).getId().equals("CiscoSwitchNIC"));
        // assertNull(list.get(1).getParentResourceDef());
        // assertTrue(list.get(0).getParentResourceDef().hashCode() == list
        // .get(1).hashCode());
        //
        // assertTrue(list.get(1).getChildResourceDefs()[0].hashCode() == list
        // .get(0).hashCode());
        // assertTrue(list.get(1).getCategory().getId().equals("Switch"));
        // assertTrue(list.get(0).getCategory() == null);
        // }
    }

    @Test
    public void testGetResourceDefById() {
        System.out.println("TESTCASE_CAP006---测试读取某一个模型...");
        ResourceDef ciscoSwitchResource = capacityService
                .getResourceDefById("CiscoSwitch");
        System.out.println("模型名称:" + ciscoSwitchResource.getName());
        org.junit.Assert.assertNotNull(ciscoSwitchResource);

        assertNull(ciscoSwitchResource.getParentResourceDef());
        assertTrue(ciscoSwitchResource.getCategory().getId().equals("Switch"));
        assertTrue(ciscoSwitchResource.getChildResourceDefs().length == 1);
        assertTrue(ciscoSwitchResource.getChildResourceDefs()[0]
                .getParentResourceDef().hashCode() == ciscoSwitchResource
                .hashCode());
        Set<String> metricIds = new HashSet<String>();
        metricIds.add("CiscoSwitchNIC");
        assertTrue(metricIds.contains(ciscoSwitchResource
                .getChildResourceDefs()[0].getId()));
        assertTrue(ciscoSwitchResource.getChildResourceDefs()[0]
                .getChildResourceDefs() == null);
        Map<String, String> collectTypes = ciscoSwitchResource
                .getOptionCollPluginIds();
        // assertNotNull(collectTypes);
        if (collectTypes != null) {
            for (String collectType : collectTypes.keySet()) {
                System.out.println("option Ids:" + collectType);
            }
        }
        Set<String> reqIds = ciscoSwitchResource.getRequiredCollPluginIds();
        if (reqIds != null) {
            for (String reqId : reqIds) {
                System.out.println("reqId:" + reqId);
            }
        }
    }

    // @Test
    // public void testGetResourceDefSummaryInfoById() {
    // System.out.println("测试读取模型的summary...(TODO)");
    // ResourceDef def =
    // capacityService.getResourceDefSummaryInfoById("SWITCH_CISCO");
    // org.junit.Assert.assertNotNull(def);
    // }
    @Test
    public void testGetMySQLResourceMetricDef() {
        System.out.println("TESTCASE_CAP007---测试根据模型ID和指标ID读取模型的某一个指标...");
        ResourceDef resource = capacityService.getResourceDefById("MySQL");
        ResourceMetricDef[] metricDefs = resource.getMetricDefs();
        for (ResourceMetricDef metricDef : metricDefs) {
            MetricCollect mp = metricDef.getMetricPluginByType("WMI", null);
            if (null != mp && mp.getPlugin().getId().equals("JdbcPlugin")) {
                PluginConnectSetting[] settings = mp.getPluginConnectSettings();
                if (null != settings && settings.length > 0)
                    System.out.println("id:" + settings[0].getParameterId()
                            + ",value:" + settings[0].getParameterValue());
            }
        }

    }

    @Test
    public void testGetDeviceTypeByResourceId() {
        String resourceId = "Solarissnmp";
        DeviceType[] dftRcsId = capacityService
                .getDeviceTypeByResourceId(resourceId);
        System.out.println(dftRcsId.length);
    }

    @Test
    public void testGetDefaultResourceIdSameVendor() {
        String sysoid = "1.3.6.1.4.1.9.255";
        String dftRcsId = capacityService
                .getDefaultResourceIdSameVendor(sysoid);
        System.out.println(dftRcsId);
    }

    @Test
    public void testGetResourceMetricDef() {
        System.out.println("TESTCASE_CAP007---测试根据模型ID和指标ID读取模型的某一个指标...");
        ResourceMetricDef metric = capacityService.getResourceMetricDef(
                "LinuxProcess", "processName");
        org.junit.Assert.assertNotNull(metric);
        System.out.println("指标名称:" + metric.getId());
        assertTrue(metric.getResourceDef().getId().equals("LinuxProcess"));

        // ThresholdDef[] thresholds = metric.getThresholdDefs();
        // org.junit.Assert.assertNotNull(thresholds);
        // org.junit.Assert.assertTrue(thresholds.length == 2);
        // assertTrue(thresholds[0].getDefaultvalue().equals("90"));
        // assertTrue(thresholds[1].getDefaultvalue().equals("80"));

        // MetricCollect mcs = metric.getMetricPluginByType(null);
        // assertNotNull(mcs);
        //
        // metric = capacityService.getResourceMetricDef("CiscoSwitchNIC",
        // "ifIndex");
        // mcs = metric.getMetricPluginByType(null);
        // PluginDataConverter convert = mcs.getPluginDataConverter();
        //
        // assertNotNull(convert);
        // assertNotNull(convert.getClassFullName());
        // assertTrue(convert.getClassFullName().equals(
        // "com.mainsteam.stm.plugin.common.DefaultResultSetConverter"));
        // assertNull(convert.getParameterDefs());
        //
        // PluginResultMetaInfo metaInfo = mcs.getPluginResultMetaInfo();
        // assertNotNull(metaInfo);
        // assertNotNull(metaInfo.getColumns());
        // assertTrue(metaInfo.getColumns().length == 1);
        // assertTrue(metaInfo.getColumns()[0].equals("ifIndex"));
        //
        // PluginDef plugin = mcs.getPlugin();
        // assertNotNull(plugin);
        // assertTrue(plugin.getId().equals("SnmpPlugin"));
        //
        // PluginDataHandler[] dataHandlers = mcs.getPluginDataHandlers();
        // assertNotNull(dataHandlers);
        // assertTrue(dataHandlers.length == 1);
        // assertTrue(dataHandlers[0].getClassFullName().equals(
        // "com.mainsteam.stm.plugin.common.SelectResultSetProcessor"));
        // ParameterDef[] parameters = dataHandlers[0].getParameterDefs();
        // assertNotNull(parameters);
        // assertTrue(parameters.length == 1);
        // System.out.println("parameters[0].getValue():"
        // + parameters[0].getValue());
        // assertTrue(parameters[0].getValue().equals("SELECT ifIndex"));
        //
        // ResourceMetricDef metric2 = mcs.getResourceMetric();
        // assertNotNull(metric2);
        // assertTrue(metric2.getId().equals(metric.getId()));
        // assertTrue(metric2.hashCode() == metric.hashCode());
        //
        // PluginParameter pluginParameter = mcs.getPluginParameter();
        // assertNotNull(pluginParameter);
        // assertTrue(pluginParameter.getType() == ParameterTypeEnum.ArrayType);
        // ParameterDef[] parameters2 = pluginParameter.getParameters();
        // assertNotNull(parameters2);
        // assertTrue(parameters2.length == 2);
        // assertTrue(parameters2[0].getValue().equals("walk"));
        // assertTrue(parameters2[1].getValue().equals("1.3.6.1.2.1.2.2.1.1"));

        // mcs = metric.getMetricPlugins();
        // assertTrue(mcs.length == 3);
        // assertTrue(mcs[0].getResourceMetric().hashCode() ==
        // metric.hashCode());
        // assertTrue(mcs[0].getPlugin().getId().equals("SnmpPlugin"));
        // assertNull(mcs[1].getPlugin());
        // assertNull(mcs[2].getPlugin());
        //
        // PluginInitParameter[] pips =
        // mcs[0].getPlugin().getPluginInitParameters();
        // assertTrue(pips.length == 12);
        // assertTrue(pips[3].getId().equals("Version"));
        // assertTrue(pips[3].getSupportValues()[0].getName().equals("SNMP v1"));
        // {
        // PluginParameter pep = mcs[0].getPluginParameter();
        // assertTrue(pep instanceof PluginMapParameter);
        // assertFalse(pep instanceof PluginArrayParameter);
        // Map<String, Parameter> value = (Map) pep.getParameters();
        // assertTrue(value.get("Identify").getValue().equals(".1.3.6.1.2.1.2.5.0"));
        // assertTrue(value.get("General").getValue().equals(".1.3.6.1.2.1.1.3.0"));
        // }
        //
        // {
        // assertTrue(mcs[2].getPluginResultMetaInfo().getColumns().equals("index,type,in,out,speed"));
        // assertTrue(mcs[2].getPluginDataHandlers()[0].getClassFullName().equals("com.mainsteam.stm.handler.SelectFilter"));
        // assertTrue(mcs[2].getPluginDataHandlers()[0].getParameters()[0].getValue().equals("select id,in,out,speed where type in(${key1})"));
        //
        // PluginParameter pep = mcs[2].getPluginParameter();
        // assertTrue(pep instanceof PluginArrayParameter);
        // assertFalse(pep instanceof PluginMapParameter);
        // Parameter[] value = (Parameter[]) pep.getParameters();
        // assertTrue(value[1].getValue().equals("1.3.6.1.2.1.31.1.1.1.1"));
        //
        // }

    }

}
