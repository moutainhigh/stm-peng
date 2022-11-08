package com.mainsteam.stm.home.workbench.main.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.home.workbench.main.dao.IUserWorkbenchDao;
import com.mainsteam.stm.home.workbench.main.dao.IWorkbenchDao;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;

/**
 * <li>文件名称: UserWorkbenchImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */
@Service
public class UserWorkbenchImpl implements IUserWorkBenchApi{

	private static final Logger log=Logger.getLogger(UserWorkbenchImpl.class);
	
	@Autowired
	private IUserWorkbenchDao userWorkbenchDao;

	@Autowired
	private IWorkbenchDao workbenchDao;
	
	private List<WorkBench> workBenches;

	@Autowired
	@Qualifier("customResourceGroupApi")
	private ICustomResourceGroupApi resourceGroupApi;
	@Autowired
	@Qualifier("stm_system_resourceApi")
	private com.mainsteam.stm.system.resource.api.IResourceApi systemResourceApi;
	@Autowired
	private AlarmEventService alarmEventService;
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	
	@Override
	public List<WorkBench> getUserWorkBenchs(Long userId) {
		List<WorkBench> wbList = userWorkbenchDao.getUserWorkBenchs(userId);
		List<WorkBench> resultList = new ArrayList<WorkBench>();
		
		for(WorkBench wb:wbList){
			switch (wb.getWorkbenchId().intValue()) {
			case 2:
			case 4:
			case 5:
				if(null!=wb.getSelfExt() && !"".equals(wb.getSelfExt())){
					Long ins = Long.parseLong(wb.getSelfExt());
					try {
						ResourceInstance ri = resourceInstanceService.getResourceInstance(ins);
						if(null==ri || null==ri.getLifeState()){
							wb.setSelfExt("");
						}else{
							InstanceLifeStateEnum lse = ri.getLifeState();
							if(lse==InstanceLifeStateEnum.DELETED){
								wb.setSelfExt("");
							};
						}
					} catch (InstancelibException e) {
						log.error("getUserWorkBenchs getResourceInstance id="+ins+","+e);
						continue;
					}
				}
				break;
			}
			resultList.add(wb);
		}
		return resultList;
	}

	@Override
	public int setUserWorkBenchs(Long userId,List<WorkBench> uws) {
		userWorkbenchDao.delUserWorkBench(userId);
		return userWorkbenchDao.insertUserWorkBenchs(uws);
	}

	@Override
	public int delUserWorkBench(WorkBench uw) {
		return userWorkbenchDao.delSingleUserWorkBench(uw);
	}

	@Override
	public int setExt(WorkBench wb) {
		return userWorkbenchDao.setExt(wb);
	}
	
	@Override
	public List<WorkBench> getAllWorkBench() {
		if(workBenches==null){
			workBenches=workbenchDao.getAllWorkBench();
			for(int i=0,len=workBenches.size();i<len;i++){
				workBenches.get(i).setSort(i);
			}
		}
		return workBenches;
	}
	
	@Override
	public int insertUserAllWorkbenchs(Long userId) {
		return userWorkbenchDao.insertUserWorkBenchs(getBenchesByResetUserId(userId));
	}

	@Override
	public int delUserAllWorkbenchs(Long userId) {
		return userWorkbenchDao.delUserWorkBench(userId);
	}
	@Override
	public int delUserAllWorkbenchs(Long []userId) {
		return userWorkbenchDao.delUsersWorkBench(userId);
	}
	
	private List<WorkBench> getBenchesByResetUserId(Long userId){
		List<WorkBench> benches=this.getAllWorkBench();
		List<WorkBench> temps=new ArrayList<WorkBench>();
		WorkBench wb,wbS;
		for(int i=0,len=benches.size();i<len;i++){
			wbS=benches.get(i);
			wb=new WorkBench();
			wb.setUserId(userId);
			wb.setSort(wbS.getSort());
			wb.setWorkbenchId(wbS.getId());
			wb.setDomainId(0l);
			temps.add(wb);
		}
		return temps;
	}

	@Override
	public WorkBench getUserWorkBenchById(WorkBench wb) {
		return userWorkbenchDao.getUserWorkBenchById(wb);
	}

	@Override
	public WorkBench getWorkBenchById(Long id) {
		return workbenchDao.getWorkBenchById(id);
	}

