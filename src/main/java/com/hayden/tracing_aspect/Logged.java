package com.hayden.tracing_aspect;

import com.hayden.tracing_aspect.observation_aspects.*;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {

    /**
     * Extract the arguments from the method call.
     * @return
     */
    Class<? extends BehaviorDataExtractor> argumentExtractor() default BehaviorDataExtractor.DefaultBehaviorDataExtractor.class;

    /**
     * Perform some operation on the Trace after it is extracted.
     * @return
     */
    Class<? extends MessageCapture> messageCapture() default MessageCapture.DefaultMessageCapture.class;
    /**
     * Filter dynamically at runtime whether the trace is called.
     * @return
     */
    Class<? extends BehaviorMatcher>[] behaviorMatcher() default {BehaviorMatcher.DefaultBehaviorMatcher.class};

    Class<? extends ClassSerializer>[] classSerializers() default {};

    String logId() default "";

    MonitoringTypes[] monitoringTypes() default {};

}