package ru.itmo.se.cli.parser;

import java.util.List;

/**
 * Интерфейс парсера командной строки.
 *
 * @author Sergey Sokolvyak on 04.03.2021
 */
public interface CommandLineParser {
    /**
     * Выполняет разбор входной строки на токены.
     *
     * @param input входная строка
     * @return список токенов
     * @see Token
     */
    List<Token> parse(String input);
}
