package vn.sharkdms.ui.password.change

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.activity.MainActivity
import vn.sharkdms.R
import vn.sharkdms.activity.SharedViewModel
import vn.sharkdms.databinding.FragmentChangePasswordBinding
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils
import vn.sharkdms.util.Validator

@AndroidEntryPoint
open class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    companion object {
        const val TAG = "ChangePasswordFragment"
        private const val CHANGE_OLD_PASSWORD = Activity.RESULT_FIRST_USER
        private const val CHANGE_NEW_PASSWORD = CHANGE_OLD_PASSWORD + 1
        private const val CHANGE_CONFIRM_PASSWORD = CHANGE_NEW_PASSWORD + 1
    }

    private val viewModel by viewModels<ChangePasswordViewModel>()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hideIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_hide)
        val showIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_show)
        val binding = FragmentChangePasswordBinding.bind(view)
        setBackIconListener(binding)
        setOldPasswordEditTextListener(binding, hideIcon, showIcon)
        setNewPasswordEditTextListener(binding, hideIcon, showIcon)
        setConfirmPasswordEditTextListener(binding, hideIcon, showIcon)
        setConfirmButtonListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.changePasswordEvent.collect { event ->
                when (event) {
                    is ChangePasswordViewModel.ChangePasswordEvent.OnResponse -> {
                        handleChangePasswordResponse(binding, event.code, event.message)
                    }
                    is ChangePasswordViewModel.ChangePasswordEvent.OnFailure -> {
                        handleChangePasswordRequestFailure(binding)
                    }
                    is ChangePasswordViewModel.ChangePasswordEvent.ShowLoginScreen ->
                        navigateToLoginScreen()
                    is ChangePasswordViewModel.ChangePasswordEvent.ShowUnauthorizedDialog -> {
                        Utils.showUnauthorizedDialog(requireActivity())
                    }
                }
            }
        }
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        Utils.setupUI(binding.changePasswordFragment, requireActivity() as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun setBackIconListener(binding: FragmentChangePasswordBinding) {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOldPasswordEditTextListener(binding: FragmentChangePasswordBinding,
                                               hideIcon: Drawable?, showIcon: Drawable?) {
        setOldPasswordEditTextTextChangedListener(binding)
        setOldPasswordEditTextOnTouchListener(binding, hideIcon, showIcon)
        setOldPasswordEditTextFocusChangeListener(binding, hideIcon, showIcon)
    }

    private fun setOldPasswordEditTextTextChangedListener(binding: FragmentChangePasswordBinding) {
        binding.editTextOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, CHANGE_OLD_PASSWORD)
            }
        })
    }

    private fun setOldPasswordEditTextOnTouchListener(binding: FragmentChangePasswordBinding,
                                                      hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextOldPassword.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || hideIcon == null || showIcon == null
                            || event.action != MotionEvent.ACTION_UP) return false
                        val currentEndIcon = editTextOldPassword.compoundDrawablesRelative[2]
                        if (event.rawX < editTextOldPassword.right - hideIcon.bounds.width() -
                            editTextOldPassword.paddingEnd * 2 || currentEndIcon == null) {
                            return false
                        }
                        val start = editTextOldPassword.selectionStart
                        val end = editTextOldPassword.selectionEnd
                        val currentPasswordIcon = editTextOldPassword.compoundDrawablesRelative[0]
                        if (editTextOldPassword.transformationMethod == null) {
                            editTextOldPassword.transformationMethod =
                                PasswordTransformationMethod()
                            editTextOldPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, hideIcon, null)
                        } else {
                            editTextOldPassword.transformationMethod = null
                            editTextOldPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, showIcon, null)
                        }
                        editTextOldPassword.setSelection(start, end)
                        return true
                    }
                })
            }
        }
    }

    private fun setOldPasswordEditTextFocusChangeListener(binding: FragmentChangePasswordBinding,
                                                          hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextOldPassword.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentPasswordIcon = editTextOldPassword.compoundDrawablesRelative[0]
                    if (hasFocus && editTextOldPassword.text.toString().isNotEmpty()) {
                        editTextOldPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null,
                            if (editTextOldPassword.transformationMethod == null) showIcon
                            else hideIcon, null)
                    } else {
                        editTextOldPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, null, null)
                    }
                }
            }
        }
    }

    private fun setNewPasswordEditTextListener(binding: FragmentChangePasswordBinding,
                                               hideIcon: Drawable?, showIcon: Drawable?) {
        setNewPasswordEditTextTextChangedListener(binding)
        setNewPasswordEditTextOnTouchListener(binding, hideIcon, showIcon)
        setNewPasswordEditTextFocusChangeListener(binding, hideIcon, showIcon)
    }

    private fun setNewPasswordEditTextTextChangedListener(binding: FragmentChangePasswordBinding) {
        binding.editTextNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, CHANGE_NEW_PASSWORD)
            }
        })
    }

    private fun setNewPasswordEditTextOnTouchListener(binding: FragmentChangePasswordBinding,
                                                      hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextNewPassword.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || hideIcon == null || showIcon == null
                            || event.action != MotionEvent.ACTION_UP) return false
                        val currentEndIcon = editTextNewPassword.compoundDrawablesRelative[2]
                        if (event.rawX < editTextNewPassword.right - hideIcon.bounds.width() -
                            editTextNewPassword.paddingEnd * 2 || currentEndIcon == null) {
                            return false
                        }
                        val start = editTextNewPassword.selectionStart
                        val end = editTextNewPassword.selectionEnd
                        val currentPasswordIcon = editTextNewPassword.compoundDrawablesRelative[0]
                        if (editTextNewPassword.transformationMethod == null) {
                            editTextNewPassword.transformationMethod =
                                PasswordTransformationMethod()
                            editTextNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, hideIcon, null)
                        } else {
                            editTextNewPassword.transformationMethod = null
                            editTextNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, showIcon, null)
                        }
                        editTextNewPassword.setSelection(start, end)
                        return true
                    }
                })
            }
        }
    }

    private fun setNewPasswordEditTextFocusChangeListener(binding: FragmentChangePasswordBinding,
                                                          hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextNewPassword.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentPasswordIcon = editTextNewPassword.compoundDrawablesRelative[0]
                    if (hasFocus && editTextNewPassword.text.toString().isNotEmpty()) {
                        editTextNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null,
                            if (editTextNewPassword.transformationMethod == null) showIcon
                            else hideIcon, null)
                    } else {
                        editTextNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, null, null)
                    }
                }
            }
        }
    }

    private fun setConfirmPasswordEditTextListener(binding: FragmentChangePasswordBinding,
                                                   hideIcon: Drawable?, showIcon: Drawable?) {
        setConfirmPasswordEditTextTextChangedListener(binding)
        setConfirmPasswordEditTextOnTouchListener(binding, hideIcon, showIcon)
        setConfirmPasswordEditTextFocusChangeListener(binding, hideIcon, showIcon)
    }

    private fun setConfirmPasswordEditTextTextChangedListener(
        binding: FragmentChangePasswordBinding) {
        binding.editTextConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, CHANGE_CONFIRM_PASSWORD)
            }
        })
    }

    private fun setConfirmPasswordEditTextOnTouchListener(binding: FragmentChangePasswordBinding,
                                                          hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextConfirmPassword.apply {
                setOnTouchListener(object : View.OnTouchListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                        if (view == null || event == null || hideIcon == null || showIcon == null
                            || event.action != MotionEvent.ACTION_UP) return false
                        val currentEndIcon = editTextConfirmPassword.compoundDrawablesRelative[2]
                        if (event.rawX < editTextConfirmPassword.right - hideIcon.bounds.width()
                            - editTextConfirmPassword.paddingEnd * 2 || currentEndIcon == null) {
                            return false
                        }
                        val start = editTextConfirmPassword.selectionStart
                        val end = editTextConfirmPassword.selectionEnd
                        val currentPasswordIcon = editTextConfirmPassword
                            .compoundDrawablesRelative[0]
                        if (editTextConfirmPassword.transformationMethod == null) {
                            editTextConfirmPassword.transformationMethod =
                                PasswordTransformationMethod()
                            editTextConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, hideIcon, null)
                        } else {
                            editTextConfirmPassword.transformationMethod = null
                            editTextConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                currentPasswordIcon, null, showIcon, null)
                        }
                        editTextConfirmPassword.setSelection(start, end)
                        return true
                    }
                })
            }
        }
    }

    private fun setConfirmPasswordEditTextFocusChangeListener(
        binding: FragmentChangePasswordBinding, hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            editTextConfirmPassword.apply {
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    val currentPasswordIcon = editTextConfirmPassword.compoundDrawablesRelative[0]
                    if (hasFocus && editTextConfirmPassword.text.toString().isNotEmpty()) {
                        editTextConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null,
                            if (editTextConfirmPassword.transformationMethod == null) showIcon
                            else hideIcon, null)
                    } else {
                        editTextConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, null, null)
                    }
                }
            }
        }
    }

    private fun afterTextChanged(binding: FragmentChangePasswordBinding, change: Int) {
        val oldPassword = binding.editTextOldPassword.text.toString()
        val newPassword = binding.editTextNewPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val oldPasswordTrimmed = oldPassword.trim()
        val newPasswordTrimmed = newPassword.trim()
        val confirmPasswordTrimmed = confirmPassword.trim()
        val validOldPassword = Validator.isValidPassword(oldPasswordTrimmed)
        val validNewPassword = Validator.isValidPassword(
            newPasswordTrimmed) && newPasswordTrimmed != oldPasswordTrimmed
        val validConfirmPassword = Validator.isValidPassword(
            confirmPasswordTrimmed) && confirmPasswordTrimmed == newPasswordTrimmed &&
                confirmPasswordTrimmed != oldPasswordTrimmed
        when (change) {
            CHANGE_OLD_PASSWORD -> {
                updateOldPasswordView(validOldPassword, oldPassword, binding.editTextOldPassword, binding.textViewOldPasswordError)
                updateNewPasswordView(validNewPassword, newPassword, binding.editTextNewPassword, binding.textViewNewPasswordError)
                updateConfirmPasswordView(validConfirmPassword, confirmPassword,
                    binding.editTextConfirmPassword, false, binding.textViewConfirmPasswordError)
            }
            CHANGE_NEW_PASSWORD -> {
                updateNewPasswordView(validNewPassword, newPassword, binding.editTextNewPassword, binding.textViewNewPasswordError)
                updateConfirmPasswordView(validConfirmPassword, confirmPassword,
                    binding.editTextConfirmPassword, false, binding.textViewConfirmPasswordError)
            }
            CHANGE_CONFIRM_PASSWORD -> {
                updateConfirmPasswordView(validConfirmPassword, confirmPassword,
                    binding.editTextConfirmPassword, true, binding.textViewConfirmPasswordError)
            }
        }
        if (validOldPassword && validNewPassword && newPassword == confirmPassword) {
            binding.buttonConfirm.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.button_primary)
            }
        } else {
            binding.buttonConfirm.apply {
                isEnabled = false
                setBackgroundResource(R.drawable.button_disable)
            }
        }
    }

    private fun updateOldPasswordView(validOldPassword: Boolean, oldPassword: String,
                                      oldPasswordEditText: EditText, oldPasswordError: TextView) {
        var passwordIcon = R.drawable.ic_password_invalid
        if (validOldPassword) {
            passwordIcon = R.drawable.ic_password_valid
            oldPasswordError.visibility = View.GONE
        } else {
            oldPasswordError.visibility = View.VISIBLE
        }
        var endIcon = 0
        if (oldPassword.isNotEmpty() && oldPasswordEditText.hasFocus()) {
            endIcon = if (oldPasswordEditText.transformationMethod == null) R.drawable.ic_show
            else R.drawable.ic_hide
        }
        oldPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(passwordIcon, 0,
            endIcon, 0)
    }

    private fun updateNewPasswordView(validNewPassword: Boolean, newPassword: String,
                                      newPasswordEditText: EditText, newPasswordError: TextView) {
        var passwordIcon = R.drawable.ic_password_invalid
        if (validNewPassword) {
            passwordIcon = R.drawable.ic_password_valid
            newPasswordError.visibility = View.GONE
        } else {
            newPasswordError.visibility = View.VISIBLE
        }
        var endIcon = 0
        if (newPassword.isNotEmpty() && newPasswordEditText.hasFocus()) {
            endIcon = if (newPasswordEditText.transformationMethod == null) R.drawable.ic_show
            else R.drawable.ic_hide
        }
        newPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(passwordIcon, 0,
            endIcon, 0)
    }

    private fun updateConfirmPasswordView(validConfirmPassword: Boolean, confirmPassword: String,
                                          confirmPasswordEditText: EditText, changeConfirmPassword: Boolean, confirmPasswordError: TextView) {
        var passwordIcon = R.drawable.ic_password_invalid
        if (validConfirmPassword) {
            passwordIcon = R.drawable.ic_password_valid
            confirmPasswordError.visibility = View.GONE
        } else {
            confirmPasswordError.visibility = View.VISIBLE
        }
        var endIcon = 0
        if (changeConfirmPassword && confirmPassword.isNotEmpty() && confirmPasswordEditText
                .hasFocus()) {
            endIcon = if (confirmPasswordEditText.transformationMethod == null) R.drawable.ic_show
            else R.drawable.ic_hide
        }
        confirmPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(passwordIcon, 0,
            endIcon, 0)
    }

    private fun setConfirmButtonListener(binding: FragmentChangePasswordBinding) {
        binding.apply {
            buttonConfirm.setOnClickListener {
                if (sharedViewModel.connectivity.value != true) {
                    Utils.showConnectivityOffMessage(requireContext())
                    return@setOnClickListener
                }
                buttonConfirm.isEnabled = false
                buttonConfirm.text = ""
                progressBar.visibility = View.VISIBLE
                val oldPassword = editTextOldPassword.text.toString()
                val newPassword = editTextNewPassword.text.toString()
                viewModel.sendChangePasswordRequest(sharedViewModel.token, oldPassword, newPassword)
            }
        }
    }

    private fun handleChangePasswordResponse(binding: FragmentChangePasswordBinding, code: Int,
                                             message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonConfirm.text = getString(R.string.fragment_change_password_button_confirm_text)
            buttonConfirm.isEnabled = true
        }
        when (code) {
            HttpStatus.OK -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.deleteUserInfo()
            }
            HttpStatus.BAD_REQUEST -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                .show()
            else -> Log.e(TAG, code.toString().plus(" ").plus(message))
        }
    }

    private fun handleChangePasswordRequestFailure(binding: FragmentChangePasswordBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            buttonConfirm.text = getString(R.string.fragment_change_password_button_confirm_text)
            buttonConfirm.isEnabled = true
        }
        Utils.showConnectivityOffMessage(requireContext())
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}