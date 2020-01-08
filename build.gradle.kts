val mainClass = "no.nav.opptjening.planke.ApplicationKt"
val ktorVersion = "1.2.4"
val junitJupiterVersion = "5.5.2"
val javaVersion = "12"
val confluentVersion = "5.0.0"
val kafkaVersion = "2.0.0"

plugins {
    `build-scan`
    kotlin("jvm") version "1.3.50"
    application
}

buildscript {
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))

    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("io.confluent:kafka-avro-serializer:$confluentVersion") {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }

    implementation("no.nav.opptjening:nais-support:f04696f")
    implementation("no.nav.opptjening:avro-schemas:d15ce9b")

    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")
    implementation("no.nav.security:token-validation-ktor:1.0.1")

    testCompile("com.github.tomakehurst:wiremock-jre8:2.25.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testImplementation("no.nav:kafka-embedded-env:2.1.1"){
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

val githubUser: String by project
val githubPassword: String by project

repositories {
    mavenCentral()
    jcenter()
    maven("http://packages.confluent.io/maven/")

    maven {
        credentials {
            username = githubUser
            password = githubPassword
        }
        setUrl("https://maven.pkg.github.com/navikt/avro-schemas") // Todo se etter en bedre måte å gjøre dette på
        setUrl("https://maven.pkg.github.com/navikt/tortuga-nais-support") //TODO
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}

application {
    mainClassName = mainClass
}

sourceSets {
    main {
        java.srcDir("src/main/kotlin/no/nav/opptjeneing/planke")
    }
    test {
        java.srcDir("src/test/kotlin/no/nav/opptjeneing/planke")
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "12"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "12"
        }
    }

    test{
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    jar{
        baseName = "app"

        manifest {
            attributes["Main-Class"] = mainClass
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") { it.name }
        }

        doLast {
            configurations.runtimeClasspath.get().forEach {
                val file = File("$buildDir/libs/${it.name}")
                if (!file.exists()) it.copyTo(file)
            }
        }
    }
}
