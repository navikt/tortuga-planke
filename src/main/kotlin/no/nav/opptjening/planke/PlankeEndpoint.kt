package no.nav.opptjening.planke;

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.routing
import io.prometheus.client.CollectorRegistry
import no.nav.opptjening.planke.instanse.Person

internal val SKATTEOPPGJORHENDELSE_TOPIC = "privat-tortuga-skatteoppgjorhendelse"

internal fun Application.PlankeEndpoint(
    collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry
) {

    routing {
        isAlive()
        isReady()
        addToTopic(SKATTEOPPGJORHENDELSE_TOPIC)
    }
}

private fun Routing.addToTopic(topic: String) {
    post("/queue") {
        val obj: Person = call.receive<Person>()
    }
}






