package com.mainsteam.stm.message.cmpp.domain;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
/**
 * <li>文件名称: MsgDeliverResp.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
public class MsgDeliverResp extends MsgHead {
	private static Logger logger=Logger.getLogger(MsgDeliverResp.class);
	private long msg_Id;//信息标识（CMPP_DELIVER中的Msg_Id字段）
	private int result;//结果 0：正确 1：消息结构错 2：命令字错 3：消息序号重复 4：消息长度错 5：资费代码错 6：超过最大信息长 7：业务代码错8: 流量控制错9~ ：其他错误
	public byte[] toByteArry(){
		ByteArrayOutputStream bous=new ByteArrayOutputStream();
		DataOutputStream dous=new DataOutputStream(bous);
		try {
			dous.writeInt(this.getTotalLength());
			dous.writeInt(this.getCommandId());
			dous.writeInt(this.getSequenceId());
			dous.writeLong(this.msg_Id);
			dous.writeInt(this.result);
			dous.close();
		} catch (IOException e) {
			logger.error("封装链接二进制数组失败。");
		}
		return bous.toByteArray();
	}
	public long getMsg_Id() {
		return msg_Id;
	}

	public void setMsg_Id(long msg_Id) {
		this.msg_Id = msg_Id;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
