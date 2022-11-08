package com.mainsteam.stm.dsl.expression.jexl;

import com.mainsteam.stm.dsl.expression.MapContext;
import com.mainsteam.stm.dsl.expression.OCELContext;

import static org.junit.Assert.*;

public class OCELEngineJEXLImplTest {
    @org.junit.Test
    public void createBooleanExpression() throws Exception {
        OCELEngineJEXLImpl engineJEXL = new OCELEngineJEXLImpl();
        OCELContext context = new MapContext();
        context.set("cpuRate", 80);
        System.out.println(engineJEXL.createBooleanExpression("cpuRate > \"20\" and cpuRate < 100").evaluate(context));
    }

}
