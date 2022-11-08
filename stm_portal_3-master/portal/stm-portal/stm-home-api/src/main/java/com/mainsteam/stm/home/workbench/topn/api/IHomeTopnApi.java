package com.mainsteam.stm.home.workbench.topn.api;


import java.util.List;
import java.util.Map;


/**
 * <li>文件名称: HomeTopnApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月17日
 * @author   zhangjunfeng
 */
public interface IHomeTopnApi {

	/** 
	* @Title: getHomeTopnData 
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param resource 要统计的资源类型
	* @param metricId 要统计的资源指标
	* @param resourceIds 要统计的资源实例ID集合
	* @return
	* @return List<HomeTopnBo>    返回类型 
	* @throws 
	*/
	public Map<String,Object> getHomeTopnData(String metricId,List<Long> resourceIds,int top,String sortMethod,String showPattern, boolean isSubMetrics);
}
