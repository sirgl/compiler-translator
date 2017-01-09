package sirgl.compiler.parser.transformer

import sirgl.compiler.parser.ast.*

class ParserException(message: String?) : RuntimeException(message)

fun LangParser.ClassDefinitionContext.toAst(): ClassDefinition {
    val classDefinitionExpressions = classDefinitionBlock().classDefinitionExpression()
    val methodDefinitions = mutableListOf<MethodDefinition>()
    val fieldDeclarations = mutableListOf<FieldDeclaration>()
    val constructors = mutableListOf<ConstructorDefinition>()
    for (classDefinitionExpression in classDefinitionExpressions) {
        val fieldDeclaration = classDefinitionExpression.fieldDeclaration()
        val methodDefinition = classDefinitionExpression.methodDefinition()
        val constructorDefinition = classDefinitionExpression.constructorDefinition()
        if (fieldDeclaration != null) {
            fieldDeclarations.add(fieldDeclaration.toAst())
        }
        if (methodDefinition != null) {
            methodDefinitions.add(methodDefinition.toAst())
        }
        if (constructorDefinition != null) {
            constructors.add(constructorDefinition.toAst())
        }
    }
    val classDefinition = ClassDefinition(methodDefinitions, fieldDeclarations, constructors, Identifier().text, LineInfo(start.line, start.charPositionInLine))
    methodDefinitions.map { it.parent = classDefinition }
    fieldDeclarations.map { it.parent = classDefinition }
    constructors.map { it.parent = classDefinition }
    return classDefinition
}

fun LangParser.MethodDefinitionContext.toAst(): MethodDefinition {
    val methodDefinition = MethodDefinition(methodDeclaration().toAst(), block().toAst(), LineInfo(start.line, start.charPositionInLine))
    methodDefinition.methodDeclaration.parent = methodDefinition
    methodDefinition.block.parent = methodDefinition
    return methodDefinition
}

fun LangParser.MethodDeclarationContext.toAst(): MethodDeclaration {
    val methodDeclaration = MethodDeclaration(Identifier().text, parameters().toAst(), returnType().toAst(), LineInfo(start.line, start.charPositionInLine))
    methodDeclaration.parameters.map { it.parent = methodDeclaration }
    return methodDeclaration
}

fun LangParser.ParametersContext.toAst() = parameter().map { it.toAst() }

fun LangParser.ParameterContext.toAst() = Parameter(assignableType().toAst(), Identifier().text, LineInfo(start.line, start.charPositionInLine))

fun LangParser.FieldDeclarationContext.toAst(): FieldDeclaration {
    val symbol = Identifier().symbol
    val field = Field(Identifier().text, ObjectReferenceMetaInfo(symbol.line, symbol.charPositionInLine))
    val fieldDeclaration = FieldDeclaration(field, assignableType().toAst(), LineInfo(start.line, start.charPositionInLine))
    field.parent = fieldDeclaration
    return fieldDeclaration
}

fun LangParser.ConstructorDeclarationContext.toAst(): ConstructorDeclaration {
    val parameters = parameters().toAst()
    val constructorDeclaration = ConstructorDeclaration(Identifier().text, parameters, LineInfo(start.line, start.charPositionInLine))
    parameters.map { it.parent = constructorDeclaration }
    return constructorDeclaration
}

fun LangParser.ConstructorDefinitionContext.toAst(): ConstructorDefinition {
    val constructorDeclaration = constructorDeclaration().toAst()
    val block = block().toAst()
    val constructorDefinition = ConstructorDefinition(constructorDeclaration, block, LineInfo(start.line, start.charPositionInLine))
    constructorDeclaration.parent = constructorDeclaration
    block.parent = constructorDeclaration
    return constructorDefinition
}
