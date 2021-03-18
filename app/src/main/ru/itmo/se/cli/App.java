package ru.itmo.se.cli;

import ru.itmo.se.cli.command.builder.CommandsPipelineBuilder;
import ru.itmo.se.cli.command.builder.PipelineBuildingException;
import ru.itmo.se.cli.command.execution.CommandExecutionException;
import ru.itmo.se.cli.command.execution.CommandExecutor;
import ru.itmo.se.cli.command.execution.SignalExitException;
import ru.itmo.se.cli.command.execution.SimpleCommandExecutor;
import ru.itmo.se.cli.parser.CommandLineParser;
import ru.itmo.se.cli.parser.ExpansionProvider;
import ru.itmo.se.cli.parser.SimpleCommandLineParser;
import ru.itmo.se.cli.parser.ParsingException;

import java.util.Scanner;

/**
 * Класс, отвечающий за запуск и работу интерпретатора.
 *
 * @author Sergey Sokolvyak
 */
public class App {
    private static final String COMMAND_FORMAT =
        "VARIABLE_DECLARATION... or COMMAND [ARG]... [| VARIABLE_DECLARATION... or COMMAND [ARG]...]...";
    private static final CommandLineParser SIMPLE_COMMAND_LINE_PARSER =
        new SimpleCommandLineParser(new ExpansionProvider());
    private static final CommandExecutor COMMAND_EXECUTOR = new SimpleCommandExecutor();

    /**
     * Запускает интерпретатор.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        System.out.println("Enter the commands in format: " + COMMAND_FORMAT);

        String currentInput;
        var scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            currentInput = scanner.nextLine();
            try {
                COMMAND_EXECUTOR.execute(
                    CommandsPipelineBuilder.buildPipe(SIMPLE_COMMAND_LINE_PARSER.parse(currentInput))
                );
            } catch (ParsingException | PipelineBuildingException | CommandExecutionException e) {
                System.out.println(e.getMessage());
            } catch (SignalExitException e) {
                break;
            }
        }
    }
}
