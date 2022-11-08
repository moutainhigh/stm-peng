/**
 * 
 */
package com.mainsteam.stm.transfer.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * 动态加载transfer的配置信息
 * 
 * @author ziw
 * 
 *         modify by ziw at 2016年7月19日 上午8:37:06
 * 
 */
public class TransferConfigManager {

	private static final Log logger = LogFactory.getLog(TransferConfigManager.class);

	private TransferConfig[] configs;
	private TransferConfig defaultConfig = new TransferConfig();
	private long modifyFileTime = -1;
	private Thread dynamicFileMonitor;
	private boolean running = true;
	private List<TransferConfigChangeListener> listeners;

	/**
	 * 
	 */
	public TransferConfigManager() {
		initConfigArray();
		listeners = new ArrayList<>();
	}

	public void registerListener(TransferConfigChangeListener ll) {
		listeners.add(ll);
	}

	public void start() {
		File pFile = getConfigFile();
		if (pFile != null) {
			loadConfigs(pFile);
			modifyFileTime = pFile.lastModified();
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("start not found config file./properties/transfer.properties");
			}
		}
		dynamicFileMonitor = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					synchronized (this) {
						try {
							this.wait(5000);
						} catch (InterruptedException e) {
						}
					}
					File pFile = getConfigFile();
					if (pFile != null && pFile.lastModified() != modifyFileTime) {
						if (logger.isInfoEnabled()) {
							logger.info("run prop file is modified.pFile=" + pFile + " currentTime="
									+ pFile.lastModified() + " lastTime=" + modifyFileTime);
						}
						loadConfigs(pFile);
						modifyFileTime = pFile.lastModified();
						for (TransferConfigChangeListener ll : listeners) {
							try {
								ll.change();
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("change config process error.", e);
								}
							}
						}
					}
				}
			}
		});
		dynamicFileMonitor.setDaemon(true);
		dynamicFileMonitor.start();
	}

	public void stop() {
		running = false;
	}

	private File getConfigFile() {
		URL url = TransferConfigManager.class.getResource("/properties/transfer.properties");
		File pFile = null;
		if (url != null) {
			try {
				pFile = new File(URLDecoder.decode(url.getFile(), Charset.defaultCharset().name()));
			} catch (UnsupportedEncodingException e) {
				if (logger.isErrorEnabled()) {
					logger.error("getConfigFile", e);
				}
			}
		}
		return pFile;
	}

	public TransferConfig getTransferConfig(TransferDataType type) {
		return configs[type.ordinal()];
	}

	private void initConfigArray() {
		defaultConfig.setCoreThreads(1);
		defaultConfig.setMaxThreads(1);
		defaultConfig.setPersistable(false);
		defaultConfig.setTransferQueueMaxSize(5000);
		configs = new TransferConfig[TransferDataType.values().length];
		for (int i = 0; i < configs.length; i++) {
			configs[i] = defaultConfig;
		}
	}

	private void loadConfigs(File pFile) {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(pFile));
		} catch (FileNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadConfigs", e);
			}
			return;
		}
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadConfigs", e);
			}
			return;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadConfigs", e);
				}
			}
		}
		String prefix = "TransferDataType.";
		for (Entry<Object, Object> e : p.entrySet()) {
			String key = (String) e.getKey();
			String value = (String) e.getValue();
			int flag = key.indexOf(prefix);
			if (flag >= 0) {
				int flag1 = key.indexOf('.', flag + prefix.length());
				if (flag1 > 0) {
					String dataType = key.substring(flag + prefix.length(), flag1);
					TransferDataType t = TransferDataType.valueOf(dataType);
					if (dataType != null) {
						String prop = key.substring(flag1 + 1);
						if (this.configs[t.ordinal()] == defaultConfig) {
							this.configs[t.ordinal()] = defaultConfig.clone();
						}
						setConfigProp(this.configs[t.ordinal()], prop, value);
					} else {
						if (logger.isInfoEnabled()) {
							logger.info("loadConfigs invalid dataType=" + dataType);
						}
					}
				} else {
					String prop = key.substring(flag + prefix.length());
					setConfigProp(defaultConfig, prop, value);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			TransferDataType[] ts = TransferDataType.values();
			b.append("\r\n");
			for (int i = 0; i < configs.length; i++) {
				TransferConfig c = configs[i];
				String name = ts[i].name();
				b.append(name).append(".MaxThreads=").append(c.getMaxThreads()).append("\r\n");
				b.append(name).append(".CoreThreads=").append(c.getCoreThreads()).append("\r\n");
				b.append(name).append(".TransferQueueMaxSize=").append(c.getTransferQueueMaxSize()).append("\r\n");
				b.append(name).append(".persistable=").append(c.isPersistable()).append("\r\n");
			}
			logger.info("loadConfigs " + b.toString());
		}
	}

	private void setConfigProp(TransferConfig c, String prop, String value) {
		switch (prop) {
		case "MaxThreads":
			c.setMaxThreads(Integer.parseInt(value));
			break;
		case "CoreThreads":
			c.setCoreThreads(Integer.parseInt(value));
			break;
		case "TransferQueueMaxSize":
			c.setTransferQueueMaxSize(Integer.parseInt(value));
			break;
		case "persistable":
			c.setPersistable(Boolean.valueOf(value));
			break;
		default:
			if (logger.isInfoEnabled()) {
				logger.info("setConfigProp unkown prop=" + prop);
			}
			break;
		}
	}
}
