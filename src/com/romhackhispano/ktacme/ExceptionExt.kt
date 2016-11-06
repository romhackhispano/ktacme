package com.romhackhispano.ktacme

class InvalidOperationException(msg: String) : RuntimeException(msg)

fun noImpl(msg: String): Nothing = run { throw NotImplementedError(msg) }
fun invalidOp(msg: String): Nothing = run { throw InvalidOperationException(msg) }