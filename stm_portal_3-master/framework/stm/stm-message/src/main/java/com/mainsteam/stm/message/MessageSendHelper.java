package com.mainsteam.stm.message;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.message.cmpp.util.MsgContainer;
import com.mainsteam.stm.message.sgip.client.SGIPClient;
import com.mainsteam.stm.message.smgp.SMGPClient;
import com.mainsteam.stm.message.smsmodem.SMSModemService;
import com.mainsteam.stm.message.smsmodem.SentRecord;
import com.mainsteam.stm.message.smsmodem.WaitSend;

/**
 * <li>文件名称: MessageSendHelper.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 短信发送器</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月19日
 * @author zhangjunfeng
 */
public class MessageSendHelper {
	private static Logger logger = Logger.getLogger(MessageSendHelper.class);
	public static ResultMessage sendMessage(List<String> phone, String message) {
			MsgSettingInfo setting = new MsgSettingManager().getMsgSetting();
			logger.info("初始化短信发送器");
			// 短信网关发送短信
			if (setting != null && setting.getMessageSendType() != null
					&& setting.getMessageSendType().equals("SMSGateway")) {
				if (null != setting.getMessageType()) {
					if (setting.getMessageType().equals("SGIP")) {// 联通
						logger.info("采用联通网关发送短信");
						try {
							SGIPClient.sendMsg(phone, message);
							return new ResultMessage(true);
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
					} else if (setting.getMessageType().equals("CMPP")) {// 移动
						try {
							logger.info("采用移动网关发送短信");
							return new ResultMessage(MsgContainer.sendMsg(message, phone));
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
					} else if (setting.getMessageType().equals("SMGP")) {// 电信
						try {
							logger.info("采用电信网关发送短信");
							return new ResultMessage(SMGPClient.sendSMGP(phone, message));
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
					}
				}
			} else if (setting != null && setting.getMessageSendType() != null
					&& setting.getMessageSendType().equals("SMSModem")) {// 短信猫发送短信
				logger.info("采用短信猫发送短信");
				SMSModemService service = new SMSModemService();
				HttpConnectionMsg msg =	service.requestHttpSendMessage(phone, message,null);
				if(msg.isStatus()){
					if(msg!=null && msg.getMsg()!=null && !msg.getMsg().isEmpty()){
						WaitSend sendMessage = JSONObject.parseObject(msg.getMsg(), WaitSend.class);
						return new ResultMessage(msg.isStatus(),String.valueOf(sendMessage.getTaskId()));
					}
				}else{
					throw new RuntimeException(msg.getMsg());
				}
			}
			return new ResultMessage(false);
		
	}
	
	/**
	* @Title: getSMSModemSentRecord
	* @Description: 查询短信猫短信发送状态
	* @param taskId
	* @param phoneNumber
	* @param sendDate
	* @return  SentRecord
	* @throws
	*/
	public static List<SentRecord> getSMSModemSentRecord(Long taskId,String phoneNumber,Date beginSentTime,Date endSentTime){
		SMSModemService service = new SMSModemService();
		HttpConnectionMsg msg =	service.requestHttpSentRecord(taskId, phoneNumber, beginSentTime, endSentTime);
		if(msg!=null){
			if(msg.isStatus()){
				return JSONObject.parseArray(msg.getMsg(), SentRecord.class);
			}
		}
		return null;
	}
	
	/**
	* @Title: getSMSModemSentRecord
	* @Description: 查询短信猫短信发送状态
	* @param taskId
	* @param phoneNumber
	* @param sendDate
	* @return  SentRecord
	* @throws
	*/
	public static SentRecord getSMSModemSentRecord(Long taskId,String phoneNumber){
		SMSModemService service = new SMSModemService();
		HttpConnectionMsg msg =	service.requestHttpSentRecord(taskId, phoneNumber, null, null);
		if(msg!=null){
			if(msg.isStatus()){
				List<SentRecord> sentRecords = JSONObject.parseArray(msg.getMsg(), SentRecord.class);
				if(sentRecords!=null && sentRecords.size()>0){
					return sentRecords.get(0);
				}
			}
		}
		return null;
	}
}
