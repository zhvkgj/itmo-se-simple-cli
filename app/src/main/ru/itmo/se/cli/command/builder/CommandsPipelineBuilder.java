package ru.itmo.se.cli.command.builder;

import ru.itmo.se.cli.command.*;
import ru.itmo.se.cli.parser.Token;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Класс, отвечающий за конструирование pipeline-а команд.
 *
 * @author Sergey Sokolvyak on 25.02.2021
 */
public class CommandsPipelineBuilder {
    /**
     * Собирает pipeline команд из списка токенов.
     *
     * @param tokens список токенов, из которых собирается pipeline
     * @return список команд, представляющий pipeline команд
     */
    public static List<Command> buildPipe(List<Token> tokens) {
        List<Command> commands = new LinkedList<>();
        List<Token> currentCommandWithArgs;
        int startIdxOfCurrentCommand = 0;
        boolean isSingleCommand;
        for (int curPos = 0; curPos <= tokens.size(); curPos++) {
            if (curPos != tokens.size() && tokens.get(curPos).getType() != Token.TokenType.Pipe) {
                continue;
            }

            if (curPos - startIdxOfCurrentCommand == 0) {
                if (curPos == tokens.size())
                    throw new PipelineBuildingException("Unexpected end of the pipeline");
                else
                    throw new PipelineBuildingException(String.format(
                        "Bad pipeline composition: unexpected %s token", tokens.get(curPos).getType().getDescription())
                    );
            }

            currentCommandWithArgs = tokens.subList(startIdxOfCurrentCommand, curPos);
            isSingleCommand = startIdxOfCurrentCommand == 0 && curPos == tokens.size();
            if (isSequenceOfVariableDeclarations(currentCommandWithArgs)) {
                if (isSingleCommand)
                    commands.add(buildVarDeclarations(currentCommandWithArgs));
            } else {
                commands.add(buildCommand(currentCommandWithArgs,
                    startIdxOfCurrentCommand == 0, isSingleCommand));
            }
            startIdxOfCurrentCommand = curPos + 1;
        }
        return commands;
    }

    private static Command buildCommand(List<Token> tokens, boolean isFirstCommandInPipeline,
                                        boolean isSingleCommand) {
        Token firstToken = tokens.get(0);
        if (firstToken.getType() != Token.TokenType.Command)
            throw new PipelineBuildingException(String.format(
                "Bad pipeline composition: unexpected %s token", firstToken.getType().getDescription())
            );
        List<Token> arguments = tokens.subList(1, tokens.size());

        Command command;
        switch (firstToken.getContent()) {
            case "grep":
                command = buildGrepCommand(arguments);
                break;
            case "cat":
                command = buildCatCommand(arguments, isFirstCommandInPipeline);
                break;
            case "echo":
                command = buildEchoCommand(arguments);
                break;
            case "wc":
                command = buildWcCommand(arguments, isFirstCommandInPipeline);
                break;
            case "pwd":
                command = buildPwdCommand();
                break;
            case "exit":
                command = buildExitCommand(isSingleCommand);
                break;
            default:
                command = buildExternalCommand(tokens);
        }

        return command;
    }

    private static Command buildVarDeclarations(List<Token> tokens) {
        List<AbstractMap.SimpleImmutableEntry<String, String>> varDecls = tokens.stream()
            .map(token -> {
                String[] nameAndValue = token.getContent().split("=", 2);
                var name = nameAndValue[0];
                var value = nameAndValue.length > 1 ? nameAndValue[1] : "";
                return new AbstractMap.SimpleImmutableEntry<>(name, value);
            }).collect(Collectors.toList());
        return new VariablesProcessingCommand(varDecls);
    }

    private static Command buildCatCommand(List<Token> arguments, boolean isInteractiveMode) {
        List<String> filenames = arguments.stream()
            .map(Token::getContent)
            .collect(Collectors.toList());
        return new CatCommand(filenames, isInteractiveMode);
    }

    private static Command buildWcCommand(List<Token> arguments, boolean isInteractiveMode) {
        List<String> filenames = arguments.stream()
            .map(Token::getContent)
            .collect(Collectors.toList());
        return new WcCommand(filenames, isInteractiveMode);
    }

    private static Command buildEchoCommand(List<Token> arguments) {
        List<String> filenames = arguments.stream()
            .map(Token::getContent)
            .collect(Collectors.toList());
        return new EchoCommand(filenames);
    }

    private static Command buildPwdCommand() {
        return new PwdCommand();
    }

    private static Command buildGrepCommand(List<Token> arguments) {
        boolean ignoreCase = false;
        boolean matchWords = false;
        int countTrailingLines = 0;

        ListIterator<Token> iter = arguments.listIterator();
        Token currArg;
        while (iter.hasNext()) {
            currArg = iter.next();
            if (currArg.getContent().contentEquals("-i")) {
                ignoreCase = true;
                continue;
            }
            if (currArg.getContent().contentEquals("-w")) {
                matchWords = true;
                continue;
            }
            if (currArg.getContent().contentEquals("-A")) {
                if (!iter.hasNext()) {
                    throw new PipelineBuildingException("Grep key -A must has number argument");
                }
                try {
                    countTrailingLines = Integer.parseInt(iter.next().getContent());
                } catch (NumberFormatException e) {
                    throw new PipelineBuildingException("Grep key -A must has number argument", e);
                }
                continue;
            }
            iter.previous();
            break;
        }

        if (!iter.hasNext())
            throw new PipelineBuildingException("Grep must has regex argument");

        Pattern pattern;
        try {
            String regex = iter.next().getContent();
            if (matchWords)
                regex = "\\b" + regex + "\\b";
            pattern = ignoreCase
                ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                : Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new PipelineBuildingException("Syntax error in pattern argument of grep", e);
        }

        List<String> filenames = arguments.subList(iter.nextIndex(), arguments.size())
            .stream()
            .map(Token::getContent)
            .collect(Collectors.toList());

        return new GrepCommand(countTrailingLines, pattern, filenames);
    }

    private static Command buildExternalCommand(List<Token> arguments) {
        List<String> filenames = arguments.stream()
            .map(Token::getContent)
            .collect(Collectors.toList());
        return new ExternalCommand(filenames);
    }

    private static Command buildExitCommand(boolean isSingleCommand) {
        return new ExitCommand(isSingleCommand);
    }

    private static boolean isSequenceOfVariableDeclarations(List<Token> tokens) {
        return tokens.stream().allMatch(token -> token.getType() == Token.TokenType.VarDecl);
    }
}
