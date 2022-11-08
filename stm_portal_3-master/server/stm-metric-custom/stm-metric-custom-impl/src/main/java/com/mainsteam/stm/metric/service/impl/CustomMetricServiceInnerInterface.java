/**
 * 
 */
package com.mainsteam.stm.metric.service.impl;

import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.cache.CustomMetricCache;

/**
 * 为了单元使用
 * 
 * @author ziw
 *
 */
public interface CustomMetricServiceInnerInterface extends CustomMetricService {
	public CustomMetricCache getCustomMetricCache();
}
