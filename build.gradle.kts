plugins {
    application
    java
    id("org.openjfx.javafxplugin") version "0.0.14"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit 5 (latest BOM + Jupiter)
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("cs449.MainApp")
    // If you ever hit odd JavaFX CSS/module issues when running via Gradle, uncomment:
    // applicationDefaultJvmArgs = listOf("--add-opens=javafx.graphics/com.sun.javafx.css=ALL-UNNAMED")
}

javafx {
    version = "22.0.2"
    modules("javafx.base", "javafx.graphics", "javafx.controls")
}

tasks.test {
    useJUnitPlatform()
}
