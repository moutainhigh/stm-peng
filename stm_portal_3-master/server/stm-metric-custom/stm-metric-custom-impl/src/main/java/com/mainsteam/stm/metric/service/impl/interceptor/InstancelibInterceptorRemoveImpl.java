package com.mainsteam.stm.metric.service.impl.interceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.dao.CustomMetricBindDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricBindDO;
import com.mainsteam.stm.metric.exception.CustomMetricException;

/**
 * 添加资源实例删除拦截类
 * 
 * @author xiaoruqiang
 */
public class InstancelibInterceptorRemoveImpl implements InstancelibInterceptor {

	private static final Log logger = LogFactory
			.getLog(InstancelibInterceptorRemoveImpl.class);

//	private ResourceInstanceService instanceService;

	private CustomMetricService customMetricService;

	private CustomMetricBindDAO bindDAO;

	/**
	 * @param bindDAO
	 *            the bindDAO to set
	 */
	public final void setBindDAO(CustomMetricBindDAO bindDAO) {
		this.bindDAO = bindDAO;
	}
	
	/**
	 * @param instanceService the instanceService to set
	 */
	public final void setInstanceService(ResourceInstanceService instanceService) {
//		this.instanceService = instanceService;
	}



	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if (instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT) {
			try {
				List<Long> deleteIds = (List<Long>) instancelibEvent
						.getSource();
				if (!deleteIds.isEmpty()) {
//					List<Long> parentInstanceIds = new ArrayList<>();
//					for (Long instId : parentInstanceIds) {
//						ResourceInstance instance = instanceService
//								.getResourceInstance(instId);
//						
//						if (instance != null
//								&& instance.getParentId() <= 0
//								&& !"Layer2Link".equals(instance
//										.getResourceId())) {
//							parentInstanceIds.add(instance.getId());
//						}
//					}
					List<CustomMetricBindDO> customMetricBindDOs = bindDAO
							.getCustomMetricBindByInstanceIds(deleteIds);
					if (customMetricBindDOs != null
							&& customMetricBindDOs.size() > 0) {
						Set<Long> toClearInstanceIds = new HashSet<>();
						for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
							Long instanceIdObj = customMetricBindDO
									.getInstanceId();
							toClearInstanceIds.add(instanceIdObj);
						}
						customMetricService
								.clearCustomMetricBindsByInstanceIds(new ArrayList<>(
										toClearInstanceIds));
					}
				}
			} catch (CustomMetricException e) {
				if (logger.isErrorEnabled()) {
					logger.error("deleteCustomMetric error！", e);
				}
			} catch (InstancelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("deleteCustomMetric error！", e);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("deleteCustomMetric error！", e);
				}
			}
		}
	}

	public void setCustomMetricService(CustomMetricService customMetricService) {
		this.customMetricService = customMetricService;
	}
}
