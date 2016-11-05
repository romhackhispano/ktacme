package com.talestra.ktacme

class TextRepository {
	val entries = (0 until 1000).map { TextRowComponent.TextEntry("ID_$it", "original", "translated") }
}