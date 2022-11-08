package com.mainsteam.stm.camera.impl.dao;

import com.mainsteam.stm.camera.bo.CameraBo;
import com.mainsteam.stm.camera.bo.CameraVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ICameraNewDao {

    void selectPageForCamera(Page<CameraBo, CameraVo> page);

    void getOfflineNumber(Page<CameraBo, CameraVo> page);

}
