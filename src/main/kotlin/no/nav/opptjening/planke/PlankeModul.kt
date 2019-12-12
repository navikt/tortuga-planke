package no.nav.opptjening.planke;

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.routing
import no.nav.opptjening.planke.instanse.Skatteoppgjorhendelse

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

private fun Routing.addToSkatteoppgjorhendelseTopic(topic: String) {
    post("/skatteoppgjorhendelse/topic") {
        var event =  Gson().fromJson<Skatteoppgjorhendelse>(call.receive<String>(),Skatteoppgjorhendelse::class.java)

        with("" to HttpStatusCode.OK) {
            call.respondText(Gson().toJson(event), ContentType.Application.Json)
        }
    }
}






