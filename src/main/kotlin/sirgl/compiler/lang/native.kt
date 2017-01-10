package sirgl.compiler.lang

import java.io.InputStream
import java.io.OutputStream

class DefaultClassesRegistery {

}

interface NativeClass {
    val fullName : String
}

class LangRuntime(
        val inputStream : InputStream,
        val outputStream : OutputStream
)

class LangSystem(val langRuntime : LangRuntime) : NativeClass {
    override val fullName: String = "lang.System"

    fun print(string : String){
        langRuntime.outputStream.write(string.toByteArray())
    }

    fun println(string : String){
        print("$string\n")
    }
}