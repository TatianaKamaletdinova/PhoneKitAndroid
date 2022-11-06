package ru.kamal.phone_kit.util

import android.text.SpannableStringBuilder

private const val FLAG_NOT_SET = "GO"

//юникод для стрелки не работает, нужно добавлять в строке саму стрелку
private const val ARROW_DOWN = "▾"
private const val DEFAULT_LENGTH_SHORT_COUNTRY = 2
//смещение между прописными буквами ascii и символами региональных индикаторов
private const val OFFSET = 127397

/**
 * Помогает получить эмодзи-флаг страны
 * @param shortName сокращенное название страны по стандарту Alpha2 ISO 3166-1
 * @param isAddArrow нужно ли добавлять к флагу справа ▾
 * */
internal fun getFlag(shortName: String? = null, isAddArrow: Boolean = true): String = SpannableStringBuilder()
    .apply {
        append(getEmoji(countryCodeToEmoji(shortName)))
        if (isAddArrow) append(" ").append(ARROW_DOWN)
    }.toString()

private fun countryCodeToEmoji(shortName: String?): String {
    return if (shortName == null || shortName.length != DEFAULT_LENGTH_SHORT_COUNTRY) {
        FLAG_NOT_SET
    } else shortName
}

private fun getEmoji(code: String): String = StringBuilder()
    .apply {
        val shortName = code.uppercase()
        for (element in shortName) {
            appendCodePoint(element.code + OFFSET)
        }
    }.toString()
