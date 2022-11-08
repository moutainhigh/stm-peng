package com.mainsteam.stm.camera.api;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.bo.JDBCVo;
import com.mainsteam.stm.camera.bo.TreeVo;

public interface ICameraDao {
	
	public List<JDBCVo> getDataBaseList(String configFile);
	
	public List<TreeVo> getCameraGroupList(String configFile,List<JDBCVo> jdbcList);
	
	public List<TreeVo> getCameraListByParentId(long parentId,List<JDBCVo> jdbcList,Map<String,List<CaremaMonitorBo>> groupMap);
	
	
	public void loadChildCameraList(long parentId,List<TreeVo> treeList,List<Connection> connectList,Map<String,List<CaremaMonitorBo>> groupMap);
	
	public List<Connection> getConnection(List<JDBCVo> jdbcList);

}
