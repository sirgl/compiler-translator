package sirgl.compiler.verification

import sirgl.compiler.parser.ast.*
import sirgl.compiler.parser.getAllSuperclasses
import sirgl.compiler.parser.transformer.ParserException

class TreeWalker {
    private val listenersMap: MutableMap<Class<*>, MutableList<(Node) -> Unit>> = mutableMapOf()
    private val stopConditions: MutableList<(Node) -> Boolean> = mutableListOf()

    fun walk(classDefinition: ClassDefinition) {
        if(checkContinueCondition(classDefinition)) {
            return
        }
        notifyListeners(classDefinition)
        classDefinition.constructors.forEach { walk(it) }
        classDefinition.fieldDeclarations.forEach { walk(it) }
        classDefinition.methodList.forEach { walk(it) }
    }

    fun walk(methodDefinition: MethodDefinition) {
        if(checkContinueCondition(methodDefinition)) {
            return
        }
        notifyListeners(methodDefinition)
        walk(methodDefinition.block)
        walk(methodDefinition.methodDeclaration)
    }

    fun walk(methodDeclaration: MethodDeclaration) {
        if(checkContinueCondition(methodDeclaration)) {
            return
        }
        notifyListeners(methodDeclaration) // end
    }

    fun walk(fieldDeclaration: FieldDeclaration) {
        if(checkContinueCondition(fieldDeclaration)) {
            return
        }
        notifyListeners(fieldDeclaration) // end
    }

    fun walk(constructor: ConstructorDefinition) {
        if(checkContinueCondition(constructor)) {
            return
        }
        notifyListeners(constructor)
        walk(constructor.constructorDeclaration)
        walk(constructor.block)
    }

    fun walk(block: Block) {
        if(checkContinueCondition(block)) {
            return
        }
        notifyListeners(block)
        block.statements.forEach { walk(it) }
    }

    fun walk(statement: Statement) {
        when (statement) {
            is ReturnStatement -> {
                if(checkContinueCondition(statement)) {
                    return
                }
                notifyListeners(statement)
            }
            is BreakStatement -> {
                if(checkContinueCondition(statement)) {
                    return
                }
                notifyListeners(statement)
            }
            is ContinueStatement -> {
                if(checkContinueCondition(statement)) {
                    return
                }
                notifyListeners(statement)
            }
            is AssignmentStatement -> walk(statement)
            is Expression -> walk(statement)
            is IfStatement -> walk(statement)
            is ForStatement -> walk(statement)
            is WhileStatement -> walk(statement)
            else -> throw ParserException("Can't walk this type of statement: ${statement.javaClass}")
        }
    }

    fun walk(expression: Expression) {
        when (expression) {
            is BinaryExpression -> walk(expression)
            is AssignmentExpression -> walk(expression)
            is ObjectCreationExpression -> walk(expression)
            is MethodCallExpression -> walk(expression)
            is FieldAccessExpression -> walk(expression)
            is NamedReference, is StringLiteral, is CharLiteral, is IntLiteral, is ThisExpression,
            is TrueLiteral, is FalseLiteral, is NullLiteral -> notifyListeners(expression)
            else -> {
                if(checkContinueCondition(expression)) {
                    return
                }
                throw ParserException("Can't walk this type of expression: ${expression.javaClass}")
            }
        }
    }

    fun walk(expression : FieldAccessExpression) {
        if(checkContinueCondition(expression)) {
            return
        }
        notifyListeners(expression)
        walk(expression.caller)
    }

    fun walk(expression : MethodCallExpression) {
        if(checkContinueCondition(expression)) {
            return
        }
        notifyListeners(expression)
        if(expression.caller != null) {
            walk(expression.caller)
        }
        walk(expression.methodCall)
    }

    fun walk(methodCall: MethodCall) {
        if(checkContinueCondition(methodCall)) {
            return
        }
        notifyListeners(methodCall)
        methodCall.arguments.forEach { walk(it) }
    }

