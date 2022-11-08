package com.mainsteam.stm.ipmanage.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.ipmanage.api.IpMainService;
import com.mainsteam.stm.ipmanage.bo.Depart;
import com.mainsteam.stm.ipmanage.bo.IpMain;
import com.mainsteam.stm.ipmanage.bo.TreeNode;
import com.mainsteam.stm.ipmanage.impl.dao.DepartMapper;
import com.mainsteam.stm.ipmanage.impl.dao.IpMainMapper;
import com.mainsteam.stm.ipmanage.impl.dao.TreeNodeMapper;
import com.mainsteam.stm.ipmanage.util.IPUtil;
import com.mainsteam.stm.ipmanage.util.MacUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.excel.ExcelHeader;
import com.mainsteam.stm.export.excel.IExcelConvert;
import com.mainsteam.stm.export.excel.ReadExcelArgs;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.ipmanage.util.*;

public class IpMainServiceImpl implements IpMainService {
	Logger logger=Logger.getLogger(IpMainServiceImpl.class);

	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Resource
	private IpMainMapper ipMainMapper;
	@Resource
	private TreeNodeMapper treeNodeMapper;
	@Resource
	private DepartMapper departMapper;
	
	public List<IpMain> getIPList(IpMain ip) {
		
		return  ipMainMapper.getIPList(ip);
	}

	
	public int insertIp(IpMain ip) {
		// TODO Auto-generated method stub
		return ipMainMapper.insertIp(ip);
	}

	
	public int update(IpMain ip) {
		// TODO Auto-generated method stub
		return ipMainMapper.update(ip);
	}

	
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return ipMainMapper.delete(id);
	}

	
	public Map<TreeNode, List<IpMain>> IpAutoDiscover() {
		// TODO Auto-generated method stub
		Map<TreeNode, List<IpMain>> ipandMac=null;
		try {
			ipandMac = getIpandMac();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		return ipandMac;
	}

	public Map<TreeNode, List<IpMain>> getIpandMac()throws Exception{
		Map<TreeNode, List<IpMain>> map=new HashMap<>();
		List<IpMain> list=null;
		String localMac = MacUtil.getLocalMac();
		String regex = "([^:]*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(localMac);
		String ipForNetSegment = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\ *-";
		Pattern netSegment = Pattern.compile(ipForNetSegment);
		String ipregex = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
		Pattern ipPattern = Pattern.compile(ipregex);
		String macRegex="(([A-Fa-f0-9]{2}:)|([A-Fa-f0-9]{2}-)){5}[A-Fa-f0-9]{2}";
		Pattern macPattern=Pattern.compile(macRegex);
		TreeNode tNode=null;
		while (matcher.find()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				String s = matcher.group(i);

				Matcher netSegmentMatcher = netSegment.matcher(s);
				String netSegTemp = null;
				boolean useful=false;
				if (netSegmentMatcher.find()) {
					netSegTemp = netSegmentMatcher.group(0);
					useful=true;
				}
				if (!useful){
					continue;
				}
				int idx = 0;

				Matcher matcher1 = ipPattern.matcher(s);
				while (matcher1.find()) {
					tNode=new TreeNode();
					list=new ArrayList<>();
					tNode.setNode_maskbit(24);
					//System.out.println(matcher1.groupCount());
					String s1 = matcher1.group(0);
					//System.out.println(s1);
					IpMain ip=null;
					idx = netSegTemp.lastIndexOf(".");
					String ss = netSegTemp.substring(0, idx + 1);
					String[] split = ss.split("\\.");
					tNode.setNode_ip(ss+".0");
					tNode.setIp1(Integer.valueOf(split[0]));
					tNode.setIp2(Integer.valueOf(split[1]));
					tNode.setIp3(Integer.valueOf(split[2]));
					TreeNode node = treeNodeMapper.getParentTreeNodeByIp(tNode);
					if(node!=null){
						tNode.setParent_id(node.getId());
					}
					if (s1.contains(ss)){
						BufferedReader reader=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
						String temp=null;
						while (null!=(temp=reader.readLine())){
							if(temp.contains(s1)){
								Matcher matcher2 = macPattern.matcher(temp);
								if (matcher2.find()){
									ip=new IpMain();
									String mac=matcher2.group(0).replace("-",":");
									String[] split2 = s1.split("\\.");
									ip.setIp1(Integer.valueOf(split2[0]));
									ip.setIp2(Integer.valueOf(split2[1]));
									ip.setIp3(Integer.valueOf(split2[2]));
									ip.setIp4(Integer.valueOf(split2[3]));
									ip.setIp(IPUtil.getIpFromString(s1));
									ip.setCreate_time(sdf.format(new Date()));
									ip.setMac(mac);
									ip.setMask32(24);
									ip.setCreate_type(0);
									list.add(ip);
									break;
								}
							}
						}
					}
					tNode.setCreate_time(sdf.format(new Date()));
					map.put(tNode, list);
				}

			}
		}
		return map;
	}

	@Override
	public int addIpList(List<IpMain> list) {
		// TODO Auto-generated method stub
		try {
			for(IpMain ip:list){
				String ipString=ip.getIp1()+"."+ip.getIp2()+"."+ip.getIp3()+"."+ip.getIp4();
				ip.setIp(IPUtil.getIpFromString(ipString));
				int i=ipMainMapper.getcount(ip);
				if(i<1){
					ip.setDepart(new String(ip.getDepart().getBytes("Iso-8859-1"),"utf-8"));
					if(ip.getDepart_id()==null){
						Depart depart=departMapper.findIdByName(ip.getDepart());
						
						if(depart!=null){
							ip.setDepart_id(depart.getId());
						}else {
							ip.setDepart(null);
						}
					}
					ip.setCreate_time(sdf.format(new Date()));
					ipMainMapper.insertIp(ip);
				}
			}
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			return 0;
		}


	}

	@Override
	public void export(HttpServletResponse response, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		ExcelUtil<IpMain> excelUtil=new ExcelUtil<>();
		List<ExcelHeader> headers = new ArrayList<>();
		headers.add(new ExcelHeader("extr1","IP地址"));
		headers.add(new ExcelHeader("mac","MAC地址"));
		headers.add(new ExcelHeader("mask32","32位掩码"));
		headers.add(new ExcelHeader("depart","部门名称"));
		String filename="ip"+new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".xls";
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		WebUtil.initHttpServletResponse(filename, response, request);
		List<IpMain> list=new ArrayList<>();
		IpMain ipMain=new IpMain();
		ipMain.setExtr1("172.16.1.1");
		ipMain.setMac("7C:2A:31:A6:1B:E8");
		ipMain.setMask32(null);
		ipMain.setDepart("");
		list.add(ipMain);
		excelUtil.exportExcel("IP-Address", headers,list, response.getOutputStream());
	}

	@Override
	public String inputFile(InputStream inputStream) {
		// TODO Auto-generated method stub
		final StringBuilder emsg = new StringBuilder();
		ExcelUtil<IpMain> exportUtil = new ExcelUtil<IpMain>();
		List<IpMain> ips = null;
		try{
			ReadExcelArgs args=new ReadExcelArgs(inputStream, new IExcelConvert() {

				@Override
				public Object convert(List<String> cellsValue, int rowIndex) {
					IpMain ip = new IpMain();
					ip.setCreate_time(sdf.format(new Date()));
					ip.setCreate_type(1);
					
					rowIndex += 1;
					String extr1 = cellsValue.get(0).trim();
					if(StringUtils.isEmpty(extr1)){
						return null;
					}else{
						boolean flag=false;
						Long longIp=null;
						try {
							 longIp= IPUtil.getIpFromString(extr1);
							 flag=true;
							 logger.error("extr1"+extr1);
						} catch (Exception e) {
							// TODO: handle exception
							logger.error(e);
						}
						if(flag){
							ip.setIp(longIp);
							String[] split = extr1.split("\\.");
							ip.setIp1(Integer.valueOf(split[0]));
							ip.setIp2(Integer.valueOf(split[1]));
							ip.setIp3(Integer.valueOf(split[2]));
							ip.setIp4(Integer.valueOf(split[3]));
						}else {
							emsg.append("第" + rowIndex + "行:IP地址【" + extr1 + "】不合格");
							return null;
						}
					}
					String mac = cellsValue.get(1).trim();
					if(StringUtils.isEmpty(mac)){
						return null;
					}else{
						logger.error("mac"+mac);
						String[] split = mac.split(":");
						if (split.length<6) {
							emsg.append("第" + rowIndex + "行:MAC地址【" + mac + "】使用':'分割，且分为6段");
							return null;
						}
						ip.setMac(mac);
					}
					String mask32 = cellsValue.get(2).trim();
					if(StringUtils.isEmpty(mask32)){
						return null;
					}else{
					if (Integer.valueOf(mask32)>32||Integer.valueOf(mask32)<0) {
						emsg.append("第" + rowIndex + "行:掩码【" + mask32 + "】必须为32位格式");
						return null;
					}
					logger.error("mask32"+mask32);
						ip.setMask32(Integer.valueOf(mask32));
					}
					String depart = cellsValue.get(3).trim();
					if(StringUtils.isEmpty(depart)){
						return null;
					}else{
						ip.setDepart(depart);
					}
					logger.error("ip::"+ip.getExtr1()+"\t"+ip.getIp());
					return ip;
				}
			});

			args.setIgnoreRows(1);
			ips = exportUtil.readExcelContent(args);
		}catch(Exception e){
			e.printStackTrace();
			emsg.append("导入失败！！");
		}
		String errorMsg = emsg.toString();
		if(StringUtils.isNotBlank(errorMsg)){
			return errorMsg;
		}
		try {
			if(ips!=null){
//				logger.error(ips.size());
//				logger.error(ips.get(0));
			}
			for(IpMain ip:ips){
				ip.setDepart(new String(ip.getDepart().getBytes("Iso-8859-1"),"utf-8"));
//				logger.error(ip.getExtr1()+"\t"+ip.getDepart()+"\t"+ip.getMac()+"\t");
//				logger.error(ip.getIp1()+"\t"+ip.getIp2()+"\t"+ip.getIp3()+"\t"+ip.getIp4()+"\t"+ip.getIp());
				Depart depart=departMapper.findIdByName(ip.getDepart().trim());
				if(depart!=null){
					ip.setDepart_id(depart.getId());
				}else {
					ip.setDepart(null);
				}
				ip.setHas_subnet(0);
				ipMainMapper.insertIp(ip);
			}
			return "success";
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			return "error";
		}
		
		
	}
}
