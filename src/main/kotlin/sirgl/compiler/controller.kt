package sirgl.compiler

import sirgl.compiler.parser.ast.ClassDefinition
import sirgl.compiler.parser.parserForStream
import sirgl.compiler.parser.transformer.toAst
import sirgl.compiler.verification.LangClassContext
import sirgl.compiler.verification.MethodSignature
import java.io.InputStream


class MainController {
    fun resolve(inputStreams: List<InputStream>): List<LangClassContext> {
        return inputStreams
                .map { parserForStream(it).compilationUnit().toAst() }
                .map {
                    val methodSignatures = it.classDefinition.methodList
                            .map { it.methodDeclaration }
                            .map { MethodSignature(it.methodName, it.parameters.map { it.assignableType }, it.resultType) }
                    LangClassContext(methodSignatures, it.fullName, it)
                }
    }
}