package org.kodein.emoji


internal fun isCodePointInOneChar(code: Int) =
    code in 0x0000..0xD7FF || code in 0xE000..0xFFFF

internal fun codePointCharLength(code: Int) =
    if (isCodePointInOneChar(code)) 1 else 2


internal fun codePointAt(string: String, index: Int): Int {
    if (isCodePointInOneChar(string[index].code)) {
        return string[index].code
    }

    val highSurrogate = string[index].code
    val lowSurrogate = string[index + 1].code
    require(highSurrogate and 0xFC00 == 0xD800) { error("Invalid high surrogate at $index") }
    require(lowSurrogate and 0xFC00 == 0xDC00) { error("Invalid low surrogate at $index") }
    val highBits = highSurrogate and 0x3FF
    val lowBits = lowSurrogate and 0x3FF
    return ((highBits shl 10) or lowBits) + 0x10000
}

internal fun codePoints(str: String): IntArray {
    var index = 0
    var count = 0
    while (index < str.length) {
        val code = codePointAt(str, index)
        index += codePointCharLength(code)
        count += 1
    }

    val array = IntArray(count)
    index = 0
    count = 0
    while (index < str.length) {
        val code = codePointAt(str, index)
        array[count] = code
        index += codePointCharLength(code)
        count += 1
    }

    return array
}
