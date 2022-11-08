package com.mainsteam.stm.topo.bo;


/*
 * 拓扑图的图标信息
 */
public class TopoIconBo {
    private Long id;
    // 类型：icon,cmd等，用来标注此图标干什么的
	private String type;
	// 路径
	private String path;
    // 宽度
    private Double width;
    // 高度
    private Double height;
    //尺寸（0表示小尺寸，1表示大尺寸）
    private int sizeType;
    //文件名
    private String fileName;
    //上传文件id
    private Long fileId;
    
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public int getSizeType() {
		return sizeType;
	}
	public void setSizeType(int sizeType) {
		this.sizeType = sizeType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
