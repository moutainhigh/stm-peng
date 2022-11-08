package com.mainsteam.stm.plugin.ibmmq;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;
import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

public class IBMMQCollector {

	private static final Log logger = LogFactory.getLog(IBMMQCollector.class);

	/**
	 * 通过采集通道资源返回对应的KPI值 格式为 HASHMAP (kpidesc ,VALUE) -->在采集资源模型中为 ibmmqresname
	 * kpival1 kpival2 kpival3 kpival4
	 */
	public String collectMainRes(JBrokerParameter obj) {

		StringBuffer sb = new StringBuffer();
		PCFMessageAgent agent = (PCFMessageAgent) obj.getConnection();
		// 总通道数
		sb.append("<channelCount>").append(getChannelCount(agent))
				.append("</channelCount>");
		// 队列管理器名称 --- 通过队列管理器Id,获得.
		String QMR = getQMRName(agent);
		String QMRStatus = Constants.S_UNCONNECTED;
		if (QMR != null)
			QMRStatus = Constants.S_CONNENTED;
		else
			QMR = " ";
		sb.append("<queueManagerName>").append(QMR)
				.append("</queueManagerName>");
		// 资源显示名称
		String display = obj.getIp() + "_" + obj.getPort() + "_" + QMR;
		sb.append("<resDisplayName>").append(display)
				.append("</resDisplayName>");
		// 队列管理器状态 1 代表可访问 -1 代表不可访问
		sb.append("<queueManagerStatus>").append(QMRStatus)
				.append("</queueManagerStatus>");
		// 监听器状态 1 代表可访问 -1 代表不可访问
		sb.append("<listenerStatus>")
				.append(isAppOnline(obj) == 1 ? Constants.S_NORMAL
						: Constants.S_UNNORMAL).append("</listenerStatus>");
		// QueueCount 总队列数
		sb.append("<queueCount>").append(getQueueCount(agent))
				.append("</queueCount>");
		
		//sb.append("<version>").append(this.getMQVersion(agent)).append("</version>");

		return sb.toString();

	}

