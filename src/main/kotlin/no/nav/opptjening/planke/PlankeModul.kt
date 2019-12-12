package no.nav.opptjening.planke;

import io.ktor.application.Application
import io.ktor.routing.routing
import no.nav.opptjening.planke.endpoint.addToSkatteoppgjorhendelseTopic
import no.nav.opptjening.planke.endpoint.isAlive
import no.nav.opptjening.planke.endpoint.isReady

internal val SKATTEOPPGJORHENDELSE_TOPIC = "privat-tortuga-skatteoppgjorhendelse"

internal fun Application.plankeModul(
    //collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry
) {

    routing {
        isAlive()
        isReady()
        addToSkatteoppgjorhendelseTopic(SKATTEOPPGJORHENDELSE_TOPIC)
    }
}








