package com.mainsteam.stm.system.scriptmanage.api;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;

/**
 * <li>文件名称: IScriptManageApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年5月20日
 * @author   tongpl
 */

public interface IScriptManageApi {
	/*
	 * 保存脚本
	 * */
	public boolean saveScriptManage(MultipartFile file,int scriptType,String discription ,ILoginUser user);
	
	/*
	 * 删除
	 * */
	public boolean delScriptManage(Long[] scriptId,Long[] fileId);
	/*
	 * 分页查询所有
	 * */
	public void loadAllByPage(Page<ScriptManage,ScriptManage> page);
	/*
	 * 按类型查询所有
	 * */
	public List<ScriptManage> loadAllByTypeCode(ScriptManageTypeEnum type);
	
	/*
	 * 根据文件名查询
	 * */
	public List<ScriptManage> loadBydocName(String docName);
	
	/*
	 * 根据文件名和类型查询
	 * */
	public List<ScriptManage> loadBydocNameAndtype(String docName,int scriptTypeCode);
	
	/*
	 * 根据脚本ID查询
	 * */
	public ScriptManage loadByscriptId(long scriptId);
	
	/*
	 * 更新scriptId更新
	 * */
	public boolean update(ScriptManage scriptManage);
	
	/*
	 * 获取脚本限定的类型
	 * */
	public String[] getUploadScriptFileType();
}
