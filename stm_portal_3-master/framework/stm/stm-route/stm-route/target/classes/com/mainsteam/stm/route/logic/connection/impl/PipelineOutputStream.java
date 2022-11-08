/**
 * 
 */
package com.mainsteam.stm.route.logic.connection.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;

/**
 * @author ziw
 * 
 */
public class PipelineOutputStream extends OutputStream {

	private static final Log logger = LogFactory
			.getLog(PipelineOutputStream.class);

	private INonBlockingPipeline pipeline;

	/**
	 * 
	 */
	public PipelineOutputStream(INonBlockingPipeline pipeline) {
		this.pipeline = pipeline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		pipeline.write((byte) b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		pipeline.write(b);
//		printByte(b,0,b.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
//		ByteBuffer byteBuffer = ByteBuffer.wrap(b);
//		byteBuffer.position(off);
//		byteBuffer.limit(len);
		pipeline.write(b, off, len);
//		printByte(b,off,len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		pipeline.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		pipeline.close();
	}
	
	public boolean isClose(){
		return pipeline.isOpen();
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
}
