package com.mainsteam.stm.exception;

/**
 * <li>文件名称: UnsupportedException.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class UnsupportedException extends BaseRuntimeException {
	private static final long serialVersionUID = 8472072553176292306L;
	private static final String _msg="不支持该属性、方法或配置-";

	public UnsupportedException(String msg){
		super(503, _msg+msg);
	}
	
	public UnsupportedException(){
		this(blank_);
	}
}


