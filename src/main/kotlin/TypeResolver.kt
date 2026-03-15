package org.task

class TypeResolver
{
    fun inferTypeFromDoc(variable: PhpVariable): PhpType
    {
        /* take every @var tag, if null or empty -> returning "mixed" */
        val varTags = variable.getDocBlock()
            ?.getTagsByName("@var")
            ?.ifEmpty { return TypeFactory.createType("mixed") }
            ?: return TypeFactory.createType("mixed")

        /* get variable name from fun arg */
        val varName = variable.getName().ifEmpty { return TypeFactory.createType("mixed") }

        /* create the tag candidate for a case where we don't have a variable name in @var tag */
        var tagCandidate: PhpType? = null

        for (tag in varTags)
        {
            /*
            * tagValue -> get DocTag value from API (e.g., "Users $user")
            * varStart -> check if there is a var name in DocTag
            * rawType -> if there is a var name, create substring from 0 to "$" location, else just trim
            * types -> split rawType if there are multiple variables separated by |
            * processedType -> if there is only 1 type, create PhpType for it in advance, else create UnionType
            *  */
            val tagValue = tag.getValue().trim().ifEmpty { continue }
            val varStart = tagValue.indexOf("$")
            val rawType = if(varStart != -1) tagValue.substringBefore("$").trim() else { tagValue.trim() }.ifEmpty { continue }

            val types = rawType.split("|").map { it.trim() }.filter { it.isNotEmpty() }

            val processedType = if(types.size == 1) TypeFactory.createType(types[0])
            else {
                val unionType = types.map { TypeFactory.createType(it) }
                TypeFactory.createUnionType(unionType)
            }

            /*
            * Check if there is a var name
            * if it is -> get it and check if it is equal to a variable name from fun arg
            * if not -> set it as a tag candidate
            * */
            when
            {
                varStart != -1 -> {
                    val tagName = tagValue.substring(varStart).trim().split(Regex("\\s+"))[0]
                    if(tagName == varName) return processedType
                }
                else -> tagCandidate = processedType
            }
        }
        /*
        * If we have a tag candidate -> return it, else return mixed
        * */
        return tagCandidate ?: TypeFactory.createType("mixed")
    }
}