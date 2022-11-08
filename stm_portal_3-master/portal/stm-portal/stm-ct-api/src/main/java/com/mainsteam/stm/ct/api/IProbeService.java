package com.mainsteam.stm.ct.api;

import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IProbeService {

    /**
     * 获取探针信息
     * @param page
     * @param msProbeParams
     * @return
     */
    public void getProbeList(Page<MsProbe,MsProbe> page);

    /**
     * 添加探针
     * @param msProbeParams
     */
    public int addProbe(MsProbe msProbeParams);

    /**
     * 编辑探针
     * @param msProbeParams
     */
    public int editProbe(MsProbe msProbeParams);

    /**
     * 删除探针
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteProbe(String id);
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatchProbe(String ids);

	public MsProbe getById(Integer probeId);
}
