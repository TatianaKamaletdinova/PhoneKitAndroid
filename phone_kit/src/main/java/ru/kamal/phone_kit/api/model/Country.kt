package ru.kamal.phone_kit.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @param name имя страны
 * @param code телефонный код страны
 * @param isoCode цифровой код страны по стандарту ISO 3166-1
 * @param shortname сокращенное название страны по стандарту Alpha2 ISO 3166-1
 * @param phoneFormat маска для номера (если нет - пустая строка)
 * @param maxPhoneLength валидная длина номера (если null - ограничений на длину нет)
 */
@Parcelize
data class Country(
    val name: String,
    val code: String,
    val isoCode: String,
    val shortname: String,
    val phoneFormat: String = "",
    val maxPhoneLength: Int? = null,
): Parcelable