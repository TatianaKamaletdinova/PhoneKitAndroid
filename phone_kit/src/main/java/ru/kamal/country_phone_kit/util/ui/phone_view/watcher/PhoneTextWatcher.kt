package ru.kamal.country_phone_kit.util.ui.phone_view.watcher

import android.text.Editable
import android.text.TextWatcher
import ru.kamal.country_phone_kit.util.formater.PhoneFormatterImpl
import ru.kamal.country_phone_kit.util.ui.phone_view.formatPhoneOnMaxLength
import ru.kamal.country_phone_kit.util.ui.phone_view.formater.PhoneNumberCodeFormatter
import ru.kamal.country_phone_kit.util.ui.phone_view.model.FormattedPhone
import ru.kamal.country_phone_kit.util.ui.phone_view.setMaskNumber
import ru.kamal.country_phone_kit.util.toStringOrEmpty
import ru.kamal.country_phone_kit.util.ui.phone_view.view.AnimatedHintEditText
import ru.kamal.country_phone_kit.util.onlyDigits

/**
 * Вотчер для поля ввода номера
 * @param phoneField само поле ввода номера
 * @param phoneFormatter [PhoneNumberCodeFormatter]
 * @param getMaxPhoneLength - максимальная длина номера
 * @param updatePhoneState - действие после применения маски на номер
 */
internal class PhoneTextWatcher(
    private val phoneField: AnimatedHintEditText,
    private val phoneFormatter: PhoneFormatterImpl,
    private val getMaxPhoneLength: ()-> Int?,
    private val updatePhoneState: (phone: String)-> Unit
) : TextWatcher {

    /** Флаг, чтобы TextWatcher не уходил в рекурсию*/
    private var ignoreOnPhoneChange = false
    /** Флаг, который указывает, текущее состояние курсора, см. [CursorState]*/
    private var cursorState = CursorState.NONE_STATE
    /** Флаг, указывает на индекс позиции, после удаления перед пробелом */
    private var indexForCursorAfterRemovedSpace = 0

    /**
     * @param start индекс текущей позиции курсора
     * @param count длина текста, который будет удален
     * @param after длина текста, который будет добавлен
     */
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        when {
            count == 0 && after == 1 -> cursorState = CursorState.ADD_CHAR
            count == 1 && after == 0 -> {
                if ((s[start] == ' ' || s[start] == '-') && start > 0) {
                    cursorState = CursorState.CURSOR_BEFORE_SPACE
                    indexForCursorAfterRemovedSpace = start - 1
                } else {
                    cursorState = CursorState.REMOVE_CAR
                }
            }
            else -> cursorState = CursorState.NONE_STATE
        }
    }

    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (ignoreOnPhoneChange) return

        ignoreOnPhoneChange = true

        val (inputPhone, indexOfStartCursor) = getInputOnlyDigit(
            phoneField.selectionStart,
            phoneField.text.toStringOrEmpty(),
            cursorState,
            indexForCursorAfterRemovedSpace
        )

        val validPhone = formatPhoneOnMaxLength(inputPhone, getMaxPhoneLength())

        val (formattedPhone, indexForMoveCursor) = setMaskNumber(
            phoneField.hintEditText,
            validPhone,
            indexOfStartCursor,
            cursorState)

        s.replace(0, s.length, formattedPhone)

        if (indexForMoveCursor >= 0) phoneField.setSelection(indexForMoveCursor.coerceAtMost(phoneField.length()))

        updatePhoneState(validPhone)

        ignoreOnPhoneChange = false
    }

    /***
     * Фильтрует input только на числа, УЧИТЫВАЯ удаление в середине и возвращая актуальный индекс для позиции курсора
     *
     * @param indexOfCurrentCursor индекс позиции на которой находится курсор
     * @param number число, которое было введено
     * @param cursorState см. [CursorState]
     * @param indexForCursorAfterRemovedSpace флаг, указывает на индекс позиции, после удаления перед пробелом
     */
    private fun getInputOnlyDigit(
        indexOfCurrentCursor: Int = 0,
        number: String,
        cursorState: CursorState = CursorState.NONE_STATE,
        indexForCursorAfterRemovedSpace: Int = 0,
    ): FormattedPhone {
        var indexForMoveCursor = indexOfCurrentCursor
        var inputNumber = number
        if (cursorState == CursorState.CURSOR_BEFORE_SPACE) {
            inputNumber = inputNumber.substring(0, indexForCursorAfterRemovedSpace) + inputNumber.substring(indexForCursorAfterRemovedSpace + 1)
            indexForMoveCursor--
        }

        val builder = StringBuilder(inputNumber.length)
        for (a in inputNumber.indices) {
            val ch = inputNumber.substring(a, a + 1)
            builder.append(ch.onlyDigits())
        }
        return FormattedPhone(formattedPhone = builder.toString(), indexForMoveCursor = indexForMoveCursor)
    }
}