package sirgl.compiler.parser.transformer

import sirgl.compiler.ast.Expression


fun extractPossibleEmptyExpr(expr : LangParser.ExpressionContext): Expression? {
    return if (expr.text.isEmpty()) {
        null
    } else {
        expr.toAst()
    }
}
