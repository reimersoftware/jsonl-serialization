plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.0"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.17"
    id("com.palantir.git-version") version "3.1.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
group = "dev.reimer"
version = gitVersion()

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
}

lateinit var javadocJar: TaskProvider<Jar>
lateinit var sourcesJar: TaskProvider<Jar>

tasks {
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