package com.mainsteam.stm.dsl.expression.jexl;

import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.OCELExpression;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

public class OCELExpressionJEXLImpl implements OCELExpression {

    private Expression expression;

    public OCELExpressionJEXLImpl(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(OCELContext context) {
        JexlContext jexlContext = new MapContext();
        if (context instanceof com.mainsteam.stm.dsl.expression.MapContext) {
            for (Map.Entry<String, Object> entry : ((com.mainsteam.stm.dsl.expression.MapContext) context).entrySet()) {
                jexlContext.set(entry.getKey(), entry.getValue());
            }
        }
        return expression.evaluate(jexlContext);
    }

    @Override
    public Object evaluate(OCELContext context, OCELContext lastContext) throws OCELException {
        throw new UnsupportedOperationException("JEXL can't support the HAS CHANGED.");
    }

    @Override
    public boolean checkChange() {
        throw new UnsupportedOperationException("JEXL can't support the HAS CHANGED.");
    }
}
