package no.nav.opptjening.planke.endpoint

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post

fun Routing.addToSkatteoppgjorhendelseTopic(topic: String) {
    post("skatteoppgjorhendelse/topic") {
        var skatteoppgjorhendelse = call.receive<Skatteoppgjorhendelse>()
        call.respond(io.ktor.http.HttpStatusCode.OK, skatteoppgjorhendelse)
    }

}

