package no.nav.opptjening.planke

import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import java.util.concurrent.TimeUnit

class App(serverPort: Int = 8080, val auth: BasicAuth) {
    private val server: NettyApplicationEngine

    init {
        server = embeddedServer(Netty, createApplicationEnvironment(serverPort))
        server.start(wait = false)
    }

    private fun createApplicationEnvironment(serverPort: Int) = applicationEngineEnvironment {
        connector { port = serverPort }
        module { plankeModul(auth) }
    }

    fun stop() {
        server.stop(500, 500, TimeUnit.MILLISECONDS)
    }
}