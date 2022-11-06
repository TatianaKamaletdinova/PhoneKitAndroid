package ru.kamal.country_phone_kit.util.formater

import ru.kamal.country_phone_kit.api.PhoneFormatter
import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.ui.phone_view.formatPhoneOnMaxLength
import ru.kamal.country_phone_kit.util.ui.phone_view.getCountryByNumber
import ru.kamal.country_phone_kit.util.ui.phone_view.setMaskNumber
import ru.kamal.country_phone_kit.util.onlyDigits

internal class PhoneFormatterImpl constructor(private val countryMap:  Map<String, Country>) : PhoneFormatter {

    override fun format(input: String): String {
        val digitPhone = input.onlyDigits()
        val currentCountry = countryMap.getCountryByNumber(digitPhone) ?: return input

        val numberWithoutCode = digitPhone
            .removePrefix(currentCountry.code)
            .takeIf { it.isNotBlank() } ?: throw NullPointerException("номер не может быть пустым")

        val validPhone = formatPhoneOnMaxLength(numberWithoutCode, currentCountry.maxPhoneLength)

        val formattedPhone = setMaskNumber(currentCountry.phoneFormat, validPhone).formattedPhone
        return "+${currentCountry.code} $formattedPhone"
    }

    override fun getCountryByNumber(countyCodeWithNumber: String): Country? {
        return countryMap.getCountryByNumber(countyCodeWithNumber)
    }

    override fun getClearText(input: String): String {
        val phoneDigit = input.onlyDigits()
        val codeCountry = countryMap.getCountryByNumber(phoneDigit) ?: throw NullPointerException("такого кода страны нет")
        return phoneDigit.removePrefix(codeCountry.code)
    }
}