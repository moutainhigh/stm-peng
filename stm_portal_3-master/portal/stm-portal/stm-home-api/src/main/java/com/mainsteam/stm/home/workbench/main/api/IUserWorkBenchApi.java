package com.mainsteam.stm.home.workbench.main.api;

import java.util.List;

import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: IUserWorkBenchApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 用户工作台展示定制</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月10日
 * @author   ziwenwen
 */
public interface IUserWorkBenchApi {
	
	/**
	 * 根据用户id获取用户工作台
	 * @param userId
	 * @return
	 */
	List<WorkBench> getUserWorkBenchs(Long userId);
	
	/**
	 * 设置用户要显示的工作台内容
	 * @param uws
	 * @return 返回设置成功的条数
	 */
	int setUserWorkBenchs(Long userId,List<WorkBench> uws);
	
	/**
	 * 删除一个用户工作台
	 * @param uw
	 * @return 返回删除成功的条数
	 */
	int delUserWorkBench(WorkBench uw);

	/**
	 * 设置用户工作台的扩展字段，返回设置成功的条数
	 * @param wb
	 * @return
	 */
	int setExt(WorkBench wb);
	
	int insertUserAllWorkbenchs(Long userId);
	int delUserAllWorkbenchs(Long userId);
	/**
	 * 通过用户ID删除用户的工作台信息
	 * @param ids
	 * @return
	 * @author	ziwen
	 * @date	2019年10月25日
	 */
	int delUserAllWorkbenchs(Long []ids);
	
	List<WorkBench> getAllWorkBench();
	
	WorkBench getUserWorkBenchById(WorkBench wb);
	
	WorkBench getWorkBenchById(Long id);
	
	List<Long> getInstanceIdByGroupId(long groupId, ILoginUser user);
	List<Long> getInstanceIdByGroupId(long groupId, ILoginUser user,String type,Long... domainId);
	int setUserWorkBenchByDefaultId(WorkBench bench);
}


