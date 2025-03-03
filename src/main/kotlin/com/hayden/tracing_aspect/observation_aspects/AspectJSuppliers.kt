package com.hayden.tracing_aspect.observation_aspects

import java.util.function.Supplier

open class SuppliedPointcut(open val pc: String)

class SuppliedBeforePointcut(override val pc: String): SuppliedPointcut(pc)
class SuppliedAfterPointcut(override val pc: String): SuppliedPointcut(pc)
class SuppliedAroundPointcut(override val pc: String): SuppliedPointcut(pc)

interface PointcutSupplier: Supplier<Set<SuppliedPointcut>>
interface AdviceSupplier: Supplier<String>
interface MonitoringTypesSupplier: Supplier<String>


data class AspectMetadata(val aspectClassName: String, val aspectFunctionName: String);
interface AspectName: Supplier<AspectMetadata>

data class SuppliedAspect(val pointcut: PointcutSupplier, val advice: AdviceSupplier, val name: AspectName, val monitoringTypes: MonitoringTypesSupplier) {
    fun beforeId(): String = "before"
    fun afterId(): String = "after"
    fun aroundId(): String = "around"
    fun adviceId(): String = "advice"
    fun aspectFunctionId(): String = "aspect_fn_name"
    fun aspectClassId(): String = "aspect_name"
    fun monitoringTypes(): String = "monitoring_types"
}

data class TracingAspectSupplier(
    val before: List<String>?,
    val after: List<String>?,
    val around: List<String>?,
    val pointCut: List<String>?,
    val aspect: List<String>?,
    val monitoringTypes: List<String>,
    val aspectFunctionId: String,
    val aspectName: String
): Supplier<SuppliedAspect> {
    override fun get(): SuppliedAspect {
        return SuppliedAspect(object: PointcutSupplier {
            override fun get(): Set<SuppliedPointcut> {
                return mutableSetOf(
                    parseToBefore(),
                    after?.map { SuppliedAfterPointcut("@After(\"${it}\")") }?.firstOrNull() ?: SuppliedAfterPointcut(""),
                    around?.map { SuppliedAroundPointcut("@Around(\"${it}\")") }?.firstOrNull() ?: SuppliedAroundPointcut(""),
                )
            }
        }, object: AdviceSupplier {
            override fun get(): String {
                return pointCut?.map { "@PointCut(\"${it})\"" }?.firstOrNull() ?: ""
            }
        }, object: AspectName {
            override fun get(): AspectMetadata {
                return AspectMetadata(aspectName, aspectFunctionId)
            }
        }, object: MonitoringTypesSupplier {
            override fun get(): String {
                if (monitoringTypes.isEmpty())
                    return "List.of()";
                return "List.of(${monitoringTypes.map { "MonitoringTypes.${it}" }.joinToString(",")})";
            }
        });
    }

    private fun parseToBefore() =
        before?.map { SuppliedBeforePointcut("@Before(\"${it}\")") }?.firstOrNull() ?: SuppliedBeforePointcut("")
}