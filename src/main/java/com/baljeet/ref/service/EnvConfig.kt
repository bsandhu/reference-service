package com.baljeet.ref.service


enum class Topics {
    DEAL_TOPIC
}
enum class ENV {
    DEV, QA, PROD
}

class EnvConfig {
    val props = mapOf(
            "${ENV.DEV}.${Topics.DEAL_TOPIC}" to "topic/topic1",
            "${ENV.QA}.${Topics.DEAL_TOPIC}" to "topic/topic1",
            "${ENV.PROD}.${Topics.DEAL_TOPIC}" to "topic/topic1"
    )

    fun get(prop: Topics) : String = get(prop.name, props)
}

/**
 * Overridde static props with:
 *  Any passed in with -Dname=value
 *  Any env variables
 */
fun get(prop: String, props: Map<String, String>) : String {
    val ENV = System.getProperty("ENV") ?: ENV.DEV.name
    val envProp = "$ENV.$prop"

    return when {
        System.getProperty(envProp) != null -> System.getProperty(envProp)
        System.getenv(envProp) != null -> System.getenv(envProp)
        props[envProp] != null -> props[envProp]
        else -> throw Exception("Property $envProp not found")
    } as String
}