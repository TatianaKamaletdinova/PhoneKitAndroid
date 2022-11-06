package ru.kamal.country_phone_kit.util

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StyleableRes
import androidx.core.content.res.use
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal fun Any?.toStringOrEmpty(): String = this?.toString() ?: ""

inline fun <T> withUnit(receiver: T, block: T.() -> Unit): Unit = with(receiver, block)

internal fun CharSequence.onlyDigits(): String = this.toString().onlyDigits()

internal fun String.onlyDigits(): String = this.replace("""[^\d]""".toRegex(), "")

internal fun EditText.showKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

internal fun EditText.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(this.windowToken, 0)
}

internal inline fun <A, B, R> ifNotNull(a: A?, b: B?, func: (A, B) -> R): R? {
    return if (a != null && b != null) {
        func.invoke(a, b)
    } else null
}

internal fun <T> lazyUnsafe(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

internal fun View.readAttrs(
    attrs: AttributeSet?,
    @StyleableRes styleable: IntArray,
    f: TypedArray.() -> Unit,
) {
    context.obtainStyledAttributes(attrs, styleable, 0, 0).use(f)
}

internal fun CoroutineScope.launchSafeIgnoreError(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    finally: suspend CoroutineScope.() -> Unit = {},
    mutex: Mutex? = null,
    body: suspend CoroutineScope.() -> Unit,
): Job = this.launchSafe(
    context = context,
    start = start,
    error = { /* Ничего не делаем */ },
    body = body,
    finally = finally,
    mutex = mutex
)

internal fun CoroutineScope.launchSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: suspend CoroutineScope.(error: Throwable) -> Unit,
    body: suspend CoroutineScope.() -> Unit,
    finally: suspend CoroutineScope.() -> Unit = {},
    mutex: Mutex? = null,
): Job = this.launch(context = context, start = start) {
    mutex?.withLock {
        launchSafeFunc(this, error, body, finally)
    } ?: launchSafeFunc(this, error, body, finally)
}

private suspend fun launchSafeFunc(
    scope: CoroutineScope,
    error: suspend CoroutineScope.(error: Throwable) -> Unit,
    body: suspend CoroutineScope.() -> Unit,
    finally: suspend CoroutineScope.() -> Unit = {},
) {
    try {
        body.invoke(scope)
    } catch (e: Throwable) {
        if (e !is CancellationException) error.invoke(scope, e)
    } finally {
        finally.invoke(scope)
    }
}
