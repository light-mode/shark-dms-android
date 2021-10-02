package vn.sharkdms.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
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
import vn.sharkdms.CustomerActivity
import vn.sharkdms.R
import vn.sharkdms.SaleActivity
import vn.sharkdms.SharedViewModel
import vn.sharkdms.api.LoginResponseData
import vn.sharkdms.databinding.FragmentLoginBinding
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.ResponseCode
import vn.sharkdms.util.Validator

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    companion object {
        const val TAG = "LoginFragment"
        private const val CHANGE_USERNAME = Activity.RESULT_FIRST_USER
        private const val CHANGE_PASSWORD = CHANGE_USERNAME + 1
    }

    private val viewModel by viewModels<LoginViewModel>()
    private var connectivity: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        val hideIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_hide)
        val showIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_show)
        val binding = FragmentLoginBinding.bind(view)
        setUsernameEditTextListener(binding, clearIcon)
        setPasswordEditTextListener(binding, hideIcon, showIcon)
        setForgotPasswordTextViewListener(binding)
        setLoginButtonListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvent.OnResponse -> handleLoginResponse(binding,
                        event.code, event.message, event.data)
                    is LoginViewModel.LoginEvent.OnFailure -> handleLoginRequestFailure(binding)
                    is LoginViewModel.LoginEvent.ShowOverviewScreen -> navigateToOverviewScreen(
                        event.token, event.username, event.roleName)
                    is LoginViewModel.LoginEvent.ShowProductsScreen -> navigateToProductsScreen(
                        event.token, event.username, event.roleName)
                }
            }
        }
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUsernameEditTextListener(binding: FragmentLoginBinding, clearIcon: Drawable?) {
        setUsernameEditTextTextChangedListener(binding)
        setUsernameEditTextOnTouchListener(binding, clearIcon)
        setUsernameEditTextFocusChangeListener(binding, clearIcon)
    }

    private fun setUsernameEditTextTextChangedListener(binding: FragmentLoginBinding) {
        binding.editTextUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, CHANGE_USERNAME)
            }
        })
    }

    private fun setUsernameEditTextOnTouchListener(binding: FragmentLoginBinding,
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
                        afterTextChanged(binding, CHANGE_USERNAME)
                        return true
                    }
                })
            }
        }
    }

    private fun setUsernameEditTextFocusChangeListener(binding: FragmentLoginBinding,
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

    private fun setPasswordEditTextListener(binding: FragmentLoginBinding, hideIcon: Drawable?,
        showIcon: Drawable?) {
        setPasswordEditTextTextChangedListener(binding)
        setPasswordEditTextOnTouchListener(binding, hideIcon, showIcon)
        setPasswordEditTextFocusChangeListener(binding, hideIcon, showIcon)
    }

    private fun setPasswordEditTextTextChangedListener(binding: FragmentLoginBinding) {
        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, CHANGE_PASSWORD)
            }
        })
    }

    private fun setPasswordEditTextOnTouchListener(binding: FragmentLoginBinding,
        hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextPassword.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || hideIcon == null || showIcon == null
                            || event.action != MotionEvent.ACTION_UP) return false
                        val currentEndIcon = editTextPassword.compoundDrawablesRelative[2]
                        if (event.rawX < editTextPassword.right - hideIcon.bounds.width() -
                            editTextPassword.paddingEnd * 2 || currentEndIcon == null) return false
                        val start = editTextPassword.selectionStart
                        val end = editTextPassword.selectionEnd
                        val currentPasswordIcon = editTextPassword.compoundDrawablesRelative[0]
                        if (editTextPassword.transformationMethod == null) {
                            editTextPassword.transformationMethod = PasswordTransformationMethod()
                            editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, hideIcon, null)
                        } else {
                            editTextPassword.transformationMethod = null
                            editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, showIcon, null)
                        }
                        editTextPassword.setSelection(start, end)
                        return true
                    }
                })
            }
        }
    }

    private fun setPasswordEditTextFocusChangeListener(binding: FragmentLoginBinding,
        hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextPassword.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentPasswordIcon = editTextPassword.compoundDrawablesRelative[0]
                    if (hasFocus && editTextPassword.text.toString().isNotEmpty()) {
                        editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null,
                            if (editTextPassword.transformationMethod == null) showIcon
                            else hideIcon, null)
                    } else {
                        editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, null, null)
                    }
                }
            }
        }
    }

    private fun afterTextChanged(binding: FragmentLoginBinding, change: Int) {
        val username = binding.editTextUsername.text.toString()
        val password = binding.editTextPassword.text.toString()
        val validUsername = Validator.isValidUsername(username)
        val validPassword = Validator.isValidPassword(password)
        when (change) {
            CHANGE_USERNAME -> {
                updateUsernameView(validUsername, username, binding.editTextUsername)
            }
            CHANGE_PASSWORD -> {
                updatePasswordView(validPassword, password, binding.editTextPassword)
            }
        }
        if (validUsername && validPassword) {
            binding.buttonLogin.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.button_primary)
            }
        } else {
            binding.buttonLogin.apply {
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

    private fun updatePasswordView(validPassword: Boolean, password: String,
        passwordEditText: EditText) {
        val passwordIcon = if (validPassword) R.drawable.ic_password_valid
        else R.drawable.ic_password_invalid
        var endIcon = 0
        if (password.isNotEmpty() && passwordEditText.hasFocus()) {
            endIcon = if (passwordEditText.transformationMethod == null) R.drawable.ic_show else
                R.drawable.ic_hide
        }
        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(passwordIcon, 0, endIcon,
            0)
    }

    private fun setForgotPasswordTextViewListener(binding: FragmentLoginBinding) {
        binding.textViewForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }
    }

    private fun setLoginButtonListener(binding: FragmentLoginBinding) {
        binding.apply {
            buttonLogin.setOnClickListener {
                if (!connectivity) {
                    Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                buttonLogin.isEnabled = false
                buttonLogin.text = ""
                progressBar.visibility = View.VISIBLE
                val username = editTextUsername.text.toString()
                val password = editTextPassword.text.toString()
                viewModel.sendLoginRequest(username, password)
            }
        }
    }

    private fun handleLoginResponse(binding: FragmentLoginBinding, code: String, message: String,
        data: LoginResponseData?) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonLogin.text = getString(R.string.fragment_login_button_login_text)
            buttonLogin.isEnabled = true
        }
        try {
            when (code.toInt()) {
                HttpStatus.OK -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    viewModel.saveUserInfo(data!!, binding.editTextUsername.text.toString())
                }
                HttpStatus.FORBIDDEN, HttpStatus.BAD_REQUEST -> Toast.makeText(requireContext(),
                    message, Toast.LENGTH_SHORT).show()
                else -> Log.e(TAG, code)
            }
        } catch (nfe: NumberFormatException) {
            if (code == ResponseCode.E001) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            } else {
                Log.e(TAG, nfe.message, nfe)
            }
        }
    }

    private fun handleLoginRequestFailure(binding: FragmentLoginBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonLogin.text = getString(R.string.fragment_login_button_login_text)
            buttonLogin.isEnabled = true
        }
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }

    private fun navigateToOverviewScreen(token: String, username: String, roleName: String) {
        val intent = Intent(requireContext(), SaleActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("username", username)
        intent.putExtra("role_name", roleName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToProductsScreen(token: String, username: String, roleName: String) {
        val intent = Intent(requireContext(), CustomerActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("username", username)
        intent.putExtra("role_name", roleName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}