plugins {
    application
    java
    id("org.openjfx.javafxplugin") version "0.0.14"
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories { mavenCentral() }

dependencies {

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {

    mainClass.set("cs449.MainApp")
}

javafx {
    version = "22.0.2"
    modules("javafx.base", "javafx.graphics", "javafx.controls")
}

tasks.test { useJUnitPlatform() }
