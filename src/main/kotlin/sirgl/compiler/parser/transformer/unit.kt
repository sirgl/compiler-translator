package sirgl.compiler.parser.transformer

import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.ast.ImportDeclaration
import sirgl.compiler.ast.LineInfo
import sirgl.compiler.ast.PackageDeclaration

fun LangParser.ImportDeclarationContext.toAst(): ImportDeclaration {
    return ImportDeclaration(Identifier().text, LineInfo(Identifier().symbol.line, Identifier().symbol.charPositionInLine))
}

fun LangParser.CompilationUnitContext.toAst() : CompilationUnit {
    val classDefinition = classDefinition().toAst()
    val imports = importDeclaration().map { it.toAst() }
    var packageDeclaration: PackageDeclaration? = null
    if(packageDeclaration() != null) {
        packageDeclaration = packageDeclaration().toAst()
    }
    val compilationUnit = CompilationUnit(imports, classDefinition, packageDeclaration, LineInfo(0, 0))
    classDefinition.parent = compilationUnit
    imports.forEach { it.parent = compilationUnit }
    packageDeclaration?.parent = compilationUnit
    return compilationUnit
}

fun LangParser.PackageDeclarationContext.toAst() : PackageDeclaration {
    return PackageDeclaration(Identifier().text, LineInfo(Identifier().symbol.line, Identifier().symbol.charPositionInLine))
}