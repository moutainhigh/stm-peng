// Generated from F:/OC4/ITM_trunk/Common/oc-dsl/oc-dsl-impl/src/main/java/com.mainsteam.stm/dsl/expression/antlr4/gen\ExprParser.g4 by ANTLR 4.7
package com.mainsteam.stm.dsl.expression.antlr4.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSingleExpression(ExprParser.SingleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSingleExpression(ExprParser.SingleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExprParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExprParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparisonPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterComparisonPredicate(ExprParser.ComparisonPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparisonPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitComparisonPredicate(ExprParser.ComparisonPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code isNullPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterIsNullPredicate(ExprParser.IsNullPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code isNullPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitIsNullPredicate(ExprParser.IsNullPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code containsPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterContainsPredicate(ExprParser.ContainsPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code containsPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitContainsPredicate(ExprParser.ContainsPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code hasChangedPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterHasChangedPredicate(ExprParser.HasChangedPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code hasChangedPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitHasChangedPredicate(ExprParser.HasChangedPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compoundPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterCompoundPredicate(ExprParser.CompoundPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compoundPredicate}
	 * labeled alternative in {@link ExprParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitCompoundPredicate(ExprParser.CompoundPredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(ExprParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(ExprParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#compoundExpression}.
	 * @param ctx the parse tree
	 */
	void enterCompoundExpression(ExprParser.CompoundExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#compoundExpression}.
	 * @param ctx the parse tree
	 */
	void exitCompoundExpression(ExprParser.CompoundExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(ExprParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(ExprParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(ExprParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(ExprParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(ExprParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(ExprParser.ConstantContext ctx);
}
