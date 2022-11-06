package ru.kamal.phone_kit.util.ui.phone_view.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Модель для внутреннего анализа во вью ввода полей кода и номера */
internal sealed interface InputResult : Parcelable {
    /** Поле не задано - инициализация **/
    @Parcelize object NotSet : InputResult
    /** Поле пустое **/
    @Parcelize object Empty : InputResult
    /** Поле корректное **/
    @Parcelize object Valid : InputResult
    /** Поле не корректное **/
    @Parcelize object Invalid : InputResult
}