package com.mainsteam.stm.home.workbench.main.dao;

import java.util.List;

import com.mainsteam.stm.home.workbench.main.bo.WorkBench;

/**
 * <li>文件名称: IUserWorkbenchDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */
public interface IUserWorkbenchDao {

	public List<WorkBench> getUserWorkBenchs(Long userId);

	public WorkBench getUserWorkBenchById(WorkBench wb);

	public int delUserWorkBench(Long userId);
	public int insertUserWorkBenchs(List<WorkBench> uws);

	/**
	 * 通过用户id删除用的工作台信息
	 * @param userId
	 * @return
	 * @author	ziwen
	 * @date	2019年10月25日
	 */
	public int delUsersWorkBench(Long[] userId);

	public int delSingleUserWorkBench(WorkBench um);

	public int setExt(WorkBench wb);
	
	List<WorkBench> getUserBenchIdByResourceId(Long id);
	
	void setUserBenchResourceIdById(WorkBench workBench);
	int setUserBenchDefaultIdById(WorkBench workBench);
}
