package com.mainsteam.stm.transfer.local.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年6月13日 下午5:17:20
 * @version 1.0
 */
public class LocalConfig {
	public static final String DATA_FILE_FORMAT = "^\\d{8}_\\d{6}\\.oc\\.xml$";// HHmmss
	public static final String FILE_NAME_FORAMT = "{0,date,yyyyMMdd_HHmmss}.oc.xml";
	private static final Log logger = LogFactory.getLog(LocalConfig.class);
	private static final String FTP_CONFIG_FILE = "/ftp_config.properties";
	private static final String FTP_DOWN_DIR = "ftp.data.dir";
	private static final String LOCAL_DATA_DIR = "local.data.dir";
	private static final String LOCAL_DATA_TIMEOUT = "local.data.timeout";
	private static final LocalConfig _self = new LocalConfig();
	private String ftpDataDir;
	private String localDataDir;
	private long localDataTimeout = 24 * 60 * 60 * 1000;

	private LocalConfig() {
		loadCfg();
	}

	public static LocalConfig getSelf() {
		return _self;
	}

	public void loadCfg() {
		InputStream in = LocalConfig.class.getResourceAsStream(FTP_CONFIG_FILE);
		if (in != null) {
			Properties p = new Properties();
			try {
				p.load(in);
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("freshConfig", e);
				}
				return;
			}finally{
				try {
					in.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("loadCfg", e);
					}
				}
			}
			this.ftpDataDir = p.getProperty(FTP_DOWN_DIR);
			this.localDataDir = p.getProperty(LOCAL_DATA_DIR);
			if (NumberUtils.isNumber(p.getProperty(LOCAL_DATA_TIMEOUT))) {
				long timeout = NumberUtils.toLong(p.getProperty(LOCAL_DATA_TIMEOUT));
				if (timeout > 0) {
					this.localDataTimeout = timeout;
				}
			}
		}
	}

	public String getFtpDataDir() {
		return ftpDataDir;
	}

	public String getLocalDataDir() {
		return localDataDir;
	}

	public long getLocalDataTimeout() {
		return localDataTimeout;
	}
}
