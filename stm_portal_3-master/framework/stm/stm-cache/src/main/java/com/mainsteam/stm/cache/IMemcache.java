package com.mainsteam.stm.cache;

import java.util.Collection;

/**
 * 
 * <li>文件名称: IMemcache.java.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月19日
 * @author wangxinghao
 */
public interface IMemcache<T> {
	
	public String getName();

	// 存放对象
	/**
	 * 存放对象到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 */
	public boolean set(String key, T value);
	
	/**
	 * 添加对象到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 */
	public boolean add(String key, T value);

	/**
	 * 存放对象到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @param expiry
	 *            过期时间，单位 (秒)
	 */
	public boolean set(String key, T value, long expiry);

	/**
	 * 存放对象集合到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 */
	public boolean setCollection(String key, Collection<T> collections);

	/**
	 * 存放对象集合到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @param expiry
	 *            过期时间，单位 (秒)
	 */
	public boolean setCollection(String key, Collection<T> collections,
			long expiry);

	/**
	 * 更新缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 */
	public boolean update(String key, T value);

	/**
	 * 更新缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @param expiry
	 *            过期时间，单位 (秒)
	 */
	public boolean update(String key, T value, long expiry);

	/**
	 * 更新对象集合到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 */
	public boolean updateCollection(String key, Collection<T> collections);

	/**
	 * 更新对象集合到缓存
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @param expiry
	 *            过期时间，单位 (秒)
	 */
	public boolean updateCollection(String key, Collection<T> collections,
			long expiry);

	/**
	 * 获取缓存对象
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @return 缓存对象
	 */
	public T get(String key);

	/**
	 * 获取缓存对象集合
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @return 缓存对象集合
	 */
	public Collection<T> getCollection(String key);

	/**
	 * 删除缓存对象
	 * 
	 * @param key
	 *            缓存的唯一标示
	 */
	public boolean delete(String key);

	/**
	 * load,添加判断，如果当前对象存在就不添加，如果不存在就add
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @return
	 */
	public boolean load(String key, T value);

	/**
	 * load,添加判断，如果当前对象存在就不添加，如果不存在就add
	 * 
	 * @param key
	 *            缓存的唯一标示
	 * @param value
	 *            缓存对象
	 * @param expiry
	 *            过期时间，单位 (秒)
	 * @return
	 */
	public boolean load(String key, T value, long expiry);

	/**
	 * 验证缓存对象是否可用
	 * 
	 * @return
	 */
	public boolean isActivate();

	/**
	 * 获取查询的个数。
	 * 
	 * @return
	 */
	public long getGetCount();

	/**
	 * 获取查询结果为null的个数
	 * 
	 * @return
	 */
	public long getGetmissCount();

	/**
	 * 获取更新的个数
	 * 
	 * @return
	 */
	public long getUpdateCount();

	/**
	 * 获取更新失败的次数
	 * 
	 * @return
	 */
	public long getUpdateMissCount();

	/**
	 * 获取删除的次数
	 * 
	 * @return
	 */
	public long getDeleteCount();

	/**
	 * 获取删除失败的次数
	 * 
	 * @return
	 */
	public long getDeleteMissCount();
}
