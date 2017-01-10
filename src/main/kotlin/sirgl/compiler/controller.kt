package sirgl.compiler

import sirgl.compiler.parser.parserForStream
import sirgl.compiler.parser.transformer.toAst
import sirgl.compiler.verification.*
import sirgl.compiler.verification.scope.ScopeVerifier
import sirgl.compiler.verification.scope.VerificationError
import java.io.InputStream


class MainController {
    val stdLibRepo = StandardLibraryRepository()

    fun parse(inputStreams: List<InputStream>): List<LangClassContext> {
        return inputStreams
                .map { parserForStream(it).compilationUnit().toAst() }
                .map {
                    val methodSignatures = it.classDefinition.methodList
                            .map { it.methodDeclaration }
                            .map { MethodSignature(it.methodName, it.parameters.map { it.assignableType }, it.resultType) }
                    LangClassContext(methodSignatures, it.fullName, it)
                }
    }

    fun checkScope(contexts: List<LangClassContext>): List<VerificationError> {
        return contexts.map { it.compilationUnit }
                .flatMap { compilationUnit ->
                    ScopeVerifier(compilationUnit, stdLibRepo.classNames).checkScope()
                            .map { ClassRelatedError(compilationUnit.fullName, it) }
                }
    }

    fun resolveClasses(contexts: List<ClassContext>): List<VerificationError> {
        val resolver = Resolver(contexts, stdLibRepo)
        val resolveErrors = resolver.resolveClassNames()
        return resolveErrors
    }
}

data class ClassRelatedError(val fullName: String, val error: VerificationError) : VerificationError