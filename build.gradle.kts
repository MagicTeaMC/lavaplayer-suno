plugins {
    kotlin("jvm") version "1.8.22"
    application
}

group = "tw.maoyue"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "jcenter"
        url = uri("https://jcenter.bintray.com/")
    }
    maven {
        name = "lavaplayer"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    implementation("dev.arbjerg:lavaplayer:2.2.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("com.google.code.gson:gson:2.8.8")
}

application {
    mainClass.set("tw.maoyue.lavaplayer.suno.SunoAudioSourceManager")
}

tasks.test {
    useJUnitPlatform()
}
