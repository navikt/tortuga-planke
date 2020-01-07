package no.nav.opptjening.planke

import no.nav.common.KafkaEnvironment
import no.nav.opptjening.planke.KafkaConfiguration.KafkaSecurityConfig
import no.nav.opptjening.planke.KafkaConfiguration.KafkaSecurityConfig.Properties.SECURITY_PROTOCOL
import org.junit.jupiter.api.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AppTest {

    private val client = HttpClient.newHttpClient()

    private val localhost = "http://localhost:"
    private val defaultPort = "8080"
    private val livelinessEndpoint = "isAlive"
    private val readinessEndpoint = "isReady"
    private val skatteoppgjorHendelseTopic = "topic/skatteoppgjorhendelse/"
    private val httpOk = 200
    private val httpBadRequest = 400

    @BeforeAll
    internal fun setUp() {
        kafkaEnvironment = KafkaEnvironment(
            NUMBER_OF_BROKERS,
            TOPICS,
            withSchemaRegistry = true
        )
        kafkaEnvironment.start()

        with(KafkaConfiguration) {
            bootstrapServers = kafkaEnvironment.brokersURL
            schemaUrl = kafkaEnvironment.schemaRegistry!!.url
            securityConfig = PlainTextKafkaSecurityConfig
        }

        app = App()
    }

    @AfterAll
    internal fun tearDown() = kafkaEnvironment.tearDown()

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 200 OK when event is added to kafka topic`() {
        val body = """{"ident":"12345678901" ,"periode":"2019"}"""
        val request = createPostRequest(body)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(httpOk, response.statusCode())
    }


    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when ident is missing`() {
        val body = """{"periode":"2019"}"""
        val request = createPostRequest(body)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(httpBadRequest, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when periode is missing`() {
        val body = """{"ident":"12345678901"}"""
        val request = createPostRequest(body)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(httpBadRequest, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when periode and ident is missing`() {
        val request = createPostRequest("{}")
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(httpBadRequest, response.statusCode())
    }

    @Test
    fun `isReady endpoint returns 200 OK when application runs`() {
        val request = createGetRequest(readinessEndpoint)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(httpOk, response.statusCode())
    }

    @Test
    fun `isAlive endpoint returns 200 OK when application runs`() {
        val request = createGetRequest(livelinessEndpoint)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(httpOk, response.statusCode())
    }

    @Test
    fun `should write to topic`() {
    }

    private fun createPostRequest(body: String): HttpRequest {
        return HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(URI.create("$localhost$defaultPort/$skatteoppgjorHendelseTopic"))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }

    private fun createGetRequest(endpoint: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("$localhost$defaultPort/$endpoint"))
            .GET()
            .build()
    }

    object PlainTextKafkaSecurityConfig : KafkaSecurityConfig {
        override val securityConfig = mapOf(SECURITY_PROTOCOL to "plaintext")
    }

    companion object {
        private const val NUMBER_OF_BROKERS = 3

        private val TOPICS = listOf(KafkaConfiguration.SKATTEOPPGJORHENDELSE_TOPIC)

        lateinit var kafkaEnvironment: KafkaEnvironment
        lateinit var app: App
    }
}

