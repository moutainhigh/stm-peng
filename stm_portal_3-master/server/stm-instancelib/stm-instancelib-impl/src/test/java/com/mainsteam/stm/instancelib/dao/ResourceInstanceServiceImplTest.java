package com.mainsteam.stm.instancelib.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
//import com.mainsteam.stm.instancelib.dao.interceptor.InstancelibInterceptorAddImpl;
//import com.mainsteam.stm.instancelib.dao.interceptor.InstancelibInterceptorDeleteImpl;
//import com.mainsteam.stm.instancelib.dao.interceptor.InstancelibInterceptorUpdateStateImpl;
//import com.mainsteam.stm.instancelib.dao.interceptor.InstancelibListenerAddImpl;
//import com.mainsteam.stm.instancelib.dao.interceptor.InstancelibListenerDeleteImpl;
//import com.mainsteam.stm.instancelib.dao.interceptor.InstancelibListenerUpdateStateImpl;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
//import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
//import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ExtendProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	"classpath*:META-INF/services/test-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class ResourceInstanceServiceImplTest extends DataSourceBasedDBTestCase {

	@Resource(name="defaultDataSource")
	private DataSource source;

	@Resource
	private InstancelibEventManager instancelibEventManager;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ResourceInstanceExtendService resourceInstanceExtendService;
	
	//@Resource
	//private InstancelibInterceptor addInterceptor;
	
	//@Resource
	//private InstancelibListener addListener;
	
	//@Resource
	//private InstancelibInterceptor deleteInterceptor;
	
	//@Resource
	//private InstancelibListener deleteListener;
	
	//@Resource
	//private InstancelibInterceptor updateNameInterceptor;
	
	//@Resource
	//private InstancelibListener updateNameListener;
	
	
	//缓存
	IMemcache<ResourceInstance> cache = MemCacheFactory
			.getRemoteMemCache(ResourceInstance.class);
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
		System.setProperty("testCase","test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		//清空缓存 instance = 2 
		cache.delete("2");
		cache.delete("3");
		cache.delete("4");
		cache.delete("121");
		super.tearDown();
	}

	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/instancelib_resource_main_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}

	@Test
	public void testAddResourceInstance() {
		ResourceInstance def = new ResourceInstance();
		List<ModuleProp> moduleProps = new ArrayList<ModuleProp>();
		String key = "kkkk";
		String modulePropKey = MetricIdConsts.METRIC_MACADDRESS;
		CustomProp customProp = new CustomProp();
		String[] customPropValues = {"1custom_a1a1","1custom_b1"};
		customProp.setKey(key);
		customProp.setValues(customPropValues);

		DiscoverProp discoverProp = new DiscoverProp();
		String[] discoverPropValues = {"1Discover_a1a1","1Discover_b1"};
		discoverProp.setKey(key);
		discoverProp.setValues(discoverPropValues);

		ExtendProp extendProp = new ExtendProp();
		String[] extendPropValues = {"1Extend_a1a1","1Extend_b1"};
		extendProp.setKey(key);
		extendProp.setValues(extendPropValues);

		ModuleProp moduleProp = new ModuleProp();
		String[] modulePropValues = {"00:0b:ca:fe:00:01","2c:21:72:9d:4a:02","2c:21:72:9d:4a:01","2c:21:72:9d:4a:03"};
		moduleProp.setKey(modulePropKey);
		moduleProp.setValues(modulePropValues);

		moduleProps.add(moduleProp);

		List<ExtendProp> extendProps = new ArrayList<ExtendProp>();
		extendProps.add(extendProp);

		List<DiscoverProp> discoverProps = new ArrayList<DiscoverProp>();
		discoverProps.add(discoverProp);

		List<CustomProp> customProps = new ArrayList<CustomProp>();
		customProps.add(customProp);
		def.setCategoryId("Switch");
		def.setShowIP("192.168.1.211");
		def.setDiscoverNode("111111");
		def.setDiscoverWay(DiscoverWayEnum.SSH);
		def.setName("name1111");
		def.setShowName("showName");
		def.setLifeState(InstanceLifeStateEnum.INITIALIZE);
		def.setModuleProps(moduleProps);
		def.setCustomProps(customProps);
		def.setDiscoverProps(discoverProps);
		def.setExtendProps(extendProps);
		//def.(setParentId121);
		def.setResourceId("resourceId11");
		def.setChildType("cpu11");
		def.setDomainId(22222222);
		ResourceInstance child = new ResourceInstance();
	
		List<ModuleProp> childModuleProps = new ArrayList<ModuleProp>();
		ModuleProp childModuleProp = new ModuleProp();
		String[] childModulePropValues = {"child_Module_a1a1","child_Module_b1"};
		childModuleProp.setKey("childKey");
		childModuleProp.setValues(childModulePropValues);
		
		childModuleProps.add(childModuleProp);
		
	
		child.setCategoryId("childCategoryId1");
		child.setShowIP("2.2.2.2");
		child.setDiscoverNode("1111");
		child.setDiscoverWay(DiscoverWayEnum.SSH);
		child.setName("childName1111");
		child.setLifeState(InstanceLifeStateEnum.INITIALIZE);
		child.setModuleProps(moduleProps);
		child.setParentId(121);
		child.setResourceId("childResourceId11");
		child.setChildType("childCpu11");
		
		List<ResourceInstance> children = new ArrayList<>();
		children.add(child);
		
		def.setChildren(children);
		ResourceInstanceResult result = null;
		long instanceId = 0;
		try {
			result = resourceInstanceService.addResourceInstance(def);
		} catch (InstancelibException e) {
			e.printStackTrace();
			fail();
		} 
		if(result != null){
			instanceId = result.getResourceInstanceId();
		}
		//判断拦截是否执行
		//assertEquals(true, addInterceptor.isExecute());
		
		//判断监听是否执行
		//assertEquals(true, addListener.isExecute());
		
		ResourceInstance newResourceInstance = null;
		try {
			newResourceInstance = resourceInstanceService
					.getResourceInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(newResourceInstance);

		assertEquals(newResourceInstance.getId(), def.getId());

		assertEquals(newResourceInstance.getCategoryId(), def.getCategoryId());

		assertEquals(newResourceInstance.getShowIP(), def.getShowIP());

		assertEquals(newResourceInstance.getDiscoverNode(),
				def.getDiscoverNode());

		assertEquals(newResourceInstance.getDiscoverWay(), def.getDiscoverWay());

		assertEquals(newResourceInstance.getName(), def.getName());

		assertEquals(newResourceInstance.getLifeState(), def.getLifeState());

		assertEquals(newResourceInstance.getParentId(), def.getParentId());

		assertEquals(newResourceInstance.getResourceId(), def.getResourceId());

		assertEquals(newResourceInstance.getChildType(), def.getChildType());
		
		assertEquals(newResourceInstance.getDomainId(), def.getDomainId());
		IDataSet actualDataset = null;
		try {
			actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "CATEGORYID"),
							def.getCategoryId());

					assertEquals(actualTable.getValue(i, "SHOWIP"),
							def.getShowIP());

					assertEquals(actualTable.getValue(i, "DiscoverNode"),
							def.getDiscoverNode());

					assertEquals(actualTable.getValue(i, "DiscoverWay")
							.toString(), def.getDiscoverWay().toString());

					assertEquals(actualTable.getValue(i, "InstanceName"),
							def.getName());

					assertEquals(actualTable.getValue(i, "LifeState")
							.toString(), def.getLifeState().toString());

//					assertEquals(Long.parseLong(actualTable.getValue(i,
//							"ParentId").toString()), def.getParentId());

					assertEquals(actualTable.getValue(i, "ResourceId"),
							def.getResourceId());

					assertEquals(actualTable.getValue(i, "InstanceType"),
							def.getChildType());

					find = true;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

		int customCount = 0;
		int discoverCount = 0;
		int moudleCount = 0;
		int extendCount = 0;
		try {
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			// boolean find = false;
			// List<String> dbValues = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())) {
					if (PropTypeEnum.CUSTOM.toString().equals(
							actualTable.getValue(i, "propType"))) {
						customCount++;
					} else if (PropTypeEnum.DISCOVER.toString().equals(
							actualTable.getValue(i, "propType"))) {
						discoverCount++;
					} else if (PropTypeEnum.EXTEND.toString().equals(
							actualTable.getValue(i, "propType"))) {
						extendCount++;
					}
				}else if (modulePropKey.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())) {
				    if (PropTypeEnum.MODULE.toString().equals(
							actualTable.getValue(i, "propType"))) {
						moudleCount++;
					} 
				}
			}
			assertEquals(customPropValues.length, customCount);
			assertEquals(discoverPropValues.length, discoverCount);
			assertEquals(modulePropValues.length, moudleCount);
			assertEquals(extendPropValues.length, extendCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
		/*
		 * 验证实例添加重复
		 */
		boolean isRepeat = false;
		try {
			result = resourceInstanceService.addResourceInstance(def);
		} catch (InstancelibException e) {
			fail();
			e.printStackTrace();
		} 
		if(result != null){
			if(result.isRepeat()){
				isRepeat = true;
			}
		}else{
			fail();
		}
		assertTrue(isRepeat);
	}

	@Test
	public void testAddLink(){ 
		List<ResourceInstance> resourceInstances = new ArrayList<ResourceInstance>();
		ResourceInstance resourceInstance = new ResourceInstance();
//		resourceInstance.setCategoryId("Switch");
//		resourceInstance.setShowIP("192.168.1.211");
//		resourceInstance.setDiscoverNode("111111");
//		resourceInstance.setDiscoverWay(DiscoverWayEnum.SSH);
		resourceInstance.setName("name13423111");
		resourceInstance.setShowName("showName");
	//	resourceInstance.setLifeState(InstanceLifeStateEnum.INITIALIZE);
		resourceInstance.setResourceId("resourceId11");
//		resourceInstance.setChildType("cpu11");
//		resourceInstance.setDomainId(1111111);
//		
//		ResourceInstance resourceInstance1 = new ResourceInstance();
//		resourceInstance1.setCategoryId("Switch1");
//		resourceInstance1.setShowIP("192.168.1.211");
//		resourceInstance1.setDiscoverNode("222222");
//		resourceInstance1.setDiscoverWay(DiscoverWayEnum.SSH);
//		resourceInstance1.setName("name2222");
//		resourceInstance1.setShowName("showName22222");
//		resourceInstance1.setLifeState(InstanceLifeStateEnum.INITIALIZE);
//		resourceInstance1.setResourceId("resourceId222");
//		resourceInstance1.setChildType("cpu2");
//		resourceInstance1.setDomainId(2222222);
//		
//		resourceInstances.add(resourceInstance1);
		resourceInstances.add(resourceInstance);
		
		ResourceInstance resourceInstanceOne = new ResourceInstance();
		resourceInstanceOne.setName("name13423111");
		resourceInstanceOne.setShowName("showName");
	//	resourceInstance.setLifeState(InstanceLifeStateEnum.INITIALIZE);
		resourceInstanceOne.setResourceId("resourceId11");
		
		resourceInstances.add(resourceInstanceOne);
		
		
		try {
			resourceInstanceService.addResourceInstanceForLink(resourceInstances,true);
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (ResourceInstance r : resourceInstances) {
			System.out.println("instanceId:" + r.getId());
		}
	}
	@Test
	public void testRefreshResourceInstance() {
		long instanceId = 2;
		String key = "key";
		CustomProp customProp = new CustomProp();
		String[] customPropValues = {"custom_a1a1","custom_b1"};
		customProp.setInstanceId(instanceId);
		customProp.setKey(key);
		customProp.setValues(customPropValues);

		DiscoverProp discoverProp = new DiscoverProp();
		String[] discoverPropValues = {"Discover_a1a1","Discover_b1"};
		discoverProp.setInstanceId(instanceId);
		discoverProp.setKey(key);
		discoverProp.setValues(discoverPropValues);

		ExtendProp extendProp = new ExtendProp();
		String[] extendPropValues = {"Extend_a1a1","Extend_b1"};
		extendProp.setInstanceId(instanceId);
		extendProp.setKey(key);
		extendProp.setValues(extendPropValues);

		ModuleProp moduleProp = new ModuleProp();
		String[] modulePropValues = {"Module_a1a1","Module_b1"};
		moduleProp.setInstanceId(instanceId);
		moduleProp.setKey(key);
		moduleProp.setValues(modulePropValues);

		ResourceInstance def = new ResourceInstance();
		List<ModuleProp> moduleProps = new ArrayList<ModuleProp>();
		moduleProps.add(moduleProp);

		List<ExtendProp> extendProps = new ArrayList<ExtendProp>();
		extendProps.add(extendProp);

		List<DiscoverProp> discoverProps = new ArrayList<DiscoverProp>();
		discoverProps.add(discoverProp);

		List<CustomProp> customProps = new ArrayList<CustomProp>();
		customProps.add(customProp);

		def.setId(instanceId);
		def.setCategoryId("CategoryId2");
		def.setCustomProps(customProps);
		def.setShowIP("192.168.1.1");
		def.setDiscoverNode("111111");
		def.setDiscoverProps(discoverProps);
		def.setDiscoverWay(DiscoverWayEnum.SSH);
		def.setExtendProps(extendProps);
		def.setName("name2");
		def.setLifeState(InstanceLifeStateEnum.DELETED);
		def.setModuleProps(moduleProps);
		def.setParentId(121);
		def.setResourceId("resourceId2");
		def.setChildType("主机2");
		try {
			resourceInstanceService.refreshResourceInstance(def);
		} catch (InstancelibException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		ResourceInstance updateAfter = null;
		try {
			updateAfter = resourceInstanceService
					.getResourceInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(updateAfter);

		assertEquals(updateAfter.getId(), def.getId());

		assertEquals(updateAfter.getCategoryId(), def.getCategoryId());

		assertEquals(updateAfter.getShowIP(), def.getShowIP());

		assertEquals(updateAfter.getDiscoverNode(), def.getDiscoverNode());

		assertEquals(updateAfter.getDiscoverWay(), def.getDiscoverWay());

		assertEquals(updateAfter.getName(), def.getName());

		assertEquals(updateAfter.getLifeState(), def.getLifeState());

		assertEquals(updateAfter.getParentId(), def.getParentId());

		assertEquals(updateAfter.getResourceId(), def.getResourceId());

		assertEquals(updateAfter.getChildType(), def.getChildType());
		IDataSet actualDataset = null;
		try {
			actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "CATEGORYID"),
							def.getCategoryId());

					assertEquals(actualTable.getValue(i, "SHOWIP"),
							def.getShowIP());

					assertEquals(actualTable.getValue(i, "DiscoverNode"),
							def.getDiscoverNode());

					assertEquals(actualTable.getValue(i, "DiscoverWay")
							.toString(), def.getDiscoverWay().toString());

					assertEquals(actualTable.getValue(i, "InstanceName"),
							def.getName());

					assertEquals(actualTable.getValue(i, "LifeState")
							.toString(), def.getLifeState().toString());

					// assertEquals(Long.parseLong(actualTable.getValue(i,
					// "ParentId").toString()), def.getParentId());

					assertEquals(actualTable.getValue(i, "ResourceId"),
							def.getResourceId());

					assertEquals(actualTable.getValue(i, "InstanceType"),
							def.getChildType());

					find = true;
				}
			}

			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		int customCount = 0;
		int discoverCount = 0;
		int moudleCount = 0;
		int extendCount = 0;
		try {
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			// boolean find = false;
			// List<String> dbValues = new ArrayList<String>();
			for (int i = 0; i < rowCount; i++) {
				if (key.equals(actualTable.getValue(i, "PROPKEY"))
						&& instanceId == Long.parseLong(actualTable.getValue(i,
								"INSTANCEID").toString())) {
					if (PropTypeEnum.CUSTOM.toString().equals(
							actualTable.getValue(i, "propType"))) {
						customCount++;
					} else if (PropTypeEnum.DISCOVER.toString().equals(
							actualTable.getValue(i, "propType"))) {
						discoverCount++;
					} else if (PropTypeEnum.MODULE.toString().equals(
							actualTable.getValue(i, "propType"))) {
						moudleCount++;
					} else if (PropTypeEnum.EXTEND.toString().equals(
							actualTable.getValue(i, "propType"))) {
						extendCount++;
					}
				}
			}
			assertEquals(2, customCount);
			assertEquals(2, discoverCount);
			assertEquals(2, moudleCount);
			assertEquals(2, extendCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRemoveResourceInstance() {
		long instanceId = 4;
		try {
			resourceInstanceService.removeResourceInstance(instanceId);
			ResourceInstance deleteAfter = resourceInstanceService
					.getResourceInstance(instanceId);
			assertNotNull(deleteAfter);
			assertEquals(deleteAfter.getLifeState().toString(),InstanceLifeStateEnum.DELETED.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		//测试拦截是否执行
		//assertEquals(true, deleteInterceptor.isExecute());
		//assertEquals(true, deleteListener.isExecute());
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
					if(InstanceLifeStateEnum.DELETED.toString().equals(actualTable.getValue(i, "LifeState"))){
						find = true;
					}
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetResourceInstance() throws InstancelibException, Exception {
		long instanceId = 3;
		ResourceInstance resoureceInstance = null;
		try {
			resoureceInstance = resourceInstanceService
					.getResourceInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		IDataSet actualDataset = null;
		try {
			actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();
			boolean find = false;
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "CATEGORYID"),
							resoureceInstance.getCategoryId());

					assertEquals(actualTable.getValue(i, "SHOWIP"),
							resoureceInstance.getShowIP());

					assertEquals(actualTable.getValue(i, "DiscoverNode"),
							resoureceInstance.getDiscoverNode());

					assertEquals(actualTable.getValue(i, "DiscoverWay")
							.toString(), resoureceInstance.getDiscoverWay()
							.toString());

					assertEquals(actualTable.getValue(i, "InstanceName"),
							resoureceInstance.getName());

					assertEquals(actualTable.getValue(i, "LifeState")
							.toString(), resoureceInstance.getLifeState()
							.toString());

					assertEquals(actualTable.getValue(i, "ResourceId"),
							resoureceInstance.getResourceId());

					assertEquals(actualTable.getValue(i, "InstanceType"),
							resoureceInstance.getChildType());

					find = true;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		int foundCount = 0;
		int actual = 4;
		try {
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_PROP_VALUE");
			int rowCount = actualTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {
				
					if (PropTypeEnum.CUSTOM.toString().equals(
							actualTable.getValue(i, "propType"))) {
						foundCount ++;
					} else if (PropTypeEnum.DISCOVER.toString().equals(
							actualTable.getValue(i, "propType"))) {
						foundCount ++;
					} else if (PropTypeEnum.MODULE.toString().equals(
							actualTable.getValue(i, "propType"))) {
						foundCount ++;
					} else if (PropTypeEnum.EXTEND.toString().equals(
							actualTable.getValue(i, "propType"))) {
						foundCount ++;
					}
				}
			}
			assertEquals(foundCount, actual);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}	
		resoureceInstance.getModulePropBykey("IPAddress");
		//通过查询提供出去的类，防止用户再次用这个类做一些更新，插入操作。插入，更新，必须new一个实例对象
		thrown.expect(InstancelibRuntimeException.class);
		resoureceInstance.setCategoryId("categoryId");
	
	}

	@Test
	public void testGetParentInstanceByResourceId() {
		String resourceId = "window";
		List<ResourceInstance> resoureceInstances = null;
		try {
			resoureceInstances = resourceInstanceService.getResourceInstanceByResourceId(resourceId);
			assertNotNull(resoureceInstances);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int findCount = 0;
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				if (actualTable.getValue(i, "RESOURCEID") != null) {
					if (resourceId
							.equals(actualTable.getValue(i, "RESOURCEID"))) {
						for (ResourceInstance resoureceInstance : resoureceInstances) {
							if (resoureceInstance.getId() == Long
									.parseLong(actualTable.getValue(i,
											"INSTANCEID").toString())) {
								assertEquals(
										actualTable.getValue(i, "CATEGORYID"),
										resoureceInstance.getCategoryId());

								assertEquals(
										actualTable.getValue(i, "SHOWIP"),
										resoureceInstance.getShowIP());

								assertEquals(
										actualTable.getValue(i, "DiscoverNode"),
										resoureceInstance.getDiscoverNode());

								assertEquals(
										actualTable.getValue(i, "DiscoverWay")
												.toString(), resoureceInstance
												.getDiscoverWay().toString());

								assertEquals(
										actualTable.getValue(i, "InstanceName"),
										resoureceInstance.getName());
								
								assertEquals(
										actualTable.getValue(i, "LifeState")
												.toString(), resoureceInstance
												.getLifeState().toString());

								if (actualTable.getValue(i, "ParentId") != null) {
									assertEquals(
											Long.parseLong(actualTable
													.getValue(i, "ParentId")
													.toString()),
											resoureceInstance.getParentId());
								}

								assertEquals(
										actualTable.getValue(i, "ResourceId"),
										resoureceInstance.getResourceId());

								assertEquals(
										actualTable.getValue(i, "InstanceType"),
										resoureceInstance.getChildType());
							}
						}
						findCount++;
					}
				}
			}
			assertEquals(findCount, resoureceInstances.size());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

	}

	@Test
	public void testGetParentIntanceByCategoryId() {
		String categoryId = "主机";
		List<ResourceInstance> resoureceInstances = null;
		try {
			resoureceInstances = resourceInstanceService
					.getParentInstanceByCategoryId(categoryId);
			assertNotNull(resoureceInstances);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int findCount = 0;
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				if (actualTable.getValue(i, "CATEGORYID") != null) {
					if (categoryId
							.equals(actualTable.getValue(i, "CATEGORYID"))) {
						for (ResourceInstance resoureceInstance : resoureceInstances) {
							if (resoureceInstance.getId() == Long
									.parseLong(actualTable.getValue(i,
											"INSTANCEID").toString())) {
								assertEquals(
										actualTable.getValue(i, "CATEGORYID"),
										resoureceInstance.getCategoryId());

								assertEquals(
										actualTable.getValue(i, "SHOWIP"),
										resoureceInstance.getShowIP());

								assertEquals(
										actualTable.getValue(i, "DiscoverNode"),
										resoureceInstance.getDiscoverNode());

								assertEquals(
										actualTable.getValue(i, "DiscoverWay")
												.toString(), resoureceInstance
												.getDiscoverWay().toString());

								assertEquals(
										actualTable.getValue(i, "InstanceName"),
										resoureceInstance.getName());

								assertEquals(
										actualTable.getValue(i, "LifeState")
												.toString(), resoureceInstance
												.getLifeState().toString());

								if (actualTable.getValue(i, "ParentId") != null) {
									assertEquals(
											Long.parseLong(actualTable
													.getValue(i, "ParentId")
													.toString()),
											resoureceInstance.getParentId());
								}

								assertEquals(
										actualTable.getValue(i, "ResourceId"),
										resoureceInstance.getResourceId());

								assertEquals(
										actualTable.getValue(i, "InstanceType"),
										resoureceInstance.getChildType());
							}
						}
						findCount++;
					}
				}
			}
			assertEquals(findCount, resoureceInstances.size());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}

	}
	

	@Test
	public void testGetChildInstanceByParentId() {
		long parentId = 121;
		List<ResourceInstance> resoureceInstances = null;
		try {
			resoureceInstances = resourceInstanceService
					.getChildInstanceByParentId(parentId);
			assertNotNull(resoureceInstances);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int findCount = 0;
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				Object dbParentId = actualTable.getValue(i, "PARENTID");
				if (dbParentId != null) {
					if (parentId == Long.parseLong(dbParentId.toString())) {
						for (ResourceInstance resoureceInstance : resoureceInstances) {

							if (resoureceInstance.getId() == Long
									.parseLong(actualTable.getValue(i,
											"INSTANCEID").toString())) {
								assertEquals(
										actualTable.getValue(i, "CATEGORYID"),
										resoureceInstance.getCategoryId());

								assertEquals(
										actualTable.getValue(i, "SHOWIP"),
										resoureceInstance.getShowIP());

								assertEquals(
										actualTable.getValue(i, "DiscoverNode"),
										resoureceInstance.getDiscoverNode());

								assertEquals(
										actualTable.getValue(i, "DiscoverWay")
												.toString(), resoureceInstance
												.getDiscoverWay().toString());

								assertEquals(
										actualTable.getValue(i, "InstanceName"),
										resoureceInstance.getName());

								assertEquals(
										actualTable.getValue(i, "LifeState")
												.toString(), resoureceInstance
												.getLifeState().toString());

								assertEquals(
										Long.parseLong(actualTable.getValue(i,
												"ParentId").toString()),
										resoureceInstance.getParentId());

								assertEquals(
										actualTable.getValue(i, "ResourceId"),
										resoureceInstance.getResourceId());

								assertEquals(
										actualTable.getValue(i, "InstanceType"),
										resoureceInstance.getChildType());
							}
						}
						findCount++;
					}
				}
			}

			assertEquals(findCount, resoureceInstances.size());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetParentResourceInstanceByLifeState(){
		List<ResourceInstance> resoureceInstances = null;
		try {
			resoureceInstances = resourceInstanceService.getParentResourceInstanceByLifeState(InstanceLifeStateEnum.MONITORED);
			assertNotNull(resoureceInstances);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetAllParentInstance() {
		List<ResourceInstance> resoureceInstances = null;
		try {
			resoureceInstances = resourceInstanceService.getAllParentInstance();
			assertNotNull(resoureceInstances);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int findCount = 0;
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();

			for (int i = 0; i < rowCount; i++) {
				if (actualTable.getValue(i, "ParentId") == null) {
					for (ResourceInstance resoureceInstance : resoureceInstances) {
						if (resoureceInstance.getId() == Long
								.parseLong(actualTable
										.getValue(i, "INSTANCEID").toString())) {
							assertEquals(actualTable.getValue(i, "CATEGORYID"),
									resoureceInstance.getCategoryId());

							assertEquals(actualTable.getValue(i, "SHOWIP"),
									resoureceInstance.getShowIP());

							assertEquals(
									actualTable.getValue(i, "DiscoverNode"),
									resoureceInstance.getDiscoverNode());

							assertEquals(actualTable.getValue(i, "DiscoverWay")
									.toString(), resoureceInstance
									.getDiscoverWay().toString());

							assertEquals(
									actualTable.getValue(i, "InstanceName"),
									resoureceInstance.getName());

							assertEquals(actualTable.getValue(i, "LifeState")
									.toString(), resoureceInstance
									.getLifeState().toString());

							assertEquals(actualTable.getValue(i, "ResourceId"),
									resoureceInstance.getResourceId());

							assertEquals(
									actualTable.getValue(i, "InstanceType"),
									resoureceInstance.getChildType());
						}
					}
					findCount++;
				}

			}
			assertEquals(findCount, resoureceInstances.size());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testUpdateResourceInstanceState(){
		long instanceId_One = 3;
		long instanceId_two = 2;
		
		Map<Long, InstanceLifeStateEnum> states = new HashMap<Long, InstanceLifeStateEnum>();
		states.put(instanceId_One, InstanceLifeStateEnum.INITIALIZE);
		states.put(instanceId_two, InstanceLifeStateEnum.DELETED);
		
		try {
			resourceInstanceService.updateResourceInstanceState(states);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	//	assertEquals(true, updateNameInterceptor.isExecute());
	//	assertEquals(true, updateNameListener.isExecute());
		
		ResourceInstance updateAfter1 = null;
		try {
			updateAfter1 = resourceInstanceService
					.getResourceInstance(instanceId_One);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(updateAfter1);
		assertEquals(updateAfter1.getLifeState(), states.get(instanceId_One));
		
		ResourceInstance updateAfter2 = null;
		try {
			updateAfter2 = resourceInstanceService
					.getResourceInstance(instanceId_One);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(updateAfter2);
		assertEquals(updateAfter2.getLifeState(), states.get(instanceId_One));

		int findCount = 0;
		
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();
		
			for (int i = 0; i < rowCount; i++) {
				if (instanceId_One == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "LifeState")
							.toString(), states.get(instanceId_One).toString());
					findCount ++;
				}
				if (instanceId_two == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "LifeState")
							.toString(), states.get(instanceId_two).toString());
					findCount ++;
				}
			}
			assertEquals(states.size(), findCount);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
	
	
	@Test
	public void testUpdateResourceInstanceName(){
		long instanceId = 2;
		String name = "name12121212"; 
	
		try {
			resourceInstanceService.updateResourceInstanceName(instanceId, name);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//assertEquals(true, updateNameInterceptor.isExecute());
		//assertEquals(true, updateNameListener.isExecute());
		
		ResourceInstance updateAfter1 = null;
		try {
			updateAfter1 = resourceInstanceService
					.getResourceInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(updateAfter1);
		assertEquals(name, updateAfter1.getShowName());
		
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable actualTable = actualDataset
					.getTable("STM_INSTANCELIB_RESOURCE_MAIN");
			int rowCount = actualTable.getRowCount();
		
			for (int i = 0; i < rowCount; i++) {
				if (instanceId == Long.parseLong(actualTable.getValue(i,
						"INSTANCEID").toString())) {

					assertEquals(actualTable.getValue(i, "InstanceShowName")
							.toString(), name);
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
}
