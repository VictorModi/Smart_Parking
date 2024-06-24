plugins {
    java
    war
    kotlin("jvm") version "1.8.0"
    id("app.cash.sqldelight") version "2.0.2"
}

group = "mba.vm.smart.parking"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    // Kotlin 标准库和反射库
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // SQLDelight JDBC 驱动
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")

    // lombok
    implementation("org.projectlombok:lombok:1.18.30")

    // Gson
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.google.guava:guava:33.2.1-jre")

    implementation("org.springframework.security:spring-security-crypto:6.3.0")
    implementation("commons-logging:commons-logging:1.3.2")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.78.1")
    implementation("org.owasp.encoder:encoder:1.2.3")

    compileOnly(files("tomcat/lib/servlet-api.jar"))
    compileOnly(files("tomcat/lib/jsp-api.jar"))

    // websocket
//    implementation("javax:javaee-api:8.0")
}



tasks.test {
    useJUnitPlatform()
}

val copyJars by tasks.registering(Copy::class) {
    val targetDir = file("need-import-libs")
    from(configurations.runtimeClasspath)
    into(targetDir)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    doFirst {
        // Ensure the target directory exists
        targetDir.mkdirs()
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set(group.toString())
            dialect("app.cash.sqldelight:mysql-dialect:2.0.2")
        }
    }
}

tasks.register("prepareJars") {
    dependsOn(copyJars)
}
