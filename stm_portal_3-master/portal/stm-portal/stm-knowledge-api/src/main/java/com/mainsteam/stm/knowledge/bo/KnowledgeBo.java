package com.mainsteam.stm.knowledge.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <li>文件名称: KnowledgeBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年10月13日
 * @author ziwenwen
 */
public class KnowledgeBo implements Serializable {

	private static final long serialVersionUID = 1074628892221788324L;

	/**
	 * 知识id，忽略云端下载下来的知识id，使用本地知识库的id生成规则重建
	 */
	private long id;

	/**
	 * 知识分类编码
	 */
	private String knowledgeTypeCode;

	/**
	 * 知识分类名称
	 * */
	private String knowledgeTypeName;

	/**
	 * 是否云端 1是 0否
	 */
	private int isCloudy;

	/**
	 * 云端知识ID
	 * */
	private String cloudyId;
	/**
	 * 关键字
	 */
	private String keywords;

	/**
	 * 知识内容
	 */
	private String sourceContent;

	/**
	 * 知识创建人ID
	 * */
	private long createUserId;

	/**
	 * 知识创建时间
	 * */
	private Date createTime;

	/**
	 * 知识解决方案集合
	 */
	private List<KnowledgeResolveBo> resolves;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKnowledgeTypeCode() {
		return knowledgeTypeCode;
	}

	public void setKnowledgeTypeCode(String knowledgeTypeCode) {
		this.knowledgeTypeCode = knowledgeTypeCode;
	}

	public String getKnowledgeTypeName() {
		return knowledgeTypeName;
	}

	public void setKnowledgeTypeName(String knowledgeTypeName) {
		this.knowledgeTypeName = knowledgeTypeName;
	}

	public int getIsCloudy() {
		return isCloudy;
	}

	public void setIsCloudy(int isCloudy) {
		this.isCloudy = isCloudy;
	}

	public String getCloudyId() {
		return cloudyId;
	}

	public void setCloudyId(String cloudyId) {
		this.cloudyId = cloudyId;
	}


	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSourceContent() {
		return sourceContent;
	}

	public void setSourceContent(String sourceContent) {
		this.sourceContent = sourceContent;
	}

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public KnowledgeBo(long id, String knowledgeTypeCode,
			String knowledgeTypeName, int isCloudy, String cloudyId, String keywords, String sourceContent,
			long createUserId, Date createTime) {
		super();
		this.id = id;
		this.knowledgeTypeCode = knowledgeTypeCode;
		this.knowledgeTypeName = knowledgeTypeName;
		this.isCloudy = isCloudy;
		this.cloudyId = cloudyId;
		this.keywords = keywords;
		this.sourceContent = sourceContent;
		this.createUserId = createUserId;
		this.createTime = createTime;
	}

	public KnowledgeBo() {
		super();
	}

	public List<KnowledgeResolveBo> getResolves() {
		return resolves;
	}

	public void setResolves(List<KnowledgeResolveBo> resolves) {
		this.resolves = resolves;
	}

	
}
