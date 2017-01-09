package sirgl.compiler.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FindUpperTests {
    @Test
    fun `find upper scoped in block returns class def`() {
        val classDef = parseClassDef("compiler/parser/scope/undef_vars.lng")
        val block = classDef.methodList[0].block
        val scoped = block.findUpperScoped()
        assertThat(scoped).isEqualTo(classDef)
    }


}

class GetAllSuperclassesTests {
    @Test
    fun `No hierarchy - single class`() {
        val allSuperclasses = getAllSuperclasses(A::class.java)
        assertThat(allSuperclasses).containsOnly(A::class.java)
    }

    @Test
    fun `return full hierarchy`() {
        val allSuperclasses = getAllSuperclasses(C::class.java)
        assertThat(allSuperclasses).containsOnly(A::class.java, B::class.java, C::class.java)
    }

    @Test
    fun `return interfaces also`() {
        val allSuperclasses = getAllSuperclasses(G::class.java)
        assertThat(allSuperclasses).containsOnly(
                A::class.java,
                B::class.java,
                C::class.java,
                D::class.java,
                E::class.java,
                F::class.java,
                G::class.java
        )
    }

    @Test
    fun `return super of interface`() {
        val allSuperclasses = getAllSuperclasses(I::class.java)
        assertThat(allSuperclasses).containsOnly(I::class.java, D::class.java, F::class.java, H::class.java)
    }
}

open class A
open class B : A()
open class C : B()

interface D
interface F
open class E : C(), D
open class G : E(), F
interface H : D, F
class I : H
