package com.mainsteam.stm.portal.resource.bo;

public class BatdisckvBo {
	private Long id;
	private String celltype;
	private String cellkey;
	private String cellvalue;
	private String reg;
	private String errorInfo;
	private Long titleId;
	private String resourceId = "";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCelltype() {
		return celltype;
	}

	public void setCelltype(String celltype) {
		this.celltype = celltype != null ? celltype.trim()
				: celltype;
	}

	public String getCellkey() {
		return cellkey;
	}

	public void setCellkey(String cellkey) {
		this.cellkey = cellkey != null ? cellkey.trim()
				: cellkey;
	}

	public String getCellvalue() {
		return cellvalue;
	}

	public void setCellvalue(String cellvalue) {
		this.cellvalue = cellvalue != null ? cellvalue.trim()
				: cellvalue;
	}

	public String getReg() {
		return reg;
	}

	public void setReg(String reg) {
		this.reg = reg != null ? reg.trim() : reg;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo != null ? errorInfo.trim()
				: errorInfo;
	}

	public Long getTitleId() {
		return titleId;
	}

	public void setTitleId(Long titleId) {
		this.titleId = titleId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof BatdisckvBo) {
			BatdisckvBo paramObject = (BatdisckvBo) object;
			return this.getCellkey().equals(paramObject.getCellkey()) && this.getResourceId().equals(paramObject.getResourceId());
		}
		return super.equals(object);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cellkey == null) ? 0 : cellkey.hashCode());
		result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
		return result;
	}
}
