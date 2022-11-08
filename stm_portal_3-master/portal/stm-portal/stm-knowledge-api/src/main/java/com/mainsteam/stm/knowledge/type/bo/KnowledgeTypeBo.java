/**
 * 
 */
package com.mainsteam.stm.knowledge.type.bo;

/**
 * <li>文件名称: KnowledgeTypeBo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 知识分类</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日 下午5:24:31
 * @author   俊峰
 */
public class KnowledgeTypeBo {

	/**知识分类编码*/
	private String code;
	/**知识分类名称*/
	private String name;
	/**知识分类描述*/
	private String description;
	/**知识上级分类*/
	private String pcode;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPcode() {
		return pcode;
	}
	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	
}
