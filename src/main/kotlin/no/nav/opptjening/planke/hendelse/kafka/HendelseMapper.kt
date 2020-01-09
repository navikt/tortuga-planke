package no.nav.opptjening.planke.hendelse.kafka

import no.nav.opptjening.planke.hendelse.endpoint.HendelseRequest
import no.nav.opptjening.schema.skatt.hendelsesliste.Hendelse


fun mapToHendelse(hendelse: HendelseRequest): Hendelse {
    return Hendelse(-1L, hendelse.ident, hendelse.periode)
}