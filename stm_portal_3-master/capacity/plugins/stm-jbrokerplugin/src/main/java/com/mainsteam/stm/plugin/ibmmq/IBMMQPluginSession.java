package com.mainsteam.stm.plugin.ibmmq;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.pcf.PCFMessageAgent;

/**
 * IBMMQ采集插件
 * @author xiaop_000
 *
 */
public class IBMMQPluginSession extends BaseSession {
	
	private static final String IBMMQ_PASSWORD = "IBMMQPassword";
	private static final String IBMMQ_USER_ID = "IBMMQUserID";
	private static final String IBMMQ_CHANNEL = "IBMMQChannel";
	private static final String IBMMQQMGR = "IBMMQQMGR";
	private static final String IBMMQ_CHARSET = "IBMMQCharset";
	private static final String IBMMQ_PORT = "IBMMQPort";
	private static final String IP = "IP";
	private static final Log logger = LogFactory.getLog(IBMMQPluginSession.class);
	
	private static final int QM_UNAVAIL = 2059;//队列管理器不可用
	
	private PCFMessageAgent pcfAgent;
	
	private MQQueueManager queueManager;
	
	private String ip;
	
	private int port;
	/**
	 * WebSphere MQ编码字符集
	 */
	private int ccsid = 1381;
	/**
	 * 队列管理器名称
	 */
	private String qmgr;
	/**
	 * 服务器通道
	 */
	private String channel;
	/**
	 * 主机用户名，但必须对应mq的MCA的User ID
	 */
	private String mqUsername;
	/**
	 * 主机密码
	 */
	private String mqPassword;
	
	/**
	 * 重试次数，默认重连两次
	 */
	private int retryTimes = 1;
	
	/**
	 * 当前session是否可用
	 */
	private boolean isAlive;
	

