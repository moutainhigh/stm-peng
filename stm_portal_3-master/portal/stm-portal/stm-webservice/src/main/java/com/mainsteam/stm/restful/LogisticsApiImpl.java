package com.mainsteam.stm.restful;  
  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
  
public class LogisticsApiImpl implements ILogisticsApi {  
  
    private Logger log = LoggerFactory.getLogger(LogisticsApiImpl.class);  
    
  
    /**  
     * @see com.abc.warehouse.service.ILogisticsApi#doGet(java.lang.String, java.lang.String) 
     */  
    @Override  
    public String doGet(String firstName, String lastName) {  
        // TODO Auto-generated method stub  
        log.debug("doGet : " + firstName + ", lastName : " + lastName);  
        // to to something ...  
  
        return firstName;  
    }  
  
    @Override  
    public String doPost(){
    	return "success";
    }
  
}
