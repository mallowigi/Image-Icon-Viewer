/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Elior "Mallowigi" Boukhobza, David Sommer and Jonathan Lermitage
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
 *
 */

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "0.7.3"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")
val depsTwelveMonkeys = properties("depsTwelveMonkeys")

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/jetbrains/intellij-plugin-service")
    maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
    maven(url = "https://www.jetbrains.com/intellij-repository/releases")
    maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")
    implementation("com.twelvemonkeys.imageio:imageio-core:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-metadata:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-sgi:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-psd:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-tiff:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-pdf:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-icns:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-pcx:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-pnm:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-tga:$depsTwelveMonkeys")
    implementation("com.twelvemonkeys.imageio:imageio-bmp:$depsTwelveMonkeys")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")
    downloadSources = true
    instrumentCode = true
    updateSinceUntilBuild = true
//  localPath.set(properties("idePath"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
// changelog {
//  path = "${project.projectDir}/docs/CHANGELOG.md"
//  version = properties("pluginVersion")
//  keepUnreleasedSection = true
//  unreleasedTerm = "Changelog"
//  groups = emptyList()
// }

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true
    autoCorrect = true

    reports {
        html.enabled = true
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<io.gitlab.arturbosch.detekt.Detekt> {
        jvmTarget = "1.8"
    }

    sourceSets {
        main {
            java.srcDirs("src/main/java")
            resources.srcDirs("src/main/resources")
        }
    }

    patchPluginXml {
        version(properties("pluginVersion"))
        sinceBuild(properties("pluginSinceBuild"))
        untilBuild(properties("pluginUntilBuild"))

        // Get the latest available change notes from the changelog file
//    changeNotes(
//        closure {
//          File(projectDir, "docs/CHANGELOG.md")
//              .readText()
//              .lines()
//              .joinToString("\n")
//              .run { markdownToHTML(this) }
//        }
//    )
    }

    runPluginVerifier {
        ideVersions(properties("pluginVerifierIdeVersions"))
    }

    buildSearchableOptions {
        enabled = false
    }

    publishPlugin {
//    dependsOn("patchChangelog")
        token(file("./publishToken").readText())
    }
}
