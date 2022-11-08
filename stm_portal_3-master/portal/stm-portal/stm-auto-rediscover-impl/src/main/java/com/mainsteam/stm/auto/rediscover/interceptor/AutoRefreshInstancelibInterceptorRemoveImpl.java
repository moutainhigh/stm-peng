package com.mainsteam.stm.auto.rediscover.interceptor;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


//import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;
import com.mainsteam.stm.profilelib.ProfileAutoRediscoverService;
import com.mainsteam.stm.profilelib.interceptor.InstancelibInterceptorRemoveImpl;

/**
 * 添加资源实例删除拦截类,用于计算资源删除，取消自动刷新该资源
 * 
 * @author xiaoruqiang
 */
public class AutoRefreshInstancelibInterceptorRemoveImpl implements InstancelibInterceptor {

	private static final Log logger = LogFactory
			.getLog(InstancelibInterceptorRemoveImpl.class);

	private ProfileAutoRediscoverService profileAutoRediscoveryService;

	private ResourceInstanceExtendService resourceInstanceExtendService;
	
	public void setResourceInstanceExtendService(
			ResourceInstanceExtendService resourceInstanceExtendService) {
		this.resourceInstanceExtendService = resourceInstanceExtendService;
	}

	public void setProfileAutoRediscoveryService(ProfileAutoRediscoverService profileAutoRediscoveryService) {
		this.profileAutoRediscoveryService = profileAutoRediscoveryService;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if (instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT || instancelibEvent.getEventType() == EventEnum.INSTANCE_CHILDREN_DELETE_EVENT) {
			try {
				List<Long> deleteIds = (List<Long>) instancelibEvent.getSource();
				// 查找父资源，如果是，取消资源自动发现。
				StringBuilder b = new StringBuilder(10000);
				for (long deleteInstanceId : deleteIds) {
					ResourceInstance resourceInstance = resourceInstanceExtendService
							.getResourceInstanceById(deleteInstanceId);
					if(resourceInstance != null){
						if(resourceInstance.getParentId()<=0){
							//如果是主资源删除资源自动重新发现策略
							profileAutoRediscoveryService.deleteAutoRediscoverProfileInstanceByInstanceId(resourceInstance.getId());
							if(logger.isInfoEnabled()){
								b.append("resourceinstance has deleted,Cancel resourceinstance auto refresh,instanceId=");
								b.append(resourceInstance.getId());
							}
						}
					}
				}
				if(logger.isInfoEnabled()){
					logger.info(b.toString());
				}	
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("deleteInstances error！", e);
				}
			}
		}
	}
}
