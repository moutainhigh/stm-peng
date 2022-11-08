package com.mainsteam.stm.dcimmanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.dcimmanage.api.ISyncPropertyApi;
import com.mainsteam.stm.dcimmanage.dao.ISyncPropertyDao;
import com.mainsteam.stm.dcimmanage.po.DcimResourcePo;
import com.mainsteam.stm.dcimmanage.util.HttpUtil;
import com.mainsteam.stm.dcimmanage.vo.DcimResourceVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.util.PropertiesFileUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SyncPropertyServiceImpl implements ISyncPropertyApi {

    private Logger logger = Logger.getLogger(SyncPropertyServiceImpl.class);

    @Autowired
    private ISyncPropertyDao syncPropertyDao;

    @Override
    public void getProperties() {
        try {
            String params = "";
            // 调用接口获取资产信息
            JSONObject obj = HttpUtil.httppost(getUrl("dcim.get"), params);
            List<DcimResourceVo> respList = JSON.parseArray(obj.toString(), DcimResourceVo.class);

            if(respList != null && respList.size() > 0){
                List<DcimResourcePo> poList = toPo(respList);
                //TODO 去重入库，demo没做
//                List<DcimResourcePo> list = syncPropertyDao.getAll();
                int count = syncPropertyDao.batchInsert(poList);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }

    }

    @Override
    public void sendToDcim(Long dcimId, Long stmId) {
        try {
            JSONObject params = new JSONObject();
            params.put("dcimId", dcimId);
            params.put("stmId", stmId);
            JSONObject obj = HttpUtil.httppost(getUrl("dcim.sync"), params.toString());
            logger.info("sendToDcim respones : " + obj.toString());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public void getDcimPage(Page<DcimResourceVo, DcimResourceVo> dcimPage) {
        logger.info("malachi dcimmanage getDcimPage start");
        syncPropertyDao.getDcimPage(dcimPage);
//        List<DcimResourceVo> list = syncPropertyDao.getAll();
        logger.info("malachi dcimmanage getDcimPage end ");
    }

    @Override
    public int update(Long dcimId, Long stmId) {
        // 查询对应表 dcimId
        List list = syncPropertyDao.countRelationByDcim(dcimId);
        // 有则更新  无则新增
        int count = 0;
        if(list.size() > 0){
            count = syncPropertyDao.updateRelation(dcimId, stmId);
        }else{
            count = syncPropertyDao.insertRelation(dcimId, stmId);
        }
        return count;
    }

    private List<DcimResourcePo> toPo(List<DcimResourceVo> voList){
        List<DcimResourcePo> poList = new ArrayList<>();
        DcimResourcePo po = null;
        for (DcimResourceVo vo : voList
             ) {
            po = new DcimResourcePo();
            po.setId(vo.getId());
            po.setIp(vo.getIp());
            po.setName(vo.getName());
            poList.add(po);
        }
        return poList;
    }

    private String getUrl(String key){
        // 获取dcim配置(先写在配置文件，后在页面配置存数据库)
        Properties pp= PropertiesFileUtil.getProperties("properties/dcim.properties");
        String url = pp.getProperty(key);
        return url;
    }
}
