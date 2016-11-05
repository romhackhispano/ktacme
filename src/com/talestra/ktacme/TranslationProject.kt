package com.talestra.ktacme

import java.io.File
import java.util.*

open class TranslationProject {
	open val sections: ArrayList<TranslationSection> = arrayListOf()

	val formats = listOf(AcmeSectionFileFormat(), SimpleSectionFileFormat(), PoFileFormat())

	constructor(folder: File) {
		for (file in folder.listFiles().filter { it.isFile }) {
			val section = object : TranslationSection(file.name) {
				var format: SectionFileFormat? = null
				override fun load() {
					val text = file.readText()
					texts.clear()
					format = formats.firstOrNull() { it.check(text) }
					format?.load(text, texts)
				}

				override fun save() {
					val acme = format ?: AcmeSectionFileFormat()
					file.writeText(acme.save(texts))
				}
			}
			sections += section
		}
	}
}

open class SectionFileFormat {
	open fun check(data: String): Boolean {
		try {
			load(data, arrayListOf())
			return true
		} catch (e: Throwable) {
			return false
		}
	}

	open fun load(data: String, entries: ArrayList<TextRowComponent.TextEntry>) {
		noImpl("Not implemented")
	}

	open fun save(entries: List<TextRowComponent.TextEntry>): String {
		noImpl("Not implemented")
	}
}

class PoFileFormat : SectionFileFormat() {

}

class AcmeSectionFileFormat : SectionFileFormat() {
}

class SimpleSectionFileFormat : SectionFileFormat() {
	override fun load(data: String, entries: ArrayList<TextRowComponent.TextEntry>) {
		for (line in data.lines()) {
			val parser = TextParser(line)
			val id = parser.readUntil(':')
			parser.expect(':')
			val original = parser.readQuotedString()
			parser.expect(':')
			val translated = parser.readQuotedString()

			entries += TextRowComponent.TextEntry(id, original, translated)
		}
	}

	override fun save(entries: List<TextRowComponent.TextEntry>): String {
		val lines = arrayListOf<String>()
		for (entry in entries) {
			lines += "${entry.id}:${entry.original.quoteAsCString()}:${entry.translated.quoteAsCString()}"
		}
		return lines.joinToString("\n")
	}
}

open class TranslationSection(val name: String) {
	open val texts = arrayListOf<TextRowComponent.TextEntry>()

	open fun load() {

	}

	open fun save() {
	}

	open val repository: TextRepository by lazy {
		load()
		object : TextRepository() {
			override val entries: ArrayList<TextRowComponent.TextEntry> = texts
		}
	}

	override fun toString(): String = name
}