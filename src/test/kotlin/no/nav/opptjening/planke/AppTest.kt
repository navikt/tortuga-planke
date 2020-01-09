package no.nav.opptjening.planke

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import no.nav.common.KafkaEnvironment
import no.nav.opptjening.planke.KafkaConfiguration.KafkaSecurityConfig
import no.nav.opptjening.planke.KafkaConfiguration.KafkaSecurityConfig.Properties.SECURITY_PROTOCOL
import org.junit.jupiter.api.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AppTest {

    private val client = HttpClient.newHttpClient()

    private val localhost = "http://localhost:"
    private val defaultPort = "8080"
    private val livelinessEndpoint = "isAlive"
    private val readinessEndpoint = "isReady"
    private val skatteoppgjorHendelseTopic = "topic/skatteoppgjorhendelse/"

    private val userName = "TestUserName"
    private val password = "TestPassword"

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

        app = App(auth = BasicAuth(userName, password))
    }

    @AfterAll
    internal fun tearDown() = kafkaEnvironment.tearDown()

    @Test
    fun `isReady endpoint returns 200 OK when application runs`() {
        val request = createGetRequest(readinessEndpoint)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.OK.value, response.statusCode())
    }

    @Test
    fun `isAlive endpoint returns 200 OK when application runs`() {
        val request = createGetRequest(livelinessEndpoint)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.OK.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 200 OK when event is added to kafka topic`() {
        val body = """{"ident":"12345678901" ,"periode":"2019"}"""
        val request = createPostRequest(body, getBasicAuth(userName, password))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.OK.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 401 unauthorized when event is added to kafka topic`() {
        val body = """{"ident":"12345678901" ,"periode":"2019"}"""
        val request = createPostRequest(body, getBasicAuth("invalidUsername", "invalidPassword"))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.Unauthorized.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when ident is missing`() {
        val body = """{"periode":"2019"}"""
        val request = createPostRequest(body, getBasicAuth(userName, password))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.BadRequest.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when periode is missing`() {
        val body = """{"ident":"12345678901"}"""
        val request = createPostRequest(body, getBasicAuth(userName, password))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.BadRequest.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when periode and ident is missing`() {
        val request = createPostRequest("{}", getBasicAuth(userName, password))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.BadRequest.value, response.statusCode())
    }

    private fun getBasicAuth(userName: String, password: String): String {
        return "Basic " + Base64.getEncoder().encodeToString(("""$userName:$password""").toByteArray())
    }

    private fun createGetRequest(endpoint: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("$localhost$defaultPort/$endpoint"))
            .GET()
            .build()
    }

    private fun createPostRequest(body: String, basicAuth: String): HttpRequest {
        return HttpRequest.newBuilder()
            .header(HttpHeaders.ContentType, "application/json")
            .header(HttpHeaders.Authorization, basicAuth)
            .uri(URI.create("$localhost$defaultPort/$skatteoppgjorHendelseTopic"))
            .POST(HttpRequest.BodyPublishers.ofString(body))
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

