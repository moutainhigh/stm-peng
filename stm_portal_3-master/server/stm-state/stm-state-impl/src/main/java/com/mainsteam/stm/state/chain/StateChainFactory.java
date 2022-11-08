/**
 * 
 */
package com.mainsteam.stm.state.chain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mainsteam.stm.util.SpringBeanUtil;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午10:24:52
 * @version 1.0
 */
/** 
 */
public class StateChainFactory {

	private static final String CHAIN_DEFINE_FILE = "/stateChain.properties";

	private static Map<String, StateChainStepContextRunner> chainMap;

	/**
	 * 
	 */
	public StateChainFactory() {
	}

	public static StateChainStepContextRunner findStateComputeChain(String chainName)
			throws IOException {
		findStateComputeChains();
		return chainMap.get(chainName);
	}

	private synchronized static void findStateComputeChains()
			throws IOException {
		if (chainMap != null) {
			return;
		}
		InputStream in = StateChainFactory.class
				.getResourceAsStream(CHAIN_DEFINE_FILE);
		Map<String, List<String>> blockMap = BlockPropertiesReader
				.readBlockValues(in);
		chainMap = new HashMap<String, StateChainStepContextRunner>(blockMap.size());
		for (Entry<String, List<String>> entry : blockMap.entrySet()) {
			String chainName = entry.getKey();
			List<String> chainStepSpringBeanIds = entry.getValue();
			List<StateChainStep> stateChainSteps = new ArrayList<>(
					chainStepSpringBeanIds.size());
			for (String springBeanId : chainStepSpringBeanIds) {
				StateChainStep step = selectStateChainStepFromSpringContext(springBeanId);
				stateChainSteps.add(step);
			}

			StateChainStepContextRunner computeChain = new DefaultStateComputeChain(
					stateChainSteps.toArray(new StateChainStep[stateChainSteps
							.size()]));
			chainMap.put(chainName, computeChain);
		}
	}

	private static StateChainStep selectStateChainStepFromSpringContext(
			String springBeanId) {
		return (StateChainStep) SpringBeanUtil.getObject(springBeanId);
	}
}
