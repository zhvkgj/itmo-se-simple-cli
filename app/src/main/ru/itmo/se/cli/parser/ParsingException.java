package ru.itmo.se.cli.parser;

/**
 * Класс ошибки синтаксического анализа.
 *
 * @author Sergey Sokolvyak
 */
public class ParsingException extends RuntimeException {
    /**
     * Конструктор класса ошибки.
     *
     * @param message описание ошибки
     */
    public ParsingException(String message) {
        super(message);
    }
}
