package no.nav.opptjening.planke.hendelse.kafka

import no.nav.opptjening.schema.skatt.hendelsesliste.Hendelse
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey
import org.apache.kafka.clients.producer.ProducerRecord


fun mapToProducerRecord(topic: String, hendelse: Hendelse): ProducerRecord<HendelseKey, Hendelse> = ProducerRecord(
    topic,
    HendelseKey.newBuilder()
        .setGjelderPeriode(hendelse.getGjelderPeriode())
        .setIdentifikator(hendelse.getIdentifikator())
        .build(),
    hendelse
)
