package ru.itmo.se.cli.command;

import ru.itmo.se.cli.command.execution.CommandExecutionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Класс, представляющий команду shell-a wc.
 *
 * @author Sergey Sokolvyak on 19.02.2021
 */
public final class WcCommand extends Command {
    private final List<String> filenames;
    private final boolean isInteractiveMode;
    private final boolean isTableFormat;

    /**
     * Конструктор команды.
     *
     * @param filenames         список файлов, из которых команда читает содержимое
     * @param isInteractiveMode флаг запуска команды в интерактивном режиме
     */
    public WcCommand(List<String> filenames, boolean isInteractiveMode) {
        if (filenames.isEmpty()) {
            filenames.add("-");
        }
        this.filenames = filenames;
        this.isInteractiveMode = isInteractiveMode;
        this.isTableFormat = filenames.size() > 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() {
        List<String> infoAboutAllSources = new LinkedList<>();
        long[] countInCurrentSource;
        String currentSourceInfo;

        for (String filename : filenames) {
            if (!filename.contentEquals("-")) {
                countInCurrentSource = getLineWordsBytesCountInFile(filename);
            } else {
                if (isInteractiveMode) {
                    countInCurrentSource = getLineWordsBytesCountInUserInput();
                } else {
                    currentSourceInfo = input.readFromSource();
                    countInCurrentSource = new long[]{getCountOfLinesInString(currentSourceInfo),
                        getCountOfWordsInLine(currentSourceInfo), currentSourceInfo.getBytes().length};
                }
            }
            currentSourceInfo = String.format("      %d      %d      %d ",
                countInCurrentSource[0], countInCurrentSource[1], countInCurrentSource[2]
            );
            if (isTableFormat)
                currentSourceInfo += filename;
            infoAboutAllSources.add(currentSourceInfo);
        }

        output.writeToDestination(String.join("\n", infoAboutAllSources));
        return 0;
    }

    private long[] getLineWordsBytesCountInFile(String filename) {
        var path = Path.of(filename);
        try (var bufferedReader = Files.newBufferedReader(path)) {
            long countOfBytes = Files.size(path);
            long countOfLines = 0;
            long countOfWords = 0;

            String currentLine;
            while (Objects.nonNull(currentLine = bufferedReader.readLine())) {
                countOfLines++;
                countOfWords += getCountOfWordsInLine(currentLine);
            }

            return new long[]{countOfLines, countOfWords, countOfBytes};

        } catch (IOException e) {
            throw new CommandExecutionException(
                String.format("Wc command error: cannot read file %s", filename), e);
        }
    }

    private long[] getLineWordsBytesCountInUserInput() {
        long countOfBytes = 0;
        long countOfLines = 0;
        long countOfWords = 0;
        String currentLine;

        var scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            currentLine = scanner.nextLine();
            countOfLines++;
            countOfWords += getCountOfWordsInLine(currentLine);
            countOfBytes += currentLine.getBytes().length;
        }

        return new long[]{countOfLines, countOfWords, countOfBytes};
    }

    private long getCountOfWordsInLine(String line) {
        return line.split("\\s+").length;
    }

    private long getCountOfLinesInString(String str) {
        return str.split("\\n").length;
    }
}
