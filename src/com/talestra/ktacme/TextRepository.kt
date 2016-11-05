package com.talestra.ktacme

open class TextRepository {
	open val entries = arrayListOf<TextRowComponent.TextEntry>()
	//(0 until 1000).map { TextRowComponent.TextEntry("ID_$it", "original", "translated") }
}