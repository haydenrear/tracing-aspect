package com.hayden.tracing_aspect.observation_aspects

import com.hayden.tracing_aspect.model.Trace
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
open class DiObservationUtility(
    val arguments: List<BehaviorDataExtractor>,
    val consumer: List<MessageCapture>,
    val serializers: List<ClassSerializer>,
    val matcher:  List<BehaviorMatcher>
) : ObservationUtility<ObservationUtility.JoinPointObservationArgs> {

    val serializersCache: MutableMap<KClass<*>, ClassSerializer> = mutableMapOf()

    override fun consumer(argumentExtractor: ObservationUtility.JoinPointObservationArgs, trace: Trace) {
        consumer.forEach { it.mapMessage(trace) }
    }

    override fun extractData(argumentExtractor: ObservationUtility.JoinPointObservationArgs): Map<String, *> {
        return arguments
            .flatMap { it.extract(argumentExtractor, this).entries }
            .associate { Pair(it.key, it.value) }

    }

    override fun extractTrace(argumentExtractor: ObservationUtility.JoinPointObservationArgs): Map<String, *>? {
        return mutableMapOf(
            Pair("MethodName", argumentExtractor.id),
            Pair("JoinPointAction", argumentExtractor.advice.advice.joinPointAction)
        )
    }

    override fun getSerializer(value: Any): ClassSerializer? {
        return getSerializerCache(value)
    }

    override fun matchers(args: ObservationUtility.ObservationArgs): List<BehaviorMatcher> {
        return matcher
    }

    fun getSerializerCache(value: Any): ClassSerializer? {
        if (serializersCache.containsKey(value::class))  {
            return serializersCache[value::class]
        }

        serializers
            .filter { it.matches(value::class.java) }
            .take(1)
            .forEach { serializersCache[value::class] = it }

        return serializersCache[value::class];
    }

}