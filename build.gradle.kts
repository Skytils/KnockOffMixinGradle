plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "skytils"
version = "LOCAL"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("allopen"))
    implementation(kotlin("reflect"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin.plugins.create("com.github.Skytils.KnockOffMixinGradle") {
    id = "com.github.skytils.knockoffmixingradle"
    implementationClass = "com.github.skytils.knockoffmixingradle.gradle.plugin.KnockoffMixinGradlePlugin"
    displayName = "KnockOffMixinGradle"
    description = "Please send help"
}