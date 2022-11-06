package ru.kamal.myapplication.main


data class MainViewState(
    val isEnableButton: Boolean,
    val isErrorInput: Boolean,
    val errorText: String?,
) {
    companion object {
        fun init() = MainViewState(
            isEnableButton = false,
            isErrorInput = false,
            errorText = null,
        )

        fun phoneValid(previous: MainViewState) =
            previous.copy(
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
        )

        fun notSetPhone(previous: MainViewState) = previous.copy(
            isEnableButton = false,
            isErrorInput = false,
            errorText = null,
        )
    }
}