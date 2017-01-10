package sirgl.compiler.verification

import sirgl.compiler.ClassRelatedError
import sirgl.compiler.lang.NativeClass
import sirgl.compiler.parser.ast.CompilationUnit
import sirgl.compiler.parser.ast.ReturnType
import sirgl.compiler.parser.ast.Type
import sirgl.compiler.verification.scope.VerificationError
import kotlin.reflect.KCallable

interface ClassContext {
    val methodSignatures: List<MethodSignature>
    val className: String
    val dependencies: List<String>
    val dependents: MutableList<ClassContext>
}

data class MethodSignature(
        val name: String,
        val types: List<Type>,
        val returnType: ReturnType
)

class LangClassContext(
        override val methodSignatures: List<MethodSignature>,
        override val className: String,
        val compilationUnit: CompilationUnit) : ClassContext {
    override val dependencies: List<String> = compilationUnit.imports.map { it.name }
    override val dependents: MutableList<ClassContext> = mutableListOf()

}

class NativeClassContext(
        override val methodSignatures: List<MethodSignature>,
        override val className: String,
        val nativeClass: NativeClass,
        val methodMap: Map<MethodSignature, KCallable<*>>
) : ClassContext {

    override val dependencies: List<String>
    override val dependents: MutableList<ClassContext>

    init {
        this.dependencies = emptyList()
        this.dependents = mutableListOf()
    }


}

class StandardLibraryRepository {
    var classes = mutableListOf<ClassContext>()
    val classNames = classes.map { it.className }
}

class Resolver(classContexts: List<ClassContext>, standardLibraryRepository: StandardLibraryRepository) {
    val resolvingSet = mutableSetOf<ClassContext>()
    val errors = mutableListOf<VerificationError>()

    init {
        resolvingSet.addAll(classContexts)
        resolvingSet.addAll(standardLibraryRepository.classes)
    }

    fun resolveClassNames(): MutableList<VerificationError> {
        addMethodsToRegistery()
        for (classContext in resolvingSet) {
            classContext.dependencies
                    .filterNot { dependency -> resolvingSet.any { it.className == dependency} }
                    .mapTo(errors) { ClassRelatedError(classContext.className, UndefinedClassReference(it)) }
        }
        return errors

//        return resolvingSet
//                .flatMap { it.dependencies }
//                .filterNot { dependency -> resolvingSet.any { it.className == dependency } }
//                .mapTo(errors, ::UndefinedClassReference)
    }

    private fun addMethodsToRegistery() {
        resolvingSet.forEach {
            methodRegistery[it.className] = it.methodSignatures
        }
    }
}

data class UndefinedClassReference(val fullName: String) : VerificationError


val methodRegistery = mutableMapOf<String, List<MethodSignature>>()