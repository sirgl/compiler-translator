package sirgl.compiler.parser.ast

data class CompilationUnit(val imports: List<ImportDeclaration>, val classDefinition: ClassDefinition, val packageDeclaration: PackageDeclaration?) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(anImports: List<ImportDeclaration>, classDefinition: ClassDefinition, packageDeclaration: PackageDeclaration?, metaInfo: MetaInfo) : this(anImports, classDefinition, packageDeclaration) {
        this.metaInfo = metaInfo
    }

    val fullName = getFull()

    private fun getFull() : String {
        var packagePrefix  = ""
        if(packageDeclaration != null) {
            packagePrefix = "${packageDeclaration.name}."
        }
        return packagePrefix + classDefinition.name
    }
}

data class ImportDeclaration(val name: String) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(name: String, metaInfo: MetaInfo) : this(name) {
        this.metaInfo = metaInfo
    }
}

data class PackageDeclaration(val name: String) : Node {
    override var metaInfo: MetaInfo? = null
    override var parent: Node? = null

    constructor(name: String, metaInfo: MetaInfo) : this(name) {
        this.metaInfo = metaInfo
    }
}