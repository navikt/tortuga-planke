package no.nav.opptjening.planke;

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.route
import io.ktor.routing.routing
import no.nav.opptjening.planke.endpoint.addToSkatteoppgjorhendelseTopic
import no.nav.opptjening.planke.endpoint.isAlive
import no.nav.opptjening.planke.endpoint.isReady

internal val SKATTEOPPGJORHENDELSE_TOPIC = "privat-tortuga-skatteoppgjorhendelse"

internal fun Application.plankeModul(
    //collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry

) {
    install(ContentNegotiation) {
        jackson {
            // Configure Jackson's ObjectMapper here
        }
    }

    install(Authentication) {
        System.out.println("Test Autentisering")
        basic {
            skipWhen { call -> true }
        }
    }
    routing {
        isAlive()
        isReady()
        addToSkatteoppgjorhendelseTopic(SKATTEOPPGJORHENDELSE_TOPIC)
    }
}








