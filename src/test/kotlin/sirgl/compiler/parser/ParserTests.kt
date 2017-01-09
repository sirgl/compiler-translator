package sirgl.compiler.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.parser.ast.*
import sirgl.compiler.parser.transformer.toAst

class ParserTests {
    @Test
    fun `should parse single simple class`() {
        val parserForText = parserForText("class A{}")
        val ast = parserForText.classDefinition().toAst()
        assertThat(ast).isEqualTo(ClassDefinition(emptyList(), emptyList(), emptyList(), "A"))
    }


    @Test
    fun `int field declaration`() {
        val ast = parserForText("int a;").fieldDeclaration().toAst()
        assertThat(ast).isEqualTo(FieldDeclaration(Field("a"), IntegerType))
    }

    @Test
    fun `object type field declaration`() {
        val ast = parserForText("myType a;").fieldDeclaration().toAst()
        assertThat((ast.type as ObjectType).className).isEqualTo("myType")
        assertThat(ast.field).isEqualTo(Field("a"))
    }


    @Test
    fun `if statement without else`() {
        val ast = parserForText("if(a>12){}").ifStatement().toAst()
        assertThat(ast).isEqualTo(IfStatement(
                GreaterThanExpression(NamedReference("a"), IntLiteral(12)),
                Block(),
                null
        ))
    }

    @Test
    fun `if statement with else`() {
        val ast = parserForText("if(a>12){}else{}").ifStatement().toAst()
        assertThat(ast).isEqualTo(IfStatement(
                GreaterThanExpression(NamedReference("a"), IntLiteral(12)),
                Block(),
                Block()
        ))
    }

    @Test
    fun `if statement with else and content`() {
        val ast = parserForText("if(a>12){int a = 12;}else{int b = 24;}").ifStatement().toAst()
        assertThat(ast).isEqualTo(IfStatement(
                GreaterThanExpression(NamedReference("a"), IntLiteral(12)),
                Block(listOf(
                        AssignmentStatement(NamedReference("a"), IntegerType, IntLiteral(12))
                )),
                Block(listOf(
                        AssignmentStatement(NamedReference("b"), IntegerType, IntLiteral(24))
                ))
        ))
    }

    @Test
    fun `for statement with full control block`() {
        val ast = parserForText("for(int i = 12; i < 100; i = i + 1){}").forStatement().toAst()
        assertThat(ast).isEqualTo(ForStatement(
                SimpleForControl(
                        AssignmentForInitBlock(
                                AssignmentStatement(NamedReference("i"), IntegerType, IntLiteral(12))
                        ),
                        LessThanExpression(NamedReference("i"), IntLiteral(100)),
                        AssignmentExpression(NamedReference("i"), SumExpression(NamedReference("i"), IntLiteral(1)))
                ),
                Block()
        ))
    }

    @Test
    fun `null expression`() {
        val ast = parserForText("null").expression().toAst()
        assertThat(ast).isEqualTo(NullLiteral())
    }

    @Test
    fun `true expression`() {
        val ast = parserForText("true").expression().toAst()
        assertThat(ast).isEqualTo(TrueLiteral())
    }

    @Test
    fun `false expression`() {
        val ast = parserForText("false").expression().toAst()
        assertThat(ast).isEqualTo(FalseLiteral())
    }

    @Test
    fun `string expression`() {
        val ast = parserForText("\"mystring\"").expression().toAst()
        assertThat(ast).isEqualTo(StringLiteral("mystring"))
    }

    @Test
    fun `zero argument call`() {
        val ast = parserForText("this.call()").expression().toAst()
        assertThat(ast).isEqualTo(MethodCallExpression(ThisExpression(), MethodCall("call", emptyList())))
    }

    @Test
    fun `full class example`() {
        val ast = parseClassDef("compiler/parser/full.lng")
        assertThat(ast).isEqualTo(
                ClassDefinition(listOf(
                        MethodDefinition(
                                MethodDeclaration("doX", listOf(Parameter(IntegerType, "x")), VoidType),
                                Block(listOf(
                                        MethodCallExpression(NamedReference("main"), MethodCall("print", listOf())),
                                        IfStatement(NamedReference("b"), Block(listOf(
                                                MethodCallExpression(NamedReference("main"), MethodCall("println", listOf(StringLiteral("good")))),
                                                ReturnStatement(null)
                                        )), Block(listOf(
                                                MethodCallExpression(NamedReference("main"), MethodCall("println", listOf(StringLiteral("bad"))))
                                        ))),
                                        MethodCallExpression(NamedReference("main"), MethodCall("print", listOf(StringLiteral("onEnd!"))))
                                ))
                        )
                ), listOf(
                        FieldDeclaration(Field("b"), BooleanType),
                        FieldDeclaration(Field("c"), IntegerType)
                ), emptyList(), "MyClass"
                )
        )
    }

    @Test
    fun `call current class method`() {
        val ast = parserForText("method(13)").expression().toAst()
        assertThat(ast).isEqualTo(MethodCallExpression(null, MethodCall("method", listOf(IntLiteral(13)))))
    }

    @Test
    fun `empty for control`() {
        val ast = parserForText(";;").forControl().toAst()
        assertThat(ast).isEqualTo(SimpleForControl(null, null, null))
    }

    @Test
    fun `break statement`() {
        val ast = parserForText("break;").breakStatement().toAst()
        assertThat(ast).isEqualTo(BreakStatement())
    }

    @Test
    fun `constructor definition`() {
        val ast = parserForText("MyClass(){}").constructorDefinition().toAst()
        assertThat(ast).isEqualTo(ConstructorDefinition(ConstructorDeclaration("MyClass", emptyList()), Block()))
    }
}
