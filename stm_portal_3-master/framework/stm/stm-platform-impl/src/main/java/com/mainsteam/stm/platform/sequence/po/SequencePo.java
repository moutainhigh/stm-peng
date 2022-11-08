package com.mainsteam.stm.platform.sequence.po;

/**
 * <li>文件名称: SequencePo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
public class SequencePo {
	private String seqName;
	private Long curVal;
	private Integer cacheCount;
	
	public String getSeqName() {
		return seqName;
	}
	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}
	public Long getCurVal() {
		return curVal;
	}
	public void setCurVal(Long curVal) {
		this.curVal = curVal;
	}
	public Integer getCacheCount() {
		return cacheCount;
	}
	public void setCacheCount(Integer cacheCount) {
		this.cacheCount = cacheCount;
	}
}


