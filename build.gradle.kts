import com.kevintok.gradle.Dep
import com.kevintok.gradle.Version
import com.kevintok.gradle.Mod

typealias Date = java.util.Date
typealias SimpleDateFormat = java.text.SimpleDateFormat
typealias RunConfig = net.minecraftforge.gradle.common.util.RunConfig
typealias RunConfiguration = net.minecraftforge.gradle.common.util.RunConfig.() -> Unit
typealias UserDevExtension = net.minecraftforge.gradle.userdev.UserDevExtension

fun minecraft(configuration: UserDevExtension.() -> Unit) = configuration(
    extensions.getByType(UserDevExtension::class.java)
)

fun NamedDomainObjectContainerScope<RunConfig>.config(name: String, additionalConfiguration: RunConfiguration = {}) {
    val runDirectory = project.file("run")
    val sourceSet = the<JavaPluginConvention>().sourceSets["main"]

    create(name) {
        workingDirectory(runDirectory)
        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")

        additionalConfiguration(this)

        mods { create(Mod.id) { source(sourceSet) } }
    }
}

buildscript {
    repositories {
        maven { setUrl("https://files.minecraftforge.net/maven") }
        jcenter()
        mavenCentral()
    }
    dependencies {
        // Issue with Kotlin DSL, have to use full path here
        classpath(com.kevintok.gradle.Dep.ForgeGradle)
        classpath(com.kevintok.gradle.Dep.KotlinGradle)
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

plugins {
    java
    // Issue with Kotlin DSL, have to use full path here
    kotlin("jvm") version (com.kevintok.gradle.Version.kotlin)
}

apply(plugin = "net.minecraftforge.gradle")

group = Mod.group
version = Mod.version
base.archivesBaseName = Mod.id

repositories {
    maven {
        name = "kotlinforforge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    "minecraft"(Dep.Forge)
    implementation(Dep.KotlinForForge)
}

minecraft {
    mappingChannel = "official"
    mappingVersion = Version.minecraft

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        config("client")
        config("server")
        config("data") {
            args(
                "--mod",
                Mod.id,
                "--all",
                "--output",
                file("src/generated/resources/"),
                "--existing",
                file("src/main/resources/")
            )
        }
    }
}

// Include resources generated by data generators.
sourceSets["main"].resources.srcDir("src/generated/resources")

tasks.withType<Jar> {
    // Manifest
    manifest {
        attributes(
            "Specification-Title" to Mod.id,
            "Specification-Vendor" to Mod.author,
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to Mod.version,
            "Implementation-Vendor" to Mod.author,
            "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        )
    }
    // Obfuscate
    finalizedBy("reobfJar")
}

tasks.processResources {
    expand(
        "modAuthor" to Mod.author,
        "modId" to Mod.id,
        "modVersion" to Mod.version,
        "modName" to Mod.name,
        "modDescription" to Mod.description
    )
}
