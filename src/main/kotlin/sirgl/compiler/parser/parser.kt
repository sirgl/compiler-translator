package sirgl.compiler.parser

import LangLexer
import LangParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BufferedTokenStream
import sirgl.compiler.ast.ClassDefinition
import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.parser.transformer.toAst
import java.io.ByteArrayInputStream
import java.io.InputStream


fun lexerForText(text : String) = lexerForStream(ByteArrayInputStream(text.toByteArray()))

fun parserForLexer(lexer : LangLexer) = LangParser(BufferedTokenStream(lexer))

fun lexerForStream(inputStream : InputStream) = LangLexer(ANTLRInputStream(inputStream))

fun parserForText(text : String) = parserForLexer(lexerForText(text))

fun parserForStream(inputStream : InputStream) = parserForLexer(lexerForStream(inputStream))

fun fromFile(name: String): InputStream = ClassLoader.getSystemResourceAsStream(name)

fun parseClassDef(name: String): ClassDefinition {
    val parser = parserForStream(fromFile(name))
    return parser.classDefinition().toAst()
}

fun parseCompilationUnit(name: String): CompilationUnit{
    val parser = parserForStream(fromFile(name))
    return parser.compilationUnit().toAst()
}

