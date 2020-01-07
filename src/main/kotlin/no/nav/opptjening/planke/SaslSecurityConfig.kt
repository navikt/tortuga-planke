package no.nav.opptjening.planke

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SaslConfigs

class SaslSecurityConfig(env: Map<String, String>) : KafkaConfiguration.KafkaSecurityConfig {
    private var saslJaasConfig: String =
        createPlainLoginModule(env[Properties.USERNAME].orEmpty(), env[Properties.PASSWORD].orEmpty())

    private fun createPlainLoginModule(username: String, password: String) =
        """org.apache.kafka.common.security.plain.PlainLoginModule required username="$username" password="$password";"""

    override val securityConfig = mapOf(
        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SASL_SSL",
        SaslConfigs.SASL_MECHANISM to "PLAIN",
        SaslConfigs.SASL_JAAS_CONFIG to saslJaasConfig
    )

    internal object Properties {
        const val USERNAME: String = "KAFKA_USERNAME"
        const val PASSWORD: String = "KAFKA_PASSWORD"
    }
}



