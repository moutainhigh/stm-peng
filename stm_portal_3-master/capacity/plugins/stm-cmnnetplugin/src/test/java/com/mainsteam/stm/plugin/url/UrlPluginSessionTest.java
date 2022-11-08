package com.mainsteam.stm.plugin.url;

import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.junit.Test;

/**
 * Created by Lich on 2017/5/2.
 */
public class UrlPluginSessionTest {

    @Test
    public void execute() throws Exception {
        UrlPluginSession session = new UrlPluginSession();
        session.init(new PluginInitParameter() {
            @Override
            public Parameter[] getParameters() {
                return new Parameter[]{new Parameter() {
                    @Override
                    public String getType() {
                        return "";
                    }

                    @Override
                    public String getKey() {
                        return UrlPluginSession.URL;
                    }

                    @Override
                    public String getValue() {
                        return "http://www.tju.edu.cn";
                    }
                }};
            }

            @Override
            public String getParameterValueByKey(String key) {
                return null;
            }
        });
        System.out.println(session.execute(new PluginArrayExecutorParameter(), null));
    }

}
