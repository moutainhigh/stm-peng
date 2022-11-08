package com.mainsteam.stm.portal.alarm.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.confirm.AlarmConfirmService;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.AlarmNotify;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
//import com.mainsteam.stm.instancelib.RelationshipService;
import com.mainsteam.stm.instancelib.exception.InstancelibRelationException;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeResolveApi;
import com.mainsteam.stm.knowledge.local.api.ILocalKnowledgeApi;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;
import com.mainsteam.stm.knowledge.type.api.IKnowledgeTypeApi;
import com.mainsteam.stm.knowledge.type.bo.KnowledgeTypeBo;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.portal.alarm.api.AlarmApi;
import com.mainsteam.stm.portal.alarm.bo.AlarmKnowledgeBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmKnowledgePageBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmNotifyPageBo;
import com.mainsteam.stm.portal.alarm.bo.AlarmPageBo;

/**
 * <li>文件名称: AlarmImpl.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月
 * @author xhf
 */
public class AlarmImpl implements AlarmApi {
	private static final Logger logger = LoggerFactory.getLogger(AlarmImpl.class);

	@Resource
	private AlarmEventService alarmEventService;
	
	@Resource
	private AlarmNotifyService alarmNotifyService;
	
//	@Resource
//	private RelationshipService relationshipService;
	
	@Resource
	private  ILocalKnowledgeApi localKnowledgeApi;
	
	@Resource
	private IKnowledgeResolveApi knowledgeResolveApi; 
	
	@Resource
	private IKnowledgeTypeApi knowledgeTypeApi;
	
	@Resource
	private IFileClientApi fileClient;
	@Resource
	private AlarmConfirmService confirmService;
	@Resource
	private ISystemConfigApi configApi;
	
	private final static String ALL_ALARM = "0";
	private final static String UNCOVER_ALARM = "1";
	private final static String RECOVERED_ALARM = "2";
	/**
	 * 告警分页查询
	 * @return
	 */
	@Override
	public AlarmPageBo getAlarmList(long startRow, long rowNumber,
			AlarmEventQuery2 condition) {
		AlarmEvent alarmEvent= new AlarmEvent();
		
		Page<AlarmEvent, AlarmEventQuery2> page =	
				alarmEventService.queryAlarmEvent(condition,  (int)startRow, (int)rowNumber);
				//alarmEventService.findAlarmEvent(condition,  (int)startRow, (int)rowNumber);
		AlarmPageBo alarmPageBo = new AlarmPageBo();
		alarmPageBo.setStartRow(page.getStartRow());
		alarmPageBo.setRowCount(page.getRowCount());
		alarmPageBo.setTotalRecord(page.getTotalRecord());
		alarmPageBo.setRows(page.getDatas());
		
		return alarmPageBo;
	}
	


	/**
	 * 通过告警ID查询已恢复告警对象
	 * @param alarmId
	 * @param alarmType 0、所有 1、未恢复 2、已恢复
	 * @return
	 */
	public AlarmEvent getAlarmById(long alarmId, String alarmType) {
		Boolean paramBoolean = null;
		if(ALL_ALARM.equals(alarmType)){
			paramBoolean = null;
		}else if(UNCOVER_ALARM.equals(alarmType)){
			paramBoolean = false;
		}else if(RECOVERED_ALARM.equals(alarmType)){
			paramBoolean = true;
		}
		AlarmEvent alarmEvent = alarmEventService.getAlarmEvent(alarmId, paramBoolean);
		return alarmEvent;
	}
	/**
	 * 查询告警的影响和根源
	 * @param alarmId
	 * @return
	 */
	public Map<String, String> getAlarmRelationship(long alarmId){
		Map<String, String> relation = new HashMap<String, String>();
		String rootRelation = null, affectedRelation = null;
//		try {
//			rootRelation = relationshipService.getRootRelationByAlarmEventId(alarmId);
//			affectedRelation = relationshipService.getAffectedRelationByAlarmEventId(alarmId);
//		} catch (InstancelibRelationException e) {
//			e.printStackTrace();
//		}
		relation.put("rootRelation", rootRelation);
		relation.put("affectedRelation", affectedRelation);
		return relation;
	}
	
