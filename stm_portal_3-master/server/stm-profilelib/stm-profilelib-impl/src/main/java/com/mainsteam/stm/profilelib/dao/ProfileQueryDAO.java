/**
 * 
 */
package com.mainsteam.stm.profilelib.dao;

import java.util.List;

/**
 * @author ziw
 * 
 */
public interface ProfileQueryDAO {
	/**
	 * 查询dcs下相关的策略
	 * 
	 * @param dcsId
	 *            dcsID
	 * @return List<Long> 策略id列表
	 */
	public List<Long> queryProfileByDCS(int dcsId);
}
