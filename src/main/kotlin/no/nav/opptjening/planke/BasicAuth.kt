package no.nav.opptjening.planke

import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic

class BasicAuth(private val userName: String, private val password: String) {

    private fun authenticate(userName: String, password: String): Boolean {
        return this.userName == userName && this.password == password
    }

    fun Authentication.Configuration.authenticate() {
        basic(name = "basicAuth") {
            realm = "Ktor Server"
            validate { credentials ->
                if (authenticate(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}