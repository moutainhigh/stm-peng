package com.mainsteam.stm.restful;

import org.apache.cxf.interceptor.Fault;  
import org.apache.cxf.interceptor.LoggingInInterceptor;  
import org.apache.cxf.message.Message;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
  
  
public class EncodingLoggingInInterceptor extends LoggingInInterceptor {  
  
    private Logger log = LoggerFactory.getLogger(getClass());  
      
    /** 
     *  
     */  
    public EncodingLoggingInInterceptor() {  
        // TODO Auto-generated constructor stub  
        super();  
    }  
      
    /**  
     * @see org.apache.cxf.interceptor.LoggingInInterceptor#handleMessage(org.apache.cxf.message.Message) 
     */  
    @Override  
    public void handleMessage(Message message) throws Fault {  
        // TODO Auto-generated method stub  
          
        String encoding = System.getProperty("file.encoding");  
        encoding = encoding == null || encoding.equals("") ? "UTF-8" : encoding;  
          
        log.debug("encoding : " + encoding);  
        message.put(Message.ENCODING, encoding);  
          
        super.handleMessage(message);  
    }  
} 
