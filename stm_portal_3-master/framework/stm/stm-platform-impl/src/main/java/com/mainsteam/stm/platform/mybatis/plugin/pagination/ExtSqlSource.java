package com.mainsteam.stm.platform.mybatis.plugin.pagination;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 用于绑定数据源以便分页查询，解决异步冲突，sql重写问题等
 * @author ziwenwen
 */
public class ExtSqlSource implements SqlSource {
	BoundSql boundSql;
	protected ExtSqlSource(BoundSql boundSql){
		this.boundSql=boundSql;
	}
	
	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		return boundSql;
	}

}
