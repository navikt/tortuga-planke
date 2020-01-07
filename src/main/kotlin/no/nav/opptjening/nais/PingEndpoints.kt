package no.nav.opptjening.nais

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.isReady() = probeRouting("/isAlive")

fun Routing.isAlive() = probeRouting("/isReady")

private fun Routing.probeRouting(path: String) {
    get(path) {
        with("" to HttpStatusCode.OK) {
            call.respondText(first, ContentType.Text.Plain, second)
        }
    }
}