package com.mainsteam.stm.platform.local;

/**
 * <li>文件名称: DefaultLocalImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class DefaultLocalImpl implements ILocal {
	
	private String sysNamespace=begin+"sys.";
	
	private String moduleNameSpace;
	
	@Override
	public final String getSysKey(String key) {
		return sysNamespace+key+end;
	}
	
	@Override
	public String getKey(String key) {
		return moduleNameSpace+key+end;
	}
	
	/**
	 * @param moduleNameSpace 模块命名空间
	 */
	public DefaultLocalImpl(String moduleNameSpace) {
		this.moduleNameSpace=begin+moduleNameSpace;
	}
}


