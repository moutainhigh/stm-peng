package com.mainsteam.stm.home.workbench.app.api;

import java.util.List;
import java.util.Map;


/**
 * <li>文件名称: HomeAppApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月17日
 * @author   zhangjunfeng
 */
public interface IHomeAppApi {

	/** 
	* @Title: getHomeAppData 
	* @Description: TODO(通过应用获取应用的指标数据)
	* @param app
	* @return
	* @return HomeAppBo    返回类型 
	* @throws 
	*/
	public Map<String, Object> getHomeAppData(String resourceId);
	
	/** 
	* @Title: getMetricList 
	* @Description: TODO(通过资源ID获取资源性能指标列表)
	* @param resourctId
	* @return
	* @return List<Map<String,Object>>    返回类型 
	* @throws 
	*/
	public List<Map<String, Object>> getMetricList(String resourctId);
}
