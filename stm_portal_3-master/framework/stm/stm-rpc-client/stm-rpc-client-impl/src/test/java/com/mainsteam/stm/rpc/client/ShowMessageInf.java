package com.mainsteam.stm.rpc.client;

import com.mainsteam.stm.rpc.server.OCRPCServer;


class ShowMessageInf implements ShowMessageInfMBean {
	
	private OCRPCServer ocrpcServer;
	
	public void setOcrpcServer(OCRPCServer ocrpcServer) {
		this.ocrpcServer = ocrpcServer;
	}

	public void start(){
		ocrpcServer.registerService(this, ShowMessageInfMBean.class);
	}
	
	@Override
	public String spellAndShow(String... array) {
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (String s : array) {
			b.append(s).append(',');
		}
		b.deleteCharAt(b.length() - 1);
		b.append(']');
		System.out.println("Server print:" + b);
		return b.toString();
	}

}
