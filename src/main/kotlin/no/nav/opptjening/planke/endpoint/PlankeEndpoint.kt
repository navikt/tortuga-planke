package no.nav.opptjening.planke.endpoint

import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.post

fun Routing.addToSkatteoppgjorhendelseTopic(topic: String) {
    post("/skatteoppgjorhendelse/topic") {
        var event = Gson().fromJson<Skatteoppgjorhendelse>(call.receive<String>(), Skatteoppgjorhendelse::class.java)

        with("" to HttpStatusCode.OK) {
            call.respondText(Gson().toJson(event), ContentType.Application.Json)
        }
    }
}

