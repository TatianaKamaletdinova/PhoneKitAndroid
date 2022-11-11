package ru.example.country_phone_kit.phone_formatter

import org.junit.Assert
import org.junit.Test
import ru.kamal.phone_kit.api.PhoneFormatter
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.formater.PhoneFormatterImpl

@Suppress("NonAsciiCharacters")
class FormatTest {

    private val countryMap = mapOf(
        "7" to Country(name = "Russian Federation",
            shortname = "RU",
            maxPhoneLength = 10,
            phoneFormat = "XXX XXX XX-XX",
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
        "1268" to Country(
            name = "Antigua & Barbuda",
            shortname = "AG",
            phoneFormat = "XXXX XXXXXX",
            code = "1268",
            isoCode = "1268",
        ),
        "375" to Country(
            name = "Bela",
            shortname = "BE",
            phoneFormat = "XX XXX-XX-XX",
            code = "375",
            isoCode = "375",
        ),
    )

    private
    val phoneCountryTextFormatter: PhoneFormatter = PhoneFormatterImpl(countryMap)

    @Test(expected = NullPointerException::class)
    fun `номер телефона не может быть пустым - NullPointerException`() {
        phoneCountryTextFormatter.format("44")
    }

    @Test
    fun `номер телефона с символами и числами - оставляет только + в начале и цифры`() {
        Assert.assertEquals("+44 847", phoneCountryTextFormatter.format("44srg8s47"))
    }

    @Test
    fun `номер телефона длиннее 10 чисел обрезает`() {
        Assert.assertEquals("+44 1234 567891", phoneCountryTextFormatter.format("44123456789123456"))
    }

    @Test
    fun `номер нормальной длины форматируется`() {
        Assert.assertEquals("+44 9343 454343", phoneCountryTextFormatter.format("449343454343"))
    }

    @Test
    fun `номер очищается от лишних цифр`() {
        Assert.assertEquals(phoneCountryTextFormatter.getClearText("+44 934 345-43-43"), "9343454343")
    }

    @Test
    fun `у номера нет маски - после кода числа без форматирования`() {
        Assert.assertEquals("+84 9343454343", phoneCountryTextFormatter.format("849343454343"))
    }

    @Test
    fun `некорректный код - возвращаем исходный`() {
        Assert.assertEquals("029343454343", phoneCountryTextFormatter.format("029343454343"))
    }

    @Test
    fun `код больше 4 символов`() {
        Assert.assertEquals("+1268 2934 345434", phoneCountryTextFormatter.format("12682934345434"))
    }

    @Test
    fun `код из 1 символа`() {
        Assert.assertEquals("+1 3682 934345", phoneCountryTextFormatter.format("136829343454343"))
    }
}