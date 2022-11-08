/**
 * 
 */
package com.mainsteam.stm.instancelib.cache;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.util.InitLoadPropKeyUtil;
import com.mainsteam.stm.instancelib.util.PropCache;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:META-INF/services/public-*-beans.xml" ,
	//"classpath*:META-INF/services/test-*-beans.xml" ,
	"classpath*:META-INF/services/server-processer-*-beans.xml"})
public class CollectorResourceInstanceCacheTest {

	//private CollectorResourceInstanceCache cache;
	
	@Resource(name="defaultDataSource")
	private DataSource source;

	//@Resource
	//private ISequence instanceSeq;
	@Resource
	private CapacityService capacityService;
	//private CollectorPropCache propCache;
	//@Resource
	//private PropCache propCache;
	
	//@Resource
	//private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private InitLoadPropKeyUtil initLoadPropKeyUtil;

	private IMemcache<ResourceInstance> mebCache = MemCacheFactory.getRemoteMemCache(ResourceInstance.class);
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache#add(long, com.mainsteam.stm.instancelib.obj.ResourceInstance)}
	 * .
	 */
	@Test
	public void testAdd() {
		//System.out.println(instanceSeq.next());
		long instanceId =-8989;
		ResourceInstance instance = new ResourceInstance();
		instance.setId(instanceId);
		//mebCache.set(instanceId, instance);
		//ResourceInstance selectInstance = cache.get(instanceId);
		//assertEquals(instance, selectInstance);

		//mebCache.remove(instanceId);
		//selectInstance = mebCache.get(instanceId);
		//assertNull(selectInstance);
		//System.out.println(mebCache.getAllResourceInstances());
		
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		//instances.add(instance);
		
		mebCache.setCollection("allll", instances);
		//instances.add(instance);
		
		List<ResourceInstance> ins = (List<ResourceInstance>)mebCache.getCollection("allll");
		
		System.out.println(ins == instances);
//		InstanceProp prop = propCache.get(109500, "macAddress", PropTypeEnum.MODULE);
//		System.out.println(prop.getValues());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache#updateModuleProp(com.mainsteam.stm.instancelib.obj.ModuleProp)}
	 * .
	 */
	@Test
	public void testUpdateModuleProp() {
		System.out.println(initLoadPropKeyUtil.getModuleKeys());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache#updateDiscoveryProp(com.mainsteam.stm.instancelib.obj.DiscoverProp)}
	 * .
	 */
	@Test
	public void testUpdateDiscoveryProp() {
//		long instanceId = 100;
//		ResourceInstance instance = new ResourceInstance();
//		instance.setId(instanceId);
//		List<DiscoverProp> discoverProps = new ArrayList<>(1);
//		DiscoverProp p = new DiscoverProp();
//		p.setInstanceId(instanceId);
//		p.setKey("t");
//		List<String> values = new ArrayList<>();
//		values.add("1");
//		p.setValues(values);
//		discoverProps.add(p);
//		instance.setDiscoverProps(discoverProps);
//		cache.add(instanceId, instance);
//
//		DiscoverProp selectProp = cache.getDiscoverProp(instanceId, p.getKey());
//		assertEquals(p, selectProp);
//
//		DiscoverProp p1 = new DiscoverProp();
//		p1.setInstanceId(instanceId);
//		p1.setKey("t");
//		values = new ArrayList<>();
//		values.add("2");
//		p1.setValues(values);
//		cache.updateDiscoveryProp(p1);
//		selectProp = cache.getDiscoverProp(instanceId, p.getKey());
//		System.out.println("selectProp.getValues()="+selectProp.getValues());
//		assertEquals(p1.getValues(), selectProp.getValues());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache#getModuleProp(long, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetModuleProp() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache#getDiscoverProp(long, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetDiscoverProp() {
		fail("Not yet implemented");
	}


}
