package com.mainsteam.stm.state.chain;



/**
 * 状态计算的逻辑单元
 * 
 * @author 作者：ziw
 * @date 创建时间：2016年11月15日 上午10:42:58
 * @version 1.0
 */
public interface StateChainStep {
	public void doStepChain(StateChainStepContext context,
			StateComputeChain chain);
}
