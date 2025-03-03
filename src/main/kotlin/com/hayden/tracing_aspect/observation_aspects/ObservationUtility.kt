package com.hayden.tracing_aspect.observation_aspects

import com.hayden.tracing_aspect.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.CodeSignature

interface ObservationUtility<T: ObservationUtility.ObservationArgs> {

    data class AdviceJoinPoint(val advice: Advice,
                               val args: Map<String, *>) {
        companion object {
            fun from(joinPoint: JoinPoint): AdviceJoinPoint {
                var args =  joinPoint.args.mapIndexed{ i, nextArg ->  getParameterName(joinPoint, i)
                        ?.flatMap { if (nextArg == null) listOf() else listOf<Pair<String, *>>(Pair(it.toString(), nextArg)) }
                    }
                    .flatMap { it!! }
                    .toMap()

                return AdviceJoinPoint(Advice(joinPoint.signature.name, JoinPointAction.Enter, JoinPointType.Annotation), args)
            }

            fun getParameterName(joinPoint: JoinPoint, numArg: Int): String? {
                if (joinPoint.signature is CodeSignature && (joinPoint.signature as CodeSignature?)?.parameterNames?.any { it.length < numArg } == true) {
                    return (joinPoint.signature as CodeSignature?)?.parameterNames?.get(numArg)
                }

                return null
            }
        }
    }

    data class Advice(val fn: String, val joinPointAction: JoinPointAction, val joinPointType: JoinPointType)

    enum class JoinPointAction {
        Enter, Exit
    }

    enum class JoinPointType {
        Agent, Annotation
    }

    interface DecoratingObservationArgs: ObservationArgs {
        val advice: AdviceJoinPoint;

        fun proceed(): Any?
    }

    interface ObservationArgs {
        val id: String
        val monitoringTypes: List<MonitoringTypes>

        fun args(): Map<String, *>
    }

    interface JoinPointObservationArgs: DecoratingObservationArgs {

        override val id: String
            get() = advice.advice.fn

        override fun args(): Map<String, *> {
            return advice.args
        }

    }

    fun extractData(argumentExtractor: T): Map<String, *>?
    fun extractTrace(argumentExtractor: T): Map<String, *>?
    fun consumer(argumentExtractor: T, trace: Trace)
    fun getSerializer(value: Any): ClassSerializer?
    fun matchers(args: ObservationArgs): List<BehaviorMatcher>

}