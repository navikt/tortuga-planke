package no.nav.opptjening.planke.hendelse.endpoint

import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import no.nav.opptjening.planke.hendelse.kafka.HendelseProducer
import no.nav.opptjening.planke.hendelse.kafka.mapToHendelse
import org.slf4j.LoggerFactory

object HendelseTopicEndpoint {

    private const val identLength = 11
    private const val periodeLength = 4
    private val LOG = LoggerFactory.getLogger(HendelseProducer::class.java)
    private val ONLY_DIGITS: Regex = "^[0-9]*\$".toRegex()

    private fun addToTopic(hendelse: HendelseRequest) = with(HendelseProducer()) {
        sendHendelser(mapToHendelse(hendelse))
        shutdown()
    }


    private fun isValidIdent(ident: String): Boolean {
        return ident.length == identLength && ident.matches(
            ONLY_DIGITS
        )
    }

    private fun isValidPeriode(periode: String): Boolean {
        return periode.length == periodeLength && periode.matches(
            ONLY_DIGITS
        )
    }

    fun Routing.addToSkatteoppgjorhendelseTopic() =
        post("topic/skatteoppgjorhendelse") {

            val hendelse = call.receive<HendelseRequest>()

            when {
                !isValidIdent(hendelse.ident) -> {
                    LOG.info("""Invalid ident in hendelse: $hendelse""")
                    BadRequest to """{ "error":"Ident should be FNR or DNR consisting of 11 digits" }"""
                }

                !isValidPeriode(hendelse.periode) ->{
                    LOG.info("""Invalid periode in hendelse: $hendelse""")
                    BadRequest to """{ "error":"Periode should be a 4 digit" }"""
                }

                else -> {
                    addToTopic(hendelse)
                    OK to hendelse
                }
            }.run { call.respond(first, second) }
        }
}











