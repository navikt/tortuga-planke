package no.nav.opptjening.planke.hendelse.kafka

import no.nav.opptjening.nais.signals.Signaller
import no.nav.opptjening.planke.KafkaConfiguration
import no.nav.opptjening.schema.skatt.hendelsesliste.Hendelse
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory


class HendelseProducer {
    private val producer = KafkaConfiguration.kafkaHendelseProducer()
    private val topic = KafkaConfiguration.SKATTEOPPGJORHENDELSE_TOPIC

    private val shutdownSignal = Signaller.CallbackSignaller()

    init {
        this.shutdownSignal.addListener(shutdownListener())
    }

    fun sendHendelser(hendelse: Hendelse) {
        val record: ProducerRecord<HendelseKey, Hendelse> = mapToProducerRecord(topic, hendelse)
        LOG.trace("sending record: $record")
        producer.send(record, ProducerCallback(record, shutdownSignal))
    }

    private fun shutdownListener(): Signaller.SignalListener {
        return Signaller.SignalListener()
        {
            LOG.info("Shutting signal received, shutting down hendelse producer")
            shutdown()
        }
    }

    fun shutdown() {
        LOG.info("Shutting down SkatteoppgjorhendelseProducer")
        producer.close()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(HendelseProducer::class.java)

        private class ProducerCallback constructor(
            private val record: ProducerRecord<HendelseKey, Hendelse>,
            private val shutdownSignal: Signaller
        ) :
            Callback {

            override fun onCompletion(metadata: RecordMetadata, exception: Exception?) {
                if (shutdownSignal.signalled()) {
                    LOG.warn(
                        "Skipping persisting of sekvensnummer = {} because we have initiated shutdown",
                        record.value().getSekvensnummer()
                    )
                    return
                }
                if (exception != null) {
                    LOG.error(
                        "Unrecoverable error when sending record with sekvensnummer = {}, shutting down",
                        record.value().getSekvensnummer(),
                        exception
                    )
                    shutdownSignal.signal()
                } else {
                    val offset: String = if (metadata.hasOffset()) "" + metadata.offset() else "Without offset!"
                    val topic = metadata.topic()

                    LOG.trace("""Record sent ok. topic: $topic offset: $offset """)
                }
            }
        }
    }
}
