package com.mainsteam.stm.ipmanage.bo;

public class Segment {
    private Integer id;

    private String start_address;

    private String end_address;

    private String broadcast_address;

    private Integer parent_id;

    private Integer has_childseg;

    private String web_address;

    private Integer parent_node_id;

    private String create_time;
    
    private Integer parent_group_id;
    

	public Integer getParent_group_id() {
		return parent_group_id;
	}

	public void setParent_group_id(Integer parent_group_id) {
		this.parent_group_id = parent_group_id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStart_address() {
		return start_address;
	}

	public void setStart_address(String start_address) {
		this.start_address = start_address;
	}

	public String getEnd_address() {
		return end_address;
	}

	public void setEnd_address(String end_address) {
		this.end_address = end_address;
	}

	public String getBroadcast_address() {
		return broadcast_address;
	}

	public void setBroadcast_address(String broadcast_address) {
		this.broadcast_address = broadcast_address;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public Integer getHas_childseg() {
		return has_childseg;
	}

	public void setHas_childseg(Integer has_childseg) {
		this.has_childseg = has_childseg;
	}

	public String getWeb_address() {
		return web_address;
	}

	public void setWeb_address(String web_address) {
		this.web_address = web_address;
	}

	public Integer getParent_node_id() {
		return parent_node_id;
	}

	public void setParent_node_id(Integer parent_node_id) {
		this.parent_node_id = parent_node_id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "Segment [id=" + id + ", start_address=" + start_address + ", end_address=" + end_address
				+ ", broadcast_address=" + broadcast_address + ", parent_id=" + parent_id + ", has_childseg="
				+ has_childseg + ", web_address=" + web_address + ", parent_node_id=" + parent_node_id
				+ ", create_time=" + create_time + "]";
	}

    
}