package sirgl.compiler.parser.verification

import sirgl.compiler.parser.ast.*
import java.util.*

fun extractMethodNames(classDefinition: ClassDefinition): List<String> {
    return classDefinition.methodList.map { it.methodDeclaration.methodName }
}


interface VerificationError

class ScopeVerifier(val classDefinition : ClassDefinition) {
    val errors : MutableList<VerificationError> = mutableListOf()
    val methodNames = extractMethodNames(classDefinition)
    val knownClasses = listOf(classDefinition.name)

    fun checkScope(): MutableList<VerificationError> {
        initClassDefinitionScope()
        val blockWalker = TreeWalker()
        blockWalker.addListener(onBlock(), Block::class.java)
        blockWalker.addListener(onMethodCall(), MethodCallExpression::class.java)
        blockWalker.addListener(onConstructorCall(), ObjectCreationExpression::class.java)
        blockWalker.addListener(onReturnStatement(), ReturnStatement::class.java)
        blockWalker.walk(classDefinition)
        return errors
    }

    private fun initClassDefinitionScope() {
        if(classDefinition.scope!!.variableList.isNotEmpty()) {
            return
        }
        classDefinition.fieldDeclarations
                .map { it.field }
                .forEach { classDefinition.scope!!.tryAdd(it) }
    }

    private fun onBlock(): (Any) -> Unit = {
        node -> node as Block
        if (node.scope == null) {
            node.createScope()
        }
        walkVariables(node)
    }

    private fun onMethodCall(): (Any) -> Unit = {
        node -> node as MethodCallExpression
        val methodCall = node.methodCall
        if(node.isThisClassMethod() && !methodNames.contains(methodCall.name)) {
            errors.add(NoSuchMethodError(methodCall))
        }
    }

    private fun onConstructorCall(): (Any) -> Unit = {
        node -> node as ObjectCreationExpression
        if(!knownClasses.contains(node.constructorCall.name)) {
            errors.add(UnknownClassError(node.constructorCall))
        }
    }

    private fun onReturnStatement(): (Any) -> Unit = {
        node -> node as ReturnStatement

    }

    private fun walkVariables(block: Block) {
        val conflictingReferences : MutableMap<String, MutableSet<PositionNodeWrapper>> = HashMap()
        val scope = block.scope!!

        val varWalker = TreeWalker()
        varWalker.addListener({
            node -> node as AssignmentStatement
            val error = scope.tryAdd(node.namedReference)
            handleRedefinitionErrors(conflictingReferences, error, node)
        }, AssignmentStatement::class.java)
        varWalker.addListener({
            node -> node as NamedReference
            if(node.isMethod()) {
                node.referenceType = ReferenceType.Method
            } else {
                if(!scope.isInScope(node.name)) {
                    errors.add(UndefinedVariableUsageError(node))
                }
            }
        }, NamedReference::class.java)
        varWalker.addStopCondition { it is Block }
        block.statements.forEach { varWalker.walk(it) }
        convertToErrors(conflictingReferences)
    }

    private fun convertToErrors(conflictingReferences: MutableMap<String, MutableSet<PositionNodeWrapper>>) {
        conflictingReferences.forEach {
            errors.add(RedefinitionError(
                    it.value
                    .map { it.node as ObjectReference }
                    .toList())
            )
        }
    }

    private fun handleRedefinitionErrors(conflictingReferences: MutableMap<String, MutableSet<PositionNodeWrapper>>, error: RedefinitionError?, node: AssignmentStatement) {
        if (error != null) {
            val name = node.namedReference.name
            var conflicting = conflictingReferences[name]
            if (conflicting == null) {
                conflicting = mutableSetOf()
                conflictingReferences[name] = conflicting
            }
            conflicting.addAll(error.conflictingVariables.map { PositionNodeWrapper(it as Node) })
        }
    }
}

data class UndefinedVariableUsageError(val namedReference: NamedReference) : VerificationError
data class UnknownClassError(val constructorCall : ConstructorCall) : VerificationError
data class NoSuchMethodError(val methodCall : MethodCall) : VerificationError

data class PositionNodeWrapper(val node : Node) {
    override fun equals(other: Any?): Boolean {
        val otherMeta = (other as PositionNodeWrapper).node.metaInfo
        val nodeMeta = node.metaInfo
        return node == other.node && otherMeta?.line == nodeMeta?.line && otherMeta?.position == nodeMeta?.position
    }

    override fun hashCode(): Int {
        var result = node.hashCode()
        result = 31 * result + (node.metaInfo?.line?.hashCode()?:0)
        result = 31 * result + (node.metaInfo?.position?.hashCode()?:0)
        return result
    }
}
