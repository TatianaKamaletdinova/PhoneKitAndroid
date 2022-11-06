package ru.kamal.phone_kit.util.ui.phone_view.watcher

import android.text.Editable
import android.text.TextWatcher
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.ui.phone_view.formater.PhoneNumberCodeFormatter
import ru.kamal.phone_kit.util.toStringOrEmpty
import ru.kamal.phone_kit.util.ui.phone_view.view.AnimatedHintEditText

/**
 * Вотчер для поля ввода кода
 * При вводе кода ищет страну из [PhoneNumberCodeFormatter]
 * И если по коду найдет страну,
 * то в параметре setInputCode она вернется и numberToMoveToPhone укажет строку для переноса текста в
 * поле номера
 *
 * @param codeField само поле ввода кода
 * @param phoneNumberCodeFormatter [PhoneNumberCodeFormatter]
 * @param setEmptyCode действие при пустом коде
 * @param getTextFromPhoneField текущий номер из поля ввода номера
 * @param setInputCode действие при коде после форматирования
 */
internal class CodeTextWatcher(
    private val codeField: AnimatedHintEditText,
    private val phoneNumberCodeFormatter: PhoneNumberCodeFormatter,
    private val setEmptyCode: ()-> Unit,
    private val getTextFromPhoneField: ()-> String,
    private val setInputCode: (country: Country?, numberToMoveToPhone: String?)-> Unit,
): TextWatcher {

    /** флаг, чтобы TextWatcher не уходил в рекурсию*/
    var ignoreOnCodeChange = false

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (ignoreOnCodeChange) return
        ignoreOnCodeChange = true

        val inputCode = codeField.text.toStringOrEmpty()
        if (inputCode.isEmpty()) setEmptyCode() else setCode(s, inputCode)

        ignoreOnCodeChange = false
    }

    private fun setCode(s: Editable, inputCode: String) {
        val (code, country, numberToMoveToPhone) =
            phoneNumberCodeFormatter.formatTypingCode(inputCode, getTextFromPhoneField())

        s.replace(0, s.length, code)

        setInputCode(country, numberToMoveToPhone)
    }
}