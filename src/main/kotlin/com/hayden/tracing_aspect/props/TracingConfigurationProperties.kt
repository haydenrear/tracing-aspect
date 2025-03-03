package com.hayden.tracing_aspect.props

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix= "trace")
@Component
@Data
open class TracingConfigurationProperties {

    var serviceInstanceId: String? = null
    var serviceId: String? = null


}