package com.mainsteam.stm.portal.vm.web.action;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.vm.api.TopologyVmService;
import com.mainsteam.stm.portal.vm.bo.VmResourceTreeBo;

/**
 * <li>文件名称: TopologyVmAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月13日
 * @author   caoyong
 */
@Controller
@RequestMapping("portal/vm/topologyVm")
public class TopologyVmAction extends BaseAction {
	@Resource
	private TopologyVmService topologyVmService;
	
	/**
	 * 获取topology左侧导航栏数据中心结构
	 * @param objects 查询参数
	 * @return
	 */
	@RequestMapping("/getLeftNavigateTree")
	Object getLeftNavigateTree(){
		return toSuccess(topologyVmService.getLeftNavigateTree());
	}
	
	/**
	 * 获取topology关系结构图数据
	 * @param objects 查询参数
	 * @return
	 */
	@RequestMapping("/getTopologyData")
	Object getTopologyData(VmResourceTreeBo bo){
		return toSuccess(topologyVmService.getTopologyData(bo));
	}
}
