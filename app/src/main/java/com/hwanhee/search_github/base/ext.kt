package com.hwanhee.search_github.base

import androidx.compose.ui.graphics.Color
import com.hwanhee.search_github.model.vo.RequestPage
import java.security.MessageDigest

inline val String.hashColor
    get() = Color.parse("#" + this.sha256()
        .substring(0, 6)
        .toLong(radix = 16)
        .toString(16)
        .padStart(6, '0')
    )

fun Color.Companion.parse(colorString: String): Color {
    return try {
        Color(color = android.graphics.Color.parseColor(colorString))
    }
    catch (e: Exception) {
        Red
    }
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

infix fun Int.lessThan(other: Int)
    = this < other

infix fun Int.lessThanOrEquals(other: Int)
    = this <= other

infix fun RequestPage.lessThan(other: Int)
    = this.page lessThan other

infix fun RequestPage.lessThanOrEquals(other: Int)
    = this.page lessThanOrEquals other
