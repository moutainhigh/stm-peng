package com.mainsteam.stm.message.smsmodem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.message.HttpConnectionMsg;
import com.mainsteam.stm.message.MsgSettingInfo;
import com.mainsteam.stm.message.MsgSettingManager;

/**
 * <li>文件名称: SMSService.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有:版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月19日
 * @author zhangjunfeng
 */

public class SMSModemService {
	public HttpConnectionMsg requestHttpSendMessage(List<String> sendPhones,String content,Date sendTime){
		HttpConnectionMsg requestMsg = null;
		try {
			MsgSettingInfo setting = new MsgSettingManager().getMsgSetting();
			String urlPath = "http://"+setting.getClientIp()+":"+setting.getClientPort()+"/smsclient/service/http/sendMessage.htm";
			URL url = new URL(urlPath);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setDoOutput(true); 
			con.setDoInput(true); 
			con.setRequestMethod("POST"); 
			con.setUseCaches(false);
			con.setInstanceFollowRedirects(true); 
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			con.connect();
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			StringBuffer requestParameters = new StringBuffer();
			if(sendPhones!=null && sendPhones.size()>0){
				String phoneStr = "";
				for (String phone : sendPhones) {
					phoneStr+=phone+";";
				}
				if(phoneStr!=null && phoneStr!=""){
					phoneStr = phoneStr.substring(0,phoneStr.length()-1);
				}
				requestParameters.append("&destNumber=").append(phoneStr);
			}
			if(content!=null && content!=""){
				requestParameters.append("&content=").append(URLEncoder.encode(content, "utf-8"));
			}
			if(sendTime!=null){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				requestParameters.append("&sendTime=").append(dateFormat.format(sendTime));
			}
			String paramStr = requestParameters.toString();
			if(!StringUtils.isEmpty(paramStr)){
				paramStr = paramStr.substring(1);
				out.writeBytes(paramStr);
				out.flush();
				out.close();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));//设置编码,否则中文乱码 
			String requestStr=""; 
			requestStr = reader.readLine();
			if(requestStr!=null && requestStr!=""){
				requestMsg = JSONObject.parseObject(requestStr,HttpConnectionMsg.class);
			}
			reader.close(); 
			con.disconnect();
			return requestMsg;
		}catch(ConnectException ce){
			requestMsg = new HttpConnectionMsg();
			requestMsg.setMsg("连接失败，请检查IP和端口是否设置正确，或者短信猫服务是否启动！");
			requestMsg.setStatus(false);
			return requestMsg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestMsg = new HttpConnectionMsg();
		requestMsg.setStatus(false);
		return requestMsg;
	}
	
	public HttpConnectionMsg requestHttpSentRecord(Long taskId,String phoneNumber,Date beginSentTime,Date endSentTime){
		HttpConnectionMsg requestMsg = null;
		try {
			
			MsgSettingInfo setting = new MsgSettingManager().getMsgSetting();
			String urlPath = "http://"+setting.getClientIp()+":"+setting.getClientPort()+"/smsclient/service/http/sentRecord.htm";
			URL url = new URL(urlPath);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setDoOutput(true); 
			con.setDoInput(true); 
			con.setRequestMethod("POST"); 
			con.setUseCaches(false);
			con.setInstanceFollowRedirects(true); 
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			con.connect();
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			StringBuffer requestParameters = new StringBuffer();
			if(taskId!=null){
				requestParameters.append("&taskId=").append(taskId);
			}
			if(phoneNumber!=null && !phoneNumber.isEmpty()){
				requestParameters.append("&phoneNumber=").append(phoneNumber);
			}
			if(beginSentTime!=null){
				requestParameters.append("&beginSentTime").append(beginSentTime);
			}
			
			if(endSentTime!=null){
				requestParameters.append("&endSentTime").append(endSentTime);
			}
			
			String paramStr = requestParameters.toString();
			if(!StringUtils.isEmpty(paramStr)){
				paramStr = paramStr.substring(1);
				out.writeBytes(paramStr);
				out.flush();
				out.close();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));//设置编码,否则中文乱码 
			String requestStr=""; 
			requestStr = reader.readLine();
			if(requestStr!=null && requestStr!=""){
				requestMsg = JSONObject.parseObject(requestStr,HttpConnectionMsg.class);
			}
			reader.close(); 
			con.disconnect();
			return requestMsg;
		}catch(ConnectException ce){
			requestMsg = new HttpConnectionMsg();
			requestMsg.setMsg("连接失败，请检查IP和端口是否设置正确，或者短信猫服务是否启动！");
			requestMsg.setStatus(false);
			return requestMsg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestMsg = new HttpConnectionMsg();
		requestMsg.setStatus(false);
		return requestMsg;
	}
}
