package vn.sharkdms.ui.forgotpassword

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentForgotPasswordBinding
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Validator

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    companion object {
        const val TAG = "ForgotPasswordFragment"
    }

    private val viewModel by viewModels<ForgotPasswordViewModel>()
    private var connectivity: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        val binding = FragmentForgotPasswordBinding.bind(view)
        setBackIconListener(binding)
        setUsernameEditTextListener(binding, clearIcon)
        setSendButtonListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordEvent.collect { event ->
                when (event) {
                    is ForgotPasswordViewModel.ForgotPasswordEvent.OnResponse -> {
                        handleForgotPasswordResponse(binding, event.code, event.message)
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.OnFailure -> {
                        handleForgotPasswordRequestFailure(binding)
                    }
                }
            }
        }
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }
    }

    private fun setBackIconListener(binding: FragmentForgotPasswordBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUsernameEditTextListener(binding: FragmentForgotPasswordBinding,
        clearIcon: Drawable?) {
        setUsernameEditTextTextChangedListener(binding)
        setUsernameEditTextOnTouchListener(binding, clearIcon)
        setUsernameEditTextFocusChangeListener(binding, clearIcon)
    }

    private fun setUsernameEditTextTextChangedListener(binding: FragmentForgotPasswordBinding) {
        binding.editTextUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding)
            }
        })
    }

    private fun setUsernameEditTextOnTouchListener(binding: FragmentForgotPasswordBinding,
        clearIcon: Drawable?) {
        binding.apply {
            editTextUsername.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || clearIcon == null || event.action !=
                            MotionEvent.ACTION_UP) return false
                        val currentClearIcon = editTextUsername.compoundDrawablesRelative[2]
                        if (event.rawX < editTextUsername.right - clearIcon.bounds.width() -
                            editTextUsername.paddingEnd * 2 || currentClearIcon == null) {
                            return false
                        }
                        editTextUsername.text.clear()
                        afterTextChanged(binding)
                        return true
                    }
                })
            }
        }
    }

    private fun setUsernameEditTextFocusChangeListener(binding: FragmentForgotPasswordBinding,
        clearIcon: Drawable?) {
        binding.apply {
            editTextUsername.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentUsernameIcon = editTextUsername.compoundDrawablesRelative[0]
                    editTextUsername.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        currentUsernameIcon, null,
                        if (hasFocus && editTextUsername.text.toString().isNotEmpty()) clearIcon
                        else null, null)
                }
            }
        }
    }

    private fun afterTextChanged(binding: FragmentForgotPasswordBinding) {
        val username = binding.editTextUsername.text.toString()
        val validUsername = Validator.isValidUsername(username)
        updateUsernameView(validUsername, username, binding.editTextUsername)
        binding.buttonSend.apply {
            if (validUsername) {
                isEnabled = true
                setBackgroundResource(R.drawable.button_primary)
            } else {
                isEnabled = false
                setBackgroundResource(R.drawable.button_disable)
            }
        }
    }

    private fun updateUsernameView(validUsername: Boolean, username: String,
        usernameEditText: EditText) {
        val usernameIcon = if (validUsername) R.drawable.ic_username_valid else R.drawable
            .ic_username_invalid
        val clearIcon = if (username.isNotEmpty() && usernameEditText.hasFocus()) R.drawable
            .ic_clear else 0
        usernameEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(usernameIcon, 0, clearIcon,
            0)
    }

    private fun setSendButtonListener(binding: FragmentForgotPasswordBinding) {
        binding.apply {
            buttonSend.setOnClickListener {
                if (!connectivity) {
                    Toast.makeText(requireContext(),
                        getString(R.string.message_connectivity_off), Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                buttonSend.isEnabled = false
                buttonSend.text = ""
                progressBar.visibility = View.VISIBLE
                val username = binding.editTextUsername.text.toString()
                viewModel.sendForgotPasswordRequest(username)
            }
        }
    }

    private fun handleForgotPasswordResponse(binding: FragmentForgotPasswordBinding, code: Int,
        message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonSend.text = getString(R.string.fragment_forgot_password_button_send_text)
            buttonSend.isEnabled = true
        }
        when (code) {
            HttpStatus.OK -> Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(requireContext(),
                message, Toast.LENGTH_SHORT).show()
            else -> Log.e(TAG, code.toString())
        }
    }

    private fun handleForgotPasswordRequestFailure(binding: FragmentForgotPasswordBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonSend.text = getString(R.string.fragment_forgot_password_button_send_text)
            buttonSend.isEnabled = true
        }
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }
}