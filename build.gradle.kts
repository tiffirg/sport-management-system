import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"

}

repositories {
    mavenCentral()
    //maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    //google()
}

val exposedVersion: String by project

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0") //csv
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1") //datetime
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11") //logging
    runtimeOnly("org.slf4j:slf4j-simple:1.7.29") //logging
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    implementation("com.sksamuel.hoplite:hoplite-core:1.4.15")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.15")

    implementation("org.kodein.di:kodein-di-erased-jvm:6.4.1")
    implementation("org.kodein.di:kodein-di-framework-android-x:6.4.1")

    testImplementation(kotlin("test"))
    implementation("com.h2database:h2:1.4.199")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    testImplementation ("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation ("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}