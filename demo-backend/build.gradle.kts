// Features of the language which run on the backend, e.g. annotators and inspections.
plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform.module")
}

dependencies {
    implementation(project(":demo-shared"))
}
