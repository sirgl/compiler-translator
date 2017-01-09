package sirgl.tests

import java.lang.reflect.Field
import java.util.*


// TODO it is not recursive version, add recurive one!
fun assertEqualsFieldByField(source: Any, ref: Any, privateAlso: Boolean) {
    val sourceClass = source.javaClass
    val refClass = ref.javaClass
    val sourceFields = HashSet(sourceClass.fields.toList())
    val refFields = HashSet(refClass.fields.toList())
    assertFieldSetsEqual(refClass, refFields, sourceClass, sourceFields)
    if(privateAlso) {
        hackAccessible(sourceFields)
    }

    val notEqualFields = sourceFields
            .map { Triple(it.get(source), it.get(ref), it) }
            .filter { it.first != it.second }
    if(!notEqualFields.isEmpty()) {
        throw AssertionError()
    }
}

private fun hackAccessible(fields: HashSet<Field>) = fields.forEach { it.isAccessible = true }

private fun assertFieldSetsEqual(refClass: Class<Any>, refFields: HashSet<Field>, sourceClass: Class<Any>, sourceFields: HashSet<Field>) {
    sourceFields.containsAll(refFields)
    if (sourceFields != refFields) {
        val clarification = getClarificationForDifferentFieldSets(refClass, sourceClass, refFields, sourceFields)
        throw AssertionError("Class $sourceClass and class $refClass have different sets of fields\n$clarification")
    }
}

private fun getClarificationForDifferentFieldSets(refClass: Class<Any>, sourceClass: Class<Any>, refFields: HashSet<Field>, sourceFields: HashSet<Field>): String {
    val sourceFieldsOnly = (sourceFields - refFields).map { it.name }
    var clarification1: String = getClarificationForFieldSet(refClass, refFields, sourceFields)
    val clarification2: String = getClarificationForFieldSet(sourceClass, sourceFields, refFields)
    if(!clarification1.isEmpty()) {
        clarification1 += "\n"
    }
    return clarification1 + clarification2
}

private fun getClarificationForFieldSet(clazz: Class<Any>, classFields: HashSet<Field>, anotherFields: HashSet<Field>): String {
    val thisClassOnlyFields = (classFields - anotherFields).map { it.name }
    var clarification = ""
    if (!thisClassOnlyFields.isEmpty()) {
        clarification = "Class $clazz has additionally fields: $thisClassOnlyFields"
    }
    return clarification
}