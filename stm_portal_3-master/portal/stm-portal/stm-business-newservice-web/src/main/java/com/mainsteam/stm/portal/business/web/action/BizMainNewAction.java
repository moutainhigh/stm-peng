package com.mainsteam.stm.portal.business.web.action;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainteam.stm.portal.business.api.BizMainNewApi;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/portal/business/newservice")
public class BizMainNewAction extends BaseAction {

    private Logger logger = Logger.getLogger(BizMainNewAction.class);

    @Autowired
    private BizMainNewApi bizMainNewApi;

    /**
     * 获取分页业务集合(汇总界面)
     * @return
     */
    @RequestMapping("/getDefaultView")
    public JSONObject getDefaultView(HttpSession session){
        return toSuccess(bizMainNewApi.getDefaultView(getLoginUser(session)));
    }




}
