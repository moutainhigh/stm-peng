package com.mainsteam.stm.portal.resource.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.portal.resource.api.ICustomResGroupApi;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.bo.Wbh4HomeBo;
import com.mainsteam.stm.portal.resource.dao.ICustomResGroupDao;

public class CustomResGroupImpl implements ICustomResGroupApi,InstancelibInterceptor{
	
	@Resource
	private ICustomResGroupDao customResGroupDao;
	
	@Autowired
	private ICustomResourceGroupApi customResourceGroupApi;
	
	@Override
	public List<Wbh4HomeBo> getWbh4HomeLikeGroupId(long groupId) {
		return customResGroupDao.getWbh4HomeLikeGroupId(groupId);
	}

	@Override
	public int updateHomeWbh(Wbh4HomeBo wbh4Home) {
		return customResGroupDao.updateWbh4HomeSelfExtByPrimary(wbh4Home);
	}

	@Override
	public void operDelResGroupOthers(long groupId) {
		List<Wbh4HomeBo> wbh4HomeList = getWbh4HomeLikeGroupId(groupId);
		for (int i = 0; wbh4HomeList != null && i < wbh4HomeList.size(); i++) {
			Wbh4HomeBo wbh4Home = wbh4HomeList.get(i);
			if(wbh4Home.getSelfExt() != null){
				// topN
				if(1 == wbh4Home.getWorkbenchId() && wbh4Home.getSelfExt().split(";").length == 3){
					String[] selfExt = wbh4Home.getSelfExt().split(";");
					wbh4Home.setSelfExt(selfExt[0] + ";" + selfExt[1] + ";0,全部");
					updateHomeWbh(wbh4Home);
				// 告警一览
				}else if(3 == wbh4Home.getWorkbenchId()){
					wbh4Home.setSelfExt("0,全部");
					updateHomeWbh(wbh4Home);
				// 告警统计
				}else if(7 == wbh4Home.getWorkbenchId()){
					wbh4Home.setSelfExt("0,全部");
					updateHomeWbh(wbh4Home);
				}
			}
		}
	}

	@Override
	public void operUpdateResGroupOthers(long groupId, String groupName) {
		List<Wbh4HomeBo> wbh4HomeList = getWbh4HomeLikeGroupId(groupId);
		for (int i = 0; wbh4HomeList != null && i < wbh4HomeList.size(); i++) {
			Wbh4HomeBo wbh4Home = wbh4HomeList.get(i);
			if(wbh4Home.getSelfExt() != null){
				// topN
				if(1 == wbh4Home.getWorkbenchId() && wbh4Home.getSelfExt().split(";").length == 3){
					String[] selfExt = wbh4Home.getSelfExt().split(";");
					if(selfExt[2].split(",").length == 2 && !groupName.equals(selfExt[2].split(",")[1])){
						wbh4Home.setSelfExt(selfExt[0] + ";" + selfExt[1] + ";" + groupId + "," + groupName);
						updateHomeWbh(wbh4Home);
					}
				// 告警一览
				}else if(3 == wbh4Home.getWorkbenchId() && wbh4Home.getSelfExt().split(",").length == 2){
					if(!groupName.equals(wbh4Home.getSelfExt().split(",")[1])){
						wbh4Home.setSelfExt(groupId + "," + groupName);
						updateHomeWbh(wbh4Home);
					}
				// 告警统计
				}else if(7 == wbh4Home.getWorkbenchId() && wbh4Home.getSelfExt().split(",").length == 2){
					if(!groupName.equals(wbh4Home.getSelfExt().split(",")[1])){
						wbh4Home.setSelfExt(groupId + "," + groupName);
						updateHomeWbh(wbh4Home);
					}
				}
			}
		}
	}

	@Override
	public void interceptor(InstancelibEvent instancelibEvent) throws Exception {
		// TODO Auto-generated method stub
		if(instancelibEvent == null){
			return;
		}
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT){
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			// deleteIds 需要删除的资源实例Id 集合
			//删除自定义资源组关系
			if(deleteIds == null || deleteIds.size() <= 0){
				return;
			}
			System.out.println("Into CustomResourceGroupImpl to deleteGroupAndResourceRelation !");
			long[] ids = new long[deleteIds.size()];
			for(int i = 0 ; i < deleteIds.size() ; i ++){
				ids[i] = deleteIds.get(i);
			}
			customResourceGroupApi.deleteGroupAndResourceRelation(ids);
		}
	}

	@Override
	public Map<String, Object> changeGroupSort(Long groupId, String direction) {
		Map<String, Object> map = new HashMap<String, Object>();
		CustomGroupBo currentBo = customResourceGroupApi
				.getCustomGroup(groupId);
		List<CustomGroupBo> bos = customResourceGroupApi.getChildGroupsById(currentBo.getEntryId(), currentBo.getPid());
		Collections.sort(bos);
		CustomGroupBo changeBo = null;
		boolean flag = true;
		for (int i = 0; i < bos.size(); i++) {
			CustomGroupBo bo = bos.get(i);
			if (bo.getId().equals(groupId)) {
				if ("up".equalsIgnoreCase(direction)) {
					if (i == 0) {
						flag = false;
						map.put("errorMsg", "此资源组上方无同级资源组");
						break;
					}
					changeBo = bos.get(i - 1);
				}
				if ("down".equalsIgnoreCase(direction)) {
					if (i == (bos.size() - 1)) {
						flag = false;
						map.put("errorMsg", "此资源组下方无同级资源组");
						break;
					}
					changeBo = bos.get(i + 1);
				}
			}
		}
		if (currentBo == null || changeBo == null) {
			flag = false;
		}
		if (flag) {
			customResourceGroupApi.updateGroupSort(changeBo.getSort(), groupId);
			customResourceGroupApi.updateGroupSort(currentBo.getSort(),
					changeBo.getId());
		}
		map.put("result", flag);
		return map;
	}

}
