package com.mainsteam.stm.platform.dao;

import java.util.Collection;
import java.util.List;

/**
 * <li>文件名称: BaseDao1.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月21日
 * @author   ziwenwen
 */
public interface IBaseDao<E> {
	
	/**
	 * 插入一个实体
	 * 
	 * @param entity
	 *            实体对象
	 */
	int insert(E entity);

	/**
	 * 批量插入
	 * 
	 * @param list
	 */
	int batchInsert(List<E> entities);

	/**
	 * 按主键删除记录
	 * 
	 * @param pk
	 *            主键
	 * @return 删除的对象个数
	 */
	int del(Long pk);

	/**
	 * 按主键删除记录
	 * 
	 * @param pk
	 *            主键
	 * @return 删除的对象个数
	 */
	int del(long pk);

	/**
	 * 按主键删除记录
	 * 
	 * @param 主键集合
	 * @return 返回成功删除的条数
	 */
	int batchDel(Long[] pks);

	/**
	 * 按主键删除记录
	 * 
	 * @param 主键集合
	 * @return 返回成功删除的条数
	 */
	int batchDel(long[] pks);

	/**
	 * 修改一个实体对象（UPDAEE一条记录）
	 * 
	 * @param entity
	 *            实体对象
	 * @return 成功修改的对象个数
	 */
	int update(E entity);

	/**
	 * 修改一批实体
	 * 
	 * @return 修改的记录个数
	 */
	int batchUpdate(List<E> entities);

	/**
	 * 查询符合条件的记录数
	 * 
	 * @param entity
	 * @return
	 */
//	int count(E entity);

	/**
	 * 按主键取记录
	 * 
	 * @param primaryKey
	 *            主键值
	 * @return 记录实体对象，如果没有符合主键条件的记录，则返回null
	 */
	E get(Long id);

	/**
	 * 按主键取记录
	 * 
	 * @param primaryKey
	 *            主键值
	 * @return 记录实体对象，如果没有符合主键条件的记录，则返回null
	 */
	E get(long id);

	/**
	 * 按条件查询记录
	 * 
	 * @return 符合条件记录的实体对象的List
	 */
	List<E> select(E entity);
	
	/**
	 * 根据sqlmapper中的sql片段id插入一条数据
	 * @param commond sql片段id，只需要id
	 * @param entity
	 * @return
	 */
	int insert(String commond,E entity);
	
	/**
	 * 根据sqlmapper中的sql片段id批量插入数据
	 * @param commond sql片段id，只需要id
	 * @param entities
	 * @return
	 */
	int batchInsert(String commond,Collection<? extends Object> entities);
	
	/**
	 * 根据sqlmapper中的sql片段id删除一条数据
	 * @param commond sql片段id，只需要id
	 * @param key
	 * @return
	 */
	int del(String commond,Object key);
	
	/**
	 * 根据sqlmapper中的sql片段id批量删除数据
	 * @param commond sql片段id，只需要id
	 * @param keies
	 * @return
	 */
	int batchDel(String commond,Object[] keies);
	
	/**
	 * 根据sqlmapper中的sql片段id修改一条数据
	 * @param commond sql片段id，只需要id
	 * @param entity
	 * @return
	 */
	int update(String commond,E entity);
	
	/**
	 * 根据sqlmapper中的sql片段id批量修改数据
	 * @param commond sql片段id，只需要id
	 * @param entities
	 * @return
	 */
	int batchUpdate(String commond,Collection<E> entities);
	
	/**
	 * 根据sqlmapper中的sql片段id查询数据
	 * @param commond sql片段id，只需要id
	 * @param entity
	 * @return
	 */
	List<E> select(String commond,Object entity);
	
	/**
	 * 根据sqlmapper中的sql片段id获取数据
	 * @param commond sql片段id，只需要id
	 * @param key
	 * @return
	 */
	E get(String commond,Object key);
}
