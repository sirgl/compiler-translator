package sirgl.compiler.parser.transformer

import LangParser
import sirgl.compiler.ast.*


fun LangParser.ExpressionContext.toAst(): Expression = when (this) {
    is LangParser.PrimaryExprContext -> primary().toAst()
    is LangParser.FieldAccessContext -> toAst()
    is LangParser.MethodCallContext -> toAst()
    is LangParser.MethodCallWithoutSourceContext -> toAst()
    is LangParser.MultiplyExprContext -> toAst()
    is LangParser.SumExprContext -> toAst()
    is LangParser.ComparsionExprContext -> toAst()
    is LangParser.EqallityExprContext -> toAst()
    is LangParser.AssignmentExprContext -> toAst()
    is LangParser.ConstructorCallContext -> toAst()
    else -> throw ParserException("Unsupported expression: " + javaClass)
}

fun LangParser.MethodCallWithoutSourceContext.toAst(): MethodCallExpression {
    val methodCall = functionCall().toMethodAst()
    val methodCallExpression = MethodCallExpression(null, methodCall, TypedLineInfo(start.line, start.charPositionInLine))
    methodCall.parent = methodCallExpression
    return methodCallExpression
}

fun LangParser.AssignmentExprContext.toAst(): AssignmentExpression {
    val variable = NamedReference(Identifier().text, ReferenceInfo(start.line, start.charPositionInLine))
    val expression = expression().toAst()
    val assignmentExpression = AssignmentExpression(variable, expression, TypedLineInfo(start.line, start.charPositionInLine))
    variable.parent = assignmentExpression
    expression.parent = assignmentExpression
    return assignmentExpression
}

fun LangParser.ConstructorCallContext.toAst(): ObjectCreationExpression {
    val constructorCall = functionCall().toConstructorAst()
    val objectCreationExpression = ObjectCreationExpression(constructorCall, TypedLineInfo(start.line, start.charPositionInLine))
    constructorCall.parent = objectCreationExpression
    return objectCreationExpression
}

fun LangParser.EqallityExprContext.toAst(): EqualityExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val equalityExpression = EqualityExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
    left.parent = equalityExpression
    right.parent = equalityExpression
    return equalityExpression
}

fun LangParser.MultiplyExprContext.toAst(): BinaryExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val binaryExpression = when (operator.text) {
        "*" -> MultiplyExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        "/" -> DivideExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        "%" -> RemainderExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        else -> throw ParserException("Unexpected multiply operator")
    }
    left.parent = binaryExpression
    right.parent = binaryExpression
    return binaryExpression
}

fun LangParser.SumExprContext.toAst(): BinaryExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val binaryExpression = when (operator.text) {
        "+" -> SumExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        "-" -> SubtractionExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        else -> throw ParserException("Unexpected sum operator")
    }
    left.parent = binaryExpression
    right.parent = binaryExpression
    return binaryExpression
}

fun LangParser.ComparsionExprContext.toAst(): BinaryPredicateExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val binaryPredicateExpression = when (operator.text) {
        ">" -> GreaterThanExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        "<" -> LessThanExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        "<=" -> LessThanOrEqualsExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        ">=" -> GreaterThanOrEqualsExpression(left, right, TypedLineInfo(start.line, start.charPositionInLine))
        else -> throw ParserException("Unexpected sum operator")
    }
    left.parent = binaryPredicateExpression
    right.parent = binaryPredicateExpression
    return binaryPredicateExpression
}


fun LangParser.FieldAccessContext.toAst(): FieldAccessExpression {
    val caller = expression().toAst()
    val fieldAccessExpression = FieldAccessExpression(caller, Identifier().text, ReferenceInfo(start.line, start.charPositionInLine))
    caller.parent = fieldAccessExpression
    return fieldAccessExpression
}

fun LangParser.MethodCallContext.toAst(): MethodCallExpression {
    val caller = expression().toAst()
    val methodCall = functionCall().toMethodAst()
    val methodCallExpression = MethodCallExpression(caller, methodCall, ReferenceInfo(start.line, start.charPositionInLine))
    caller.parent = methodCallExpression
    methodCall.parent = methodCallExpression
    return methodCallExpression
}

fun LangParser.FunctionCallContext.toMethodAst(): MethodCall {
    val arguments = expressionList().toAst()
    val methodCall = MethodCall(Identifier().text, arguments, MethodInfo(start.line, start.charPositionInLine))
    arguments.forEach { it.parent = methodCall }
    return methodCall
}

fun LangParser.FunctionCallContext.toConstructorAst(): ConstructorCall {
    val arguments = expressionList().toAst()
    val constructorCall = ConstructorCall(Identifier().text, arguments, MethodInfo(start.line, start.charPositionInLine))
    arguments.forEach { it.parent = constructorCall }
    return constructorCall
}

private fun LangParser.ExpressionListContext.toAst(): List<Expression> {
    return if (text.isEmpty()) {
        emptyList()
    } else {
        expression().map { it.toAst() }
    }
}

fun LangParser.PrimaryContext.toAst(): Expression {
    if (expression() != null) {
        return expression().toAst()
    }
    if (StringLiteral() != null) {
        return StringLiteral(StringLiteral().text.substring(1, StringLiteral().text.lastIndex), TypedLineInfo(start.line, start.charPositionInLine, stringType))
    }
    if (IntLiteral() != null) {
        return IntLiteral(IntLiteral().text.toInt(), TypedLineInfo(start.line, start.charPositionInLine, IntegerType))
    }
    if (Identifier() != null) {
        return NamedReference(Identifier().text, ReferenceType.Variable, TypedLineInfo(start.line, start.charPositionInLine))
    }
    if (THIS() != null) {
        return ThisExpression(TypedLineInfo(start.line, start.charPositionInLine))
    }
    if (NULL() != null) {
        return NullLiteral(TypedLineInfo(start.line, start.charPositionInLine, NullType)) // TODO
    }
    if (CharLiteral() != null) {
        return CharLiteral(CharLiteral().text[0], TypedLineInfo(start.line, start.charPositionInLine, CharType))
    }
    if (TRUE() != null) {
        return TrueLiteral(TypedLineInfo(start.line, start.charPositionInLine, BooleanType))
    }
    if (FALSE() != null) {
        return FalseLiteral(TypedLineInfo(start.line, start.charPositionInLine, BooleanType))
    }
    throw UnsupportedOperationException()
}
