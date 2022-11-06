package ru.kamal.phone_kit.util.ui.phone_view.view.layout

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedState(
    private val state: Parcelable?,
    val childrenStates: SparseArray<Parcelable> = SparseArray()
) : View.BaseSavedState(state)