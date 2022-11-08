package com.mainsteam.stm.ipmanage.api;

import java.util.List;

import com.mainsteam.stm.ipmanage.bo.Depart;

public interface DepartService {
	public List<Depart> getDepartList();
	
	public int insert(Depart depart);
	
	public int del(Integer id);
}
