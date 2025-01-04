plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":haraldr-engine"))
    implementation(files("../lib/annotations-19.0.0.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-opengl.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-stb.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-tinyexr.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-tinyexr-natives-windows.jar"))
    implementation(files("../lib/JsonParser.jar"))
}

application {
    mainClass.set("offlinerenderer.OfflineRendererEntryPoint")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "offlinerenderer.OfflineRendererEntryPoint"
    }
    from({
        // Include all runtime dependencies in the JAR
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}
