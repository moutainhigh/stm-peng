package com.mainsteam.stm.dcimmanage.api;

import com.mainsteam.stm.dcimmanage.po.DcimResourcePo;
import com.mainsteam.stm.dcimmanage.vo.DcimResourceVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ISyncPropertyApi {

    public void getProperties();

    public void sendToDcim(Long dcimId, Long stmId);

    public void getDcimPage(Page<DcimResourceVo, DcimResourceVo> dcimPage);

    public int update(Long dcimId, Long stmId);

}
