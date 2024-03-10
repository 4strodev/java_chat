plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val flatlafVersion = "3.4"
    implementation( "com.formdev:flatlaf:${flatlafVersion}" )
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}