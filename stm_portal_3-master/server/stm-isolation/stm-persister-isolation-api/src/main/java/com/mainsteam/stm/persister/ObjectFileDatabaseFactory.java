/**
 * 
 */
package com.mainsteam.stm.persister;

import java.util.Map;

/**
 * @author ziw
 * 
 */
public interface ObjectFileDatabaseFactory {
	/**
	 * 创建模块的持久化对象服务
	 * 
	 * @param moduleKey
	 *            模块的key.只能是英文字母或数字。大小写不敏感
	 * @return ObjectFileDatabase<T> 持久化服务
	 */
	<T> ObjectFileDatabase<T> createObjectFileDatabase(String moduleKey);

	/**
	 * 创建模块的持久化对象服务
	 * 
	 * @param moduleKey
	 *            模块的key.只能是英文字母。大小写不敏感
	 * @param alias
	 *            别名映射
	 * @return ObjectFileDatabase<T> 持久化服务
	 */
	<T> ObjectFileDatabase<T> createObjectFileDatabase(String moduleKey,
			Map<String, Class<?>> alias);
}
