package com.mainsteam.stm.platform.sequence.service.impl;
 
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.platform.sequence.dao.ISequenceDao;
import com.mainsteam.stm.platform.sequence.po.SequencePo;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * <li>文件名称: SequenceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
public class SequenceImpl implements ISequence {

	private String seqName;
	private ISequenceDao seqDao;
	private long curVal;
	private int capacity;
	private LockService lockService;
	private final String sequenceLockMsg="SequenceOfGlobalLock";
	
	
	public SequenceImpl(String seqName,ISequenceDao seqDao,LockService lockService){
		this.seqDao=seqDao;
		this.seqName=seqName;
		this.lockService=lockService;
		
		SequencePo seq=seqDao.get(seqName);
		if(seq==null){
			seqDao.insert(seqName);
			seq=seqDao.get(seqName);
		}
		if(seq != null){
			curVal=seq.getCurVal();
			capacity=seq.getCacheCount();
			
			seq.setCurVal(seq.getCurVal()+seq.getCacheCount());
			seqDao.update(seq);
		}
	}
	
	@Override
	public synchronized long next() {
		if(capacity==0){
			update();
		}
		capacity--;
		return curVal++;
	}

	private void update(){
		lockService.sync(sequenceLockMsg,new LockCallback<Object>() {
			@Override
			public Object doAction() {
				SequencePo seq=seqDao.get(seqName);
				if(seq==null){
					seqDao.insert(seqName);
					seq=seqDao.get(seqName);
				}
				curVal=seq.getCurVal();
				capacity=seq.getCacheCount();
				
				seq.setCurVal(seq.getCurVal()+seq.getCacheCount());
				seqDao.update(seq);
				return null;
			}
		}, 1);
	}

	@Override
	public long current() {
		return curVal-1;
	}
}
