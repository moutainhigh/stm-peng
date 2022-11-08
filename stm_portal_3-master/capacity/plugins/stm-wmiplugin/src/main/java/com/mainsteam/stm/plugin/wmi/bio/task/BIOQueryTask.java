package com.mainsteam.stm.plugin.wmi.bio.task;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.wmi.bean.Request;
import com.mainsteam.stm.plugin.wmi.bean.Response;

public class BIOQueryTask implements Callable<Response> {

	private static final Log logger = LogFactory.getLog(BIOQueryTask.class);

	private final InetSocketAddress address;
	private final Request request;
	private final int timeout;

	public BIOQueryTask(InetSocketAddress address, Request request, int timeout) {
		this.request = request;
		this.timeout = timeout;
		this.address = address;
	}

	@Override
	public Response call() {
		try (Socket socket = new Socket()) {
			socket.setSoTimeout(timeout);
			socket.connect(address, timeout);
			// send
			OutputStream out = socket.getOutputStream();
			String requestString = JSON.toJSONString(request);
			if (logger.isDebugEnabled())
				logger.debug(requestString);
			out.write((requestString + "\n;").getBytes("UTF-8"));
			out.flush();
			// receive
			InputStream in = socket.getInputStream();
			ArrayList<Byte> bufferList = new ArrayList<Byte>(1024);
			int b;
			while ((b = in.read()) > 0) {
				if (b == ';' && bufferList.size() > 0 && bufferList.get(bufferList.size() - 1) == '\n') {
					break;
				}
				bufferList.add((byte) b);
			}
			// parse
			byte[] bufferArray = new byte[bufferList.size()];
			for (int i = 0; i < bufferList.size(); ++i) {
				bufferArray[i] = bufferList.get(i);
			}
			String responseString = new String(bufferArray, "UTF-8");
			if (logger.isDebugEnabled())
				logger.debug(responseString);
			return JSON.parseObject(responseString, Response.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
