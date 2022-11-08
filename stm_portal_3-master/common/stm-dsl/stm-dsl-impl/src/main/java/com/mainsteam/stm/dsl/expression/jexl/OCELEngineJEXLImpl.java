package com.mainsteam.stm.dsl.expression.jexl;

import com.mainsteam.stm.dsl.expression.OCELBooleanExpression;
import com.mainsteam.stm.dsl.expression.OCELEngine;
import com.mainsteam.stm.dsl.expression.OCELExpression;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.apache.commons.jexl2.JexlEngine;

public class OCELEngineJEXLImpl implements OCELEngine {

    private final JexlEngine engine;

    {
        engine = new JexlEngine();
        engine.setLenient(false);
        engine.setSilent(false);
    }

    @Override
    public void init() {

    }

    @Override
    public OCELExpression createExpression(String expression) throws OCELException {
        return new OCELExpressionJEXLImpl(engine.createExpression(expression));
    }

    @Override
    public OCELBooleanExpression createBooleanExpression(String booleanExpression) throws OCELException {
        return new OCELBooleanExpressionJEXLImpl(engine.createExpression(booleanExpression));
    }
}