	/**
	 * 通过告警ID查询发送信息
	 * @param alarmId
	 * @return
	 */
	@Override
	public AlarmNotifyPageBo getAlarmNotify(long startRecord,long pageSize,long alarmId) {
		List<AlarmNotify>  alarmNotifyList =  alarmNotifyService.findByAlarmID(alarmId);
		AlarmNotifyPageBo pageBo  = new AlarmNotifyPageBo();
		if(alarmNotifyList == null || alarmNotifyList.size() <= 0){
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(0);
			pageBo.setRowCount(0);
			pageBo.setRows(null);
		}else{
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(alarmNotifyList.size());
			pageBo.setRowCount(pageSize);
			if((startRecord + pageSize) > alarmNotifyList.size()){
				alarmNotifyList =  alarmNotifyList.subList((int)startRecord, alarmNotifyList.size());
			}else{
				alarmNotifyList= alarmNotifyList.subList((int)startRecord, (int)(startRecord + pageSize));
			}
			pageBo.setRows(alarmNotifyList);
		}
		return pageBo;
	}

	/**
	 * 通过告警ID查询知识库关联信息
	 * @param alarmId
	 * @return
	 */
	public List<AlarmKnowledgeBo> queryAlarmKnowledgeList(long alarmId) {
		AlarmEvent  alarmEvent =  alarmEventService.getAlarmEvent(alarmId, false);
		List<AlarmKnowledgeBo> list = new ArrayList<AlarmKnowledgeBo>();
		if(alarmEvent != null){
			FaultBo fb = new FaultBo();
			//通过模型ID+资源类型ID+指标ID组合成故障类型
			fb.setColudyType(alarmEvent.getExt1()+"-"+alarmEvent.getExt0()+"-"+alarmEvent.getExt3());
			fb.setLocalType(alarmEvent.getExt1()+"-"+alarmEvent.getExt3());
			//通过告警获取知识列表
			List<KnowledgeBo>  localBoList = localKnowledgeApi.queryKnowledgeByType(fb);
			if(localBoList != null && localBoList.size() > 0){
				for(KnowledgeBo kb : localBoList){
					AlarmKnowledgeBo akb = new AlarmKnowledgeBo();
					akb.setId(kb.getId());
					KnowledgeTypeBo ktypeBo	 = knowledgeTypeApi.getKnowledgeTypeByCode(kb.getKnowledgeTypeCode());
					if(ktypeBo != null){
						akb.setAlarmKnowledgeName(ktypeBo.getName());
					}
					akb.setAlarmKnowleContent(kb.getSourceContent());
					List<KnowledgeResolveBo> resolveList = knowledgeResolveApi.queryKnowledgeResolves(kb.getId());
					
					if(resolveList != null && resolveList.size() > 0){
						akb.setSchemeId(resolveList.get(0).getId());
						akb.setSchemeName(resolveList.get(0).getResolveTitle());
						list.add(akb);
						for(int i=1;i < resolveList.size(); i++){
							AlarmKnowledgeBo akbs = new AlarmKnowledgeBo();
							akbs.setSchemeId(resolveList.get(i).getId());
							akbs.setSchemeName(resolveList.get(i).getResolveTitle());
							list.add(akbs);
						}
					}else{
						list.add(akb);
					}
					
				}
			}
			
		}
		return list;
	}
	
	
	/**
	 * 知识库关联分页查询
	 * @param startRecord
	 * @param pageSize
	 * @param alarmId
	 * @return
	 */
	public AlarmKnowledgePageBo getAlarmKnowledge(long startRecord,long pageSize,long alarmId) {
		List<AlarmKnowledgeBo>  alarmKnowledgeList = queryAlarmKnowledgeList(alarmId);
		AlarmKnowledgePageBo pageBo  = new AlarmKnowledgePageBo();
		if(alarmKnowledgeList == null || alarmKnowledgeList.size() <= 0){
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(0);
			pageBo.setRowCount(0);
			pageBo.setRows(null);
		}else{
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(alarmKnowledgeList.size());
			pageBo.setRowCount(pageSize);
			if((startRecord + pageSize) > alarmKnowledgeList.size()){
				alarmKnowledgeList =  alarmKnowledgeList.subList((int)startRecord, alarmKnowledgeList.size());
			}else{
				alarmKnowledgeList= alarmKnowledgeList.subList((int)startRecord, (int)(startRecord + pageSize));
			}
			pageBo.setRows(alarmKnowledgeList);
		}
		return pageBo;
	}

