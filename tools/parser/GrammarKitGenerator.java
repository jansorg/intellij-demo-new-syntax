import com.intellij.lang.ASTFactory;
import com.intellij.lang.LanguageASTFactory;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.BnfParserDefinition;
import org.intellij.grammar.LightPsi;
import org.intellij.grammar.generator.Generator;
import org.intellij.grammar.generator.batch.BnfGenerationService;
import org.intellij.grammar.psi.BnfFile;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Generates the parser and the PSI of a language from its Grammar-Kit grammar.
 *
 * <p>Grammar-Kit ships a command line of its own, {@code org.intellij.grammar.Main}, which always
 * writes a parser of the classic PSI API. A parser of the platform syntax API, which this project
 * uses, is only written by the generator service, so this launcher calls that service instead. The
 * grammar decides which of the two APIs the generated parser speaks, see its {@code parser-api}
 * option.
 *
 * <p>It is run as a source file, {@code java -cp <grammar-kit and the IDE> GrammarKitGenerator.java
 * <output directory> <grammar>}, so it needs no build of its own. The build keeps a part of what it
 * writes in each module, see {@code GenerateParserTask}.
 */
public final class GrammarKitGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: GrammarKitGenerator <output directory> <grammar.bnf>");
            System.exit(1);
        }

        File outputDirectory = new File(args[0]);
        File grammar = new File(args[1]);

        // No IDE loads the registry here, and the light PSI warns about every key it reads before that.
        // The keys it reads have values of their own, which are the ones it needs.
        Registry.markAsLoaded();

        // Grammar-Kit reads a grammar with a PSI of its own, outside of a running IDE.
        LightPsi.init();
        LightPsi.Init.addKeyedExtension(LanguageASTFactory.INSTANCE, BnfLanguage.INSTANCE, bnfAstFactory(), null);

        PsiFile parsed = LightPsi.parseFile(grammar, new BnfParserDefinition());
        if (!(parsed instanceof BnfFile bnfFile)) {
            System.err.println(grammar + " is no Grammar-Kit grammar");
            System.exit(1);
            return;
        }

        // The service picks the generator of the API the grammar asks for. The PSI and the parser
        // share one output directory, the packages of the grammar keep them apart.
        Generator generator = BnfGenerationService.createGenerator(
                bnfFile,
                grammar.getAbsolutePath(),
                outputDirectory,
                outputDirectory,
                "",
                new ArrayList<File>()
        );
        generator.generate();

        System.out.println("Generated the parser of " + grammar.getName() + " into " + outputDirectory);

        // The light PSI leaves threads of the platform running, which would keep the JVM alive.
        System.exit(0);
    }

    /**
     * The AST factory of the grammar language of Grammar-Kit, which reading a grammar needs.
     *
     * <p>The class is not public, and a source file is loaded by a class loader of its own, so a
     * package of its own would not reach it either. Reflection is the way left.
     */
    private static ASTFactory bnfAstFactory() throws Exception {
        Constructor<?> constructor = Class.forName("org.intellij.grammar.BnfASTFactory").getDeclaredConstructor();
        constructor.setAccessible(true);
        return (ASTFactory) constructor.newInstance();
    }
}
