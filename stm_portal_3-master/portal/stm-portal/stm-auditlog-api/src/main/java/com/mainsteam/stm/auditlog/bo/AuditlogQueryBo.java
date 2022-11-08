package com.mainsteam.stm.auditlog.bo;


/**
 * 
 * <li>文件名称: AuditlogQueryBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 业务逻辑层日志查询的传输对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月12日  上午11:16:50
 * @author   xch
 */
public class AuditlogQueryBo extends AuditlogBo{
	
	private static final long serialVersionUID = 1L;
	//操作开始时间
	private String beginDate;
	//操作结束时间
	private String endDate;
	
	private String keyword;

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	

}
