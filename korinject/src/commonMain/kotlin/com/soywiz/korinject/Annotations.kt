package com.soywiz.korinject

import kotlinx.serialization.*

@Target(AnnotationTarget.CLASS)
@SerialInfo
annotation class Prototype

@Target(AnnotationTarget.CLASS)
@SerialInfo
annotation class Singleton

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
//@SerialInfo
@Deprecated("Not used")
annotation class Optional
