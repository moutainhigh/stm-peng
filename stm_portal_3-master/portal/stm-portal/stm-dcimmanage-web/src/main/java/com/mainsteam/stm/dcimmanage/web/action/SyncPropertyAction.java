package com.mainsteam.stm.dcimmanage.web.action;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.dcimmanage.api.ISyncPropertyApi;
import com.mainsteam.stm.dcimmanage.po.DcimResourcePo;
import com.mainsteam.stm.dcimmanage.vo.DcimResourceVo;
import com.mainsteam.stm.dcimmanage.vo.DcimResourcePageVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/portal/dcim/sync")
public class SyncPropertyAction extends BaseAction {
    private Logger logger = Logger.getLogger(SyncPropertyAction.class);

    @Autowired
    private ISyncPropertyApi iSyncPropertyApi;

    @RequestMapping(value="/getProperties")
    public JSONObject getProperties(){
        iSyncPropertyApi.getProperties();
        return toSuccess("OK");
    }

    @RequestMapping(value="/getDcimPage")
    public JSONObject getDcimPage(Page<DcimResourceVo, DcimResourceVo> page,DcimResourceVo condition, HttpSession session){
        logger.info("malachi dcimmanage getDcimPage start");
        logger.info("****dcimResourcePo****" + condition.getId());
        logger.info("****dcimResourcePo****" + condition.getIp());
        // 获取当前登录用户
        ILoginUser user = getLoginUser(session);
        try {
            page.setCondition(condition);
            iSyncPropertyApi.getDcimPage(page);
            logger.info("malachi dcimmanage getDcimPage end");
            return toSuccess(page);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value="/update")
    public JSONObject update(Long dcimId, Long stmId, HttpSession session){
        logger.info("malachi dcimmanage saveConfig start");
        logger.info("****dcimId****" + dcimId);
        logger.info("****stmId****" + stmId);
        // 获取当前登录用户
        ILoginUser user = getLoginUser(session);
        int count = 0;
        try {
            count = iSyncPropertyApi.update(dcimId, stmId);

            // 对应关系发送给dcim平台
            if(count > 0){
                iSyncPropertyApi.sendToDcim(dcimId, stmId);
            }

        }catch (Exception e){
            logger.error(e.getMessage());
            return toSuccess("OK");
        }
        return toSuccess(count);
    }
}
