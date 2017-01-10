package sirgl.compiler.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.parser.ast.IntegerType
import sirgl.compiler.parser.ast.Parameter
import sirgl.compiler.parser.ast.NamedReference
import sirgl.compiler.verification.scope.RedefinitionError
import sirgl.compiler.verification.scope.ScopeVerifier
import sirgl.compiler.verification.scope.UndefinedVariableUsageError

class ScopeVerifierTests{
    @Test
    fun `single block with undef vars leads to errors`() {
        val classDef = parseCompilationUnit("compiler/parser/scope/undef_vars.lng")
        val scopeVerifier = ScopeVerifier(classDef, emptyList())
        val scopeCheck = scopeVerifier.checkScope()
        assertThat(scopeCheck).containsOnly(
                UndefinedVariableUsageError(NamedReference("a")),
                UndefinedVariableUsageError(NamedReference("b")),
                UndefinedVariableUsageError(NamedReference("c"))
        )
    }

    @Test
    fun `should not get error on defined variable`() {
        val classDef = parseCompilationUnit("compiler/parser/scope/def_and_undef_vars.lng")
        val scopeVerifier = ScopeVerifier(classDef, emptyList())
        val scopeCheck = scopeVerifier.checkScope()
        assertThat(scopeCheck).containsOnly(
                UndefinedVariableUsageError(NamedReference("b")),
                UndefinedVariableUsageError(NamedReference("c"))
        )
    }

    @Test
    fun `should not get error on field usage`() {
        val classDef = parseCompilationUnit("compiler/parser/scope/field_usage.lng")
        val scopeVerifier = ScopeVerifier(classDef, emptyList())
        val scopeCheck = scopeVerifier.checkScope()
        assertThat(scopeCheck).isEmpty()
    }

    @Test
    fun `redefinition check`() {
        val classDef = parseCompilationUnit("compiler/parser/scope/redefinition.lng")
        val scopeVerifier = ScopeVerifier(classDef, emptyList())
        val scopeCheck = scopeVerifier.checkScope()
        assertThat(scopeCheck).containsOnly(RedefinitionError(listOf(Parameter(IntegerType, "a"), NamedReference("a"), NamedReference("a"))))
    }

    @Test
    fun `big example`() {
        val classDef = parseCompilationUnit("compiler/parser/full.lng")
        val scopeVerifier = ScopeVerifier(classDef, emptyList())
        val scopeCheck = scopeVerifier.checkScope()
        assertThat(scopeCheck).isEmpty()
    }
}