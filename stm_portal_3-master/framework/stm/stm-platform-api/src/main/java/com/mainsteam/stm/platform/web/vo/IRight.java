package com.mainsteam.stm.platform.web.vo;

import java.io.Serializable;
import java.util.Set;

/**
 * <li>文件名称: IRight.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月29日
 * @author   ziwenwen
 */
public interface IRight extends Serializable{
	public Long getId();
	public String getName();
	public String getUrl();
	public String getDescription();
	public int getType();
	public int getSort();
	public Long getPid();
	public Long getFileId();
	public Set<IDomain> getDomains();
}


