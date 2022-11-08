package com.mainsteam.stm.ipmanage.bo;

import java.io.Serializable;

public class Depart implements Serializable{
	
	
    private Integer id;

    private String name;

    private String create_Time;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreate_Time() {
		return create_Time;
	}

	public void setCreate_Time(String create_Time) {
		this.create_Time = create_Time;
	}

    
}