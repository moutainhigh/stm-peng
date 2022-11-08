package com.mainsteam.stm.dsl.expression.jexl;

import com.mainsteam.stm.dsl.expression.OCELBooleanExpression;
import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.apache.commons.jexl2.Expression;

public class OCELBooleanExpressionJEXLImpl extends OCELExpressionJEXLImpl implements OCELBooleanExpression {
    public OCELBooleanExpressionJEXLImpl(Expression expression) {
        super(expression);
    }

    @Override
    public Boolean evaluate(OCELContext context) {
        return (Boolean) super.evaluate(context);
    }

    @Override
    public Boolean evaluate(OCELContext context, OCELContext lastContext) throws OCELException {
        return (Boolean) super.evaluate(context, lastContext);
    }

    @Override
    public OCELBooleanExpression getCause() {
        throw new UnsupportedOperationException("JEXL can't get the cause.");
    }
}
