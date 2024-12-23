plugins {
    id("java")
}

group = "org.toxi.wpp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = uri("https://repo.papermc.io/repository/maven-public/"))
}

dependencies {
    implementation("org.toxi.misc:toxi-misc:1.0-SNAPSHOT")
    implementation("io.github.odalita-developments.odalitamenus:core:0.5.13")
    implementation("com.google.code.gson:gson:2.11.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}