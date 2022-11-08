package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileInfo;

/**
 * 策略列表排序
 * @author Administrator
 *
 */
public interface IProfileSortableBySingleFieldApi {
	/**
	 * <pre>
	 * 根据提供的字段和排序规则进行排序操作
	 * </pre>
	 * @param profileInfos 无序的策略集合
	 * @param field 要排序的字段
	 * @param order 排序的方向 可取 asc：正序 desc逆序
	 * @return
	 */
	List<ProfileInfo> sort(List<ProfileInfo> profileInfos,String field,String order);
	
}
