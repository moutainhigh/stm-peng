/**
 * 
 */
package com.mainsteam.stm.transfer.queue;

import java.util.concurrent.LinkedBlockingQueue;

import com.mainsteam.stm.transfer.obj.InnerTransferData;

/**
 * @author ziw
 * 
 */
public interface PersistableQueue<T> {
	public LinkedBlockingQueue<InnerTransferData> getMemoryQueue();

	public void replaceMemoryQueue(
			LinkedBlockingQueue<InnerTransferData> memoryQueue);

	public void add(T simpleObj);

	public T take() throws InterruptedException;

	public int size();
}
