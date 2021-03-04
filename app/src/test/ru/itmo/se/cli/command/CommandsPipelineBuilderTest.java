package ru.itmo.se.cli.command;

import org.junit.jupiter.api.*;
import ru.itmo.se.cli.command.builder.CommandsPipelineBuilder;
import ru.itmo.se.cli.command.builder.PipelineBuildingException;
import ru.itmo.se.cli.parser.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Sokolvyak on 02.03.2021
 */
public class CommandsPipelineBuilderTest {

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with single command")
    public void testCommandBuilder1() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("cat", Token.TokenType.Command));
        tokens.add(new Token("test.txt", Token.TokenType.Arg));
        tokens.add(new Token("super_secret_passwords.pdf", Token.TokenType.Arg));
        tokens.add(new Token("funny_file.txt", Token.TokenType.Arg));
        List<Command> command = CommandsPipelineBuilder.buildPipe(tokens);
        Assertions.assertEquals(command.size(), 1);
        Assertions.assertEquals(CatCommand.class, command.get(0).getClass());
    }

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with correct pipeline of commands")
    public void testCommandBuilder2() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("wc", Token.TokenType.Command));
        tokens.add(new Token("test.txt", Token.TokenType.Arg));
        tokens.add(new Token("super_secret_passwords.pdf", Token.TokenType.Arg));
        tokens.add(new Token("funny_file.txt", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("echo", Token.TokenType.Command));
        tokens.add(new Token("hello, world!", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("pwd", Token.TokenType.Command));
        tokens.add(new Token("123", Token.TokenType.Arg));
        tokens.add(new Token("he-he", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("cat", Token.TokenType.Command));
        tokens.add(new Token("var=12", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("exit", Token.TokenType.Command));
        tokens.add(new Token("this text won't be printed", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("ls", Token.TokenType.Command));
        tokens.add(new Token("-l", Token.TokenType.Arg));
        List<Command> command = CommandsPipelineBuilder.buildPipe(tokens);
        Assertions.assertEquals(command.size(), 6);
        Assertions.assertEquals(WcCommand.class, command.get(0).getClass());
        Assertions.assertEquals(EchoCommand.class, command.get(1).getClass());
        Assertions.assertEquals(PwdCommand.class, command.get(2).getClass());
        Assertions.assertEquals(CatCommand.class, command.get(3).getClass());
        Assertions.assertEquals(ExitCommand.class, command.get(4).getClass());
        Assertions.assertEquals(ExternalCommand.class, command.get(5).getClass());
    }

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with variable declarations sequence")
    public void testCommandBuilder3() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("var1=12", Token.TokenType.VarDecl));
        tokens.add(new Token("v_2=version_2.0", Token.TokenType.VarDecl));
        tokens.add(new Token("funny=ha-ha", Token.TokenType.VarDecl));
        List<Command> command = CommandsPipelineBuilder.buildPipe(tokens);
        Assertions.assertEquals(command.size(), 1);
        Assertions.assertEquals(VariablesProcessingCommand.class, command.get(0).getClass());
    }

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with variable declarations sequence in pipeline")
    public void testCommandBuilder4() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("var1=", Token.TokenType.VarDecl));
        tokens.add(new Token("v_2=version_2.0", Token.TokenType.VarDecl));
        tokens.add(new Token("funny=ha-ha", Token.TokenType.VarDecl));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("cat", Token.TokenType.Command));
        tokens.add(new Token("var=12", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("exit", Token.TokenType.Command));
        tokens.add(new Token("this text won't be printed", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("variable=informative", Token.TokenType.VarDecl));
        List<Command> command = CommandsPipelineBuilder.buildPipe(tokens);
        Assertions.assertEquals(command.size(), 2);
        Assertions.assertEquals(CatCommand.class, command.get(0).getClass());
        Assertions.assertEquals(ExitCommand.class, command.get(1).getClass());
    }

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with extra pipe at the end")
    public void testCommandBuilderWithExtraPipe() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("wc", Token.TokenType.Command));
        tokens.add(new Token("test.txt", Token.TokenType.Arg));
        tokens.add(new Token("funny_file.txt", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        Throwable exception = Assertions.assertThrows(PipelineBuildingException.class,
            () -> CommandsPipelineBuilder.buildPipe(tokens));
        Assertions.assertEquals("Unexpected end of the pipeline", exception.getMessage());
    }

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with unexpected pipe at the start")
    public void testCommandBuilderWithPipeAtTheStart() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("wc", Token.TokenType.Command));
        tokens.add(new Token("test.txt", Token.TokenType.Arg));
        tokens.add(new Token("funny_file.txt", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        Throwable exception = Assertions.assertThrows(PipelineBuildingException.class,
            () -> CommandsPipelineBuilder.buildPipe(tokens));
        Assertions.assertEquals("Bad pipeline composition: unexpected pipeline token", exception.getMessage());
    }

    @Test
    @Tag("CommandsPipelineBuilder")
    @DisplayName("Test command builder with non command in pipeline")
    public void testCommandBuilderWithWrongPipeline() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("cat", Token.TokenType.Command));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        tokens.add(new Token("heh.txt", Token.TokenType.Arg));
        tokens.add(new Token("test.txt", Token.TokenType.Arg));
        tokens.add(new Token("funny_file.txt", Token.TokenType.Arg));
        tokens.add(new Token("|", Token.TokenType.Pipe));
        Throwable exception = Assertions.assertThrows(PipelineBuildingException.class,
            () -> CommandsPipelineBuilder.buildPipe(tokens));
        Assertions.assertEquals("Bad pipeline composition: unexpected argument token", exception.getMessage());
    }
}
