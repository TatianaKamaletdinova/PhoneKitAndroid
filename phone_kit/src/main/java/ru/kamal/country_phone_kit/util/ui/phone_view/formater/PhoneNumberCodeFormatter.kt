package ru.kamal.country_phone_kit.util.ui.phone_view.formater

import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.ui.phone_view.getCountryByNumber
import ru.kamal.country_phone_kit.util.ui.phone_view.model.EditorCodeInfo

/** Форматер, который ищет соответствующую страну по введенному коду */
internal class PhoneNumberCodeFormatter(private var countryMap: Map<String, Country>) {

    /** Форматирует только код при вводе из PhoneNumberView
     * @param inputCode текущий код из поля ввода кода
     * @param textFromPhoneField текущий номер из поля ввода номера
     * */
    fun formatTypingCode(inputCode: String, textFromPhoneField: String): EditorCodeInfo {
        var codeForSet = inputCode
        var country: Country?
        var numberToMoveToPhone: String? = null

        //есть ли страна с таким кодом
        country = countryMap[inputCode]

        // код не может быть больше 4 символов
        if (country == null && inputCode.length >= 4) {
            //ищем совпадения стран с этим кодом по каждому символу символам
            country = countryMap.getCountryByNumber(inputCode)
            //страна найдена
            if (country != null) {
                //убираем из кода не относящиеся к коду числа и переносим их на поле номера
                numberToMoveToPhone = inputCode.removePrefix(country.code) + textFromPhoneField
                //сетим код для вставки в поле кода
                codeForSet = country.code
            } else {
                //если страна не найдена, переносим один символ на поле номера
                numberToMoveToPhone = inputCode.substring(4) + textFromPhoneField
                //сетим код для вставки в поле кода
                codeForSet = inputCode.substring(0, 4)
            }
        } else {
            //ищем страны с совпадением такого кода
            val matchedCountries = countryMap.values.count { it.code.startsWith(codeForSet) }
            if (matchedCountries == 1) {
                country = countryMap[codeForSet]
                // если страны нет - даем ввести еще код
                if (country != null) {
                    //убираем из кода не относящиеся к коду числа и переносим их на поле номера
                    numberToMoveToPhone =
                        codeForSet.substring(country.code.length) + textFromPhoneField
                    //сетим код для вставки в поле кода
                    codeForSet = country.code
                }
            }
        }

        return EditorCodeInfo(
            numberToMoveToPhone = numberToMoveToPhone,
            codeForSet = codeForSet,
            country = country
        )
    }
}