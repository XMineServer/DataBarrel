import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import java.util.*

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("org.jetbrains.kotlin.plugin.lombok") version "2.2.0-RC"
    id("maven-publish")
}

group = "org.skyisland"
version = "1.1.0"

paper {
    name = rootProject.name
    prefix = rootProject.name
    main = "org.skyisland.databarrel.DataBarrelPlugin"
    bootstrapper = "org.skyisland.databarrel.DataBarrelBootstrap"
    apiVersion = "1.21"
    authors = listOf("sidey383", "vasiniyo")
    foliaSupported = true
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api(libs.hikarycp)
    api(libs.awssdk.s3)
    api(libs.jedis)
    api(libs.curator.framework)
    api(libs.curator.recipes)
    api(libs.mongodb.driver)

    implementation("com.mysql:mysql-connector-j:9.5.0")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("com.h2database:h2:2.2.224")
    implementation(libs.hikarycp)
    implementation(libs.awssdk.s3)
    implementation(libs.awssdk.core)
    implementation(libs.awssdk.auth)
    implementation(libs.awssdk.regions)
    implementation(libs.awssdk.url)
    implementation(libs.curator.framework)
    implementation(libs.curator.recipes)
    implementation(libs.mongodb.driver)

    compileOnly(libs.minecraft.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

tasks {

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.version}-all.jar")
        mergeServiceFiles()
    }

    assemble {
        dependsOn(shadowJar)
    }

    jar {
        archiveFileName.set("${rootProject.name}-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            groupId = group.toString().lowercase(Locale.ROOT)
            artifactId = rootProject.name.lowercase(Locale.ROOT)
            version = version.toString().lowercase(Locale.ROOT)
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/xmineserver/databarrel")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}