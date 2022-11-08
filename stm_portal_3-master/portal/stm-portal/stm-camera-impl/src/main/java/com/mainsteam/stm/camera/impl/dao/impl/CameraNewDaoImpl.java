package com.mainsteam.stm.camera.impl.dao.impl;

import com.mainsteam.stm.camera.bo.CameraBo;
import com.mainsteam.stm.camera.bo.CameraVo;
import com.mainsteam.stm.camera.impl.dao.ICameraNewDao;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import org.mybatis.spring.SqlSessionTemplate;

public class CameraNewDaoImpl extends BaseDao<CameraBo> implements ICameraNewDao {
    public CameraNewDaoImpl(SqlSessionTemplate session) {
        super(session, ICameraNewDao.class.getName());
    }

    @Override
    public void selectPageForCamera(Page<CameraBo, CameraVo> page) {
        this.select("selectPageForCamera", page);
    }

    @Override
    public void getOfflineNumber(Page<CameraBo, CameraVo> page) {
        this.select("getOfflineNumber", page);
    }

}
