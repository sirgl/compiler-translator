package sirgl.compiler.ast

import sirgl.compiler.parser.getAllSuperclasses
import sirgl.compiler.verification.MethodSignature
import sirgl.compiler.verification.scope.Scope
import sirgl.compiler.verification.typing.SignatureDefined
import sirgl.compiler.verification.typing.Typed

interface MetaInfo {
    val line: Int
    val position: Int
}

data class LineInfo(override val line: Int, override val position: Int) : MetaInfo

data class TypedLineInfo(override val line: Int, override val position: Int, override var inferredType : Type? = null) : MetaInfo, Typed

data class MethodInfo(override val line: Int, override val position: Int, override var signature: MethodSignature? = null, override var inferredType : Type? = null) : MetaInfo, SignatureDefined, Typed

data class ReferenceInfo(override val line: Int, override val position: Int) : MetaInfo

data class BlockMetaInfo(
        override val line: Int,
        override val position: Int,
        override var scope : Scope?
) : MetaInfo, Scoped

interface Scoped {
    var scope : Scope?
}

data class ObjectReferenceMetaInfo(
        override val line: Int,
        override val position: Int
) : MetaInfo

interface Node {
    var metaInfo : MetaInfo?
    var parent : Node?

    fun <T> findUpper(targetClass: Class<T>) : T? = findUpper<T, Nothing>(targetClass, null)

    fun <T, R : Node> findUpper(targetClass: Class<T>, limitClass : Class<R>?) : T? {
        val parent = parent ?: return null
        val parentClasses: List<Class<*>> = getAllSuperclasses(parent.javaClass)
        if(limitClass != null && parentClasses.contains(limitClass)) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        if(parentClasses.contains(targetClass)) {
            return parent as T
        }
        return parent.findUpper(targetClass, limitClass)
    }

    fun findClassDefinition()  = findUpper(ClassDefinition::class.java)

    fun findUpperBlock(): Block? {
        return findUpper(Block::class.java, ConstructorDefinition::class.java)
    }

    fun findUpperScoped() = findUpper(Scoped::class.java)
}

//Types

interface Type

interface ReturnType : Type
interface AssignableType : ReturnType

object IntegerType : AssignableType
object ByteType : AssignableType
object BooleanType : AssignableType
object CharType : AssignableType
object LongType : AssignableType
object NullType : Type // surrogate type

val stringType = ObjectType("lang.String")
val objectType = ObjectType("lang.String")

data class ObjectType(val className: String) : AssignableType

object VoidType : ReturnType


interface NamedEntity {
    val name : String
}

interface FunctionCall : Node {
    val name: String
    val arguments: List<Expression>
}

interface ObjectReference : Typed {
    val name : String
}

fun Type.isCastableTo(another : Type) : Boolean {
    return when (this) {
        is NullType -> true
        is VoidType -> false
        is BooleanType -> false
        is ByteType -> when (another) {
            is ByteType, is IntegerType, is LongType -> true
            else -> false
        }
        is CharType -> when (another) {
            is CharType, is IntegerType, is LongType -> true
            else -> false
        }
        is IntegerType -> when(another) {
            is IntegerType, is LongType -> true
            else -> false
        }
        is LongType -> when(another) {
            is LongType -> true
            else -> false
        }
        is ObjectType -> another is ObjectType && (another.className == className) // Sometimes here will be inheritance
        else -> false // todo
    }
}

fun Type.isArithmetic()  = this is ByteType || this is IntegerType || this is LongType || this is CharType