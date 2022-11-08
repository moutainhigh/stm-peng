package com.mainsteam.stm.dataprocess.bigData;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.kafka.producer.impl.KafkaProducerToolImpl;
import com.mainsteam.kafka.producer.inter.KafkaProducerTool;
//import com.chinawiserv.kafka.producer.impl.KafkaProducerToolImpl;
//import com.chinawiserv.kafka.producer.inter.KafkaProducerTool;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * 用于大数据的UDP发送服务接口
 * 
 * @author heshengchao
 *
 */
public class UdpSenderForBigData {

	public static final int SYNC_DATA_METRIC = 1;//性能指标
	public static final int SYNC_DATA_ALARM = 2;
	public static final int SYNC_DATA_INFO_METRIC = 4; //信息指标

	private static final Log logger = LogFactory
			.getLog(UdpSenderForBigData.class);
	private DatagramSocket datagramSocket = null;

	private Thread serverConfigRefresher;
	//1表示使用kafka接口，2表示使用socket方式发送
	private static int SEND_METHOD = 1;

	private final KafkaProducerTool kafkaProducerTool  = new KafkaProducerToolImpl();

	private volatile boolean kafakaIsRun = false;
	
	private Map<String, Object> sendConfig = Collections.synchronizedMap(new HashMap<String, Object>(3));

	private final ExecutorService executors = new ThreadPoolExecutor(5,10,60L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadFactory() {
		private int counter = 0;
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t = new Thread(runnable,"sendBigdata-"+ counter++);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	});

	static {
		InputStream resourceAsStream = UdpSenderForBigData.class.getClassLoader().getResourceAsStream("SenderSelect.properties");
		Properties properties = new Properties();
		try {
			properties.load(resourceAsStream);
			String property = properties.getProperty("itba.send.method");
			if(StringUtils.isNotBlank(property)) {
				try{
					SEND_METHOD = Integer.valueOf(property);
				}catch (Exception e){
					if(logger.isErrorEnabled()) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		} catch (IOException e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	public static int getSendMethod() {
		return SEND_METHOD;
	}

	public String getItbaSourceId(){
		return kafkaProducerTool.getSourceId();
	}

	public void init() {
		try{

			loadServerConfig();
			serverConfigRefresher = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try{
							loadServerConfig();
						}catch (Exception e) { }

						synchronized (this) {
							try {
								this.wait(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}, "serverConfigRefresher");
			serverConfigRefresher.setDaemon(true);
			serverConfigRefresher.start();

			if(allowSync()) {
				kafkaProducerTool.updateConf(getSendConfig("ip"), getSendConfig("port"), getSendConfig("datasources"));
			}
			ScheduledExecutorService kafkaTimer = Executors.newScheduledThreadPool(1, new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "kafkaTimer");
				}
			});
			kafkaTimer.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if(allowSync()){
						long start = System.currentTimeMillis();
						try {
							KafakCallable task = KafakCallable.getInstance();
							if(task.getTool() == null){
								task.setTool(kafkaProducerTool);
							}
							Future<Boolean> future = executors.submit(task);
							kafakaIsRun = future.get(600, TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							kafakaIsRun = false;
						}
						if(logger.isDebugEnabled()){
							long end = System.currentTimeMillis();
							long diff = end - start;
							logger.debug("kafka connection time: " + diff + " "+ kafakaIsRun);
						}
					}else{
						//系统没有开启传输开关，kafaka默认都不可用。
						kafakaIsRun = false;
					}
				}
			},1,5, TimeUnit.SECONDS);
		}catch (Exception e){
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage() + ", send itba init failed.", e);
			}
		}

	}


	private void loadServerConfig() {
		ISystemConfigApi bigDataApi = SpringBeanUtil.getBean(ISystemConfigApi.class);
		try {
			SystemConfigBo systemConfigById = bigDataApi.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_BIGDATA.getCfgId());
			String content = systemConfigById.getContent();
			Map<String, Object> map = JSON.parseObject(content, Map.class);
			if(map == null || map.isEmpty())
				return;
			synchronized (sendConfig) {
				if(sendConfig.isEmpty()){
					sendConfig.putAll(map);
				} else{
					if(!map.equals(sendConfig)) {
						if(logger.isInfoEnabled()) {
							logger.info("itba configuration changed :" + map);
						}
						sendConfig.putAll(map);
						if(allowSync()) {
							kafkaProducerTool.updateConf(getSendConfig("ip"), getSendConfig("port"), getSendConfig("datasources"));
						}
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadserverConfig", e);
			}
		}
	}

	public boolean allowSync() {
		try{
			Boolean integrate = (Boolean) sendConfig.get("integrate");
			return integrate;
		}catch (Exception e){
			return false;
		}
	}

	public String getSendConfig(String key) {
		synchronized (sendConfig){
			Object o = sendConfig.get(key);
			if(o == null)
				return null;
			else
				return o.toString();
		}

	}

	public void sendMsg(final String message) {
		executors.execute(new Runnable() {
			public void run() {
				try{
					if(kafakaIsRun){
						kafkaProducerTool.publishMessage(message);
					}
				}catch (Throwable e) {
					if(logger.isErrorEnabled()) {
						logger.error("kafaka send msg error:[" + message +"]", e);
					}
				}
			}
		});
	}

	public void sendMsg(byte[] msg, int type) {
		if (!allowSync()) {
			if (logger.isInfoEnabled())
				logger.info("not allow send data to bigDataServer!");
			return;
		}

		InetAddress splunkInetAddress;
		try {
			splunkInetAddress = Inet4Address.getByName(getSendConfig("ip"));
		} catch (UnknownHostException e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
			return;
		}

		// out.write(1);//版本
		// out.write(type);//消息类型
		// byte[] cont=msg.getBytes("UTF-8");
		// out.write(intToByte(cont.length));//消息长度

		ByteBuffer bbf = ByteBuffer.allocate(msg.length + 4 + 4 + 4);
		bbf.putInt(1);
		bbf.putInt(type);
		bbf.putInt(msg.length);
		bbf.put(msg);
		bbf.flip();

		byte[] sendData = bbf.array();

		DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
				splunkInetAddress, Integer.parseInt(getSendConfig("port")));

		try {
			if (datagramSocket == null || datagramSocket.isClosed()) {
				synchronized (this) {
					if (datagramSocket == null || datagramSocket.isClosed()) {
						datagramSocket = openSocket();
					}
				}
			}
			datagramSocket.send(packet); // 发送数据
		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
			datagramSocket.close();
		}

	}

	private DatagramSocket openSocket() throws SocketException {
		DatagramSocket datagramSocket = new DatagramSocket(10000);
		return datagramSocket;
	}
}
