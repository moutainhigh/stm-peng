package com.mainsteam.stm.ipmanage.impl.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ipmanage.bo.Depart;


public interface DepartMapper {
	
	List<Depart> getDepartList();

	int insert(@Param("depart")Depart depart);
	
	int delete(@Param("id")Integer id);

	Depart findIdByName(@Param("depart")String depart);
}