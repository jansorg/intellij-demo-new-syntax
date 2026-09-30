package demo.lexer

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.element.SyntaxTokenTypes.BAD_CHARACTER
import com.intellij.platform.syntax.element.SyntaxTokenTypes.WHITE_SPACE
import com.intellij.platform.syntax.util.lexer.FlexLexer
import demo.syntax.DemoSyntaxElementTypes

%%

%public
%class _DemoLexer
%implements FlexLexer
%function advance
%type SyntaxElementType
%unicode

WHITE_SPACE = [ \t\f\r\n]+
WORD        = [a-zA-Z]+

%%

{WHITE_SPACE}  { return WHITE_SPACE; }
{WORD}         { return DemoSyntaxElementTypes.WORD; }
"="            { return DemoSyntaxElementTypes.EQ; }

[^]            { return BAD_CHARACTER; }
