package com.mainsteam.stm.message.cmpp.domain;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * <li>文件名称: MsgActiveTestResp.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 链路检查消息结构定义</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
public class MsgActiveTestResp extends MsgHead {
	private static Logger logger=Logger.getLogger(MsgActiveTestResp.class);
	private byte reserved;//
	public MsgActiveTestResp(byte[] data){
		if(data.length==8+1){
			ByteArrayInputStream bins=new ByteArrayInputStream(data);
			DataInputStream dins=new DataInputStream(bins);
			try {
				this.setTotalLength(data.length+4);
				this.setCommandId(dins.readInt());
				this.setSequenceId(dins.readInt());
				this.reserved=dins.readByte();
				dins.close();
				bins.close();
			} catch (IOException e){}
		}else{
			logger.info("链路检查,解析数据包出错，包长度不一致。长度为:"+data.length);
		}
	}
	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}
}
