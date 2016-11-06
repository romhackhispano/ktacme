package com.romhackhispano.ktacme.serialization

import com.romhackhispano.ktacme.invalidOp
import java.lang.reflect.Field
import java.lang.reflect.Modifier

val <T> Class<T>.allDeclaredFields: List<Field> get() {
    return this.declaredFields.toList() + (this.superclass?.allDeclaredFields ?: listOf())
}

object JSON {
    fun parse(str: String): Map<String, Any?> = if (str.isEmpty()) hashMapOf() else Reader(MiniStrReader(str)).readObject()
    //fun stringify(obj: JsonObject): String = obj.toJsonString()

    inline fun <reified T : Any> parseTyped(str: String): T = JSON.toTyped<T>(parse(str))

    fun stringify(value: Any?): String {
        return when (value) {
            null -> "null"
            Unit -> "null"
            true -> "true"
            false -> "false"
            is Byte, is Char, is Short, is Int, is Long, is Double, is Float -> "$value"
            is String -> '"' + JSON.escape(value) + '"'
            is Map<*, *> -> "{" + value.entries.map { JSON.stringify(it.key?.toString()) + ":" + JSON.stringify(it.value) }.joinToString(",") + "}"
            is Iterable<*> -> "[" + value.map { JSON.stringify(it) }.joinToString(",") + "]"
            is Enum<*> -> JSON.stringify(value.name)
            is JsonSerializable -> value.toJson()
            else -> JSON.stringify(value.javaClass.allDeclaredFields.filter { !Modifier.isTransient(it.modifiers) }.map { it.name to it.get2(value) }.toMap())
        }
    }

    private fun escape(str: String): String {
        var out = ""
        for (c in str) {
            out += when (c) {
                '\"' -> "\\\""
                '\'' -> "\\\'"
                '\\' -> "\\\\"
                '/' -> "\\/"
                '\b' -> "\\b"
                '\u21A1' -> "\\f"
                '\n' -> "\\n"
                '\r' -> "\\r"
                '\t' -> "\\t"
                else -> "$c"
            }
        }
        return out
    }


    inline fun <reified T : Any> toTyped(value: Map<String, Any?>): T = toTyped(T::class.java, value)

    fun <T : Any> toTyped(clazz: Class<T>, json: Map<String, Any?>): T = Reflect.toTyped(clazz, json)

    fun <T : Any> fromTyped(value: T): Map<String, Any?> = Reflect.fromTyped(value)

    // http://www.json.org/
    private class Reader(val r: MiniStrReader) {
        fun readAny(): Any? {
            r.skipSpaces()
            val result = when (r.peekChar()) {
                '"' -> readString()
                '[' -> readArray()
                '{' -> readObject()
                '-', in '0'..'9' -> readNumber()
                't', 'f' -> readBool()
                'n' -> readNull()
                else -> invalidOp("Unexpected ${r.peekChar()} reading json at ${r.pos}")
            }
            r.skipSpaces()
            return result
        }

        companion object {
            private val NUMBER_SET = "eE+-.0123456789".toSet()
        }

        fun readNumber(): Number {
            r.skipSpaces()
            val str = r.readWhile { it in NUMBER_SET } ?: "0"
            val out: Number = if (str.contains('.') || str.contains('e')) str.toDouble() else str.toLong()
            r.skipSpaces()
            return out
        }

        fun readBool(): Boolean {
            return when (r.peekChar()) {
                'f' -> {
                    r.readExpect("false")
                    r.skipSpaces()
                    false
                }
                't' -> {
                    r.readExpect("true")
                    r.skipSpaces()
                    true
                }
                else -> invalidOp("Expected boolean")
            }
        }

        fun readNull(): Any? {
            r.skipSpaces()
            r.readExpect("null")
            r.skipSpaces()
            return null
        }

        fun readArray(): List<Any?> {
            r.skipSpaces()
            r.expect('[')
            r.skipSpaces()
            val out = arrayListOf<Any?>()
            while (r.peekChar() != ']') {
                r.skipSpaces()
                val value = readAny()
                r.skipSpaces()
                out.add(value)
                if (r.peekChar() == ']') break
                r.skipSpaces()
                r.expect(',')
                r.skipSpaces()
            }
            r.skipSpaces()
            r.expect(']')
            r.skipSpaces()
            return out
        }

        fun readObject(): Map<String, Any?> {
            r.skipSpaces()
            r.expect('{')
            r.skipSpaces()
            val out = hashMapOf<String, Any?>()
            while (r.peekChar() != '}') {
                val key = readAny().toString()
                r.skipSpaces()
                r.expect(':')
                r.skipSpaces()
                val value = readAny()
                out.put(key, value)
                if (r.peekChar() == '}') break
                r.expect(',')
                r.skipSpaces()
            }
            r.skipSpaces()
            r.expect('}')
            r.skipSpaces()
            return out
        }

        fun readString(): String {
            var out = "";
            r.skipSpaces()
            r.expect('"')
            loop@while (true) {
                val ch = r.readChar()
                when (ch) {
                    '"' -> break@loop
                    '\\' -> {
                        when (r.readChar()) {
                            '"' -> out += '\"'
                            '\\' -> out += '\\'
                            '/' -> out += '/'
                            'b' -> out += '\b'
                        // https://en.wikipedia.org/wiki/Page_break
                        //'f' -> out += '\f' // @TODO: Kotlin should support this?
                            'f' -> out += '\u000c'
                            'n' -> out += '\n'
                            'r' -> out += '\r'
                            't' -> out += '\t'
                        // Unicode
                            'u' -> out += Integer.parseInt(r.read(4), 16).toChar()
                            else -> invalidOp("Invalid JSON escape character")
                        }
                    }
                    else -> out += ch
                }
            }
            r.skipSpaces()
            return out
        }
    }

    private class MiniStrReader(val str: String, val file: String = "file", var pos: Int = 0) {
        val length: Int = this.str.length
        val hasMore: Boolean get() = (this.pos < this.str.length)

        inline fun slice(action: () -> Unit): String? {
            val start = this.pos
            action()
            val end = this.pos
            return if (end > start) this.slice(start, end) else null
        }

        fun slice(start: Int, end: Int): String = this.str.substring(start, end)

        fun peek(count: Int): String = substr(this.pos, count)

        fun peekChar(): Char = this.str[this.pos]

        fun read(count: Int): String {
            val out = this.peek(count)
            this.skip(count)
            return out
        }

        inline fun skipWhile(filter: (Char) -> Boolean) {
            while (hasMore && filter(this.peekChar())) this.readChar()
        }

        inline fun readWhile(filter: (Char) -> Boolean) = this.slice { skipWhile(filter) }

        fun readChar(): Char = this.str[this.pos++]

        fun readExpect(expected: String): String {
            val readed = this.read(expected.length)
            if (readed != expected) invalidOp("Expected '$expected' but found '$readed' at $pos")
            return readed
        }

        fun expect(expected: Char) = readExpect("$expected")

        fun skip(count: Int) = this.apply { this.pos += count; }

        private fun substr(pos: Int, length: Int): String {
            return this.str.substring(pos, Math.min(pos + length, this.length))
        }

        fun skipSpaces(): MiniStrReader {
            this.skipWhile { Character.isWhitespace(it) }
            return this
        }
    }
}

interface JsonSerializable {
    fun toJson(): String
}

fun Any?.toJson() = JSON.stringify(this)

