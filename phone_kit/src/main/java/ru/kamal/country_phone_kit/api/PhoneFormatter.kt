package ru.kamal.country_phone_kit.api

import androidx.annotation.Keep
import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.formater.PhoneFormatterImpl


@Keep
fun getPhoneFormatter(countryMap:  Map<String, Country>): PhoneFormatter {
    return PhoneFormatterImpl(countryMap)
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

    /** Возвращает телефон как цифры, без суффикса "+" и кода страны **/
    fun getClearText(input: String): String
}