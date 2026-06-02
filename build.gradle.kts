plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Playwright for Java — browser automation
    implementation("com.microsoft.playwright:playwright:1.49.0")

    // AssertJ for fluent assertions in the POM tests
    testImplementation("org.assertj:assertj-core:3.26.3")

    // Apache Commons Lang for random test-data generation
    implementation("org.apache.commons:commons-lang3:3.17.0")
}

tasks.test {
    useJUnitPlatform()

    // Admin credentials are passed from -D system properties (step 9 requires
    // them to be supplied securely from the terminal, never hard-coded).
    systemProperty("admin.email", System.getProperty("admin.email", ""))
    systemProperty("admin.password", System.getProperty("admin.password", ""))

    // Built-in Gradle HTML test report (the screenshot the assignment asks for).
    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Convenience task to download Playwright's bundled Chromium. Only needed if the
// system Google Chrome channel is unavailable (the factory tries system Chrome first).
//   ./gradlew installPlaywright
tasks.register<JavaExec>("installPlaywright") {
    group = "playwright"
    description = "Downloads Playwright browser binaries (bundled Chromium)."
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.microsoft.playwright.CLI")
    args("install", "chromium")
}
