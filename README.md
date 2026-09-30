# Demo: Kotlin lexer and parser with the syntax API

A bare-bone IntelliJ Platform plugin for a language of lines `name = value`. Its lexer and parser are Kotlin
code of the syntax API of the platform (`com.intellij.platform.syntax`), which the build generates from
a JFlex and a Grammar-Kit grammar. Nothing generated is checked in.

Supports IntelliJ Platform 2026.1, 2026.2 and 2026.3 EAP.

## Modules

It's a plugin of the plugin model v2, its content modules are:

| Directory      | Module         | Content                                                                                        |
|----------------|----------------|------------------------------------------------------------------------------------------------|
| `demo-syntax`  | `demo.syntax`  | the grammars at `src/grammar`, the generated lexer and parser, nothing of the PSI               |
| `demo-shared`  | `demo.shared`  | language, file type, the generated PSI, parser definition and highlighter                      |
| `demo-backend` | `demo.backend` | an annotator which only runs on the backend                                                    |

## Generation

The tasks are in `buildSrc`, see `Grammars.kt`.

- `:demo-syntax:generateDemoLexer` runs JFlex on `demo.flex`. JetBrains' JFlex emits Kotlin, shaped by the
  skeleton `tools/lexer/idea-flex-kotlin.skeleton` into a `FlexLexer` of the syntax API.
- `:demo-syntax:generateDemoSyntax` runs Grammar-Kit on `demo.bnf` and keeps the Kotlin parser and element types,
  i.e. the classes of `parserClass` and `syntaxElementTypeHolderClass`.
- `:demo-shared:generateDemoPsi` runs Grammar-Kit on the same grammar and keeps everything else, the Java PSI.

Grammar-Kit's command line only writes parsers of the classic PSI API. `tools/parser/GrammarKitGenerator.java`
calls its generator service instead, which writes a parser of the API the grammar asks for with
`generate=[parser-api="syntax"]`. Java runs it as a source file (JEP 330) with the classpath of the stock
`generateParser` task of the IntelliJ Platform Gradle Plugin.

Grammar-Kit passes `null` as the extends sets of a grammar without `extends` relations, which the runtime rejects.
`GenerateParserTask` replaces it with an empty array.

The PSI parses with the syntax API: `DemoFileElementType` builds a `PsiSyntaxBuilder` and calls the generated
parser. Two element type converters map the element types of the syntax API to those of the PSI: the generated
`DemoElementTypeConverterFactory` maps the rules and tokens, `DemoFileElementTypeConverterFactory` the root of the file.

## Commands

| Task                | Command                                   |
|---------------------|-------------------------------------------|
| Build and test      | `./gradlew build`                         |
| Sandbox IDE         | `./gradlew runIde`                        |
| Another platform    | `./gradlew build -PplatformVersion=262`   |

The platform versions are configured by `gradle-<platformVersion>.properties`.
