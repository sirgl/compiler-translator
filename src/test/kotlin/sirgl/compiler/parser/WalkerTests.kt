package sirgl.compiler.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.parser.ast.MethodCallExpression
import sirgl.compiler.parser.verification.TreeWalker

class WalkerTests  {
    @Test
    fun `Tree walker no listener`() {
        TreeWalker().walk(parseClassDef("compiler/parser/full.lng"))
    }

    @Test
    fun `Tree walker with one listener`() {
        val treeWalker = TreeWalker()
        val methodNames = mutableListOf<String>()
        treeWalker.addListener({
            node -> node as MethodCallExpression
            methodNames.add(node.methodCall.name)
        }, MethodCallExpression::class.java)
        treeWalker.walk(parseClassDef("compiler/parser/full.lng"))
        assertThat(methodNames).containsExactly("print", "println", "println", "print")
    }
}