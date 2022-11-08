package com.mainsteam.stm.ipmanage.impl.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ipmanage.bo.IpMain;



public interface IpMainMapper {
	
    public List<IpMain> getIPList(@Param("ip")IpMain ip);
    
    public int insertIp(@Param("ip")IpMain ip);
    
    public int update(@Param("ip")IpMain ip);
    
    public int delete(@Param("id")Integer id);
    
    public int resetDepart(@Param("id")Integer departId);

	public int getcount(@Param("ip")IpMain ip);
}