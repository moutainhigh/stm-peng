package com.mainsteam.stm.platform.mybatis.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: AbstractDialect.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
public abstract class AbstractDialect {
	private static final Pattern FROM_PATTERN = Pattern.compile(
			"from[\\s\\S]+", Pattern.CASE_INSENSITIVE);
	private static final String COUNT_STRING = "select count(*) ";
	private static final Pattern GROUP_BY = Pattern.compile(
			"\\sgroup\\sby\\s[^)]+$", Pattern.CASE_INSENSITIVE);
	private static final Pattern DISTINCT_PATTERN = Pattern.compile(
			"distinct\\(.*\\)", Pattern.CASE_INSENSITIVE);
	public static final String ORDER_BY = "^[a-zA-Z,_]+$";

	public abstract String getPageSql(String originalSql, Page<?, ?> page);

	public static String getCountSql(String originalSql) {
		final Matcher m = GROUP_BY.matcher(originalSql);
		if (m.find()) {
			return "select count(1) from (" + originalSql + ") page";
		}
		final Matcher matcher = FROM_PATTERN.matcher(originalSql);
		matcher.find();
		final Matcher matcherDistinct = DISTINCT_PATTERN.matcher(originalSql);
		if(matcherDistinct.find()){
			return COUNT_STRING.replace("*", matcherDistinct.group()) + matcher.group();
		}
		return COUNT_STRING + matcher.group();
	}
}
