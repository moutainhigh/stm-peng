package com.mainsteam.stm.ct.api;

import java.util.List;

import com.mainsteam.stm.ct.bo.MsResourceMain;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IResourceService {

    /**
     * 获取资源信息
     * @param page
     * @param msResourceParams
     * @return
     */
    public void getResourceList(Page<MsResourceMain,MsResourceMain> page);

    MsResourceMain getFullResource(MsResourceMain msResourceParams);

    /**
     * 添加资源
     * @param msResourceParams
     */
    public int addResource(MsResourceMain msResourceParams);

    /**
     * 编辑资源
     * @param msResourceParams
     */
    public int editResource(MsResourceMain msResourceParams);

    /**
     * 删除资源
     * @param id
     */
    public int deleteResource(String id);

    public int deleteBatchResource(String id);

    public List<String> getResourceIdList(MsResourceMain msResourceParams);

	public MsResourceMain getById(String string);

	public List<MsResourceMain> getResourceIdAndTestName(String testWay);
	
	public int success(String resourceId);

	public int fail(String resourceId);
}
