package ru.example.country_phone_kit

import org.junit.Assert
import org.junit.Test
import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.ui.phone_view.formater.PhoneNumberCodeFormatter
import ru.kamal.country_phone_kit.util.ui.phone_view.model.EditorCodeInfo

@Suppress("NonAsciiCharacters")
class PhoneNumberCodeFormatterTest {

    private val countryMap = mapOf(
        "7" to Country(name = "Russian Federation",
            shortname = "RU",
            maxPhoneLength = 10,
            phoneFormat = "XXX XXX XX XX",
            code = "7",
            isoCode = "643"),
        "1" to Country(name = "US",
            shortname = "US",
            maxPhoneLength = 10,
            phoneFormat = "XXXX XXXXXX",
            code = "1",
            isoCode = "840"),
        "44" to Country(name = "United Kingdom",
            shortname = "GB",
            maxPhoneLength = 10,
            phoneFormat = "XXXX XXXXXX",
            code = "44",
            isoCode = "44"),
        "84" to Country(name = "Vietnam",
            shortname = "VN",
            phoneFormat = "",
            code = "84",
            isoCode = "84"),
        "1268" to Country(name = "Antigua & Barbuda",
            shortname = "AG",
            phoneFormat = "XXXX XXXXXX",
            code = "1268",
            isoCode = "1268"),
    )

    private val phoneNumberCodeFormatter: PhoneNumberCodeFormatter =  PhoneNumberCodeFormatter(countryMap)

    @Test
    fun `вводим 7`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "7",
            country = Country(name = "Russian Federation",
                shortname = "RU",
                maxPhoneLength = 10,
                phoneFormat = "XXX XXX XX XX",
                code = "7",
                isoCode = "643"),
            numberToMoveToPhone = ""
        ), phoneNumberCodeFormatter.formatTypingCode("7", ""))
    }

    @Test
    fun `вводим 1`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "1",
            country = Country(name = "US",
                shortname = "US",
                maxPhoneLength = 10,
                phoneFormat = "XXXX XXXXXX",
                code = "1",
                isoCode = "840"),
            numberToMoveToPhone = null
        ), phoneNumberCodeFormatter.formatTypingCode("1", ""))
    }

    @Test
    fun `вводим 12`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "12",
            country = null,
            numberToMoveToPhone = null
        ), phoneNumberCodeFormatter.formatTypingCode("12", ""))
    }

    @Test
    fun `вводим 168`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "168",
            country = null,
            numberToMoveToPhone = null
        ), phoneNumberCodeFormatter.formatTypingCode("168", ""))
    }

    @Test
    fun `вводим 1468`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "1",
            country = Country(name = "US",
                shortname = "US",
                maxPhoneLength = 10,
                phoneFormat = "XXXX XXXXXX",
                code = "1",
                isoCode = "840"),
            numberToMoveToPhone = "468"
        ), phoneNumberCodeFormatter.formatTypingCode("1468", ""))
    }

    @Test
    fun `вводим 6954`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "6954",
            country = null,
            numberToMoveToPhone = ""
        ), phoneNumberCodeFormatter.formatTypingCode("6954", ""))
    }

    @Test
    fun `вводим 69547`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "6954",
            country = null,
            numberToMoveToPhone = "7"
        ), phoneNumberCodeFormatter.formatTypingCode("69547", ""))
    }

    @Test
    fun `вводим 695478`() {
        Assert.assertEquals(
            EditorCodeInfo(
            codeForSet = "6954",
            country = null,
            numberToMoveToPhone = "78"
        ), phoneNumberCodeFormatter.formatTypingCode("69547", "8"))
    }
}

