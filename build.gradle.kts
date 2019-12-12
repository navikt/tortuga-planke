import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mainClass = "no.nav.opptjening.planke.ApplicationKt"
val ktorVersion = "1.2.4"

plugins {
    kotlin("jvm") version "1.3.50"
}

buildscript {
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
    }
}

dependencies {
    compile(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")

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

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "5.6.4"
}