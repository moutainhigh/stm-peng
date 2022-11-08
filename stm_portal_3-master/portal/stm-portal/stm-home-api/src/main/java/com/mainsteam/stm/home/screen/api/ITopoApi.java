package com.mainsteam.stm.home.screen.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: ITopoApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   ziwenwen
 */
public interface ITopoApi {
	/**
	 * <pre>
	 * 获取用户能够查看的所有top、业务视图
	 * biz需设置
	 * bizId
	 * title
	 * bizType 业务类型 1-业务管理；2-拓扑管理 3-首页工作台
	 * thumbnail  缩略图id或者svg缩略图内容
	 * content 拓扑svg数据
	 * </pre>
	 * @param user
	 * @return
	 */
	List<Biz> getBizs(ILoginUser user,HttpServletRequest request);
}


