parser grammar ExprParser;

options { tokenVocab=ExprLexer; }

singleExpression
    : expression EOF
    ;

expression
    : NOT? predicate
    | left=expression op=AND right=expression
    | left=expression op=XOR right=expression
    | left=expression op=OR right=expression
    ;

predicate
    : left=compoundExpression comparisonOperator right=compoundExpression      #comparisonPredicate
    | compoundExpression (IS NOT? NULL | ISNULL)                    #isNullPredicate
//    | compoundExpression NOT? LIKE compoundExpression
//    | compoundExpression NOT? REGEXP compoundExpression
//    | compoundExpression NOT? IN compoundExpression
    | left=compoundExpression (NOT? CONTAINS | NOT_CONTAINS) right=compoundExpression           #containsPredicate
    | id (HAS NOT? CHANGED | HAS_CHANGED)                           #hasChangedPredicate
    | compoundExpression                                            #compoundPredicate
    ;

comparisonOperator
    : '=' '='? | '<' '>' | '!' '='| '>' '=' | '>' | '<' '=' | '<'
    ;

compoundExpression
    : op=('-'| '+')? atom
    | left=compoundExpression op=('*' | '/' | '%') right=compoundExpression
    | left=compoundExpression op=('-' | '+') right=compoundExpression
    ;

atom
    : id
    | constant
    | '(' expression ')'
    ;

id
    : HAS
    | CHANGED
    | HAS_CHANGED
    | CONTAINS
    | NOT_CONTAINS
    | ISNULL
    | ID
    ;

constant
    : STRING
    | INTEGER
    | REAL
    | TRUE | FALSE | NULL
    ;