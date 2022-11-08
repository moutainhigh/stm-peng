package com.mainsteam.stm.ct.bo;

import java.util.Date;

public class MsProbe {

    private static final long serialVersionUID = 1L;

   
    private Integer id;
    private String probe_site;
    private String probe_ip;
    private String probe_port;
    private Integer probe_status;
    private Date last_time;
    private Date create_time;
    private Date update_time;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProbe_site() {
		return probe_site;
	}
	public void setProbe_site(String probe_site) {
		this.probe_site = probe_site;
	}
	public String getProbe_ip() {
		return probe_ip;
	}
	public void setProbe_ip(String probe_ip) {
		this.probe_ip = probe_ip;
	}
	public String getProbe_port() {
		return probe_port;
	}
	public void setProbe_port(String probe_port) {
		this.probe_port = probe_port;
	}
	public Integer getProbe_status() {
		return probe_status;
	}
	public void setProbe_status(Integer probe_status) {
		this.probe_status = probe_status;
	}
	public Date getLast_time() {
		return last_time;
	}
	public void setLast_time(Date last_time) {
		this.last_time = last_time;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	
    
    

}
