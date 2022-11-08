/**
 * 
 */
package com.mainsteam.stm.knowledge.local.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: KnowlwdgeAttachmentBo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月30日 上午10:37:34
 * @author   俊峰
 */
public class KnowledgeAttachmentBo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5714628829478534254L;

	/**解决方案ID*/
	private long resolveId;
	
	/**附件ID*/
	private long fileId;
	
	/**附件名称*/
	private String fileName;
	
	private Date uploadDate;
	
	/**附件排序字段*/
	private int sort;

	public long getResolveId() {
		return resolveId;
	}

	public void setResolveId(long resolveId) {
		this.resolveId = resolveId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
}
