package com.mainsteam.stm.portal.vm.api;

import java.util.List;

import com.mainsteam.stm.portal.vm.bo.TopNWorkBench;
import com.mainsteam.stm.portal.vm.bo.VmCategoryResourceBo;

/**
 * <li>文件名称: TopNVmService.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月20日
 * @author   caoyong
 */
public interface TopNVmService {
	/**
	 * 获取topN柱状图数据
	 * @param objects 查询参数
	 * @return
	 */
	Object getTopNGraphData(String sortMetric,String resourceIds,int topNum,final String sortOrder) throws Exception;
	
	/**
	 * 根据用户id获取用户工作台
	 * @param userId
	 * @return
	 */
	List<TopNWorkBench> getTopNUserWorkBenchs(Long userId);
	
	/**
	 * 设置用户要显示的工作台内容
	 * @param uws
	 * @return 返回设置成功的条数
	 */
	int setTopNUserWorkBenchs(Long userId,List<TopNWorkBench> uws);
	
	/**
	 * 删除一个用户工作台
	 * @param uw
	 * @return 返回删除成功的条数
	 */
	int delTopNUserWorkBench(TopNWorkBench uw);
	/**
	 * @return
	 */
	int updateTopNSetting(TopNWorkBench t);
	
	List<TopNWorkBench> getAllWorkBench();
	
	TopNWorkBench getUserWorkBenchById(TopNWorkBench wb);
	
	List<VmCategoryResourceBo> getVmCategory();
	List<VmCategoryResourceBo> getVmMetricByCategoryId(String resourceId);
	List<VmCategoryResourceBo> getVmInstances(String resourceId,Long domainId,String nameOrIp) throws Exception;
	
}
