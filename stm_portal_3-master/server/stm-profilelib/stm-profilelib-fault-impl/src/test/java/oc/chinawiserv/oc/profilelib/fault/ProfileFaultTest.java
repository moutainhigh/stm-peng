package oc.mainsteam.stm.profilelib.fault;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultDao;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultInstanceDao;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultMetricDao;
import com.mainsteam.stm.profile.fault.execute.ProfileFaultScriptExecuteServiceMBean;
import com.mainsteam.stm.profilelib.fault.ProfileFaultInstanceService;
import com.mainsteam.stm.profilelib.fault.ProfileFaultService;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultRelation;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-collector-profilelib-fault-beans.xml"
		})
public class ProfileFaultTest extends TestCase{
 
	@Resource(name="profilefaultService")
	private ProfileFaultService profileFaultService;
	
	@Resource(name="profileFaultDao")
	private ProfileFaultDao profileFaultDao;
	
	@Resource(name="profileInstanceDao")
	private ProfileFaultInstanceDao profileInstanceDao;
	
	@Resource(name="profileMetridDao")
	private ProfileFaultMetricDao profileMetridDao;
	
	@Resource(name="profilefaultInstanceService")
	private ProfileFaultInstanceService profilefaultInstanceService;
	
	@Resource(name="resourceInstanceService")
	private ResourceInstanceService instanceService;
	
	
	@Resource(name="profileFaultScriptExecuteService")
	private ProfileFaultScriptExecuteServiceMBean faultScriptExecuteServiceMBean;
	
	@Resource(name="fileClient")
	private IFileClientApi fileClient;
	
	public void testPageSelectProfile(){
		Page<Profilefault, Profilefault> page = new Page<Profilefault, Profilefault>();
		profileFaultService.queryProfilefaults(page);
		System.out.println(page.getDatas().size());
	}
	
	
	public void testGetProfile(){
		ProfileFaultRelation profilefault = profileFaultService.getProfileFaultRelationById(1);
		System.out.println(profilefault.getProfileName());
	}
	
	
	public void testBatchInsertProfileInstance(){
		List<ProfileFaultInstance> profileFaultInstances = new ArrayList<ProfileFaultInstance>();
		for(int i=0;i<5;i++){
			ProfileFaultInstance instance = new ProfileFaultInstance();
			instance.setInstanceId(""+i);
			instance.setProfileId(1);
			profileFaultInstances.add(instance);
		}
		int result = profileInstanceDao.batchInsertProfileFaultInstance(profileFaultInstances);
		System.out.println(result);
	}
	
	
	public void testQueryProfileInstance(){
		List<ProfileFaultInstance> test = profileInstanceDao.selectProfileFaultInstances(1);
		for (ProfileFaultInstance profileFaultInstance : test) {
			System.out.println(profileFaultInstance.getProfileId()+"-----"+profileFaultInstance.getInstanceId());
		}
	}
	
	
	
	
	public void testBatchInsertProfileMetric(){
		List<ProfileFaultMetric> profileFaultMetrics = new ArrayList<ProfileFaultMetric>();
		for(int i=0;i<5;i++){
			ProfileFaultMetric metrid = new ProfileFaultMetric();
			metrid.setMetricId(""+i);
			metrid.setProfileId(1);
			profileFaultMetrics.add(metrid);
		}
		int result = profileMetridDao.batchInsertProfileFaultMetric(profileFaultMetrics);
		System.out.println(result);
	}
	
	
	public void testQueryProfileMetric(){
		List<ProfileFaultMetric> test = profileMetridDao.selectProfileFaultMetrics(1);
		for (ProfileFaultMetric metric : test) {
			System.out.println(metric.getProfileId()+"-----"+metric.getMetricId());
		}
	}
	
	public void teseRemoveProfileMetric(){
		int result = profileMetridDao.deleteProfileFaultMetric(1);
		System.out.println(result);
	}
	
