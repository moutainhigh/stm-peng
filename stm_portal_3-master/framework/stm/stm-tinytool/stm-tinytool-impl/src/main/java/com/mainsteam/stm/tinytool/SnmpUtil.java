package com.mainsteam.stm.tinytool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.mainsteam.stm.tinytool.obj.SnmpTarget;

public class SnmpUtil {
	
	private static SnmpUtil snmpUtil;
    private SnmpUtil (){}  
    public static SnmpUtil getInstance(){
        if (snmpUtil == null) {  
            synchronized (SnmpUtil.class) {  
            if (snmpUtil == null) {  
            	snmpUtil = new SnmpUtil();  
            }  
            }  
        }  
        return snmpUtil;  
    }
    
	
	public List<String> SNMPTest(SnmpTarget snmpTarget)  throws IOException, InterruptedException{
		
		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());  
        snmp.listen();  
        
		int version=snmpTarget.getVersion();
		Address address=new UdpAddress(snmpTarget.getAddress());
		//团体子
		OctetString community= new OctetString(snmpTarget.getCommunity());
		//超时
		long timeout=snmpTarget.getTimeout();
		//重试
		int retries=snmpTarget.getRetries();

        
        //V1 and V2
        if(snmpTarget.getVersion()==SnmpConstants.version1 || snmpTarget.getVersion()==SnmpConstants.version2c){

            CommunityTarget target = new CommunityTarget();  
            target.setCommunity(community);
            target.setAddress(address);  
            target.setTimeout(timeout);   
            target.setRetries(retries);  
            target.setVersion(version);  
            return sendRequest(snmp, createGetPdu(), target);  

            
        }else if(snmpTarget.getVersion()==SnmpConstants.version3){//V3
        	USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);  
            SecurityModels.getInstance().addSecurityModel(usm);  
    		//安全名
            OctetString securityName=new OctetString(snmpTarget.getSecurityName());
            int securityLevel=snmpTarget.getSecurityLevel();
        	
            //认证协议
            OID authenticationProtocol = null;
            if(snmpTarget.getAuthenticationProtocol()!= null){
            	authenticationProtocol=new OID(snmpTarget.getAuthenticationProtocol());
            }
            
            
        	//认证密码
        	OctetString authenticationPassphrase = null;
        	if(snmpTarget.getAuthenticationPassphrase()!=null){
        		authenticationPassphrase=new OctetString(snmpTarget.getAuthenticationPassphrase());
        	}
        	
        	//私有协议
        	OID  privacyProtocol= null;
        	if(snmpTarget.getPrivacyProtocol()!=null){
        		privacyProtocol=new OID(snmpTarget.getPrivacyProtocol());
        	}
        	
        	
        	//私有密码
        	OctetString privacyPassphrase= null;
        	if(snmpTarget.getPrivacyPassphrase()!=null){
        		privacyPassphrase=new OctetString(snmpTarget.getPrivacyPassphrase());
        	}
        	
        	UsmUser user = new UsmUser(securityName,authenticationProtocol,authenticationPassphrase,privacyProtocol,privacyPassphrase);
            
            snmp.getUSM().addUser(securityName, user);  
              
            UserTarget target = new UserTarget();  
            target.setSecurityLevel(securityLevel); 
            target.setVersion(version);  
            target.setAddress(address); 
            target.setSecurityName(securityName);  
            target.setTimeout(timeout);   
            target.setRetries(retries);
                      
