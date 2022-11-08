package com.mainsteam.stm.webService.sms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.message.HttpConnectionMsg;
import com.mainsteam.stm.message.smsmodem.SMSModemService;
import com.mainsteam.stm.webService.obj.Result;
import com.mainsteam.stm.webService.obj.ResultCodeEnum;

@WebService
public class SmsWebServiceImpl implements SmsWebService{
	private static final Logger logger=LoggerFactory.getLogger(SmsWebServiceImpl.class);

	
	@Override
	public SmsResult sendMessage(SmsBean smsBean) {

		SmsResult result = new SmsResult();
		if (null!=smsBean.getDestNumber() && null!=smsBean.getContent() && !"".equals(smsBean.getDestNumber())	&& !"".equals(smsBean.getContent())	) {
			
			String[] phoneNum = smsBean.getDestNumber().split(",");
			List<String> phoneList = new ArrayList<String>();
			for(String phone:phoneNum){
				phoneList.add(phone);
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sendDate = null;
			if(null!=smsBean.getSendTime() && !"".equals(smsBean.getSendTime())){
				try {
					sendDate = dateFormat.parse(smsBean.getSendTime());
				} catch (ParseException e) {
					result.setResult(false);
					result.setErrorMsg(ResultCodeEnum.RESULT_DateFormatError_CODE.getResultDecp());
					result.setResultCode(ResultCodeEnum.RESULT_DateFormatError_CODE.getResultCode());
					
					return result;
				}
			}
			SMSModemService smsService = new SMSModemService(); 
			HttpConnectionMsg connMsg = smsService.requestHttpSendMessage(phoneList, smsBean.getContent(), sendDate);
			
			result.setResult(connMsg.isStatus());
			result.setMsg(connMsg.getMsg());
			
			return result;
		} else {
			result.setResult(false);
			result.setErrorMsg(ResultCodeEnum.RESULT_SMS_PhoneNumOrContentError_CODE.getResultDecp());
			result.setResultCode(ResultCodeEnum.RESULT_SMS_PhoneNumOrContentError_CODE.getResultCode());
			return result;
		}

	}
}
