package sirgl.compiler.parser.transformer

import LangParser
import org.antlr.v4.runtime.tree.TerminalNode
import sirgl.compiler.ast.*

fun LangParser.BlockContext.toAst(): Block {

    val statements = children.map {
        when (it) {
            is LangParser.AssignableLineContext -> it.assignmentStatement().toAst()
            is LangParser.ExpressionContext -> it.toAst()
            is LangParser.ReturnStatementContext -> it.toAst()
            is LangParser.WhileStatementContext -> it.toAst()
            is LangParser.ForStatementContext -> it.toAst()
            is LangParser.IfStatementContext -> it.toAst()
            is LangParser.BreakStatementContext -> it.toAst()
            is LangParser.ContinueStatementContext -> it.toAst()
            is TerminalNode -> null
            else -> throw UnsupportedOperationException()
        }
    }.filterNotNull()
    val block = Block(statements, BlockMetaInfo(start.line, start.charPositionInLine, null))
    statements.forEach { it.parent = block }
    return block
}

fun LangParser.AssignmentStatementContext.toAst(): AssignmentStatement {
    val lineInfo = LineInfo(start.line, start.charPositionInLine)
    val expression = expression().toAst()
    val symbol = Identifier().symbol
    val variable = NamedReference(Identifier().text, ObjectReferenceMetaInfo(symbol.line, symbol.charPositionInLine))
    val assignmentStatement = AssignmentStatement(variable, assignableType().toAst(), expression, lineInfo)
    variable.parent = assignmentStatement
    expression.parent = assignmentStatement
    return assignmentStatement
}

fun LangParser.ReturnStatementContext.toAst(): ReturnStatement {
    val lineInfo = TypedLineInfo(start.line, start.charPositionInLine)
    return if(expression().text.isEmpty()) {
        ReturnStatement(null, lineInfo)
    } else {
        ReturnStatement(expression().toAst(), lineInfo)
    }
}