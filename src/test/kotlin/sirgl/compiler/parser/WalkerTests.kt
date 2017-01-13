package sirgl.compiler.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.ClassDefinition
import sirgl.compiler.ast.MethodCallExpression
import sirgl.compiler.ast.Node
import sirgl.compiler.verification.TreeWalker

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

    @Test
    fun `every node has parent`(){
        val ast = parseClassDef("compiler/parser/full.lng")
        val treeWalker = TreeWalker()
        treeWalker.addListener({
            node -> node as Node
            assertThat(node.parent != null || node is ClassDefinition).isTrue()
        }, Node::class.java)
    }
}