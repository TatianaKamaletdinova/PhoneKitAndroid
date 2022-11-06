package ru.kamal.myapplication.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import ru.kamal.phone_kit.api.phone_view.PhoneResult
import ru.kamal.phone_kit.util.withUnit
import ru.kamal.myapplication.R
import ru.kamal.myapplication.databinding.FragmentMainBinding
import ru.kamal.myapplication.second.SecondFragment
import ru.kamal.myapplication.util.toStringOrEmpty

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var codeAndNumberFilled: PhoneResult.CodeAndNumberFilled? = null
    private val viewState = MutableStateFlow(MainViewState.init())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhoneFragment()
        setupPhoneFragment2()
        setupCheckRecreatePhoneView()
        renderState()
    }

    private fun setupPhoneFragment2() {
      //  binding.phoneNumberView2.isFocusAndShowKeyboard = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCheckRecreatePhoneView() {
        binding.button2.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack("SecondFragment")
                replace(R.id.startContainer, SecondFragment(), "SecondFragment")
            }
        }
    }


    private fun setupPhoneFragment() {
        withUnit(binding.phoneNumberView) {
            phoneFlow
                .onEach { validatePhone(it) }
                .launchIn(CoroutineScope(Dispatchers.Main))
        }
    }

    private fun renderState() {
        viewState
            .onEach { state ->
                updateError(
                    state.isErrorInput,
                    state.errorText.toStringOrEmpty()
                )
                binding.button.isEnabled = state.isEnableButton
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

    private fun updateError(isError: Boolean, errorText: String) {
        binding.phoneNumberView.errorText = errorText
        binding.phoneNumberView.isError = isError
    }

    private fun validatePhone(phoneResult: PhoneResult) {
        when (phoneResult) {
            is PhoneResult.CodeAndNumberFilled -> {
                codeAndNumberFilled = phoneResult
                viewState.update(MainViewState::phoneValid)
            }
            is PhoneResult.Invalid -> {
                codeAndNumberFilled = null
                viewState.update { state ->
                    MainViewState.phoneInValid(
                        previous = state,
                        isErrorInput = false,
                        errorText = null
                    )
                }
            }

            is PhoneResult.ErrorCountryCode -> {
                codeAndNumberFilled = null
                viewState.update { state ->
                    MainViewState.phoneInValid(
                        state,
                        isErrorInput = true,
                        errorText = resources.getString(R.string.common_error_code_phone)
                    )
                }
            }

            is PhoneResult.EmptyCode -> {
                codeAndNumberFilled = null
                viewState.update { state ->
                    MainViewState.phoneInValid(
                        state,
                        isErrorInput = true,
                        errorText = resources.getString(R.string.common_error_code_phone)
                    )
                }
            }

            is PhoneResult.NotSet -> {
                codeAndNumberFilled = null
                viewState.update { state ->
                    MainViewState.notSetPhone(
                        state
                    )
                }
            }
        }
    }
}