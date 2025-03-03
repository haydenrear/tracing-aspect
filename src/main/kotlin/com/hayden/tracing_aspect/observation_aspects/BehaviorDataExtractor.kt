package com.hayden.tracing_aspect.observation_aspects

interface BehaviorDataExtractor {

    class DefaultBehaviorDataExtractor: BehaviorDataExtractor {

        override fun extract(proceeding: ObservationUtility.ObservationArgs,
                             utility: ObservationUtility<out ObservationUtility.ObservationArgs>
        ): Map<String, *> {
            return mutableMapOf<String, String>()
        }
    }


    fun extract(proceeding: ObservationUtility.ObservationArgs,
                utility: ObservationUtility<out ObservationUtility.ObservationArgs>
    ): Map<String, *>

}