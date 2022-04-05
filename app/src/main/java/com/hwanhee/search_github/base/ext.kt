package com.hwanhee.search_github.base

fun Int.MB() : Long {
    return (this * 1024 * 1024).toLong()
}

inline val Int.MegaBytes: Long get() = this.MB()

fun Long.MB() : Long {
    return (this * 1024 * 1024)
}

inline val Long.MegaBytes: Long get() = this.MB()