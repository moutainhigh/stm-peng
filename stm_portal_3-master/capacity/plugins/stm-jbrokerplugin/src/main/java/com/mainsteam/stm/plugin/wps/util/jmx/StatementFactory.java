package com.mainsteam.stm.plugin.wps.util.jmx;

import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.internalimpl.StatementFactoryImpl;

/**
 * 
 * {class description}
 * <br>
 *  
 * <p>
 * Create on : 2012-7-22<br>
 * <p>
 * </p>
 * <br>
 * @author chuzunying@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps v1.0
 * <p>
 *<br>
 * <strong>Modify History:</strong><br>
 * user     modify_date    modify_content<br>
 * -------------------------------------------<br>
 * <br>
 */
public abstract class StatementFactory {

    /**
     * Obtain a new instance of a <code>StatementFactory</code>. 
     * This static method creates a new factory instance.
     * @return a instance;
     */
    public static StatementFactoryImpl newInstance() {
        return new StatementFactoryImpl();
    }
    
    
    
    /**
     * create statement object.
     * @param name statement name.
     * @param connInfo connection information
     * @return statement object.
     */
    public abstract Statement newStatement(String name, WPSConnectionInfo connInfo);

}
