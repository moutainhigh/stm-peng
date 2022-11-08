package com.mainsteam.stm.simple.manager.workbench.dao;

import java.util.List;

import com.mainsteam.stm.simple.manager.workbench.report.bo.ExpectBo;

public interface IWorkbenchDao {

	/**
	* @Title: get
	* @Description: 根据期望值ID获取
	* @return  ExpectBo
	* @throws
	*/
	ExpectBo get(Long id);
	
	/**
	* @Title: save
	* @Description: 保存期望值
	* @param expect
	* @return  int
	* @throws
	*/
	int insert(ExpectBo expect);
	
	/**
	* @Title: delete
	* @Description: 删除期望值
	* @param id
	* @return  int
	* @throws
	*/
	int delete(long id);
	
	/**
	* @Title: select
	* @Description: 通过报表ID获取期望值
	* @param reportId
	* @return  List<ExpectBo>
	* @throws
	*/
	List<ExpectBo> select(Long reportId);
	
	int update(ExpectBo expect);
}
