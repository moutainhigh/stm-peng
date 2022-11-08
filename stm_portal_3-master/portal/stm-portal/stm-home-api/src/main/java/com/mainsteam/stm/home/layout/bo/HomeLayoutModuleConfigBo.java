package com.mainsteam.stm.home.layout.bo;

/**
 * 首页组件属性配置
 * <li>文件名称: HomeLayoutModuleConfigBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月13日 上午10:41:05
 * @author   dfw
 */
public class HomeLayoutModuleConfigBo {
	
    /** ID,用来唯一区分首页组件 */
    private long id;

    /** 组件ID */
    private long moduleId;

    /** 模块编号 */
    private String moduleCode;

    /** 用户ID */
    private long userId;

    /** 布局ID */
    private long layoutId;

    /** 组件属性，json格式 */
    private String props;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(long layoutId) {
		this.layoutId = layoutId;
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}
    
}
