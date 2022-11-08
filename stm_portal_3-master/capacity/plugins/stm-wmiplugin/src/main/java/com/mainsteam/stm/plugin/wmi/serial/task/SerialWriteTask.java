package com.mainsteam.stm.plugin.wmi.serial.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SerialWriteTask implements Runnable {
	
	private final Selector selector;
	private final SocketChannel channel;
	
	public SerialWriteTask(Selector selector, SocketChannel channel) throws IOException {
		this.selector = selector;
		this.channel = channel;
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}
	
	@Override
	public void run() {

		
	}

}
