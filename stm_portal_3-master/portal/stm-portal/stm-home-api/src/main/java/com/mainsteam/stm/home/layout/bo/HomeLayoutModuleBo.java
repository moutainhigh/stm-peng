package com.mainsteam.stm.home.layout.bo;

/**
 * 系统组件配置
 * <li>文件名称: HomeLayoutModuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月13日 上午10:40:59
 * @author   dfw
 */
public class HomeLayoutModuleBo {
	
    /** ID */
    private long id;

    /** 名称 */
    private String name;

    /** 模块编号，主要用于加载模块数据时，区分模块以便于对props进行处理 */
    private String moduleCode;

    /** 默认模块宽度 */
    private int defaultWidth;

    /** 默认模块高度 */
    private int defaultHeight;

    /** 组件页面地址 */
    private String url;

    /** 顺序号 */
    private int sortNum;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
    
}
