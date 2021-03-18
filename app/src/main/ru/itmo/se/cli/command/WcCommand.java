package ru.itmo.se.cli.command;

import ru.itmo.se.cli.command.execution.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
                countInCurrentSource = getLinesWordsBytesCountInFile(filename);
            } else {
                if (isInteractiveMode) {
                    countInCurrentSource = getLinesWordsBytesCountInUserInput();
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

    private long[] getLinesWordsBytesCountInFile(String filename) {
        var path = Path.of(filename);
        try (var bufferedReader = Files.newBufferedReader(path)) {
            return getLinesWordsBytesCountFromSource(bufferedReader);
        } catch (IOException e) {
            throw new CommandExecutionException(
                String.format("Wc command error: cannot read file %s", filename), e);
        }
    }

    private long[] getLinesWordsBytesCountInUserInput() {
        try (var bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            return getLinesWordsBytesCountFromSource(bufferedReader);
        } catch (IOException e) {
            throw new CommandExecutionException(
                "Wc command error: cannot read user input", e);
        }
    }

    private long[] getLinesWordsBytesCountFromSource(BufferedReader reader) throws IOException {
        long countOfBytes = 0;
        long countOfLines = 0;
        long countOfWords = 0;
        String currentLine;
        while (Objects.nonNull(currentLine = reader.readLine())) {
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