	/**
	 * 创建PCFAgent连接，用于PCF查询
	 * @param currentTimes 当前重连次数
	 * @throws PluginSessionRunException 
	 */
	private synchronized void makeAgent(int currentTimes) throws PluginSessionRunException{
		try {
			/**
			 * 对应MQ V7.x及其以上版本，新增了对通道的认证机制。如果出现2035错误，最简单粗暴的办法则是，将通道验证关闭，但这会大大影响安全性
			 * 登录MQI(cd 安装目录；切换到mqm用户，执行runmqsc 队列管理器名称)，执行命令：ALTER QMGR CHLAUTH(DISABLED)
			 * 
			 * 还有一种办法则是官方推荐的办法，新增自定义的服务器类型通道（Define channel (channel1) chltype (svrconn) trptype (tcp) mcauser('mqm')）
			 * 然后设置blocking(SET CHLAUTH(channel1) TYPE(BLOCKUSER) USERLIST(ALLOWANY))
			 * 最后则可以使用channel1连接队列管理器
			 * 官方说明是不要使用系统预定义的服务器类型通道，而应该将这些没有认证的通道都禁用。
			 * Note: it is not advisable to use SYSTEM.DEF.* channels for active connections. The system default channels are the 
			 * objects from which all user-defined channels inherit properties. The recommended practice is that SYSTEM.DEF.* 
			 * and SYSTEM.AUTO.* channels should NOT be configured to be usable.
			 */
			MQEnvironment.hostname = ip;
			/**
			 * 如何查找MQ监听器端口，可通过命令：display lsstatus(*) all,查看port属性即可。一般情况都是1414，当然这是可设置的。
			 */
			MQEnvironment.port = port;
			MQEnvironment.channel = channel;
			/**
			 * 获取CCSID最可靠的办法则是，登录到MQI，然后执行display qmgr,查看CCSID属性即可。一般情况下在UTF-8环境下都是1381
			 */
			MQEnvironment.CCSID = ccsid;
			MQEnvironment.userID = mqUsername;
			MQEnvironment.password = mqPassword;
			
			queueManager = new MQQueueManager(qmgr);
			pcfAgent = new PCFMessageAgent(queueManager);
			pcfAgent.connect(queueManager);
			
		} catch (MQException e) {
			/*
			 * MQ连接不上的原因大致可以从以下方面找：
			 * 1.登录到目标机器中，运行netstat -an命令，看连接数是否太多，如果连接数太多，可能导致默认的通道异常关闭
			 * 2.登录到目标机器中，在MQ的安装目录中查看服务器日志，一般的问题都可以找到。具体查找这几个目录：
			 * MQ_install_root/errors或者MQ_install_root/qmgrs/queue_manager_name/errors。
			 * 一般都是FDC文件或者普通的log文件
			 */
			if(logger.isWarnEnabled()){
				logger.warn(e.getMessage() + ".Connects MQ error.ip/port:" + this.ip + "/" + this.port
						+", ccsid:" + this.ccsid + ",default channel :" + channel, e);
			}
			int reason = e.getReason();
			if(reason == QM_UNAVAIL){
				/*
				 * 队列管理器不可用，参考以下链接：(可能是通道关闭导致)
				 * http://www-01.ibm.com/support/knowledgecenter/SSFKSJ_8.0.0/com.ibm.mq.tro.doc/q041290_.htm
				 * 如果是队列管理器不可用，则没有重试的必要 
				 */
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_MQ_QM_UNAVAILABLE,
						"WebsphereMQ queueManager is not available. "
						+ "ip/port:" + ip + "/" + port + ",ccsid:" + ccsid + ",Queue manager name:" + channel, e);
			}
			
			this.destory();
			//重连
			if(currentTimes < retryTimes){
				makeAgent(++currentTimes);
			}else{
				if(pcfAgent == null)
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED,
						"Websphere MQ connects failed, ip/port:" + ip + "/" + port + ",ccsid:" + ccsid);
			}
		}
	}
	
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case IP:
				this.setIp(initParameters[i].getValue());
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case IBMMQ_PORT:
				try{
					this.setPort(Integer.parseInt(initParameters[i].getValue()));
					super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));
				}catch(Exception e){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"WebSphere MQ端口错误，当前值为【" + initParameters[i].getValue() + "】");
				}
				break;
			case IBMMQ_CHARSET:
				try{
					this.setCcsid(Integer.parseInt(initParameters[i].getValue()));
				}catch(Exception e){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"WebSphere MQ编码字符集错误，当前值为【" + initParameters[i].getValue() + "】");
				}
				break;
			case IBMMQQMGR:
				try{
					this.setQmgr(StringUtils.trim(initParameters[i].getValue()));
				}catch(Exception e){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"WebSphere MQ队列管理器错误，当前值为【" + initParameters[i].getValue() + "】");
				}
				break;
			case IBMMQ_CHANNEL:
				try{
					this.setChannel(StringUtils.trim(initParameters[i].getValue()));
				}catch(Exception e){
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"WebSphere MQ通道错误，当前值为【" + initParameters[i].getValue() + "】");
				}
				break;
			case IBMMQ_USER_ID:
				this.setMqUsername(StringUtils.trimToEmpty(initParameters[i].getValue()));
				break;
			case IBMMQ_PASSWORD:
				this.setMqPassword(StringUtils.trimToEmpty(initParameters[i].getValue()));
				break;
			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}
		
		this.makeAgent(0);
		this.isAlive = true;
		super.getParameter().setConnection(pcfAgent);
		
	}
	
	@Override
	public synchronized void destory() {
		if(pcfAgent != null){
			try {
				pcfAgent.disconnect();
			} catch (MQException e1) {
				pcfAgent = null;
			}
		}
		if(queueManager != null){
			try {
				queueManager.disconnect();
			} catch (MQException e) {
				if(queueManager != null && (queueManager.isConnected() || queueManager.isOpen())){
					try {
						queueManager.disconnect();
					} catch (MQException e1) {
						queueManager = null;
					}
				}
					
			}
		}
		
		isAlive = false;
	}

	@Override
	public void reload() {}

	@Override
	public boolean isAlive() {
		
		if(pcfAgent == null || queueManager == null){
			isAlive = false;
		}else{
			if(queueManager.isConnected() || queueManager.isOpen())
				isAlive = true;
			else
				isAlive = false;
		}
		return isAlive;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getCcsid() {
		return ccsid;
	}

	public void setCcsid(int ccsid) {
		this.ccsid = ccsid;
	}


	public String getQmgr() {
		return qmgr;
	}


	public void setQmgr(String qmgr) {
		this.qmgr = qmgr;
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}


	public String getMqUsername() {
		return mqUsername;
	}


	public void setMqUsername(String mqUsername) {
		this.mqUsername = mqUsername;
	}


	public String getMqPassword() {
		return mqPassword;
	}


	public void setMqPassword(String mqPassword) {
		this.mqPassword = mqPassword;
	}

}
