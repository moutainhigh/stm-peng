lexer grammar ExprLexer;

SPACES: [ \t\r\n]+ -> skip;

NOT:                                'NOT';
AND:                                'AND';
XOR:                                'XOR';
OR:                                 'OR';
IS:                                 'IS';

//IN:                                 'IN';
//LIKE:                               'LIKE';
//REGEXP:                             'REGEXP';

HAS:                                'HAS';
CHANGED:                            'CHANGED';
CONTAINS:                           'CONTAINS';

HAS_CHANGED:                        'HAS_CHANGED';
NOT_CONTAINS:                       'NOT_CONTAINS';
ISNULL:                             'ISNULL';

NULL:                               'NULL';
TRUE:                               'TRUE';
FALSE:                              'FALSE';

EQUAL:                              '=';
GREATER:                            '>';
LESS:                               '<';
EXCLAMATION:                        '!';

LEFT_BRACKET:                       '(';
RIGHT_BRACKET:                      ')';
COMMA:                              ',';
STAR:                               '*';
DIVIDE:                             '/';
MODULE:                             '%';
PLUS:                               '+';
MINUS:                              '-';

INTEGER:                            [0-9]+;
REAL:                               FLOAT ([Ee] [+-]? INTEGER)?;
STRING:                             '"' ('\\"' | ~'"')* '"';
ID:                                 (UPPER_CASE | LOWER_CASE) (UPPER_CASE| LOWER_CASE | DIGIT)*;

fragment UPPER_CASE:                [A-Z];
fragment LOWER_CASE:                [a-z];
fragment DIGIT:                     [0-9];
fragment FLOAT:                     INTEGER* '.'? INTEGER+;