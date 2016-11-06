package com.romhackhispano.ktacme.serialization

import com.romhackhispano.ktacme.invalidOp
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object Reflect {
    private enum class AnyEnum {}

    @Suppress("UNCHECKED_CAST")
    fun <T> createEmptyClass(clazz: Class<T>): T {
        if (clazz == java.util.List::class.java) return listOf<Any?>() as T
        if (clazz == java.util.Map::class.java) return mapOf<Any?, Any?>() as T
        if (clazz == java.lang.Iterable::class.java) return listOf<Any?>() as T

        val constructor = clazz.declaredConstructors.firstOrNull()
        if (constructor != null) {
            constructor.isAccessible = true
            val args = constructor.parameterTypes.map {
                dynamicCast(null, it)
            }
            return constructor.newInstance(*args.toTypedArray()) as T
        } else {
            invalidOp("Don't know how to create a dummy instance of class $clazz")
        }
    }

    fun getAllFields(type: Class<*>): List<Field> {
        val classFields = type.declaredFields.toList()
        return if (type.superclass != null) {
            classFields.plus(getAllFields(type.superclass))
        } else classFields
    }

    inline fun <reified T : Any> dynamicCast(value: Any?): T? = dynamicCast(value, T::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> dynamicCast(value: Any?, target: Class<T>, genericType: Type? = null): T? {
        if (value != null && target.isAssignableFrom(value.javaClass)) {
            return if (genericType != null && genericType is ParameterizedType) {
                val typeArgs = genericType.actualTypeArguments
                when (value) {
                    is List<*> -> value.map { dynamicCast(it, typeArgs[0] as Class<Any>) }
                    is Map<*, *> ->
                        //dynamicCast()
                        value.entries.associate {
                            Pair(
                                    dynamicCast(it.key, typeArgs[0] as Class<Any>),
                                    dynamicCast(it.value, typeArgs[1] as Class<Any>)
                            )
                        }
                    else -> value
                } as T
            } else {
                value as T
            }
        }
        val str = if (value != null) "$value" else "0"
        if (target.isPrimitive) {
            when (target) {
                java.lang.Boolean.TYPE -> return (str == "true" || str == "1") as T
                java.lang.Byte.TYPE -> return str.parseInt().toByte() as T
                Character.TYPE -> return str.parseInt().toChar() as T
                java.lang.Short.TYPE -> return str.parseInt().toShort() as T
                java.lang.Long.TYPE -> return str.parseLong() as T
                java.lang.Float.TYPE -> return str.toFloat() as T
                java.lang.Double.TYPE -> return str.parseDouble() as T
                Integer.TYPE -> return str.parseInt() as T
                else -> invalidOp("Unhandled primitive '${target.name}'")
            }
        }
        if (target.isAssignableFrom(java.lang.Boolean::class.java)) return (str == "true" || str == "1") as T
        if (target.isAssignableFrom(java.lang.Byte::class.java)) return str.parseInt().toByte() as T
        if (target.isAssignableFrom(Character::class.java)) return str.parseInt().toChar() as T
        if (target.isAssignableFrom(java.lang.Short::class.java)) return str.parseShort() as T
        if (target.isAssignableFrom(Integer::class.java)) return str.parseInt() as T
        if (target.isAssignableFrom(java.lang.Long::class.java)) return str.parseLong() as T
        if (target.isAssignableFrom(java.lang.Float::class.java)) return str.toFloat() as T
        if (target.isAssignableFrom(java.lang.Double::class.java)) return str.toDouble() as T
        if (target.isAssignableFrom(java.lang.String::class.java)) {
            return (if (value == null) "" else str) as T
        }
        if (target.isEnum) {
            if (value == null) return (target.getMethod("values").invoke(null) as Array<Any?>)[0] as T
            return java.lang.Enum.valueOf<AnyEnum>(target as Class<AnyEnum>, str) as T
        }
        if (value is List<*>) {
            return value.toList() as T
        }
        if (value is Map<*, *>) {
            val map = value as Map<Any?, *>
            val resultClass = target as Class<Any>
            val result = createEmptyClass(resultClass)
            for (field in getAllFields(resultClass)) {
                if (field.name in map) {
                    val value = map[field.name]
                    field.isAccessible = true
                    field.set(result, dynamicCast(value, field.type, field.genericType))
                }
            }
            return result as T
        }
        //println("$value, $target")
        //CustomSerializer.list.
        if (value == null) return createEmptyClass(target)

        invalidOp("Can't convert '$value' to '$target'")
    }

    fun String?.parseInt(): Int = this?.parseDouble()?.toInt() ?: 0
    fun String?.parseShort(): Short = this?.parseDouble()?.toShort() ?: 0
    fun String?.parseLong(): Long = try { this?.toLong() } catch (e: Throwable) { this?.parseDouble()?.toLong() } ?: 0
    fun String?.parseDouble(): Double = if (this == null) 0.0 else try { this.toDouble() } catch (e: Throwable) { 0.0 }

    fun <T : Any> toTyped(clazz: Class<T>, map: Map<String, Any?>): T {
        return Reflect.dynamicCast(map, clazz) as T
    }

    fun <T : Any> fromTyped(value: T): Map<String, Any?> {
        val clazz = value.javaClass
        val out = hashMapOf<String, Any?>()
        for (field in Reflect.getAllFields(clazz)) {
            if (field.name.startsWith('$')) continue
            if (Modifier.isTransient(field.modifiers)) continue
            field.isAccessible = true
            out[field.name] = field.get(value)
        }
        return out
    }
}

fun Field.get2(obj: Any?): Any? = this.apply { isAccessible = true }.get(obj)
