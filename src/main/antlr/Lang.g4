grammar Lang;


//Lexems

CLASS : 'class';
PrimitiveType : 'int' | 'byte' | 'boolean' | 'char' | 'long';
VoidType : 'void';
NULL: 'null';
THIS: 'this';
RETURN: 'return';
IF: 'if';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
NEW: 'new';
TRUE : 'true';
FALSE: 'false';
BREAK: 'break';
CONTINUE: 'continue';

PLUS : '+';
MINUS : '-';
MULTIPLY : '*';
DIVIDE : '/';
REMAINDER : '%';
ASSIGNMENT : '=';
GT : '>';
GTEQ : '>=';
LTEQ : '<=';
LT : '<';
EQ : '==';

Identifier : ('A'..'Z' | 'a'..'z')+;
IntLiteral : ('0'..'9')+;
StringLiteral
   : '"' (~ ["\\])* '"';
CharLiteral : '\'' (~ ["\\]) '\'';

WS  :   ( [ \t\r\n] | COMMENT) -> skip;

fragment
COMMENT
: '/*'.*'*/' /*single comment*/
| '//'~('\r' | '\n')* /* multiple comment*/
;




//ParserRules

//General symbols

returnType : PrimitiveType | VoidType | Identifier;

assignableType : PrimitiveType | Identifier;

//Expression

expressionList :   expression (',' expression)* ;

functionCall : Identifier '(' expressionList? ')';


expression :
    primary                                                 #primaryExpr
    | expression '.' Identifier                             #fieldAccess
    | expression '.' functionCall                           #methodCall
    | functionCall                           #methodCallWithoutSource
    | expression operator=(DIVIDE | MULTIPLY)  expression            #multiplyExpr
    | expression operator=(PLUS | MINUS | REMAINDER)  expression     #sumExpr
    | expression operator=(GT | GTEQ | LT | LTEQ)  expression        #comparsionExpr
    | expression (EQ)  expression                           #eqallityExpr
    | <assoc=right> Identifier ASSIGNMENT expression        #assignmentExpr
    | NEW functionCall                                      #constructorCall
    ;

primary : '(' expression ')'
    | THIS
    | Identifier
    | IntLiteral
    | StringLiteral
    | CharLiteral

    | TRUE
    | FALSE
    | NULL
    |
;

//Control

breakStatement : BREAK ';';

continueStatement : CONTINUE ';';

ifStatement : IF '(' expression ')' block (ELSE block)?;

forInit : (assignmentStatement | expression);

forCondition : expression;

forIteration : expression;

forControl : (forInit? ';' forCondition? ';' forIteration?);

forStatement : FOR '(' forControl ')' block;

whileStatement : WHILE '(' expression ')' block;

//Block


assignmentStatement : assignableType Identifier ASSIGNMENT expression;

assignableLine : assignmentStatement ';';

returnStatement : RETURN expression? ';';

block : '{' (assignableLine | returnStatement | expression ';' | ifStatement | forStatement | whileStatement | breakStatement | continueStatement)* '}';

// Class related rules

fieldDeclaration : assignableType Identifier ';';

parameter : assignableType Identifier;

parameters : (parameter)*;

methodDeclaration : returnType Identifier '(' parameters ')';

methodDeclarationLine : methodDeclaration ';';

methodDefinition : methodDeclaration block;

constructorDeclaration : Identifier '(' parameters ')';

constructorDefinition : constructorDeclaration block;

classDefinitionExpression :
    fieldDeclaration |
    constructorDefinition |
    methodDefinition;

classDefinitionBlock :
    '{' (classDefinitionExpression)* '}';

classDefinition : CLASS Identifier classDefinitionBlock;

//compilation unit

packageDeclaration: 'package' Identifier ';';

importDeclaration: 'import' Identifier ';';

compilationUnit: packageDeclaration? importDeclaration* classDefinition;