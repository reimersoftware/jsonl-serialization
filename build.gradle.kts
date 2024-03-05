plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    `maven-publish`
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "dev.reimer"
version = "0.1.0"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}

lateinit var javadocJar: TaskProvider<Jar>
lateinit var sourcesJar: TaskProvider<Jar>

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    // Include project license in generated JARs.
    withType<Jar> {
        from(project.projectDir) {
            include("LICENSE")
            into("META-INF")
        }
    }

    // Generate Kotlin/Java documentation from sources.
    dokka {
        outputFormat = "html"
    }

    // JAR containing Kotlin/Java documentation.
    javadocJar = register<Jar>("javadocJar") {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        dependsOn(dokka)
        from(dokka)
        archiveClassifier.set("javadoc")
    }

    // JAR containing all source files.
    sourcesJar = register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
    }
}