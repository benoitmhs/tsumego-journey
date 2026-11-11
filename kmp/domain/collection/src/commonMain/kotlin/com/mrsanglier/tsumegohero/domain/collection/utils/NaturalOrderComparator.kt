package com.mrsanglier.tsumegohero.domain.collection.utils

/**
 * Sorts names like a file browser (Finder): numbers are compared by value ("p8" before "p12"),
 * letters ignore case. Accents are compared by character code (no locale rules).
 * Equal names by these rules ("p08" and "p8") are ordered by their raw text, so the order is stable.
 */
object NaturalOrderComparator : Comparator<String> {

    override fun compare(a: String, b: String): Int {
        val chunksA = a.chunks()
        val chunksB = b.chunks()
        for (i in 0 until minOf(chunksA.size, chunksB.size)) {
            val chunkA = chunksA[i]
            val chunkB = chunksB[i]
            val result = when {
                chunkA.isNumber() && chunkB.isNumber() -> compareNumbers(chunkA, chunkB)
                else -> chunkA.lowercase().compareTo(chunkB.lowercase())
            }
            if (result != 0) return result
        }
        return (chunksA.size - chunksB.size).takeIf { it != 0 } ?: a.compareTo(b)
    }

    /** Compares digit strings of any length by value. */
    private fun compareNumbers(a: String, b: String): Int {
        val trimmedA = a.trimStart('0')
        val trimmedB = b.trimStart('0')
        return if (trimmedA.length != trimmedB.length) {
            trimmedA.length - trimmedB.length
        } else {
            trimmedA.compareTo(trimmedB)
        }
    }

    /** "p12b" -> ["p", "12", "b"] */
    private fun String.chunks(): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0
        for (i in 1..length) {
            if (i == length || this[i].isDigit() != this[start].isDigit()) {
                chunks += substring(start, i)
                start = i
            }
        }
        return chunks
    }

    private fun String.isNumber(): Boolean = isNotEmpty() && first().isDigit()
}
