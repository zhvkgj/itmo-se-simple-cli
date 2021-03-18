package ru.itmo.se.cli.command;

import ru.itmo.se.cli.command.execution.CommandExecutionException;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс представляющий команду shell-a grep.
 *
 * @author Sergey Sokolvyak on 03.03.2021
 */
public class GrepCommand extends Command {
    private final int printTrailingLines;
    private final Pattern pattern;
    private final StringBuilder content;

    private final List<String> filenames;

    /**
     * Конструктор команды.
     *
     * @param printTrailingLines кол-во строк, которые необходимо напечатать после совпадения
     * @param pattern            регулярное выражение для поиска подходящих строк
     * @param filenames          список имен файлов, в которых необходимо искать совпадения
     */
    public GrepCommand(int printTrailingLines, Pattern pattern,
                       List<String> filenames) {
        if (filenames.isEmpty()) {
            filenames.add("-");
        }
        this.printTrailingLines = printTrailingLines;
        this.filenames = filenames;
        this.pattern = pattern;
        this.content = new StringBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        for (String filename : filenames) {
            try {
                if (filename.contentEquals("-"))
                    process(new StringReader(input.readFromSource()));
                else
                    process(new FileReader(filename));
            } catch (IOException e) {
                throw new CommandExecutionException(
                    String.format("Grep command error: cannot read file %s", filename), e);
            }
        }
        output.writeToDestination(content.toString());
        return 0;
    }

    private void process(Reader source) throws IOException {
        String line;
        Matcher currentMatcher;
        int count = 0;
        try (var bufferedReader = new BufferedReader(source)) {
            while (Objects.nonNull(line = bufferedReader.readLine())) {
                currentMatcher = pattern.matcher(line);
                if (currentMatcher.find()) {
                    if (printTrailingLines > 0)
                        count = printTrailingLines;
                    content.append(line).append("\n");
                    continue;
                }

                if (count > 0) {
                    content.append(line).append("\n");
                    count--;
                }
            }
        }
    }
}
