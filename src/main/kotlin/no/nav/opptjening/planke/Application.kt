package no.nav.opptjening.planke

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import no.nav.opptjening.planke.KafkaConfiguration.BOOTSTRAP_SERVERS
import no.nav.opptjening.planke.hendelse.endpoint.HendelseTopicEndpoint
import org.slf4j.LoggerFactory

fun main() {
    val log = LoggerFactory.getLogger(HendelseTopicEndpoint::class.java)
    log.trace("BootstrapServers:")
    log.trace(System.getenv()[BOOTSTRAP_SERVERS])

    log.trace("SCHEMA_REGISTRY_URL:")
    log.trace(System.getenv()[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG])

    App(auth = BasicAuth(System.getenv()["USERNAME"]!!, System.getenv()["PASSWORD"]!!))
}

