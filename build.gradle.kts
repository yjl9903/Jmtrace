import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.10"
  application
}

group = "edu.nju.iser"
version = "0.1.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")
  implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
  implementation("org.javassist:javassist:3.28.0-GA")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
  mainClass.set("MainKt")
}

tasks {
  register("fatJar", Jar::class.java) {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
      attributes("Main-Class" to "MainKt")
    }
    from(configurations.runtimeClasspath.get()
      .onEach { println("add from dependencies: ${it.name}") }
      .map { if (it.isDirectory) it else zipTree(it) })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
  }
}
