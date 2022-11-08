package com.mainsteam.stm.restful;

import javax.ws.rs.GET;    
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  
import javax.ws.rs.core.MediaType;
 
  
@Path("/logisticsApi")  
public interface ILogisticsApi {  
  
    @GET  
    @Path("/doGet/{first}/{last}")  
    @Produces(MediaType.TEXT_PLAIN)  
    public String doGet(@PathParam(value = "first") String firstName, @PathParam(value = "last") String lastName);  
      
    @GET  
    @Path("/doPost")
    @Produces(MediaType.TEXT_PLAIN)  
    public String doPost();
} 
