/**
 * 
 */
package com.mainsteam.stm.route.xsocket;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.IBlockingConnection;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;
import org.xsocket.connection.multiplexed.IMultiplexedConnection;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.IPipelineConnectHandler;
import org.xsocket.connection.multiplexed.IPipelineDataHandler;
import org.xsocket.connection.multiplexed.MultiplexedProtocolAdapter;

/**
 * @author ziw
 *
 */
public class XSocketServer {

	/**
	 * 
	 */
	public XSocketServer() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IServer server = new Server(9011, new MultiplexedProtocolAdapter(new CommandPipelineHandler()));
			ConnectionUtils.start(server);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
class CommandPipelineHandler implements IPipelineDataHandler,IPipelineConnectHandler {
	 
    public boolean onData(INonBlockingPipeline pipeline) throws IOException {
        String cmd = pipeline.readStringByDelimiter("\r\n");
        System.out.println("cmd="+cmd);
        if (cmd.equals("NEW_MESSAGE_PIPELINE")) {
            IMultiplexedConnection mplCon = pipeline.getMultiplexedConnection();
 
            String msgPipelineId = mplCon.createPipeline();
            INonBlockingPipeline msgPipeline = mplCon.getNonBlockingPipeline(msgPipelineId);
 
            // replace the CommandPipelineHandler of the new pipeline
            // by a pipeline-specific data handler
            msgPipeline.setHandler(new DataHandler(mplCon));
            pipeline.write(msgPipelineId + "\r\n");
        }
		return false; 
    }

	@Override
	public boolean onConnect(INonBlockingPipeline pipeline) throws IOException,
			BufferUnderflowException, MaxReadSizeExceededException {
		System.out.println("onConnect="+pipeline.getId());
		return false;
	}
}
 
// A pipeline handler could also be a ordinary handler like IDataHandler
class DataHandler implements IDataHandler {
	private IMultiplexedConnection mplCon;
    @SuppressWarnings("unused")
	private FileChannel fc = null;
 
    DataHandler(IMultiplexedConnection mplCon) {
    	this.mplCon= mplCon;
    }
 
 
    public boolean onData(INonBlockingConnection pipeline) throws IOException {
//        pipeline.transferTo(fc, pipeline.available());
    	System.out.println("onData "+pipeline.getId());
    	IBlockingConnection conn = mplCon.getBlockingPipeline(pipeline.getId());
    	System.out.println(pipeline.available());
    	ByteBuffer buffer = ByteBuffer.allocate(pipeline.available());
    	System.out.println("to read buffer.");
    	conn.read(buffer);
        System.out.println("data="+new String(buffer.array(),0,buffer.position()));
        conn.read(buffer);
        return true;
    }
}
