/**
 * 
 */
package com.mainsteam.stm.route;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.remote.generic.MessageConnection;
import javax.management.remote.message.Message;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.sun.jmx.remote.generic.DefaultConfig;

/**
 * 可路由的连接
 * 
 * @author ziw
 * 
 */
public class RouteableJmxConnection implements MessageConnection {
	private static final int UNCONNECTED = 1;
	private static final int CONNECTING = 2;
	private static final int CONNECTED = 4;
	private static final int FAILED = 8;
	private static final int TERMINATED = 16;

	private static final Log logger = LogFactory
			.getLog(RouteableJmxConnection.class);

	private Subject subject;
	private InputStream in;
	private ObjectInputStream oin;
	private OutputStream out;
	private ObjectOutputStream oout;
	private boolean replaceInputStreamFlag = false;
	private boolean replaceOutputStreamFlag = false;
	private String addr;
	private int port;
	private ClassLoader defaultClassLoader;

	private int state = 1;
	private int[] stateLock = new int[0];

	private long waitConnectedState = 1000L;

	private LogicConnection connection;

	private LogicClient client;

	private LogicAppEnum appEnum;

	@SuppressWarnings("rawtypes")
	private Map paramMap;
	
	private int bufferSize = 1024*512;//512K Byte size buffer.
	
