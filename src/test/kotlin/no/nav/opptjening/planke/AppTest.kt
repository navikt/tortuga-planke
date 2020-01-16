package no.nav.opptjening.planke

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import no.nav.common.KafkaEnvironment
import no.nav.opptjening.planke.KafkaConfiguration.KafkaSecurityConfig
import no.nav.opptjening.planke.KafkaConfiguration.KafkaSecurityConfig.Properties.SECURITY_PROTOCOL
import no.nav.opptjening.planke.KafkaConfiguration.SKATTEOPPGJORHENDELSE_TOPIC
import no.nav.opptjening.schema.skatt.hendelsesliste.Hendelse
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*


internal class AppTest {

    @Test
    fun `isReady endpoint returns 200 OK when application runs`() {
        val request = createGetRequest(READINESS_PATH)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.OK.value, response.statusCode())
    }

    @Test
    fun `isAlive endpoint returns 200 OK when application runs`() {
        val request = createGetRequest(LIVELINESS_PATH)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.OK.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 200 OK when event is added topic`() {
        val body = """{"ident":"12345678901" ,"periode":"2019"}"""
        val request = createPostRequest(body, getBasicAuth(USER_NAME, PASSWORD))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.OK.value, response.statusCode())
        Assertions.assertEquals(getRecordsOnTopic()!!.size, 1)
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should add period and identification to kafka topic`() {
        val periode = "2018"
        val ident = "12345678901"
        val request =
            createPostRequest("""{"ident":"$ident" ,"periode":"$periode"}""", getBasicAuth(USER_NAME, PASSWORD))

        client.send(request, HttpResponse.BodyHandlers.ofString())

        val recods = getRecordsOnTopic()
        Assertions.assertEquals(recods!!.count(), 1)
        Assertions.assertEquals(recods[0].topic(), TOPIC)
        Assertions.assertEquals(recods[0].value()!!.getGjelderPeriode(), periode)
        Assertions.assertEquals(recods[0].value()!!.getIdentifikator(), ident)
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
        val request = createPostRequest(body, getBasicAuth(USER_NAME, PASSWORD))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.BadRequest.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when periode is missing`() {
        val body = """{"ident":"12345678901"}"""
        val request = createPostRequest(body, getBasicAuth(USER_NAME, PASSWORD))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.BadRequest.value, response.statusCode())
    }

    @Test
    fun `addToSkatteoppgjorhendelseTopic should return 400 BadRequest when periode and ident is missing`() {
        val request = createPostRequest("{}", getBasicAuth(USER_NAME, PASSWORD))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertEquals(HttpStatusCode.BadRequest.value, response.statusCode())
    }

    private fun createGetRequest(endpoint: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("$LOCALHOST/$endpoint"))
            .GET()
            .build()
    }

    private fun getBasicAuth(userName: String, password: String): String {
        return "Basic " + Base64.getEncoder().encodeToString(("""$userName:$password""").toByteArray())
    }

    private fun createPostRequest(body: String, basicAuth: String): HttpRequest {
        return HttpRequest.newBuilder()
            .header(HttpHeaders.ContentType, "application/json")
            .header(HttpHeaders.Authorization, basicAuth)
            .uri(URI.create("$LOCALHOST/$SKATTEOPPGJOR_HENDELSE_TOPIC_PATH"))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }


    private fun getRecordsOnTopic(): List<ConsumerRecord<HendelseKey?, Hendelse?>>? {
        return kafkaConsumer.poll(Duration.ofSeconds(5L))!!.records(TOPIC).toList()
    }

    companion object {
        private const val LOCALHOST = "http://localhost:8080"
        private const val SKATTEOPPGJOR_HENDELSE_TOPIC_PATH = "topic/skatteoppgjorhendelse/"
        private const val LIVELINESS_PATH = "isAlive"
        private const val READINESS_PATH = "isReady"

        private const val PASSWORD = "TestPassword"
        private const val USER_NAME = "TestUserName"

        private const val NUMBER_OF_BROKERS = 3
        private const val TOPIC = SKATTEOPPGJORHENDELSE_TOPIC

        private val client = HttpClient.newHttpClient()

        private lateinit var app: App
        private lateinit var kafkaEnvironment: KafkaEnvironment
        private lateinit var kafkaConsumer: KafkaConsumer<HendelseKey, Hendelse>

        object PlainTextKafkaSecurityConfig : KafkaSecurityConfig {
            override val securityConfig = mapOf(SECURITY_PROTOCOL to "plaintext")
        }


        @JvmStatic
        @BeforeAll
        internal fun setUp() {
            app = App(auth = BasicAuth(USER_NAME, PASSWORD))

            kafkaEnvironment = KafkaEnvironment(
                NUMBER_OF_BROKERS,
                listOf(TOPIC),
                withSchemaRegistry = true
            )
            kafkaEnvironment.start()

            with(KafkaConfiguration) {
                bootstrapServers = kafkaEnvironment.brokersURL
                schemaUrl = kafkaEnvironment.schemaRegistry!!.url
                securityConfig = PlainTextKafkaSecurityConfig
            }

            kafkaConsumer = KafkaConsumer(
                mapOf(
                    CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to kafkaEnvironment.brokersURL,
                    AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to kafkaEnvironment.schemaRegistry!!.url,
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java,
                    KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG to true,
                    ConsumerConfig.GROUP_ID_CONFIG to "loot-consumer-group",
                    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
                )
            )

            kafkaConsumer.subscribe(listOf(TOPIC))
        }

        @JvmStatic
        @AfterAll
        internal fun tearDown() {
            kafkaEnvironment.tearDown()
            kafkaConsumer.close()
            app.stop()
        }
    }
}

