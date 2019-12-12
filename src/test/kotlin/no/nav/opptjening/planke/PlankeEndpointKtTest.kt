package no.nav.opptjening.planke


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


internal class PlankeEndpointKtTest {
    private val client = HttpClient.newHttpClient()

    private val LOCALHOST = "http://localhost:"
    private val DEFAULT_PORT = "8080"
    private val LIVENESS_ENDPOINT = "isAlive"
    private val READINESS_ENDPOINT = "isReady"
    private val HTTP_OK = 200

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