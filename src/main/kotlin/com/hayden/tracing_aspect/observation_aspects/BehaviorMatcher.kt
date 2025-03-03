package com.hayden.tracing_aspect.observation_aspects

import org.aspectj.lang.JoinPoint
import java.lang.reflect.Field

interface BehaviorMatcher {

    class DefaultBehaviorMatcher: BehaviorMatcher {
        override fun matches(joinPoint: JoinPoint): Boolean {
            return true
        }

        override fun matches(field: Field): Boolean {
            return true;
        }

        override fun matches(value: Any): Boolean {
            return when(value) {
                String -> true
                Int -> true
                Long -> true
                else -> false
            }
        }
    }

    fun matches(joinPoint: JoinPoint): Boolean

    fun matches(field: Field): Boolean

    fun matches(value: Any): Boolean

}