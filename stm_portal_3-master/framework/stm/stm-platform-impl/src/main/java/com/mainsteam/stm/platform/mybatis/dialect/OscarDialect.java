package com.mainsteam.stm.platform.mybatis.dialect;

import java.util.regex.Pattern;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: MySqlDialect.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class OscarDialect extends AbstractDialect{
	@Override
	public String getPageSql(String originalSql, Page<?,?> page) {
		String sql= "SELECT * FROM ( SELECT ocpaget1.*, ROWNUM ocpageRN FROM ("+originalSql+genenrateOrderBy(page)+
				") ocpaget1 WHERE ROWNUM <= "+(page.getRowCount()+page.getStartRow() > Integer.MAX_VALUE ? Integer.MAX_VALUE : page.getRowCount()+page.getStartRow())+" ) WHERE ocpageRN > "+page.getStartRow();
		
		return sql;
	}
	
	private String genenrateOrderBy(Page<?,?> page){
		if(page.getSort()!=null){
			if(!Pattern.matches(ORDER_BY, page.getSort())){
				return "";
			}
			if(page.getOrder() != null && !Pattern.matches(ORDER_BY, page.getOrder())){
				return "";
			}
			return " order by "+page.getSort()+' '+page.getOrder();
		}
		return "";
	}
}


