package com.mainsteam.stm.portal.config.web.vo;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
/**
 * 
 * <li>文件名称: BackupPickGridVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月17日
 * @author   liupeng
 */
public class BackupPickGridVo {
	/**
	 * pickGrid左边展示的数据
	 */
	private Page<DeviceResourceVo, Object> left = new Page<DeviceResourceVo, Object>();
	/**
	 * pickGrid右边展示的数据
	 */
	private Page<DeviceResourceVo, Object> right = new Page<DeviceResourceVo, Object>();
	public Page<DeviceResourceVo, Object> getLeft() {
		return left;
	}
	public void setLeft(Page<DeviceResourceVo, Object> left) {
		this.left = left;
	}
	public Page<DeviceResourceVo, Object> getRight() {
		return right;
	}
	public void setRight(Page<DeviceResourceVo, Object> right) {
		this.right = right;
	}
}
