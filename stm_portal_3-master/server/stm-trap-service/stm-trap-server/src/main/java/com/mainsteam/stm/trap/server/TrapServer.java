package com.mainsteam.stm.trap.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSON;

public class TrapServer implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {
	private static final Log logger = LogFactory.getLog(TrapServer.class);
	private ArrayList<TrapDataDriver> dataDrivers = new ArrayList<>();
	private String listen;
	public void startServer(int port, final ArrayList<TrapDataHandler> handleres) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("startServer start to listen at port " + port);
		}
		TrapDataDriver dataDriver = new TrapDataDriver(port, handleres.toArray(new TrapDataHandler[handleres.size()]));
		dataDrivers.add(dataDriver);
		dataDriver.start();
	}
	
	public void setListen(String listen) {
		this.listen = listen;
	}

	public void stop() {
		for (TrapDataDriver endpoint : dataDrivers) {
			endpoint.stop();
		}
	}

	private static final HashMap<Integer, ArrayList<TrapDataHandler>> handlerMap = new HashMap<>();

	@Override
	public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
		if (bean instanceof TrapDataHandler) {
			TrapDataHandler h = (TrapDataHandler) bean;
			ArrayList<TrapDataHandler> list = handlerMap.get(h.getPort());
			if (logger.isInfoEnabled()) {
				logger.info("TrapServer scan handler[" + name + "," + bean.getClass() + " port=" + h.getPort() + "]");
			}
			if (list == null) {
				list = new ArrayList<>();
				handlerMap.put(h.getPort(), list);
			}
			list.add(h);
		}
		return bean;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(!"on".equalsIgnoreCase(listen)) {
			if (logger.isInfoEnabled()) {
				logger.info("trap is off.please modify the $server/config/properties/trap.properties ");
			}
			return;
		}
		if (event.getApplicationContext().getParent() == null) {
			logger.info("handlerMap:" + JSON.toJSONString(handlerMap));
			for (Entry<Integer, ArrayList<TrapDataHandler>> nt : handlerMap.entrySet()) {
				nt.getValue().trimToSize();
				try {
					startServer(nt.getKey(), nt.getValue());
				} catch (IOException e) {
					logger.error("start port[" + nt.getKey() + "] failed:" + e.getMessage(), e);
				}
			}
		}
	}
}
