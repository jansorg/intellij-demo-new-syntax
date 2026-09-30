import demo.build.GeneratedPart
import demo.build.registerGrammarKitTasks
import demo.build.registerLexerTasks

// The lexer and the parser of the syntax API. They know nothing of the PSI.
plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform.module")
    id("org.jetbrains.intellij.platform.grammarkit")
}

dependencies {
    intellijPlatform {
        grammarKit(providers.gradleProperty("grammarKitVersion"))
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val grammarDir = layout.projectDirectory.dir("src/grammar")

// src/grammar/demo.flex -> build/generated/lexer/demo/_DemoLexer.kt
val lexers = registerLexerTasks(grammarDir)

// src/grammar/demo.bnf -> build/generated/syntax/demo: the parser and the element types of the syntax API,
// i.e. the classes of the grammar's `parserClass` and `syntaxElementTypeHolderClass`
val parsers = registerGrammarKitTasks(grammarDir, GeneratedPart.SYNTAX)

kotlin.sourceSets.named("main") {
    kotlin.srcDirs(lexers, parsers)
}
