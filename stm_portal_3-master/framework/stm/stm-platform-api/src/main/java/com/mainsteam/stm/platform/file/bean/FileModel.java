package com.mainsteam.stm.platform.file.bean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件模型
* <li>文件名称: FileModel.java</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: ...</li>
* <li>内容摘要: ...</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年8月14日
* @author   wangxinghao
* @tags
 */
public class FileModel implements Serializable{

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7727249362721150780L;
	
	
	//ID
	private long id;
	//文件的业务分组
	private String fileGroup;
	//文件的真实名称
	private String fileName;
	//文件的服务器存放的物理名称
	private String finalName;
	//文件的路径
	private String filePath;
	//文件的类型
	private String mimeType;
	//文件的后缀名称
	private String fileExt;
	//文件的大小
	private long fileSize;
	//文件的MD5编码
	private String MD5;
	//文件的创建日期
	private Date createDatetime;
	
	//1 :Lock,2:unlock
	private int lock;
	
	//文本流
	InputStream in;
	
	
//	public boolean isLock(){
//		return this.lock==1;
//	}
//	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFinalName() {
		return finalName;
	}

	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}


	public int getLock() {
		return lock;
	}


	public void setLock(int lock) {
		this.lock = lock;
	}

	@Override
	public boolean equals(Object obj) {
		FileModel target=(FileModel)obj;

		return target.getId()==this.id;
	}


	


}
