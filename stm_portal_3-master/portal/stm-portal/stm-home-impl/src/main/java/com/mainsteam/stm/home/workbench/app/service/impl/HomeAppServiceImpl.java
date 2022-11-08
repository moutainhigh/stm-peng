package com.mainsteam.stm.home.workbench.app.service.impl;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.home.workbench.app.api.IHomeAppApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;

/**
 * <li>文件名称: HomeAppServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月18日
 * @author   zhangjunfeng
 */
public class HomeAppServiceImpl implements IHomeAppApi {

	private IResourceDetailInfoApi resourceDetailInfoApi;
	
	@Override
	public Map<String, Object> getHomeAppData(String resourceId) {
		if(resourceId!=null && !resourceId.equals("")){
			Map<String, Object> resourceDetail = resourceDetailInfoApi.getResourceDetailInfo(Long.valueOf(resourceId));
			return resourceDetail.isEmpty()?null:resourceDetail;
		}else{
			return null;
		}
		
	}

	
	@Override
	public List<Map<String, Object>> getMetricList(String resourctId){
		List<Map<String, Object>> metricList = resourceDetailInfoApi.getMetricByType(resourctId==null?0l:Long.valueOf(resourctId), "PerformanceMetric",false);
		return metricList;
	}
	
	
	/**
	 * get and set 
	 * */

	public void setResourceDetailInfoApi(IResourceDetailInfoApi resourceDetailInfoApi) {
		this.resourceDetailInfoApi = resourceDetailInfoApi;
	}
}
