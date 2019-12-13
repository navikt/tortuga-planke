package no.nav.opptjening.planke.api;

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.routing

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








