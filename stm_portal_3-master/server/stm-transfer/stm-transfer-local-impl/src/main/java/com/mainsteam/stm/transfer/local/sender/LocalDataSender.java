package com.mainsteam.stm.transfer.local.sender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.local.config.LocalConfig;
import com.mainsteam.stm.transfer.local.maintain.LocalFileCleaner;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年6月13日 下午5:22:52
 * @version 1.0
 */
public class LocalDataSender {

	private static final Log logger = LogFactory.getLog(LocalDataSender.class);

	private volatile BufferedWriter writer;
	private MessageFormat fileNameFormater = new MessageFormat(LocalConfig.FILE_NAME_FORAMT);
	private static final long SEND_INTERVAL = 60000;// 1 minute
	private LocalConfig config = LocalConfig.getSelf();
	private Date[] formatParameter = new Date[1];
	private File ftpDataDir;
	private File currentFile;
	private File switcherFile;
	private Runnable dataSendRunner;
	private LocalFileCleaner localFileCleaner;
	private volatile boolean isSendData = false;

	public LocalDataSender() throws IOException {
		init();
	}

	private void clearBuffer() throws IOException {
		FileUtils.writeByteArrayToFile(currentFile, empty_bytes);
	}

	private void init() throws IOException {
		File parent = new File(config.getLocalDataDir());
		ftpDataDir = new File(config.getFtpDataDir());
		currentFile = new File(parent, "step1.data");
		switcherFile = new File(parent, "step2.data");
		if (!currentFile.exists()) {
			currentFile.getParentFile().mkdirs();
			currentFile.createNewFile();
		}
		if (!switcherFile.exists()) {
			switcherFile.createNewFile();
		}
		clearBuffer();
		dataSendRunner = new Runnable() {
			@Override
			public void run() {
				while (true) {
					synchronized (this) {
						try {
							this.wait(SEND_INTERVAL);
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("run", e);
							}
						}
					}
					try {
						sendData0();
					} catch (IOException e) {
						if (logger.isErrorEnabled()) {
							logger.error("sendData error.", e);
						}
					}
				}
			}
		};
		localFileCleaner = new LocalFileCleaner(ftpDataDir.getAbsolutePath(), config.getLocalDataTimeout());
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFile), "utf-8"));
		new Thread(dataSendRunner, "dataSendRunner").start();
		new Thread(localFileCleaner, "localFileCleaner").start();
	}

	private BufferedWriter takeWriter() {
		return writer;
	}

	public void sendData(String content) throws IOException {
		takeWriter().write(content);
		isSendData = true;
	}

	private static final byte[] empty_bytes = new byte[0];

	private void sendData0() throws IOException {
		writer.flush();
		if (!isSendData || FileUtils.sizeOf(currentFile) <= 10) {
			return;
		}
		FileUtils.writeByteArrayToFile(switcherFile, empty_bytes);

		BufferedWriter toCloseWriter = writer;
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(switcherFile), "utf-8"));
		try {
			toCloseWriter.flush();
			toCloseWriter.close();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("close last writer");
			}
		}

		File tosendfile = currentFile;
		currentFile = switcherFile;
		switcherFile = tosendfile;

		formatParameter[0] = new Date();
		File distFile = new File(ftpDataDir, fileNameFormater.format(formatParameter));
		FileUtils.copyFile(tosendfile, distFile);
		isSendData = false;
		if (logger.isDebugEnabled()) {
			logger.debug("sendData from " + tosendfile + " to " + distFile);
		}
	}
}
