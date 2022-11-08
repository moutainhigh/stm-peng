package com.mainsteam.stm.plugin.wmi.bio;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.mainsteam.stm.plugin.wmi.WMIAgent;
import com.mainsteam.stm.plugin.wmi.bean.Request;
import com.mainsteam.stm.plugin.wmi.bean.Response;
import com.mainsteam.stm.plugin.wmi.bio.task.BIOQueryTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BIOAgent implements WMIAgent {

	private static final Log logger = LogFactory.getLog(BIOAgent.class);

	private final InetSocketAddress address;
	private final int timeout;
	private final ExecutorService queryExecutor = Executors.newFixedThreadPool(1);

	public BIOAgent(String ip, int port, int timeout) {
		this.address = new InetSocketAddress(ip, port);
		this.timeout = timeout;
	}

	public Response query(Request request) {
		Future<Response> responseFuture = queryExecutor.submit(new BIOQueryTask(address, request, timeout));
		try {
			return responseFuture.get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e.getCause());
		} catch (TimeoutException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public void start() {
		// nothing to do
	}
	
	public void stop() {
		try {
			queryExecutor.shutdown();
			queryExecutor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	

	public static void main(String[] args) {
		BIOAgent agent = new BIOAgent("192.168.10.16", 12345, 10000);
		Request request = new Request();
		request.setWmi_namespace("192.168.1.36");
		request.setWmi_domain_user("administrator");
		request.setWmi_password("root3306");
		request.setWmi_query_cmd("root\\cimv2::select Name from Win32_ComputerSystem");
		System.out.println(agent.query(request).getWmi_query_msg());
		agent.stop();
	}

}
