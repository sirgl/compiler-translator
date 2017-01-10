package sirgl.compiler.parser.ast


interface Expression : Statement



interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

//Arithmetic expressions

data class DivideExpression(override val left: Expression, override val right: Expression) : BinaryExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class MultiplyExpression(override val left: Expression, override val right: Expression) : BinaryExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class SumExpression(override val left: Expression, override val right: Expression) : BinaryExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class SubtractionExpression(override val left: Expression, override val right: Expression) : BinaryExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class RemainderExpression(override val left: Expression, override val right: Expression) : BinaryExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

//Predicates

interface BinaryPredicateExpression : BinaryExpression

data class GreaterThanExpression(override val left: Expression, override val right: Expression) : BinaryPredicateExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class LessThanExpression(override val left: Expression, override val right: Expression) : BinaryPredicateExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class LessThanOrEqualsExpression(override val left: Expression, override val right: Expression) : BinaryPredicateExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class GreaterThanOrEqualsExpression(override val left: Expression, override val right: Expression) : BinaryPredicateExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class EqualityExpression(override val left: Expression, override val right: Expression) : BinaryExpression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(left: Expression, right: Expression, metaInfo: MetaInfo) : this(left, right) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}


//Special expressions

data class AssignmentExpression(val namedReference: NamedReference, val expression: Expression) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(namedReference: NamedReference, expression: Expression, metaInfo: MetaInfo) : this(namedReference, expression) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class ObjectCreationExpression(val constructorCall: ConstructorCall) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(constructorCall: ConstructorCall, metaInfo: MetaInfo) : this(constructorCall) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class MethodCallExpression(val caller: Expression?, val methodCall: MethodCall) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(caller: Expression?, methodCall: MethodCall, metaInfo: MetaInfo) : this(caller, methodCall) {
        this.metaInfo = metaInfo
        this.parent = parent
    }

    fun isThisClassMethod() = caller == null
}

data class FieldAccessExpression(val caller: Expression, val fieldName: String) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(caller: Expression, fieldName: String, metaInfo: MetaInfo) : this(caller, fieldName) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

enum class ReferenceType {
    Variable,
    Method
}

open class NamedReference(override val name: String) : Expression, NamedEntity, ObjectReference {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    var referenceType: ReferenceType = ReferenceType.Variable

    constructor(name: String, metaInfo: MetaInfo) : this(name, ReferenceType.Variable, metaInfo)

    constructor(name: String, referenceType: ReferenceType, metaInfo: MetaInfo) : this(name) {
        this.metaInfo = metaInfo
        this.parent = parent
        this.referenceType = referenceType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as NamedReference

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun isMethod() = parent is MethodCallExpression

}

data class Field(override val name: String) : ObjectReference, Node, NamedEntity {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(name: String, metaInfo: MetaInfo) : this(name) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class IntLiteral(val number: Number) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(number: Number, metaInfo: MetaInfo) : this(number) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class StringLiteral(val string: String) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(string: String, metaInfo: MetaInfo) : this(string) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class CharLiteral(val char: Char) : Expression {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(char: Char, metaInfo: MetaInfo) : this(char) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}


class ThisExpression() : Expression {
    override var parent: Node? = null

    constructor(metaInfo: MetaInfo) : this() {
        this.metaInfo = metaInfo
        this.parent = parent
    }

    override var metaInfo: MetaInfo? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}

class TrueLiteral() : Expression {
    override var parent: Node? = null

    constructor(metaInfo: MetaInfo) : this() {
        this.metaInfo = metaInfo
        this.parent = parent
    }

    override var metaInfo: MetaInfo? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}

class FalseLiteral() : Expression {
    override var parent: Node? = null

    constructor(metaInfo: MetaInfo) : this() {
        this.metaInfo = metaInfo
        this.parent = parent
    }

    override var metaInfo: MetaInfo? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}

class NullLiteral() : Expression {
    override var parent: Node? = null

    constructor(metaInfo: MetaInfo) : this() {
        this.metaInfo = metaInfo
        this.parent = parent
    }

    override var metaInfo: MetaInfo? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}
