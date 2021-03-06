package sirgl.compiler.ast

import sirgl.compiler.verification.scope.Scope
import sirgl.compiler.verification.typing.Typed

interface Statement : Node


data class AssignmentStatement(var variable: NamedReference, var variableType: AssignableType, var expression: Expression) : Statement {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(namedReference: NamedReference, variableType: AssignableType, expression: Expression, metaInfo: MetaInfo) : this(namedReference, variableType, expression) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class ReturnStatement(var expression: Expression?) : Statement, Typed {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(expression: Expression?, metaInfo: MetaInfo) : this(expression) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class Block(var statements: List<Statement>) : Scoped, Node {

    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor() : this(emptyList())
    constructor(statements: List<Statement>, metaInfo: BlockMetaInfo) : this(statements) {
        this.metaInfo = metaInfo
        this.parent = parent
    }




    override var scope: Scope?
        get() = (metaInfo as BlockMetaInfo).scope
        set(value) {
            (metaInfo as BlockMetaInfo).scope = value
        }
}