package com.mainsteam.stm.dsl.expression.antlr4;

import com.mainsteam.stm.dsl.expression.OCELBooleanExpression;
import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.antlr4.exception.EvaluatorException;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OCELBooleanExpressionANLTRImpl extends OCELExpressionANTLRImpl implements OCELBooleanExpression {

    private static final Log LOGGER = LogFactory.getLog(OCELExpressionANTLRImpl.class);

    public OCELBooleanExpressionANLTRImpl(String expression) throws OCELException {
        super(expression);
    }

    @Override
    public Boolean evaluate(OCELContext context) throws EvaluatorException {
        Object o = super.evaluate(context);
        if (o instanceof Boolean) {
            return (Boolean) o;

        } else {
            LOGGER.error("expression:" + getExpression() + "\tcontext:" + context + " should be a boolean. result:" + o);
            throw new EvaluatorException("expression:" + getExpression() + "\tcontext:" + context + " should be a boolean. result:" + o);
        }
    }

    @Override
    public Boolean evaluate(OCELContext context, OCELContext lastContext) throws OCELException {
        Object o = super.evaluate(context, lastContext);
        if (o instanceof Boolean) {
            return (Boolean) o;

        } else {
            LOGGER.error("expression:" + getExpression() + "\tcontext:" + context + "\tlastContext:" + lastContext + " should be a boolean. result:" + o);
            throw new EvaluatorException("expression:" + getExpression() + "\tcontext:" + context + "\tlastContext:" + lastContext + " should be a boolean. result:" + o);
        }
    }

    @Override
    public OCELBooleanExpression getCause() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
