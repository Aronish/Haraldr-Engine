plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":haraldr-engine"))
    implementation(files("../lib/annotations-19.0.0.jar"))
    implementation(files("../lib/JsonParser.jar"))
    implementation(files("../lib/eo-yaml-5.1.9-SNAPSHOT.jar"))
}

application {
    mainClass.set("editor.EditorEntryPoint")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "editor.EditorEntryPoint"
    }
    from({
        // Include all runtime dependencies in the JAR
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}