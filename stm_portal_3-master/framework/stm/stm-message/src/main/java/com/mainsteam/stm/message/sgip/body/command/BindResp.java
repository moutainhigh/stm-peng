package com.mainsteam.stm.message.sgip.body.command;

import org.apache.log4j.Logger;

import com.mainsteam.stm.message.sgip.body.SGIPCommand;
import com.mainsteam.stm.message.sgip.util.SGIPUtils;


public class BindResp extends SGIPCommand {

	private static Logger	logger	= Logger.getLogger(BindResp.class);
	
	private static final int COMMAND_LENGTH = 9;
	
	private int result;
	
	private byte[] resultByte = new byte[1];
	
	private byte[] reserve = new byte[8];

	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}

	public BindResp()
	{
		super();
		this.commandLength = COMMAND_LENGTH;
	}
	
	@Override
	public byte[] getByteData()
	{
		this.resultByte[0] = SGIPUtils.convertInt2Byte(this.result);
		return SGIPUtils.mergeBytes(this.resultByte,this.reserve);
	}
	
	@Override
	public void initPropertiesByBytes(byte[] source)
	{
		if(source != null && source.length >= COMMAND_LENGTH)
		{
			SGIPUtils.copyBytes(source, this.resultByte, 1, 1, 1);
			this.result = SGIPUtils.convertUnsignByte2Int(this.resultByte[0]);
		}else
		{
			logger.error("init bindResp properties from bytes error: the bytes not enough");
		}
	}

	@Override
	public String toString()
	{
		return "BindResp [result=" + result + "]";
	}
	
}
