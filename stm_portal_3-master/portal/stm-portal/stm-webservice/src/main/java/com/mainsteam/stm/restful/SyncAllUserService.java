package com.mainsteam.stm.restful;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/syncAllUserService")
public interface SyncAllUserService {

	@GET
	@Path("/getAllUser")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllUser();
}
