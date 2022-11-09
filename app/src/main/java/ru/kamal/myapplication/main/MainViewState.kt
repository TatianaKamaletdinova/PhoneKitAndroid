package ru.kamal.myapplication.main

import ru.kamal.phone_kit.api.phone_view.PhoneResult


data class MainViewState(
    val isEnableButton: Boolean,
    val isErrorInput: Boolean,
    val errorText: String?,
    val codeAndNumberFilled: PhoneResult.CodeAndNumberFilled?
) {
    companion object {
        fun init() = MainViewState(
            isEnableButton = false,
            isErrorInput = false,
            errorText = null,
            codeAndNumberFilled = null,
        )

        fun phoneValid(previous: MainViewState, codeAndNumberFilled: PhoneResult.CodeAndNumberFilled) =
            previous.copy(
                codeAndNumberFilled = codeAndNumberFilled,
                isEnableButton = true,
                isErrorInput = false,
            )

        fun phoneInValid(
            previous: MainViewState,
            isErrorInput: Boolean,
            errorText: String?
        ) = previous.copy(
            isEnableButton = false,
            isErrorInput = isErrorInput,
            errorText = errorText,
            codeAndNumberFilled = null,
        )

        fun notSetPhone(previous: MainViewState) = previous.copy(
            isEnableButton = false,
            isErrorInput = false,
            errorText = null,
            codeAndNumberFilled = null,
        )
    }
}