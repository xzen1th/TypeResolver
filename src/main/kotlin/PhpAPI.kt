package org.task

open class PhpType(val typeName: String)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhpType) return false
        return typeName == other.typeName
    }
    override fun hashCode(): Int = typeName.hashCode()
    override fun toString(): String = typeName
}
class UnionType(val types: List<PhpType>) : PhpType(types.joinToString("|"))
{
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is UnionType) return false
        return types == other.types
    }
    override fun hashCode(): Int = types.hashCode()
}

class PhpVariable(name: String, docBlock: PhpDocBlock?) {
    private val internalName = name
    private val internalDocBlock = docBlock

    fun getName(): String = internalName
    fun getDocBlock(): PhpDocBlock? = internalDocBlock
}

class PhpDocBlock(val tags: Map<String, List<DocTag>>) {
    fun getTagsByName(tagName: String): List<DocTag> = tags[tagName] ?: emptyList()
}

interface DocTag {
    fun getValue(): String
}

class TypeFactory {
    companion object {
        fun createType(typeName: String): PhpType = PhpType(typeName)
        fun createUnionType(types: List<PhpType>): PhpType = UnionType(types)
    }
}