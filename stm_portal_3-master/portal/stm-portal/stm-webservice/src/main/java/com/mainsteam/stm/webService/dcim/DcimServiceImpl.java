package com.mainsteam.stm.webService.dcim;

import javax.jws.WebService;

@WebService
public class DcimServiceImpl implements DcimService {

    @Override
    public String dcim(Dcim dcim) {

        String str = "{'id':123,'name':'test'}";
        return str;
    }

    @Override
    public String getMetricData(Long[] instanceIds) {
        // 查询设备类型，确定返回指标

        

        return null;
    }
}
