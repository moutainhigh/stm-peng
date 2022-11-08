package com.mainsteam.stm.route.logic.connection.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.IPipelineDisconnectHandler;

import com.mainsteam.stm.route.logic.LogicConnection;

public class PipelineInputStream extends InputStream implements IDataHandler,
		IPipelineDisconnectHandler {

	private static final Log logger = LogFactory
			.getLog(PipelineInputStream.class);

	private PipedInputStream inputStream;
	private PipedOutputStream outputStream;
	private LogicConnection connection;
	private boolean closed;

	public PipelineInputStream() throws IOException {
		outputStream = new PipedOutputStream();
		inputStream = new PipedInputStream();
		try {
			inputStream.connect(outputStream);
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * @return the connection
	 */
	public final LogicConnection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public final void setConnection(LogicConnection connection) {
		this.connection = connection;
	}

	/**
	 * @return the closed
	 * @throws IOException
	 */
	public final boolean isClosed() {
		try {
			return closed && available() <= 0;
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("isClosed", e);
			}
		}
		return false;
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int length = inputStream.read(b);
		// printByte(b);
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int length = inputStream.read(b, off, len);
		// printByte(b, off, length);
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return inputStream == null ? -1 : inputStream.available();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		if (inputStream != null) {
			inputStream.close();
		}
		if (outputStream != null) {
			outputStream.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public boolean onData(INonBlockingConnection conn) throws IOException,
			BufferUnderflowException, ClosedChannelException,
			MaxReadSizeExceededException {
		int avail = conn.available();
		byte[] bytes = conn.readBytesByLength(avail);
		// if (logger.isDebugEnabled()) {
		// logger.debug("onData avail="+avail+" bytes.length="+bytes.length);
		// }
		outputStream.write(bytes);
		// printByte(bytes);
		return true;
	}

	@SuppressWarnings("unused")
	private void printByte(byte[] bytes) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			b.append(Byte.toString(bytes[i])).append(' ');
		}
		if (logger.isDebugEnabled()) {
			logger.error("read " + b);
		}
	}

	@SuppressWarnings("unused")
	private void printByte(byte[] bytes, int offset, int length) {
		StringBuilder b = new StringBuilder();
		for (int i = offset; i < offset + length; i++) {
			b.append(Byte.toString(bytes[i])).append(' ');
		}
		if (logger.isDebugEnabled()) {
			logger.debug("out " + b);
		}
	}

	@Override
	public boolean onDisconnect(INonBlockingPipeline pipeline)
			throws IOException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("onDisconnect pipeline is disconnect,close the inputstream.");
			b.append(pipeline.getRemoteAddress());
			b.append(':');
			b.append(pipeline.getRemotePort());
			b.append(" connId=").append(pipeline.getId());
			logger.info(b.toString());
		}
		closed = true;
		close();
		if (connection != null) {
			connection.close();
		}
		return true;
	}
}
