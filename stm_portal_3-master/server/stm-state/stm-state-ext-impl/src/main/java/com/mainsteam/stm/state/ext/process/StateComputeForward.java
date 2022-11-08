package com.mainsteam.stm.state.ext.process;

import com.mainsteam.stm.state.ext.StateComputeContext;

/**
 * 状态计算调度接口，实际上这是一个冗余接口，因为系统自身的缺陷性，使得Spring AOP没有办法使用CGLIB代理，
 * 故只能使用JDK的动态代理，所以需要一个接口
 */
public interface StateComputeForward {

    void fireStateCompute(StateComputeContext context);
}
