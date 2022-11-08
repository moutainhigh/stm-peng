package com.mainsteam.stm.ct.bo;


public class MsPingMetric {

    private String id;

    private String ip;
    private Integer state;
    private Integer latency;
    private Integer packet_loss;
    private Integer jitter;
    private String create_time;
    private String resource_id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getLatency() {
		return latency;
	}
	public void setLatency(Integer latency) {
		this.latency = latency;
	}
	public Integer getPacket_loss() {
		return packet_loss;
	}
	public void setPacket_loss(Integer packet_loss) {
		this.packet_loss = packet_loss;
	}
	public Integer getJitter() {
		return jitter;
	}
	public void setJitter(Integer jitter) {
		this.jitter = jitter;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
	
}
