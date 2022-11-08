package com.mainsteam.stm.ct.api;

import com.mainsteam.stm.ct.bo.MsProfilelibMain;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IProfilelibService  {

    /**
     * 获取策略信息
     * @param page
     * @param msProfilelibParams
     * @return
     */
    public void getProfilelibList(Page<MsProfilelibMain,MsProfilelibMain> page);

    /**
     * 添加策略
     * @param msProfilelibParams
     */
    public int addProfilelib(MsProfilelibMain msProfilelibParams);

    /**
     * 编辑策略
     * @param msProfilelibParams
     */
    public int editProfilelib(MsProfilelibMain msProfilelibParams);

    /**
     * 删除策略
     * @param id
     */
    public int deleteProfilelib(String id);

    public int deleteBatchProfilelib(String id);

	public MsProfilelibMain getById(String string);
}
