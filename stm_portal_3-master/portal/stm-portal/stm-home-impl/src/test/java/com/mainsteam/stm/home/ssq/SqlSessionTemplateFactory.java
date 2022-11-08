/**
 * 
 */
package com.mainsteam.stm.home.ssq;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDao;
import com.mainsteam.stm.home.layout.dao.impl.HomeLayoutDaoImpl;

/**
 * <li>文件名称: SqlSessionTemplateFactory.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 对外提供SessionTemplateFactory，该类提供给测试操作</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年5月10日
 * @author  tandl
 */
public class SqlSessionTemplateFactory {
	
	private String url="jdbc:mysql://192.168.1.198:3306/oc4db?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&rewriteBatchedStatements=true";
	private String userName="root";
	private String password="root3306";
	private String driverName = null;
	
	public SqlSessionTemplateFactory(){
		
	}
	
	public SqlSessionTemplateFactory(String url,String userName,String password){
		this.url = url;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * 
	 * @param mappers mybaits 对应的mapper文件包路径，如：com.mainsteam.stm.portal.probe.dao
	 * @return
	 * @throws SQLException
	 */
	public SqlSessionTemplate getSqlSessionTemplate(String mappers) throws SQLException{
		
		DruidDataSource dds =  new DruidDataSource();
		dds.setUrl(this.url);
		dds.setUsername(this.userName);
		dds.setPassword(this.password);
		if(this.driverName != null)
			dds.setDriverClassName(this.driverName);
		
		dds.setInitialSize(10);
		dds.setMaxActive(100);
		dds.init();
		
		DataSource dataSource = dds;
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		
		Configuration configuration = new Configuration(environment);
		//configuration.addMapper(IProbeUrlAccessSuccessRateDao.class);
		configuration.addMappers(mappers);
		//MappedStatement ms = MappedStatement;
		//configuration.addMappedStatement(ms);
		System.out.println(configuration.getMappedStatements().size());
		
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		SqlSessionTemplate st = new SqlSessionTemplate(sqlSessionFactory);
		return st;
	}
	
	public SqlSessionTemplate getSqlSessionTemplate(Class<?> mapper) throws SQLException{
		
		DruidDataSource dds =  new DruidDataSource();
		dds.setUrl(this.url);
		dds.setUsername(this.userName);
		dds.setPassword(this.password);
		if(this.driverName != null)
			dds.setDriverClassName(this.driverName);
		
		dds.setInitialSize(10);
		dds.setMaxActive(100);
		dds.init();
		
		DataSource dataSource = dds;
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		
		Configuration configuration = new Configuration(environment);
		
		//configuration.addMappers(mappers);
		//MappedStatement ms = MappedStatement;
		//configuration.addMappedStatement(ms);
		
		DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
		Properties p = new Properties();
		p.setProperty("Oracle", "oracle");
		p.setProperty("MySQL", "mysql");
		p.setProperty("OSCAR", "oscar");
		databaseIdProvider.setProperties(p);
		String databaseId = databaseIdProvider.getDatabaseId(dataSource);
		System.out.println("当前采用的数据库:" + databaseId);
		configuration.setDatabaseId(databaseIdProvider.getDatabaseId(dataSource));//*/
		
		configuration.addMapper(mapper);
		System.out.println("mappedStatements size:" + configuration.getMappedStatements().size());
		
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		SqlSessionTemplate st = new SqlSessionTemplate(sqlSessionFactory);
		return st;
	}
	
	/**
	 * 配置数据库连接信息
	 * @param url
	 * @param userName
	 * @param password
	 */
	public void setJDBCInfo(String url,String userName,String password){
		this.url = url;
		this.userName = userName;
		this.password = password;
	}
	
	public void setDriverName(String driverName){
		this.driverName = driverName;
	}
	
	public static void main(String[] args) {
		SqlSessionTemplateFactory stt = new SqlSessionTemplateFactory();
		String turl="jdbc:mysql://192.168.10.14:3306/oc4db?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&rewriteBatchedStatements=true";
		String tuserName="oc4dev";
		String tpassword="oc4dev"; 
		stt.setJDBCInfo(turl, tuserName, tpassword);
		try {
			SqlSessionTemplate st = stt.getSqlSessionTemplate(IHomeLayoutDao.class);
			HomeLayoutDaoImpl hdi = new HomeLayoutDaoImpl(st);
			HomeLayoutBo b = hdi.getById(1);
			System.out.println(b.getName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
