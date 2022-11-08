package com.mainsteam.stm.camera.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.camera.bo.CameraBo;
import com.mainsteam.stm.camera.bo.CameraDcListPageVo;
import com.mainsteam.stm.camera.bo.CameraVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;

public abstract interface ICameraService {
	public Map<String, Object> addCameraInstances(Map paramter);
	
	public Map<String, Object> discovery(Map paramter);
	
	public boolean updateCameraInfo(Map paramter);

	public List<List<List<Object>>> getCameraDetial(Long instanceId,ILoginUser user);

	public Map<String, String> getCameraConInfo(Long instanceId);

	public List<CustomGroupBo> getList(long userId);
	
	//##################### zhanghb新增 #####################// 
	public Map<String, Object> discoverCameraResource(Map<String, String> paramMap, ILoginUser user);

	public boolean addCameraMonitor(ILoginUser user, List<Long> instanceIds);

	public CameraDcListPageVo getDiscoveryList(CameraDcListPageVo pageVo, ILoginUser user);

	public int testDiscover(Map<String, String> paramMap, long instanceId);

	public int updateDiscoverParam(Map<String, String> paramMap, long instanceId);

	public Map<String, Object> reDiscover(Map<String, String> paramMap, long instanceId, ILoginUser user);

    void selectPageForCamera(Page<CameraBo, CameraVo> page);

    void getOfflineNumber(Page<CameraBo, CameraVo> page);
	
}
