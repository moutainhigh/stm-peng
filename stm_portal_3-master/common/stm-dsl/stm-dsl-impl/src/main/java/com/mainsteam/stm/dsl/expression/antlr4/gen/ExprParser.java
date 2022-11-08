// Generated from F:/OC4/ITM_trunk/Common/oc-dsl/oc-dsl-impl/src/main/java/com.mainsteam.stm/dsl/expression/antlr4/gen\ExprParser.g4 by ANTLR 4.7
package com.mainsteam.stm.dsl.expression.antlr4.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExprParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACES=1, NOT=2, AND=3, XOR=4, OR=5, IS=6, HAS=7, CHANGED=8, CONTAINS=9, 
		HAS_CHANGED=10, NOT_CONTAINS=11, ISNULL=12, NULL=13, TRUE=14, FALSE=15, 
		EQUAL=16, GREATER=17, LESS=18, EXCLAMATION=19, LEFT_BRACKET=20, RIGHT_BRACKET=21, 
		COMMA=22, STAR=23, DIVIDE=24, MODULE=25, PLUS=26, MINUS=27, INTEGER=28, 
		REAL=29, STRING=30, ID=31;
	public static final int
		RULE_singleExpression = 0, RULE_expression = 1, RULE_predicate = 2, RULE_comparisonOperator = 3, 
		RULE_compoundExpression = 4, RULE_atom = 5, RULE_id = 6, RULE_constant = 7;
	public static final String[] ruleNames = {
		"singleExpression", "expression", "predicate", "comparisonOperator", "compoundExpression", 
		"atom", "id", "constant"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'NOT'", "'AND'", "'XOR'", "'OR'", "'IS'", "'HAS'", "'CHANGED'", 
		"'CONTAINS'", "'HAS_CHANGED'", "'NOT_CONTAINS'", "'ISNULL'", "'NULL'", 
		"'TRUE'", "'FALSE'", "'='", "'>'", "'<'", "'!'", "'('", "')'", "','", 
		"'*'", "'/'", "'%'", "'+'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "SPACES", "NOT", "AND", "XOR", "OR", "IS", "HAS", "CHANGED", "CONTAINS", 
		"HAS_CHANGED", "NOT_CONTAINS", "ISNULL", "NULL", "TRUE", "FALSE", "EQUAL", 
		"GREATER", "LESS", "EXCLAMATION", "LEFT_BRACKET", "RIGHT_BRACKET", "COMMA", 
		"STAR", "DIVIDE", "MODULE", "PLUS", "MINUS", "INTEGER", "REAL", "STRING", 
		"ID"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ExprParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExprParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SingleExpressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(ExprParser.EOF, 0); }
		public SingleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterSingleExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitSingleExpression(this);
		}
	}

	public final SingleExpressionContext singleExpression() throws RecognitionException {
		SingleExpressionContext _localctx = new SingleExpressionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_singleExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			expression(0);
			setState(17);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode NOT() { return getToken(ExprParser.NOT, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(ExprParser.AND, 0); }
		public TerminalNode XOR() { return getToken(ExprParser.XOR, 0); }
		public TerminalNode OR() { return getToken(ExprParser.OR, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(20);
				match(NOT);
				}
			}

			setState(23);
			predicate();
			}
			_ctx.stop = _input.LT(-1);
			setState(36);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(34);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(25);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(26);
						((ExpressionContext)_localctx).op = match(AND);
						setState(27);
						((ExpressionContext)_localctx).right = expression(4);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(28);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(29);
						((ExpressionContext)_localctx).op = match(XOR);
						setState(30);
						((ExpressionContext)_localctx).right = expression(3);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(31);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(32);
						((ExpressionContext)_localctx).op = match(OR);
						setState(33);
						((ExpressionContext)_localctx).right = expression(2);
						}
						break;
					}
					} 
				}
				setState(38);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	 
		public PredicateContext() { }
		public void copyFrom(PredicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ComparisonPredicateContext extends PredicateContext {
		public CompoundExpressionContext left;
		public CompoundExpressionContext right;
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public List<CompoundExpressionContext> compoundExpression() {
			return getRuleContexts(CompoundExpressionContext.class);
		}
		public CompoundExpressionContext compoundExpression(int i) {
			return getRuleContext(CompoundExpressionContext.class,i);
		}
		public ComparisonPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterComparisonPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitComparisonPredicate(this);
		}
	}
	public static class IsNullPredicateContext extends PredicateContext {
		public CompoundExpressionContext compoundExpression() {
			return getRuleContext(CompoundExpressionContext.class,0);
		}
		public TerminalNode IS() { return getToken(ExprParser.IS, 0); }
		public TerminalNode NULL() { return getToken(ExprParser.NULL, 0); }
		public TerminalNode ISNULL() { return getToken(ExprParser.ISNULL, 0); }
		public TerminalNode NOT() { return getToken(ExprParser.NOT, 0); }
		public IsNullPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterIsNullPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitIsNullPredicate(this);
		}
	}
	public static class ContainsPredicateContext extends PredicateContext {
		public CompoundExpressionContext left;
		public CompoundExpressionContext right;
		public List<CompoundExpressionContext> compoundExpression() {
			return getRuleContexts(CompoundExpressionContext.class);
		}
		public CompoundExpressionContext compoundExpression(int i) {
			return getRuleContext(CompoundExpressionContext.class,i);
		}
		public TerminalNode CONTAINS() { return getToken(ExprParser.CONTAINS, 0); }
		public TerminalNode NOT_CONTAINS() { return getToken(ExprParser.NOT_CONTAINS, 0); }
		public TerminalNode NOT() { return getToken(ExprParser.NOT, 0); }
		public ContainsPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterContainsPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitContainsPredicate(this);
		}
	}
	public static class HasChangedPredicateContext extends PredicateContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode HAS() { return getToken(ExprParser.HAS, 0); }
		public TerminalNode CHANGED() { return getToken(ExprParser.CHANGED, 0); }
		public TerminalNode HAS_CHANGED() { return getToken(ExprParser.HAS_CHANGED, 0); }
		public TerminalNode NOT() { return getToken(ExprParser.NOT, 0); }
		public HasChangedPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterHasChangedPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitHasChangedPredicate(this);
		}
	}
	public static class CompoundPredicateContext extends PredicateContext {
		public CompoundExpressionContext compoundExpression() {
			return getRuleContext(CompoundExpressionContext.class,0);
		}
		public CompoundPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterCompoundPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitCompoundPredicate(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_predicate);
		int _la;
		try {
			setState(72);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new ComparisonPredicateContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(39);
				((ComparisonPredicateContext)_localctx).left = compoundExpression(0);
				setState(40);
				comparisonOperator();
				setState(41);
				((ComparisonPredicateContext)_localctx).right = compoundExpression(0);
				}
				break;
			case 2:
				_localctx = new IsNullPredicateContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(43);
				compoundExpression(0);
				setState(50);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IS:
					{
					setState(44);
					match(IS);
					setState(46);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==NOT) {
						{
						setState(45);
						match(NOT);
						}
					}

					setState(48);
					match(NULL);
					}
					break;
				case ISNULL:
					{
					setState(49);
					match(ISNULL);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 3:
				_localctx = new ContainsPredicateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(52);
				((ContainsPredicateContext)_localctx).left = compoundExpression(0);
				setState(58);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NOT:
				case CONTAINS:
					{
					setState(54);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==NOT) {
						{
						setState(53);
						match(NOT);
						}
					}

					setState(56);
					match(CONTAINS);
					}
					break;
				case NOT_CONTAINS:
					{
					setState(57);
					match(NOT_CONTAINS);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(60);
				((ContainsPredicateContext)_localctx).right = compoundExpression(0);
				}
				break;
			case 4:
				_localctx = new HasChangedPredicateContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(62);
				id();
				setState(69);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case HAS:
					{
					setState(63);
					match(HAS);
					setState(65);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==NOT) {
						{
						setState(64);
						match(NOT);
						}
					}

					setState(67);
					match(CHANGED);
					}
					break;
				case HAS_CHANGED:
					{
					setState(68);
					match(HAS_CHANGED);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 5:
				_localctx = new CompoundPredicateContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(71);
				compoundExpression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonOperatorContext extends ParserRuleContext {
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitComparisonOperator(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_comparisonOperator);
		int _la;
		try {
			setState(88);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				match(EQUAL);
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EQUAL) {
					{
					setState(75);
					match(EQUAL);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(78);
				match(LESS);
				setState(79);
				match(GREATER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(80);
				match(EXCLAMATION);
				setState(81);
				match(EQUAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(82);
				match(GREATER);
				setState(83);
				match(EQUAL);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(84);
				match(GREATER);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(85);
				match(LESS);
				setState(86);
				match(EQUAL);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(87);
				match(LESS);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CompoundExpressionContext extends ParserRuleContext {
		public CompoundExpressionContext left;
		public Token op;
		public CompoundExpressionContext right;
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<CompoundExpressionContext> compoundExpression() {
			return getRuleContexts(CompoundExpressionContext.class);
		}
		public CompoundExpressionContext compoundExpression(int i) {
			return getRuleContext(CompoundExpressionContext.class,i);
		}
		public CompoundExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compoundExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterCompoundExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitCompoundExpression(this);
		}
	}

	public final CompoundExpressionContext compoundExpression() throws RecognitionException {
		return compoundExpression(0);
	}

	private CompoundExpressionContext compoundExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		CompoundExpressionContext _localctx = new CompoundExpressionContext(_ctx, _parentState);
		CompoundExpressionContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_compoundExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(91);
				((CompoundExpressionContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
					((CompoundExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(94);
			atom();
			}
			_ctx.stop = _input.LT(-1);
			setState(104);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(102);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
					case 1:
						{
						_localctx = new CompoundExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_compoundExpression);
						setState(96);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(97);
						((CompoundExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STAR) | (1L << DIVIDE) | (1L << MODULE))) != 0)) ) {
							((CompoundExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(98);
						((CompoundExpressionContext)_localctx).right = compoundExpression(3);
						}
						break;
					case 2:
						{
						_localctx = new CompoundExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_compoundExpression);
						setState(99);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(100);
						((CompoundExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((CompoundExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(101);
						((CompoundExpressionContext)_localctx).right = compoundExpression(2);
						}
						break;
					}
					} 
				}
				setState(106);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_atom);
		try {
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HAS:
			case CHANGED:
			case CONTAINS:
			case HAS_CHANGED:
			case NOT_CONTAINS:
			case ISNULL:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				id();
				}
				break;
			case NULL:
			case TRUE:
			case FALSE:
			case INTEGER:
			case REAL:
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(108);
				constant();
				}
				break;
			case LEFT_BRACKET:
				enterOuterAlt(_localctx, 3);
				{
				setState(109);
				match(LEFT_BRACKET);
				setState(110);
				expression(0);
				setState(111);
				match(RIGHT_BRACKET);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdContext extends ParserRuleContext {
		public TerminalNode HAS() { return getToken(ExprParser.HAS, 0); }
		public TerminalNode CHANGED() { return getToken(ExprParser.CHANGED, 0); }
		public TerminalNode HAS_CHANGED() { return getToken(ExprParser.HAS_CHANGED, 0); }
		public TerminalNode CONTAINS() { return getToken(ExprParser.CONTAINS, 0); }
		public TerminalNode NOT_CONTAINS() { return getToken(ExprParser.NOT_CONTAINS, 0); }
		public TerminalNode ISNULL() { return getToken(ExprParser.ISNULL, 0); }
		public TerminalNode ID() { return getToken(ExprParser.ID, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitId(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HAS) | (1L << CHANGED) | (1L << CONTAINS) | (1L << HAS_CHANGED) | (1L << NOT_CONTAINS) | (1L << ISNULL) | (1L << ID))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(ExprParser.STRING, 0); }
		public TerminalNode INTEGER() { return getToken(ExprParser.INTEGER, 0); }
		public TerminalNode REAL() { return getToken(ExprParser.REAL, 0); }
		public TerminalNode TRUE() { return getToken(ExprParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(ExprParser.FALSE, 0); }
		public TerminalNode NULL() { return getToken(ExprParser.NULL, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprParserListener ) ((ExprParserListener)listener).exitConstant(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NULL) | (1L << TRUE) | (1L << FALSE) | (1L << INTEGER) | (1L << REAL) | (1L << STRING))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 4:
			return compoundExpression_sempred((CompoundExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean compoundExpression_sempred(CompoundExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 2);
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!z\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\3\3\3\5"+
		"\3\30\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3%\n\3\f\3\16"+
		"\3(\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\61\n\4\3\4\3\4\5\4\65\n\4\3\4"+
		"\3\4\5\49\n\4\3\4\3\4\5\4=\n\4\3\4\3\4\3\4\3\4\3\4\5\4D\n\4\3\4\3\4\5"+
		"\4H\n\4\3\4\5\4K\n\4\3\5\3\5\5\5O\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\5\5[\n\5\3\6\3\6\5\6_\n\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6"+
		"i\n\6\f\6\16\6l\13\6\3\7\3\7\3\7\3\7\3\7\3\7\5\7t\n\7\3\b\3\b\3\t\3\t"+
		"\3\t\2\4\4\n\n\2\4\6\b\n\f\16\20\2\6\3\2\34\35\3\2\31\33\4\2\t\16!!\4"+
		"\2\17\21\36 \2\u008b\2\22\3\2\2\2\4\25\3\2\2\2\6J\3\2\2\2\bZ\3\2\2\2\n"+
		"\\\3\2\2\2\fs\3\2\2\2\16u\3\2\2\2\20w\3\2\2\2\22\23\5\4\3\2\23\24\7\2"+
		"\2\3\24\3\3\2\2\2\25\27\b\3\1\2\26\30\7\4\2\2\27\26\3\2\2\2\27\30\3\2"+
		"\2\2\30\31\3\2\2\2\31\32\5\6\4\2\32&\3\2\2\2\33\34\f\5\2\2\34\35\7\5\2"+
		"\2\35%\5\4\3\6\36\37\f\4\2\2\37 \7\6\2\2 %\5\4\3\5!\"\f\3\2\2\"#\7\7\2"+
		"\2#%\5\4\3\4$\33\3\2\2\2$\36\3\2\2\2$!\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'"+
		"\3\2\2\2\'\5\3\2\2\2(&\3\2\2\2)*\5\n\6\2*+\5\b\5\2+,\5\n\6\2,K\3\2\2\2"+
		"-\64\5\n\6\2.\60\7\b\2\2/\61\7\4\2\2\60/\3\2\2\2\60\61\3\2\2\2\61\62\3"+
		"\2\2\2\62\65\7\17\2\2\63\65\7\16\2\2\64.\3\2\2\2\64\63\3\2\2\2\65K\3\2"+
		"\2\2\66<\5\n\6\2\679\7\4\2\28\67\3\2\2\289\3\2\2\29:\3\2\2\2:=\7\13\2"+
		"\2;=\7\r\2\2<8\3\2\2\2<;\3\2\2\2=>\3\2\2\2>?\5\n\6\2?K\3\2\2\2@G\5\16"+
		"\b\2AC\7\t\2\2BD\7\4\2\2CB\3\2\2\2CD\3\2\2\2DE\3\2\2\2EH\7\n\2\2FH\7\f"+
		"\2\2GA\3\2\2\2GF\3\2\2\2HK\3\2\2\2IK\5\n\6\2J)\3\2\2\2J-\3\2\2\2J\66\3"+
		"\2\2\2J@\3\2\2\2JI\3\2\2\2K\7\3\2\2\2LN\7\22\2\2MO\7\22\2\2NM\3\2\2\2"+
		"NO\3\2\2\2O[\3\2\2\2PQ\7\24\2\2Q[\7\23\2\2RS\7\25\2\2S[\7\22\2\2TU\7\23"+
		"\2\2U[\7\22\2\2V[\7\23\2\2WX\7\24\2\2X[\7\22\2\2Y[\7\24\2\2ZL\3\2\2\2"+
		"ZP\3\2\2\2ZR\3\2\2\2ZT\3\2\2\2ZV\3\2\2\2ZW\3\2\2\2ZY\3\2\2\2[\t\3\2\2"+
		"\2\\^\b\6\1\2]_\t\2\2\2^]\3\2\2\2^_\3\2\2\2_`\3\2\2\2`a\5\f\7\2aj\3\2"+
		"\2\2bc\f\4\2\2cd\t\3\2\2di\5\n\6\5ef\f\3\2\2fg\t\2\2\2gi\5\n\6\4hb\3\2"+
		"\2\2he\3\2\2\2il\3\2\2\2jh\3\2\2\2jk\3\2\2\2k\13\3\2\2\2lj\3\2\2\2mt\5"+
		"\16\b\2nt\5\20\t\2op\7\26\2\2pq\5\4\3\2qr\7\27\2\2rt\3\2\2\2sm\3\2\2\2"+
		"sn\3\2\2\2so\3\2\2\2t\r\3\2\2\2uv\t\4\2\2v\17\3\2\2\2wx\t\5\2\2x\21\3"+
		"\2\2\2\22\27$&\60\648<CGJNZ^hjs";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
