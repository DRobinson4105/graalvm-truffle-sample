grammar EasyScript ;

@header{
package org.example;
}

start : stmt+ EOF ;

stmt :         kind=('var' | 'let' | 'const') binding (',' binding)* ';'? #VarDeclStmt
     |                                                         expr1 ';'? #ExprStmt
     |       'function' name=ID '(' args=func_args ')' '{' stmt* '}' ';'? #FuncDeclStmt
     |                                               'return' expr1 ';'? #ReturnStmt
     |                                                 '{' stmt* '}' ';'? #BlockStmt
     |    'if' '(' cond=expr1 ')' then_stmt=stmt ('else' else_stmt=stmt)? #IfStmt
     |                               'while' '(' cond=expr1 ')' body=stmt #WhileStmt
     |                     'do' body=stmt 'while' '(' cond=expr1 ')' ';'? #DoWhileStmt
     | 'for' '(' init=stmt? ';' cond=expr1? ';' updt=expr1? ')' body=stmt #ForStmt
     |                                                       'break' ';'? #BreakStmt
     |                                                    'continue' ';'? #ContinueStmt
     ;
func_args : (ID (',' ID)* )? ;
binding : ID ('=' expr1)? ;

expr1 : ID '=' expr1                                           #AssignmentExpr1
      | arr=expr5 '[' index=expr1 ']' '=' rValue=expr1         #ArrayIndexWriteExpr1
      | expr2                                                  #PrecedenceTwoExpr1
      ;

expr2 : left=expr2 c=('===' | '!==') right=expr3               #EqNotEqExpr2
      | expr3                                                  #PrecedenceThreeExpr2
      ;

expr3 : left=expr3 c=('<' | '<=' | '>' | '>=') right=expr4     #ComparisonExpr3
      | expr4                                                  #PrecedenceFourExpr3
      ;

expr4 : left=expr4 o=('+' | '-' | '*' | '/' | '%') right=expr5 #ArithmeticExpr4
      | '-' expr5                                              #NegationExpr4
      | expr5                                                  #PrecedenceFiveExpr4
      ;

expr5 : expr5 '.' ID                                           #PropertyReadExpr5
      | '[' (expr1 (',' expr1)*)? ']'                          #ArrayLiteralExpr5
      | arr=expr5 '[' index=expr1 ']'                          #ArrayIndexReadExpr5
      | 'this'                                                 #ThisExpr5
      | literal                                                #LiteralExpr5
      | '(' args=func_args ')' '=>' '{' stmt* '}'              #ClosureLiteralExpr5
      | ID                                                     #ReferenceExpr5
      | expr5 '(' (expr1 (',' expr1)*)? ')'                    #CallExpr5
      | '(' expr1 ')'                                          #PrecedenceOneExpr5
      ;

literal : INT | DOUBLE | BOOLEAN | STRING | 'undefined' ;

fragment DIGIT : [0-9] ;
INT : DIGIT+ ;
DOUBLE : DIGIT+ '.' DIGIT+ ;

BOOLEAN: 'true' | 'false' ;

STRING: SINGLE_QUOTE_STRING | DOUBLE_QUOTE_STRING ;
SINGLE_QUOTE_STRING : '\'' (~[\\'\r\n] | '\\' ~[\r\n])* '\'' ;
DOUBLE_QUOTE_STRING : '"' (~[\\'\r\n] | '\\' ~[\r\n])* '"' ;

fragment LETTER : [a-zA-Z$_] ;
ID : LETTER (LETTER | DIGIT)* ;

// skip all whitespace
WS : (' ' | '\r' | '\t' | '\n' | '\f')+ -> skip ;