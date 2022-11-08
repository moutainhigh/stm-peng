package com.mainsteam.stm.video.report.dao;

import java.util.List;

import com.mainsteam.stm.video.report.bo.Organizayions;


public interface OrganizationsInfoDao {
	
	public List<Organizayions> getAllOrg();
	public List<Organizayions> getAllOrgByLevel(int pid,int level);
	public List<Organizayions> getAllOrgByid(int id);
	public List<Organizayions> getAllOrgByPid(int pid);
	
}
