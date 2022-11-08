package com.mainsteam.stm.state.chain;
/** 
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 下午12:07:24
 * @version 1.0
 */
public interface StateChainStepContextRunner extends StateComputeChain {
	public void startRun(StateChainStepContext chainStepContext);
}
