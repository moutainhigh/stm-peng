package com.mainsteam.stm.trap.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.xsocket.datagram.IDatagramHandler;
import org.xsocket.datagram.IEndpoint;
import org.xsocket.datagram.UserDatagram;
import org.xsocket.datagram.Endpoint;
import com.alibaba.fastjson.JSON;

public class XSocketServer implements BeanPostProcessor,ApplicationListener<ContextRefreshedEvent> {
	private static final Log logger=LogFactory.getLog(XSocketServer.class);
	
	private final ExecutorService threadExecutor =new ThreadPoolExecutor(5,50,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),new ThreadFactory() {
		private int counter = 0;
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t = new Thread(runnable,"TrapDataHandler-"+ counter++);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	});
	
	 /**设置当前的端口*/  
    public void startServer(int port,final List<TrapDataHandler> handleres) throws IOException {
		List<InetAddress> inetAddresses = new ArrayList<>(2);
		Properties prop=new Properties();
		prop.load(XSocketServer.class.getClassLoader().getResourceAsStream("properties/stm.properties"));
		String ip=prop.getProperty("ip");
		boolean flag = false;
		if(StringUtils.isBlank(ip)) {
			try {
				Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
				while (enumeration.hasMoreElements()) {
					NetworkInterface networkInterface = enumeration.nextElement();
					if(networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual()){
						Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
						while (inetAddressEnumeration.hasMoreElements()) {
							InetAddress inetAddress = inetAddressEnumeration.nextElement();
							if(inetAddress instanceof Inet4Address){
								inetAddresses.add(inetAddress);
							}
						}

					}

				}

			} catch (SocketException e) {
				if(logger.isWarnEnabled()) {
					logger.warn("XSocketServer get networkInterface error." + e.getMessage(), e);
				}
				flag = true;
			}
		}else {
			flag = true;
		}
		if(flag) {
			InetAddress address=InetAddress.getByName(ip);
			inetAddresses.add(address);
		}

		for(InetAddress inetAddress : inetAddresses) {
			//创建一个服务端的对象
			Endpoint  endpoint = new Endpoint(1024,new IDatagramHandler() {
				@Override
				public boolean onDatagram(IEndpoint localEndpoint) throws IOException {

					final UserDatagram response =localEndpoint.receive();

					if(response !=null) {
						final byte[] bytes = response.readBytes();
						if(logger.isDebugEnabled()) {
							logger.debug("Syslog receive : " + new String(bytes, "UTF8"));
						}
						for(final TrapDataHandler handler:handleres){
							threadExecutor.execute(new Runnable() {
								@Override public void run() {
									// malachi 将获取到的日志给处理类
									handler.handleData(response.getRemoteAddress().getHostAddress(),bytes);
								}
							});
						}
					}
					return true;
				}
			}, inetAddress, port);
			if(logger.isInfoEnabled()) {
				logger.info("XSocketServer listens this ip : " + inetAddress.getHostAddress() + ":" + endpoint.getLocalPort());
			}
		}
    }
    
    

    private static final Map<Integer,List<TrapDataHandler>> handlerMap=new HashMap<>();
    
    
	
    @Override
	public Object postProcessAfterInitialization(Object bean, String name)	throws BeansException {
		return bean;
	}
	@Override
	public Object postProcessBeforeInitialization(Object bean, String name)	throws BeansException {
		logger.info("postProcessBeforeInitialization["+name+","+bean.getClass()+"]");
		if(bean instanceof TrapDataHandler){
			TrapDataHandler h=(TrapDataHandler)bean;
			List<TrapDataHandler> list=handlerMap.get(h.getPort());
			if(list==null){
				list=new ArrayList<>();
				handlerMap.put(h.getPort(), list);
			}
			list.add(h);
		}
		return bean;
	}

	// malachi 获取应用程序事件
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){
			logger.info("handlerMap:"+JSON.toJSONString(handlerMap));
			
			for(Entry<Integer,List<TrapDataHandler>> nt:handlerMap.entrySet()){
				try {
					startServer(nt.getKey(),nt.getValue());
				} catch (IOException e) {
					logger.error("start port["+nt.getKey()+"] failed:"+e.getMessage(),e);
				}
			}
		}
	}
}
