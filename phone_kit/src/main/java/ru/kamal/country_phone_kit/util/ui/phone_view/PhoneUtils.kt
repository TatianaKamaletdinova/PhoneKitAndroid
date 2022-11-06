package ru.kamal.country_phone_kit.util.ui.phone_view

import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.ui.phone_view.model.FormattedPhone
import ru.kamal.country_phone_kit.util.ui.phone_view.watcher.CursorState


/**
 * Возвращает инфо о стране по номеру
* @param countyCodeWithNumber номер телефона с кодом
*/
internal fun Map<String, Country>.getCountryByNumber(countyCodeWithNumber: String): Country? {
    //код не может быть больше 4 символов, отрезаем лишние числа
    val code = if (countyCodeWithNumber.length >= 4) {
        countyCodeWithNumber.substring(0, 4)
    } else countyCodeWithNumber

    var country: Country? = null

    //перебираем, удаляя по символу у кода пока не найдем страну
    for (a in code.length downTo 0) {
        val sub = code.substring(0, a)
        country = this[sub]
        if (country != null) {
            break
        }
    }

    return country
}

/**
 * Форматирует номер без кода по маске страны
 * @param numberWithoutCode номер без кода страны (пример 9652345)
 * @param hintForPhoneMask маска (пример XXX XXX XX XX)
 * @param indexOfCurrentCursor индекс позиции на которой находится курсор
 * @param cursorState см. [CursorState]
 */
internal fun setMaskNumber(
    hintForPhoneMask: String,
    numberWithoutCode: String,
    indexOfCurrentCursor: Int = 0,
    cursorState: CursorState = CursorState.NONE_STATE,
): FormattedPhone {
    val formattedNumber = StringBuilder(numberWithoutCode)
    var indexForMoveCursor = indexOfCurrentCursor

    //если маски нет - не форматируем номер
    if (hintForPhoneMask.isNotBlank()) {
        //индекс позиции сдвига по маске на след число (после пробела при применении маски)
        var moveCursorOnIndex = 0
        //начинаем цикл от 0 до конца введенного номера
        while (moveCursorOnIndex < formattedNumber.length) {
            //если мы на шаге, где маска еще видна, то нужно форматнуть ввод по маске
            if (moveCursorOnIndex < hintForPhoneMask.length) {
                //если на данной позиции по маске есть спецсимвол, прибавляем его к введенному тексту
                val char = hintForPhoneMask[moveCursorOnIndex]
                if (char == ' ' || char == '-') {
                    formattedNumber.insert(moveCursorOnIndex, char)
                    //сдвигаем индекс, так как был спецсимвол
                    moveCursorOnIndex++
                    if (indexForMoveCursor == moveCursorOnIndex &&
                        cursorState != CursorState.REMOVE_CAR &&
                        cursorState != CursorState.CURSOR_BEFORE_SPACE
                    ) {
                        //сдвигаем позиция курсора на актуальную
                        indexForMoveCursor++
                    }
                }
            }
            //сдвигаем индекс на следующий символ в маске
            moveCursorOnIndex++
        }
    }
    return FormattedPhone(formattedPhone = formattedNumber.toString(), indexForMoveCursor = indexForMoveCursor)
}


/**
 * Проверяет номер телефона без КОДА
 * @param numberWithoutCode номер телефона без КОДА страны
 * @param maxLength если null, строка не обрезается, иначе обрезает по максимальной длине
 */
internal fun formatPhoneOnMaxLength(numberWithoutCode: String, maxLength: Int?): String {
    return if (maxLength != null) {
        if (numberWithoutCode.length < maxLength) {
            numberWithoutCode
        } else {
            numberWithoutCode.removeRange(maxLength, numberWithoutCode.length)
        }
    } else {
        numberWithoutCode
    }
}
