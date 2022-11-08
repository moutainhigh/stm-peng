package com.mainsteam.stm.web.connector;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jetty.io.bio.StreamEndPoint;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.BlockingHttpConnection;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.logic.connection.ServerConnection;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年12月7日 上午11:11:46
 * @version 1.0
 */
public class RouteableConnector extends AbstractConnector {

	private final LogicServer logicServer;
	protected final Set<ServerConnection> _connections; // 所有已经建立的连接

	public RouteableConnector(LogicServer logicServer) {
		this.logicServer = logicServer;
		this._connections = new HashSet<>(); // 创建一个hashset用于存放建立的连接
	}

	@Override
	public void close() throws IOException {
		logicServer.stopServer(LogicAppEnum.HTTP_EMBEDDED);
	}

	@Override
	public Object getConnection() {
		return logicServer;
	}

	@Override
	public int getLocalPort() {
		return logicServer.getServerHost() == null ? -1 : logicServer
				.getServerHost().getPort();
	}

	@Override
	protected void doStart() throws Exception {
		logicServer.startServer(LogicAppEnum.HTTP_EMBEDDED);
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		logicServer.stopServer(LogicAppEnum.HTTP_EMBEDDED);
		Set<ServerConnection> set = null;
		synchronized (_connections) {
			set = new HashSet<>(_connections);
		}
		Iterator<ServerConnection> iter = set.iterator();
		while (iter.hasNext()) {
			// 将所有已经建立的连接关闭
			ServerConnection connection = iter.next();
			try {
				connection.close();
			} catch (Exception e) {
			}
		}
		synchronized (_connections) {
			_connections.clear();
		}
	}

	@Override
	public void open() throws IOException {
	}

	@Override
	protected void accept(int accept) throws IOException, InterruptedException {
		ServerConnection serverConnection = logicServer
				.accept(LogicAppEnum.HTTP_EMBEDDED);
		RouteableEndPoint endPoint = new RouteableEndPoint(serverConnection);
		endPoint.dispatch();
	}

	private class RouteableEndPoint extends StreamEndPoint implements Runnable {
		private ServerConnection connection;
		private BlockingHttpConnection _connection;

		public RouteableEndPoint(ServerConnection serverConnection) {
			super(serverConnection.getInputStream(), serverConnection
					.getOutputStream());
			this.connection = serverConnection;
			this._connection = new BlockingHttpConnection(
					RouteableConnector.this, this, getServer());
		}

		public void dispatch() {
			if (getThreadPool() == null || !getThreadPool().dispatch(this)) {
				close();
			}
		}

		public void close() {
			try {
				super.close();
			} catch (IOException e) {
			}
			connectionClosed(_connection);
			this.connection.close();
			synchronized (_connections) {
				_connections.remove(_connection);
			}
		}

		@Override
		public void run() {
			connectionOpened(_connection);
			synchronized (_connections) {
				_connections.add(connection);
			}
			try {
				while (isStarted() && !isClosed()) {
					_connection.handle(); // 处理当前这个httpconnection
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connectionClosed(_connection);
				synchronized (_connections) {
					_connections.remove(_connection);
				}
			}
		}
	}

}
