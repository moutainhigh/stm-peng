/**
 * 
 */
package com.mainsteam.stm.transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.mainsteam.stm.util.SpringBeanUtil;
import com.github.xsonorg.XSON;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年6月14日 下午12:13:10
 * @version 1.0
 */
/** 
 */
public class CommonDataSender implements DataSender {

	private static final Log logger = LogFactory.getLog(CommonDataSender.class);

	private LocaleNodeService localNodeService;

	private LogicClient logicClient;

	private LogicConnection tcpConnection;

	private NodeGroup funcGroup;

	/**
	 * 
	 */
	public CommonDataSender() {
	}
	
	private void initDependSerivce(){
		if(localNodeService == null){
			this.localNodeService = SpringBeanUtil.getBean(LocaleNodeService.class);
			this.logicClient =  SpringBeanUtil.getBean(LogicClient.class);
		}
	}

	private Node getParentNode() throws IOException {
		Node parentNode = null;
		if (funcGroup == null) {
			initDependSerivce();
			if(localNodeService == null){
				return null;
			}
			NodeGroup selectGroup = null;
			try {
				NodeGroup group = localNodeService.getCurrentNodeGroup();
				selectGroup = group.getPre();
				while (selectGroup != null && selectGroup != group) {
					if (selectGroup.getFunc() == NodeFunc.processer) {
						funcGroup = selectGroup;
						break;
					}
				}
			} catch (NodeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("getParentNode", e);
				}
				throw new IOException(e);
			}
		}
		if (funcGroup != null) {
			parentNode = funcGroup.selectNode();
		}
		return parentNode;
	}

	private void createConnection() throws IOException {
		Node parentNode = getParentNode();
		if (parentNode == null) {
			throw new IOException("Cannt found parent node.");
		}
		tcpConnection = this.logicClient.createConection(parentNode.getIp(), parentNode.getPort(),
				LogicAppEnum.TRANSFER_TCP);
		if (logger.isInfoEnabled()) {
			logger.info("createConnection ready send data to " + parentNode);
		}
	}

	private byte[] writeLength(int length) {
		byte[] lengths = new byte[4];
		lengths[0] = int0(length);
		lengths[1] = int1(length);
		lengths[2] = int2(length);
		lengths[3] = int3(length);
		return lengths;
	}

	private static byte int3(int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(int x) {
		return (byte) (x);
	}

	@Override
	public int sendData(List<InnerTransferData> datas) throws IOException {
		InnerTransferData[] v = new InnerTransferData[datas.size()];
		datas.toArray(v);
		byte[] data = XSON.write(v);
		int length = data.length;
		OutputStream out = tcpConnection.getOutputStream();
		try {
			out.write(writeLength(length));
			out.write(data);
			out.flush();
			return datas.size();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("doSenderror", e);
			}
			// blockAndCreateConnection();
			throw e;
		}
	}

	private boolean isConnectionValid() {
		return tcpConnection != null && tcpConnection.isValid();
	}

	@Override
	public void init() throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("init common data sender.");
		}
		createConnection();
	}

	@Override
	public boolean isValid() throws IOException {
		return isConnectionValid();
	}

	@Override
	public void close() throws IOException {
		if (tcpConnection != null) {
			tcpConnection.close();
			if (logger.isInfoEnabled()) {
				logger.info("closeInvalidConnection.");
			}
		}
	}
}
