/*
 * MIT License
 *
 * Copyright (c) 2020 Duncan Sterken
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

plugins {
    idea
    java
    `java-library`
    `maven-publish`

    id("com.github.breadmoirai.github-release") version "2.2.12"
}

group = "com.dunctebot"
version = "0.1.${getBuildNum()}"
val archivesBaseName = "dunctebot-models"

repositories {
    mavenCentral()
    jcenter() // Legacy :(
}

dependencies {
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.1")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")

}

fun getBuildNum(): String {
    return System.getenv("GITHUB_RUN_NUMBER") ?: "dev"
}

val compileJava: JavaCompile by tasks
val javadoc: Javadoc by tasks
val jar: Jar by tasks
val build: Task by tasks
val clean: Task by tasks
val test: Task by tasks
val check: Task by tasks

val sourcesJar = task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allJava)
}

val javadocJar = task<Jar>("javadocJar") {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.destinationDir)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set(archivesBaseName)
                description.set("A helper package for shared models")
                url.set("https://github.com/DuncteBot/models")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("duncte123")
                        name.set("Duncan Sterken")
                        email.set("contact@duncte123.me")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/DuncteBot/models.git")
                    developerConnection.set("scm:git:ssh://git@github.com:DuncteBot/models.git")
                    url.set("https://github.com/DuncteBot/models")
                }
            }

            from(components["java"])

            artifactId = archivesBaseName
            groupId = project.group as String
            version = project.version as String

            artifact(javadocJar)
            artifact(sourcesJar)
        }
    }
}

githubRelease {
    token(System.getenv("GITHUB_TOKEN"))
    owner("DuncteBot")
    repo("models")
    tagName(project.version as String)
    overwrite(false)
    prerelease(false)
    body(changelog())
}

build.apply {
    dependsOn(jar)
    dependsOn(javadocJar)
    dependsOn(sourcesJar)

    jar.mustRunAfter(clean)
    javadocJar.mustRunAfter(jar)
    sourcesJar.mustRunAfter(javadocJar)
}
