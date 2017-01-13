package sirgl.compiler.ast

import sirgl.compiler.verification.MethodSignature
import sirgl.compiler.verification.scope.Scope
import sirgl.compiler.verification.typing.Typed

data class ClassDefinition(
        var methodList: List<MethodDefinition>,
        var fieldDeclarations: List<FieldDeclaration>,
        var constructors : List<ConstructorDefinition>,
        override var name: String
) : Node, NamedEntity, Scoped {
    override var scope: Scope? = Scope(null)
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(methodList : List<MethodDefinition>, fieldDeclarations : List<FieldDeclaration>, constructors : List<ConstructorDefinition>, name : String, metaInfo : MetaInfo?) :
            this(methodList, fieldDeclarations, constructors, name) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class Parameter(var assignableType : AssignableType, override var name : String) : Node, ObjectReference, Typed {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(assignableType : AssignableType, name : String, metaInfo : MetaInfo) : this(assignableType, name) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class MethodDeclaration(var methodName : String, var parameters : List<Parameter>, var resultType : ReturnType) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(methodName : String, parameters : List<Parameter>, resultType : ReturnType, metaInfo : MetaInfo) : this(methodName, parameters, resultType) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class MethodDefinition(var methodDeclaration : MethodDeclaration, var block : Block) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(methodDeclaration : MethodDeclaration, block : Block, metaInfo : MetaInfo) : this(methodDeclaration, block) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class FieldDeclaration(var field : Field, var type : AssignableType) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(field : Field, type : AssignableType, metaInfo : MetaInfo) : this(field, type) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class ConstructorDefinition(var constructorDeclaration: ConstructorDeclaration, var block : Block) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(constructorDeclaration : ConstructorDeclaration, block : Block, metaInfo : MetaInfo) : this(constructorDeclaration, block) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class ConstructorDeclaration(var className : String, var parameters : List<Parameter>) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(className : String, parameters : List<Parameter>, metaInfo : MetaInfo) : this(className, parameters)  {
        this.metaInfo = metaInfo
        this.parent = parent
    }

//    fun toSignature() : Boolean {
//
//    }

}

data class ConstructorCall(override val name: String, override val arguments: List<Expression>) : FunctionCall {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(name : String, arguments : List<Expression>, metaInfo : MetaInfo) : this(name, arguments) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}

data class MethodCall(override val name: String, override val arguments: List<Expression>) : FunctionCall {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null
    constructor(name : String, arguments : List<Expression>, metaInfo : MetaInfo) : this(name, arguments) {
        this.metaInfo = metaInfo
        this.parent = parent
    }
}
