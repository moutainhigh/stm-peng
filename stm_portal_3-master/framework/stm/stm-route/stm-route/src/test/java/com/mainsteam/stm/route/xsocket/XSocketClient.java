package com.mainsteam.stm.route.xsocket;

import java.io.IOException;

import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import org.xsocket.connection.multiplexed.IBlockingPipeline;
import org.xsocket.connection.multiplexed.IMultiplexedConnection;
import org.xsocket.connection.multiplexed.MultiplexedConnection;

public class XSocketClient {

	public XSocketClient() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		INonBlockingConnection nativeCon = new NonBlockingConnection("localhost", 9011);
		IMultiplexedConnection multiplexedCon = new MultiplexedConnection(nativeCon);
		System.out.println("multiplexedCon Id="+multiplexedCon.getId());

		// create a new pipeline
		String controlPipelineId = multiplexedCon.createPipeline();
		System.out.println("controlPipelineId="+controlPipelineId);
		IBlockingPipeline controlPipeline = multiplexedCon.getBlockingPipeline(controlPipelineId);
		 
		// open pipelines, send and received data, and close the pipelines
		controlPipeline.write("NEW_MESSAGE_PIPELINE\r\n");
		String messagePipelineId = controlPipeline.readStringByDelimiter("\r\n");
		System.out.println("messagePipelineId="+messagePipelineId);
		IBlockingPipeline messagePipeline = multiplexedCon.getBlockingPipeline(messagePipelineId);
		messagePipeline.write("a");
		messagePipeline.write("b");
		 
		controlPipeline.write("CLOSE_MESSAGE_PIPELINE" + messagePipelineId + "\r\n");
		messagePipeline.close();
//		multiplexedCon.close();
		System.out.println("over");
	}

}
