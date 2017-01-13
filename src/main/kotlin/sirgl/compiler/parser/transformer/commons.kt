package sirgl.compiler.parser.transformer

import sirgl.compiler.ast.*

fun LangParser.AssignableTypeContext.toAst() : AssignableType {
    return when (text) {
        "int" -> IntegerType
        "byte" -> ByteType
        "boolean" -> BooleanType
        "char" -> CharType
        "long" -> LongType
        else -> ObjectType(text)
    }
}

fun LangParser.ReturnTypeContext.toAst() : ReturnType {
    return when (text) {
        "int" -> IntegerType
        "byte" -> ByteType
        "boolean" -> BooleanType
        "char" -> CharType
        "long" -> LongType
        "void" -> VoidType
        else -> ObjectType(text)
    }
}