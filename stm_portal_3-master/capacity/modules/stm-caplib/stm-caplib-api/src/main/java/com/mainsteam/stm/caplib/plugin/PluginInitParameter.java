package com.mainsteam.stm.caplib.plugin;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.mainsteam.stm.caplib.dict.BoxStyleEnum;

/**
 * 初始化参数
 * 
 * @author Administrator
 *
 */
public class PluginInitParameter implements Serializable, Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 446093849700870920L;
	/**
	 * id
	 */
	private String id;
	/**
	 * 类型
	 */
	private String valueValidate;
	/**
	 * 名称
	 */
	private String name;

	/**
	 * 是不是session的key
	 */
	private boolean isSessionKey;

	/**
	 * 显示顺序
	 */
	private String displayOrder;

	/**
	 * 显示框的类型
	 */
	private BoxStyleEnum BoxStyle;

	/**
	 * 不能为空
	 */
	private boolean mustInput;

	/**
	 * 是否可编辑
	 */
	private boolean isEdit;

	/**
	 * 是否密码
	 */
	private boolean isPassword;
	/**
	 * 是否可显示
	 */
	private boolean isDisplay;
	/**
	 * 缺省值
	 */
	private String defaultValue;
	/**
	 * 帮助信息
	 */
	private String helpInfoId;

	/**
	 * 值
	 */
	private SupportValue[] supportValues;

	/**
	 * 组,例如snmpv3
	 */
	private String group;
	
	/**
	 * 类型，user表示是用户名；password表示是密码
	 */
	private String type;
	
	/**
	 * 单位枚举
	 */
	private TimeUnit unitEnum;
	
	/**
	 * 当配置有valueValidate时验证不通过的错误提示
	 */
	private String errorInfo;
	
	public PluginInitParameter(){
	}
	/**
	 * 获取id
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取name
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取DisplayOrder(排序使用)
	 * @return
	 */
	public String getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(String displayOrder) {
		this.displayOrder = displayOrder;
	}

	/**
	 * 是否编辑
	 * @return
	 */
	public boolean isEdit() {
		return isEdit;
	}

	/**
	 * 是否显示
	 * @return
	 */
	public boolean isDisplay() {
		return isDisplay;
	}

	/**
	 * 获取默认值
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 获取帮助信息
	 * @return
	 */
	public String getHelpInfoId() {
		return helpInfoId;
	}

	public void setHelpInfoId(String helpInfoId) {
		this.helpInfoId = helpInfoId;
	}

	/**
	 * 是否是session key
	 * @return
	 */
	public boolean isSessionKey() {
		return isSessionKey;
	}

	public void setSessionKey(boolean isSessionKey) {
		this.isSessionKey = isSessionKey;
	}

	/**
	 * 获取组名称
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * 获取box style(网页展示style)
	 * @return
	 */
	public BoxStyleEnum getBoxStyle() {
		return BoxStyle;
	}

	public void setBoxStyle(BoxStyleEnum BoxStyle) {
		this.BoxStyle = BoxStyle;
	}

	/**
	 * 是否必须输入
	 * @return
	 */
	public boolean isMustInput() {
		return mustInput;
	}

	public void setMustInput(boolean mustInput) {
		this.mustInput = mustInput;
	}

	/**
	 * 获取support value集合
	 * @return
	 */
	public SupportValue[] getSupportValues() {
		return supportValues;
	}

	public void setSupportValues(SupportValue[] supportValues) {
		this.supportValues = supportValues;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	/**
	 * 是否是密码类型
	 * @return
	 */
	public boolean isPassword() {
		return isPassword;
	}

	public void setPassword(boolean isPassword) {
		this.isPassword = isPassword;
	}

	/**
	 * 获取ValueValidate
	 * @return
	 */
	public String getValueValidate() {
		return valueValidate;
	}

	public void setValueValidate(String valueValidate) {
		this.valueValidate = valueValidate;
	}

	/**
	 * 获取类型
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TimeUnit getUnitEnum() {
		return unitEnum;
	}

	public void setUnitEnum(TimeUnit unitEnum) {
		this.unitEnum = unitEnum;
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	
	
	@Override
	public Object clone() {
		PluginInitParameter def = new PluginInitParameter();
		def.setBoxStyle(this.getBoxStyle());
		def.setDefaultValue(this.getDefaultValue());
		def.setDisplay(this.isDisplay());
		def.setDisplayOrder(this.getDisplayOrder());
		def.setEdit(this.isEdit());
		def.setErrorInfo(this.getErrorInfo());
		def.setGroup(this.getGroup());
		def.setHelpInfoId(this.getHelpInfoId());
		def.setId(this.getId());
		def.setMustInput(this.isMustInput());
		def.setName(this.getName());
		def.setPassword(this.isPassword());
		def.setSessionKey(this.isSessionKey());
		if(this.getSupportValues() != null && this.getSupportValues().length > 0) {
			SupportValue[] values = new SupportValue[this.getSupportValues().length];
			for(int i = 0; i < this.getSupportValues().length; i++) {
				values[i] = (SupportValue) this.getSupportValues()[i].clone();
			}
			def.setSupportValues(values);
		}
		def.setType(this.getType());
		def.setUnitEnum(this.getUnitEnum());
		def.setValueValidate(this.getValueValidate());
		return def;
	}

}
