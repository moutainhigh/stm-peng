package com.mainsteam.stm.plugin.wmi.serial.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.wmi.bean.Response;

public class SerialReadTask implements Runnable {

	private static final Log logger = LogFactory.getLog(SerialReadTask.class);

	private final Selector selector;
	private final SocketChannel channel;
	private final ConcurrentHashMap<String, Response> uuidResponseMap;

	public SerialReadTask(Selector selector, SocketChannel channel, ConcurrentHashMap<String, Response> uuidResponseMap) throws IOException {
		this.selector = selector;
		this.channel = channel;
		this.uuidResponseMap = uuidResponseMap;
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}

	@Override
	public void run() {
		ArrayList<Byte> bufferList = new ArrayList<Byte>(1024);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			if (selector.select() > 0) {
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iter = keys.iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					if (key.isReadable()) {
						while (channel.read(buffer) > 0) {
							buffer.flip();
							while (buffer.hasRemaining()) {
								byte b = buffer.get();
								if (b == ';' && bufferList.size() > 0 && bufferList.get(bufferList.size() - 1) == '\n') {
									byte[] bufferArray = new byte[bufferList.size()];
									for (int i = 0; i < bufferList.size(); ++i) {
										bufferArray[i] = bufferList.get(i);
									}
									String responseString = new String(bufferArray, "UTF-8");
									if (logger.isDebugEnabled())
										logger.debug(responseString);
									Response response = JSON.parseObject(responseString, Response.class);
									String uuid = response.getWmi_query_req().getWmi_query_uuid().intern();
									synchronized (uuid) {
										uuidResponseMap.replace(uuid, response);
										uuid.notify();
									}

								}
								bufferList.add((byte) b);

							}
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClosedSelectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