	@Override
	public List<Long> getInstanceIdByGroupId(long groupId, ILoginUser user,String type,Long... domainId) {
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		if(domainId != null){
			//List<Long> domainIds = new ArrayList<Long>();
			//domainIds.add(domainId);
			queryBo.setDomainIds(Arrays.asList(domainId));
		}
		//获取用户、域、资源类别下的资源实例
		//List<Long> resourceInstanceIds = new ArrayList<Long>();
		Set<Long> parentInstaceIdSet = new HashSet<Long>();
		if(type!=null && !type.equals("")){
			//通过“主机、网络、应用”获取子资源类别
			List<String> categories = this.getCategorIds(type);
			queryBo.setCategoryIds(categories);
		}
		
		List<ResourceInstanceBo> instanceBos = systemResourceApi.getResources(queryBo);
		for(int j = 0; instanceBos != null && j < instanceBos.size(); j++){
			ResourceInstanceBo instance = instanceBos.get(j);
			if(instance!=null ){
				parentInstaceIdSet.add(instance.getId());
			}
		}
		List<Long> resultInstanceIds = new ArrayList<Long>();
		//如果设置了资源组，在资源组中过虑资源
		if(groupId!=0){
			Set<Long> groupInstanceIdSet = new HashSet<Long>();
			List<CustomGroupBo> boList = resourceGroupApi.getList(user.getId());
			List<Long> resourceIds = new ArrayList<Long>();
			CustomGroupBo Bo = resourceGroupApi.getCustomGroup(groupId);
			boList2Tree(Bo, boList, resourceIds);
					List<String> resourceDatas= new ArrayList<String>();
					for (Long id : resourceIds) {
						resourceDatas.add(String.valueOf(id));
					}
					List<String> groupInstanceIds = resourceDatas != null ? resourceDatas : null;//资源组里的所有资源
					if(!parentInstaceIdSet.isEmpty() && groupInstanceIds != null){
						for(Long parentInstanceId : parentInstaceIdSet){
							if(groupInstanceIds.contains(String.valueOf(parentInstanceId))){
								groupInstanceIdSet.add(parentInstanceId);
								resultInstanceIds.add(parentInstanceId);
							}
						}
						if(!groupInstanceIdSet.isEmpty()){
							List<Long> childInstanceIdList = resourceInstanceService.getAllChildrenInstanceIdbyParentId(groupInstanceIdSet);
							for(int j = 0; childInstanceIdList != null && j < childInstanceIdList.size(); j++){
								resultInstanceIds.add(childInstanceIdList.get(j));
							}
						}
					}
		}
		
		return resultInstanceIds;
	}

	private void boList2Tree(CustomGroupBo parentBo, List<CustomGroupBo> boList, List<Long> resourceIds){
		for (int i = 0; parentBo != null && parentBo.getResourceInstanceIds() != null && i < parentBo.getResourceInstanceIds().size(); i++) {
			resourceIds.add(Long.valueOf(parentBo.getResourceInstanceIds().get(i)));
		}
		for (int i = 0; i < boList.size(); i++) {
			CustomGroupBo bo = boList.get(i);
			if(bo.getPid() != null && parentBo != null && parentBo.getId().longValue() == bo.getPid().longValue()){
				boList2Tree(bo, boList, resourceIds);
			}
		}
	}
	
	public List<String> getCategorIds(String type){
		List<String> categories = null;
		if(null!=type){
			if(type.equals("All")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.HOST);
				categories.add(CapacityConst.NETWORK_DEVICE);
//				categories.add(CapacityConst.STORAGE);
				categories.add(CapacityConst.DATABASE);
				categories.add(CapacityConst.MIDDLEWARE);
				categories.add(CapacityConst.J2EEAPPSERVER);
				categories.add(CapacityConst.WEBSERVER);
				categories.add(CapacityConst.STANDARDSERVICE);
				categories.add(CapacityConst.MAILSERVER);
				categories.add(CapacityConst.DIRECTORY);
				categories.add(CapacityConst.LOTUSDOMINO);
				categories.add(CapacityConst.VM);
				categories.add(CapacityConst.SNMPOTHERS);
				categories.add("UniversalModel");
			}
			if(type.equals("Host")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.HOST);
			}
			if(type.equals("NetworkDevice")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.NETWORK_DEVICE);
				categories.add(CapacityConst.SNMPOTHERS);
			}
			if(type.equals("App")){
				categories = new ArrayList<String>();
//				categories.add(CapacityConst.STORAGE);
				categories.add(CapacityConst.DATABASE);//
				categories.add(CapacityConst.MIDDLEWARE);//
				categories.add(CapacityConst.J2EEAPPSERVER);//
				categories.add(CapacityConst.WEBSERVER);//
				categories.add(CapacityConst.STANDARDSERVICE);//
				categories.add(CapacityConst.MAILSERVER);//
				categories.add(CapacityConst.DIRECTORY);//
				categories.add(CapacityConst.LOTUSDOMINO);//
				categories.add(CapacityConst.VM);
				categories.add("UniversalModel");
			}
		}
		return categories;
	}
//	@Override
//	public List<Biz> getBizs(ILoginUser user, HttpServletRequest request) {
//		List<WorkBench> benchs=getAllWorkBench();
//		List<Biz> bizs=new ArrayList<Biz>();
//		Biz biz;
//		for(WorkBench bench:benchs){
//			biz=new Biz();
//		}
//		return null;
//	}

	@Override
	public List<Long> getInstanceIdByGroupId(long groupId, ILoginUser user) {
		List<CustomGroupBo> boList = resourceGroupApi.getList(user.getId());
		List<Long> resourceIds = new ArrayList<Long>();
		CustomGroupBo Bo = resourceGroupApi.getCustomGroup(groupId);
		boList2Tree(Bo, boList, resourceIds);
		return resourceIds;
	}

	@Override
	public int setUserWorkBenchByDefaultId(WorkBench bench) {
		return userWorkbenchDao.setUserBenchDefaultIdById(bench);
		
	}
}


