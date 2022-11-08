package com.mainsteam.stm.ct.bo;


public class MsCtResourceinfo {
    private String id;
    private Integer status;
    private Long content_size;
    private String mime_type;
    private String server_ip;
    private String req_url;
    private Long blocked;
    private Long dns;
    private Long connect;
    private Long send;
    private Long wait;
    private Long receive;
    private Long total_time;
    private Long ssl_time;
    private String resource_id;
    private String create_time;
    
    
	@Override
	public String toString() {
		return "MsCtResourceinfo [id=" + id + ", status=" + status + ", content_size=" + content_size + ", mime_type="
				+ mime_type + ", server_ip=" + server_ip + ", req_url=" + req_url + ", blocked=" + blocked + ", dns="
				+ dns + ", connect=" + connect + ", send=" + send + ", wait=" + wait + ", receive=" + receive
				+ ", total_time=" + total_time + ", ssl_time=" + ssl_time + ", resource_id=" + resource_id
				+ ", create_time=" + create_time + "]";
	}
	public Long getSsl_time() {
		return ssl_time;
	}
	public void setSsl_time(Long ssl_time) {
		this.ssl_time = ssl_time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getContent_size() {
		return content_size;
	}
	public void setContent_size(Long content_size) {
		this.content_size = content_size;
	}
	public String getMime_type() {
		return mime_type;
	}
	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}
	public String getServer_ip() {
		return server_ip;
	}
	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}
	public String getReq_url() {
		return req_url;
	}
	public void setReq_url(String req_url) {
		this.req_url = req_url;
	}
	public Long getBlocked() {
		return blocked;
	}
	public void setBlocked(Long blocked) {
		this.blocked = blocked;
	}
	public Long getDns() {
		return dns;
	}
	public void setDns(Long dns) {
		this.dns = dns;
	}
	public Long getConnect() {
		return connect;
	}
	public void setConnect(Long connect) {
		this.connect = connect;
	}
	public Long getSend() {
		return send;
	}
	public void setSend(Long send) {
		this.send = send;
	}
	public Long getWait() {
		return wait;
	}
	public void setWait(Long wait) {
		this.wait = wait;
	}
	public Long getReceive() {
		return receive;
	}
	public void setReceive(Long receive) {
		this.receive = receive;
	}
	public Long getTotal_time() {
		return total_time;
	}
	public void setTotal_time(Long total_time) {
		this.total_time = total_time;
	}
	
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
    
}
