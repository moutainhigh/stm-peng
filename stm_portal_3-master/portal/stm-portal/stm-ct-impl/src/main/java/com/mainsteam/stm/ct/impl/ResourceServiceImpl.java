package com.mainsteam.stm.ct.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.ct.api.IResourceService;
import com.mainsteam.stm.ct.bo.MsResourceMain;
import com.mainsteam.stm.ct.dao.ResourceMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

import java.util.List;
import java.util.UUID;


public class ResourceServiceImpl implements IResourceService{

    @Autowired
    private ResourceMapper resourceMapper;

    

    @Override
    public MsResourceMain getFullResource(MsResourceMain msResourceParams) {
        return resourceMapper.getFullResource(msResourceParams);
    }

    @Override
    public int addResource(MsResourceMain msResourceParams) {
        msResourceParams.setId(UUID.randomUUID().toString());
        //TODO:传输资源给指定的probe
        /**
         * HTTP
         */
        return resourceMapper.insert(msResourceParams);
    }

    @Override
    public int editResource(MsResourceMain msResourceParams) {
    	return resourceMapper.updateById(msResourceParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteResource(String id) {
        return resourceMapper.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatchResource(String ids) {
    	String[] split = ids.split(",");
		
    	try {
    		for (int i = 0; i < split.length; i++) {
    			resourceMapper.removeById(split[i]);
    		}
    		return 1;
		} catch (Exception e) {
			// TODO: handle exception
			 return 0;
		}
		
       
    }

    @Override
    public List<String> getResourceIdList(MsResourceMain msResourceParams) {
        return resourceMapper.getResourceIdList(msResourceParams);
    }

	@Override
	public void getResourceList(Page<MsResourceMain, MsResourceMain> page) {
		// TODO Auto-generated method stub
		resourceMapper.getResourceList(page);
	}

	@Override
	public MsResourceMain getById(String id) {
		// TODO Auto-generated method stub
		return resourceMapper.getById(id);
	}

	@Override
	public List<MsResourceMain> getResourceIdAndTestName(String testWay) {
		// TODO Auto-generated method stub
		return resourceMapper.getResourceIdAndTestName(testWay);
	}

	@Override
	public int success(String resourceId) {
		// TODO Auto-generated method stub
		return resourceMapper.success(resourceId);
	}
	@Override
	public int fail(String resourceId) {
		// TODO Auto-generated method stub
		return resourceMapper.fail(resourceId);
	}


}
