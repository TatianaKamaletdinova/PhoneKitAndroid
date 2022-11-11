package ru.kamal.phone_kit.util.formater

import ru.kamal.phone_kit.api.PhoneFormatter
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.ui.phone_view.formatPhoneOnMaxLength
import ru.kamal.phone_kit.util.ui.phone_view.getCountryByNumber
import ru.kamal.phone_kit.util.ui.phone_view.setMaskNumber
import ru.kamal.phone_kit.util.onlyDigits
import java.util.regex.Pattern

internal class PhoneFormatterImpl constructor(private val countryMap:  Map<String, Country>) : PhoneFormatter {

    private companion object {
        const val PLUS = "+"
    }

    //000 123 00-00 ->  " 123 ", 00 1234-0000 -> " 1234-"
    private val operatorCodeMask = Pattern.compile(" \\d+? | \\d+?-")

    //0 123456 -> "123456"
    private val withoutOperatorMask = Pattern.compile(" \\d+")


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

    override fun formatWithSecretMask(phone: String): String {
        val currentCountry = countryMap.getCountryByNumber(phone) ?: return phone

        val numberWithoutCode = phone
            .removePrefix(currentCountry.code)
            .takeIf { it.isNotBlank() } ?: return "$PLUS$phone"

        val validPhone = formatPhoneOnMaxLength(numberWithoutCode, currentCountry.maxPhoneLength)

        val formattedPhone = setMaskNumber(currentCountry.phoneFormat, validPhone).formattedPhone

        val operatorCodeMaskMatcher = operatorCodeMask.matcher(formattedPhone)
        val numberWithoutOperatorCode = if (operatorCodeMaskMatcher.find()) {
            formattedPhone
                .substring(operatorCodeMaskMatcher.start(), operatorCodeMaskMatcher.end())
                .onlyDigits()
        } else ""

        val mask = if (numberWithoutOperatorCode.isBlank()) {
            val withoutOperatorMatcher = withoutOperatorMask.matcher(formattedPhone)
            if (withoutOperatorMatcher.find()) {
                val withoutOperatorNumber = formattedPhone
                    .substring(withoutOperatorMatcher.start(), withoutOperatorMatcher.end())
                    .onlyDigits()

                val numberWithMask = hideFirstChars(withoutOperatorNumber)
                formattedPhone
                    .replaceRange(withoutOperatorMatcher.start() + 1, withoutOperatorMatcher.end(), numberWithMask)
            } else {
                hideFirstChars(formattedPhone)
            }
        } else {
            formattedPhone
                .replaceRange(operatorCodeMaskMatcher.start() + 1, operatorCodeMaskMatcher.end() - 1, numberWithoutOperatorCode
                    .map { "*" }
                    .joinToString(separator = "")
                )
        }
        return "$PLUS${currentCountry.code} $mask"
    }

    private fun hideFirstChars(phone: String): String {
        return if (phone.length > 2) {
            phone.replaceRange(0, 2, "**")
        } else {
            phone.replaceRange(0, 1, "*")
        }
    }
}