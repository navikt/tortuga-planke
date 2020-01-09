package no.nav.opptjening.planke

fun main() {
    App(auth = BasicAuth(System.getenv()["USERNAME"]!!, System.getenv()["PASSWORD"]!!))
}