	/**
	 * 通过采集通道资源返回对应的KPI值 格式为 HASHMAP (CHANNNEL_NAME+ KPIDESC ,VALUE)
	 * 
	 * @throws Exception
	 */
	public String collectChannelRes(JBrokerParameter one) {

		PCFMessageAgent agent = (PCFMessageAgent) one.getConnection();
		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL);
		// 获得所有的通道名称
		request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, "*");

		PCFMessage[] responses;
		try {
			responses = agent.send(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("PCFAgent向Websphere MQ发送命令失败", e);
			return null;
		}
		// 通道名称 通道类通道名称 通道类型 通道描述 连接名称 通道状态 传输队列 协议类型
		StringBuilder sb = new StringBuilder();
		int index = 0;
		/*if (logger.isDebugEnabled())
			logger.error("responses---size:" + responses.length);*/
		for (PCFMessage message : responses) {
			Object channelName = message
					.getParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
			String channelStatus = getChannelStatus(channelName.toString(),
					agent);
			Object channelType = message
					.getParameterValue(CMQCFC.MQIACH_CHANNEL_TYPE);
			Object desc = message.getParameterValue(CMQCFC.MQCACH_DESC);
			if (desc == null || "".equals(desc.toString().trim()))
				desc = " ";
			else
				desc = desc.toString().trim();
			Object connName = message
					.getParameterValue(CMQCFC.MQCACH_CONNECTION_NAME);
			if (connName == null || "".equals(connName.toString().trim()))
				connName = " ";
			else
				connName = connName.toString().trim();
			Object qName = message.getParameterValue(CMQCFC.MQCACH_XMIT_Q_NAME);
			if (qName == null || "".equals(qName.toString().trim()))
				qName = " ";
			Object protocol = message
					.getParameterValue(CMQCFC.MQIACH_XMIT_PROTOCOL_TYPE);
			// 组装格式为： 通道ID 通道名称 通道类型 通道描述 连接名称 通道状态 传输队列 协议类型
			sb.append(index++).append("\t")
					.append(channelName.toString().trim()).append("\t")
					.append(channelType).append("\t").append(desc).append("\t")
					.append(connName).append("\t").append(channelStatus)
					.append("\t").append(qName).append("\t").append(protocol)
					.append("\n");
		}

		return sb.toString();
	}

	/**
	 * 根据通道名称获取通道状态.
	 * 
	 * @param channelName
	 *            String
	 * @return String
	 * @throws Exception
	 *             异常
	 */
	public String getChannelStatus(final String channelName,
			PCFMessageAgent agent) {
		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL_STATUS);
		request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, channelName);
		// connect to the queue manager using the PCFMessageAgent
		String statusStr = Constants.CHANNEL_STATUS_NORMAL;
		try {
			PCFMessage[] responses = agent.send(request);
			if (responses != null && responses.length > 0) {
				for (PCFMessage message : responses) {
					int status = message
							.getIntParameterValue(CMQCFC.MQIACH_CHANNEL_STATUS);
					
					if (status == CMQCFC.MQCHS_STOPPING
							|| status == CMQCFC.MQCHS_RETRYING
							     || status == CMQCFC.MQCHS_PAUSED
							           || status == CMQCFC.MQCHS_STOPPED
							                 || status == CMQCFC.MQCHS_REQUESTING
							                       || status == CMQCFC.MQCHS_STARTING
							                             || status == CMQCFC.MQCHS_BINDING
							                                   || status == CMQCFC.MQCHS_INITIALIZING) {
						statusStr = Constants.CHANNEL_STATUS_ABNORMAL;
					}
					break;
				}

			}
		} catch (Exception e) {
			logger.warn(e);
			return statusStr;
		}

		return statusStr;
	}

	/**
	 * 通过采集 队列资源返回对应的KPI值 格式为 HASHMAP (key(queueNAME+ KPIDESC) ,VALUE)
	 * 
	 * @throws Exception
	 */
	public String collectQueueRes(JBrokerParameter one) {

		PCFMessageAgent agent = (PCFMessageAgent) one.getConnection();
		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
		// 获得所有的队列名称
		request.addParameter(CMQC.MQCA_Q_NAME, "*");
		// 队列名称 队列类型 队列描述 队列放入消息 取出消息 最大深度 最大长度 队列状态 当前深度
		int[] queueAttrs = new int[] { CMQC.MQCA_Q_NAME, CMQC.MQIA_Q_TYPE,
				CMQC.MQCA_Q_DESC, CMQC.MQIA_INHIBIT_GET, CMQC.MQIA_INHIBIT_PUT,
				CMQC.MQIA_MAX_Q_DEPTH, CMQC.MQIA_MAX_MSG_LENGTH,
				CMQC.MQIA_CURRENT_Q_DEPTH, CMQC.MQIA_DEFINITION_TYPE };
		request.addParameter(CMQCFC.MQIACF_Q_ATTRS, queueAttrs);

		PCFMessage[] responses;
		try {
			responses = agent.send(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("PCFAgent向Websphere MQ发送命令失败", e);
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if (responses != null && responses.length > 0) {
			int queueId = 0;
			for (PCFMessage message : responses) {
				/**
				 * 返回格式为 队列ID 队列名称 队列类型 队列描述 平均利用率 队列放入消息 取出消息 最大深度 最大长度 队列状态
				 * 当前深度
				 */
				String qName = "";
				try {
					qName = message.getStringParameterValue(CMQC.MQCA_Q_NAME);
					/*
					 * if (qName == null || "".equals(qName) ||
					 * qName.startsWith("AMQ.") ||
					 * qName.startsWith("MQAI.REPLY.")) { continue; }
					 */
					Object defineType = message
							.getParameterValue(CMQC.MQIA_DEFINITION_TYPE);
					// 3表示临时 1 表示预定义
					if (defineType != null && "3".equals(defineType.toString())) {
						continue;
					}
					sb.append(queueId++).append("\t").append(qName.trim())
							.append("\t");
					int type = message.getIntParameterValue(CMQC.MQIA_Q_TYPE);

					sb.append(type).append("\t");
					String desc = message
							.getStringParameterValue(CMQC.MQCA_Q_DESC);
					if (desc == null || "".equals(desc))
						desc = " ";
					sb.append(desc).append("\t");
					String queueUtil = null; // 队列平均利用率
					int currentDepth = 0;
					int maxDepth = 0;
					if (type == Constants.QUEUE_LOCAL) { // 本地队列
						currentDepth = message
								.getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH);
						maxDepth = message
								.getIntParameterValue(CMQC.MQIA_MAX_Q_DEPTH);
						queueUtil = getUtilRatio(currentDepth, maxDepth);
					} else if (type == Constants.QUEUE_MODEL) {
						maxDepth = message
								.getIntParameterValue(CMQC.MQIA_MAX_Q_DEPTH);
					}
					sb.append(queueUtil).append("\t");
					int output = message
							.getIntParameterValue(CMQC.MQIA_INHIBIT_PUT);
					sb.append(output).append("\t");
					if (type != Constants.QUEUE_REMOTE) {
						int input = message
								.getIntParameterValue(CMQC.MQIA_INHIBIT_GET);
						sb.append(input);
					} else
						sb.append(" ");
					sb.append("\t");
					if (type != Constants.QUEUE_ALIA
							&& type != Constants.QUEUE_REMOTE) {
						sb.append(maxDepth).append("\t");
						int maxLength = message
								.getIntParameterValue(CMQC.MQIA_MAX_MSG_LENGTH);
						sb.append(maxLength).append("\t");
					} else {
						sb.append(" \t \t");
					}
					sb.append(Constants.S_AVAIL).append("\t");
					if (type == Constants.QUEUE_LOCAL) {
						sb.append(currentDepth).append("\n");
					} else {
						sb.append(" \n");
					}

				} catch (PCFException e) {
					logger.warn("获取队列【" + qName + "】的属性错误");
					e.printStackTrace();
					continue;
				}
			}
		}

		return sb.toString();

	}

	/**
	 * 获取总通道数
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public int getChannelCount(PCFMessageAgent agent) {

		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL);
		// 获得所有的通道名称
		request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, "*");

		PCFMessage[] responses;
		try {
			responses = agent.send(request);
			return responses.length;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("PCFAgent向Websphere MQ发送命令失败", e);
			return 0;
		}

	}

	/**
	 * 获取总队列数
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public int getQueueCount(PCFMessageAgent agent) {

		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
		// 获得所有的队列名称
		request.addParameter(CMQC.MQCA_Q_NAME, "*");
		PCFMessage[] responses;
		try {
			responses = agent.send(request);
			int count = 0;
			for (PCFMessage message : responses) {
				// String qName =
				// message.getStringParameterValue(CMQC.MQCA_Q_NAME);
				Object defineType = message
						.getParameterValue(CMQC.MQIA_DEFINITION_TYPE);
				/*
				 * if (qName == null || "".equals(qName) ||
				 * qName.startsWith("AMQ.") || qName.startsWith("MQAI.REPLY."))
				 * { continue; }
				 */
				// 3表示临时 1 表示预定义
				if (defineType != null) {
					try {
						Integer tmp = Integer.parseInt(defineType.toString());
						if (tmp == Constants.TEMP_QUEUE_TYPE)
							continue;
					} catch (NumberFormatException e) {
						continue;
					}
				}
				count++;
			}
			return count;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("PCFAgent向Websphere MQ发送命令失败", e);
			return 0;
		}
	}

	/**
	 * 计算队列平均利用率 = 当前深度/队列最大消息长度 每个队列（queue 有一个值）
	 * 
	 * @param currentDepth
	 * @param maximumMessageLength
	 * @return
	 */
	public String getUtilRatio(int currentDepth, int maximumMessageLength) {
		if (maximumMessageLength != 0) {
			double temp = Double.valueOf(currentDepth)
					/ Double.valueOf(maximumMessageLength);
			if (temp == 0.0)
				return "0";
			else {
				NumberFormat percentFormat = NumberFormat.getPercentInstance();
				percentFormat.setMaximumFractionDigits(2);
				String result = percentFormat.format(temp);
				result = "0%".equals(result) ? "0" : result;
				return result.replaceAll("%", "");
			}
		}
		return "0";

	}

	/**
	 * 获取队列管理名称
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws MQException
	 */
	public String getQMRName(PCFMessageAgent agent) {
		// Connect a PCFAgent to the specified queue manager
		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_MGR);
		int[] attrs = new int[] { CMQC.MQCA_Q_MGR_NAME,
		// CMQCFC.MQIACF_Q_MGR_STATUS

		};
		request.addParameter(CMQCFC.MQIACF_Q_MGR_ATTRS, attrs);
		// Use the agent to send the request
		PCFMessage[] responses;
		try {
			responses = agent.send(request);
			String qmrName = "";
			for (PCFMessage message : responses) {
				qmrName = message.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME);
				break;
			}
			return qmrName.trim();

		} catch (MQException e) {
			logger.error("Websphere MQ fails in get queue attributes.", e);
		} catch (IOException e) {
			logger.error("Websphere MQ IOException.", e);
		}

		return null;
	}
	
	public String getMQVersion(PCFMessageAgent agent) {
		// Connect a PCFAgent to the specified queue manager
		PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_MGR);
		int[] attrs = new int[] { //CMQCFC.mqca,
		// CMQCFC.MQIACF_Q_MGR_STATUS

		};
		request.addParameter(CMQCFC.MQIACF_Q_MGR_ATTRS, attrs);
		// Use the agent to send the request
		PCFMessage[] responses;
		try {
			responses = agent.send(request);
			String qmrName = "";
			for (PCFMessage message : responses) {
				qmrName = message.getStringParameterValue(CMQCFC.MQIACF_Q_MGR_VERSION);
				
				break;
			}
			return qmrName.trim();

		} catch (MQException e) {
			logger.error("Websphere MQ fails in get queue attributes.", e);
		} catch (IOException e) {
			logger.error("Websphere MQ IOException.", e);
		}

		return null;
	}

	/**
	 * 可用性判断 1表示可用，0表示不可用
	 * 
	 * @param obj
	 * @return
	 */
	public static int isAppOnline(JBrokerParameter obj) {
		int flag = 1;
		Socket t_socket = new Socket();
		try {
			t_socket.connect(new InetSocketAddress(obj.getIp(), obj.getPort()), 10000);
			t_socket.close();
			t_socket = null;
		} catch (Exception t_e) {
			if (t_socket != null && !t_socket.isClosed()) {
				try {
					t_socket.close();
				} catch (Exception t_e1) {
					t_socket = null;
				}
			}
			flag = 0;
		} 

		try{
			PCFMessageAgent agent = (PCFMessageAgent)obj.getConnection();
			String qName = agent.getQManagerName();
			if(qName == null)
				flag = 0;
		}catch(Exception e) {
			if(logger.isWarnEnabled()) {
				logger.warn("IBM MQ 测试可用性失败" + e.getMessage(), e);
			}
			flag = 0;
		}
		
		return flag;
	}

}

class MyComparator2 implements Comparator<String> {

	public int compare(String o1, String o2) {

		return o2.compareTo(o1);

	}
}
