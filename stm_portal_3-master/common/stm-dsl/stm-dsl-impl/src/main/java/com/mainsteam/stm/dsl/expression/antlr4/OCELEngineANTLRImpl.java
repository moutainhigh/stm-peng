package com.mainsteam.stm.dsl.expression.antlr4;

import com.mainsteam.stm.dsl.expression.OCELEngine;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OCELEngineANTLRImpl implements OCELEngine {

    @Override
    public void init() {
    }

    @Override
    public OCELExpressionANTLRImpl createExpression(String expression) throws OCELException {
        return new OCELExpressionANTLRImpl(expression);
    }

    @Override
    public OCELBooleanExpressionANLTRImpl createBooleanExpression(String booleanExpression) throws OCELException {
        return new OCELBooleanExpressionANLTRImpl(booleanExpression);
    }
}
