package com.mainsteam.stm.portal.vm.dao;

import java.util.List;

import com.mainsteam.stm.portal.vm.bo.TopNWorkBench;

/**
 * <li>文件名称: ITopNUserWorkbenchDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月3日
 * @author   caoyong
 */
public interface ITopNUserWorkbenchDao {

	public List<TopNWorkBench> getTopNUserWorkBenchs(Long userId);

	public TopNWorkBench getTopNUserWorkBenchById(TopNWorkBench wb);

	public int delTopNUserWorkBench(Long userId);
	public int batchInsertWorkBenchs(List<TopNWorkBench> uws);

	/**
	 * 通过用户id删除用的工作台信息
	 * @param userId
	 * @return
	 */
	public int delTopNUsersWorkBench(Long[] userId);

	public int delSingleUserWorkBench(TopNWorkBench um);

	public int setExt(TopNWorkBench wb);
	
	public int updateTopNSetting(TopNWorkBench wb);
	
	List<TopNWorkBench> getUserBenchIdByResourceId(Long id);
	
	void setUserBenchResourceIdById(TopNWorkBench workBench);
	
	List<TopNWorkBench> getAllWorkBench();
	
}
