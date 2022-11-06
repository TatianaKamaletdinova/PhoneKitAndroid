package ru.kamal.country_phone_kit.util.ui.phone_view.model

import ru.kamal.country_phone_kit.api.model.Country

/**
 * @param codeForSet код для вставки в поле кода
 * @param country страна, @see [Country]
 * @param numberToMoveToPhone код для вставки в поле номера
 */
internal data class EditorCodeInfo(
    val codeForSet: String,
    val country: Country?,
    val numberToMoveToPhone: String?,
)