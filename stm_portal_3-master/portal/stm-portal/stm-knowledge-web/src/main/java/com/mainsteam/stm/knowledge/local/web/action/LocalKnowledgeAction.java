/**
 * 
 */
package com.mainsteam.stm.knowledge.local.web.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeResolveApi;
import com.mainsteam.stm.knowledge.local.api.ILocalKnowledgeApi;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.type.api.IKnowledgeTypeApi;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: LocalKnowledgeAction</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日 下午2:59:28
 * @author   俊峰
 */
@Controller
@RequestMapping("knowledge/local")
public class LocalKnowledgeAction extends BaseAction {

	@Autowired
	private ILocalKnowledgeApi localKnowledgeApi;
	
	@Autowired
	@Qualifier("knowledgeResolveApi")
	private IKnowledgeResolveApi knowledgeResolveApi;
	
	@Autowired
	private IKnowledgeTypeApi knowledgeTypeApi;
	
	@Autowired
	private IFileClientApi fileClient;
	

	@RequestMapping("queryLocalKnowledge")
	public JSONObject queryLocalKnowledge(Page<KnowledgeBo, KnowledgeBo> page,String search){
		localKnowledgeApi.queryLocalKnowledgeBos(page,search);
		return toSuccess(page);
	}
	
	@RequestMapping("saveLocalKnowledge")
	public JSONObject saveLocalKnowledge(KnowledgeBo knowledge,String parentCode,String resolvesStr){
		String code = knowledgeTypeApi.createKnowledgeTypeCodeByMetric(knowledge.getKnowledgeTypeCode(), parentCode);
		knowledge.setKnowledgeTypeCode(code);
		knowledge.setCreateUserId(getLoginUser().getId());
		knowledge = localKnowledgeApi.addKnowledge(knowledge);
		List<KnowledgeResolveBo> resolveList = JSONObject.parseArray(resolvesStr,KnowledgeResolveBo.class);
		for (KnowledgeResolveBo resolve : resolveList) {
			resolve.setKnowledgeId(knowledge.getId());
			this.saveKnowledgeResolve(resolve);
		}
		return toSuccess(knowledge.getId()>0?true:false);
	}
	
	@RequestMapping("updateLocalKnowledge")
	public JSONObject updateLocalKnowledge(KnowledgeBo knowledge,String parentCode,String resolvesStr){
		String code = knowledgeTypeApi.createKnowledgeTypeCodeByMetric(knowledge.getKnowledgeTypeCode(), parentCode);
		knowledge.setKnowledgeTypeCode(code);
		int result = localKnowledgeApi.updateKnowledge(knowledge);
		List<KnowledgeResolveBo> resolveList = JSONObject.parseArray(resolvesStr,KnowledgeResolveBo.class);//更新后的解决方案列表
		knowledge = localKnowledgeApi.queryKnowledge(knowledge.getId());
		List<KnowledgeResolveBo> thisResolves = knowledge.getResolves();//当前知识的解决方案列表
		/**
		 * 比较原始解决方案列表与更新后解决方案列表，找出更新后须要删除的列表
		 * */
		List<KnowledgeResolveBo> delResolves = new ArrayList<KnowledgeResolveBo>();//准备删除的解决方案
		List<KnowledgeResolveBo> updateResolve = new ArrayList<KnowledgeResolveBo>();//准备更新的解决方案
		for (KnowledgeResolveBo thisResolve : thisResolves) {
			boolean flag = true;
			for (KnowledgeResolveBo nowResolve : resolveList) {
				if(thisResolve.getId()==nowResolve.getId()){
					flag = false;
				}
			}
			if(flag)delResolves.add(thisResolve);
		}
		//比较原始解决方案，区分出新增的和需要更新的解决方案
		List<KnowledgeResolveBo> saveResolves = new ArrayList<KnowledgeResolveBo>();//准备添加的解决方案
		for (KnowledgeResolveBo nowResolve : resolveList) {
			boolean flag = true;
			for (KnowledgeResolveBo thisResolve : thisResolves) {
				if(thisResolve.getId()==nowResolve.getId()){
					flag = false;
				}
			}
			if (flag){
				saveResolves.add(nowResolve);
			}else{
				updateResolve.add(nowResolve);
			}
		}
		for(KnowledgeResolveBo delResolve : delResolves){
			knowledgeResolveApi.deleteKnowledgeResolve(delResolve.getId());
		}
		for (KnowledgeResolveBo saveResolve : saveResolves) {
			saveResolve.setKnowledgeId(knowledge.getId());
			this.saveKnowledgeResolve(saveResolve);
		}
		for (KnowledgeResolveBo updateResolveBo : updateResolve) {
			updateResolveBo.setKnowledgeId(knowledge.getId());
			this.updateKnowledgeResolves(updateResolveBo);
		}
		return toSuccess(result>0?true:false);
	}
	
	@RequestMapping("removeLocalKnowledge")
	public JSONObject removeLocalKnowledge(long[] ids){
		int result = localKnowledgeApi.batchDelKnowledge(ids);
		return toSuccess(result>0?true:false);
	}
	
