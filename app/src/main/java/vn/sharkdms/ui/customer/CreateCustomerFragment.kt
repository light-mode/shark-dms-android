package vn.sharkdms.ui.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentCreateCustomerBinding
import vn.sharkdms.util.Validator

class CreateCustomerFragment: Fragment(R.layout.fragment_create_customer) {

    companion object {
        const val TAG = "CreateCustomerFragment"
        private const val CHANGE_NAME = Activity.RESULT_FIRST_USER
        private const val CHANGE_USERNAME = CHANGE_NAME + 1
        private const val CHANGE_PASSWORD = CHANGE_USERNAME + 1
        private const val CHANGE_PHONE = CHANGE_PASSWORD + 1
        private const val CHANGE_EMAIL = CHANGE_PHONE + 1
        private const val CHANGE_ADDRESS = CHANGE_EMAIL + 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        val binding = FragmentCreateCustomerBinding.bind(view)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        val hideIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_hide)
        val showIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_show)

        setEditTextListener(binding, clearIcon, hideIcon, showIcon)

    }

    private fun initTextWatcher(binding: FragmentCreateCustomerBinding, change: Int): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, change)
            }
        }
        return textWatcher
    }

    private fun initOnTouchListener(binding: FragmentCreateCustomerBinding, editText: EditText, clearIcon: Drawable?,
            hideIcon: Drawable?, showIcon: Drawable?, isPass: Boolean, change: Int): View.OnTouchListener {
        val onTouchListener = object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if (!isPass) {
                    if (view == null || event == null || clearIcon == null || event.action !=
                        MotionEvent.ACTION_UP) return false
                    val currentClearIcon = editText.compoundDrawablesRelative[2]
                    if (event.rawX < editText.right - clearIcon.bounds.width() -
                        editText.paddingEnd * 2 || currentClearIcon == null) {
                        return false
                    }
                    editText.text.clear()
                    afterTextChanged(binding, change)
                    return true
                } else {
                    if (view == null || event == null || hideIcon == null || showIcon == null
                        || event.action != MotionEvent.ACTION_UP) return false
                    val currentEndIcon = editText.compoundDrawablesRelative[2]
                    if (event.rawX < editText.right - hideIcon.bounds.width() -
                        editText.paddingEnd * 2 || currentEndIcon == null) return false
                    val start = editText.selectionStart
                    val end = editText.selectionEnd
                    val currentPasswordIcon = editText.compoundDrawablesRelative[0]
                    if (editText.transformationMethod == null) {
                        editText.transformationMethod = PasswordTransformationMethod()
                        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, hideIcon, null)
                    } else {
                        editText.transformationMethod = null
                        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, showIcon, null)
                    }
                    editText.setSelection(start, end)
                    return true
                }
            }
        }
        return onTouchListener
    }

    private fun setEditTextListener(binding: FragmentCreateCustomerBinding, clearIcon: Drawable?,
            hideIcon: Drawable?, showIcon: Drawable?) {
        setEditTextTextChangedListener(binding)
        setEditTextOnTouchListener(binding, clearIcon, hideIcon, showIcon)
    }

    private fun setEditTextTextChangedListener(binding: FragmentCreateCustomerBinding) {
        binding.apply {
            etCreateCustomerName.addTextChangedListener(initTextWatcher(binding, CHANGE_NAME))
            etCreateCustomerAccount.addTextChangedListener(initTextWatcher(binding, CHANGE_USERNAME))
            etCreateCustomerPassword.addTextChangedListener(initTextWatcher(binding, CHANGE_PASSWORD))
            etCreateCustomerPhone.addTextChangedListener(initTextWatcher(binding, CHANGE_PHONE))
            etCreateCustomerEmail.addTextChangedListener(initTextWatcher(binding, CHANGE_EMAIL))
            etCreateCustomerAddress.addTextChangedListener(initTextWatcher(binding, CHANGE_ADDRESS))
        }
    }

    private fun setEditTextOnTouchListener(binding: FragmentCreateCustomerBinding,
            clearIcon: Drawable?, hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            etCreateCustomerName.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerName,
                    clearIcon, hideIcon, showIcon, false, CHANGE_NAME))
            }
            etCreateCustomerAccount.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerAccount,
                    clearIcon, hideIcon, showIcon, false, CHANGE_USERNAME))
            }
            etCreateCustomerPassword.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerPassword,
                    clearIcon, hideIcon, showIcon, true, CHANGE_PASSWORD))
            }
            etCreateCustomerPhone.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerPhone,
                    clearIcon, hideIcon, showIcon, false, CHANGE_PHONE))
            }
            etCreateCustomerEmail.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerEmail,
                    clearIcon, hideIcon, showIcon, false, CHANGE_EMAIL))
            }
            etCreateCustomerAddress.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerAddress,
                    clearIcon, hideIcon, showIcon, false, CHANGE_ADDRESS))
            }
        }
    }

    private fun afterTextChanged(binding: FragmentCreateCustomerBinding, change: Int) {
        val name = binding.etCreateCustomerName.text.toString()
        val username = binding.etCreateCustomerAccount.text.toString()
        val password = binding.etCreateCustomerPassword.text.toString()
        val phone = binding.etCreateCustomerPhone.text.toString()
        val email = binding.etCreateCustomerEmail.text.toString()
        val address = binding.etCreateCustomerAddress.text.toString()
        val validName = Validator.isValidUsername(name)
        val validUsername = Validator.isValidUsername(username)
        val validPassword = Validator.isValidPassword(password)
        val validPhone = Validator.isValidUsername(phone)
        var validEmail: Boolean = false
        if (!email.isEmpty()) validEmail = Validator.isValidEmail(email)
        val validAddress = Validator.isValidUsername(address)
        when (change) {
            CHANGE_NAME -> {
                val clearIcon = if (name.isNotEmpty() && binding.etCreateCustomerName.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
            }
            CHANGE_USERNAME -> {
                val clearIcon = if (username.isNotEmpty() && binding.etCreateCustomerAccount.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerAccount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
            }
            CHANGE_PASSWORD -> {
                var endIcon = 0
                if (password.isNotEmpty() && binding.etCreateCustomerPassword.hasFocus()) {
                    endIcon = if (binding.etCreateCustomerPassword.transformationMethod == null) R.drawable.ic_show else
                        R.drawable.ic_hide
                }
                binding.etCreateCustomerPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, endIcon,
                    0)
            }
            CHANGE_PHONE -> {
                val clearIcon = if (phone.isNotEmpty() && binding.etCreateCustomerPhone.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerPhone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
            }
            CHANGE_EMAIL -> {
                val clearIcon = if (email.isNotEmpty() && binding.etCreateCustomerEmail.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
            }
            CHANGE_ADDRESS -> {
                val clearIcon = if (address.isNotEmpty() && binding.etCreateCustomerAddress.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
            }
        }
        // Need change to capture email case
        if (validAddress && validName && validPassword && validPhone && validUsername) {
            binding.btnCreateCustomer.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.button_primary)
            }
        } else {
            binding.btnCreateCustomer.apply {
                isEnabled = false
                setBackgroundResource(R.drawable.button_disable)
            }
        }
    }


}