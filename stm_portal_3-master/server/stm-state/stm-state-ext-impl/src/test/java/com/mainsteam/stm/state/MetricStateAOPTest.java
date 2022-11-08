package com.mainsteam.stm.state;

import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.state.ext.StateComputeDispatcher;
import com.mainsteam.stm.state.ext.tools.SpringCatchBeanUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Xiaopf on 2017/7/10.
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages="com.mainsteam.stm.state.ext")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:applicationContext.xml"})
public class MetricStateAOPTest extends TestCase{

    @Autowired
    MetricDataProcessor stateComputeDispatcher;
    @org.junit.Test
    public void test() throws Exception {

        MetricCalculateData calculateData = new MetricCalculateData(new String[]{"1"}, "cpuRate", 1000L, new Date(),
                "Windowswmi");
        try {
            stateComputeDispatcher.process(calculateData, null, null, new HashMap<String, Object>());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
