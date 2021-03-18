package ru.itmo.se.cli.command;

import ru.itmo.se.cli.command.execution.CommandExecutionException;
import ru.itmo.se.cli.environment.Console;
import ru.itmo.se.cli.environment.Descriptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Класс, представляющий команду shell-a cat.
 *
 * @author Sergey Sokolvyak
 */
public final class CatCommand extends Command {
    private final List<String> filenames;
    private final boolean isInteractiveMode;

    /**
     * Конструктор команды.
     *
     * @param filenames         список файлов, из которых команда читает содержимое
     * @param isInteractiveMode флаг запуска команды в интерактивном режиме
     */
    public CatCommand(List<String> filenames, boolean isInteractiveMode) {
        if (filenames.isEmpty()) {
            filenames.add("-");
        }
        this.filenames = filenames;
        this.isInteractiveMode = isInteractiveMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        for (String filename : filenames) {
            if (!filename.contentEquals("-")) {
                output.writeToDestination(readFromFile(filename));
                continue;
            }

            if (isInteractiveMode) {
                var scanner = new Scanner(System.in);
                while (scanner.hasNext()) {
                    output.writeToDestination(scanner.nextLine());
                    if (output.getType() == Descriptor.DescriptorType.Console)
                        Console.getInstance().flush();
                }
            } else {
                output.writeToDestination(input.readFromSource());
            }
        }

        return 0;
    }

    private String readFromFile(String filename) {
        try {
            return Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new CommandExecutionException(
                String.format("Cat command error: cannot read file %s", filename), e);
        }
    }
}
