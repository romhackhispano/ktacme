package com.romhackhispano.ktacme.serialization

import java.io.StringReader
import java.io.StringWriter
import java.util.*

object Props {
    fun stringify(obj: Any): String = serialize(obj)

    fun serialize(obj: Any): String {
        val props = Properties()
        for ((key, value) in Reflect.fromTyped(obj)) {
            props.put(key, value)
        }
        val sw = StringWriter()
        props.store(sw, "")
        return sw.toString()
    }

    inline fun <reified T : Any> parseTyped(str: String): T = deserializeTyped<T>(str)

    inline fun <reified T : Any> deserializeTyped(str: String): T {
        val props = Properties()
        props.load(StringReader(str))
        val map = props.propertyNames().toList().map { "$it" to props[it] }.toMap()
        return Reflect.toTyped(T::class.java, map)
    }
}