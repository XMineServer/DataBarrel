plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("org.jetbrains.kotlin.plugin.lombok") version "2.2.0-RC"
}

group = "org.skyisland"
version = "0.1"

paper {
    name = rootProject.name
    prefix = rootProject.name
    main = "org.skyisland.bedrockdata.BedrockDataPlugin"
    bootstrapper = "org.skyisland.bedrockdata.BedrockDataBootstrapper"
    apiVersion = "1.21"
    authors = listOf("sidey383")
    foliaSupported = true
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api("com.zaxxer:HikariCP:6.3.0")

    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:6.3.0")

    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
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