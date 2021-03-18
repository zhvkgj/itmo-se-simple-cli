package ru.itmo.se.cli.command.execution;

/**
 * Класс ошибки выполнения команды.
 *
 * @author Sergey Sokolvyak on 27.02.2021
 */
public class CommandExecutionException extends RuntimeException {
    /**
     * Конструктор класса ошибки.
     *
     * @param message описание ошибки
     */
    public CommandExecutionException(String message) {
        super(message);
    }

    /**
     * Конструктор класса ошибки.
     *
     * @param message описание ошибки
     * @param cause   причина возникновения
     */
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
