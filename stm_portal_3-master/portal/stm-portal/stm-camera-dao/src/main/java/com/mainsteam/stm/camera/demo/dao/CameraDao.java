package com.mainsteam.stm.camera.demo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.mainsteam.stm.camera.api.ICameraDao;
import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.bo.JDBCVo;
import com.mainsteam.stm.camera.bo.TreeVo;
import com.mainsteam.stm.camera.util.CameraUtils;

public class CameraDao implements ICameraDao{
	
	private Logger logger = Logger.getLogger(CameraDao.class);
	
	@Override
	public List<JDBCVo> getDataBaseList(String configFile){
		logger.info("开始得到sqlserver数据库的信息");
		List<Element> elements = CameraUtils.getListFromXML(configFile);
		List<JDBCVo> jdbcList = new ArrayList<JDBCVo>();
		Element databases = elements.get(1);  
		 List<DefaultElement> databaseElements=databases.elements("database");
		 for (int k=0;k<databaseElements.size();k++) {  
				DefaultElement attribute=databaseElements.get(k);
				List<DefaultElement> eList=attribute.elements();
				String jdbcDriver=null;
				String jdbcUrl=null;
				String username=null;
				String password=null;
				String rootGroupId=null;
				
				for (DefaultElement child : eList) {  
		                String name = child.getName();
		                if(name.equals("jdbcDriver")){
		                	 jdbcDriver= child.getText();
		                }
		                if(name.equals("jdbcUrl")){
		                	 jdbcUrl= child.getText();
		                }
		                if(name.equals("username")){
		                	 username= child.getText();
		                }
		                if(name.equals("password")){
		                	 password= child.getText();
		                }
		                if(name.equals("rootGroupId")){
		                	rootGroupId= child.getText();
		                }
		                
			}
				JDBCVo vo=new JDBCVo();
                vo.setJdbcDriver(jdbcDriver);
                vo.setJdbcUrl(jdbcUrl);
                vo.setUsername(username);
                vo.setPassword(password);
                vo.setRootGroupId(rootGroupId);
                jdbcList.add(vo);
			}
		 return jdbcList;
	}
	
	
	
