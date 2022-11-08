package com.mainsteam.stm.plugin.smtp;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.AvailableStateEnum;
public class SmtpCollector {
	private static final Log log=LogFactory.getLog(SmtpCollector.class);
	private boolean AVAILABLE;
	private String  RESPONSE_TIME;
	private static Transport transport = null;
	public SmtpCollector(){
		this.AVAILABLE=false;
		this.RESPONSE_TIME=null;
	}
	public String availability(SmtpBo smtpBo){
		connect(smtpBo);
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
	
	public boolean connect(SmtpBo smtpBo){
        Session session = null;
       
        long startTime=0,endTime=0;
        try {
            Properties p = new Properties();
            p.put("mail.smtp.host", smtpBo.getIp());
            p.put("mail.smtp.port", smtpBo.getPort());
            String userName = smtpBo.getUserName();
            String password = smtpBo.getUserPassword();
            if ( null == userName|| userName.length() == 0) {
                p.put("mail.smtp.auth", new Boolean(false).toString());
                session = Session.getInstance(p);

            } else {
                p.put("mail.smtp.auth", new Boolean(true).toString());
                session = Session.getInstance(p, new SmtpMailAuth(userName, password));
            }
            transport = session.getTransport("smtp");
            startTime=System.currentTimeMillis();
            transport.connect();
            AVAILABLE =transport.isConnected();
            endTime=System.currentTimeMillis();
            RESPONSE_TIME=(endTime-startTime)+"";
            
        } catch (Exception e) {
            log.error("error in collect smtp node, can not create transport for smtp.", e);
        } 
        return AVAILABLE;
	}
	public static void close(){
		if (transport != null) {
            try {
            	transport.close();
            } catch (Exception e) {
            }
            transport = null;
        }
	}
}
