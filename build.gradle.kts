import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile

val mainClass = "no.nav.opptjening.planke.ApplicationKt"
val ktorVersion = "1.2.4"
val junitJupiterVersion = "5.5.2"
val javaVersion = "12"

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

buildscript {
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
    }
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")

    testCompile("com.github.tomakehurst:wiremock-jre8:2.25.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

val githubUser: String by project
val githubPassword: String by project

repositories {
    mavenCentral()
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

tasks{
    compileKotlin{
        kotlinOptions{
            jvmTarget = "12"
        }
    }
    compileTestKotlin{
        kotlinOptions{
            jvmTarget = "12"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}





/*



tasks.withType<Wrapper> {
    gradleVersion = "5.6.4"
}

tasks.named<Jar>("jar") {
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

tasks.named<KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "12"
}


tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = "12"
}
 */