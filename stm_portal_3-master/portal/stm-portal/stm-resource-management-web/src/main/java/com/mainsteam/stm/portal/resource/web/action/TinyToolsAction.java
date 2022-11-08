package com.mainsteam.stm.portal.resource.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.IMetricApi;
import com.mainsteam.stm.portal.resource.bo.ArpTablePageBo;
import com.mainsteam.stm.portal.resource.bo.NetstatPageBo;
import com.mainsteam.stm.portal.resource.bo.RollbackPageBo;
import com.mainsteam.stm.portal.resource.bo.RouteTablePageBo;
import com.mainsteam.stm.portal.resource.bo.SessionPageBo;
import com.mainsteam.stm.portal.resource.web.vo.SnmpTargetVo;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.tinytool.TinyTool;
import com.mainsteam.stm.tinytool.TinyToolMBean;
import com.mainsteam.stm.tinytool.obj.SnmpTarget;


@Controller
@RequestMapping("/portal/resource/tinyTools")
public class TinyToolsAction extends BaseAction {

	@Autowired
	private IMetricApi metricApi;
	
	@Resource
	private OCRPCClient OCRPClient;
	
	@Resource
	private LocaleNodeService localNodeService;
	private Logger logger = Logger.getLogger(TinyToolsAction.class);
	private static String OS = System.getProperty("os.name").toLowerCase();  
	/**
	 * 获取远程 Tool
	 * @return
	 */
	public TinyToolMBean getRemotetool(int nodeGroupId){
		Node node=null;
		TinyToolMBean tool=null;
		try {
			node = localNodeService.getLocalNodeTable().getNodeInGroup(nodeGroupId);
			logger.error("node: "+node);
			if(node!=null){
				tool=OCRPClient.getRemoteSerivce(node, TinyToolMBean.class);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getRemotetool: "+e);
		}
		
		return tool;
	}
	
	@RequestMapping("/ping")
	public JSONObject ping(String cmd,String nodeGroupId) {
		TinyToolMBean tool=getRemotetool(Integer.parseInt(nodeGroupId));

		if(tool==null){
			tool=new TinyTool();
		}
		
		String reply = "Only excute ping command.";
		try {
			if (cmd == null || cmd.length() == 0) {
				reply = "Can't excute null command.";
			} else if (cmd.indexOf("-t") >= 0) {
				reply = "Can't excute 'ping ipaddress -t' command.";
			} else if (cmd.indexOf("ping") == 0) {
				if(OS.indexOf("windows")>=0){//WINDOW
				}else{
					cmd=cmd+" -c 5";
				} 
				logger.error("cmd: "+cmd);
				reply=tool.ping(cmd);
			}
		} catch (Exception e) {
			reply=cmd +" faild.";
			e.printStackTrace();
			logger.error("ping: "+e);
		}
		return toSuccess(reply);
	}
	
	@RequestMapping("/tracert")
	public JSONObject tracert(String cmd,String nodeGroupId){
		TinyToolMBean tool=getRemotetool(Integer.parseInt(nodeGroupId));
		if(tool==null){
			tool=new TinyTool();
		}
		String reply = "Only excute tracert command.";
		try {
			if (cmd == null || cmd.length() == 0) {
				reply = "Can't excute null command.";
			}else if (cmd.indexOf("tracert") == 0) {				
				reply=tool.cmd(cmd);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return toSuccess(reply);
	}

	
	
	@RequestMapping("/arptable")
	public JSONObject arptable(long instanceId){
		ArpTablePageBo pageBo=null;
		
		try {
			pageBo=metricApi.getArpTableByInstanceId(instanceId);
		} catch (MetricExecutorException e) {
			e.printStackTrace();
		}
		return toSuccess(pageBo);
	}
	
	@RequestMapping("/arptableExportExcel")
	public String arptableExportExcel(long instanceId, HttpServletResponse response, HttpServletRequest request){
		try {
			metricApi.arptableExportExcel(instanceId, response, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping("/routetable")
	public JSONObject routeTable(long instanceId){
		RouteTablePageBo pageBo=null;
		try {
			pageBo=metricApi.getRouteTableByInstanceId(instanceId);
		} catch (MetricExecutorException e) {
			e.printStackTrace();
		}
		return toSuccess(pageBo);
	}

	@RequestMapping("/routetableExportExcel")
	public String routetableExportExcel(long instanceId, HttpServletResponse response, HttpServletRequest request){
		try {
			metricApi.routetableExportExcel(instanceId, response, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("/netstat")
	public JSONObject netstat(long instanceId){

		NetstatPageBo pageBo=metricApi.getNetstatByInstanceId(instanceId);
		
		return toSuccess(pageBo);
	}
	
	
	@RequestMapping("/snmpTest")
	public JSONObject snmpTest(SnmpTargetVo snmpTargetVo){
		
		TinyToolMBean tool=getRemotetool(Integer.parseInt(snmpTargetVo.getDiscoverNode()));
		if(tool==null){
			tool=new TinyTool();
		}
		List<String> resultList=new ArrayList<String>();
		
		SnmpTarget target=new SnmpTarget();
		
		BeanUtils.copyProperties(snmpTargetVo, target);
		try {
			resultList=tool.snmpTest(target);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
        return toSuccess(resultList);
	}

	@RequestMapping("/session")
	public JSONObject session(long instanceId){
		SessionPageBo pageBo=null;
		try {
			pageBo=metricApi.getSessionByInstanceId(instanceId);
		} catch (MetricExecutorException e) {
			e.printStackTrace();
		}
		return toSuccess(pageBo);
	}

	@RequestMapping("/rollback")
	public JSONObject rollback(RollbackPageBo pageBo){
		try {
			metricApi.getRollbackByInstanceId(pageBo);
		} catch (MetricExecutorException e) {
			e.printStackTrace();
		}
		return toSuccess(pageBo);
	}
}
