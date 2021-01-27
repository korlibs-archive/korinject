package com.soywiz.korinject.internal

import kotlin.reflect.*

internal fun KType.kclass() = when (val t = classifier) {
    is KClass<*> -> t
    else -> error("Only KClass supported as classifier, got $t")
} as KClass<Any>
