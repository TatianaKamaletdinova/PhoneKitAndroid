package ru.kamal.phone_kit.api.phone_view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.kamal.phone_kit.api.model.Country

/** Модель результата ввода телефона */
sealed interface PhoneResult : Parcelable {

    /** Начальная инициализация - код и номер не заполнены **/
    @Parcelize object NotSet : PhoneResult

    /** Пустой код **/
    @Parcelize object EmptyCode : PhoneResult

    /** Некорректный код страны **/
    @Parcelize object ErrorCountryCode : PhoneResult

    /** Код и номер заполнены некорректно **/
    @Parcelize object Invalid : PhoneResult

    /** Код и номер заполнены корректно
     * @param number - номер телефона без кода страны
     * @param country - см [Country]
     ***/
    @Parcelize data class CodeAndNumberFilled(
        val number: String,
        val country: Country,
    ) : PhoneResult
}