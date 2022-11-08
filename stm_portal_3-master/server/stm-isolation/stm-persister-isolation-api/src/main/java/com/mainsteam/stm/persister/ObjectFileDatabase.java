/**
 * 管理持久化对象到文件
 */
package com.mainsteam.stm.persister;

import java.io.IOException;
import java.util.List;

/**
 * @author ziw
 * 
 */
public interface ObjectFileDatabase<T> {
	/**
	 * 保存待持久化对象
	 * 
	 * @param objId
	 *            对象的唯一id
	 * @param obj
	 *            待保存的对象
	 * @return 保存的文件路径
	 * @throws IOException 
	 */
	public String saveObject(String objId, T obj) throws IOException;

	/**
	 * 从文件中加载待持久化对象
	 * 
	 * @param objId
	 *            对象的唯一id
	 * @return 保存的对象
	 */
	public T loadObjectFromId(String objId)throws IOException;

	/**
	 * 加载所有的持久化对象
	 * 
	 * @return 加载持久化对象
	 */
	public List<T> loadAllObjects()throws IOException;
	
	
	/**
	 * 删除某一个对象
	 */
	public void removeObjectById(String objId)throws IOException;
}
