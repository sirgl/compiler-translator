package sirgl.compiler.ast

interface ForControl : Node

interface ForInitBlock : Node

data class ExpressionForInitBlock(val expression: Expression) : ForInitBlock {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(expression: Expression, metaInfo: MetaInfo) : this(expression) {
        this.parent = parent
        this.metaInfo = metaInfo
    }
}

data class AssignmentForInitBlock(val assignmentStatement: AssignmentStatement) : ForInitBlock {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(assignmentStatement: AssignmentStatement, metaInfo: MetaInfo) : this(assignmentStatement) {
        this.parent = parent
        this.metaInfo = metaInfo
    }
}

data class SimpleForControl(val forInitBlock: ForInitBlock?, val condition: Expression?, val afterIteration: Expression?) : ForControl {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(forInitBlock: ForInitBlock, condition: Expression?, afterIteration: Expression?, metaInfo: MetaInfo) : this(forInitBlock, condition, afterIteration) {
        this.parent = parent
        this.metaInfo = metaInfo
    }
}

data class IfStatement(val condition: Expression, val block: Block, val elseBlock: Block?) : Statement {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(condition: Expression, block: Block, elseBlock: Block?, metaInfo: MetaInfo) : this(condition, block, elseBlock) {
        this.parent = parent
        this.metaInfo = metaInfo
    }
}

data class ForStatement(val forControl: ForControl, val block: Block) : Statement {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(forControl: ForControl, block: Block, metaInfo: MetaInfo) : this(forControl, block) {
        this.parent = parent
        this.metaInfo = metaInfo
    }
}

data class WhileStatement(val condition: Expression, val block: Block) : Statement {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(condition: Expression, block: Block, metaInfo: MetaInfo) : this(condition, block) {
        this.parent = parent
        this.metaInfo = metaInfo
    }
}

class BreakStatement() : Statement {
    constructor(metaInfo: MetaInfo) : this() {
        this.parent = parent
        this.metaInfo = metaInfo
    }
    override var parent: Node? = null
    override var metaInfo: MetaInfo? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}

class ContinueStatement() : Statement {
    constructor(metaInfo: MetaInfo) : this() {
        this.parent = parent
        this.metaInfo = metaInfo
    }
    override var parent: Node? = null
    override var metaInfo: MetaInfo? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}