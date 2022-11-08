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
public class MySqlDialect extends AbstractDialect{
	@Override
	public String getPageSql(String originalSql, Page<?,?> page) {
		return originalSql+genenrateOrderBy(page)+" limit "+page.getStartRow()+","+page.getRowCount();
	}
	
	private String genenrateOrderBy(Page<?,?> page){
		if(page.getSort() != null){
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
	
	private static String humpToLine(String str){
		String temp="";
		char c,prevA='A'-1,nexZ='Z'+1;
		for(int i=0,len=str.length();i<len;i++){
			c=str.charAt(i);
			if(c>prevA&&c<nexZ){
				temp+='_';
			}
			temp+=c;
		}
		return temp;
	}
}


