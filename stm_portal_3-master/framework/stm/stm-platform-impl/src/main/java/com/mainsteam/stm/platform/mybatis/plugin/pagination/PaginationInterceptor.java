package com.mainsteam.stm.platform.mybatis.plugin.pagination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.mainsteam.stm.exception.OriginalException;
import com.mainsteam.stm.platform.mybatis.dialect.AbstractDialect;
import com.mainsteam.stm.platform.mybatis.dialect.DialectFactory;
import com.mainsteam.stm.util.IConstant;

/**
 * <li>文件名称: PaginationPlugin.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: mybatis分页插件</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */

@Intercepts({@Signature(
		type= Executor.class,
		method = "query",
		args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PaginationInterceptor  implements Interceptor,IConstant {
	private static final int MAPPED_STATEMENT_INDEX = 0;
	private static final int PARAMETER_INDEX = 1;
	private static final int ROWBOUNDS_INDEX = 2;
	private static final String sql="sql",SQLSOURCE_STRING="sqlSource";
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
//    private static AbstractDialect dialect;
	private static final Map<String,Builder> BUILDER_MAP=new HashMap<String,Builder>();
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object intercept(final Invocation invocation) throws Throwable {
        final Object[] queryArgs = invocation.getArgs();
        final MappedStatement ms = (MappedStatement)queryArgs[MAPPED_STATEMENT_INDEX];
        final BoundSql boundSql = ms.getBoundSql(queryArgs[PARAMETER_INDEX]);
        final Object prameterObj=boundSql.getParameterObject();

        if(prameterObj instanceof Page){
        	Page<?,?> page=(Page<?,?>)prameterObj;
        	int count=getCount(((Executor) invocation.getTarget()).getTransaction().getConnection(), boundSql, page, ms);
        	page.setTotalRecord(count);
        	if(count==0){
        		page.emptyDatas();
        	}else{
                queryArgs[ROWBOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
                queryArgs[MAPPED_STATEMENT_INDEX]=getPageStatement(ms, boundSql, page);
                page.setDatas((List)invocation.proceed());
        	}
        	return page.getDatas();
        }
        return invocation.proceed();
	}
	
	private static final MappedStatement getPageStatement(MappedStatement ms,BoundSql boundSql,Page<?,?> page){
		String dbtype=ms.getConfiguration().getDatabaseId();
		System.out.println("malachi dbtype == " + dbtype);
		if(dbtype==null)dbtype="mysql";
		AbstractDialect dialect=getDialect(dbtype);
		String id=ms.getId();
		Builder builder=BUILDER_MAP.get(id);
		if(builder==null){
			synchronized (BUILDER_MAP) {
				if(builder==null){
					builder = new Builder(ms.getConfiguration(),ms.getId(),new ExtSqlSource(boundSql),ms.getSqlCommandType());
					builder.resource(ms.getResource());
					builder.fetchSize(ms.getFetchSize());
					builder.statementType(ms.getStatementType());
					builder.keyGenerator(ms.getKeyGenerator());
					if(ms.getKeyProperties() != null && ms.getKeyProperties().length !=0){
			            StringBuffer keyProperties = new StringBuffer();
			            for(String keyProperty : ms.getKeyProperties()){
			                keyProperties.append(keyProperty).append(IConstant.p_comma_);
			            }
			            keyProperties.delete(keyProperties.length()-1, keyProperties.length());
						builder.keyProperty(keyProperties.toString());
					}
					
					builder.timeout(ms.getTimeout());
					
					builder.parameterMap(ms.getParameterMap());
					
			        builder.resultMaps(ms.getResultMaps());
					builder.resultSetType(ms.getResultSetType());
		
					builder.cache(ms.getCache());
					builder.flushCacheRequired(ms.isFlushCacheRequired());
					builder.useCache(ms.isUseCache());
			       	BUILDER_MAP.put(id,builder);
				}
			}
		}
		builder.resultMaps(ms.getResultMaps());
		ms=builder.build();
		
        MetaObject.forObject(boundSql,DEFAULT_OBJECT_FACTORY,DEFAULT_OBJECT_WRAPPER_FACTORY)
        	.setValue(sql, dialect.getPageSql(boundSql.getSql(), page));
       	MetaObject.forObject(ms,DEFAULT_OBJECT_FACTORY,DEFAULT_OBJECT_WRAPPER_FACTORY)
       		.setValue(SQLSOURCE_STRING,new ExtSqlSource(boundSql));
		return ms;
	}

	private static final int getCount(Connection connection,BoundSql boundSql,Page<?,?> page,
			MappedStatement mappedStatement){
		int count=0;
		String countSql=null;
		PreparedStatement countStmt=null;
		ResultSet rs =null;
		try {
			countSql=AbstractDialect.getCountSql(boundSql.getSql());
			countStmt = connection.prepareStatement(countSql);
	        DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement,page,boundSql);
	        handler.setParameters(countStmt);
	        rs = countStmt.executeQuery();
	        if (rs.next()) {
	            count=rs.getInt(1);
	        }

		} catch (SQLException e) {
			throw new OriginalException(e);
		}finally{
	        try {
				if(rs!=null){
					rs.close();
				}
				if(countStmt!=null){
					countStmt.close();
				}
			} catch (SQLException e) {
				
			}
	       
		}
		return count;
	}
	
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	private static AbstractDialect getDialect(String dbType){
		return DialectFactory.getDialect(dbType);
	}
	
	public void setProperties(Properties props) {
//    	dialect=DialectFactory.getDialect(props.getProperty(IConstant.str_dialect));
	}
}
