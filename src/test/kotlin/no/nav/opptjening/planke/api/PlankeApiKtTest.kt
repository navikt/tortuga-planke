package no.nav.opptjening.planke.api


import no.nav.opptjening.planke.App
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


internal class PlankeApiKtTest {
    private val client = HttpClient.newHttpClient()

    private val LOCALHOST = "http://localhost:"
    private val DEFAULT_PORT = "8080"
    private val LIVENESS_ENDPOINT = "isAlive"
    private val READINESS_ENDPOINT = "isReady"
    private val SKATTEOPPGJØR_HENDELSE_TOPIC = "/skatteoppgjorhendelse/topic"
    private val HTTP_OK = 200

    @Test
    fun addToSkatteoppgjorhendelseTopic_should_return_200_OK_when_event_is_added_to_kafka_topic() {
        val body = " {\"fnr\":\"12345678901\" ,\"year\":1970}"
        val request = createPostRequest(SKATTEOPPGJØR_HENDELSE_TOPIC, body);
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(HTTP_OK, response.statusCode())
    }

    @Test
    fun isReady_endpoint_returns_200_OK_when_application_runs() {
        val request = createGetRequest(READINESS_ENDPOINT)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(HTTP_OK, response.statusCode())
    }

    @Test
    fun isAlive_endpoint_returns_200_OK_when_application_runs() {
        val request = createGetRequest(LIVENESS_ENDPOINT)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(HTTP_OK, response.statusCode())
    }

    private fun createPostRequest(endpoint: String, body: String): HttpRequest {
        return HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(URI.create("$LOCALHOST$DEFAULT_PORT/$endpoint"))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }

    private fun createGetRequest(endpoint: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("$LOCALHOST$DEFAULT_PORT/$endpoint"))
            .GET()
            .build()
    }

    companion object {
        private var app: App? = null

        @JvmStatic
        @BeforeAll
        fun setUp() {
            app = App()
        }
    }
}