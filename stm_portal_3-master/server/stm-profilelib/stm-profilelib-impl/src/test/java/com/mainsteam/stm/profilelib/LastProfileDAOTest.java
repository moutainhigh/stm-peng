package com.mainsteam.stm.profilelib;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.dao.LastProfileDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class LastProfileDAOTest extends TestCase{

	@Resource
	private LastProfileDAO lastProfileDAO;
	
	@Resource(name = "defaultDataSource")
	private DataSource source;
	
	@Resource
	private CapacityService capacityService;
	@Resource
	private  ISequence ocProfilelibThresholdSequence;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
		System.setProperty("testCase","test");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	@Test
	public void testSeq(){
		final List<Long> ids = new ArrayList<>(100000);
		final ISequence iq =  ocProfilelibThresholdSequence;
		ExecutorService threadPoolTaskExecutor = Executors.newFixedThreadPool(20,
				new ThreadFactory() {
					private int counter = 0;
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,"dispatcher-"
								+ counter++);
					}
				});
		for(int i = 0; i < 10000; i++){
			threadPoolTaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					long id = iq.next();
					ids.add(id);
				}
			});
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<Long> setIds = new HashSet<Long>(20);
		for(long id : ids){
			if(setIds.contains(id)){
				System.out.println("repeat id:" +id);
			}else{
				setIds.add(id);
//				System.out.println("add id:" +id);
			}
		}
	}
	@Test
	public void testInsert(){
//		LastProfilePO po = new LastProfilePO();
//		po.setId(1);
//		po.setInstanceId(111);
//		po.setProfileId(111);
//		po.setProfileType("Default");
//		po.setUpdateTime(System.currentTimeMillis());
//		
//		LastProfilePO po1 = new LastProfilePO();
//		po1.setId(2);
//		po1.setInstanceId(222);
//		po1.setProfileId(222);
//		po1.setProfileType("Default");
//		po1.setUpdateTime(System.currentTimeMillis());
//		
//		List<LastProfilePO> lastProfilePOs = new ArrayList<LastProfilePO>();
//		lastProfilePOs.add(po);
//		lastProfilePOs.add(po1);
//		try {
//			List<LastProfilePO> profilePOs =	lastProfileDAO.getLastProfileByInstanceId(22);
//			System.out.println();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
