package sirgl.compiler.parser.transformer

import sirgl.compiler.parser.ast.*

fun LangParser.IfStatementContext.toAst(): IfStatement {
    val condition = expression().toAst()
    val block = block(0).toAst()
    val elseBlock = block(1)?.toAst()
    val ifStatement = IfStatement(condition, block, elseBlock, BlockMetaInfo(start.line, start.charPositionInLine, null))
    condition.parent = ifStatement
    block.parent = ifStatement
    elseBlock?.parent = ifStatement
    return ifStatement
}

fun LangParser.ForStatementContext.toAst(): ForStatement {
    val block = block().toAst()
    val forStatement = ForStatement(forControl().toAst(), block, BlockMetaInfo(start.line, start.charPositionInLine, null))
    forControl().toAst().parent = forStatement
    block.parent = forStatement
    return forStatement
}

fun LangParser.ForControlContext.toAst(): ForControl {
    val forCondition = extractPossibleEmptyExpr(forCondition().expression())
    val forIteration = extractPossibleEmptyExpr(forIteration().expression())
    val simpleForControl = SimpleForControl(forInit().toAst(), forCondition, forIteration)
    forCondition?.parent = simpleForControl
    forIteration?.parent = simpleForControl
    return simpleForControl
}

fun LangParser.ForInitContext.toAst(): ForInitBlock? {
    if (text.isEmpty()) {
        return null
    }
    if (assignmentStatement() != null) {
        val assignmentStatement = assignmentStatement().toAst()
        val assignmentForInitBlock = AssignmentForInitBlock(assignmentStatement, LineInfo(start.line, start.charPositionInLine))
        assignmentStatement.parent = assignmentForInitBlock
        return assignmentForInitBlock
    }
    if (expression() != null) {
        val expression = expression().toAst()
        val expressionForInitBlock = ExpressionForInitBlock(expression, LineInfo(start.line, start.charPositionInLine))
        expression.parent = expressionForInitBlock
        return expressionForInitBlock
    }
    throw ParserException("Unexpected init option")
}

fun LangParser.WhileStatementContext.toAst(): WhileStatement {
    val condition = expression().toAst()
    val block = block().toAst()
    val whileStatement = WhileStatement(condition, block)
    condition.parent = whileStatement
    block.parent = whileStatement
    return whileStatement
}

fun LangParser.BreakStatementContext.toAst() = BreakStatement(LineInfo(start.line, start.charPositionInLine))

fun LangParser.ContinueStatementContext.toAst() = ContinueStatement(LineInfo(start.line, start.charPositionInLine))