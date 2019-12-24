
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
// tag::plugin[]
//apply plugin: 'com.palantir.docker'
// end::plugin[]

plugins {
    idea
    java
    eclipse
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    id("org.springframework.boot") version "2.1.8.RELEASE"
    id("com.gorylenko.gradle-git-properties") version "1.5.2"
}

group = "m-sadaka"
version = "1.0.1"

val javaVersion by extra { JavaVersion.VERSION_1_8}
val swaggerVersion by extra { "2.9.2" }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.json:json:20090211")
    implementation("com.squareup.okhttp3:okhttp:3.2.0")
    implementation("org.json:json:20180130")
    implementation("com.google.code.gson:gson:2.8.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.10.1")
    implementation("io.springfox:springfox-swagger-ui:$swaggerVersion")
    implementation("io.springfox:springfox-swagger2:$swaggerVersion")


    runtime("com.h2database:h2")
    testCompile("org.springframework.boot:spring-boot-starter-test")
}

gitProperties {
    dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
    gitPropertiesDir = "$buildDir/resources/main/META-INF"
}

idea {
    module {
        generatedSourceDirs.add(File("$buildDir/generated"))
    }
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

repositories {
    mavenLocal()
    mavenCentral()
}