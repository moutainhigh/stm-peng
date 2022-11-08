package com.mainsteam.stm.exception;

/**
 * <li>文件名称: ConfigException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class ConfigException extends BaseRuntimeException {
	private static final long serialVersionUID = -8676350005362551536L;
	private static final String _msg="系统配置错误-";

	public ConfigException(String msg){
		super(501, _msg+msg);
	}
	public ConfigException(){
		this(blank_);
	}
}


