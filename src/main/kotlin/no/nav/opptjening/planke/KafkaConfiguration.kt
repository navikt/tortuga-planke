package no.nav.opptjening.planke

import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import no.nav.opptjening.schema.skatt.hendelsesliste.Hendelse
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig


object KafkaConfiguration {
    const val BOOTSTRAP_SERVERS = "KAFKA_BOOTSTRAP_SERVERS"
    const val SKATTEOPPGJORHENDELSE_TOPIC = "privat-tortuga-skatteoppgjorhendelse"
    private const val DEFAULT_SCHEMA_REGISTRY_URL = "http://kafka-schema-registry.tpa:8081"

    val env = System.getenv()
    var securityConfig: KafkaSecurityConfig = SaslSecurityConfig(env)

    var bootstrapServers = env[BOOTSTRAP_SERVERS]
    var schemaUrl = env[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG]
        ?: DEFAULT_SCHEMA_REGISTRY_URL

    fun kafkaHendelseProducer(): Producer<HendelseKey, Hendelse> =
        KafkaProducer(
            securityConfig.securityConfig!! +
                    (ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to KafkaAvroSerializer::class.java) +
                    (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaAvroSerializer::class.java) +
                    (KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG to schemaUrl) +
                    (ProducerConfig.ACKS_CONFIG to "all") +
                    (ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE) +
                    (CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers!!)
        )

    interface KafkaSecurityConfig {
        object Properties {
            const val SECURITY_PROTOCOL = "KAFKA_SECURITY_PROTOCOL"
        }

        val securityConfig: Map<String, Any?>?
    }
}
