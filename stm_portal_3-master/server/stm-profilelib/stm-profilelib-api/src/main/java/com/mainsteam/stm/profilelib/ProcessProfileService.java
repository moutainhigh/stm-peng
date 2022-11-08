package com.mainsteam.stm.profilelib;

import java.util.List;
import com.mainsteam.stm.profilelib.obj.MetricChange;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;

/**
 * 策略服务接口 <br/>
 * 设计原则：<br/>
 * 1、对外不暴露出厂策略（无法查询修改和删除） <br/>
 * 2、不修改策略的全对象，对于监控频度、指标阈值是单独修改的<br/>
 * 3、对于想不清楚的业务场景，暂不提供接口
 * 
 * <p>
 * Create on : 2014-6-17<br>
 * <p>
 * </p>
 * <br>
 * 
 * @author sunsht <br>
 * @version profilelib-api v4.1
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public interface ProcessProfileService extends ProfileService{
	
	/**
	 * 隔离墙DCS 需要用的方法列表
	 * @param resourceId
	 */
	void updateDefaultProfileStateByResourceId(List<String> resourceId,boolean isUse);
	void insertProfile(Profile profile);
	
	void deleteProfile(List<Long> deleteProfileIds);

	void addBindInstance(List<ProfileInstanceRelation> relations);
	
	void removeBindInstance(List<Long> instanceIds);
	
	void updateMetricMonitor(MetricChange change);
	
	void updateMetricFreq(MetricChange change);
}
