plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

javafx {
    version = "22.0.2"
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("cs449.MainApp")
}
