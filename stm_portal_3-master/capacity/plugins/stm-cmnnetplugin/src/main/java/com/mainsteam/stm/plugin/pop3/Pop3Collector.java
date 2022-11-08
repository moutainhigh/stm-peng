package com.mainsteam.stm.plugin.pop3;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.AvailableStateEnum;

public class Pop3Collector {
	private static final Log log=LogFactory.getLog(Pop3Collector.class);
	private boolean AVAILABLE;
	private String  RESPONSE_TIME;
	private static Store store=null;
	public Pop3Collector(){
		this.AVAILABLE=false;
		this.RESPONSE_TIME=null;
	}
	public String availability(Pop3Bo pop3Bo){
		connect(pop3Bo);
		String result=null;
		if(AVAILABLE){
        	result=AvailableStateEnum.Normal.getStateVal()+"";
        	return result;
        }
        else {
        	result=AvailableStateEnum.Critical.getStateVal()+"";
        	return result;
		}
	}
	
	public String ResponseTime(){
		if(AVAILABLE){
			return RESPONSE_TIME;
		}
		else {
			return "";
		}
	}
	 /**
     * 连接邮件服务器
     */
	public boolean connect(Pop3Bo pop3Bo) {
    	Session session=null;
    	
        // 判断是否需要身份认证
        Pop3MailAuth authenticator = null;
        if (StringUtils.isNotBlank(pop3Bo.getUserName())) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new Pop3MailAuth(pop3Bo.getUserName(),
                    pop3Bo.getUserPassword());
            //创建session
          session = Session.getInstance(pop3Bo
                    .getProperties(), authenticator);
        }
        else {
        	log.error("连接服务器失败");
        	AVAILABLE=false;
        	return AVAILABLE;
		}
       

        //创建store,建立连接
        try {
            store = session.getStore(pop3Bo.getProtocal());
            long startTime=System.currentTimeMillis();
            store.connect();
            AVAILABLE = store.isConnected();
            long endTime=System.currentTimeMillis();
            RESPONSE_TIME=(endTime-startTime)+"";
        } catch (NoSuchProviderException e) {
        	log.error("创建连接失败！", e);
        	AVAILABLE = false;
        }
        catch (MessagingException e) {
        	log.error("连接服务器失败！", e);
            AVAILABLE = false;
        }
        return AVAILABLE;
    }
    
    public static void close(){
    	if (store != null) {
            try {
            	store.close();
            } catch (Exception e) {
            }
            store = null;
        }
    }
}
