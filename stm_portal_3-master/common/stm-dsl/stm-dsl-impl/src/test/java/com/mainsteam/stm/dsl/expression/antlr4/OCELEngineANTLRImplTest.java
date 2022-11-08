package com.mainsteam.stm.dsl.expression.antlr4;

import com.mainsteam.stm.dsl.expression.MapContext;
import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.OCELEngine;
import com.mainsteam.stm.dsl.expression.OCELExpression;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.junit.Test;

public class OCELEngineANTLRImplTest {

    private final OCELEngine engine = new OCELEngineANTLRImpl();

    @Test
    public void createExpression() {
        OCELContext context = new MapContext();
        OCELExpression expression;
        try {
            context.clear();
            expression = engine.createExpression("cpuRate = \"7.00\"");
            context.put("cpuRate", 7.0);
            System.out.println(expression.evaluate(context));

            context.clear();
            expression = engine.createExpression("cpuRate > \"7e-50\"");
            context.put("cpuRate", 7.0);
            System.out.println(expression.evaluate(context));

            context.clear();
            expression = engine.createExpression("cpuRate <= \"1000000000000000000.00\"");
            context.put("cpuRate", 7.0);
            System.out.println(expression.evaluate(context));


            context.clear();
            expression = engine.createExpression("cpuRate >= \"3/0\"");
            context.put("cpuRate", 3.0);
            System.out.println(expression.evaluate(context));

            context.clear();
            expression = engine.createExpression("availability != \"正常\"");
            context.put("availability", "正常");
            System.out.println(expression.evaluate(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
