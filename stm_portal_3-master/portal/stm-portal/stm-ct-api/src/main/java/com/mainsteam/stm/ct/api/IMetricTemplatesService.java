package com.mainsteam.stm.ct.api;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.ct.bo.ProfileMetricVo;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IMetricTemplatesService {

    void getMetricTemplates(Page<MsMetricTemplates,MsMetricTemplates> page);

    Result updateOrInsertMetricTemplates(List<MsMetricTemplates> list);

    int deleteTemplates(String profilelibId);

    int insertTemplates(MsMetricTemplates msMetricTemplates);
}
