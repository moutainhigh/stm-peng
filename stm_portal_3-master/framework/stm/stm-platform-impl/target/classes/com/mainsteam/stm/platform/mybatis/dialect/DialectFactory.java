package com.mainsteam.stm.platform.mybatis.dialect;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.exception.ConfigException;
import com.mainsteam.stm.exception.UnsupportedException;
import com.mainsteam.stm.util.IConstant;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: DialectFactory.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class DialectFactory implements IConstant {
	private static final Map<String,AbstractDialect> dialectMap=new HashMap<String, AbstractDialect>();
	static{
		dialectMap.put(IConstant.dbtype_mysql,new MySqlDialect());
		dialectMap.put(IConstant.dbtype_oracle,new OracleDialect());
		dialectMap.put(IConstant.dbtype_oscar,new OscarDialect());
		dialectMap.put(IConstant.dbtype_dm,new DMDialect());
		dialectMap.put(IConstant.dbtype_kingbase,new KingbaseDialect());

	}
	// malachi 配置数据库类型及分页插件
	public static final AbstractDialect getDialect(String dialect){
    	if(Util.isEmpty(dialect)||
    			(!IConstant.dbtype_mysql.equals(dialect)
    			&&!IConstant.dbtype_oracle.equals(dialect)
    			&&!IConstant.dbtype_oscar.equals(dialect)
    			&&!IConstant.dbtype_dm.equals(dialect)
						&&!IConstant.dbtype_kingbase.equals(dialect))    			){
    		throw new ConfigException("分页插件数据库方言配置错误，目前支持mysql、oracle、Oscar、DM、kingbase！");
    	}
    	AbstractDialect dia=dialectMap.get(dialect);
    	if(dia==null){
    		throw new UnsupportedException(dialect+"不支持该数据库方言！");
    	}
    	return dia;
	}
}


