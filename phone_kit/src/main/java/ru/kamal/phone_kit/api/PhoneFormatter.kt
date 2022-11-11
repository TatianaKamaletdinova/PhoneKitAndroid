package ru.kamal.phone_kit.api

import android.content.res.Resources
import androidx.annotation.Keep
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.data.CountriesRepository
import ru.kamal.phone_kit.util.formater.PhoneFormatterImpl


@Keep
fun getPhoneFormatter(resources: Resources): PhoneFormatter {
    return PhoneFormatterImpl(CountriesRepository.getCodeCountiesMap(resources))
}
/** Форматер номера телефона с поддержкой кодов всех стран мира **/
interface PhoneFormatter {

    /**
     * Метод, определяет по номеру телефона его код страны и форматирует номер по маске этой страны
     * @param input строка с кодом и номером телефона
     */
    fun format(input: String): String

    /**
     * Возвращает инфо о стране по номеру
     * @param countyCodeWithNumber номер телефона с кодом
     */
    fun getCountryByNumber(countyCodeWithNumber: String): Country?

    /** Возвращает телефон как цифры, без суффикса "+" и кода страны
     *  @param input строка с кодом и номером телефона
     * **/
    fun getClearText(input: String): String

    /** Форматирует номер и подставляет звездочки после кода оператора телефона или
     * если нет кода оператора подставляет звездочки на первые 1 или 2 цифры номера
     * @param phone строка с кодом и номером телефона **/
    fun formatWithSecretMask(phone: String): String
}