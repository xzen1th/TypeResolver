package org.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TypeResolverTest {

    private fun buildVar(name: String, varValues: List<String>?): PhpVariable {
        val doc = varValues?.let { values ->
            val tagsMap = mapOf("@var" to values.map { v ->
                object : DocTag { override fun getValue() = v }
            })
            PhpDocBlock(tagsMap)
        }
        return PhpVariable(name, doc)
    }

    @Test
    fun `Standard type test`() = assertEquals(
        TypeFactory.createType("User"),
        TypeResolver().inferTypeFromDoc(buildVar("\$user", listOf("User")))
    )

    @Test
    fun `Standard primitive type test`() = assertEquals(TypeFactory.createType("int"), TypeResolver().inferTypeFromDoc(buildVar("\$count", listOf("int"))))

    @Test
    fun `Standard type with namespace`() = assertEquals(
        TypeFactory.createType("\\App\\Models\\Product"),
        TypeResolver().inferTypeFromDoc(buildVar("\$p", listOf("\\App\\Models\\Product")))
    )

    @Test
    fun `Union type test`() = assertEquals(
        TypeFactory.createUnionType(
            listOf(
                TypeFactory.createType("string"),
                TypeFactory.createType("int")
            )
        ),
        TypeResolver().inferTypeFromDoc(buildVar("\$id", listOf("string|int")))
    )

    @Test
    fun `Triple union type test`() = assertEquals(
        TypeFactory.createUnionType(
            listOf(
                TypeFactory.createType("string"),
                TypeFactory.createType("int"),
                TypeFactory.createType("null")
            )
        ),
        TypeResolver().inferTypeFromDoc(buildVar("\$val", listOf("string|int|null"))))

    @Test
    fun `Nullable shorthand test`() = assertEquals(
        TypeFactory.createType("?User"),
        TypeResolver().inferTypeFromDoc(buildVar("\$user", listOf("?User")))
    )

    @Test
    fun `Named tag test`() = assertEquals(
        TypeFactory.createType("Logger"),
        TypeResolver().inferTypeFromDoc(buildVar("\$log", listOf("Logger \$log")))
    )

    @Test
    fun `Named tag with complex type`() = assertEquals(
        TypeFactory.createUnionType(
            listOf(
                TypeFactory.createType("Collection"),
                TypeFactory.createType("User[]")
            )
        ),
        TypeResolver().inferTypeFromDoc(buildVar("\$users", listOf("Collection|User[] \$users")))
    )

    @Test
    fun `Named tag with different spacing`() = assertEquals(
        TypeFactory.createType("Manager"),
        TypeResolver().inferTypeFromDoc(buildVar("\$m", listOf("Manager    \$m")))
    )

    @Test
    fun `Name mismatch should return mixed`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$guest", listOf("Admin \$adm")))
    )

    @Test
    fun `Case sensitivity mismatch`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$user", listOf("User \$User")))
    )

    @Test
    fun `Partial name match mismatch`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$id", listOf("int \$identity")))
    )

    @Test
    fun `Multiple tags - pick correct name`() = assertEquals(
        TypeFactory.createType("string"),
        TypeResolver().inferTypeFromDoc(buildVar("\$name", listOf("int \$id", "string \$name")))
    )

    @Test
    fun `Multiple tags - pick first if multiple match name`() = assertEquals(
        TypeFactory.createType("Admin"),
        TypeResolver().inferTypeFromDoc(buildVar("\$u", listOf("Admin \$u", "Guest \$u")))
    )

    @Test
    fun `Multiple tags - pick named over unnamed`() = assertEquals(
        TypeFactory.createType("Expert"),
        TypeResolver().inferTypeFromDoc(buildVar("\$user", listOf("User", "Expert \$user")))
    )

    @Test
    fun `Multiple tags - fallback to unnamed if no name matches`() = assertEquals(
        TypeFactory.createType("User"),
        TypeResolver().inferTypeFromDoc(buildVar("\$guest", listOf("Admin \$adm", "User")))
    )

    @Test
    fun `Fallback no docblock`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$any", null))
    )

    @Test
    fun `Fallback empty var tags`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$any", emptyList()))
    )

    @Test
    fun `Fallback empty tag value`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$any", listOf("")))
    )

    @Test
    fun `Fallback only whitespace in tag`() = assertEquals(
        TypeFactory.createType("mixed"),
        TypeResolver().inferTypeFromDoc(buildVar("\$any", listOf("   ")))
    )

    @Test
    fun `Array type test`() = assertEquals(
        TypeFactory.createType("string[]"),
        TypeResolver().inferTypeFromDoc(buildVar("\$list", listOf("string[]")))
    )

    @Test
    fun `Generic array notation`() = assertEquals(
        TypeFactory.createType("array<int, string>"),
        TypeResolver().inferTypeFromDoc(buildVar("\$map", listOf("array<int, string>")))
    )

    @Test
    fun `Tag with comment after it`() = assertEquals(
        TypeFactory.createType("string"),
        TypeResolver().inferTypeFromDoc(buildVar("\$name", listOf("string \$name This is a name")))
    )

    @Test
    fun `Multiple unnamed tags - last one wins`() = assertEquals(
        TypeFactory.createType("float"),
        TypeResolver().inferTypeFromDoc(buildVar("\$x", listOf("int", "float")))
    )

    @Test
    fun `Generic array with spaces test`() = assertEquals(
        TypeFactory.createType("array<int, string>"),
        TypeResolver().inferTypeFromDoc(buildVar("\$map", listOf("array<int, string> \$map")))
    )
}