plugins {
    id("java")
    id("application")
    id("antlr")
    id("me.champeau.jmh") version "0.6.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val antlrVersion = "4.9.2"
    antlr("org.antlr:antlr4:${antlrVersion}")

    val truffleVersion = "22.3.0"
    implementation("org.graalvm.truffle:truffle-api:${truffleVersion}")
    annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:${truffleVersion}")
    implementation("org.graalvm.js:js:${truffleVersion}")

    val junitVersion = "5.9.3"
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    val jmhVersion = "1.36"
    jmh("org.openjdk.jmh:jmh-core:${jmhVersion}")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:${jmhVersion}")
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:${jmhVersion}")

    var apacheVersion = "1.10.0"
    implementation("org.apache.commons:commons-text:${apacheVersion}")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--add-exports", "java.base/jdk.internal.module=ALL-UNNAMED"))
}

application {
    mainClass.set("org.example.Main")
    applicationDefaultJvmArgs = listOf("--add-exports", "java.base/jdk.internal.module=ALL-UNNAMED")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf("--add-exports", "java.base/jdk.internal.module=ALL-UNNAMED", "-Dgraalvm.locatorDisabled=true")
}

