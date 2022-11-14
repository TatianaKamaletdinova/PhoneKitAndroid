package ru.kamal.phone_kit.api.phone_view

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import ru.kamal.country_phone_kit.R
import ru.kamal.country_phone_kit.databinding.ViewPhoneBinding
import ru.kamal.phone_kit.api.getPhoneFormatter
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.*
import ru.kamal.phone_kit.util.data.CountriesRepository
import ru.kamal.phone_kit.util.formater.PhoneFormatterImpl
import ru.kamal.phone_kit.util.ui.country_alert.CountryBottomSheet
import ru.kamal.phone_kit.util.ui.phone_view.formater.DigitsOnlyTextInputFilter
import ru.kamal.phone_kit.util.ui.phone_view.formater.PhoneNumberCodeFormatter
import ru.kamal.phone_kit.util.ui.phone_view.model.InputResult
import ru.kamal.phone_kit.util.ui.phone_view.view.layout.BaseLinearLayout
import ru.kamal.phone_kit.util.ui.phone_view.watcher.CodeTextWatcher
import ru.kamal.phone_kit.util.ui.phone_view.watcher.PhoneTextWatcher

/**
 * Компонент для ввода телефона.
 * Поддерживает коды всех стран мира на основе [PhoneFormatterImpl].
 */
class PhoneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseLinearLayout(context, attrs, defStyleAttr) {

    private val phoneFormatter by lazyUnsafe { getPhoneFormatter(context.resources) }
    private var phoneResultFlow = MutableStateFlow<PhoneResult>(PhoneResult.NotSet)

    /** Флоу для получения результата корректного ввода телефона */
    val phoneFlow: StateFlow<PhoneResult> = phoneResultFlow

    /** Устанавливает дефолтную страну - Россию **/
    var setupRussiaCountry = true
        set(value) {
            setupDefaultCountry()
            field = value
        }

    var isError: Boolean = false
        set(value) {
            field = value
            invalidateError()
        }

    var errorText: String? = null
        set(value) {
            field = value
            binding.errorView.text = errorText
        }

    private val binding: ViewPhoneBinding =
        ViewPhoneBinding.inflate(LayoutInflater.from(context), this)

    private var currentCountry: Country? = null
    private var countryState: InputResult = InputResult.NotSet
    private var phoneState: InputResult = InputResult.NotSet

    private val phoneNumberCodeFormatter by lazyUnsafe {
        PhoneNumberCodeFormatter(
            CountriesRepository.getCodeCountiesMap(resources)
        )
    }
    private val phoneCountryTextFormatter by lazyUnsafe {
        PhoneFormatterImpl(
            CountriesRepository.getCodeCountiesMap(
                resources
            )
        )
    }

    private val countryBottomSheet by lazyUnsafe { CountryBottomSheet(context, ::updateSelectedCountry) }

    private val codeTextWatcher: CodeTextWatcher by lazyUnsafe {
        CodeTextWatcher(
            codeField = binding.codeField,
            phoneNumberCodeFormatter = phoneNumberCodeFormatter,
            setEmptyCode = ::setEmptyCode,
            getTextFromPhoneField = { binding.phoneField.text.toStringOrEmpty() },
            setInputCode = ::setInputCode
        )
    }
    private val phoneTextWatcher: PhoneTextWatcher by lazyUnsafe {
        PhoneTextWatcher(
            phoneField = binding.phoneField,
            phoneFormatter = phoneCountryTextFormatter,
            getMaxPhoneLength = { currentCountry?.maxPhoneLength },
            updatePhoneState = { validPhone -> updatePhoneState(validPhone) }
        )
    }

    init {
        getAttributes(attrs)
        setupCodeField()
        setupPhoneField()
        setupVies()
        setupDefaultCountry()
    }

