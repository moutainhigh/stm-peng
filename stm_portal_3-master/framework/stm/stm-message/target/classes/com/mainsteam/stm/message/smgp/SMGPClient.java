package com.mainsteam.stm.message.smgp;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mainsteam.stm.message.MsgSettingInfo;
import com.mainsteam.stm.message.MsgSettingManager;

import cn.com.zjtelecom.smgp.Client;
import cn.com.zjtelecom.smgp.bean.Result;
import cn.com.zjtelecom.smgp.bean.Submit;

public class SMGPClient {

	private static Logger logger = Logger.getLogger(SMGPClient.class);
	/**
	 * 中国电信SMGP协议发送短信
	 * 
	 * @param sms
	 * @return
	 */
	public static Boolean sendSMGP(List<String> mobile, String message) {
		
		MsgSettingInfo setting = new MsgSettingManager().getMsgSetting();
		logger.info("SMGP准备发送短信！");
		logger.info("开始获取SMGP配置");
		// 获取配置参数
		String host = setting.getMessageGatewayIp();
		int port = Integer.parseInt(setting.getMessagePort());
		String account = setting.getMessageLoginAccount();
		String passwd = setting.getMessagePassword();
		String spid = setting.getSpCode();
		// String productid=EventConfigVar.configVar.cmppIpAddr;
		// 初始化client
		Client client = new Client(host, port, 2, account, passwd, spid, 0);
		// 设置submit
		Submit submit = new Submit();
		// submit.setSrcTermid("这里填写要下发短信的接入号");
		try {
			// 发送消息:
			submit.setMsgContent(message.getBytes("iso-10646-ucs-2"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		submit.setMsgType(0);
		submit.setNeedReport(0);
		submit.setPriority(0);
		submit.setServiceID(spid);
		submit.setMsgFormat(8);
		// if (productid!=null){
		// submit.setProductID(productid);
		// }

		logger.info("SMGP开始发送短信");
		for (String phone : mobile) {
			logger.info("短信发送到：" + phone);
			// 目标号码：
			submit.setDestTermid(phone);
			// 发送短信
			Result result = client.Send(submit);
			System.out.println(result.toString());
			logger.info("Status:" + result.ErrorCode);
			logger.info("MsgID:" + result.ErrorDescription);
			if(result.ErrorDescription.contains("Can not creat socket")){
				throw new RuntimeException("发送短信Socket链接短信网关失败："+result.ErrorDescription);
			}else {
				throw new RuntimeException("短信发送状态："+result.ErrorCode+";"+result.ErrorDescription);
			}
		}
		logger.info("SMGP开始发送完成");
		// 退出
		if (null != client)
			client.Close();
		return true;
	}

}
