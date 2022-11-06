package ru.kamal.phone_kit.util.ui.phone_view.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import android.widget.ViewSwitcher

/** Обертка для TextView с красивой анимацией при изменении текста **/
internal class TextViewSwitcher @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewSwitcher(context, attrs) {

    fun setText(text: CharSequence?, animated: Boolean) {
        if (text != currentView.text) {
            if (animated) {
                nextView.text = text
                showNext()
            } else {
                currentView.text = text
            }
        }
    }

    fun addView(child: TextView) {
        super.addView(child)
    }

    override fun getCurrentView(): TextView {
        return super.getCurrentView() as TextView
    }

    override fun getNextView(): TextView {
        return super.getNextView() as TextView
    }
}