plugins {
    java
    application
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:29.0-jre")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceSets {
        main {
            java.setSrcDirs(listOf("src/main"))
            resources.setSrcDirs(listOf("src/resources"))
        }
        test {
            java.setSrcDirs(listOf("src/test"))
        }
    }
}

application {
    mainClass.set("ru.itmo.se.cli.App")
}

checkstyle {
    toolVersion = "8.29"
    isIgnoreFailures = true
    isShowViolations = true
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    reportsDir = file("${buildDir}/checkstyleReports")
}

tasks.compileJava {
    options.release.set(11)
}

tasks.test {
    useJUnitPlatform()
}
