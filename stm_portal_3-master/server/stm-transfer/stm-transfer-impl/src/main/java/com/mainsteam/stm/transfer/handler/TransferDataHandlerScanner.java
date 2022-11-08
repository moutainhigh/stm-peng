/**
 * 
 */
package com.mainsteam.stm.transfer.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author ziw
 *
 */
public class TransferDataHandlerScanner implements BeanPostProcessor {
	
	

	/**
	 * 
	 */
	public TransferDataHandlerScanner() {
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

}
