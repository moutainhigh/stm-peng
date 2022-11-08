package com.mainsteam.stm.plugin.wmi.deprecated;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 负责与wmiagent通信的类
 */
public class TransportContactor implements Runnable {

	/**
	 * TCP/IP通信通道
	 */
	private SocketChannel channel;

	/**
	 * 选择器
	 */
	private Selector selector;

	/**
	 * 读取的字节换
	 */
	private ByteBuffer readbuffer = ByteBuffer.allocate(1024);

	/**
	 * 连接的IP地址
	 */
	private String ipaddress;

	/**
	 * 连接的端口
	 */
	private int port;
	
	/**
	 * 负责解析返回数据
	 */
	private TransportHandle handle;
	
	/**
	 * 该通信线程是否是就绪的，即是否可以发送接收数据
	 */
	private volatile boolean isReady;

	/**
	 * 构造器，
	 * @param	ipaddress	WMI AGENT的ip地址
	 * @param	port		WMI AGENT的登录端口
	 * @param	handle		收到返回数据时的处理程序
	 * @roseuid 5350985301CB
	 */
	public TransportContactor(String ipaddress, int port, TransportHandle handle) {
		this.ipaddress = ipaddress;
		this.port = port;
		this.handle = handle;
	}
	
	/**
	 * 连接到服务器
	 * @param timeout	超时时间（毫秒）
	 * @return			true连接成功，false连接失败
	 * @throws Exception	当网络通信错误时，抛出异常
	 */
	public boolean connect(long timeout) throws Exception {
		boolean success = false;
		//只是打开了通道，底层尚未建立Socket链接，如此时进行读写操作则会抛出异常
		channel = SocketChannel.open();
		//非阻塞模式
		channel.configureBlocking(false);
		//通过Selecter可以一个线程管理多个通道，但通道必须切换成非阻塞模式（上一行代码）
		selector = Selector.open();
		//要通过Selector管理通道，需要先注册
		channel.register(selector, SelectionKey.OP_CONNECT);
		channel.connect(new InetSocketAddress(this.ipaddress, this.port));
		Set<SelectionKey> keys = null;
		//一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了。timeout表示阻塞时间
		if(selector.select(timeout) > 0){
			//访问“已选择键集（selected key set）”中的就绪通道
			keys = selector.selectedKeys();
		}
		//循环遍历已选择键集中的每个键，并检测各个键所对应的通道的就绪事件
		if(keys != null && keys.size() > 0){
			Iterator<SelectionKey> iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = iter.next();
				if(key.isConnectable() || key.isReadable()){
					try{
						channel.finishConnect();
					}catch(Exception e){
						disConnection();
						throw e;
					}
					channel.register(selector, SelectionKey.OP_READ);
					this.isReady = true;
					success = true;
					/*
					 * Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。
						下次该通道变成就绪时，Selector会再次将其放入已选择键集中。
					 */
					iter.remove();
				}
			}
		}		
		if(success){//如果连接成功，则启动线程
			new Thread(this).start();
		}else{//如果连接失败，则关闭所有
			disConnection();
		}
		return success;
	}
	
	/**
	 * 断开连接
	 */
	public void disConnection(){		
		if(this.selector != null){
			try {
				this.selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(this.channel != null){
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		this.isReady = false;		
	}
	
	@Override
	public void run() {
		try{						
			Set<SelectionKey> keys = null;			
			List<Byte> oneDataBuffer = new ArrayList<Byte>();			
			do {				
				if(selector.select() > 0){
					keys = selector.selectedKeys();
				}
				
				if(keys != null && keys.size() > 0){
					Iterator<SelectionKey> iter = keys.iterator();
					while(iter.hasNext()){
						SelectionKey key = iter.next();
						
						if(key.isReadable()){
							while(channel.read(readbuffer) > 0){
								readbuffer.flip();
								//逐个字节的读取,知道读取到结束符后，则解析一条数据
								while(readbuffer.hasRemaining()){
									byte oneByte = readbuffer.get();									
									//判断是否为结束符
									if(oneByte == RequestMessage.END_CHAR && oneDataBuffer.get(oneDataBuffer.size()-1) == '\n'){
										byte[] temp = new byte[oneDataBuffer.size()];
										for(int i = 0; i < temp.length; i++){
											temp[i] = oneDataBuffer.get(i);
											
										}
										String message = new String(temp,"UTF-8");
//										System.out.println(message);
										try{
											this.handle.operation(message);
										}catch(Exception e){
											throw new RuntimeException("-----解析错误--------", e);
										}
										oneDataBuffer.clear();
									}else{
										oneDataBuffer.add(oneByte);
									}
								}
								readbuffer.clear();
							}						
						}
						iter.remove();
					}
				}	
			}while(true && this.isReady);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.isReady = false;
			if(this.selector != null){
				try {
					this.selector.close();
				} catch (IOException e) {}
			}
			if(this.channel != null){
				try {
					this.channel.close();
				} catch (IOException e) {}
			}
			this.readbuffer.clear();
		}
		
	}

	/**
	 * 发送命令
	 * 
	 * @param message
	 *            - 发送到服务端的命令
	 * @roseuid 5350C31600F0
	 */
	public void sendMessage(String message) {
		if(message == null){
			return;
		}
		message = message + "\n;";
		if(this.isReady && this.channel != null){
			try {
				this.channel.write(ByteBuffer.wrap(message.getBytes()));
			} catch (IOException e) {				
				e.printStackTrace();
				throw new RuntimeException(e);				
			}
		}else{
			throw new NullPointerException("isReady=false or channel is null!");
		}
	}
}
