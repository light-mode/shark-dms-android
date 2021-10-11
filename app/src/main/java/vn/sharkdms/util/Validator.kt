package vn.sharkdms.util

import java.util.regex.Pattern

class Validator {
    companion object {
        private val PASSWORD_PATTERN: Pattern = Pattern.compile("[^\\s]{6,}")
        private val PHONE_PATTERN: Pattern = Pattern.compile("[^\\s]{10,11}")
        private val EMAIL_PATTERN: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}\$", Pattern.CASE_INSENSITIVE)
        fun isValidUsername(username: String): Boolean = username.trim().isNotEmpty()
        fun isValidPassword(password: String) = PASSWORD_PATTERN.matcher(password).matches()
        fun isValidEmail(email: String) = EMAIL_PATTERN.matcher(email).matches()
        fun isValidPhone(phone: String) = PHONE_PATTERN.matcher(phone).matches()
    }
}