    fun walk(expression : ObjectCreationExpression) {
        if(checkContinueCondition(expression)) {
            return
        }
        notifyListeners(expression)
        walk(expression.constructorCall)
    }

    fun walk(constructorCall: ConstructorCall) {
        if(checkContinueCondition(constructorCall)) {
            return
        }
        notifyListeners(constructorCall)
        constructorCall.arguments.forEach { walk(it) }
    }

    fun walk(expression : AssignmentExpression) {
        if(checkContinueCondition(expression)) {
            return
        }
        notifyListeners(expression)
        walk(expression.namedReference)
        walk(expression.expression)
    }

    fun walk(expression: NamedReference) {
        if(checkContinueCondition(expression)) {
            return
        }
        notifyListeners(expression)
    }

    fun walk(expression : BinaryExpression) {
        if(checkContinueCondition(expression)) {
            return
        }
        notifyListeners(expression)
        walk(expression.left)
        walk(expression.right)
    }

    fun walk(statement: AssignmentStatement) {
        if(checkContinueCondition(statement)) {
            return
        }
        notifyListeners(statement)
        walk(statement.expression)
    }

    fun walk(statement: WhileStatement) {
        if(checkContinueCondition(statement)) {
            return
        }
        notifyListeners(statement)
        walk(statement.condition)
        walk(statement.block)
    }

    fun walk(statement: ForStatement) {
        if(checkContinueCondition(statement)) {
            return
        }
        notifyListeners(statement)
        walk(statement.forControl)
        walk(statement.block)
    }

    fun walk(forControl: ForControl) {
        if(checkContinueCondition(forControl)) {
            return
        }
        notifyListeners(forControl)
        when (forControl) {
            is SimpleForControl -> walk(forControl)
            else -> throw ParserException("Can't walk this type of for control: ${forControl.javaClass}")
        }
    }

    fun walk(forControl : SimpleForControl) {
        if(checkContinueCondition(forControl)) {
            return
        }
        notifyListeners(forControl)
        if(forControl.forInitBlock != null) {
            walk(forControl.forInitBlock)
        }
        if(forControl.condition != null) {
            walk(forControl.condition)
        }
        if(forControl.afterIteration != null) {
            walk(forControl.afterIteration)
        }
    }

    fun walk(forInitBlock: ForInitBlock) {
        if(checkContinueCondition(forInitBlock)) {
            return
        }
        notifyListeners(forInitBlock)
        when (forInitBlock) {
            is AssignmentForInitBlock -> walk(forInitBlock.assignmentStatement)
            is ExpressionForInitBlock -> walk(forInitBlock.expression)
            else -> throw ParserException("for init block not supported: ${forInitBlock.javaClass}")
        }
    }

    fun walk(statement: IfStatement) {
        if(checkContinueCondition(statement)) {
            return
        }
        notifyListeners(statement)
        walk(statement.condition)
        walk(statement.block)
        if (statement.elseBlock != null) {
            walk(statement.elseBlock)
        }
    }

    fun walk(constructorDeclaration: ConstructorDeclaration) {
        if(checkContinueCondition(constructorDeclaration)) {
            return
        }
        notifyListeners(constructorDeclaration) // end
    }

    private fun notifyListeners(node: Node){

        val classes = getAllSuperclasses(node.javaClass)
        classes.map { listenersMap[it]}.forEach { it?.forEach { it.invoke(node) } }
    }

    fun addListener(listener: (Any) -> Unit, nodeClass: Class<*>) {
        var listeners = listenersMap[nodeClass]
        if (listeners == null) {
            listeners = mutableListOf()
        }
        listeners.add(listener)
        listenersMap[nodeClass] = listeners
    }

    private fun checkContinueCondition(node: Node): Boolean {
        return stopConditions.any { it(node) }
    }

    fun addStopCondition(listener: (Node) -> Boolean) {
        stopConditions.add(listener)
    }
}