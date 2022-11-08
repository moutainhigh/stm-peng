package com.mainsteam.stm.simple.search.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.simple.search.vo.SearchConditionsVo;

/**
 * <li>文件名称: ISearchApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public interface ISearchApi {
	
	/**
	 * 保存资源管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchResource(ResourceBizRel model);
	
	/**
	 * 删除资源管理数据，，删除表中所有资源相关的数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchResource(long resourceId);
	
	/**
	* @Title: delCancelMonitorResource
	* @Description: 取消监控调
	* @return  int
	* @throws
	*/
	int delCancelMonitorResource(ResourceBizRel model);
	
	/**
	 * 保存业务管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchBiz(ResourceBizRel model);
	
	/**
	 * 删除业务管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchBiz(ResourceBizRel model);
	/**
	 * 保存拓扑管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchTopo(ResourceBizRel model);
	
	/**
	 * 删除拓扑管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchTopo(ResourceBizRel model);
	/**
	 * 保存告警管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchAlarm(ResourceBizRel model);
	
	/**
	 * 删除告警管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchAlarm(ResourceBizRel model);
	/**
	 * 保存流量分析查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchNetflow(ResourceBizRel model);
	
	/**
	 * 删除流量分析查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchNetflow(ResourceBizRel model);
	/**
	 * 保存配置文件管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchConfigFile(ResourceBizRel model);
	/**
	 * 删除配置文件管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchConfigFile(ResourceBizRel model);
	/**
	 * 保存巡检管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchInspect(ResourceBizRel model);
	/**
	 * 删除巡检管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchInspect(ResourceBizRel model);
	/**
	 * 保存报表管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveSearchReport(ResourceBizRel model);
	/**
	 * 删除报表管理查询数据
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delSearchReport(ResourceBizRel model);

	/**
	 * 搜索功能
	 * @param page
	 * @return
	 * @author	ziwen
	 * @date	2019年10月29日
	 */
	List<ResourceBizRel> search(Page<ResourceBizRel, SearchConditionsVo> page);
	/**
	 * 搜索知识
	 * @param page
	 * @return
	 * @author	ziwen
	 * @date	2019年11月24日
	 */
	List<KnowledgeBo> searchKnowledge(SearchConditionsVo conditions, Page<Byte, Byte> page);

	/**
	 * 初始化资源
	 * @param condition
	 * @return
	 * @author	ziwen
	 * @date	2019年11月5日
	 */
	Map<String, Object> initResource(SearchConditionsVo condition);

	/**
	 * @param page
	 * @return
	 * @author	ziwen
	 * @date	2019年11月29日
	 */
	List<AlarmEvent> searchAlarm(Page<ResourceBizRel, SearchConditionsVo> page);

}


