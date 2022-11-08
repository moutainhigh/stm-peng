package com.mainsteam.stm.knowledge.zip.bo;

import java.io.Serializable;

import com.mainsteam.stm.knowledge.zip.annotation.Table;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.zip.bo.CloudyKnowledge.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 知识表</li>
 * <li>其他说明:存储了云端或客户端下载的故障知识</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月14日
 */
@Table(name="CLOUDY_KNOWLEDGE")
public class CloudyKnowledge implements Serializable{

	private static final long serialVersionUID = 3719481201378960696L;
	private String cloudyId;
	private String knowledgeTypeCode;
	private String title;
	private String sourceContent;
	private String resolveContent; 
	private byte[] resolveAttachmentContent1;
	private byte[] resolveAttachmentContent2;
	private byte[] resolveAttachmentContent3;
	private byte[] resolveAttachmentContent4;
	private byte[] resolveAttachmentContent5;
	private String resolveAttachmentName1;
	private String resolveAttachmentName2;
	private String resolveAttachmentName3;
	private String resolveAttachmentName4;
	private String resolveAttachmentName5;
	
	private Long resolveId;
	public String getCloudyId() {
		return cloudyId;
	}
	public void setCloudyId(String cloudyId) {
		this.cloudyId = cloudyId;
	}
	public String getKnowledgeTypeCode() {
		return knowledgeTypeCode;
	}
	public void setKnowledgeTypeCode(String knowledgeTypeCode) {
		this.knowledgeTypeCode = knowledgeTypeCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSourceContent() {
		return sourceContent;
	}
	public void setSourceContent(String sourceContent) {
		this.sourceContent = sourceContent;
	}
	public String getResolveContent() {
		return resolveContent;
	}
	public void setResolveContent(String resolveContent) {
		this.resolveContent = resolveContent;
	}
	public byte[] getResolveAttachmentContent1() {
		return resolveAttachmentContent1;
	}
	public void setResolveAttachmentContent1(byte[] resolveAttachmentContent1) {
		this.resolveAttachmentContent1 = resolveAttachmentContent1;
	}
	public byte[] getResolveAttachmentContent2() {
		return resolveAttachmentContent2;
	}
	public void setResolveAttachmentContent2(byte[] resolveAttachmentContent2) {
		this.resolveAttachmentContent2 = resolveAttachmentContent2;
	}
	public byte[] getResolveAttachmentContent3() {
		return resolveAttachmentContent3;
	}
	public void setResolveAttachmentContent3(byte[] resolveAttachmentContent3) {
		this.resolveAttachmentContent3 = resolveAttachmentContent3;
	}
	public byte[] getResolveAttachmentContent4() {
		return resolveAttachmentContent4;
	}
	public void setResolveAttachmentContent4(byte[] resolveAttachmentContent4) {
		this.resolveAttachmentContent4 = resolveAttachmentContent4;
	}
	public byte[] getResolveAttachmentContent5() {
		return resolveAttachmentContent5;
	}
	public void setResolveAttachmentContent5(byte[] resolveAttachmentContent5) {
		this.resolveAttachmentContent5 = resolveAttachmentContent5;
	}
	public String getResolveAttachmentName1() {
		return resolveAttachmentName1;
	}
	public void setResolveAttachmentName1(String resolveAttachmentName1) {
		this.resolveAttachmentName1 = resolveAttachmentName1;
	}
	public String getResolveAttachmentName2() {
		return resolveAttachmentName2;
	}
	public void setResolveAttachmentName2(String resolveAttachmentName2) {
		this.resolveAttachmentName2 = resolveAttachmentName2;
	}
	public String getResolveAttachmentName3() {
		return resolveAttachmentName3;
	}
	public void setResolveAttachmentName3(String resolveAttachmentName3) {
		this.resolveAttachmentName3 = resolveAttachmentName3;
	}
	public String getResolveAttachmentName4() {
		return resolveAttachmentName4;
	}
	public void setResolveAttachmentName4(String resolveAttachmentName4) {
		this.resolveAttachmentName4 = resolveAttachmentName4;
	}
	public String getResolveAttachmentName5() {
		return resolveAttachmentName5;
	}
	public void setResolveAttachmentName5(String resolveAttachmentName5) {
		this.resolveAttachmentName5 = resolveAttachmentName5;
	}
	public Long getResolveId() {
		return resolveId;
	}
	public void setResolveId(Long resolveId) {
		this.resolveId = resolveId;
	}
	
}
