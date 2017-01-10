package sirgl.compiler.verification

import sirgl.compiler.parser.ast.Type
import sirgl.compiler.parser.ast.VoidType
import sirgl.compiler.lang.NativeClass
import sirgl.compiler.parser.ast.CompilationUnit
import sirgl.compiler.parser.ast.ReturnType
import kotlin.reflect.KCallable

interface ClassContext {
    val methodSignatures: List<MethodSignature>
    val className: String
    val dependencies: List<Dependency>
    val dependents: MutableList<ClassContext>
}

data class MethodSignature(
        val name: String,
        val types: List<Type>,
        val returnType: ReturnType
)

data class Dependency(
        val className: String,
        var classContext: ClassContext?
)

class LangClassContext(
        override val methodSignatures: List<MethodSignature>,
        override val className: String,
        val compilationUnit : CompilationUnit) : ClassContext {
    override val dependencies: List<Dependency> = compilationUnit.imports.map { Dependency(it.name, null) }
    override val dependents: MutableList<ClassContext> = mutableListOf()

}

class NativeClassContext(
        override val methodSignatures: List<MethodSignature>,
        override val className: String,
        val nativeClass: NativeClass,
        val methodMap: Map<MethodSignature, KCallable<*>>
) : ClassContext {

    override val dependencies: List<Dependency>
    override val dependents: MutableList<ClassContext>

    init {
        this.dependencies = emptyList()
        this.dependents = mutableListOf()
    }


}

class Resolver