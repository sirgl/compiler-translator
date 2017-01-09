package sirgl.compiler.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.parser.transformer.toAst
import sirgl.compiler.parser.verification.extractMethodNames

class ResolutionTests {
    @Test
    fun `one method class resolution`() {
        val classDefinition = parserForStream(fromFile("compiler/parser/full.lng")).classDefinition().toAst()
        val methodNames = extractMethodNames(classDefinition)
        assertThat(methodNames).containsExactly("doX")
    }
}