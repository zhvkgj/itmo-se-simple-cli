package ru.itmo.se.cli.parser;

import org.junit.jupiter.api.*;
import ru.itmo.se.cli.environment.Context;

import java.util.List;

/**
 * @author Sergey Sokolvyak on 25.02.2021
 */
public class SimpleCommandLineParserTest {
    private SimpleCommandLineParser simpleCommandLineParser;

    @BeforeEach
    public void setUp() {
        simpleCommandLineParser = new SimpleCommandLineParser(new ExpansionProvider());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with empty string")
    public void testParse0() {
        var input = "  ";
        List<Token> parsedResult = simpleCommandLineParser.parse(input);
        Assertions.assertEquals(0, parsedResult.size());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with pipe")
    public void testParse1() {
        var input = "   echo  \"test.txt\"   |cat";
        List<Token> parsedResult = simpleCommandLineParser.parse(input);
        Assertions.assertEquals(4, parsedResult.size());

        Assertions.assertEquals("echo", parsedResult.get(0).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(0).getType());

        Assertions.assertEquals("test.txt", parsedResult.get(1).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(1).getType());

        Assertions.assertEquals("|", parsedResult.get(2).getContent());
        Assertions.assertEquals(Token.Type.Pipe, parsedResult.get(2).getType());

        Assertions.assertEquals("cat", parsedResult.get(3).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(3).getType());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with keys")
    public void testParser2() {
        var input = " wc -n 2 -A \"fafa*\" ";
        List<Token> parsedResult = simpleCommandLineParser.parse(input);
        Assertions.assertEquals(5, parsedResult.size(), parsedResult.toString());

        Assertions.assertEquals("wc", parsedResult.get(0).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(0).getType());

        Assertions.assertEquals("-n", parsedResult.get(1).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(1).getType());

        Assertions.assertEquals("2", parsedResult.get(2).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(2).getType());

        Assertions.assertEquals("-A", parsedResult.get(3).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(3).getType());

        Assertions.assertEquals("fafa*", parsedResult.get(4).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(4).getType());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with pipes inside quotes")
    public void testParse3() {
        var input = "  echo  \" | test.txt\"|cat ' :<( !|'  ";
        List<Token> parsedResult = simpleCommandLineParser.parse(input);
        Assertions.assertEquals(5, parsedResult.size(), parsedResult.toString());

        Assertions.assertEquals("echo", parsedResult.get(0).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(0).getType());

        Assertions.assertEquals(" | test.txt", parsedResult.get(1).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(1).getType());

        Assertions.assertEquals("|", parsedResult.get(2).getContent());
        Assertions.assertEquals(Token.Type.Pipe, parsedResult.get(2).getType());

        Assertions.assertEquals("cat", parsedResult.get(3).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(3).getType());

        Assertions.assertEquals(" :<( !|", parsedResult.get(4).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(4).getType());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with variable declaration and substitution")
    public void testParser4() {
        var input = " ke=$var  echo  \"${var}, world! | test.txt\"|cat '$var :<( !|'  ";
        Context.getInstance().setOrAddVariable("var", "Hello");
        List<Token> parsedResult = simpleCommandLineParser.parse(input);
        Assertions.assertEquals(7, parsedResult.size(), parsedResult.toString());

        Assertions.assertEquals("ke=Hello", parsedResult.get(0).getContent());
        Assertions.assertEquals(Token.Type.VarDecl, parsedResult.get(0).getType());

        Assertions.assertEquals("|", parsedResult.get(1).getContent());
        Assertions.assertEquals(Token.Type.Pipe, parsedResult.get(1).getType());

        Assertions.assertEquals("echo", parsedResult.get(2).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(2).getType());

        Assertions.assertEquals("Hello, world! | test.txt", parsedResult.get(3).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(3).getType());

        Assertions.assertEquals("|", parsedResult.get(4).getContent());
        Assertions.assertEquals(Token.Type.Pipe, parsedResult.get(4).getType());

        Assertions.assertEquals("cat", parsedResult.get(5).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(5).getType());

        Assertions.assertEquals("$var :<( !|", parsedResult.get(6).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(6).getType());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Another test parser with variable declaration and substitution")
    public void testParser5() {
        var input = " ke=$var  var2=231  _super_var34=\"${var}, world! | test.txt\"|cat '$var :<( !|'  ";
        Context.getInstance().setOrAddVariable("var", "Hello");
        List<Token> parsedResult = simpleCommandLineParser.parse(input);
        Assertions.assertEquals(6, parsedResult.size(), parsedResult.toString());

        Assertions.assertEquals("ke=Hello", parsedResult.get(0).getContent());
        Assertions.assertEquals(Token.Type.VarDecl, parsedResult.get(0).getType());

        Assertions.assertEquals("var2=231", parsedResult.get(1).getContent());
        Assertions.assertEquals(Token.Type.VarDecl, parsedResult.get(1).getType());

        Assertions.assertEquals("_super_var34=Hello, world! | test.txt", parsedResult.get(2).getContent());
        Assertions.assertEquals(Token.Type.VarDecl, parsedResult.get(2).getType());

        Assertions.assertEquals("|", parsedResult.get(3).getContent());
        Assertions.assertEquals(Token.Type.Pipe, parsedResult.get(3).getType());

        Assertions.assertEquals("cat", parsedResult.get(4).getContent());
        Assertions.assertEquals(Token.Type.Command, parsedResult.get(4).getType());

        Assertions.assertEquals("$var :<( !|", parsedResult.get(5).getContent());
        Assertions.assertEquals(Token.Type.Arg, parsedResult.get(5).getType());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with incomplete full quote")
    public void testParserWithSingleFullQuote() {
        Throwable exception = Assertions.assertThrows(ParsingException.class,
            () -> simpleCommandLineParser.parse(" dafaf \" ' "));
        Assertions.assertEquals("Syntax error: unexpected token \"", exception.getMessage());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with incomplete quote")
    public void testParserWithSingleWeakQuote() {
        Throwable exception = Assertions.assertThrows(ParsingException.class,
            () -> simpleCommandLineParser.parse(" dhey ' ff "));
        Assertions.assertEquals("Syntax error: unexpected token '", exception.getMessage());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with forbidden symbol")
    public void testParserWitnForbiddenSymbols() {
        Throwable exception = Assertions.assertThrows(ParsingException.class,
            () -> simpleCommandLineParser.parse(" dafaf ( "));
        Assertions.assertEquals("Syntax error: unexpected token (", exception.getMessage());

        exception = Assertions.assertThrows(ParsingException.class,
            () -> simpleCommandLineParser.parse(" echo >test.txt | grep <test.txt "));
        Assertions.assertEquals("Syntax error: unexpected token >", exception.getMessage());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Test parser with wrong substitution")
    public void testParserWithWrongSubstitution1() {
        Throwable exception = Assertions.assertThrows(ParsingException.class,
            () -> simpleCommandLineParser.parse("var=${ "));
        Assertions.assertEquals("Syntax error: bad substitution", exception.getMessage());
    }

    @Test
    @Tag("Parser")
    @DisplayName("Another test parser with wrong substitution")
    public void testParserWithWrongSubstitution2() {
        Throwable exception = Assertions.assertThrows(ParsingException.class,
            () -> simpleCommandLineParser.parse("var=${}"));
        Assertions.assertEquals("Syntax error: bad substitution", exception.getMessage());
    }
}
