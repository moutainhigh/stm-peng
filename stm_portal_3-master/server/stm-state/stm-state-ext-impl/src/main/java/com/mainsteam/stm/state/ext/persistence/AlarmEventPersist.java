package com.mainsteam.stm.state.ext.persistence;

import com.mainsteam.stm.state.ext.process.bean.AlarmEventBean;

import java.util.Set;

/**
 * Created by Xiaopf on 2017/7/21.
 */
public interface AlarmEventPersist {

     void offer(final AlarmEventBean event);

     AlarmEventBean poll();

     Set<String> occurEvent(final AlarmEventBean event) ;

     Set<String> addTransactionalEvent(final AlarmEventBean eventBean);
}
