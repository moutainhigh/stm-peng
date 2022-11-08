package com.mainsteam.stm.simple.search.vo;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;

/**
 * <li>文件名称: com.mainsteam.stm.simple.search.vo.SearchResultVo.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月30日
 */
public class SearchResultVo {
	private List<ResourceBizRel> rels = new ArrayList<ResourceBizRel>();
	private List<KnowledgeBo> knowledges = new ArrayList<KnowledgeBo>();
	public List<ResourceBizRel> getRels() {
		return rels;
	}
	public void setRels(List<ResourceBizRel> rels) {
		this.rels = rels;
	}
	public List<KnowledgeBo> getKnowledges() {
		return knowledges;
	}
	public void setKnowledges(List<KnowledgeBo> knowledges) {
		this.knowledges = knowledges;
	}
}