	@Override
	public List<TreeVo> getCameraGroupList(String configFile,List<JDBCVo> jdbcList){
		 List<TreeVo> resourceCategory=new ArrayList<TreeVo>();
		for (int k=0;k<jdbcList.size();k++) {  
			    JDBCVo vo=jdbcList.get(k);
				String jdbcDriver=vo.getJdbcDriver();
				String jdbcUrl=vo.getJdbcUrl();
				String username=vo.getUsername();
				String password=vo.getPassword();
				String rootId=vo.getRootGroupId();
				TreeVo re = new TreeVo();
				 long parentId=Long.valueOf(rootId);
				 re.setId("CameraPlatform["+rootId+"]");
				 re.setName("摄像头");
				 re.setPid("Resource");
				 re.setState("closed");
				 re.setIsCamera("Y");
				 re.setType(1);
				 resourceCategory.add(re);
	         	   try {
			              Class.forName(jdbcDriver);
			              Connection dbConn = DriverManager.getConnection(jdbcUrl, username, password);
			              StringBuffer sql=new StringBuffer();
			              sql.append(" select * from v_group t where t.id=? ");
			              sql.append(" union ");
			              sql.append(" select * from v_group t where t.parentid=?");
			              /*sql.append(" union ");
			              sql.append(" select * from v_group s where s.parentid in ");
			              sql.append("(select t.id from v_group t where t.parentid=?)");
			              sql.append(" union ");
			              sql.append(" select * from v_group m where m.parentid in");
			              sql.append("(select s.id from v_group s where s.parentid in");
			              sql.append("(select t.id from v_group t where t.parentid=?))");*/
			              PreparedStatement statement=dbConn.prepareStatement(sql.toString());
			              statement.setLong(1, parentId);
			              statement.setLong(2, parentId);
			              ResultSet rs=statement.executeQuery();
			              //得到root下面的子节点
							 while (rs.next()) {
								 long id=rs.getLong("id");
								 String name=rs.getString("name");
								 long parId=rs.getLong("parentid");
								 TreeVo resource = new TreeVo();
								 resource.setId("CameraRes["+String.valueOf(id)+"]");
								 resource.setName(name);
								 resource.setState("closed");
								 resource.setType(1);
								 resource.setIsCamera("Y");
								 if(id==parentId){
									 resource.setPid("CameraPlatform["+String.valueOf(parentId)+"]");
								 }
								 else{
									 resource.setPid("CameraRes["+String.valueOf(parId)+"]");
								 }
								 resourceCategory.add(resource);
								 resource=null;
							 }
							 
							 
							 
							 String sql1=" select * from v_group s where s.parentid in (select t.id from v_group t where t.parentid=?)";
							  PreparedStatement statement1=dbConn.prepareStatement(sql1);
							  statement1.setLong(1, parentId);
							  ResultSet rs1=statement1.executeQuery();
								 while (rs1.next()) {
									 long id=rs1.getLong("id");
									 String name=rs1.getString("name");
									 long parId=rs1.getLong("parentid");
									 TreeVo resource = new TreeVo();
									 resource.setId("CameraRes["+String.valueOf(id)+"]");
									 resource.setName(name);
									 resource.setPid(String.valueOf("CameraRes["+String.valueOf(parId)+"]"));
									 resource.setType(2);
									 resource.setIsCamera("Y");
									 resourceCategory.add(resource);
									 resource=null;
								 }
							 
							 
							 
			         } catch (Exception e) {
			             e.printStackTrace();
			       }
		}
		return resourceCategory;
	}
	
	
	public List<TreeVo> getCameraListByParentId(long parentId,List<JDBCVo> jdbcList,Map<String,List<CaremaMonitorBo>> groupMap){
		List<TreeVo> treeList=new ArrayList<TreeVo>();
		for (int k=0;k<jdbcList.size();k++) {  
			 JDBCVo vo=jdbcList.get(k);
				String jdbcDriver=vo.getJdbcDriver();
				String jdbcUrl=vo.getJdbcUrl();
				String username=vo.getUsername();
				String password=vo.getPassword();
				try {
					 Class.forName(jdbcDriver);
		              Connection dbConn = DriverManager.getConnection(jdbcUrl, username, password);
		      //根据parentId查询下级节点是否有值
						 StringBuffer sql_check=new StringBuffer();
						 sql_check.append(" select * from v_group t where t.parentid=? ");
						 PreparedStatement statement_check=dbConn.prepareStatement(sql_check.toString());
						 statement_check.setLong(1, parentId);
						 ResultSet rs_check=statement_check.executeQuery();
						 logger.info("根据parentId查询下级节点是否有值");
						 //如果下级节点有值，不要遍历parentId
						 if(rs_check.next()){
							 logger.info("下级节点有值");
							 //查询下级节点
							 StringBuffer sql2=new StringBuffer();
							 sql2.append(" select * from v_group t where t.parentid=? ");
							 PreparedStatement statement2=dbConn.prepareStatement(sql2.toString());
							 statement2.setLong(1, parentId);
							 ResultSet rs2=statement2.executeQuery();
							 while (rs2.next()) {
								 long id=rs2.getLong("id");
								 String name=rs2.getString("name");
								 TreeVo resource = new TreeVo();
								 resource.setName(name);
								 resource.setId(String.valueOf(id));
								 if(groupMap.containsKey(String.valueOf(id))){
									 resource.setCameraList(groupMap.get(String.valueOf(id)));
								 }
								 treeList.add(resource);
							 }
						 }
						//如果下级节点没值，只要遍历parentId
						 else{
							 logger.info("下级节点没值");
							 StringBuffer sql=new StringBuffer();
				              sql.append(" select * from v_group t where t.id=? ");
				              PreparedStatement statement=dbConn.prepareStatement(sql.toString());
				              statement.setLong(1, parentId);
				              ResultSet rs=statement.executeQuery();
								 while (rs.next()) {
									 long id=rs.getLong("id");
									 String name=rs.getString("name");
									 TreeVo resource = new TreeVo();
									 resource.setName(name);
									 resource.setId(String.valueOf(id));
									 if(groupMap.containsKey(String.valueOf(id))){
										 resource.setCameraList(groupMap.get(String.valueOf(id)));
									 }
									 treeList.add(resource);
						 }
						 }
						 
						 
						 
						 
				  } catch (Exception e) {
			             e.printStackTrace();
			       }
		}
		return treeList;
	}
	
	@Override
	public void loadChildCameraList(long parentId,List<TreeVo> treeList,List<Connection> connectList,Map<String,List<CaremaMonitorBo>> groupMap){
         StringBuffer sql=new StringBuffer();
         sql.append(" select * from v_group t where t.parentid=? ");
         for(Connection dbConn:connectList){
             try{
            	 PreparedStatement statement=dbConn.prepareStatement(sql.toString());
                 statement.setLong(1, parentId);
                 ResultSet rs=statement.executeQuery();
                 while (rs.next()) {
                	 long id=rs.getLong("id");
    				 String name=rs.getString("name");
    				 TreeVo resource = new TreeVo();
    				 resource.setName(name);
    				 resource.setId(String.valueOf(id));
    				 if(groupMap.containsKey(String.valueOf(id))){
    					 resource.setCameraList(groupMap.get(String.valueOf(id)));
    				 }
    				 treeList.add(resource);
    				 loadChildCameraList(id,treeList,connectList,groupMap);
                 }
             }catch(Exception e){
            	 e.printStackTrace();
             }
         }
    
        
	}
	
	
	@Override
	public List<Connection> getConnection(List<JDBCVo> jdbcList){
		List<Connection> connList=new ArrayList<Connection>();
		if(null!=jdbcList){
			for(JDBCVo vo:jdbcList){
				String jdbcDriver=vo.getJdbcDriver();
				String jdbcUrl=vo.getJdbcUrl();
				String username=vo.getUsername();
				String password=vo.getPassword();
				try {
					 Class.forName(jdbcDriver);
					 Connection dbConn = DriverManager.getConnection(jdbcUrl, username, password);
					 connList.add(dbConn);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return connList;
		
	}
	
	
	
	
	
	
	
	

}
