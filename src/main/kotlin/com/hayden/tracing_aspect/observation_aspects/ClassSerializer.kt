package com.hayden.tracing_aspect.observation_aspects

interface ClassSerializer {

    fun doSerialize(value: Any): String

    fun matches(value: Class<*>): Boolean


}