    /** Устанавливает фокус и открывает клавиатуру **/
    fun setupFocusAndShowKeyboard(isSetup: Boolean) = withUnit(binding) {
        if (isSetup) {
            if (isPhoneFocus()) {
                phoneField.requestFocus()
                phoneField.setSelection(phoneField.length())
                onFocusAndShowKeyboard(true)
            } else {
                codeField.requestFocus()
                codeField.setSelection(codeField.length())
                onFocusAndShowKeyboard(false)
            }
        } else {
            phoneField.clearFocus()
            codeField.clearFocus()
            binding.phoneField.hideKeyboard()
            binding.codeField.hideKeyboard()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val isFocus = binding.codeField.isFocused || binding.phoneField.isFocused
        val superState = super.onSaveInstanceState()
        return SavedPhoneNumberState(
            superState = superState,
            setupFocusAndShowKeyboard = isFocus,
            code = binding.codeField.text.toStringOrEmpty(),
            number = binding.phoneField.text.toStringOrEmpty(),
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedPhoneNumberState = state as? SavedPhoneNumberState
        savedPhoneNumberState?.let {
            setupFocusAndShowKeyboard(savedPhoneNumberState.setupFocusAndShowKeyboard)
            withUnit(binding) {
                codeTextWatcher.ignoreOnCodeChange = true

                codeField.setText(it.code)
                val country = phoneFormatter.getCountryByNumber(it.code)
                checkCounty(country)
                phoneField.setText(it.number)

                codeTextWatcher.ignoreOnCodeChange = false
            }
        }
        super.onRestoreInstanceState(savedPhoneNumberState?.superState)
    }

    private fun setupDefaultCountry() {
        if (setupRussiaCountry) binding.codeField.setText("7")
    }

    /** Устанавливает номер в поле **/
    fun setupPhone(value: String?) {
        try {
            if (value.isNullOrBlank()) return

            codeTextWatcher.ignoreOnCodeChange = true

            val country = phoneFormatter.getCountryByNumber(value)
            if (binding.codeField.text.toStringOrEmpty().onlyDigits() != country?.code) binding.codeField.setText(country?.code)

            val clearText = phoneFormatter.getClearText(value)
            if (binding.phoneField.text.toStringOrEmpty().onlyDigits() != clearText) {
                checkCounty(country)
                if (clearText.isNotBlank()) binding.phoneField.setText(clearText)
            }

            codeTextWatcher.ignoreOnCodeChange = false

        } catch (_: Throwable) {
            //если код страны не найден, то в поле ничего не вставится
        }
    }


    private fun getAttributes(attrs: AttributeSet?) {
        readAttrs(attrs, R.styleable.PhoneView) {
            setupRussiaCountry = getBoolean(R.styleable.PhoneView_initDefaultCountry, true)
        }
    }

    private fun invalidateError() {
        if (isError) {
            binding.errorView.isVisible = true
        } else {
            binding.errorView.isInvisible = true
        }
    }

    private fun onFocusAndShowKeyboard(isFocusPhoneField: Boolean) = withUnit(binding) {
        postDelayed(300) {
            if (isFocusPhoneField) phoneField.showKeyboard() else codeField.showKeyboard()
        }
    }

    private fun isPhoneFocus(): Boolean {
        return if (isEnabled) binding.codeField.length() != 0 else false
    }

    private fun setupVies() {
        withUnit(binding) {
            errorView.setTextColor(context.getColor(R.color.red_70))
            title.setTextColor(context.getColor(R.color.color_accent))
            lineCode.setBackgroundColor(context.getColor(R.color.grey))
        }

        setupFlagView()
    }

    private fun setupFlagView() {
        withUnit(binding.flag) {
            setFactory {
                val tv = TextView(context)
                tv.setTextColor(context.getColor(R.color.grey))
                tv.maxLines = 1
                tv.isSingleLine = true
                tv.ellipsize = TextUtils.TruncateAt.END
                tv.gravity = Gravity.START
                tv
            }

            val anim = AnimationUtils.loadAnimation(context, R.anim.text_in)
            inAnimation = anim
            setOnClickListener { countryBottomSheet.showDialog() }
            setFlag()
        }
    }

    /** Устанавливает выбранную страну */
    private fun updateSelectedCountry(selectCountry: Country?) {
        selectCountry ?: return
        codeTextWatcher.ignoreOnCodeChange = true
        binding.codeField.setText(selectCountry.code)
        binding.codeField.setSelection(binding.codeField.length())
        setInputCode(selectCountry, binding.phoneField.text.toStringOrEmpty())
        codeTextWatcher.ignoreOnCodeChange = false
    }

    /** Устанавливает флаг страны - смайлик флага */
    private fun setFlag(shortname: String? = null) {
        val flag = getFlag(shortname)
        val anim = AnimationUtils.loadAnimation(
            context,
            if (binding.flag.currentView.text != null) {
                R.anim.text_out_down
            } else {
                R.anim.text_out
            }
        )
        binding.flag.outAnimation = anim
        val prevText: CharSequence = binding.flag.currentView.text
        binding.flag.setText(flag, prevText.toString() != flag && prevText.isNotBlank())
    }

    private fun setTitleColor(focused: Boolean) = withUnit(binding) {
        if (focused) {
            title.setTextColor(context.getColor(R.color.color_accent))
            lineCode.setBackgroundColor(context.getColor(R.color.color_accent))
        } else {
            title.setTextColor(context.getColor(R.color.grey))
            lineCode.setBackgroundColor(context.getColor(R.color.grey))
        }
    }

    private fun updatePhoneResult(newCountryState: InputResult, newPhoneState: InputResult) {
        countryState = newCountryState
        phoneState = newPhoneState
        phoneResultFlow.update { getPhoneResult() }
    }

    private fun getPhoneResult(): PhoneResult {
        return when {
            binding.codeField.text.toStringOrEmpty().isBlank() -> PhoneResult.EmptyCode

            countryState is InputResult.Invalid -> PhoneResult.ErrorCountryCode

            countryState is InputResult.Valid && phoneState is InputResult.Valid -> {
                PhoneResult.CodeAndNumberFilled(
                    country = currentCountry ?: throw NullPointerException(),
                    number = binding.phoneField.text.toStringOrEmpty().replace(" ", "")
                )
            }

            else -> PhoneResult.Invalid
        }
    }

    private fun setupCodeField() {
        withUnit(binding.codeField) {
            filters = arrayOf(DigitsOnlyTextInputFilter())

            binding.codeField.addTextChangedListener(codeTextWatcher)
            setOnFocusChangeListener { _, focused: Boolean ->
                if (focused) binding.codeField.setSelection(binding.codeField.length())
                setTitleColor(focused || binding.phoneField.isFocused)
            }

            setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    binding.phoneField.requestFocus()
                    binding.phoneField.setSelection(binding.phoneField.length())
                    return@setOnEditorActionListener true
                }
                false
            }
        }
    }

