package ru.example.country_phone_kit.phone_formatter

import org.junit.Assert
import org.junit.Test
import ru.kamal.phone_kit.api.PhoneFormatter
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.formater.PhoneFormatterImpl

@Suppress("NonAsciiCharacters")
class FormatWithMaskTest {

    private val countryMap = mapOf(
        "7" to Country(name = "Russian Federation",
            shortname = "RU",
            maxPhoneLength = 10,
            phoneFormat = "000 000 00-00",
            code = "7",
            isoCode = "643"),
        "375" to Country(
            name = "Bela",
            shortname = "BE",
            phoneFormat = "00 000-00-00",
            code = "375",
            isoCode = "375",
        ),
        "1" to Country(name = "US",
            shortname = "US",
            maxPhoneLength = 10,
            phoneFormat = "000 000-0000",
            code = "1",
            isoCode = "840"),
        "44" to Country(name = "United Kingdom",
            shortname = "GB",
            maxPhoneLength = 10,
            phoneFormat = "00 0000-0000",
            code = "44",
            isoCode = "44"),
        "84" to Country(name = "Vietnam",
            shortname = "VN",
            phoneFormat = "",
            code = "84",
            isoCode = "84"),
        "1268" to Country(
            name = "Antigua & Barbuda",
            shortname = "AG",
            phoneFormat = "000-0000",
            code = "1268",
            isoCode = "1268",
        ),
        "690" to Country(
            name = "Токелау",
            shortname = "TK",
            phoneFormat = "0000",
            code = "690",
            isoCode = "690",
        ),
        "263" to Country(
            name = "Зимбабве",
            shortname = "ZW",
            phoneFormat = "0 000000",
            code = "263",
            isoCode = "263",
        )
    )

    private
    val phoneCountryTextFormatter: PhoneFormatter = PhoneFormatterImpl(countryMap)

    @Test
    fun `маска1`() {
        Assert.assertEquals("+7 965 *** 51-60", phoneCountryTextFormatter.formatWithSecretMask("79652345160"))
    }

    @Test
    fun `маска2`() {
        Assert.assertEquals("+375 11 ***-11-11", phoneCountryTextFormatter.formatWithSecretMask("375111111111"))
    }

    @Test
    fun `маска3`() {
        Assert.assertEquals("+1 777 ***-7777", phoneCountryTextFormatter.formatWithSecretMask("17777777777"))
    }

    @Test
    fun `маска4`() {
        Assert.assertEquals("+44 11 ****-1111", phoneCountryTextFormatter.formatWithSecretMask("441111111111"))
    }

    @Test
    fun `маска5`() {
        Assert.assertEquals("+84 **77", phoneCountryTextFormatter.formatWithSecretMask("847777"))
    }

    @Test
    fun `маска6`() {
        Assert.assertEquals("+1268 **5-7777", phoneCountryTextFormatter.formatWithSecretMask("12687757777"))
    }

    @Test
    fun `маска7`() {
        Assert.assertEquals("+690 **34", phoneCountryTextFormatter.formatWithSecretMask("6901234"))
    }

    @Test
    fun `маска8`() {
        Assert.assertEquals("+690 **44", phoneCountryTextFormatter.formatWithSecretMask("6904444"))
    }

    @Test
    fun `маска9`() {
        Assert.assertEquals("+7 965 *** 22-22", phoneCountryTextFormatter.formatWithSecretMask("79652222222"))
    }

    @Test
    fun `маска10`() {
        Assert.assertEquals("", phoneCountryTextFormatter.formatWithSecretMask(""))
    }

    @Test
    fun `несуществующий код - отдаем без форматирования и маски`() {
        Assert.assertEquals("434545", phoneCountryTextFormatter.formatWithSecretMask("434545"))
    }

    @Test
    fun `маска11`() {
        Assert.assertEquals("+84 *", phoneCountryTextFormatter.formatWithSecretMask("841"))
    }

    @Test
    fun `маска12`() {
        Assert.assertEquals("+84", phoneCountryTextFormatter.formatWithSecretMask("84"))
    }

    @Test
    fun `маска13`() {
        Assert.assertEquals("+7 **5", phoneCountryTextFormatter.formatWithSecretMask("7965"))
    }

    @Test
    fun `маска14`() {
        Assert.assertEquals("+7", phoneCountryTextFormatter.formatWithSecretMask("7"))
    }

    @Test
    fun `маска15`() {
        Assert.assertEquals("+263 2 **6666", phoneCountryTextFormatter.formatWithSecretMask("2632666666"))
    }

    @Test
    fun `маска16`() {
        Assert.assertEquals("+263 6 **6666", phoneCountryTextFormatter.formatWithSecretMask("2636666666"))
    }

    @Test
    fun `маска17`() {
        Assert.assertEquals("+263 0 **3456", phoneCountryTextFormatter.formatWithSecretMask("2630123456"))
    }

    @Test
    fun `маска18`() {
        Assert.assertEquals("+263 0 **3456", phoneCountryTextFormatter.formatWithSecretMask("2630123456"))
    }
}