	@Override
	public Map<String, String> getSnapShotFile(long snapshotFileId, long recoverFileId) {
		Map<String, String> fileMap = new HashMap<String, String>();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			if(snapshotFileId != 0){
				File file = fileClient.getFileByID(snapshotFileId);
				if(file != null){
					isr = new InputStreamReader(new FileInputStream(file), "utf-8");
					br = new BufferedReader(isr);
					StringBuffer allContent = new StringBuffer("<table class='datagird-btable'>");
					String content;
					while ((content = br.readLine()) != null) {
						allContent.append("<tr class='datagrid-snapshot'><td class='datagrid-snapshotsty'>").append(content).append("</td></tr>");
					}
					allContent.append("</table>");
					isr.close();
					br.close();
					fileMap.put("snapShotContent", allContent.toString());
				}else{
					fileMap.put("snapShotContent", "无快照文件!");
				}
			}else{
				fileMap.put("snapShotContent", "无快照文件。");
			}
		} catch (Exception e) {
			fileMap.put("snapShotContent", "查询快照文件出错!");
			e.printStackTrace();
		} finally {
			try {
				if(isr != null)
					isr.close();
				if(br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if(recoverFileId != 0){
				File file = fileClient.getFileByID(recoverFileId);
				if(file != null){
					isr = new InputStreamReader(new FileInputStream(file), "utf-8");
					br = new BufferedReader(isr);
					StringBuffer allContent = new StringBuffer("<table class='datagird-btable'>");
					String content;
					while ((content = br.readLine()) != null) {
						allContent.append("<tr class='datagrid-snapshot'><td class='datagrid-snapshotsty'>").append(content).append("</td></tr>");
					}
					allContent.append("</table>");
					isr.close();
					br.close();
					fileMap.put("recoverContent", allContent.toString());
				}else{
					fileMap.put("recoverContent", "无恢复结果文件!");
				}
			}else{
				fileMap.put("recoverContent", "无恢复结果文件。");
			}
		} catch (Exception e) {
			fileMap.put("recoverContent", "查询恢复结果文件出错!");
			e.printStackTrace();
		} finally {
			try {
				if(isr != null)
					isr.close();
				if(br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileMap;
	}


	@Override
	public String SureAlarmByIds(Long[] ids) {
//		for (Long id : ids) {
//			System.out.println(id);
//		}
		return null;
	}



	@Override
	public String confirmAlarmEvent(String alarmInfo,String type) {
		String	alarmId=null;
		String	metricId=null;
		String	resourceId=null;
		String result="";
		Boolean isBl=true;
		SystemConfigBo bo=	configApi.getSystemConfigById(1);
		logger.info("getSystemConfigById"+bo);
		if(alarmInfo != null && !"".equals(alarmInfo)){
			JSONArray jsonArray = JSONObject.parseArray(alarmInfo);
			for(int i=0;i<jsonArray.size();i++){
				JSONObject obj =jsonArray.getJSONObject(i);	
				if (StringUtils.isNotEmpty(obj.getString("alarmId"))) {
					alarmId = obj.getString("alarmId");
				}
				if (StringUtils.isNotEmpty(obj.getString("metricId"))) {
						metricId = obj.getString("metricId");
				}
				if (StringUtils.isNotEmpty(obj.getString("resourceId"))) {
						resourceId = obj.getString("resourceId");
				}
				if(type.equals("0")){//告警未恢复
					isBl=false;
				}
				try {
					result=	confirmService.confirmAlarmEventById(alarmId,resourceId, metricId,  isBl);
					if(result.equals("done")){
						continue;
					}
				} catch (Exception e) {
				
					e.printStackTrace();
					logger.error("confirmAlarmEventById", e);
					result="failed";
				}
				
			}
			
			
}
		return result;
		
	}

	
}
