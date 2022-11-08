package com.mainsteam.stm.platform.sequence.dao;

import java.util.List;

import com.mainsteam.stm.platform.sequence.po.SequencePo;

/**
 * <li>文件名称: ISequenceDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
public interface ISequenceDao {
	public int insert(String seqName);
	public int update(SequencePo seq);
	public SequencePo get(String seqName);
	public List<SequencePo> getAll();
}