	@RequestMapping("getLocalknowledge")
	public JSONObject getLocalknowledge(long id){
		KnowledgeBo knowledge = localKnowledgeApi.queryKnowledge(id);
		return toSuccess(knowledge);
	}
	
	
	/**
	* @Title: saveKnowledgeResolve
	* @Description: 新增故障解决方案
	* @param resolve 解决方案信息
	* @param fileIds 解决方案附件ID列表
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("saveKnowledgeResolve")
	public JSONObject saveKnowledgeResolve(KnowledgeResolveBo resolve){
		try {
			//将attachments转换为KnowledgeAttachmentBo对象
			List<KnowledgeAttachmentBo> attachmentBos = new ArrayList<KnowledgeAttachmentBo>();
			int isScript = 0;
			if(resolve.getFileIds()!=null && resolve.getFileIds().length>0){
				Arrays.sort(resolve.getFileIds());
				KnowledgeAttachmentBo attachment = null;
				for(int i=0;i<resolve.getFileIds().length;i++){
					String fileId = resolve.getFileIds()[i];
					if(!StringUtils.isEmpty(fileId)){
						attachment = new KnowledgeAttachmentBo();
						FileModel model = fileClient.getFileModelByID(Long.valueOf(fileId));
						attachment.setFileId(model.getId());
						attachment.setFileName(model.getFileName());
						attachment.setUploadDate(model.getCreateDatetime());
						attachment.setSort(i+1);
						attachmentBos.add(attachment);
						//遍历附件文件扩展名，验证解决方案为脚本还是文档
						if(KnowledgeResolveBo.SCRIPT_FILE_EXT.contains(model.getFileExt())){
							isScript = 1;
						}
					}
				}
			}
			resolve.setIsScript(isScript);
			resolve.setResolveAttachments(attachmentBos);
			return toSuccess(knowledgeResolveApi.insertKnowledgeResolve(resolve));
		} catch (Exception e) {
			e.printStackTrace();
			return toSuccess(false);
		}
		
	}
	
	public JSONObject updateKnowledgeResolves(KnowledgeResolveBo resolve){
		try {
			//将attachments转换为KnowledgeAttachmentBo对象
			List<KnowledgeAttachmentBo> attachmentBos = new ArrayList<KnowledgeAttachmentBo>();
			int isScript = 0;
			if(resolve.getFileIds()!=null && resolve.getFileIds().length>0){
				Arrays.sort(resolve.getFileIds());
				KnowledgeAttachmentBo attachment = null;
				for(int i=0;i<resolve.getFileIds().length;i++){
					String fileId = resolve.getFileIds()[i];
					attachment = new KnowledgeAttachmentBo();
					FileModel model = fileClient.getFileModelByID(Long.valueOf(fileId));
					attachment.setFileId(model.getId());
					attachment.setFileName(model.getFileName());
					attachment.setUploadDate(model.getCreateDatetime());
					attachment.setSort(i+1);
					attachmentBos.add(attachment);
					//遍历附件文件扩展名，验证解决方案为脚本还是文档
					if(KnowledgeResolveBo.SCRIPT_FILE_EXT.contains(model.getFileExt())){
						isScript = 1;
					}
				}
			}
			resolve.setIsScript(isScript);
			resolve.setResolveAttachments(attachmentBos);
			return toSuccess(knowledgeResolveApi.updateKnowledgeResolve(resolve));
		} catch (Exception e) {
			e.printStackTrace();
			return toSuccess(false);
		}
	}
	
	/**
	* @Title: queryFileModelList
	* @Description: 通过文件ID集合获取，文件model集合
	* @param fileIds
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryFileModelList")
	public JSONObject queryFileModelList(long[] fileIds){
		List<KnowledgeAttachmentBo> attachments = new ArrayList<KnowledgeAttachmentBo>();
		try {
			if(fileIds!=null && fileIds.length>0){
				KnowledgeAttachmentBo attachment = null;
				for (long fileId : fileIds) {
					FileModel model = fileClient.getFileModelByID(fileId);
					attachment = new KnowledgeAttachmentBo();
					attachment.setFileId(model.getId());
					attachment.setFileName(model.getFileName());
					attachments.add(attachment);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toSuccess(attachments);
	}
	
	/**
	* @Title: getDownloadAddr
	* @Description: 获取云端下载地址
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getDownloadAddr")
	public JSONObject getDownloadAddr(){
		return toSuccess(localKnowledgeApi.getFaultKnowledgeDownloadAddr());
	}
	
	/**
	* @Title: getKnowledgeResolveById
	* @Description: 通过解决方案ID获取解决方案
	* @param id
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getKnowledgeResolveById")
	public JSONObject getKnowledgeResolveById(Long id){
		KnowledgeResolveBo resolve = null;
		if(null!=id){
			resolve = knowledgeResolveApi.getResolve(id);
		}
		return toSuccess(resolve);
	}
}
