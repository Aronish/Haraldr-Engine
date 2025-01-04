plugins {
    `java-library`
}

java {
    withSourcesJar()
    modularity.inferModulePath.set(true)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("../lib/annotations-19.0.0.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-natives-windows.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-glfw.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-glfw-natives-windows.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-opengl.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-opengl-natives-windows.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-stb.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-stb-natives-windows.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-tinyexr.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-tinyexr-natives-windows.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-tinyfd.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl-tinyfd-natives-windows.jar"))
    implementation(files("../lib/lwjgl-release-3.2.3-custom/lwjgl.jar"))
    implementation(files("../lib/JsonParser.jar"))
    implementation(files("../lib/eo-yaml-5.1.9-SNAPSHOT.jar"))
}