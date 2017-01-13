package sirgl.compiler.verification.scope

import sirgl.compiler.ast.*
import sirgl.compiler.verification.VerificationException

class Scope(val parentScope: Scope?) {
    val variableList = mutableListOf<ObjectReference>()

    fun tryAdd(variable: ObjectReference): RedefinitionError? {
        val redefinitionError = if (isInScope(variable.name)) {
            val conflictingVariables = findVarsWithName(variable.name)
            conflictingVariables.add(variable)
            RedefinitionError(conflictingVariables)
        } else {
            null
        }
        variableList.add(variable)
        return redefinitionError
    }

    fun findVarsWithName(variable: String): MutableList<ObjectReference> {
        val varList = variableList
                .filter { it.name == variable }
                .toMutableList()
        varList.addAll(parentScope?.findVarsWithName(variable) ?: emptyList<NamedReference>())
        return varList
    }

    fun isInScope(variable: String): Boolean {
        val thisScopeContainsVar = variableList.any { it.name == variable }
        val parentScopeContainsVar = parentScope?.isInScope(variable) ?: false
        return thisScopeContainsVar or parentScopeContainsVar
    }
}

fun Block.createScope() {

    val upperScoped = findUpperScoped()
    val upperScope = if(upperScoped != null) {
        upperScoped.scope ?: throw VerificationException("Upper scoped was not initialized ${upperScoped.javaClass}")
    } else {
        null
    }
    scope = Scope(upperScope)
    val valParent = parent
    if (valParent is MethodDefinition) {
        injectMethodParameters(valParent)
    }
}

private fun Block.injectMethodParameters(valParent: MethodDefinition) {
    valParent.methodDeclaration.parameters.forEach { scope?.tryAdd(it) }
}

data class RedefinitionError(val conflictingVariables: List<ObjectReference>) : VerificationError