package no.nav.opptjening.planke

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import no.nav.opptjening.nais.isAlive
import no.nav.opptjening.nais.isReady
import no.nav.opptjening.planke.hendelse.endpoint.HendelseTopicEndpoint.addToSkatteoppgjorhendelseTopic

internal fun Application.plankeModul(auth: BasicAuth) {
    install(ContentNegotiation) {
        jackson {
        }
    }

    install(Authentication) {
        with(auth) {
            authenticate()
        }
    }
    routing {
        isAlive()
        isReady()
        addToSkatteoppgjorhendelseTopic()
    }
}








