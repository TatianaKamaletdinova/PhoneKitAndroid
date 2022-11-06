package ru.kamal.phone_kit.util.ui.phone_view.formater

import android.text.InputFilter
import android.text.Spanned
import ru.kamal.phone_kit.util.onlyDigits

internal class DigitsOnlyTextInputFilter : InputFilter {

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        return source?.onlyDigits()
    }
}