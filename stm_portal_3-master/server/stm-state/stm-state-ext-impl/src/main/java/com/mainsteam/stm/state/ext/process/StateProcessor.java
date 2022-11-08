package com.mainsteam.stm.state.ext.process;

import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;

/**
 * Created by Xiaopf on 2017/7/11.
 * 状态计算处理接口
 */
public interface StateProcessor {

    Object process(StateComputeContext context);

    StateProcessorEnum processOrder();
}
