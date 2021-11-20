package vn.sharkdms.util

import java.util.regex.Pattern

class Validator {
    companion object {
        private val NAME_PATTERN: Pattern = Pattern.compile(
            "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳ" +
                    "ẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W|_]+",
            Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
        private val USERNAME_PATTERN: Pattern = Pattern.compile("[^\\s]{1,30}")
        private val PASSWORD_PATTERN: Pattern = Pattern.compile("[^\\s]{6,50}")
        private val PHONE_PATTERN: Pattern = Pattern.compile("[^\\s]{10,11}")
        private val EMAIL_PATTERN: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}\$",
            Pattern.CASE_INSENSITIVE)
        private val ADDRESS_PATTERN: Pattern = Pattern.compile(
            "^[0-9a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳ" +
                    "ẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W|_]+",
            Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
        fun isValidName(name: String) = NAME_PATTERN.matcher(name).matches()
        fun isValidUsername(username: String) = USERNAME_PATTERN.matcher(username).matches()
        fun isValidPassword(password: String) = PASSWORD_PATTERN.matcher(password).matches()
        fun isValidEmail(email: String) = EMAIL_PATTERN.matcher(email).matches()
        fun isValidPhone(phone: String) = PHONE_PATTERN.matcher(phone).matches()
        fun isValidAddress(address: String) = ADDRESS_PATTERN.matcher(address).matches()
    }
}