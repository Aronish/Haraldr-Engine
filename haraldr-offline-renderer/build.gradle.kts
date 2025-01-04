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