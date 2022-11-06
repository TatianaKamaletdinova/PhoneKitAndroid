package ru.kamal.country_phone_kit.util.ui.phone_view.view.layout

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children

open class BaseLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        this.setChildrenEnabled(enabled)
    }

    private fun ViewGroup.setChildrenEnabled(isEnabled: Boolean) {
        children.forEach {
            it.isEnabled = isEnabled
            (it as? ViewGroup)?.setChildrenEnabled(isEnabled)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) { dispatchFreezeSelfOnly(container) }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).also {
            for (i in 0 until childCount) getChildAt(i).saveHierarchyState(it.childrenStates)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? SavedState)?.let {
            super.onRestoreInstanceState(it.superState)

            for (i in 0 until childCount) getChildAt(i).restoreHierarchyState(it.childrenStates)
        }
    }
}

