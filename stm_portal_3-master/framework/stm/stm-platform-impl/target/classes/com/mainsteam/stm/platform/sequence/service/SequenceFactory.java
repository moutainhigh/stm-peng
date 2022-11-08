package com.mainsteam.stm.platform.sequence.service;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.platform.sequence.dao.ISequenceDao;
import com.mainsteam.stm.platform.sequence.service.impl.SequenceImpl;

/**
 * <li>文件名称: SequenceFactory.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月30日
 * @author   ziwenwen
 */
public class SequenceFactory {
	public static final String SPRING_BEAN_NAME="platformSeqFactory";
	private Map<String,ISequence> seqs=new HashMap<String, ISequence>();
	private ISequenceDao seqDao;
	private LockService lockService;

	public SequenceFactory(ISequenceDao seqDao,LockService lockService){
		this.seqDao=seqDao;
		this.lockService=lockService;
	}
	
	public SequenceFactory(){}
	
	public ISequence getSeq(String seqName){
		ISequence seq=seqs.get(seqName);
		if(seq==null){
			seq=new SequenceImpl(seqName, seqDao,lockService);
			seqs.put(seqName, seq);
		}
		return seq;
	}
}
