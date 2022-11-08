package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.portal.resource.bo.Wbh4HomeBo;

public interface ICustomResGroupApi {
	
	public List<Wbh4HomeBo> getWbh4HomeLikeGroupId(long groupId);
	
	public int updateHomeWbh(Wbh4HomeBo wbh4Home);

	public void operDelResGroupOthers(long groupId);
	
	public void operUpdateResGroupOthers(long groupId, String groupName);
	
	public Map<String, Object> changeGroupSort(Long groupId, String direction);
}
