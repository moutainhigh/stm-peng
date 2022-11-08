package com.mainsteam.stm.topo.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;

/**
 * <li>拓扑权限设置业务接口定义</li>
 * <li>文件名称: ITopoAuthSettingApi.java</li>
 * @version  ms.stm
 * @since  2019年11月21日
 * @author zwx
 */
public interface ITopoAuthSettingApi {
	
	/**
	 * 根据拓扑id查询该拓扑的用户权限设置信息
	 * @param subtopoId
	 * @return
	 */
	public List<TopoAuthSettingBo> getTopoAuthSetting(Long subtopoId);
	
	/**
	 * 保存子拓扑的用户权限设置信息
	 * @param subtopoId
	 * @param settingBos
	 * @return
	 */
	public void save(Long subtopoId,List<TopoAuthSettingBo> settingBos);
	/**
	 * 判断当前用户是否有topid的相应mode权限
	 * @param userId 用户id
	 * @param topoId
	 * @param modes 待验证的权限 select edit
	 * @return
	 */
	public boolean hasAuth(Long topoId, String[] modes);
	/**
	 * 根据用户id和拓扑id查询权限信息
	 * @param user
	 * @param topoId
	 * @return
	 */
	public TopoAuthSettingBo getAuthSetting(Long topoId);

	public boolean hasAuth(ILoginUser user, Long topoId, String[] strings);

}
