package com.mainsteam.stm.transfer.config;

public class TransferConfig {

	private int maxThreads;
	private int coreThreads;
	private int transferQueueMaxSize;
	private boolean persistable;

	public TransferConfig() {
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getCoreThreads() {
		return coreThreads;
	}

	public void setCoreThreads(int coreThreads) {
		this.coreThreads = coreThreads;
	}

	public int getTransferQueueMaxSize() {
		return transferQueueMaxSize;
	}

	public void setTransferQueueMaxSize(int transferQueueMaxSize) {
		this.transferQueueMaxSize = transferQueueMaxSize;
	}

	public boolean isPersistable() {
		return persistable;
	}

	public void setPersistable(boolean persistable) {
		this.persistable = persistable;
	}

	@Override
	public TransferConfig clone() {
		TransferConfig c = new TransferConfig();
		c.coreThreads = coreThreads;
		c.maxThreads = maxThreads;
		c.persistable = persistable;
		c.transferQueueMaxSize = transferQueueMaxSize;
		return c;
	}
}
