package no.nav.opptjening.planke.unused

import java.util.*

class ApplicationProperties {

    companion object {
        fun getFromEnvironment(env: Map<String, String>, propertyName: String) =
            Optional.ofNullable(env[propertyName]).orElseThrow<RuntimeException> {
                MissingApplicationConfig("$propertyName not found in environment")
            }
    }
}