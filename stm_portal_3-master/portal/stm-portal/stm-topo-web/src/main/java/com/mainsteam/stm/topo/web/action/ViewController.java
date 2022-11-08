package com.mainsteam.stm.topo.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mainsteam.stm.platform.web.action.BaseAction;
/**
 * 用来服务器返回视图的控制器
 * @author 富强
 *
 */
@Controller
@RequestMapping(value="topo/view")
public class ViewController extends BaseAction{
	/**
	 * 获取子拓扑
	 * @param id 子拓扑id
	 * @return
	 */
	@RequestMapping(value="subtopo/{id}",produces="text/html;charset=UTF-8")
	public String subTopo(@PathVariable(value="id") Long id){
         return "<script type='text/javascript' src='/resource/module/topo/contextMenu/BackBoardInfoDia.js'>new BackBoardInfoDia();</script>";
	}
	@RequestMapping(value="")
	public String test(){
		return null;
	}
}
