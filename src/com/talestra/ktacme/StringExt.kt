package com.talestra.ktacme

fun String.quoteAsCString() = "\"${this.escapeAsCString()}\""

fun String.escapeAsCString(): String {
	var out = ""
	for (c in this) {
		when (c) {
			'\n' -> out += "\\n"
			'\t' -> out += "\\t"
			'\"' -> out += "\\\""
			'\'' -> out += "\\\'"
			else -> out += c
		}
	}
	return out
}