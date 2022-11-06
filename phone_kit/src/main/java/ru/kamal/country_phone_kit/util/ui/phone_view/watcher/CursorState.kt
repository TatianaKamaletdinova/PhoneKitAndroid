package ru.kamal.country_phone_kit.util.ui.phone_view.watcher

internal enum class CursorState {
    /** Курсор поставлен перед спецсимволом чтобы его удалить **/
    CURSOR_BEFORE_SPACE,
    /** Курсора нет **/
    NONE_STATE,
    /** Курсор ставится после добавления **/
    ADD_CHAR,
    /** Курсор ставится после удаления **/
    REMOVE_CAR
}