    private fun setEmptyCode() {
        updatePhoneResult(newCountryState = InputResult.Empty, newPhoneState = phoneState)
        currentCountry = null
        setPhoneHint("")
        setFlag()
    }

    private fun setInputCode(country: Country?, numberToMoveToPhone: String?) {
        checkCounty(country)
        if (numberToMoveToPhone != null && numberToMoveToPhone.isNotBlank()) {
            moveCursorOnPhoneIfValidCodeOrLengthMoreFour(numberToMoveToPhone)
        }
    }

    private fun checkCounty(country: Country?) {
        if (country != null) {
            currentCountry = country
            updatePhoneResult(newCountryState = InputResult.Valid, newPhoneState = phoneState)

            setPhoneHint(currentCountry?.phoneFormat)
            setFlag(country.shortname)
        } else {
            currentCountry = null
            updatePhoneResult(
                newCountryState = InputResult.Invalid,
                newPhoneState = InputResult.Invalid
            )

            setFlag()
        }
    }

    private fun moveCursorOnPhoneIfValidCodeOrLengthMoreFour(codeToSet: String) =
        withUnit(binding) {
            phoneField.requestFocus()
            phoneField.setText(codeToSet)
            phoneField.setSelection(binding.phoneField.length())
        }

    private fun setPhoneHint(phoneFormat: String?) {
        if (phoneFormat != null && phoneFormat.isNotEmpty()) {
            binding.phoneField.hintEditText = phoneFormat
        } else {
            binding.phoneField.hintEditText = ""
        }
    }

    private fun setupPhoneField() {
        with(binding.phoneField) {
            setOnKeyListener { _, keyCode, event ->
                when {
                    keyCode == KeyEvent.KEYCODE_DEL && binding.phoneField.length() == 0 -> moveCursorOnCode(
                        event
                    )
                    keyCode == KeyEvent.KEYCODE_DEL && selectionStart == 0 -> moveCursorOnCode(
                        event
                    )
                }
                return@setOnKeyListener super.onKeyDown(keyCode, event)
            }

            binding.phoneField.addTextChangedListener(phoneTextWatcher)

            setOnFocusChangeListener { _, focused: Boolean -> setTitleColor(focused || binding.codeField.isFocused) }
        }
    }

    private fun moveCursorOnCode(event: KeyEvent) {
        binding.codeField.requestFocus()
        binding.codeField.setSelection(binding.codeField.length())
        binding.codeField.dispatchKeyEvent(event)
    }

    private fun updatePhoneState(validPhone: String) {
        val numbers = validPhone.replace(" ", "")
        val maxPhoneLength = currentCountry?.maxPhoneLength

        val newPhoneState = when {
            maxPhoneLength != null -> {
                if (numbers.length == maxPhoneLength) {
                    InputResult.Valid
                } else {
                    if (numbers.length < maxPhoneLength) InputResult.Invalid else phoneState
                }
            }
            else -> if (numbers.isNotBlank()) InputResult.Valid else InputResult.Invalid
        }

        updatePhoneResult(newCountryState = countryState, newPhoneState = newPhoneState)
    }

    @Parcelize
    private data class SavedPhoneNumberState(
        val superState: Parcelable?,
        val setupFocusAndShowKeyboard: Boolean = false,
        val code: String,
        val number: String,
    ) : Parcelable
}