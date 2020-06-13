import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

/*
 * build.gradle.kts
 *
 * Glyph, a Discord bot that uses natural language instead of commands
 * powered by DialogFlow and Kotlin
 *
 * Copyright (C) 2017-2020 by Ian Moore
 *
 * This file is part of Glyph.
 *
 * Glyph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

val kotlin_version: String by project.extra

plugins {
    kotlin("jvm") version "1.3.72" apply true
    id("tanvd.kosogor") version "1.0.9" apply true
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "tanvd.kosogor")

    repositories {
        jcenter()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    }

    tasks.register("stage") {
        dependsOn(":clean")
    }

    tasks.withType(KotlinJvmCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}