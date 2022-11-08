package com.mainsteam.stm.simple.engineer.workbench.bo;

import java.io.Serializable;

/**
 * <li>文件名称: EvaluateKnowledgeBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 评价知识对象实体</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class EvaluateKnowledgeBo implements Serializable{
	
	private static final long serialVersionUID = 2417302281547119797L;

	/**
	 * 知识id
	 */
	private Long knowledgeId;
	
	/**
	 * 得分
	 */
	private int score;
	
	/**
	 * 评价描述
	 */
	private String description;

	public Long getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(Long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}


