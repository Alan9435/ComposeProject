package com.example.composeproject.extension

fun <T> List<T>.safeGetData(index: Int): T? {
    return if (index >= 0 && index < this.size) {
        this[index]
    } else {
        null
    }
}