import demo.build.GeneratedPart
import demo.build.registerGrammarKitTasks

// The language, file type, PSI and parser definition, which frontend and backend share.
plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform.module")
    id("org.jetbrains.intellij.platform.grammarkit")
}

dependencies {
    implementation(project(":demo-syntax"))

    intellijPlatform {
        grammarKit(providers.gradleProperty("grammarKitVersion"))
    }
}

// The grammar of demo-syntax -> build/generated/psi/demo: the Java PSI, i.e. everything except the classes of the
// grammar's `parserClass` and `syntaxElementTypeHolderClass`. Grammar-Kit only generates Java for the PSI.
val psi = registerGrammarKitTasks(project(":demo-syntax").layout.projectDirectory.dir("src/grammar"), GeneratedPart.PSI)

// the PSI is Java, a source root of the Java plugin serves both compilers
sourceSets.named("main") {
    java.srcDirs(psi)
}
