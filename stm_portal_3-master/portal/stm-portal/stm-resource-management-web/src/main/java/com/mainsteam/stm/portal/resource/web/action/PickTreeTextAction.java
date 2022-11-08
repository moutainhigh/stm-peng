package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.web.vo.zTreeVo;

/**
 * <li>文件名称: PickTreeTextAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年7月28日
 * @author   zjf
 */
@Controller
@RequestMapping("/test")
public class PickTreeTextAction extends BaseAction {

	@RequestMapping("/pickTreeTest")
	public JSONObject pickTreeTest(String id){
		List<zTreeVo> treeList=new ArrayList<zTreeVo>();
		for(int i=1;i<=200;i++){
			zTreeVo treeVo=new zTreeVo();
			treeVo.setId(String.valueOf(i));
			treeVo.setName("测试父节点"+i);
			treeVo.setIsParent(true);
			
			List<zTreeVo> cTreeList= new ArrayList<zTreeVo>();
			int k= Integer.valueOf(i)*200;
			for(int j=k;j<=k+200;j++){
				zTreeVo ctreeVo=new zTreeVo();
				ctreeVo.setId(String.valueOf(j));
				ctreeVo.setName("测试子节点"+j);
				ctreeVo.setPId(treeVo.getId());
				ctreeVo.setIsParent(false);
				cTreeList.add(ctreeVo);
			}
			treeVo.setChildren(cTreeList);
			treeList.add(treeVo);
		}
		
		return toSuccess(JSONObject.toJSON(treeList));
		
	}
}
