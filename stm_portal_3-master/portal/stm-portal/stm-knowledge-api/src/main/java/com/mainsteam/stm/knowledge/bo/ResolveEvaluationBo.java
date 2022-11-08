package com.mainsteam.stm.knowledge.bo;

import java.util.Date;

/**
 * <li>文件名称: ResolveEvaluationBo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 故障解决方案评价BO</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月4日 下午3:04:50
 * @author   俊峰
 */
public class ResolveEvaluationBo {

	/**
	 * 解决方案ID
	 */
	private long resolveId;
	/**
	 * 评价内容
	 */
	private String content;
	/**
	 * 评价得分
	 */
	private int score;
	
	private Date evaluateTime;
	public ResolveEvaluationBo(long resolveId, String content, int score,Date evaluateTime) {
		super();
		this.resolveId = resolveId;
		this.content = content;
		this.score = score;
		this.evaluateTime = evaluateTime;
	}
	public ResolveEvaluationBo() {
		super();
	}
	public long getResolveId() {
		return resolveId;
	}
	public void setResolveId(long resolveId) {
		this.resolveId = resolveId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Date getEvaluateTime() {
		return evaluateTime;
	}
	public void setEvaluateTime(Date evaluateTime) {
		this.evaluateTime = evaluateTime;
	}
}
