plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "skytils"
version = "1.0-SNAPSHOT"

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
    id = "skytils.knockoffmixingradle"
    implementationClass = "com.github.skytils.gradle.plugin.knockoffmixingradle"
    displayName = "KnockOffMixinGradle"
    description = "Please send help"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.Skytils"
            artifactId = "gradle.plugin.KnockOffMixinGradle"
            version = "LOCAL"

            from(components["java"])
        }
    }
}