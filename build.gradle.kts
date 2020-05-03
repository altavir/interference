plugins {
    kotlin("jvm") version "1.3.72"
    id("kotlinx-atomicfu") version "0.14.3"
}

group = "ru.mipt.phys"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://dl.bintray.com/mipt-npm/scientifik")
    maven("https://dl.bintray.com/mipt-npm/dev")
}

val kmathVersion by extra("0.1.4-dev-4")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
//    api("scientifik:kmath-core:${kmathVersion}")
    implementation("scientifik:kmath-commons:${kmathVersion}")
    implementation("scientifik:kmath-geometry:${kmathVersion}")
    implementation("scientifik:kmath-for-real:${kmathVersion}")
    implementation("scientifik:kmath-prob:${kmathVersion}")
    implementation("scientifik:kmath-histograms:${kmathVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

atomicfu{
    variant = "VH"
}