	public void teseRemoveProfileInstance(){
		int result = profileInstanceDao.deleteProfileFaultInstance(1);
		System.out.println(result);
	}
	
	
	public void teseTTT(){
		List<ProfileFaultInstance> profileFaultInstances = new ArrayList<ProfileFaultInstance>();
		for (int i=0;i<100000;i++) {
			ProfileFaultInstance instance = new ProfileFaultInstance();
			instance.setInstanceId(""+i);
			instance.setProfileId(5);
			profileFaultInstances.add(instance);//
		}
		long startDate = System.currentTimeMillis();//20
		System.out.println("startDate:"+startDate);
		int result = profilefaultInstanceService.insertProfileFaultInstance(profileFaultInstances);
		long endDate = System.currentTimeMillis();
		System.out.println("endDate:"+endDate);
		System.out.println("result:"+result);
		System.out.println("date:"+(endDate-startDate)/1000);
	}
	
	
	public void teseTs(){
		List<ProfileFaultInstance> instances = new ArrayList<ProfileFaultInstance>();
		for (int i=0;i<1000000;i++) {
			ProfileFaultInstance instance = new ProfileFaultInstance();
			instance.setInstanceId(""+i);
			instances.add(instance);//168
		}
		
		List<ProfileFaultMetric> metrics = new ArrayList<ProfileFaultMetric>();
		for (int i=0;i<1000000;i++) {
			ProfileFaultMetric metric = new ProfileFaultMetric();
			metric.setMetricId(""+i);
			metrics.add(metric);
		}
		
		ProfileFaultRelation profile = new ProfileFaultRelation();
		profile.setProfileName("批量插入测试");
		profile.setProfileDesc("批量插入测试");
		profile.setIsUse(1);
		profile.setResourceId("Linux");
		profile.setCreateUser("1");
		profile.setUpdateTime(Calendar.getInstance().getTime());
		profile.setAlarmLevel("CRITICAL,SERIOUS,WARN,UNKOWN");
		profile.setSnapshotScriptId("106000");
		profile.setRecoveryScriptId("106001");
		profile.setProfileFaultInstances(instances);
		profile.setProfileFaultMetrics(metrics);
		
		long startDate = System.currentTimeMillis();//20--1435223433084
		System.out.println("startDate:"+startDate);
		profileFaultService.insertProfileFault(profile);
		long endDate = System.currentTimeMillis();
		System.out.println("endDate:"+endDate);
		System.out.println("date:"+(endDate-startDate)/1000);
	}
	
	
	
	public void teseTsf(){
		List<ProfileFaultInstance> instances = new ArrayList<ProfileFaultInstance>();
		for (int i=0;i<1000000;i++) {
			ProfileFaultInstance instance = new ProfileFaultInstance();
			instance.setInstanceId(""+i);
			instance.setProfileId(11);
			instances.add(instance);//
		}
		
		
		long startDate = System.currentTimeMillis();//20--1435223433084
		System.out.println("startDate:"+startDate);
		profilefaultInstanceService.insertProfileFaultInstance(instances);
		long endDate = System.currentTimeMillis();
		System.out.println("endDate:"+endDate);
		System.out.println("date:"+(endDate-startDate)/1000);
	}
	
	public void testQueryProfilebyInstanceAndmetric(){
		Profilefault profilefault = profileFaultDao.queryProfilefaultByInstanceAndMetric("483522", "fileSysRate");
		System.out.println(profilefault.getProfileName());
	}
	
	public void testResourceInstance(){
		try {
			ResourceInstance instance = instanceService.getResourceInstance(2549);
			if(null!=instance.getParentInstance()){
				instance = instance.getParentInstance();
			}
			System.out.println("parentCategoryId\t"+instance.getParentCategoryId());
			System.out.println("categoryId\t"+instance.getCategoryId());
			System.out.println("resourceId\t"+instance.getResourceId());
			System.out.println("discoverWay\t"+instance.getDiscoverWay());
			List<DiscoverProp> props = instance.getDiscoverProps();
			for (DiscoverProp prop : props) {
				System.out.println(prop.getKey()+":");
				String[] values = prop.getValues();
				if(null!=values){
					for (String value : values) {
						System.out.println("\t\t\t\t\t"+value);
					}
				}
			}
				
//				String[] test = instance.getDiscoverPropBykey("collectType");
//				for (String string : test) {
//					System.out.println(string);
//				}
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testInsertProfileFaultRelation(){
		List<ProfileFaultInstance> instances = new ArrayList<ProfileFaultInstance>();
		ProfileFaultInstance instance = new ProfileFaultInstance();
		instance.setInstanceId("2563");
		instances.add(instance);
		List<ProfileFaultMetric> metrics = new ArrayList<ProfileFaultMetric>();
		ProfileFaultMetric metric = new ProfileFaultMetric();
		metric.setMetricId("fileSysTotalSize");
		metrics.add(metric);
		
		ProfileFaultRelation relation = new ProfileFaultRelation();
		relation.setProfileName("Windows fault profile");
		relation.setProfileDesc("Windows fault profile");
		relation.setIsUse(Profilefault.PROFILE_IS_USE_STATE_ENABLE);
		relation.setResourceId("windowswmi");
		relation.setCreateUser("1");
		relation.setUpdateTime(Calendar.getInstance().getTime());
		relation.setAlarmLevel("CRITICAL,SERIOUS,WARN,UNKOWN");
		relation.setSnapshotScriptId("106000");
		relation.setRecoveryScriptId("106001");
		relation.setProfileFaultInstances(instances);
		relation.setProfileFaultMetrics(metrics);
		profileFaultService.insertProfileFault(relation);
	}
	
	public void testExecuteScript(){
		try {
			ResourceInstance instance = instanceService.getResourceInstance(2563);
			profileFaultService.checkProfileFaultIsAlarmByInstanceAndMetric("2563", "fileSysTotalSize", InstanceStateEnum.SERIOUS);
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetScriptContent(){
		try {
			FileInputStream stream = new FileInputStream(fileClient.getFileByID(104000));
			if(null!=stream){
				byte b[] = new byte[1024];
				int len = 0;
				int temp = 0;
				while ((temp = stream.read()) != -1) {
					b[len] = (byte) temp;
					len++;
				}
				stream.close();
				System.out.println(new String(b, 0, len));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
