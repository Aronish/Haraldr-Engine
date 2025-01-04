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