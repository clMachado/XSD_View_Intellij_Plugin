plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "br.com.clmDev"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("xerces:xercesImpl:2.12.2")
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2024.3.6")
    type.set("IC") // IntelliJ IDEA Community Edition

    plugins.set(listOf("com.intellij.java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("252.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    buildPlugin {
        archiveFileName.set("xsd-view-${version}.zip")
    }
}