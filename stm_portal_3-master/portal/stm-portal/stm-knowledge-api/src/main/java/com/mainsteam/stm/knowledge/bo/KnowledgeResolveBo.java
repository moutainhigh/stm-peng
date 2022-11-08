/**
 * 
 */
package com.mainsteam.stm.knowledge.bo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;


/**
 * <li>文件名称: KnowledgeSolutionBo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 知识问题解决方案</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月29日 下午2:03:56
 * @author   俊峰
 */
public class KnowledgeResolveBo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7881556410181400802L;
	
	/**
	 * 脚本文件扩展名集合
	 */
	public static final String SCRIPT_FILE_EXT = "bat,sh";
	
	/**解决方案ID*/
	private long id;
	/**解决方案类型（[1、脚本]、[0、文档]）*/
	private int isScript;
	/**解决方案名称*/
	private String resolveTitle;
	/**关联知识ID*/
	private long knowledgeId;
	/**解决方案内容*/
	private String resolveContent;
	/**解决方案附件*/
	private List<KnowledgeAttachmentBo> resolveAttachments;
	private String[] fileIds;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getIsScript() {
		return isScript;
	}
	public void setIsScript(int isScript) {
		this.isScript = isScript;
	}
	public String getResolveTitle() {
		return resolveTitle;
	}
	public void setResolveTitle(String resolveTitle) {
		this.resolveTitle = resolveTitle;
	}
	public long getKnowledgeId() {
		return knowledgeId;
	}
	public void setKnowledgeId(long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}
	public String getResolveContent() {
		return resolveContent;
	}
	public void setResolveContent(String resolveContent) {
		this.resolveContent = resolveContent;
	}
	public List<KnowledgeAttachmentBo> getResolveAttachments() {
		return resolveAttachments;
	}
	public void setResolveAttachments(List<KnowledgeAttachmentBo> resolveAttachments) {
		this.resolveAttachments = resolveAttachments;
	}
	public String[] getFileIds() {
		return fileIds;
	}
	public void setFileIds(String[] fileIds) {
		this.fileIds = fileIds;
	}
	
	
	
}
