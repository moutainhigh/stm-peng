package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.Map;

import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;

/**
 * 
 * {class description}
 * <br>
 *  
 * <p>
 * Create on : 2012-9-13<br>
 * <p>
 * </p>
 * <br>
 * @author chuzunying@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps-6.2.0 v1.0
 * <p>
 *<br>
 * <strong>Modify History:</strong><br>
 * user     modify_date    modify_content<br>
 * -------------------------------------------<br>
 * <br>
 */
public class LdapStatement extends Statement {

    /**
     * 
     * Constructors.
     * @param helper 
     * @param connInfo 
     */
    public LdapStatement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        super(helper, connInfo);
    }

    @Override
    public Object execute(final String operation, final Map<String, String> params, final String subname)
        throws PluginException {
        return null;
    }

}
