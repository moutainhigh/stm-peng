package com.mainsteam.stm.ipmanage.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ipmanage.bo.IpMain;
import com.mainsteam.stm.ipmanage.bo.TreeNode;

public interface IpMainService {
	public List<IpMain> getIPList(IpMain ip);
    
    public int insertIp(IpMain ip);
    
    public int update(IpMain ip);
    
    public int delete(Integer id);
    
    public Map<TreeNode, List<IpMain>> IpAutoDiscover();

	public int addIpList(List<IpMain> list);

	public void export(HttpServletResponse response, HttpServletRequest request) throws Exception;

	public String inputFile(InputStream inputStream);
}
