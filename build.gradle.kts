import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.flyway.mysql)
    }
}

plugins {
    `java-library`
    idea
    eclipse
    `maven-publish`
    alias(libs.plugins.flyway)
    alias(libs.plugins.spotless)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.micronaut.aot)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    runtimeOnly(libs.mysql)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    implementation(libs.ulid)
    implementation(libs.guava)

    implementation("com.s-kugel.schneider:enums:1.0.0")

    runtimeOnly("org.yaml:snakeyaml")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    compileOnly("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            "SKIPPED",
            "PASSED",
            "FAILED",
            "STANDARD_ERROR",
        )
        exceptionFormat = TestExceptionFormat.FULL
    }
}

flyway {
    url = "jdbc:mysql://${System.getenv("FASAN_DB_HOST")}:${System.getenv("FASAN_DB_PORT")}/${System.getenv("FASAN_DB_NAME")}"
    user = System.getenv("FASAN_DB_USER")
    password = System.getenv("FASAN_DB_PASSWORD")
    cleanDisabled = false
}

application {
    mainClass = "com.s_kugel.schneider.fasan.Application"
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.s_kugel.schneider.eule.*")
    }
    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

spotless {
    encoding("UTF-8")
    java {
        importOrder()
        formatAnnotations()
        indentWithSpaces()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
        googleJavaFormat()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.s-kugel.schneider"
            artifactId = "fasan-db"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}
