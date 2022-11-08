package com.mainsteam.stm.portal.business.service.report.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.mainsteam.stm.portal.business.report.api.BizSerReportListener;
import com.mainsteam.stm.portal.business.report.api.BizSerReportListenerEngine;
import com.mainsteam.stm.portal.business.report.obj.BizSerReportEvent;

/**
 * <li>文件名称: BizSerReportListenerEngineImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: .业务报表接口监听引擎实现类..</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   caoyong
 */
public class BizSerReportListenerEngineImpl implements BizSerReportListenerEngine,BeanPostProcessor{
	Logger logger = Logger.getLogger(BizSerReportListenerEngineImpl.class);
	/**
	 * 业务应用报表监听接口集合
	 */
	List<BizSerReportListener> listeners = new ArrayList<BizSerReportListener>();
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(bean instanceof BizSerReportListener){
			listeners.add((BizSerReportListener)bean);
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
	@Override
	public void handleListen(BizSerReportEvent event) {
		for(BizSerReportListener listener : listeners){
			listener.listen(event);
		}
	}

}
