package sirgl.compiler.parser.ast

import sirgl.compiler.parser.getAllSuperclasses
import sirgl.compiler.parser.verification.Scope

interface MetaInfo {
    val line: Int
    val position: Int
}

data class LineInfo(override val line: Int, override val position: Int) : MetaInfo

data class MethodInfo(override val line: Int, override val position: Int) : MetaInfo

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

//    fun <T : Node, R : Node> findUpperNode(targetClass: Class<T>, limitClass : Class<R>?) : Node? {
//        val parent = parent ?: return null
//        val parentClass = parent.javaClass
//        if(parentClass == limitClass) {
//            return null
//        }
//        if(parentClass == targetClass) {
//            return parent
//        }
//        return parent.findUpper(targetClass, limitClass)
//    }

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

interface ObjectType : AssignableType {
    val className : String
}

object VoidType : ReturnType


interface NamedEntity {
    val name : String
}

interface FunctionCall : Node {
    val name: String
    val arguments: List<Expression>
}

interface ObjectReference {
    val name : String
}