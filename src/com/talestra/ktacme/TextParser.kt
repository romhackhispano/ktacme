package com.talestra.ktacme

class TextParser(val text: String) {
	var pos = 0

	val length: Int get() = text.length
	val eof: Boolean get() = pos >= text.length
	val hasMore: Boolean get() = !eof
	val pending: Int get() = length - pos

	private fun clamp(v: Int, min: Int, max: Int) = Math.min(max, Math.max(v, min))

	fun slice(start: Int, end: Int) = this.text.substring(clamp(start, 0, this.length), clamp(end, 0, this.length))

	fun substr(start: Int, count: Int) = this.slice(start, start + count)

	fun readPending() = read(pending)

	fun read(count: Int): String {
		val out = this.substr(pos, count)
		pos += count
		return out
	}

	fun readch() = text[pos++]
	fun peekch() = text[pos]
	fun skip(count: Int) {
		pos += count
	}

	fun unread(count: Int) {
		this.pos -= count
	}

	fun expect(ch: Char) {
		val r = readch()
		if (r != ch) invalidOp("Expected '$ch' but found '$r'")
	}

	fun expect(str: String) {
		val r = read(str.length)
		if (r != str) invalidOp("Expected '$str' but found '$r'")
	}

	fun readUntil(ch: Char): String {
		val start = pos
		while (hasMore) {
			if (readch() == ch) {
				unread(1)
				break
			}
		}
		val end = pos
		return this.text.substring(start, end)
	}

	fun readQuotedString(): String {
		var out = ""
		expect('"')
		while (hasMore) {
			val ch = readch()
			if (ch == '"') {
				unread(1)
				break
			}
			if (ch == '\\') {
				val ch2 = readch()
				when (ch2) {
					'n' -> out += '\n'
					't' -> out += '\t'
					'r' -> out += '\r'
					else -> out += ch2
				}
			} else {
				out += ch
			}
		}
		expect('"')
		return out
	}
}