            OctetString contextEngineId = new OctetString("0002651100[02]");  
            return sendRequest(snmp, createGetPdu(contextEngineId), target);  
        }
        
        return null;
	}
	
    private static PDU createGetPdu(OctetString contextEngineId) {  
        ScopedPDU pdu = new ScopedPDU();  
        pdu.setType(PDU.GET);  
        pdu.setContextEngineID(contextEngineId);    //if not set, will be SNMP engine id  
        //pdu.setContextName(contextName);  //must be same as SNMP agent  
          
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0"))); //sysUpTime  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"))); //sysName  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5")));   //expect an no_such_instance error  
        return pdu;  
    }

      
    private static PDU createGetPdu() {  
        PDU pdu = new PDU();  
        pdu.setType(PDU.GET);  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"))); //sysName 
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0"))); //sysUpTime  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0")));// sysDescr
        
        return pdu;  
    }  
      
    private static PDU createGetNextPdu() {  
        PDU pdu = new PDU();  
        pdu.setType(PDU.GETNEXT);  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3")));   //sysUpTime  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5")));   //sysName  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0")));// sysDescr
        return pdu;  
    }  
      
    private static PDU createGetBulkPdu() {  
        PDU pdu = new PDU();  
        pdu.setType(PDU.GETBULK);  
        pdu.setMaxRepetitions(10);  //must set it, default is 0  
        pdu.setNonRepeaters(0);  
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1")));     //system  
        return pdu;  
    }
    
      
    private static List<String> sendRequest(Snmp snmp, PDU pdu, CommunityTarget target)  
    throws IOException {
    	List<String> respArry=new ArrayList<String>();
        ResponseEvent responseEvent = snmp.send(pdu, target);  
        PDU response = responseEvent.getResponse();  
          
        if (response == null) {  
        	respArry.add("TimeOut...");
        } else {  
            if (response.getErrorStatus() == PDU.noError) {  
                Vector<? extends VariableBinding> vbs = response.getVariableBindings();  
                for (VariableBinding vb : vbs) {  
                	respArry.add(vb + " ," + vb.getVariable().getSyntaxString());  
                }  
            } else {  
            	respArry.add("Error:" + response.getErrorStatusText());  
            }  
        }  
        
        return respArry;
    }  
    
    private static List<String> sendRequest(Snmp snmp, PDU pdu, UserTarget target)  
    	    throws IOException {
    			List<String> respArry=new ArrayList<String>();
    	
    	        ResponseEvent responseEvent = snmp.send(pdu, target);  
    	        PDU response = responseEvent.getResponse();  
    	          
    	        if (response == null) {  
    	        	respArry.add("TimeOut...");  
    	        } else {  
    	            if (response.getErrorStatus() == PDU.noError) {  
    	                Vector<? extends VariableBinding> vbs = response.getVariableBindings();  
    	                for (VariableBinding vb : vbs) {  
    	                	respArry.add(vb + " ," + vb.getVariable().getSyntaxString());  
    	                }  
    	            } else {  
    	            	respArry.add("Error:" + response.getErrorStatusText());  
    	            }  
    	        }  
    	        return respArry;
    	    } 
       
    
    public static void main(String[] args) throws Exception{
    	
//    	System.out.println(AuthMD5.ID);
//    	System.out.println(AuthSHA.ID);
//    	
//    	System.out.println(SecurityLevel.NOAUTH_NOPRIV);
//    	System.out.println(SecurityLevel.AUTH_NOPRIV);
//    	System.out.println(SecurityLevel.AUTH_PRIV);
    	
    	System.out.println(PrivDES.ID);
    	System.out.println(Priv3DES.ID);
    	System.out.println(PrivAES128.ID);
    	System.out.println(PrivAES192.ID);
    	System.out.println(PrivAES256.ID);
//		<option value="des">PrivDES</option>
//		<option value="3des">Priv3DES</option>
//		<option value="aes128">PrivAES128</option>
//		<option value="aes192">PrivAES192</option>
//		<option value="aes256">PrivAES256</option>
    	System.out.println();
    	
//    	Snmp snmp = new Snmp(new DefaultUdpTransportMapping());  
//        snmp.listen();  
//        
//        CommunityTarget target = new CommunityTarget();  
//        target.setCommunity(new OctetString("public"));  
//        target.setVersion(SnmpConstants.version2c);  
//        target.setAddress(new UdpAddress("172.16.7.200/161"));  
//        target.setTimeout(10000);
//        target.setRetries(1);  
//        
//        List<String> s=sendRequest(snmp, createGetPdu(), target);  
//        for(String a:s){
//        	System.out.println(a);
//        }
        
    }
    
}
