import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id ("fabric-loom") version "1.4-SNAPSHOT"
    id ("maven-publish")
    id ("org.jetbrains.kotlin.jvm") version "1.9.21"
}

version = project.findProperty("mod_version")?.toString()!!
group = project.findProperty("maven_group")?.toString()!!

base {
    archivesName = project.findProperty("archives_base_name")?.toString()
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.

    maven("https://maven.nucleoid.xyz")
    maven("https://jitpack.io")
    mavenCentral()

}

fabricApi {
    configureDataGeneration()
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft ("com.mojang:minecraft:${project.findProperty("minecraft_version")}")
    mappings ("net.fabricmc:yarn:${project.findProperty("yarn_mappings")}:v2")
    modImplementation ("net.fabricmc:fabric-loader:${project.findProperty("loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation ("net.fabricmc.fabric-api:fabric-api:${project.findProperty("fabric_version")}")
    modImplementation ("net.fabricmc:fabric-language-kotlin:${project.findProperty("fabric_kotlin_version")}")

    modImplementation("eu.pb4:polymer-core:${project.findProperty("polymer_api_version")}")
    modImplementation("eu.pb4:polymer-blocks:${project.findProperty("polymer_api_version")}")
    modImplementation("eu.pb4:polymer-resource-pack:${project.findProperty("polymer_api_version")}")
    modImplementation("eu.pb4:placeholder-api:${project.findProperty("placeholder_api_version")}")
    modImplementation("com.github.DrexHD:message-api:${project.findProperty("message_api_version")}")
    modImplementation("xyz.nucleoid:stimuli:${project.findProperty("stimuli_api_version")}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("org.kodein.di:kodein-di:7.19.0")
}

tasks.processResources {
//    inputs.property( "version", project.findProperty("version"))

    filesMatching("fabric.mod.json") {
        expand("version" to project.findProperty("version"))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.findProperty("archives_base_name")?.toString()}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}