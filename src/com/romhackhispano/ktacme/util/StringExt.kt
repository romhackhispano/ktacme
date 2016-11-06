package com.romhackhispano.ktacme.util

fun String.trimEnd(str: String): String {
    if (this.endsWith(str)) {
        return this.substring(0, this.length - str.length)
    } else {
        return this
    }
}