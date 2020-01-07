package no.nav.opptjening.planke.api.planke.kafka

import no.nav.opptjening.planke.hendelse.endpoint.HendelseRequest
import org.junit.jupiter.api.Test

internal class HendelseRequestTest {

    private val validIdent = "12345678901"
    private val validPeriode = "2017"

    @Test
    fun should_set_ident_when_ident_is_11_digits() {
        val hendelse = HendelseRequest(
            validIdent,
            validPeriode
        )
        assert(hendelse.ident == validIdent)

    }

    @Test
    fun should_set_period_when_periode_is_4_digits_and_bigger_than_2016() {
        val hendelse = HendelseRequest(
            validIdent,
            validPeriode
        )
        assert(hendelse.periode == validPeriode)
    }
}