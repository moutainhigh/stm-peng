package com.mainsteam.stm.plugin.wmi.serial;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.wmi.WMIAgent;
import com.mainsteam.stm.plugin.wmi.bean.Request;
import com.mainsteam.stm.plugin.wmi.bean.Response;

public class SerialAgent implements WMIAgent {

	private static final Log LOGGER = LogFactory.getLog(SerialAgent.class);
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private final InetSocketAddress address;
	private final int timeout;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private Selector selector;
	private SocketChannel channel;
	private ExecutorService readExec;
	private ConcurrentHashMap<String, Response> uuidResponseMap = new ConcurrentHashMap<String, Response>();

	private boolean available;

	public SerialAgent(String ip, int port, int timeout) {
		this.address = new InetSocketAddress(ip, port);
		this.timeout = timeout;
	}

	@Override
	public void start() {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Try to start agent " + this);
		// if agent is already started, don't need to start again.
		lock.writeLock().lock();
		if (!available) {
			if (LOGGER.isInfoEnabled())
				LOGGER.info("Begin to start agent " + this);
			dispose();
			try {
				channel = SocketChannel.open();
				channel.socket().connect(address, timeout);
				selector = Selector.open();
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ);
				readExec = Executors.newSingleThreadExecutor();
				readExec.execute(new SerialReader());
				available = true;
				if (LOGGER.isInfoEnabled())
					LOGGER.info("Finish starting agent " + this);
			} catch (Exception e) {
				dispose();
				LOGGER.error("Fail to start the agent.", e);
			}
		}
		lock.writeLock().unlock();
	}

	@Override
	public void stop() {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Try to stop agent " + this);
		lock.writeLock().lock();
		// if agent is already stopped, don't need to stop again.
		if (available) {
			if (LOGGER.isInfoEnabled())
				LOGGER.info("Begin to stop agent " + this);
			available = false;
			dispose();
			if (LOGGER.isInfoEnabled())
				LOGGER.info("Finish stopping agent " + this);
		}
		lock.writeLock().unlock();
	}

	@Override
	public boolean isAvailable() {
		lock.readLock().lock();
		boolean available = this.available;
		lock.readLock().unlock();
		return available;
	}

	private void dispose() {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Begin to dispose resouces");
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Begin to dispose selector");
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				LOGGER.error("A error occurs when close the selector", e);
			}
			selector = null;
		}
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Finish disposing selector");
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Begin to dispose channel");
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
				LOGGER.error("A error occurs when close the channel", e);
			}
			channel = null;
		}
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Finish disposing channel");
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Begin to dispose readExec");
		if (readExec != null) {
			try {
				readExec.shutdown();
				readExec.awaitTermination(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				LOGGER.error("Thread interrupts when wait reading-executors to terminate", e);
			} finally {
				if (readExec != null) {
					readExec.shutdownNow();
					readExec = null;
				}
			}
		}
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Finish disposing readExec");
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Finish disposing resouces");
	}

	@Override
	public Response query(Request request) {
		Response response = new EmptyResponse();
		if (isAvailable()) {
			String uuid = UUID.randomUUID().toString().intern();
			request.setWmi_query_uuid(uuid);
			String requestString = JSON.toJSONString(request) + "\n;";
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Request: " + requestString);
			uuidResponseMap.put(uuid, response);
			synchronized (uuid) {
				try {
					// TODO SocketChannel.write() may write only some of the bytes or possibly none at all.
					channel.write(ByteBuffer.wrap(requestString.getBytes(UTF_8)));
					uuid.wait(timeout);
				} catch (IOException | NullPointerException e) {
					LOGGER.error("A error occurs when write request", e);
					if (lock.writeLock().tryLock()) {
						available = false;
						lock.writeLock().unlock();
					} else {
						LOGGER.error("Fail to get the write lock when a writing error occurs");
					}
				} catch (InterruptedException e) {
					LOGGER.error("Thread interrupts when querying waits for the response", e);
				}
			}
			response = uuidResponseMap.remove(uuid);
		}
		return response;
	}

	private static class EmptyResponse extends Response {
		private EmptyResponse() {
			setWmi_query_err(ERR_CODE_EMPTY);
		}
	}

	@SuppressWarnings("unused")
	private static class WriteIOErrorResponse extends Response {
		private WriteIOErrorResponse() {
			setWmi_query_err(ERR_CODE_WRITEIO);
		}
	}

	@SuppressWarnings("unused")
	private static class ReadIOErrorResponse extends Response {
		private ReadIOErrorResponse() {
			setWmi_query_err(ERR_CODE_READIO);
		}
	}

	private class SerialReader implements Runnable {

		@Override
		public void run() {
			if (LOGGER.isInfoEnabled())
				LOGGER.info("SerialReader starts");
			ArrayList<Byte> bufferList = new ArrayList<Byte>(1024);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (!Thread.currentThread().isInterrupted() && isAvailable()) {
				try {
					if (selector.select() > 0) {
						Set<SelectionKey> keys = selector.selectedKeys();
						if (LOGGER.isDebugEnabled())
							LOGGER.debug("Selected keys size: " + keys.size());
						Iterator<SelectionKey> iter = keys.iterator();
						while (iter.hasNext()) {
							SelectionKey key = iter.next();
							try {
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
												String responseString = new String(bufferArray, UTF_8);
												if (LOGGER.isDebugEnabled())
													LOGGER.debug("Response: " + responseString);
												try {
													Response response = JSON.parseObject(responseString, Response.class);
													String uuid = response.getWmi_query_req().getWmi_query_uuid().intern();
													synchronized (uuid) {
														uuidResponseMap.replace(uuid, response);
														uuid.notify();
													}
												} catch (Exception e) {
													LOGGER.error("Fail to parse the response", e);
												}
												bufferList.clear();
											} else {
												bufferList.add(b);
											}
										}
										buffer.clear();
									}
								}
							} catch (CancelledKeyException e) {
								LOGGER.error("A error occurs when operate on selection key", e);
								if (lock.writeLock().tryLock()) {
									available = false;
									lock.writeLock().unlock();
								} else {
									LOGGER.error("Fail to get the write lock when a selection key error occurs");
								}
								break;
							} catch (NotYetConnectedException | IOException | NullPointerException e) {
								LOGGER.error("A error occurs when read response", e);
								if (lock.writeLock().tryLock()) {
									available = false;
									lock.writeLock().unlock();
								} else {
									LOGGER.error("Fail to get the write lock when a reading error occurs");
								}
								break;
							}
						}
						keys.clear();
					}
				} catch (IOException | ClosedSelectorException | NullPointerException e) {
					LOGGER.error("A error occurs when select keys", e);
					if (lock.writeLock().tryLock()) {
						available = false;
						lock.writeLock().unlock();
					} else {
						LOGGER.error("Fail to get the write lock when a selecting error occurs");
					}
					break;
				}
			}
			if (LOGGER.isInfoEnabled())
				LOGGER.info("SerialReader stops");
		}

	}

	public static void main(String[] args) {
		WMIAgent agent = new SerialAgent("192.168.10.16", 12345, 10000);
		Request request = new Request();
		request.setWmi_namespace("192.168.1.36");
		request.setWmi_domain_user("administrator");
		request.setWmi_password("root3306");
		request.setWmi_query_cmd("root\\cimv2::select Name from Win32_ComputerSystem");
		agent.start();
		System.out.println(agent.query(request).getWmi_query_msg());
		agent.stop();
	}
}
