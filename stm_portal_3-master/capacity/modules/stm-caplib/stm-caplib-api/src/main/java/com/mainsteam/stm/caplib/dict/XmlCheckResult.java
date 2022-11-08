package com.mainsteam.stm.caplib.dict;

public class XmlCheckResult implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3328287734082459605L;
	private CheckResultEnum resultEnum;
	private String fileName;
	private String descript;

	public static final XmlCheckResult OK_RESULT = new XmlCheckResult(
			CheckResultEnum.OK, "", "");

	public XmlCheckResult(CheckResultEnum resultEnum, String fileName,
			String descript) {
		super();
		this.fileName = fileName;
		this.resultEnum = resultEnum;
		this.descript = descript;
	}

	public CheckResultEnum getResultEnum() {
		return resultEnum;
	}

	public void setResultEnum(CheckResultEnum resultEnum) {
		this.resultEnum = resultEnum;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resultEnum == null) ? 0 : resultEnum.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlCheckResult other = (XmlCheckResult) obj;
		if (resultEnum != other.resultEnum)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(resultEnum);
		builder.append(",");
		builder.append(fileName);
		builder.append(",");
		builder.append(descript);
		return builder.toString();
	}

}
