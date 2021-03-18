package ru.itmo.se.cli.command;

import com.google.common.base.Strings;
import ru.itmo.se.cli.command.execution.CommandExecutionException;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Класс внешней команды.
 *
 * @author Sergey Sokolvyak on 27.02.2021
 */
public class ExternalCommand extends Command {
    private final List<String> arguments;

    /**
     * Конструктор команды.
     * @param arguments список, состоящий из имени команды и аргументов
     */
    public ExternalCommand(List<String> arguments) {
        this.arguments = arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        try {
            var process = new ProcessBuilder(arguments).start();
            OutputStream stdin = process.getOutputStream();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin))) {
                writer.write(input.readFromSource());
                writer.flush();
            }
            String errorMessage = readOutput(process.getErrorStream());
            if (Strings.isNullOrEmpty(errorMessage)) {
                String result = readOutput(process.getInputStream());
                output.writeToDestination(result);
            } else {
                output.writeToDestination(errorMessage);
            }
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new CommandExecutionException(
                String.format("%s command error", arguments.get(0)), e);
        }
    }

    private String readOutput(InputStream inputStream) {
        var scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
