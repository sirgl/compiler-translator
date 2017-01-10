package sirgl.compiler.parser.verification

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ClassRelatedError
import sirgl.compiler.MainController
import sirgl.compiler.parser.fromFile
import sirgl.compiler.verification.UndefinedClassReference

class ResolverTests {
    @Test
    fun `simple resolve set`() {
        val mainController = MainController()
        val files = listOf(
                "compiler/parser/verification/resolver/resolving/resolve1.lng",
                "compiler/parser/verification/resolver/resolving/resolve2.lng",
                "compiler/parser/verification/resolver/resolving/resolve3.lng"
        )
        val classes = mainController.parse(files.map(::fromFile))
        val scopeErrors = mainController.checkScope(classes)
        val resolveErrors = mainController.resolveClasses(classes)
        assertThat(scopeErrors).isEmpty()
        assertThat(resolveErrors).isEmpty()
    }

    @Test
    fun `should not resolve if symbol not found`() {
        val mainController = MainController()
        val files = listOf(
                "compiler/parser/verification/resolver/not_resolving/resolve1.lng",
                "compiler/parser/verification/resolver/not_resolving/resolve2.lng"
        )
        val classes = mainController.parse(files.map(::fromFile))
        val scopeErrors = mainController.checkScope(classes)
        val resolveErrors = mainController.resolveClasses(classes)
        assertThat(scopeErrors).isEmpty()
        assertThat(resolveErrors).containsOnly(ClassRelatedError("B", UndefinedClassReference("C")))
    }
}