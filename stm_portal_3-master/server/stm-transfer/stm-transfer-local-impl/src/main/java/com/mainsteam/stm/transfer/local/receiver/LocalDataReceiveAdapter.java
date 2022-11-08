package com.mainsteam.stm.transfer.local.receiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.TransferDataDispatcher;
import com.mainsteam.stm.transfer.local.config.LocalConfig;
import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年6月14日 下午1:43:00
 * @version 1.0
 */
public class LocalDataReceiveAdapter implements Runnable {

	private static final Log logger = LogFactory.getLog(LocalDataReceiveAdapter.class);

	private LocalDataReceive dataReceive;
	private TransferDataDispatcher dataDispatcher;
	// private XStream xs = new XStream(new StaxDriver());
	private XStream xs = new XStream(new StaxDriver());

	public LocalDataReceiveAdapter() {
	}

	public void setDataDispatcher(TransferDataDispatcher dataDispatcher) {
		this.dataDispatcher = dataDispatcher;
	}

	public void start() throws IOException {
		LocalConfig config = LocalConfig.getSelf();
		if (StringUtils.isEmpty(config.getFtpDataDir()) || StringUtils.isEmpty(config.getLocalDataDir())) {
			if (logger.isWarnEnabled()) {
				logger.warn("start local data.not found config FtpDataDir or LocalDataDir.");
			}
			return;
		}
		this.dataReceive = new LocalDataReceive();
		new Thread(this, "LocalDataReceiveAdapter").start();
		if (logger.isInfoEnabled()) {
			logger.info("started LocalDataReceiveAdapter.");
		}
	}

	public static void main(String[] args) throws IOException {
		// try {
		// new LocalDataReceiveAdapter().start();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		String f = "F:/temp/localdata/dcs1/20170619_163734.oc";
		new LocalDataReceiveAdapter().parseAndSendData(f);
	}

	@Override
	public void run() {
		while (true) {
			String ftpDataFile = null;
			try {
				ftpDataFile = dataReceive.receiveOneFile();
				if (logger.isDebugEnabled()) {
					logger.debug("receive one data file. " + ftpDataFile);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("run", e);
				}
			}
			if (ftpDataFile == null) {
				synchronized (this) {
					try {
						this.wait(60000);
					} catch (InterruptedException e) {
					}
				}
			} else {
				try {
					parseAndSendData(ftpDataFile);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("parse and receive data error.", e);
					}
				}
			}
		}
	}

	private void unserializeAndSendData(String dataSection) {
		@SuppressWarnings("unchecked")
		List<InnerTransferData> datas = (List<InnerTransferData>) xs.fromXML(dataSection);
		if (datas != null && datas.size() > 0) {
			InnerTransferData[] arrs = new InnerTransferData[datas.size()];
			if (this.dataDispatcher != null) {
				this.dataDispatcher.dispatch(datas.toArray(arrs));
			}
		}
	}

	private void parseAndSendData(String fileName) throws IOException {
		File f = new File(fileName);
		String data_split = "<?xml version=\"1.0\" ?>";
		char[] buffer = new char[1024 * 1024];// buffer 1MB
		StringBuilder b = new StringBuilder();
		int start_index = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"))) {
			int offset = 0;
			int arrayLength = buffer.length;
			int length = reader.read(buffer, offset, arrayLength);
			while (length > 0) {
				String s = new String(buffer, offset, length);
				b.append(s);
				int nextIndex = b.indexOf(data_split, start_index);
				while (nextIndex >= 0) {
					if (nextIndex > start_index) {
						String dataSection = b.substring(start_index, nextIndex);
						unserializeAndSendData(dataSection);
						start_index = nextIndex;
					}
					nextIndex = b.indexOf(data_split, start_index + data_split.length());
				}
				if (start_index > 0) {
					b = new StringBuilder(b.substring(start_index));
					start_index = 0;
				}
				if (length < arrayLength) {
					offset += length;
					arrayLength = buffer.length - offset;
				} else {
					offset = 0;
					arrayLength = buffer.length;
				}
				length = reader.read(buffer, offset, arrayLength);
			}
		}
		if (b.length() > 0) {
			unserializeAndSendData(b.toString());
		}
	}
}
