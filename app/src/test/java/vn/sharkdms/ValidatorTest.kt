package vn.sharkdms

import com.google.common.truth.Truth
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import vn.sharkdms.util.Validator

class ValidatorTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // no character
            " ", // only space character
        ]
    )
    fun invalidName_returnFalse(name: String) {
        val result = Validator.isValidName(name)
        Truth.assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "John", // simple
            "John Doe", // contain white space character
            "X Æ A-Xii", // contain special character
        ]
    )
    fun validName_returnTrue(name: String) {
        val result = Validator.isValidName(name)
        Truth.assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // no character
            " ", // only space character
            "01", // less than min character (3)
            "John Doe", // contain white space character
            "0123456789012345678901234567890", // more than max character (30)
        ]
    )
    fun invalidUsername_returnFalse(username: String) {
        val result = Validator.isValidUsername(username)
        Truth.assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "012", // has min character (3)
            "John", // simple
            "012345678901234567890123456789", // has max character (30)
        ]
    )
    fun validUsername_returnTrue(username: String) {
        val result = Validator.isValidUsername(username)
        Truth.assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // no character
            " ", // only space character
            "01234", // less than min character (6)
            "John Doe", // contain white space character
            "012345678901234567890123456789012345678901234567891", // more than max character (50)
        ]
    )
    fun invalidPassword_returnFalse(password: String) {
        val result = Validator.isValidPassword(password)
        Truth.assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "012345", // has min character (6)
            "password", // simple
            "V7gzWt.Szms-xkN", // complex
            "01234567890123456789012345678901234567890123456789", // has max character (50)
        ]
    )
    fun validPassword_returnTrue(password: String) {
        val result = Validator.isValidPassword(password)
        Truth.assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // no character
            " ", // only space character
            "Abc.example.com", // no at character
            "A@b@c@example.com", // more at outside quotation marks
            "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", // special character outside quotation marks
            "i_like_underscore@but_its_not_allowed_in_this_part.example.com", // underscore in domain part
        ]
    )
    fun invalidEmail_returnFalse(email: String) {
        val result = Validator.isValidEmail(email)
        Truth.assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "simple@example.com", // simple
            "very.common@example.com", // very common
            "disposable.style.email.with+symbol@example.com", // contain plus symbol
            "other.email-with-hyphen@example.com", // contain hyphen symbol
        ]
    )
    fun validEmail_returnTrue(email: String) {
        val result = Validator.isValidEmail(email)
        Truth.assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // no character
            " ", // only space character
            "012345678", // less than min character (10)
            "O123456789", // contain non-numeric character
            "012345678901", // more than max character (11)
        ]
    )
    fun invalidPhone_returnFalse(phone: String) {
        val result = Validator.isValidPhone(phone)
        Truth.assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "0123456789", // has min character (10)
            "01234567890", // has max character (11)
        ]
    )
    fun validPhone_returnTrue(phone: String) {
        val result = Validator.isValidPhone(phone)
        Truth.assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // no character
            " ", // only space character
        ]
    )
    fun invalidAddress_returnFalse(address: String) {
        val result = Validator.isValidAddress(address)
        Truth.assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "0", // simple
            "Đất Thổ Cư Hòa Lạc, Km29 Đường Cao Tốc 08, Thạch Hoà, Thạch Thất, Hà Nội 10000", // complex
        ]
    )
    fun validAddress_returnTrue(address: String) {
        val result = Validator.isValidAddress(address)
        Truth.assertThat(result).isTrue()
    }
}