package sirgl.compiler.verification.typing

import sirgl.compiler.ClassRelatedError
import sirgl.compiler.ast.*
import sirgl.compiler.verification.*
import sirgl.compiler.verification.scope.VerificationError


interface Typed {
    var inferredType: Type?
        get() = ((this as Node).metaInfo as Typed).inferredType
        set(value) {
            ((this as Node).metaInfo as Typed).inferredType = value
        }
}

interface SignatureDefined {
    var signature : MethodSignature?
        get() = ((this as Node).metaInfo as SignatureDefined).signature
        set(value) {
            ((this as Node).metaInfo as SignatureDefined).signature = value
        }
}

class TypeVerifier(val classContext: LangClassContext, val classRegistery: ClassRegistery) {
    val errors = mutableListOf<VerificationError>()

    fun verifyAndInferTypes() {
        setFieldTypes()
        setParametersTypes()
        val treeWalker = TreeWalker()
        treeWalker.addListener({
            block ->
            block as Block
            block.statements.forEach {
                when (it) {
                    is AssignmentStatement -> it.verifyAndInferTypes()
                    is Expression -> it.verifyAndInferTypes()
                }
            }
        }, Block::class.java)
        classContext.compilationUnit.classDefinition.methodList.forEach { treeWalker.walk(it) }
    }

    private fun setParametersTypes() {
        val methods = classContext.compilationUnit.classDefinition.methodList
        methods.flatMap { it.methodDeclaration.parameters }
                .forEach { it.inferredType = it.assignableType }
    }

    private fun setFieldTypes() {
        val fields = classContext.compilationUnit.classDefinition.fieldDeclarations
        fields.forEach { it.field.inferredType = it.type }
    }

    fun AssignmentStatement.verifyAndInferTypes() {
        expression.verifyAndInferTypes()
        val expressionType = expression.inferredType!!
        variable.inferredType = variableType
        if (!expressionType.isCastableTo(variableType)) {
            addError(CastImpossibleError(expression, variable, node = this))
        }
    }

    fun Expression.verifyAndInferTypes() {
        when (this) {
            is ArithmeticExpression -> verifyAndInferTypes()
            is BinaryPredicateExpression -> verifyAndInferTypes()
            is AssignmentExpression -> verifyAndInferType()
            is ObjectCreationExpression -> verifyAndInferTypes()
            is MethodCallExpression -> verifyAndInferTypes()
            //TODO here!!!
        }
    }


    private fun MethodCallExpression.verifyAndInferTypes() {
        caller?.verifyAndInferTypes()
        val callerType = caller?.inferredType ?: ObjectType(classContext.className)
        if(callerType !is ObjectType) {
            addError(UnexpectedExpressionType(caller!!, ObjectType("<any>")))
            throw InferenceErrorOccuredException()
        }
        methodCall.arguments.forEach { it.verifyAndInferTypes() }
        val argumentsTypes = methodCall.arguments.map { it.inferredType!! }
        classRegistery.findMethodAppliableFor(callerType.className, MethodSignature(callerType.className, argumentsTypes, VoidType)) // TODO remake it
    }

    private fun ObjectCreationExpression.verifyAndInferTypes() {
        val className = constructorCall.name
        constructorCall.arguments.forEach { it.verifyAndInferTypes() }
        val inferredTypes = constructorCall.arguments.map { it.inferredType!! }

        val actualSignature = MethodSignature(className, inferredTypes, VoidType)
        val constructorSignature = classRegistery.findConstructorAppliableFor(actualSignature)
        if(constructorSignature == null) {
            addError(NoAvailableSignaturesError(actualSignature))
            throw InferenceErrorOccuredException()
        }
        signature = constructorSignature
        inferredType = ObjectType(constructorSignature.name)
    }

    fun ClassRegistery.findMethodAppliableFor(className: String, actualSignature : MethodSignature): MethodSignature? {
        val overloads = methods[className] ?: return null
        return findSignatureCorrespondingAnyOverload(actualSignature, overloads)
    }

    fun ClassRegistery.findConstructorAppliableFor(actualSignature: MethodSignature): MethodSignature? {
        val overloads = constructors[actualSignature.name] ?: return null
        return findSignatureCorrespondingAnyOverload(actualSignature, overloads)
    }

    private fun findSignatureCorrespondingAnyOverload(actualSignature: MethodSignature, overloads: List<MethodSignature>): MethodSignature {
        return overloads.first {
            it.types
                    .filterIndexed { index, type -> !actualSignature.types[index].isCastableTo(type) }
                    .none()
        }
    }


    private fun BinaryPredicateExpression.verifyAndInferTypes() {
        left.verifyAndInferTypes()
        right.verifyAndInferTypes()
        if (!verifyIsBooleanExpr(left) || !verifyIsBooleanExpr(right)) {
            throw InferenceErrorOccuredException()
        }
    }

    private fun AssignmentExpression.verifyAndInferType() {
        expression.verifyAndInferTypes()
        val expressionType = expression.inferredType
        val variableDefinition = findUpperBlock()?.scope?.findVarsWithName(variable.name)?.get(0)!!
        variable.inferredType = variableDefinition.inferredType
        val isCastable = expressionType?.isCastableTo(variableDefinition.inferredType!!) ?: false
        if (!isCastable) {
            addError(CastImpossibleError(expression, variable, this))
            throw InferenceErrorOccuredException()
        }
        inferredType = variableDefinition.inferredType
    }

    private fun verifyIsBooleanExpr(expression: Expression): Boolean {
        if (expression.inferredType!! !is BooleanType) {
            addError(UnexpectedExpressionType(expression, BooleanType))
            return false
        }
        return true
    }

    private fun ArithmeticExpression.verifyAndInferTypes() {
        left.verifyAndInferTypes()
        right.verifyAndInferTypes()
        val rightType = right.inferredType!!
        val leftType = left.inferredType!!
        if (!verifyIsArithmeticType(left) || !verifyIsArithmeticType(right)) {
            throw InferenceErrorOccuredException()
        }
        if (leftType.isCastableTo(rightType)) {
            inferredType = rightType
        } else if (rightType.isCastableTo(leftType)) {
            inferredType = leftType
        } else {
            throw VerificationException("Unexpected behavior: operands $left $right")
        }
    }

    private fun verifyIsArithmeticType(expression: Expression): Boolean {
        if (!expression.inferredType!!.isArithmetic()) {
            addError(InvalidOperandTypeError(expression))
            return false
        }
        return true
    }


    private fun addError(error: VerificationError) {
        errors.add(ClassRelatedError(classContext.className, error))
    }
}

open class TypeInferenceError : VerificationError

class UnexpectedExpressionType(val expression: Expression, val expectedType: Type) : TypeInferenceError()

class CastImpossibleError(val exprToCast: Expression, val expression: Expression, val node: Node) : TypeInferenceError()

class InvalidOperandTypeError(val operand: Expression) : TypeInferenceError()

class NoAvailableSignaturesError(val actualSignature : MethodSignature) : TypeInferenceError()

class InferenceErrorOccuredException : VerificationException("inference broken")