	public RouteableJmxConnection(String paramString, int paramInt,
			LogicClient client, LogicAppEnum appEnum) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Creating with a socket address: " + paramString + " "
					+ paramInt);
		}

		this.addr = paramString;
		this.port = paramInt;
		this.client = client;
		this.appEnum = appEnum;
	}

	public RouteableJmxConnection(LogicConnection connection) {
		this.connection = connection;
		this.appEnum = connection.getConnectionApp();
	}
	
	
	public boolean isAlive(){
		return this.connection == null ? false:this.connection.isValid();
	}

	/**
	 * @return the appEnum
	 */
	public final LogicAppEnum getAppEnum() {
		return appEnum;
	}

	private void createConnection() throws IOException {
		this.connection = this.client.createConection(this.addr, this.port,
				LogicAppEnum.RPC_JMX_NODE_GROUP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.remote.generic.MessageConnection#connect(java.util.Map)
	 */
	@Override
	public synchronized void connect(@SuppressWarnings("rawtypes") Map paramMap)
			throws IOException {
		this.waitConnectedState = DefaultConfig
				.getTimeoutForWaitConnectedState(paramMap);
		this.paramMap = paramMap;
		synchronized (this.stateLock) {
			if (this.state == UNCONNECTED) {
				if (logger.isTraceEnabled()) {
					logger.trace("connect First time to connect to the server.");
				}

				this.state = CONNECTING;
				this.stateLock.notifyAll();

				if (this.connection == null) {
					createConnection();
				}

				replaceStreams(this.connection.getInputStream(),
						this.connection.getOutputStream());

				if (paramMap != null) {
					this.defaultClassLoader = ((ClassLoader) paramMap
							.get("jmx.remote.default.class.loader"));
				}

				this.state = CONNECTED;
				this.stateLock.notifyAll();
			} else if ((this.state == FAILED) || (this.state == CONNECTED)) {
				if (logger.isTraceEnabled()) {
					logger.trace("connect Try to re-connect to the server.");
				}

				if (this.state == CONNECTED) {
					this.state = FAILED;
					this.stateLock.notifyAll();
					this.connection.close();
				}

				this.state = CONNECTING;
				this.stateLock.notifyAll();

				createConnection();
				replaceStreams(this.connection.getInputStream(),
						this.connection.getOutputStream());

				this.state = CONNECTED;
				this.stateLock.notifyAll();
			} else {
				if (this.state == TERMINATED) {
					throw new IllegalStateException(
							"The connection has been closed.");
				}
				if (logger.isTraceEnabled()) {
					logger.trace("connect Waiting the state changing.");
				}

				checkState();
			}
		}
	}
	
	public void resetState(){
		this.state = UNCONNECTED;
	}

	public void reconnection() throws IOException {
		connect(this.paramMap);
	}

	private void replaceStreams(InputStream paramInputStream,
			OutputStream paramOutputStream) throws IOException {
		this.in = paramInputStream;
		this.out = paramOutputStream;
		this.replaceInputStreamFlag = true;
		this.replaceOutputStreamFlag = true;
	}

	public void setSubject(Subject paramSubject) {
		this.subject = paramSubject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.remote.generic.MessageConnection#readMessage()
	 */
	public Message readMessage() throws IOException, ClassNotFoundException {
		checkState();

		if (logger.isDebugEnabled()) {
			logger.debug("readMessage Read a message ...");
		}
		if (this.replaceInputStreamFlag) {
			synchronized (this) {
				if (this.replaceInputStreamFlag) {
					if ((this.in instanceof BufferedInputStream))
						this.oin = new ObjectInputStreamWithLoader(this.in,
								this.defaultClassLoader);
					else {
						this.oin = new ObjectInputStreamWithLoader(
								new BufferedInputStream(this.in,bufferSize),
								this.defaultClassLoader);
					}

					this.replaceInputStreamFlag = false;
				}
			}
		}
		return (Message) this.oin.readObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.remote.generic.MessageConnection#writeMessage(javax.
	 * management.remote.message.Message)
	 */
	@Override
	public void writeMessage(Message paramMessage) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("writeMessage Write a message ...");
		}

		checkState();

		if (this.replaceOutputStreamFlag) {
			if ((this.out instanceof BufferedOutputStream))
				this.oout = new ObjectOutputStream(this.out);
			else {
				this.oout = new ObjectOutputStream(new BufferedOutputStream(
						this.out,bufferSize));
			}
			this.replaceOutputStreamFlag = false;
		}
		// setRouteHeaderOperate(paramMessage);
		if (logger.isDebugEnabled()) {
			logger.debug("writeMessage request set is "
					+ paramMessage.getClass());
		}
		this.oout.writeObject(paramMessage);
		this.oout.flush();
		this.oout.reset();
	}

	// /** 设置头信息
	// * @param paramMessage
	// */
	// private void setRouteHeaderOperate(Message paramMessage) {
	// RouteIOContext.getInstance().setDispIp(this.connection.getDistIp());
	// RouteIOContext.getInstance().setDispPort(this.connection.getDistPort());
	// if ((paramMessage instanceof CloseMessage
	// || paramMessage instanceof NotificationRequestMessage
	// || paramMessage instanceof MBeanServerRequestMessage
	// || paramMessage instanceof HandshakeBeginMessage)) {
	// RouteIOContext.getInstance().setOperation(OperationEnum.RPC_REQUEST);
	// } else if (paramMessage instanceof NotificationResponseMessage
	// || paramMessage instanceof MBeanServerResponseMessage
	// || paramMessage instanceof HandshakeEndMessage) {
	// RouteIOContext.getInstance().setOperation(OperationEnum.RPC_RESPONSE);
	// } else {
	// if (logger.isErrorEnabled()) {
	// logger.error("setRouteHeaderOperate unexcept msg " + paramMessage);
	// }
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.remote.generic.MessageConnection#close()
	 */
	@Override
	public void close() {
		if (logger.isInfoEnabled()) {
			logger.info("close Close the socket connection.");
		}

		synchronized (this.stateLock) {
			if (this.state == 16) {
				return;
			}

			this.state = 16;

			this.connection.close();
			this.connection = null;
			this.stateLock.notify();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.remote.generic.MessageConnection#getConnectionId()
	 */
	@Override
	public String getConnectionId() {
		if (this.connection == null) {
			return "Uninitialized connection id";
		}
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("jmxmp://" + this.addr + ":" + this.port + " ");
		if (this.subject != null) {
			Set<Principal> localSet = this.subject.getPrincipals();
			String str1 = "";
			for (Iterator<Principal> localIterator = localSet.iterator(); localIterator
					.hasNext();) {
				Principal localPrincipal = localIterator.next();
				String str2 = localPrincipal.getName().replace(' ', '_')
						.replace(';', ':');
				localStringBuffer.append(str1).append(str2);
				str1 = ";";
			}
		}
		localStringBuffer.append(" ").append(System.identityHashCode(this));
		return localStringBuffer.toString();
	}

	public void checkState() throws IllegalStateException {
		synchronized (this.stateLock) {
			if (this.state == CONNECTED)
				return;
			if (this.state == TERMINATED) {
				throw new IllegalStateException(
						"The connection has been closed.");
			}

			long l1 = this.waitConnectedState;
			long l2 = System.currentTimeMillis() + l1;

			while ((this.state != CONNECTED) && (this.state != TERMINATED)
					&& (l1 > 0L)) {
				try {
					this.stateLock.wait(l1);
				} catch (InterruptedException localInterruptedException) {
					break;
				}

				l1 = l2 - System.currentTimeMillis();
			}

			if (this.state == CONNECTED) {
				return;
			}
			throw new IllegalStateException(
					"The connection is not currently established.");
		}
	}

	private static class ObjectInputStreamWithLoader extends ObjectInputStream {
		private final ClassLoader cloader;

		public ObjectInputStreamWithLoader(InputStream paramInputStream,
				ClassLoader paramClassLoader) throws IOException {
			super(paramInputStream);
			this.cloader = paramClassLoader;
		}

		protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
				throws IOException, ClassNotFoundException {
			return this.cloader == null ? super
					.resolveClass(paramObjectStreamClass) : Class.forName(
					paramObjectStreamClass.getName(), false, this.cloader);
		}
	}
}
