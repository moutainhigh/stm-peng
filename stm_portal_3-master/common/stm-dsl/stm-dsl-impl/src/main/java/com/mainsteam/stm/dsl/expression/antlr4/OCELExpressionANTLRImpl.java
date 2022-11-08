package com.mainsteam.stm.dsl.expression.antlr4;

import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.OCELExpression;
import com.mainsteam.stm.dsl.expression.antlr4.exception.EvaluatorException;
import com.mainsteam.stm.dsl.expression.antlr4.exception.LexerException;
import com.mainsteam.stm.dsl.expression.antlr4.exception.ParserException;
import com.mainsteam.stm.dsl.expression.antlr4.gen.ExprLexer;
import com.mainsteam.stm.dsl.expression.antlr4.gen.ExprParser;
import com.mainsteam.stm.dsl.expression.antlr4.gen.ExprParserBaseListener;
import com.mainsteam.stm.dsl.expression.antlr4.impl.ExprEvaluator;
import com.mainsteam.stm.dsl.expression.antlr4.impl.RecordErrorListener;
import com.mainsteam.stm.dsl.expression.exception.OCELException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OCELExpressionANTLRImpl implements OCELExpression {

    private static final Log LOGGER = LogFactory.getLog(OCELExpressionANTLRImpl.class);

    private final String expression;
    private final ExprParser parser;
    private final ParserRuleContext tree;

    private boolean checkChange;

    public OCELExpressionANTLRImpl(String expression) throws OCELException {
        if (expression.length() > OCELExpression.MAX_LENGTH) {
            LOGGER.error("expression too long. expression:" + expression + ".");
            throw new LexerException(expression, "too long.");
        }
        this.expression = expression;
        CharStream stream = CharStreams.fromString(expression);
        RecordErrorListener listener = new RecordErrorListener();
        Lexer lexer = new ExprLexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(listener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        if (!listener.toString().isEmpty()) {
            LOGGER.error("expression lexer error. expression:" + expression + ". cause:" + listener.toString());
            throw new LexerException(expression, listener.toString());
        }
        parser = new ExprParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(listener);
        parser.addParseListener(new ExprParserBaseListener() {
            @Override
            public void exitHasChangedPredicate(ExprParser.HasChangedPredicateContext ctx) {
                checkChange = true;
            }
        });
        tree = parser.singleExpression();
        if (!listener.toString().isEmpty()) {
            LOGGER.error("expression parser error. expression:" + expression + ". cause:" + listener.toString());
            throw new ParserException(expression, listener.toString());
        }
    }

    @Override
    public Object evaluate(OCELContext context) throws EvaluatorException {
        ParseTreeWalker walker = new ParseTreeWalker();
        ExprEvaluator evaluator = new ExprEvaluator(context);
        try {
            walker.walk(evaluator, tree);
        } catch (Exception e) {
            LOGGER.error("expression:" + expression + "\tcontext:" + context, e);
            throw new EvaluatorException("expression:" + expression + "\tcontext:" + context, e);
        }
        return evaluator.loadValue(tree);
    }

    @Override
    public Object evaluate(OCELContext context, OCELContext lastContext) throws OCELException {
        ParseTreeWalker walker = new ParseTreeWalker();
        ExprEvaluator evaluator = new ExprEvaluator(context, lastContext);
        try {
            walker.walk(evaluator, tree);
        } catch (Exception e) {
            LOGGER.error("expression:" + expression + "\tcontext:" + context + "\tlastContext:" + lastContext, e);
            throw new EvaluatorException("expression:" + expression + "\tcontext:" + context + "\tlastContext:" + lastContext, e);
        }
        return evaluator.loadValue(tree);
    }

    @Override
    public boolean checkChange() {
        return checkChange;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return tree.toStringTree(parser);
    }
}
