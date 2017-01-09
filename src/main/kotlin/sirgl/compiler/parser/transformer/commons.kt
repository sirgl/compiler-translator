package sirgl.compiler.parser.transformer

import sirgl.compiler.parser.ast.*

fun LangParser.AssignableTypeContext.toAst() : AssignableType {
    return when (text) {
        "int" -> IntegerType
        "byte" -> ByteType
        "boolean" -> BooleanType
        "char" -> CharType
        "long" -> LongType
        else -> object : ObjectType {
            override val className: String = text
        }
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
        else -> object : ObjectType {
            override val className: String = text
        }
    }
}