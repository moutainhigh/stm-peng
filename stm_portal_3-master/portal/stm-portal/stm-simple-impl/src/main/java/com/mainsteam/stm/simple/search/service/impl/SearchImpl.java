package com.mainsteam.stm.simple.search.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.simple.search.dao.ISearchDao;
import com.mainsteam.stm.simple.search.vo.SearchConditionsVo;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: SearchImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
@SuppressWarnings("unchecked")
@Service
public class SearchImpl implements ISearchApi,InstancelibListener {
	
	@Autowired
	private ResourceInstanceService instanceService;
	
	@Autowired
	private ISearchDao searchDao;
	
	@Autowired
	private IKnowledgeZipApi zipApi;
	
	@Autowired
	private AlarmEventService alarmService;
	
	private Logger logger = Logger.getLogger(SearchImpl.class);

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchResource(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchResource(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_RESOURCE);
		if(searchDao.checkResourceBizRelIsExist(model)<=0){
			return searchDao.saveResourceBizRel(model);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchResource(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchResource(long resourceId) {
		return searchDao.delAllByResource(resourceId);
	}
	
	public int delCancelMonitorResource(ResourceBizRel model){
		model.setType(ILoginUser.RIGHT_RESOURCE);
		return searchDao.delResourceBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchBiz(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchBiz(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_BIZ);
		return changeModelToRels(model);
	}
	
	private int changeModelToRels(ResourceBizRel model){
		List<ResourceBizRel> rels = new ArrayList<ResourceBizRel>();
		for(Long resourceId : model.getResourceIds()){
			ResourceBizRel rel;
			try {
				rel = (ResourceBizRel)model.clone();
				rel.setResourceId(resourceId);
				if(searchDao.checkResourceBizRelIsExist(rel)<=0){
					rels.add(rel);
				}
			} catch (CloneNotSupportedException e) {
				logger.error("saveSearchBiz:  " + e.getMessage());
			}
			
		}
		if(rels.size()>0){
			return searchDao.saveResourceBizRels(rels);
		}else{
			return 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchBiz(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchBiz(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_BIZ);
		return searchDao.delResourceBizRels(model);
	}

	
	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchToto(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchTopo(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_TOPO);
		return changeModelToRels(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchToto(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchTopo(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_TOPO);
		return searchDao.delResourcesBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchAlarm(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchAlarm(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_ALARM);
		if(searchDao.checkResourceBizRelIsExist(model)<=0){
			return searchDao.saveResourceBizRel(model);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchAlarm(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchAlarm(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_ALARM);
		return searchDao.delResourceBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchNetflow(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchNetflow(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_NET_FLOW);
		if(searchDao.checkResourceBizRelIsExist(model)<=0){
			return searchDao.saveResourceBizRel(model);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchNetflow(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchNetflow(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_NET_FLOW);
		return searchDao.delResourceBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchConfigFile(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchConfigFile(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_CONFIG_FILE);
		if(searchDao.checkResourceBizRelIsExist(model)<=0){
		return searchDao.saveResourceBizRel(model);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchConfigFile(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchConfigFile(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_CONFIG_FILE);
		return searchDao.delResourceBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchInspect(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchInspect(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_PLAN);
		return changeModelToRels(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchInspect(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchInspect(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_PLAN);
		return searchDao.delResourceBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#saveSearchReport(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int saveSearchReport(ResourceBizRel model) {
		if(model!=null && model.getBizId()!=null && model.getResourceIds()!=null){
			List<ResourceBizRel> rels = new ArrayList<ResourceBizRel>();
			ResourceBizRel bizRel = null;
			for (long rid : model.getResourceIds()) {
				bizRel = new ResourceBizRel();
				bizRel.setType(ILoginUser.RIGHT_REPORT);
				bizRel.setBizId(model.getBizId());
				bizRel.setNav(model.getNav());
				bizRel.setResourceId(rid);
				if(rels.contains(bizRel)){
					continue;
				}
				if(searchDao.checkResourceBizRelIsExist(bizRel)<=0){
					rels.add(bizRel);
				}
			}
			if(rels.size()>0){
				return searchDao.saveResourceBizRels(rels);
			}
		}
		return 0;
		
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#delSearchReport(com.mainsteam.stm.simple.search.bo.ResourceBizRel)
	 */
	@Override
	public int delSearchReport(ResourceBizRel model) {
		model.setType(ILoginUser.RIGHT_REPORT);
		return searchDao.delReportResourceBizRel(model);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#search(com.mainsteam.stm.simple.search.vo.SearchConditionsVo)
	 */
	@Override
	public List<ResourceBizRel> search(Page<ResourceBizRel, SearchConditionsVo> page) {
		SearchConditionsVo condition = page.getCondition();
		if(condition.getResourceIds()==null||condition.getResourceIds().size()==0){
			return Collections.EMPTY_LIST;
		}
		searchDao.search(page);
		return page.getDatas();
	}
	
	private File getFile(String filePath) throws IOException{
		File file = new File(filePath);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

/*	@Value("${oc.file.server_ip}")
	private String serverIP;
	private String rootPath = "";
	
	@PostConstruct
	@Deprecated
	public void init(){
		rootPath+="\\\\"+serverIP+"\\fs\\resLog\\log.log";
	}*/
	
	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#initResource(com.mainsteam.stm.simple.search.vo.SearchConditionsVo)
	 */
	@Override
	public Map<String, Object> initResource(SearchConditionsVo condition) {
		List<ResourceInstance> resourceInstances = null;
		List<Long> resourceIds =  new ArrayList<Long>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<Long, Object> resourceMap = new HashMap<Long, Object>();
		long getBeginTime=System.currentTimeMillis();
		try {
			resourceInstances =  instanceService.getAllParentInstance();
		} catch (InstancelibException e) {
			logger.error("获取资源实例失败，请sever组查找 search ： " + e.getMessage());
		}
		
		long getEndTime=System.currentTimeMillis();
		if(resourceInstances==null||resourceInstances.size()==0){
			return result;
		}
		String keyword = condition.getKeyword();
		long beginTime=System.currentTimeMillis();
		
		if(condition.getIsIP()){
			for(ResourceInstance instance : resourceInstances){
				if(keyword!=null&&keyword.equals(instance.getShowIP())){
					resourceIds.add(instance.getId());
					resourceMap.put(instance.getId(), instance.getShowIP());
				}
			}
			
		}else{
			for(ResourceInstance instance : resourceInstances){
				if(StringUtils.isNotEmpty(instance.getName())&&instance.getName().toLowerCase().indexOf(keyword.toLowerCase())!=-1){
					resourceIds.add(instance.getId());
					resourceMap.put(instance.getId(), instance.getName());
				}
			}
		}
		
		long endTime=System.currentTimeMillis();
		
//		String getLog="记录时间："+DateUtil.format(new Date())+"获取资源时长（ms）："+(getEndTime - getBeginTime)+"\r\n";
//		String resLog="记录时间："+DateUtil.format(new Date())+"过滤资源时长（ms）："+(endTime-beginTime)+"\r\n";
		
		try {
			logger.info("记录时间："+DateUtil.format(new Date())+"获取资源时长（ms）："+(getEndTime - getBeginTime)+"\r\n");
			logger.info("记录时间："+DateUtil.format(new Date())+"过滤资源时长（ms）："+(endTime-beginTime)+"\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("resourceIds", resourceIds);
		result.put("resource", resourceMap);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#searchKnowledge(com.mainsteam.stm.platform.mybatis.plugin.pagination.Page)
	 */
	@Override
	public List<KnowledgeBo> searchKnowledge(SearchConditionsVo conditions, Page<Byte, Byte> page) {
		String keyword = conditions.getKeyword();
		if(StringUtils.isNotBlank(keyword)){
			return zipApi.searchKnowledge(conditions.getKeyword(), page);
		}else{
			return Collections.EMPTY_LIST;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.simple.search.api.ISearchApi#searchAlarm(com.mainsteam.stm.platform.mybatis.plugin.pagination.Page)
	 */
	
	@Override
	public List<AlarmEvent> searchAlarm(Page<ResourceBizRel, SearchConditionsVo> page) {
		AlarmEventQuery query = new AlarmEventQuery();
		List<String> ids = new ArrayList<String>();
		List<Long> resourceIds = page.getCondition().getResourceIds();
		if(resourceIds==null||resourceIds.size()==0){
			return Collections.EMPTY_LIST;
		}
		for(Long id : page.getCondition().getResourceIds()){
			ids.add(String.valueOf(id));
		}
		query.setSourceIDes(ids);
		Page<AlarmEvent, AlarmEventQuery> result = alarmService.findAlarmEvent(query, (int)page.getStartRow(), (int)page.getRowCount());
		return result.getDatas();
	}

	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType()==EventEnum.INSTANCE_DELETE_EVENT){
			// deleteIds 需要删除的资源实例Id 集合
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			//模块实现自己删除资源实例相关操作逻辑
			for (Long id : deleteIds) {
				this.delSearchResource(id);
			}
			
		}
	}
}


